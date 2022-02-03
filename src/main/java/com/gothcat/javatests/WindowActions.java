package com.gothcat.javatests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class WindowActions
{
    private Window window;
    private InputMap mainInputMap;
    private ActionMap mainActionMap;

    WindowActions(Window window)
    {
        this.window = window;
    }

    public void addGlobalActions()
    {
        mainInputMap = window.mainPanel.getInputMap();
        mainActionMap = window.mainPanel.getActionMap();

        InputMap menuInputap = window.menuBar.getInputMap();
        ActionMap menuActionMap = window.menuBar.getActionMap();
        Action menuAction = new ToggleMenuAction();
        mainInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), "toggleMenu");
        mainActionMap.put("toggleMenu", menuAction);
        menuInputap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), "toggleMenu");
        menuActionMap.put("toggleMenu", menuAction);
    }

    private class ToggleMenuAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (window.menuBar.hasFocus()) {
                window.mainPanel.requestFocus();
            } else {
                window.menuBar.requestFocus();
            }
        }
    }

    public abstract class CloseActionListener implements ActionListener, KeyListener {}

    public CloseActionListener closeAction()
    {
        return (new CloseAction());
    }

    private class CloseAction extends CloseActionListener
    {
        private void action()
        {
            window.dispose();
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            action();
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                action();
        }

        @Override
        public void keyReleased(KeyEvent arg) {}

        @Override
        public void keyTyped(KeyEvent arg) {}
    }
}
