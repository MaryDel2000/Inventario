package com.mariastaff.Inventario.ui.layouts;

import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.mariastaff.Inventario.ui.components.base.ImageAppIcon;
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
        header.addMenuListener(e -> sidebar.toggleSidebar());
        addToNavbar(header);

        sidebar = new AppSidebar(new ImageAppIcon("/images/logo-MariaStaff.png"), "");
        sidebar.setStateChangeHandler(expanded -> {
            String width = expanded ? "16rem" : "2.5rem";
            getElement().getStyle().set("--vaadin-app-layout-drawer-width", width);
            // Force update internal variable to fix gap
            getElement().getStyle().set("--_vaadin-app-layout-drawer-offset-size", width);
        });
        // Initialize state
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "16rem");
        getElement().getStyle().set("--_vaadin-app-layout-drawer-offset-size", "16rem");
        
        addToDrawer(sidebar);

        setupSidebar();
    }

    private void setupSidebar() {
        // Main Modules - Linking to the default view of each module
        sidebar.addNavItem(new AppNavItem("nav.module.inventory", new VaadinAppIcon(VaadinIcon.STORAGE), InventoryDashboardView.class));
        sidebar.addNavItem(new AppNavItem("nav.module.purchases", new VaadinAppIcon(VaadinIcon.CART), NewPurchaseView.class));
        sidebar.addNavItem(new AppNavItem("nav.module.sales", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        sidebar.addNavItem(new AppNavItem("nav.module.accounting", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        sidebar.addNavItem(new AppNavItem("nav.module.reports", new VaadinAppIcon(VaadinIcon.FILE_TEXT), ReportSalesUserView.class));
        sidebar.addNavItem(new AppNavItem("nav.module.settings", new VaadinAppIcon(VaadinIcon.COG), BranchesView.class));
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
        header.addNavigationItem(new AppNavItem("nav.inventory.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), InventoryDashboardView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.products", new VaadinAppIcon(VaadinIcon.PACKAGE), ProductsView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.categories", new VaadinAppIcon(VaadinIcon.TAGS), CategoriesView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.uom", new VaadinAppIcon(VaadinIcon.SLIDERS), UOMView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.movements", new VaadinAppIcon(VaadinIcon.EXCHANGE), MovementsView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.warehouses", new VaadinAppIcon(VaadinIcon.BUILDING), WarehousesView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.locations", new VaadinAppIcon(VaadinIcon.MAP_MARKER), LocationsView.class));
        header.addNavigationItem(new AppNavItem("nav.inventory.batches", new VaadinAppIcon(VaadinIcon.BARCODE), BatchesView.class));
    }

    private boolean isPurchaseView(Class<?> view) {
        return view.equals(NewPurchaseView.class) || view.equals(PurchasesHistoryView.class) || 
               view.equals(ProvidersView.class);
    }

    private void addPurchaseHeaderItems() {
        header.addNavigationItem(new AppNavItem("nav.purchases.new", new VaadinAppIcon(VaadinIcon.PLUS_CIRCLE), NewPurchaseView.class));
        header.addNavigationItem(new AppNavItem("nav.purchases.history", new VaadinAppIcon(VaadinIcon.CLOCK), PurchasesHistoryView.class));
        header.addNavigationItem(new AppNavItem("nav.purchases.providers", new VaadinAppIcon(VaadinIcon.TRUCK), ProvidersView.class));
    }

    private boolean isSalesView(Class<?> view) {
        return view.equals(POSView.class) || view.equals(ShiftView.class) || 
               view.equals(ClosuresView.class) || view.equals(CustomersView.class) || 
               view.equals(ReceivablesView.class) || view.equals(SalesHistoryView.class);
    }

    private void addSalesHeaderItems() {
        header.addNavigationItem(new AppNavItem("nav.sales.pos", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        header.addNavigationItem(new AppNavItem("nav.sales.shift", new VaadinAppIcon(VaadinIcon.CLOCK), ShiftView.class));
        header.addNavigationItem(new AppNavItem("nav.sales.closures", new VaadinAppIcon(VaadinIcon.LOCK), ClosuresView.class));
        header.addNavigationItem(new AppNavItem("nav.sales.customers", new VaadinAppIcon(VaadinIcon.USER), CustomersView.class));
        header.addNavigationItem(new AppNavItem("nav.sales.receivables", new VaadinAppIcon(VaadinIcon.MONEY), ReceivablesView.class));
        header.addNavigationItem(new AppNavItem("nav.sales.history", new VaadinAppIcon(VaadinIcon.FILE_TEXT), SalesHistoryView.class));
    }

    private boolean isAccountingView(Class<?> view) {
        return view.equals(FinancialDashboardView.class) || view.equals(JournalView.class) || 
               view.equals(ChartOfAccountsView.class) || view.equals(FiscalPeriodsView.class) || 
               view.equals(ManualEntriesView.class);
    }

    private void addAccountingHeaderItems() {
        header.addNavigationItem(new AppNavItem("nav.accounting.dashboard", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        header.addNavigationItem(new AppNavItem("nav.accounting.journal", new VaadinAppIcon(VaadinIcon.BOOK), JournalView.class));
        header.addNavigationItem(new AppNavItem("nav.accounting.chart", new VaadinAppIcon(VaadinIcon.LIST), ChartOfAccountsView.class));
        header.addNavigationItem(new AppNavItem("nav.accounting.periods", new VaadinAppIcon(VaadinIcon.CALENDAR), FiscalPeriodsView.class));
        header.addNavigationItem(new AppNavItem("nav.accounting.manual", new VaadinAppIcon(VaadinIcon.EDIT), ManualEntriesView.class));
    }

    private boolean isReportsView(Class<?> view) {
        return view.equals(ReportSalesUserView.class) || view.equals(ReportTopProductsView.class) || 
               view.equals(ReportMarginsView.class) || view.equals(ReportKardexView.class) || 
               view.equals(ReportInventoryValueView.class) || view.equals(ReportIncomeStatementView.class) || 
               view.equals(ReportTrialBalanceView.class);
    }

    private void addReportsHeaderItems() {
        header.addNavigationItem(new AppNavItem("nav.reports.sales_user", new VaadinAppIcon(VaadinIcon.USER), ReportSalesUserView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.top_products", new VaadinAppIcon(VaadinIcon.STAR), ReportTopProductsView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.margins", new VaadinAppIcon(VaadinIcon.TRENDING_UP), ReportMarginsView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O), ReportKardexView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.inventory_value", new VaadinAppIcon(VaadinIcon.MONEY), ReportInventoryValueView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.income_statement", new VaadinAppIcon(VaadinIcon.PIE_CHART), ReportIncomeStatementView.class));
        header.addNavigationItem(new AppNavItem("nav.reports.trial_balance", new VaadinAppIcon(VaadinIcon.SCALE), ReportTrialBalanceView.class));
    }

    private boolean isSettingsView(Class<?> view) {
        return view.equals(BranchesView.class) || view.equals(UsersView.class) || 
               view.equals(CurrenciesView.class) || view.equals(TaxesView.class) || 
               view.equals(GeneralSettingsView.class);
    }

    private void addSettingsHeaderItems() {
        header.addNavigationItem(new AppNavItem("nav.settings.branches", new VaadinAppIcon(VaadinIcon.BUILDING_O), BranchesView.class));
        header.addNavigationItem(new AppNavItem("nav.settings.users", new VaadinAppIcon(VaadinIcon.USERS), UsersView.class));
        header.addNavigationItem(new AppNavItem("nav.settings.currencies", new VaadinAppIcon(VaadinIcon.DOLLAR), CurrenciesView.class));
        header.addNavigationItem(new AppNavItem("nav.settings.taxes", new VaadinAppIcon(VaadinIcon.FILE_ADD), TaxesView.class));
        header.addNavigationItem(new AppNavItem("nav.settings.general", new VaadinAppIcon(VaadinIcon.COG), GeneralSettingsView.class));
    }
}
