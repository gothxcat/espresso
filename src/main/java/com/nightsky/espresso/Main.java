package com.nightsky.espresso;

public class Main 
{
    public static void main(String[] args)
    {
        System.setProperty("awt.useSystemAAFontSettings", "on");

        Resources.load();

        Window window = new Window();
        window.start();
    }
}
