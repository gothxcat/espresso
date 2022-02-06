package com.nightsky.espresso;

import java.awt.Color;
import java.time.ZoneId;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Platform {
    public final static Color caretColor = Color.WHITE;

    public static final ZoneId zoneId = ZoneId.systemDefault();

    private static final String osName = System.getProperty("os.name").toLowerCase();
    private static final String gtkAppearanceClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    public static boolean isWindows()
    {
        return (osName.indexOf("win") >= 0);
    }

    public static boolean isMac()
    {
        return (osName.indexOf("mac") >= 0);
    }

    public static boolean isUnix()
    {
        return (osName.indexOf("nix") >= 0
                || osName.indexOf("nux") >= 0
                || osName.indexOf("aix") >= 0);
    }

    public static boolean isSolaris()
    {
        return (osName.indexOf("sunos") >= 0);
    }

    public static String getLookAndFeel()
    {
        if (isUnix()) {
            LookAndFeelInfo[] appearances = UIManager.getInstalledLookAndFeels();
            for (LookAndFeelInfo appearance : appearances) {
                if (appearance.getClassName() == gtkAppearanceClassName) {
                    return gtkAppearanceClassName;
                }
            }
        }
        
        return UIManager.getSystemLookAndFeelClassName();
    }
}
