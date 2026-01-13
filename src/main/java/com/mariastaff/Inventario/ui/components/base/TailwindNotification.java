package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.Command;

public class TailwindNotification extends Div {

    public enum Type {
        SUCCESS,
        INFO,
        ERROR,
        WARNING
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
            case WARNING:
                getStyle().set("background-color", "#ca8a04"); // Yellow 600
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

            // Execute animation logic on the client side to avoid server roundtrip delays
            // 1. Wait a tick to ensure element is in DOM (setTimeout 10ms)
            // 2. Add properties to trigger transition (opacity 1, translate 0)
            // 3. Wait 3000ms
            // 4. Remove properties to trigger fade out
            // 5. Wait 500ms for transition
            // 6. Remove element
            notification.getElement().executeJs(
                "var el = this;" +
                "setTimeout(function() {" +
                "  el.style.opacity = '1';" +
                "  el.style.transform = 'translateY(0)';" +
                "}, 10);" +
                "setTimeout(function() {" +
                "  el.style.opacity = '0';" +
                "  el.style.transform = 'translateY(10px)';" +
                "  setTimeout(function() {" +
                "    el.remove();" +
                "  }, 500);" +
                "}, 3000);"
            );
        }
    }
}
