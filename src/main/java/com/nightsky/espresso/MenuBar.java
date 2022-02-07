package com.nightsky.espresso;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBar extends JMenuBar {
    private Window window;

    public JMenu fileMenu;
    public JMenuItem newDirectoryItem;
    public JMenuItem exitItem;

    public JMenu editMenu;
    public JMenuItem trashItem;

    public JMenu goMenu;
    public JMenuItem upItem;

    public JMenu viewMenu;
    public JMenuItem reloadMenuItem;
    public JMenuItem toggleMenuItem;

    MenuBar(Window window)
    {
        this.window = window;
        createMenus();
        addListeners();
        setAccelerators();
    }

    private void createMenus()
    {
        /* File menu */
        fileMenu = new JMenu(Resources.getString("MENU_FILE"));
        newDirectoryItem = new JMenuItem(Resources.getString("MENU_NEW"));
        exitItem = new JMenuItem(Resources.getString("MENU_EXIT"));

        fileMenu.add(newDirectoryItem);
        fileMenu.add(exitItem);

        /* Edit menu */
        editMenu = new JMenu(Resources.getString("MENU_EDIT"));
        trashItem = new JMenuItem(Resources.getString("MENU_TRASH"));
        editMenu.add(trashItem);

        /* Go menu */
        goMenu = new JMenu(Resources.getString("MENU_GO"));
        upItem = new JMenuItem(Resources.getString("MENU_UP"));

        goMenu.add(upItem);

        /* View menu */
        viewMenu = new JMenu(Resources.getString("MENU_VIEW"));
        reloadMenuItem = new JMenuItem(Resources.getString("MENU_RELOAD"));
        toggleMenuItem = new JMenuItem(Resources.getString("MENU_TOGGLE_MENU"));
        
        viewMenu.add(reloadMenuItem);
        viewMenu.add(toggleMenuItem);


        add(fileMenu);
        add(editMenu);
        add(goMenu);
        add(viewMenu);
    }

    private void addListeners()
    {
        newDirectoryItem.addActionListener(window.newDirectoryActionListener);
        exitItem.addActionListener(window.closeActionListener);
        trashItem.addActionListener(window.DeleteActionListener);
        upItem.addActionListener(window.upActionListener);
        reloadMenuItem.addActionListener(window.reloadActionListener);
        toggleMenuItem.addActionListener(window.toggleMenuActionListener);
    }

    private void setAccelerators()
    {
        newDirectoryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        trashItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
        upItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK));
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        toggleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
    }

    public void setTrashItemToMove()
    {
        trashItem.setText(Resources.getString("MENU_TRASH"));
    }

    public void setTrashItemToDelete()
    {
        trashItem.setText(Resources.getString("MENU_DELETE"));
    }
}
