package com.nightsky.espresso;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileSystemView;

public class Platform {
    public static final File rootDirectory = getRootDirectory();
    public static final File userHomeDirectory = getUserHomeDirectory();
    public static final String pathSeparator = getPathSeparator();
    public static final Color caretColor = Color.WHITE;
    public static final ZoneId zoneId = ZoneId.systemDefault();

    private static final String osName = System.getProperty("os.name").toLowerCase();
    private static final String gtkAppearanceClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    public static File getRootDirectory() {
        File[] roots = FileSystemView.getFileSystemView().getRoots();
        if (roots.length > 0) {
            return roots[0];
        } else {
            return new File("/");
        }
    }

    public static File getUserHomeDirectory() {
        String path;
        String userHomeProperty = System.getProperty("user.home");
        if (userHomeProperty.length() > 0
            && Files.isDirectory(Path.of(userHomeProperty))) {
            path = userHomeProperty;
        } else {
            path = ".";
        }

        return new File(path);
    }

    public static String getPathSeparator() {
        String pathSeparator;
        String pathSeparatorProperty = System.getProperty("file.separator");
        if (pathSeparatorProperty.length() > 0) {
            pathSeparator = pathSeparatorProperty;
        } else {
            pathSeparator = "/";
        }

        return pathSeparator;
    }

    public static boolean isWindows() {
        return (osName.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (osName.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (osName.indexOf("nix") >= 0
                || osName.indexOf("nux") >= 0
                || osName.indexOf("aix") >= 0);
    }

    public static boolean isSolaris() {
        return (osName.indexOf("sunos") >= 0);
    }

    public static String getLookAndFeel() {
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
