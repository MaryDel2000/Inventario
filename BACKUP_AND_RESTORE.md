# Backup and Restore Workflow

This workflow outlines how to use the Backup View in the application to manage database backups, restore data, and clear the database.

## Prerequisites
- The user must have the `ROLE_ADMIN` role.
- The PostgreSQL client tools (`pg_dump`, `psql`) must be installed and accessible in the application's runtime environment (or inside the Docker container).

## Workflow Steps

1. **Accessing the View**
   - Navigate to the application sidebar.
   - Click on "Configuración" (Settings).
   - Select "Respaldos" (Backups).

2. **Creating a Backup**
   - Click the "Crear Respaldo" button.
   - A success notification will appear, and the new backup file will be listed in the grid.
   - The backup file is named with the format `backup_<dbname>_<timestamp>.sql`.

3. **Downloading a Backup**
   - Locate the desired backup in the grid.
   - Click the download icon (arrow pointing down) in the "Acciones" column.
   - The SQL file will be downloaded to your local machine.

4. **Restoring a Database**
   - **WARNING**: Restoring overwrites existing data with the data from the backup.
   - Locate the backup file you want to restore.
   - Click the restore icon (rotate right) in the "Acciones" column.
   - Confirm the action in the popup dialog.

5. **Clearing the Database**
   - **WARNING**: This action deletes ALL data (tables) from the application but keeps the database structure intact for a fresh start. Backups are NOT deleted.
   - Click the red "Limpiar Base de Datos" button below the grid.
   - Read the warning carefully.
   - Click "Sí, BORRAR TODO" to proceed.

## Troubleshooting
- If "Error creating backup" occurs, check the application logs for connection errors or version mismatches between the server and the `pg_dump` client.
- If "Permission denied" occurs during restore or clear, ensure the database user has ownership of the tables in the `public` schema.
