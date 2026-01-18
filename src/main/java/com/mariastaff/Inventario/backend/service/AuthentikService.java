package com.mariastaff.Inventario.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthentikService {

    @Value("${authentik.api.url}")
    private String apiUrl;

    @Value("${authentik.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;

    public AuthentikService() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Crea un usuario en Authentik.
     * @param username Nombre de usuario (único)
     * @param name Nombre completo
     * @param email Correo electrónico
     * @return Map con datos del usuario creado (incluyendo 'pk' y 'uid')
     */
    public Map<String, Object> createUser(String username, String name, String email) {
        String url = apiUrl + "/api/v3/core/users/";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("name", name);
        body.put("email", email);
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik createUser Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            String resp = e.getResponseBodyAsString();
            if (resp.contains("unique")) {
                // User already exists (orphaned from previous failed step?)
                System.out.println("User " + username + " already exists in Authentik. recovering...");
                Map<String, Object> existing = searchUser(username);
                if (existing != null) {
                    return existing;
                }
            }
            throw new RuntimeException("Error creando usuario en Authentik: " + e.getMessage() + " - " + resp);
        } catch (Exception e) {
            throw new RuntimeException("Error interno creando usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza un usuario existente.
     * @param pk ID (Primary Key/UUID) de Authentik
     * @param username nuevo username
     * @param name nuevo nombre
     * @param email nuevo email
     * @param isActive estado activo
     */
    public void updateUser(String pk, String username, String name, String email, boolean isActive) {
        String url = apiUrl + "/api/v3/core/users/" + pk + "/";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("name", name);
        body.put("email", email);
        body.put("is_active", isActive);
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik updateUser Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Authentik Update Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error actualizando usuario Authentik: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno actualizando usuario: " + e.getMessage());
        }
    }

    public void setPassword(String pk, String password) {
        String url = apiUrl + "/api/v3/core/users/" + pk + "/set_password/";
        Map<String, String> body = new HashMap<>();
        body.put("password", password);
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik setPassword Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            restTemplate.postForObject(url, request, Object.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error estableciendo password: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno estableciendo password: " + e.getMessage());
        }
    }

    /**
     * Busca usuario por email o username para recuperar su PK/UID si ya existe.
     */
    public Map<String, Object> searchUser(String query) {
        String url = apiUrl + "/api/v3/core/users/?search=" + query;
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        
        try {
            Map response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    return results.get(0);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getPkByUuid(String uuid) {
        // Since we are likely using UUIDs as PKs, this might just return the UUID if it exists,
        // or fetch the user to confirm existence and return its 'pk' field (which is a string UUID in v3).
        String url = apiUrl + "/api/v3/core/users/?uuid=" + uuid;
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            Map response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    return String.valueOf(results.get(0).get("pk"));
                }
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }
    public Map<String, Object> getGroupByName(String name) {
        String url = apiUrl + "/api/v3/core/groups/?name=" + name;
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            Map response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    return results.get(0);
                }
            }
        } catch (Exception e) {}
        return null;
    }

    public Map<String, Object> createGroup(String name, Map<String, Object> attributes) {
        String url = apiUrl + "/api/v3/core/groups/";
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        if (attributes != null) {
            body.put("attributes", attributes);
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik createGroup Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            // If group already exists, try to return it
             Map<String, Object> existing = getGroupByName(name);
             if(existing != null) return existing;
             throw new RuntimeException("Error creando grupo: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno creando grupo: " + e.getMessage());
        }
    }

    public void updateGroup(String pk, String name, Map<String, Object> attributes) {
        String url = apiUrl + "/api/v3/core/groups/" + pk + "/";
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        if (attributes != null) {
            body.put("attributes", attributes);
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik updateGroup Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error actualizando grupo: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno actualizando grupo: " + e.getMessage());
        }
    }

    public void deleteGroup(String pk) {
        String url = apiUrl + "/api/v3/core/groups/" + pk + "/";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error eliminando grupo: " + e.getResponseBodyAsString());
        }
    }

    public List<Map<String, Object>> listUsers() {
        String url = apiUrl + "/api/v3/core/users/?ordering=username&page_size=100";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            System.out.println("Authentik: Fetching users from " + url);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map response = responseEntity.getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                System.out.println("Authentik: Found " + results.size() + " users.");
                return results;
            }
        } catch (Exception e) {
            System.err.println("Authentik Error listUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }

    public List<Map<String, Object>> listGroups() {
        String url = apiUrl + "/api/v3/core/groups/?ordering=name&page_size=100";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
             System.out.println("Authentik: Fetching groups from " + url);
             ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
             Map response = responseEntity.getBody();
             if (response != null && response.containsKey("results")) {
                 List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                 System.out.println("Authentik: Found " + results.size() + " groups.");
                 return results;
             }
        } catch (Exception e) {
            System.err.println("Authentik Error listGroups: " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }

    public List<String> getUserGroupNames(String userPk) {
        // 1. Get User details to get group UUIDs
        String userUrl = apiUrl + "/api/v3/core/users/" + userPk + "/";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        List<String> groupIds = new java.util.ArrayList<>();
        try {
             Map user = restTemplate.exchange(userUrl, HttpMethod.GET, request, Map.class).getBody();
             if (user != null && user.containsKey("groups")) {
                 // Authentik v3 'groups' is often list of UUID strings
                 groupIds = (List<String>) user.get("groups"); 
             }
        } catch (Exception e) { return List.of(); }

        if (groupIds.isEmpty()) return List.of();

        // 2. Get All Groups to map to names (Caching this would be good in real app)
        List<Map<String, Object>> allGroups = listGroups();
        List<String> names = new java.util.ArrayList<>();
        for (Map<String, Object> g : allGroups) {
            String gUuid = String.valueOf(g.get("pk")); // Safely convert to String
            if (groupIds.contains(gUuid)) {
                names.add((String) g.get("name"));
            }
        }
        return names;
    }
    
    public void addUserToGroup(String userPk, String groupPk) {
         String url = apiUrl + "/api/v3/core/groups/" + groupPk + "/add_user/";
         Map<String, String> body = new HashMap<>(); // Usually expects 'pk'
         body.put("pk", userPk);
         
         try {
             com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
             String jsonBody = mapper.writeValueAsString(body);
             System.out.println("Authentik addUserToGroup Payload: " + jsonBody);
             
             HttpHeaders headers = getHeaders();
             HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
             
             restTemplate.postForObject(url, request, Object.class);
         } catch (HttpClientErrorException e) {
             // 204 or just success
             System.err.println("Error adding user to group: " + e.getResponseBodyAsString());
         } catch (Exception e) {
             System.err.println("Error interno adding user to group: " + e.getMessage());
         }
    }

    public List<String> getUserPermissions(String userPk) {
        // 1. Get User's Group IDs
        String userUrl = apiUrl + "/api/v3/core/users/" + userPk + "/";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        List<String> groupIds = new java.util.ArrayList<>();
        try {
             Map user = restTemplate.exchange(userUrl, HttpMethod.GET, request, Map.class).getBody();
             if (user != null && user.containsKey("groups")) {
                 groupIds = (List<String>) user.get("groups"); 
             }
        } catch (Exception e) { return List.of(); }

        if (groupIds.isEmpty()) return List.of();

        // 2. Scan Groups for Permissions
        List<Map<String, Object>> allGroups = listGroups();
        List<String> permissions = new java.util.ArrayList<>();
        
        for (Map<String, Object> g : allGroups) {
            String gUuid = String.valueOf(g.get("pk")); // Safely convert to String
            if (groupIds.contains(gUuid)) {
                // Check attributes for app_permissions
                Map<String, Object> attrs = (Map<String, Object>) g.get("attributes");
                if (attrs != null && attrs.containsKey("app_permissions")) {
                    List<String> perms = (List<String>) attrs.get("app_permissions");
                    permissions.addAll(perms);
                } else {
                     // Fallback for system roles if not yet migrated
                     String name = (String) g.get("name");
                     if ("ADMIN".equalsIgnoreCase(name) || "ADMINS".equalsIgnoreCase(name)) {
                         permissions.add("MODULE_INVENTORY"); permissions.add("MODULE_SALES");
                         permissions.add("MODULE_ACCOUNTING"); permissions.add("MODULE_REPORTS");
                         permissions.add("MODULE_SETTINGS");
                     } else if ("INVENTARIO".equalsIgnoreCase(name)) permissions.add("MODULE_INVENTORY");
                     else if ("CAJERO".equalsIgnoreCase(name)) permissions.add("MODULE_SALES");
                     else if ("CONTADOR".equalsIgnoreCase(name)) {
                         permissions.add("MODULE_ACCOUNTING"); permissions.add("MODULE_REPORTS");
                     }
                }
            }
        }
        return permissions;
    }

    public void removeUserFromGroup(String userPk, String groupPk) {
         String url = apiUrl + "/api/v3/core/groups/" + groupPk + "/remove_user/";
         Map<String, String> body = new HashMap<>();
         body.put("pk", userPk);
         
         try {
             com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
             String jsonBody = mapper.writeValueAsString(body);
             System.out.println("Authentik removeUserFromGroup Payload: " + jsonBody);
             
             HttpHeaders headers = getHeaders();
             HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
             
             restTemplate.postForObject(url, request, Object.class);
         } catch (HttpClientErrorException e) {
             // 204 or succeed
             System.err.println("Error removing user from group: " + e.getResponseBodyAsString());
         } catch (Exception e) {
             System.err.println("Error interno removing user from group: " + e.getMessage());
         }
    }
}
