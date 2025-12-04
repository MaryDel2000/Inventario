package com.mariastaff.Inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator; // Assuming this import is needed for AppShellConfigurator

@SpringBootApplication
public class InventarioApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(InventarioApplication.class, args);
	}

}
