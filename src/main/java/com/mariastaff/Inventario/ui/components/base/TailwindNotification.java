package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.Command;

public class TailwindNotification extends Div {

    public enum Type {
        SUCCESS,
        INFO,
        ERROR
    }

    private TailwindNotification(String message, Type type) {
        // Common styles
        // We use inline Z-index to ensure it sits on top of everything without relying on JIT compilation for z-[10000] if it fails
        addClassNames("fixed", "bottom-4", "right-4", "flex", "items-center", "gap-2", "px-4", "py-3", "rounded-lg", "shadow-lg", "transform", "transition-all", "duration-500", "translate-y-10", "opacity-0");
        getStyle().set("z-index", "10000");
        
        // Type specific styles
        getStyle().set("color", "white");
        switch (type) {
            case SUCCESS:
                getStyle().set("background-color", "#16a34a"); // Green 600
                break;
            case ERROR:
                getStyle().set("background-color", "#dc2626"); // Red 600
                break;
            case INFO:
            default:
                getStyle().set("background-color", "#1f2937"); // Gray 800
                break;
        }

        Span text = new Span(message);
        text.addClassNames("font-medium", "text-sm");
        add(text);
    }

    public static void show(String message, Type type) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            TailwindNotification notification = new TailwindNotification(message, type);
            ui.add(notification);

            // Animate In with small delay to ensure DOM is ready
            new Thread(() -> {
                try { Thread.sleep(50); } catch (Exception e) {}
                ui.access(() -> {
                     notification.getStyle().set("opacity", "1");
                     notification.getStyle().set("transform", "translateY(0)");
                });
            }).start();

            // Auto Close after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {}
                
                try {
                    ui.access(() -> {
                        notification.getStyle().set("opacity", "0");
                        notification.getStyle().set("transform", "translateY(10px)");
                        // Remove from DOM after animation finishes
                        new Thread(() -> {
                            try {
                                Thread.sleep(500); 
                                ui.access(() -> notification.removeFromParent());
                            } catch (Exception ex) {}
                        }).start();
                    });
                } catch (Exception e) {
                   // UI might be detached
                }
            }).start();
        }
    }
}
