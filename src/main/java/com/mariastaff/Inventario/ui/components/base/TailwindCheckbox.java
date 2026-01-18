package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class TailwindCheckbox extends Div {

    private final Div box;
    private final Div checkmark;
    private boolean value = false;

    public TailwindCheckbox(String label) {
        addClassNames("flex", "items-center", "gap-2", "cursor-pointer", "py-1");

        box = new Div();
        box.addClassNames("h-5", "w-5", "rounded", "border", "border-gray-300", "flex", "items-center", "justify-center", "bg-white", "transition-colors");

        // Checkmark icon
        checkmark = new Div();
        checkmark.addClassNames("vaadin-icon", "text-white", "text-xs");
        checkmark.setText("✓");
        checkmark.getStyle().set("display", "none");

        box.add(checkmark);

        Span text = new Span(label);
        text.addClassNames("text-sm", "select-none");
        text.getStyle().set("color", "var(--color-text-main)");

        add(box, text);
        addClickListener(e -> setValue(!getValue()));
    }

    public void setValue(boolean value) {
        this.value = value;
        if (value) {
            box.removeClassName("bg-white");
            box.removeClassName("border-gray-300");
            box.addClassName("bg-primary");
            box.addClassName("border-primary");
            checkmark.getStyle().set("display", "block");
        } else {
            box.removeClassName("bg-primary");
            box.removeClassName("border-primary");
            box.addClassName("bg-white");
            box.addClassName("border-gray-300");
            checkmark.getStyle().set("display", "none");
        }
    }

    public boolean getValue() {
        return value;
    }
}
