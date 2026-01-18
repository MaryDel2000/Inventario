package com.mariastaff.Inventario.config;

import java.util.Map;

/**
 * Definición centralizada de los roles y permisos (Entitlements) de la aplicación.
 * Sigue el principio DRY (Single Source of Truth).
 */
public class AppRoles {

    public static final String MODULE_INVENTORY = "MODULE_INVENTORY";
    public static final String MODULE_SALES = "MODULE_SALES";
    public static final String MODULE_ACCOUNTING = "MODULE_ACCOUNTING";
    public static final String MODULE_REPORTS = "MODULE_REPORTS";
    public static final String MODULE_SETTINGS = "MODULE_SETTINGS";

    /**
     * Mapa de Código -> Descripción humana.
     */
    public static final Map<String, String> DEFINITIONS = Map.of(
        MODULE_INVENTORY, "Inventario y Compras",
        MODULE_SALES, "Punto de Venta y Ventas",
        MODULE_ACCOUNTING, "Contabilidad",
        MODULE_REPORTS, "Reportes",
        MODULE_SETTINGS, "Configuración del Sistema"
    );

    private AppRoles() {
        // Prevent instantiation
    }
}
