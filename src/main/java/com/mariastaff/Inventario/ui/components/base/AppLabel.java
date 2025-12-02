package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.html.Span;

public class AppLabel extends Span {

    public AppLabel(String i18nKey) {
        super();
        setText(getTranslation(i18nKey));
        // Default Tailwind styles for labels
        addClassNames("text-sm", "font-medium", "text-[var(--color-text-main)]");
    }

    public AppLabel(String i18nKey, String... classNames) {
        this(i18nKey);
        addClassNames(classNames);
    }
}
