package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeLabel;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TailwindDatePicker extends CustomField<LocalDate> {

    private final Input inputElement = new Input();

    public TailwindDatePicker(String label) {
        setLabel(label);
        setUpComponent();
    }
    
    public TailwindDatePicker() {
        setUpComponent();
    }

    private void setUpComponent() {
        inputElement.setType("date");
        
        // Add Tailwind classes to Input
        // Matching the styling of other inputs in the app
        inputElement.addClassNames(
            "w-full", "h-10", "px-3", "py-2",
            "rounded-md", "border", "border-gray-200", 
            "bg-white", "text-gray-900", "placeholder-gray-400",
            "focus:outline-none", "focus:ring-2", "focus:ring-primary", "focus:border-transparent",
            "transition-colors", "duration-200",
            "text-sm",
            "dark:bg-gray-800", "dark:border-gray-700", "dark:text-gray-100"
        );
        
        // Vertical layout style
        addClassNames("block", "w-full");
        
        add(inputElement);
        
        // Listen to client-side changes
        inputElement.addValueChangeListener(e -> {
            setModelValue(generateModelValue(), true);
        });
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @Override
    protected LocalDate generateModelValue() {
        String val = inputElement.getValue();
        if (val == null || val.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(val);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    protected void setPresentationValue(LocalDate newPresentationValue) {
        if (newPresentationValue != null) {
            inputElement.setValue(newPresentationValue.toString()); // is yyyy-MM-dd
        } else {
            inputElement.setValue("");
        }
    }
}
