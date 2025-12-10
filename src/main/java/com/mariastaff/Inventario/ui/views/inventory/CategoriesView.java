package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Categorías | Inventario")
@Route(value = "inventory/categories", layout = MainLayout.class)
@PermitAll
public class CategoriesView extends VerticalLayout {

    private final CatalogoService service;
    private final Grid<InvCategoria> grid = new Grid<>(InvCategoria.class);

    public CategoriesView(CatalogoService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Listado de Categorías"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "descripcion");
        grid.addColumn(c -> c.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllCategorias());
    }
}
