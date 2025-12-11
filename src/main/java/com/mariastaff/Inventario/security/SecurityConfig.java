package com.mariastaff.Inventario.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configurar OAuth2 Login
        http.oauth2Login(oauth2 -> oauth2
            .loginPage("/oauth2/authorization/authentik") // Redirigir al provider configurado
            .defaultSuccessUrl("/ui/", true)
        );

        // Permitir acceso a recursos estáticos comunes si es necesario
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/images/**", "/icons/**").permitAll()
        );

        // Aplicar la configuración de seguridad de Vaadin
        // Esto configura CSRF, ignora recursos estáticos de Vaadin, etc.
        super.configure(http);
        
        // No configuramos setLoginView porque usamos OAuth2 externo.
        // Spring Security redirigirá automáticamente a /oauth2/authorization/authentik
        // cuando se intente acceder a una ruta protegida.
    }
}
