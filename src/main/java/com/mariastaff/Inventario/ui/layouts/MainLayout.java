package com.mariastaff.Inventario.ui.layouts;

import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.mariastaff.Inventario.ui.components.base.HeaderNavItem;
import com.mariastaff.Inventario.ui.components.base.ImageAppIcon;
import com.mariastaff.Inventario.ui.components.base.SidebarNavItem;
import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.mariastaff.Inventario.ui.components.composite.AppHeader;
import com.mariastaff.Inventario.ui.components.composite.AppSidebar;
import com.mariastaff.Inventario.ui.views.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MainLayout extends AppLayout {

    private AppHeader header;
    private AppSidebar sidebar;
    private boolean isMobileMode = false;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        getElement().getThemeList().clear();

        header = new AppHeader();
        header.addMenuListener(e -> {
            if (isMobileMode) {
                // On mobile, toggle drawer visibility
                setDrawerOpened(!isDrawerOpened());
            } else {
                // On desktop, toggle sidebar expansion
                sidebar.toggleSidebar();
            }
        });
        addToNavbar(header);

        sidebar = new AppSidebar(new ImageAppIcon("/images/logo-MariaStaff.png"), new ImageAppIcon("/images/icon-MariaStaff.png"), "");
        sidebar.setStateChangeHandler(expanded -> {
            if (!isMobileMode) {
                String width = expanded ? "16rem" : "45px";
                getElement().getStyle().set("--vaadin-app-layout-drawer-width", width);
                // Force update internal variable to fix gap
                getElement().getStyle().set("--_vaadin-app-layout-drawer-offset-size", width);
            }
        });
        
        // Set initial drawer state - closed on mobile
        getElement().executeJs(
            "const checkMobile = () => {" +
            "  const isMobile = window.innerWidth < 1024;" +
            "  this.isMobileMode = isMobile;" +
            "  if (isMobile) {" +
            "    this.drawerOpened = false;" +
            "    this.style.setProperty('--vaadin-app-layout-drawer-width', '16rem');" +
            "    this.style.setProperty('--_vaadin-app-layout-drawer-offset-size', '0px');" +
            "  } else {" +
            "    this.drawerOpened = true;" +
            "    this.style.setProperty('--vaadin-app-layout-drawer-width', '16rem');" +
            "    this.style.setProperty('--_vaadin-app-layout-drawer-offset-size', '16rem');" +
            "  }" +
            "  return isMobile;" +
            "};" +
            "window.addEventListener('resize', checkMobile);" +
            "const initialMobile = checkMobile();" +
            "return initialMobile;"
        ).then(result -> {
            isMobileMode = result.asBoolean();
            if (isMobileMode) {
                setDrawerOpened(false);
                getElement().getStyle().set("--_vaadin-app-layout-drawer-offset-size", "0px");
            } else {
                setDrawerOpened(true);
                // Initialize state for desktop
                getElement().getStyle().set("--vaadin-app-layout-drawer-width", "16rem");
                getElement().getStyle().set("--_vaadin-app-layout-drawer-offset-size", "16rem");
            }
        });
        
        addToDrawer(sidebar);

        setupSidebar();
        
        // Add backdrop click listener to close drawer on mobile
        getElement().executeJs(
            "this.addEventListener('drawer-opened-changed', (e) => {" +
            "  if (this.isMobileMode && e.detail.value) {" +
            "    if (!this._backdrop) {" +
            "      this._backdrop = document.createElement('div');" +
            "      this._backdrop.className = 'sidebar-backdrop active';" +
            "      this._backdrop.addEventListener('click', () => {" +
            "        this.drawerOpened = false;" +
            "      });" +
            "      document.body.appendChild(this._backdrop);" +
            "    }" +
            "    this._backdrop.classList.add('active');" +
            "  } else if (this._backdrop) {" +
            "    this._backdrop.classList.remove('active');" +
            "  }" +
            "});"
        );
    }

    private void setupSidebar() {
        // Main Modules - Linking to the default view of each module
        sidebar.addNavItem(new SidebarNavItem("nav.module.inventory", new VaadinAppIcon(VaadinIcon.STORAGE), InventoryDashboardView.class));
        sidebar.addNavItem(new SidebarNavItem("nav.module.purchases", new VaadinAppIcon(VaadinIcon.CART), NewPurchaseView.class));
        sidebar.addNavItem(new SidebarNavItem("nav.module.sales", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        sidebar.addNavItem(new SidebarNavItem("nav.module.accounting", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        sidebar.addNavItem(new SidebarNavItem("nav.module.reports", new VaadinAppIcon(VaadinIcon.FILE_TEXT), ReportSalesUserView.class));
        sidebar.addNavItem(new SidebarNavItem("nav.module.settings", new VaadinAppIcon(VaadinIcon.COG), BranchesView.class));
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        updateHeader();
        
        // Close drawer on mobile after navigation
        if (isMobileMode) {
            setDrawerOpened(false);
        }
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
        header.addNavigationItem(new HeaderNavItem("nav.inventory.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), InventoryDashboardView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.products", new VaadinAppIcon(VaadinIcon.PACKAGE), ProductsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.categories", new VaadinAppIcon(VaadinIcon.TAGS), CategoriesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.uom", new VaadinAppIcon(VaadinIcon.SLIDERS), UOMView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.movements", new VaadinAppIcon(VaadinIcon.EXCHANGE), MovementsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.warehouses", new VaadinAppIcon(VaadinIcon.BUILDING), WarehousesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.locations", new VaadinAppIcon(VaadinIcon.MAP_MARKER), LocationsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.batches", new VaadinAppIcon(VaadinIcon.BARCODE), BatchesView.class));
    }

    private boolean isPurchaseView(Class<?> view) {
        return view.equals(NewPurchaseView.class) || view.equals(PurchasesHistoryView.class) || 
               view.equals(ProvidersView.class);
    }

    private void addPurchaseHeaderItems() {
        header.addNavigationItem(new HeaderNavItem("nav.purchases.new", new VaadinAppIcon(VaadinIcon.PLUS_CIRCLE), NewPurchaseView.class));
        header.addNavigationItem(new HeaderNavItem("nav.purchases.history", new VaadinAppIcon(VaadinIcon.CLOCK), PurchasesHistoryView.class));
        header.addNavigationItem(new HeaderNavItem("nav.purchases.providers", new VaadinAppIcon(VaadinIcon.TRUCK), ProvidersView.class));
    }

    private boolean isSalesView(Class<?> view) {
        return view.equals(POSView.class) || view.equals(ShiftView.class) || 
               view.equals(ClosuresView.class) || view.equals(CustomersView.class) || 
               view.equals(ReceivablesView.class) || view.equals(SalesHistoryView.class);
    }

    private void addSalesHeaderItems() {
        header.addNavigationItem(new HeaderNavItem("nav.sales.pos", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.shift", new VaadinAppIcon(VaadinIcon.CLOCK), ShiftView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.closures", new VaadinAppIcon(VaadinIcon.LOCK), ClosuresView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.customers", new VaadinAppIcon(VaadinIcon.USER), CustomersView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.receivables", new VaadinAppIcon(VaadinIcon.MONEY), ReceivablesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.history", new VaadinAppIcon(VaadinIcon.FILE_TEXT), SalesHistoryView.class));
    }

    private boolean isAccountingView(Class<?> view) {
        return view.equals(FinancialDashboardView.class) || view.equals(JournalView.class) || 
               view.equals(ChartOfAccountsView.class) || view.equals(FiscalPeriodsView.class) || 
               view.equals(ManualEntriesView.class);
    }

    private void addAccountingHeaderItems() {
        header.addNavigationItem(new HeaderNavItem("nav.accounting.dashboard", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.journal", new VaadinAppIcon(VaadinIcon.BOOK), JournalView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.chart", new VaadinAppIcon(VaadinIcon.LIST), ChartOfAccountsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.periods", new VaadinAppIcon(VaadinIcon.CALENDAR), FiscalPeriodsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.manual", new VaadinAppIcon(VaadinIcon.EDIT), ManualEntriesView.class));
    }

    private boolean isReportsView(Class<?> view) {
        return view.equals(ReportSalesUserView.class) || view.equals(ReportTopProductsView.class) || 
               view.equals(ReportMarginsView.class) || view.equals(ReportKardexView.class) || 
               view.equals(ReportInventoryValueView.class) || view.equals(ReportIncomeStatementView.class) || 
               view.equals(ReportTrialBalanceView.class);
    }

    private void addReportsHeaderItems() {
        header.addNavigationItem(new HeaderNavItem("nav.reports.sales_user", new VaadinAppIcon(VaadinIcon.USER), ReportSalesUserView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.top_products", new VaadinAppIcon(VaadinIcon.STAR), ReportTopProductsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.margins", new VaadinAppIcon(VaadinIcon.TRENDING_UP), ReportMarginsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O), ReportKardexView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.inventory_value", new VaadinAppIcon(VaadinIcon.MONEY), ReportInventoryValueView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.income_statement", new VaadinAppIcon(VaadinIcon.PIE_CHART), ReportIncomeStatementView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.trial_balance", new VaadinAppIcon(VaadinIcon.SCALE), ReportTrialBalanceView.class));
    }

    private boolean isSettingsView(Class<?> view) {
        return view.equals(BranchesView.class) || view.equals(UsersView.class) || 
               view.equals(CurrenciesView.class) || view.equals(TaxesView.class) || 
               view.equals(GeneralSettingsView.class);
    }

    private void addSettingsHeaderItems() {
        header.addNavigationItem(new HeaderNavItem("nav.settings.branches", new VaadinAppIcon(VaadinIcon.BUILDING_O), BranchesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.users", new VaadinAppIcon(VaadinIcon.USERS), UsersView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.currencies", new VaadinAppIcon(VaadinIcon.DOLLAR), CurrenciesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.taxes", new VaadinAppIcon(VaadinIcon.FILE_ADD), TaxesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.general", new VaadinAppIcon(VaadinIcon.COG), GeneralSettingsView.class));
    }
}
