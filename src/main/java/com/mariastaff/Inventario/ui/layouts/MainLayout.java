package com.mariastaff.Inventario.ui.layouts;

import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.mariastaff.Inventario.ui.components.composite.AppHeader;
import com.mariastaff.Inventario.ui.components.composite.AppSidebar;
import com.mariastaff.Inventario.ui.views.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MainLayout extends AppLayout {

    private AppHeader header;
    private AppSidebar sidebar;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        getElement().getThemeList().clear();

        header = new AppHeader();
        addToNavbar(header);

        sidebar = new AppSidebar(new VaadinAppIcon(VaadinIcon.PACKAGE), "app.title");
        addToDrawer(sidebar);

        setupSidebar();
    }

    private void setupSidebar() {
        // Main Modules - Linking to the default view of each module
        sidebar.addNavItem(new AppNavItem("Inventario", new VaadinAppIcon(VaadinIcon.STORAGE), InventoryDashboardView.class));
        sidebar.addNavItem(new AppNavItem("Compras", new VaadinAppIcon(VaadinIcon.CART), NewPurchaseView.class));
        sidebar.addNavItem(new AppNavItem("Ventas (POS)", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        sidebar.addNavItem(new AppNavItem("Contabilidad", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        sidebar.addNavItem(new AppNavItem("Reportes", new VaadinAppIcon(VaadinIcon.FILE_TEXT), ReportSalesUserView.class));
        sidebar.addNavItem(new AppNavItem("Configuración", new VaadinAppIcon(VaadinIcon.COG), BranchesView.class));
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        updateHeader();
    }

    private void updateHeader() {
        header.clearNavigationItems();
        Class<?> currentView = getContent().getClass();

        if (isInventoryView(currentView)) {
            addInventoryHeaderItems();
        } else if (isPurchaseView(currentView)) {
            addPurchaseHeaderItems();
        } else if (isSalesView(currentView)) {
            addSalesHeaderItems();
        } else if (isAccountingView(currentView)) {
            addAccountingHeaderItems();
        } else if (isReportsView(currentView)) {
            addReportsHeaderItems();
        } else if (isSettingsView(currentView)) {
            addSettingsHeaderItems();
        }
    }

    private boolean isInventoryView(Class<?> view) {
        return view.equals(InventoryDashboardView.class) || view.equals(ProductsView.class) || 
               view.equals(CategoriesView.class) || view.equals(UOMView.class) || 
               view.equals(MovementsView.class) || view.equals(WarehousesView.class) || 
               view.equals(LocationsView.class) || view.equals(BatchesView.class);
    }

    private void addInventoryHeaderItems() {
        header.addNavigationItem(new AppNavItem("Dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), InventoryDashboardView.class));
        header.addNavigationItem(new AppNavItem("Productos", new VaadinAppIcon(VaadinIcon.PACKAGE), ProductsView.class));
        header.addNavigationItem(new AppNavItem("Categorías", new VaadinAppIcon(VaadinIcon.TAGS), CategoriesView.class));
        header.addNavigationItem(new AppNavItem("Unidades", new VaadinAppIcon(VaadinIcon.SLIDERS), UOMView.class));
        header.addNavigationItem(new AppNavItem("Movimientos", new VaadinAppIcon(VaadinIcon.EXCHANGE), MovementsView.class));
        header.addNavigationItem(new AppNavItem("Almacenes", new VaadinAppIcon(VaadinIcon.BUILDING), WarehousesView.class));
        header.addNavigationItem(new AppNavItem("Ubicaciones", new VaadinAppIcon(VaadinIcon.MAP_MARKER), LocationsView.class));
        header.addNavigationItem(new AppNavItem("Lotes", new VaadinAppIcon(VaadinIcon.BARCODE), BatchesView.class));
    }

    private boolean isPurchaseView(Class<?> view) {
        return view.equals(NewPurchaseView.class) || view.equals(PurchasesHistoryView.class) || 
               view.equals(ProvidersView.class);
    }

    private void addPurchaseHeaderItems() {
        header.addNavigationItem(new AppNavItem("Nueva Compra", new VaadinAppIcon(VaadinIcon.PLUS_CIRCLE), NewPurchaseView.class));
        header.addNavigationItem(new AppNavItem("Historial", new VaadinAppIcon(VaadinIcon.CLOCK), PurchasesHistoryView.class));
        header.addNavigationItem(new AppNavItem("Proveedores", new VaadinAppIcon(VaadinIcon.TRUCK), ProvidersView.class));
    }

    private boolean isSalesView(Class<?> view) {
        return view.equals(POSView.class) || view.equals(ShiftView.class) || 
               view.equals(ClosuresView.class) || view.equals(CustomersView.class) || 
               view.equals(ReceivablesView.class) || view.equals(SalesHistoryView.class);
    }

    private void addSalesHeaderItems() {
        header.addNavigationItem(new AppNavItem("POS", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        header.addNavigationItem(new AppNavItem("Turno", new VaadinAppIcon(VaadinIcon.CLOCK), ShiftView.class));
        header.addNavigationItem(new AppNavItem("Cierres", new VaadinAppIcon(VaadinIcon.LOCK), ClosuresView.class));
        header.addNavigationItem(new AppNavItem("Clientes", new VaadinAppIcon(VaadinIcon.USER), CustomersView.class));
        header.addNavigationItem(new AppNavItem("Cobros", new VaadinAppIcon(VaadinIcon.MONEY), ReceivablesView.class));
        header.addNavigationItem(new AppNavItem("Historial", new VaadinAppIcon(VaadinIcon.FILE_TEXT), SalesHistoryView.class));
    }

    private boolean isAccountingView(Class<?> view) {
        return view.equals(FinancialDashboardView.class) || view.equals(JournalView.class) || 
               view.equals(ChartOfAccountsView.class) || view.equals(FiscalPeriodsView.class) || 
               view.equals(ManualEntriesView.class);
    }

    private void addAccountingHeaderItems() {
        header.addNavigationItem(new AppNavItem("Dashboard", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        header.addNavigationItem(new AppNavItem("Libro Diario", new VaadinAppIcon(VaadinIcon.BOOK), JournalView.class));
        header.addNavigationItem(new AppNavItem("Cuentas", new VaadinAppIcon(VaadinIcon.LIST), ChartOfAccountsView.class));
        header.addNavigationItem(new AppNavItem("Periodos", new VaadinAppIcon(VaadinIcon.CALENDAR), FiscalPeriodsView.class));
        header.addNavigationItem(new AppNavItem("Asientos", new VaadinAppIcon(VaadinIcon.EDIT), ManualEntriesView.class));
    }

    private boolean isReportsView(Class<?> view) {
        return view.equals(ReportSalesUserView.class) || view.equals(ReportTopProductsView.class) || 
               view.equals(ReportMarginsView.class) || view.equals(ReportKardexView.class) || 
               view.equals(ReportInventoryValueView.class) || view.equals(ReportIncomeStatementView.class) || 
               view.equals(ReportTrialBalanceView.class);
    }

    private void addReportsHeaderItems() {
        header.addNavigationItem(new AppNavItem("Ventas/Usuario", new VaadinAppIcon(VaadinIcon.USER), ReportSalesUserView.class));
        header.addNavigationItem(new AppNavItem("Top Productos", new VaadinAppIcon(VaadinIcon.STAR), ReportTopProductsView.class));
        header.addNavigationItem(new AppNavItem("Márgenes", new VaadinAppIcon(VaadinIcon.TRENDING_UP), ReportMarginsView.class));
        header.addNavigationItem(new AppNavItem("Kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O), ReportKardexView.class));
        header.addNavigationItem(new AppNavItem("Valor Inv.", new VaadinAppIcon(VaadinIcon.MONEY), ReportInventoryValueView.class));
        header.addNavigationItem(new AppNavItem("Resultados", new VaadinAppIcon(VaadinIcon.PIE_CHART), ReportIncomeStatementView.class));
        header.addNavigationItem(new AppNavItem("Balance", new VaadinAppIcon(VaadinIcon.SCALE), ReportTrialBalanceView.class));
    }

    private boolean isSettingsView(Class<?> view) {
        return view.equals(BranchesView.class) || view.equals(UsersView.class) || 
               view.equals(CurrenciesView.class) || view.equals(TaxesView.class) || 
               view.equals(GeneralSettingsView.class);
    }

    private void addSettingsHeaderItems() {
        header.addNavigationItem(new AppNavItem("Sucursales", new VaadinAppIcon(VaadinIcon.BUILDING_O), BranchesView.class));
        header.addNavigationItem(new AppNavItem("Usuarios", new VaadinAppIcon(VaadinIcon.USERS), UsersView.class));
        header.addNavigationItem(new AppNavItem("Monedas", new VaadinAppIcon(VaadinIcon.DOLLAR), CurrenciesView.class));
        header.addNavigationItem(new AppNavItem("Impuestos", new VaadinAppIcon(VaadinIcon.FILE_ADD), TaxesView.class));
        header.addNavigationItem(new AppNavItem("General", new VaadinAppIcon(VaadinIcon.COG), GeneralSettingsView.class));
    }
}
