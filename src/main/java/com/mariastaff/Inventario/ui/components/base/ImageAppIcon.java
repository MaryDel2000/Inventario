package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;

public class ImageAppIcon implements AppIcon {
    private final String src;
    private final String alt;

    public ImageAppIcon(String src) {
        this(src, "icon");
    }

    public ImageAppIcon(String src, String alt) {
        this.src = src;
        this.alt = alt;
    }

    @Override
    public Component create() {
        Image image = new Image(src, alt);
        image.setWidth("100%");
        image.setHeight("75%");
        image.getStyle().set("object-fit", "contain");
        return image;
    }
}
