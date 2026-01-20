package com.mariastaff.Inventario.ui.layouts;

import com.mariastaff.Inventario.ui.components.base.HeaderNavItem;
import com.mariastaff.Inventario.ui.components.base.ImageAppIcon;
import com.mariastaff.Inventario.ui.components.base.SidebarNavItem;
import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.mariastaff.Inventario.ui.components.composite.AppHeader;
import com.mariastaff.Inventario.ui.components.composite.AppSidebar;
import com.mariastaff.Inventario.ui.views.inventory.*;
import com.mariastaff.Inventario.ui.views.purchases.*;
import com.mariastaff.Inventario.ui.views.sales.*;
import com.mariastaff.Inventario.ui.views.accounting.*;
import com.mariastaff.Inventario.ui.views.reports.*;
import com.mariastaff.Inventario.ui.views.settings.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

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
        
        // Add resize listener to rebuild sidebar when switching between mobile/desktop
        getElement().executeJs(
            "let wasMobile = window.innerWidth < 1024;" +
            "window.addEventListener('resize', () => {" +
            "  const isMobile = window.innerWidth < 1024;" +
            "  if (isMobile !== wasMobile) {" +
            "    wasMobile = isMobile;" +
            "    this.$server.rebuildSidebar(isMobile);" +
            "  }" +
            "});"
        );
    }
    
    @com.vaadin.flow.component.ClientCallable
    public void rebuildSidebar(boolean isMobile) {
        this.isMobileMode = isMobile;
        setupSidebarForMode(isMobile);
        updateHeader();
    }

    private void setupSidebar() {
        // Check if we should setup for mobile or desktop
        getElement().executeJs("return window.innerWidth < 1024;").then(result -> {
            boolean isMobile = result.asBoolean();
            this.isMobileMode = isMobile;

            // DEBUG: Print current authorities
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                System.out.println("DEBUG: User " + auth.getName() + " has authorities: " + auth.getAuthorities());
            } else {
                System.out.println("DEBUG: No auth context in setupSidebar");
            }

            setupSidebarForMode(isMobile);
            updateHeader();
        });
    }
    
    private void setupSidebarForMode(boolean isMobile) {
        sidebar.clearAllItems();
        
        if (isMobile) {
            // Mobile: Use expandable items with sub-navigations
            setupMobileSidebar();
        } else {
            // Desktop: Use simple items (header will show sub-navigation)
            setupDesktopSidebar();
        }
    }
    
    private void setupDesktopSidebar() {
        // Simple items - sub-navigation will appear in header
        if (hasAccess("INVENTARIO", "MODULE_INVENTORY")) {
            sidebar.addNavItem(new SidebarNavItem("nav.module.inventory", new VaadinAppIcon(VaadinIcon.STORAGE), InventoryDashboardView.class));
            sidebar.addNavItem(new SidebarNavItem("nav.module.purchases", new VaadinAppIcon(VaadinIcon.CART), NewPurchaseView.class));
        }
        
        if (hasAccess("CAJERO", "MODULE_SALES")) {
            sidebar.addNavItem(new SidebarNavItem("nav.module.sales", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        }
        
        if (hasAccess("CONTADOR", "MODULE_ACCOUNTING")) {
            sidebar.addNavItem(new SidebarNavItem("nav.module.accounting", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        }
        
        if (hasAccess("CONTADOR", "INVENTARIO", "MODULE_REPORTS", "MODULE_ACCOUNTING", "MODULE_INVENTORY")) {
            sidebar.addNavItem(new SidebarNavItem("nav.module.reports", new VaadinAppIcon(VaadinIcon.FILE_TEXT), ReportSalesUserView.class));
        }
        
        if (hasAccess("ADMIN", "MODULE_SETTINGS")) {
            sidebar.addNavItem(new SidebarNavItem("nav.module.settings", new VaadinAppIcon(VaadinIcon.COG), BranchesView.class));
        }
    }
    
    private void setupMobileSidebar() {
        // Mobile: Expandable items with all sub-navigations
        
        // Inventory module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem inventoryItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.inventory", new VaadinAppIcon(VaadinIcon.STORAGE), InventoryDashboardView.class);
        inventoryItem.addSubItem(new SidebarNavItem("nav.inventory.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), InventoryDashboardView.class));
        inventoryItem.addSubItem(new SidebarNavItem("nav.inventory.products", new VaadinAppIcon(VaadinIcon.PACKAGE), ProductsView.class));


        inventoryItem.addSubItem(new SidebarNavItem("nav.inventory.movements", new VaadinAppIcon(VaadinIcon.EXCHANGE), MovementsView.class));
        inventoryItem.addSubItem(new SidebarNavItem("nav.inventory.warehouses", new VaadinAppIcon(VaadinIcon.BUILDING), WarehousesView.class));
        inventoryItem.addSubItem(new SidebarNavItem("nav.inventory.locations", new VaadinAppIcon(VaadinIcon.MAP_MARKER), LocationsView.class));

        sidebar.addExpandableItem(inventoryItem);
        
        // Purchases module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem purchasesItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.purchases", new VaadinAppIcon(VaadinIcon.CART), NewPurchaseView.class);
        purchasesItem.addSubItem(new SidebarNavItem("nav.purchases.new", new VaadinAppIcon(VaadinIcon.PLUS_CIRCLE), NewPurchaseView.class));
        purchasesItem.addSubItem(new SidebarNavItem("nav.purchases.history", new VaadinAppIcon(VaadinIcon.CLOCK), PurchasesHistoryView.class));
        purchasesItem.addSubItem(new SidebarNavItem("nav.purchases.providers", new VaadinAppIcon(VaadinIcon.TRUCK), ProvidersView.class));
        sidebar.addExpandableItem(purchasesItem);
        
        // Sales module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem salesItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.sales", new VaadinAppIcon(VaadinIcon.CASH), POSView.class);
        salesItem.addSubItem(new SidebarNavItem("nav.sales.pos", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        salesItem.addSubItem(new SidebarNavItem("nav.sales.shift", new VaadinAppIcon(VaadinIcon.CLOCK), ShiftView.class));
        salesItem.addSubItem(new SidebarNavItem("nav.sales.closures", new VaadinAppIcon(VaadinIcon.LOCK), ClosuresView.class));
        salesItem.addSubItem(new SidebarNavItem("nav.sales.customers", new VaadinAppIcon(VaadinIcon.USER), CustomersView.class));
        salesItem.addSubItem(new SidebarNavItem("nav.sales.receivables", new VaadinAppIcon(VaadinIcon.MONEY), ReceivablesView.class));
        salesItem.addSubItem(new SidebarNavItem("nav.sales.history", new VaadinAppIcon(VaadinIcon.FILE_TEXT), SalesHistoryView.class));
        sidebar.addExpandableItem(salesItem);
        
        // Accounting module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem accountingItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.accounting", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class);
        accountingItem.addSubItem(new SidebarNavItem("nav.accounting.dashboard", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        accountingItem.addSubItem(new SidebarNavItem("nav.accounting.journal", new VaadinAppIcon(VaadinIcon.BOOK), JournalEntriesView.class));
        accountingItem.addSubItem(new SidebarNavItem("nav.accounting.chart", new VaadinAppIcon(VaadinIcon.LIST), ChartOfAccountsView.class));
        accountingItem.addSubItem(new SidebarNavItem("nav.accounting.periods", new VaadinAppIcon(VaadinIcon.CALENDAR), FiscalPeriodsView.class));
        // accountingItem.addSubItem(new SidebarNavItem("nav.accounting.manual", new VaadinAppIcon(VaadinIcon.EDIT), ManualEntriesView.class)); // Removed
        sidebar.addExpandableItem(accountingItem);
        
        // Reports module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem reportsItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.reports", new VaadinAppIcon(VaadinIcon.FILE_TEXT), ReportSalesUserView.class);
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.sales_user", new VaadinAppIcon(VaadinIcon.USER), ReportSalesUserView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.top_products", new VaadinAppIcon(VaadinIcon.STAR), ReportTopProductsView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.margins", new VaadinAppIcon(VaadinIcon.TRENDING_UP), ReportMarginsView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O), ReportKardexView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.inventory_value", new VaadinAppIcon(VaadinIcon.MONEY), ReportInventoryValueView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.income_statement", new VaadinAppIcon(VaadinIcon.PIE_CHART), ReportIncomeStatementView.class));
        reportsItem.addSubItem(new SidebarNavItem("nav.reports.trial_balance", new VaadinAppIcon(VaadinIcon.SCALE), ReportTrialBalanceView.class));
        sidebar.addExpandableItem(reportsItem);
        
        // Settings module
        com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem settingsItem = 
            new com.mariastaff.Inventario.ui.components.base.ExpandableSidebarNavItem("nav.module.settings", new VaadinAppIcon(VaadinIcon.COG), BranchesView.class);
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.branches", new VaadinAppIcon(VaadinIcon.BUILDING_O), BranchesView.class));
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.users", new VaadinAppIcon(VaadinIcon.USERS), UsersView.class));
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.currencies", new VaadinAppIcon(VaadinIcon.DOLLAR), CurrenciesView.class));
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.taxes", new VaadinAppIcon(VaadinIcon.FILE_ADD), TaxesView.class));
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.general", new VaadinAppIcon(VaadinIcon.COG), GeneralSettingsView.class));
        settingsItem.addSubItem(new SidebarNavItem("nav.settings.backups", new VaadinAppIcon(VaadinIcon.DATABASE), BackupView.class));
        sidebar.addExpandableItem(settingsItem);
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

        if (isMobileMode) {
            ViewInfo info = getViewInfo(currentView);
            if (info != null) {
                Span text = new Span(getTranslation(info.titleKey));
                text.addClassNames("font-bold", "text-lg", "text-text-main");
                
                com.vaadin.flow.component.Component icon = info.icon.create();
                icon.addClassName("text-primary");
                
                HorizontalLayout hl = new HorizontalLayout(icon, text);
                hl.setAlignItems(FlexComponent.Alignment.CENTER);
                hl.setSpacing(true);
                
                header.addNavigationItem(hl);
            }
            return;
        }

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
               view.equals(MovementsView.class) || view.equals(WarehousesView.class) || 
               view.equals(LocationsView.class);
    }

    private boolean hasAccess(String... rolesOrPermissions) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        
        // Default Admin Access (Always has access)
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_AUTHENTIK_ADMINS"));
        if (isAdmin) return true;

        for (String target : rolesOrPermissions) {
            // 1. Direct Authority Check (e.g. MODULE_INVENTORY)
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(target))) return true;

            // 2. Legacy Role Check (e.g. INVENTARIO -> ROLE_INVENTARIO)
            String roleAuth = "ROLE_" + target.toUpperCase();
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(roleAuth))) return true;
            
            // 3. Mapping Legacy Role Name to Permission
            String mappedPerm = null;
            if ("INVENTARIO".equalsIgnoreCase(target)) mappedPerm = "MODULE_INVENTORY";
            else if ("CAJERO".equalsIgnoreCase(target)) mappedPerm = "MODULE_SALES";
            else if ("CONTADOR".equalsIgnoreCase(target)) mappedPerm = "MODULE_ACCOUNTING";
            else if ("SETTINGS".equalsIgnoreCase(target)) mappedPerm = "MODULE_SETTINGS";
            else if ("REPORTS".equalsIgnoreCase(target)) mappedPerm = "MODULE_REPORTS";
            
            if (mappedPerm != null) {
                String finalPerm = mappedPerm;
                if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(finalPerm))) return true;
            }
        }
        return false;
    }

    private void addInventoryHeaderItems() {
        if (!hasAccess("INVENTARIO", "MODULE_INVENTORY", "ADMIN")) return;
        header.addNavigationItem(new HeaderNavItem("nav.inventory.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), InventoryDashboardView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.products", new VaadinAppIcon(VaadinIcon.PACKAGE), ProductsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.movements", new VaadinAppIcon(VaadinIcon.EXCHANGE), MovementsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.warehouses", new VaadinAppIcon(VaadinIcon.BUILDING), WarehousesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.inventory.locations", new VaadinAppIcon(VaadinIcon.MAP_MARKER), LocationsView.class));
    }

    private boolean isPurchaseView(Class<?> view) {
        return view.equals(NewPurchaseView.class) || view.equals(PurchasesHistoryView.class) || 
               view.equals(ProvidersView.class);
    }

    private void addPurchaseHeaderItems() {
        if (!hasAccess("INVENTARIO", "MODULE_INVENTORY", "ADMIN")) return;
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
        if (!hasAccess("CAJERO", "MODULE_SALES", "ADMIN")) return;
        header.addNavigationItem(new HeaderNavItem("nav.sales.pos", new VaadinAppIcon(VaadinIcon.CASH), POSView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.shift", new VaadinAppIcon(VaadinIcon.CLOCK), ShiftView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.closures", new VaadinAppIcon(VaadinIcon.LOCK), ClosuresView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.customers", new VaadinAppIcon(VaadinIcon.USER), CustomersView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.receivables", new VaadinAppIcon(VaadinIcon.MONEY), ReceivablesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.sales.history", new VaadinAppIcon(VaadinIcon.FILE_TEXT), SalesHistoryView.class));
    }

    private boolean isAccountingView(Class<?> view) {
        return view.equals(FinancialDashboardView.class) || view.equals(JournalEntriesView.class) || 
               view.equals(ChartOfAccountsView.class) || view.equals(FiscalPeriodsView.class);
               // || view.equals(ManualEntriesView.class);
    }

    private void addAccountingHeaderItems() {
        if (!hasAccess("CONTADOR", "MODULE_ACCOUNTING", "ADMIN")) return;
        header.addNavigationItem(new HeaderNavItem("nav.accounting.dashboard", new VaadinAppIcon(VaadinIcon.CHART), FinancialDashboardView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.journal", new VaadinAppIcon(VaadinIcon.BOOK), JournalEntriesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.chart", new VaadinAppIcon(VaadinIcon.LIST), ChartOfAccountsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.accounting.periods", new VaadinAppIcon(VaadinIcon.CALENDAR), FiscalPeriodsView.class));
        // header.addNavigationItem(new HeaderNavItem("nav.accounting.manual", new VaadinAppIcon(VaadinIcon.EDIT), ManualEntriesView.class));
    }

    private boolean isReportsView(Class<?> view) {
        return view.equals(ReportSalesUserView.class) || view.equals(ReportTopProductsView.class) || 
               view.equals(ReportMarginsView.class) || view.equals(ReportKardexView.class) || 
               view.equals(ReportInventoryValueView.class) || view.equals(ReportIncomeStatementView.class) || 
               view.equals(ReportTrialBalanceView.class);
    }

    private void addReportsHeaderItems() {
        if (!hasAccess("CONTADOR", "MODULE_ACCOUNTING", "ADMIN", "INVENTARIO", "MODULE_INVENTORY", "REPORTS", "MODULE_REPORTS")) return;
        
        header.addNavigationItem(new HeaderNavItem("nav.reports.sales_user", new VaadinAppIcon(VaadinIcon.USER), ReportSalesUserView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.top_products", new VaadinAppIcon(VaadinIcon.STAR), ReportTopProductsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.margins", new VaadinAppIcon(VaadinIcon.TRENDING_UP), ReportMarginsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O), ReportKardexView.class));
        header.addNavigationItem(new HeaderNavItem("nav.reports.inventory_value", new VaadinAppIcon(VaadinIcon.MONEY), ReportInventoryValueView.class));
        
        if (hasAccess("CONTADOR", "MODULE_ACCOUNTING", "ADMIN")) {
            header.addNavigationItem(new HeaderNavItem("nav.reports.income_statement", new VaadinAppIcon(VaadinIcon.PIE_CHART), ReportIncomeStatementView.class));
            header.addNavigationItem(new HeaderNavItem("nav.reports.trial_balance", new VaadinAppIcon(VaadinIcon.SCALE), ReportTrialBalanceView.class));
        }
    }

    private boolean isSettingsView(Class<?> view) {
        return view.equals(BranchesView.class) || view.equals(UsersView.class) || 
               view.equals(CurrenciesView.class) || view.equals(TaxesView.class) || 
               view.equals(GeneralSettingsView.class) || view.equals(BackupView.class);
    }

    private void addSettingsHeaderItems() {
        if (!hasAccess("ADMIN", "MODULE_SETTINGS")) return;
        header.addNavigationItem(new HeaderNavItem("nav.settings.branches", new VaadinAppIcon(VaadinIcon.BUILDING_O), BranchesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.users", new VaadinAppIcon(VaadinIcon.USERS), UsersView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.currencies", new VaadinAppIcon(VaadinIcon.DOLLAR), CurrenciesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.taxes", new VaadinAppIcon(VaadinIcon.FILE_ADD), TaxesView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.general", new VaadinAppIcon(VaadinIcon.COG), GeneralSettingsView.class));
        header.addNavigationItem(new HeaderNavItem("nav.settings.backups", new VaadinAppIcon(VaadinIcon.DATABASE), BackupView.class));
    }

    private ViewInfo getViewInfo(Class<?> view) {
        // Inventory
        if (view.equals(InventoryDashboardView.class)) return new ViewInfo("nav.inventory.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD));
        if (view.equals(ProductsView.class)) return new ViewInfo("nav.inventory.products", new VaadinAppIcon(VaadinIcon.PACKAGE));


        if (view.equals(MovementsView.class)) return new ViewInfo("nav.inventory.movements", new VaadinAppIcon(VaadinIcon.EXCHANGE));
        if (view.equals(WarehousesView.class)) return new ViewInfo("nav.inventory.warehouses", new VaadinAppIcon(VaadinIcon.BUILDING));
        if (view.equals(LocationsView.class)) return new ViewInfo("nav.inventory.locations", new VaadinAppIcon(VaadinIcon.MAP_MARKER));

        
        // Purchases
        if (view.equals(NewPurchaseView.class)) return new ViewInfo("nav.purchases.new", new VaadinAppIcon(VaadinIcon.PLUS_CIRCLE));
        if (view.equals(PurchasesHistoryView.class)) return new ViewInfo("nav.purchases.history", new VaadinAppIcon(VaadinIcon.CLOCK));
        if (view.equals(ProvidersView.class)) return new ViewInfo("nav.purchases.providers", new VaadinAppIcon(VaadinIcon.TRUCK));
        
        // Sales
        if (view.equals(POSView.class)) return new ViewInfo("nav.sales.pos", new VaadinAppIcon(VaadinIcon.CASH));
        if (view.equals(ShiftView.class)) return new ViewInfo("nav.sales.shift", new VaadinAppIcon(VaadinIcon.CLOCK));
        if (view.equals(ClosuresView.class)) return new ViewInfo("nav.sales.closures", new VaadinAppIcon(VaadinIcon.LOCK));
        if (view.equals(CustomersView.class)) return new ViewInfo("nav.sales.customers", new VaadinAppIcon(VaadinIcon.USER));
        if (view.equals(ReceivablesView.class)) return new ViewInfo("nav.sales.receivables", new VaadinAppIcon(VaadinIcon.MONEY));
        if (view.equals(SalesHistoryView.class)) return new ViewInfo("nav.sales.history", new VaadinAppIcon(VaadinIcon.FILE_TEXT));
        
        // Accounting
        if (view.equals(FinancialDashboardView.class)) return new ViewInfo("nav.accounting.dashboard", new VaadinAppIcon(VaadinIcon.CHART));
        if (view.equals(JournalEntriesView.class)) return new ViewInfo("nav.accounting.journal", new VaadinAppIcon(VaadinIcon.BOOK));
        if (view.equals(ChartOfAccountsView.class)) return new ViewInfo("nav.accounting.chart", new VaadinAppIcon(VaadinIcon.LIST));
        if (view.equals(FiscalPeriodsView.class)) return new ViewInfo("nav.accounting.periods", new VaadinAppIcon(VaadinIcon.CALENDAR));
        // if (view.equals(ManualEntriesView.class)) return new ViewInfo("nav.accounting.manual", new VaadinAppIcon(VaadinIcon.EDIT));
        
        // Reports
        if (view.equals(ReportSalesUserView.class)) return new ViewInfo("nav.reports.sales_user", new VaadinAppIcon(VaadinIcon.USER));
        if (view.equals(ReportTopProductsView.class)) return new ViewInfo("nav.reports.top_products", new VaadinAppIcon(VaadinIcon.STAR));
        if (view.equals(ReportMarginsView.class)) return new ViewInfo("nav.reports.margins", new VaadinAppIcon(VaadinIcon.TRENDING_UP));
        if (view.equals(ReportKardexView.class)) return new ViewInfo("nav.reports.kardex", new VaadinAppIcon(VaadinIcon.FILE_TEXT_O));
        if (view.equals(ReportInventoryValueView.class)) return new ViewInfo("nav.reports.inventory_value", new VaadinAppIcon(VaadinIcon.MONEY));
        if (view.equals(ReportIncomeStatementView.class)) return new ViewInfo("nav.reports.income_statement", new VaadinAppIcon(VaadinIcon.PIE_CHART));
        if (view.equals(ReportTrialBalanceView.class)) return new ViewInfo("nav.reports.trial_balance", new VaadinAppIcon(VaadinIcon.SCALE));
        
        // Settings
        if (view.equals(BranchesView.class)) return new ViewInfo("nav.settings.branches", new VaadinAppIcon(VaadinIcon.BUILDING_O));
        if (view.equals(UsersView.class)) return new ViewInfo("nav.settings.users", new VaadinAppIcon(VaadinIcon.USERS));
        if (view.equals(CurrenciesView.class)) return new ViewInfo("nav.settings.currencies", new VaadinAppIcon(VaadinIcon.DOLLAR));
        if (view.equals(TaxesView.class)) return new ViewInfo("nav.settings.taxes", new VaadinAppIcon(VaadinIcon.FILE_ADD));
        if (view.equals(GeneralSettingsView.class)) return new ViewInfo("nav.settings.general", new VaadinAppIcon(VaadinIcon.COG));
        if (view.equals(BackupView.class)) return new ViewInfo("nav.settings.backups", new VaadinAppIcon(VaadinIcon.DATABASE));

        return null;
    }

    private static class ViewInfo {
        final String titleKey;
        final VaadinAppIcon icon;
        ViewInfo(String titleKey, VaadinAppIcon icon) {
            this.titleKey = titleKey;
            this.icon = icon;
        }
    }
}
