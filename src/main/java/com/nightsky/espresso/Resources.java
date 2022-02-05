package com.nightsky.espresso;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Resources
{
    public static Map<String,String> strings = new HashMap<>();

    private final static String stringsBundleClassName = "StringsBundle";
    private final static Locale fallbackLocale = new Locale("en", "US");

    private static ResourceBundle stringsBundle;
    private static String stringsBundleIdentifier;

    public static void load()
    {
        stringsBundleIdentifier = Resources.class.getPackageName() + "." + stringsBundleClassName;
        
        try {
            try {
                stringsBundle = ResourceBundle.getBundle(stringsBundleIdentifier, Locale.getDefault());
            } catch (Exception e) {
                stringsBundle = ResourceBundle.getBundle(stringsBundleIdentifier, fallbackLocale);
            }
            
            Enumeration<String> keys = stringsBundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                strings.put(key, stringsBundle.getString(key));
            }
        } catch (Exception e) {
            System.out.println("Unable to load string resources from bundle " + stringsBundleIdentifier + ":");
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public static String getString(String key)
    {
        return strings.get(key);
    }
}
