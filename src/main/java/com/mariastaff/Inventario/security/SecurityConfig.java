package com.mariastaff.Inventario.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configurar OAuth2 Login
        http.oauth2Login(oauth2 -> oauth2
            .loginPage("/oauth2/authorization/authentik") // Redirigir al provider configurado
            .defaultSuccessUrl("/ui/", true)
            .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(userAuthoritiesMapper()))
        );
        
        // Configurar Logout para redirigir a Authentik
        http.logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
            .logoutSuccessUrl("http://localhost:9000/application/o/inv/end-session/")
            .permitAll()
        );

        // Permitir acceso a recursos estáticos comunes si es necesario
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/images/**", "/icons/**").permitAll()
        );

        // Aplicar la configuración de seguridad de Vaadin
        // Esto configura CSRF, ignora recursos estáticos de Vaadin, etc.
        super.configure(http);
    }

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private com.mariastaff.Inventario.backend.service.AuthentikService authentikService;

    @org.springframework.context.annotation.Bean
    public org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            java.util.Set<org.springframework.security.core.GrantedAuthority> mappedAuthorities = new java.util.HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority) {
                    org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority oidcAuth = (org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority) authority;
                    
                    // 1. Standard Role Mapping
                    Object groupsObj = oidcAuth.getAttributes().get("groups");
                    if (groupsObj instanceof java.util.List) {
                        java.util.List<?> groups = (java.util.List<?>) groupsObj;
                        for (Object group : groups) {
                            if (group instanceof String) {
                                String groupName = (String) group;
                                String roleName = "ROLE_" + groupName.toUpperCase().replace(" ", "_");
                                mappedAuthorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleName));
                            }
                        }
                    }
                    
                    // 2. Dynamic Permission Mapping
                    try {
                        String sub = (String) oidcAuth.getAttributes().get("sub"); // Authentik PK
                        if (sub != null) {
                            java.util.List<String> perms = authentikService.getUserPermissions(sub);
                            for (String p : perms) {
                                mappedAuthorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(p));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching dynamic permissions: " + e.getMessage());
                    }
                }
                mappedAuthorities.add(authority);
            });

            return mappedAuthorities;
        };
    }
}
