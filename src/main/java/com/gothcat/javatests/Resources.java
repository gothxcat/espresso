package com.gothcat.javatests;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Resources
{
    private final static String languageBundleClassName = "StringResource";
    private final static Locale fallbackLocale = new Locale("en", "US");

    public static String TITLE = "";
    public static String LABEL_MESSAGE = "";
    public static String BUTTON_OK = "";
    public static String MENU_FILE = "";
    public static String MENU_EXIT = "";

    private static final String[] keys = {
        "TITLE",
        "LABEL_MESSAGE",
        "BUTTON_OK",
        "MENU_FILE",
        "MENU_EXIT"
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

            for (String key : keys) {
                Field field = Resources.class.getDeclaredField(key);
                field.set(Resources.class, languageBundle.getString(key));
            }
        } catch (Exception e) {
            System.out.println("Unable to load string resources from bundle " + languageBundleName + ":");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
