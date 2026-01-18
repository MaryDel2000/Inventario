package com.mariastaff.Inventario.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AuthentikService {

    @Value("${authentik.api.url}")
    private String apiUrl;

    @Value("${authentik.app.slug}")
    private String appSlug;

    // Token de sistema para tareas de bootstrap (inicialización) donde no hay usuario logueado.
    @Value("${authentik.api.token}")
    private String systemToken;

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public AuthentikService(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.restTemplate = new RestTemplate();
        this.authorizedClientManager = authorizedClientManager;
    }
    
    /**
     * Asegura que los entitlements requeridos existan en Authentik.
     * Utiliza el token de sistema, ya que se ejecuta al inicio de la aplicación.
     */
    public void ensureEntitlementsExist(Map<String, String> requiredEntitlements) {
        try {
            System.out.println("Bootstrap: Verificando Entitlements en Authentik...");
            // Usar token de sistema para listar
            List<Map<String, Object>> existing = listEntitlementsSystem();
            Set<String> existingNames = new java.util.HashSet<>();
            for(Map<String, Object> ent : existing) {
                existingNames.add((String) ent.get("name"));
            }
            
            for (Map.Entry<String, String> entry : requiredEntitlements.entrySet()) {
                if (!existingNames.contains(entry.getKey())) {
                    System.out.println("Bootstrap: Creando entitlement faltante: " + entry.getKey());
                    createEntitlementSystem(entry.getKey(), Map.of("description", entry.getValue()));
                }
            }
            System.out.println("Bootstrap: Verificación de entitlements completada.");
        } catch (Exception e) {
            System.err.println("Bootstrap Error: No se pudieron sincronizar los entitlements: " + e.getMessage());
        }
    }

    // Métodos privados usando System Token

    private List<Map<String, Object>> listEntitlementsSystem() {
        String url = apiUrl + "/api/v3/core/application_entitlements/?app__slug=" + appSlug + "&ordering=name&page_size=100";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + systemToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        
        if (response.getBody() != null && response.getBody().containsKey("results")) {
            return (List<Map<String, Object>>) response.getBody().get("results");
        }
        return java.util.Collections.emptyList();
    }

    private void createEntitlementSystem(String name, Map<String, Object> attributes) {
        String url = apiUrl + "/api/v3/core/application_entitlements/";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + systemToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("name", name);
        body.put("app", appSlug);
        body.put("attributes", attributes);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, Map.class);
    }

    /**
     * Obtiene los headers con el token de autenticación del usuario actual.
     * Solo usa el token OAuth2 del usuario autenticado para operaciones iniciadas por usuarios.
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        
        // Intentar obtener el token del usuario autenticado (con refresh automático)
        String token = getUserAccessToken();
        
        if (token != null) {
            System.out.println("DEBUG: Using OAuth2 user access token for Authentik API");
            headers.set("Authorization", "Bearer " + token);
        } else {
            throw new RuntimeException("No se pudo obtener el token de acceso del usuario. Asegúrese de haber iniciado sesión.");
        }
        
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Obtiene el token de acceso del usuario autenticado desde el contexto de seguridad.
     * Usa OAuth2AuthorizedClientManager que automáticamente refresca el token si ha expirado.
     * @return El access token o null si no hay usuario autenticado
     */
    private String getUserAccessToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null) {
                System.out.println("DEBUG: SecurityContext Authentication is NULL. Current Thread: " + Thread.currentThread().getName());
                return null;
            }
            
            System.out.println("DEBUG: Authentication Type: " + authentication.getClass().getName() + ", Name: " + authentication.getName() + ", Authorities: " + authentication.getAuthorities());

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
                String principalName = oauthToken.getName();
                
                // Crear request para el OAuth2AuthorizedClientManager
                // Nota: HttpServletRequest/Response pueden ser nulos en hilos background
                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(clientRegistrationId)
                    .principal(authentication)
                    .build();
                
                // El manager automáticamente refresca el token si es necesario
                try {
                    OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager.authorize(authorizeRequest);
                    if (authorizedClient != null) {
                        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                        if (accessToken != null) {
                            return accessToken.getTokenValue();
                        } else {
                            System.out.println("DEBUG: AccessToken is NULL in authorizedClient");
                        }
                    } else {
                        System.out.println("DEBUG: authorizedClient is NULL for " + clientRegistrationId);
                    }
                } catch (Exception clientError) {
                    System.err.println("DEBUG: Error authorizing client: " + clientError.getMessage());
                    clientError.printStackTrace();
                }
            } else {
                System.out.println("DEBUG: Authentication is NOT OAuth2AuthenticationToken");
            }
        } catch (Exception e) {
            System.err.println("Error obtaining user access token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
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
                // User already exists
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
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error listando usuarios (Authentik " + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error listando usuarios: " + e.getMessage());
        }
        return List.of();
    }

    // ===== ENTITLEMENTS MANAGEMENT =====

    /**
     * Lista todos los entitlements de la aplicación
     */
    public List<Map<String, Object>> listEntitlements() {
        String url = apiUrl + "/api/v3/core/application_entitlements/?app__slug=" + appSlug + "&ordering=name&page_size=100";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            System.out.println("Authentik: Fetching entitlements from " + url);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map response = responseEntity.getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                System.out.println("Authentik: Found " + results.size() + " entitlements.");
                return results;
            }
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error listando entitlements (Authentik " + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error listando entitlements: " + e.getMessage());
        }
        return List.of();
    }

    /**
     * Crea un nuevo entitlement para la aplicación
     */
    public Map<String, Object> createEntitlement(String name, Map<String, Object> attributes) {
        // Primero necesito obtener el UUID de la aplicación
        String appPk = getApplicationPk();
        if (appPk == null) {
            throw new RuntimeException("No se pudo obtener el UUID de la aplicación " + appSlug);
        }

        String url = apiUrl + "/api/v3/core/application_entitlements/";
        Map<String, Object> body = new HashMap<>();
        body.put("app", appPk);
        body.put("name", name);
        if (attributes != null) {
            body.put("attributes", attributes);
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik createEntitlement Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            return restTemplate.postForObject(url, request, Map.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error creando entitlement: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno creando entitlement: " + e.getMessage());
        }
    }

    /**
     * Actualiza un entitlement existente
     */
    public void updateEntitlement(String pk, String name, Map<String, Object> attributes) {
        String url = apiUrl + "/api/v3/core/application_entitlements/" + pk + "/";
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        if (attributes != null) {
            body.put("attributes", attributes);
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik updateEntitlement Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            restTemplate.exchange(url, HttpMethod.PATCH, request, Map.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error actualizando entitlement: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error interno actualizando entitlement: " + e.getMessage());
        }
    }

    /**
     * Elimina un entitlement
     */
    public void deleteEntitlement(String pk) {
        String url = apiUrl + "/api/v3/core/application_entitlements/" + pk + "/";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException e) {
             throw new RuntimeException("Error eliminando entitlement: " + e.getResponseBodyAsString());
        }
    }

    /**
     * Obtiene los entitlements de un usuario
     */
    public List<String> getUserEntitlements(String userPk) {
        // Necesitamos obtener el PK de la aplicación primero
        String appPk = getApplicationPk();
        if (appPk == null) {
            System.err.println("No se pudo obtener el PK de la aplicación");
            return List.of();
        }

        String url = apiUrl + "/api/v3/core/application_entitlements/?user=" + userPk + "&app=" + appPk + "&page_size=100";
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map response = responseEntity.getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                List<String> entitlementNames = new java.util.ArrayList<>();
                for (Map<String, Object> ent : results) {
                    entitlementNames.add((String) ent.get("name"));
                }
                return entitlementNames;
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo entitlements del usuario: " + e.getMessage());
        }
        return List.of();
    }

    /**
     * Asigna un entitlement a un usuario
     */
    public void assignEntitlementToUser(String entitlementPk, String userPk) {
        String url = apiUrl + "/api/v3/core/application_entitlements/" + entitlementPk + "/set_users/";
        Map<String, Object> body = new HashMap<>();
        body.put("users", List.of(userPk));
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            System.out.println("Authentik assignEntitlement Payload: " + jsonBody);
            
            HttpHeaders headers = getHeaders();
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            
            restTemplate.postForObject(url, request, Object.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Error asignando entitlement: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error interno asignando entitlement: " + e.getMessage());
        }
    }

    /**
     * Remueve un entitlement de un usuario (si el endpoint existe)
     */
    public void removeEntitlementFromUser(String entitlementPk, String userPk) {
        // TODO: Verificar el endpoint correcto en la API de Authentik
        // Por ahora, podríamos necesitar usar el endpoint de actualización parcial
        System.err.println("removeEntitlementFromUser no implementado - verificar endpoint de Authentik");
    }

    /**
     * Obtiene el UUID/PK de la aplicación por su slug
     */
    private String getApplicationPk() {
        String url = apiUrl + "/api/v3/core/applications/?slug=" + appSlug;
        HttpEntity<?> request = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map response = responseEntity.getBody();
            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    return String.valueOf(results.get(0).get("pk"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo PK de aplicación: " + e.getMessage());
        }
        return null;
    }
}
