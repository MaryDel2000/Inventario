package com.mariastaff.Inventario.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BackupService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static final String BACKUP_DIR = "backups";

    public BackupService() {
        // Ensure backup directory exists
        new File(BACKUP_DIR).mkdirs();
    }

    public void backupDatabase() throws IOException, InterruptedException {
        String dbName = extractDbName(dbUrl);
        String host = "localhost"; // Assuming localhost from simpler JDBC url parsing or config
        String port = "5432";
        
        // Simple parsing for host/port if needed, but for now assuming defaults or extracted
        if (dbUrl.contains("://")) {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            if (cleanUrl.contains("/")) {
                String hostPort = cleanUrl.substring(0, cleanUrl.indexOf("/"));
                if (hostPort.contains(":")) {
                    String[] parts = hostPort.split(":");
                    host = parts[0];
                    port = parts[1];
                } else {
                    host = hostPort;
                }
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + dbName + "_" + timestamp + ".sql";
        File backupFile = new File(BACKUP_DIR, filename);

        List<String> command = new ArrayList<>();
        command.add("pg_dump");
        command.add("-h");
        command.add(host);
        command.add("-p");
        command.add(port);
        command.add("-U");
        command.add(dbUser);
        command.add("-F");
        command.add("p"); // Plain text SQL
        command.add("-f");
        command.add(backupFile.getAbsolutePath()); // Fix: Add file path correctly
        command.add("-d"); // Explicit database flag
        command.add(dbName);
        
        System.out.println("BackupService: Executing backup command: " + command);

        runProcess(command);
    }

    public void restoreDatabase(File backupFile) throws IOException, InterruptedException {
        String dbName = extractDbName(dbUrl);
        String host = "localhost";
        String port = "5432";
        
         if (dbUrl.contains("://")) {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            if (cleanUrl.contains("/")) {
                String hostPort = cleanUrl.substring(0, cleanUrl.indexOf("/"));
                if (hostPort.contains(":")) {
                    String[] parts = hostPort.split(":");
                    host = parts[0];
                    port = parts[1];
                } else {
                    host = hostPort;
                }
            }
        }

        // NOTE: pg_restore is for binary formats (-F c). For plain SQL, use psql.
        // Since we dump as plain SQL (-F p), we use psql to restore.
        
        List<String> command = new ArrayList<>();
        command.add("psql");
        command.add("-h");
        command.add(host);
        command.add("-p");
        command.add(port);
        command.add("-U");
        command.add(dbUser);
        command.add("-d");
        command.add(dbName);
        command.add("-f");
        command.add(backupFile.getAbsolutePath());

        runProcess(command);
    }

    public List<File> listBackups() {
        try {
            return Files.list(Paths.get(BACKUP_DIR))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().endsWith(".sql"))
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Helper to extract DB name from JDBC URL (jdbc:postgresql://localhost:5432/dev_app_db)
    private String extractDbName(String url) {
        if (url == null) {
            System.err.println("BackupService: DB URL is null");
            return "dev_app_db"; // Fallback default
        }
        
        try {
            // Remove 'jdbc:' prefix
            String cleanUrl = url.replace("jdbc:", "");
            // Handle postgresql://host:port/dbname format
            if (cleanUrl.startsWith("postgresql://")) {
                cleanUrl = cleanUrl.substring("postgresql://".length());
            }
            
            // Should now accept host:port/dbname
            int slashIndex = cleanUrl.lastIndexOf("/");
            if (slashIndex != -1) {
                String dbName = cleanUrl.substring(slashIndex + 1);
                // Remove parameters starting with ?
                if (dbName.contains("?")) {
                    dbName = dbName.substring(0, dbName.indexOf("?"));
                }
                System.out.println("BackupService: Extracted DB Name '" + dbName + "' from URL '" + url + "'");
                return dbName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.err.println("BackupService: Failed to extract DB name from '" + url + "', using fallback");
        return "dev_app_db"; // Fallback
    }

    public void clearDatabase() throws IOException, InterruptedException {
        String dbName = extractDbName(dbUrl);
        String host = "localhost";
        String port = "5432";
        
        if (dbUrl.contains("://")) {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            if (cleanUrl.contains("/")) {
                String hostPort = cleanUrl.substring(0, cleanUrl.indexOf("/"));
                if (hostPort.contains(":")) {
                    String[] parts = hostPort.split(":");
                    host = parts[0];
                    port = parts[1];
                } else {
                    host = hostPort;
                }
            }
        }

        // Use a temporary file for the cleanup script to avoid escaping issues
        // We use TRUNCATE CASCADE to clean all data but keep table structures.
        // We EXCLUDE flyway_schema_history to preserve migration state.
        File scriptFile = File.createTempFile("clean_db_" + System.currentTimeMillis(), ".sql");
        String sqlScript = "DO $$ DECLARE r RECORD; BEGIN " +
                           "  FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename != 'flyway_schema_history') LOOP " +
                           "    EXECUTE 'TRUNCATE TABLE public.' || quote_ident(r.tablename) || ' CASCADE'; " +
                           "  END LOOP; " +
                           "END $$;";
        
        Files.writeString(scriptFile.toPath(), sqlScript);
        
        List<String> command = new ArrayList<>();
        command.add("psql");
        command.add("-h");
        command.add(host);
        command.add("-p");
        command.add(port);
        command.add("-U");
        command.add(dbUser);
        command.add("-d");
        command.add(dbName);
        command.add("-f");
        command.add(scriptFile.getAbsolutePath());

        System.out.println("BackupService: Executing clear database script (TRUNCATE): " + scriptFile.getAbsolutePath());
        
        try {
            runProcess(command);
        } finally {
            // Cleanup temp file
            scriptFile.delete();
        }
    }

    private void runProcess(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", dbPassword);
        
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Capture output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Process failed with exit code " + exitCode + ":\n" + output);
        }
    }
}
