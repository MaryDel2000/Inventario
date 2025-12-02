package com.mariastaff.Inventario.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

public class UIElements extends VerticalLayout {

    public UIElements() {
        addClassNames("w-full", "bg-[var(--color-bg-surface)]", "shadow-sm", "rounded-lg", "p-6", "space-y-4");
        setSpacing(false);
        setPadding(false);

        // Toolbar: Search + Edit/Add
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassNames("w-full", "flex", "gap-4");
        toolbar.setSpacing(false);
        
        TextField searchBar = new TextField();
        searchBar.setPlaceholder("Buscar...");
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchBar.addClassNames("flex-grow");
        // Note: Vaadin TextField internal styling is complex, but we can style the host
        
        Button editButton = new Button("Editar", VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassNames("bg-[var(--color-primary)]", "text-white");

        toolbar.add(searchBar, editButton);

        // Table
        Grid<Person> grid = new Grid<>(Person.class, false);
        grid.addClassNames("border", "border-[var(--color-border)]", "rounded-md");
        grid.addColumn(Person::getFirstName).setHeader("Nombre");
        grid.addColumn(Person::getLastName).setHeader("Apellido");
        grid.addColumn(Person::getEmail).setHeader("Email");
        grid.setItems(createDummyData());
        grid.setHeight("300px");

        // Pagination
        HorizontalLayout pagination = new HorizontalLayout();
        pagination.addClassNames("flex", "justify-center", "gap-2", "mt-4");
        pagination.setSpacing(false);
        
        pagination.add(createPageBtn(VaadinIcon.ARROW_LEFT), createPageBtn("1"), createPageBtn("2"), createPageBtn("3"), createPageBtn(VaadinIcon.ARROW_RIGHT));

        add(toolbar, grid, pagination);
    }

    private Button createPageBtn(String text) {
        Button btn = new Button(text);
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return btn;
    }

    private Button createPageBtn(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return btn;
    }

    private List<Person> createDummyData() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Maria", "Staff", "maria@example.com"));
        people.add(new Person("Juan", "Perez", "juan@example.com"));
        people.add(new Person("Ana", "Gomez", "ana@example.com"));
        return people;
    }

    public static class Person {
        private String firstName;
        private String lastName;
        private String email;

        public Person(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}
