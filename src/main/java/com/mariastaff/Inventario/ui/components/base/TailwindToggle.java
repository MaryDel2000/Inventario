package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;

@Tag("div")
public class TailwindToggle extends AbstractField<TailwindToggle, Boolean> implements ClickNotifier<TailwindToggle> {

    private final Div switchBg;
    private final Div switchKnob;
    private final Span labelSpan;

    public TailwindToggle(String label) {
        super(false);
        addClassNames("flex", "items-center", "gap-3", "cursor-pointer");

        // Label
        labelSpan = new Span(label);
        labelSpan.addClassNames("text-sm", "font-medium", "text-gray-900");

        // Switch Background
        switchBg = new Div();
        switchBg.addClassNames("relative", "inline-flex", "h-6", "w-11", "flex-shrink-0", "cursor-pointer", "rounded-full", "border-2", "border-transparent", "transition-colors", "duration-200", "ease-in-out", "focus:outline-none", "focus:ring-2", "focus:ring-primary", "focus:ring-offset-2", "bg-gray-200");
        
        // Knob
        switchKnob = new Div();
        switchKnob.addClassNames("pointer-events-none", "inline-block", "h-5", "w-5", "transform", "rounded-full", "bg-white", "shadow", "ring-0", "transition", "duration-200", "ease-in-out", "translate-x-0");

        switchBg.add(switchKnob);
        getElement().appendChild(switchBg.getElement(), labelSpan.getElement());

        addClickListener(e -> setValue(!getValue()));
    }

    @Override
    protected void setPresentationValue(Boolean newValue) {
        if (newValue) {
           switchBg.removeClassName("bg-gray-200");
           switchBg.addClassName("bg-primary");
           switchKnob.removeClassName("translate-x-0");
           switchKnob.addClassName("translate-x-5");
        } else {
           switchBg.removeClassName("bg-primary");
           switchBg.addClassName("bg-gray-200");
           switchKnob.removeClassName("translate-x-5");
           switchKnob.addClassName("translate-x-0");
        }
    }
}
