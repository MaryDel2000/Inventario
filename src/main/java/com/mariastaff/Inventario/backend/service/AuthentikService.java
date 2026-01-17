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
        // Default values for mandatory fields if any
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());
        
        try {
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error creando usuario en Authentik: " + e.getMessage() + " - " + e.getResponseBodyAsString());
        }
    }

    /**
     * Actualiza un usuario existente.
     * @param pk ID numérico (Primary Key) de Authentik
     * @param username nuevo username
     * @param name nuevo nombre
     * @param email nuevo email
     * @param isActive estado activo
     */
    public void updateUser(Integer pk, String username, String name, String email, boolean isActive) {
        String url = apiUrl + "/api/v3/core/users/" + pk + "/";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("name", name);
        body.put("email", email);
        body.put("is_active", isActive);
        
        System.out.println("Authentik UpdateUser Payload: " + body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());
        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Authentik Update Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error actualizando usuario Authentik: " + e.getResponseBodyAsString());
        }
    }

    public void setPassword(Integer pk, String password) {
        String url = apiUrl + "/api/v3/core/users/" + pk + "/set_password/";
        Map<String, String> body = new HashMap<>();
        body.put("password", password);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, getHeaders());
        try {
            restTemplate.postForObject(url, request, Object.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error estableciendo password: " + e.getResponseBodyAsString());
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

    public Integer getPkByUuid(String uuid) {
        String url = apiUrl + "/api/v3/core/users/?uuid=" + uuid;
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            Map response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class).getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    return (Integer) results.get(0).get("pk");
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

    public Map<String, Object> createGroup(String name) {
        String url = apiUrl + "/api/v3/core/groups/";
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        // body.put("is_superuser", false); // Removed to avoid potential API conflicts
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, getHeaders());
        try {
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
            // If group already exists, try to return it
             Map<String, Object> existing = getGroupByName(name);
             if(existing != null) return existing;
             throw new RuntimeException("Error creando grupo: " + e.getResponseBodyAsString());
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

    public List<String> getUserGroupNames(Integer userPk) {
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
            String gUuid = (String) g.get("pk"); // In groups list, 'pk' field is usually the UUID string
            if (groupIds.contains(gUuid)) {
                names.add((String) g.get("name"));
            }
        }
        return names;
    }
    
    public void addUserToGroup(Integer userPk, Integer groupPk) {
         String url = apiUrl + "/api/v3/core/groups/" + groupPk + "/add_user/";
         Map<String, Integer> body = new HashMap<>();
         body.put("pk", userPk);
         HttpEntity<Map<String, Integer>> request = new HttpEntity<>(body, getHeaders());
         try {
             restTemplate.postForObject(url, request, Object.class);
         } catch (HttpClientErrorException e) {
             // 204 or just success
         } 
    }

    public void removeUserFromGroup(Integer userPk, Integer groupPk) {
         String url = apiUrl + "/api/v3/core/groups/" + groupPk + "/remove_user/";
         Map<String, Integer> body = new HashMap<>();
         body.put("pk", userPk);
         HttpEntity<Map<String, Integer>> request = new HttpEntity<>(body, getHeaders());
         try {
             restTemplate.postForObject(url, request, Object.class);
         } catch (HttpClientErrorException e) {
             // 204 or succeed
         } 
    }
}
