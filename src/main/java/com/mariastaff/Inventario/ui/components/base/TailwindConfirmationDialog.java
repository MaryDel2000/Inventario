package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;

public class TailwindConfirmationDialog extends TailwindModal {

    public TailwindConfirmationDialog(String title, String message, String confirmButtonText, Runnable onConfirm) {
        super(title);

        Paragraph messageText = new Paragraph(message);
        messageText.addClassNames("text-text-secondary");
        addContent(messageText);

        Button cancelButton = new Button("Cancelar", e -> close());
        cancelButton.addClassNames("bg-gray-200", "text-gray-800", "font-medium", "py-2", "px-4", "rounded-lg",
                "hover:bg-gray-300");

        Button confirmButton = new Button(confirmButtonText, e -> {
            onConfirm.run();
            close();
        });
        confirmButton.addClassNames("bg-red-600", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg",
                "hover:bg-red-700", "shadow");

        addFooterButton(cancelButton);
        addFooterButton(confirmButton);

        // Make it smaller than the default modal
        setDialogMaxWidth("max-w-md");
    }
}
