package com.mariastaff.Inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.NoTheme;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@NoTheme
public class InventarioApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Managua"));
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(InventarioApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(InventarioApplication.class, args);
	}

}
