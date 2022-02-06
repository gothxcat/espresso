package com.nightsky.espresso;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WindowActions
{
    private Window window;

    WindowActions(Window window)
    {
        this.window = window;
    }

    public abstract class AbstractComponentActionListener implements ActionListener, KeyListener {}

    private class ComponentActionListener extends AbstractComponentActionListener
    {
        protected void action() {}

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

    public final class CloseActionListener extends ComponentActionListener
    {
        @Override
        protected void action()
        {
            window.dispose();
        }
    }

    public final class UpActionListener extends ComponentActionListener
    {
        @Override
        protected void action()
        {
            window.mainTable.updir();
        }
    }

    public final class NewDirectoryActionListener extends ComponentActionListener
    {
        @Override
        protected void action()
        {
            window.newdir();
        }
    }

    public final class ReloadActionListener extends ComponentActionListener
    {
        @Override
        protected void action()
        {
            window.mainTable.updateContents();
        }
    }

    public final class ToggleMenuActionListener extends ComponentActionListener
    {
        @Override
        protected void action()
        {
            if (window.menuBar.isVisible()) {
                window.menuBar.setVisible(false);
                window.mainPanel.requestFocus();
            } else {
                window.menuBar.setVisible(true);
                window.menuBar.requestFocus();
            }
        }
    }
}
