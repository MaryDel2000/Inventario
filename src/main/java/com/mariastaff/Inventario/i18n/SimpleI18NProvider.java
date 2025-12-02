package com.mariastaff.Inventario.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

@Component
public class SimpleI18NProvider implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.unmodifiableList(Arrays.asList(
                new Locale("es"),
                new Locale("en")
        ));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        String value;
        try {
            value = bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }

        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }

        return value;
    }
}
