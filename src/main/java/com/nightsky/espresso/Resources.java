package com.nightsky.espresso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class Resources
{
    public static Map<String,String> strings = new HashMap<>();

    private final static String languageBundleClassName = "StringResource";
    private final static Locale fallbackLocale = new Locale("en", "US");

    private static final String[] stringKeys = {
        "TITLE",
        "MENU_FILE",
        "MENU_EXIT",
        "MENU_GO",
        "MENU_UP",
        "MENU_VIEW",
        "MENU_TOGGLE_MENU",
        "TABLE_CELL_NAME",
        "TABLE_CELL_SIZE",
        "TABLE_CELL_TYPE",
        "TABLE_CELL_DATEMODIFIED",
        "TABLE_CELL_FILE",
        "TABLE_CELL_DIRECTORY",
        "BUTTON_UP",
        "EXCEPT_SYSTEM_APPEARANCE"
    };

    private static ResourceBundle languageBundle;
    private static String languageBundleName;

    public static void load()
    {
        languageBundleName = Resources.class.getPackageName() + "." + languageBundleClassName;
        
        try {
            try {
                languageBundle = ResourceBundle.getBundle(languageBundleName, Locale.getDefault());
            } catch (Exception e) {
                languageBundle = ResourceBundle.getBundle(languageBundleName, fallbackLocale);
            }

            for (String key : stringKeys)
                strings.put(key, languageBundle.getString(key));
        } catch (Exception e) {
            System.out.println("Unable to load string resources from bundle " + languageBundleName + ":");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public static String getString(String key)
    {
        return strings.get(key);
    }
}
