package com.nightsky.espresso;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JTextField;

public class WindowActions
{
    private Window window;

    WindowActions(Window window)
    {
        this.window = window;
        addListeners();
    }

    private void addListeners()
    {
        window.closeActionListener = new CloseActionListener();
        window.trashActionListener = new TrashActionListener();
        window.upActionListener = new UpActionListener();
        window.newDirectoryActionListener = new NewDirectoryActionListener();
        window.reloadActionListener = new ReloadActionListener();
        window.toggleMenuActionListener = new ToggleMenuActionListener();
    }

    public abstract class AbstractMultipleActionListener implements ActionListener, KeyListener {}

    private class MultipleActionListener extends AbstractMultipleActionListener
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

    private final class TrashActionListener extends MultipleActionListener
    {

    }

    private final class CloseActionListener extends MultipleActionListener
    {
        @Override
        protected void action()
        {
            window.dispose();
        }
    }

    private final class UpActionListener extends MultipleActionListener
    {
        @Override
        protected void action()
        {
            window.mainTable.updir();
        }
    }

    private final class NewDirectoryActionListener extends MultipleActionListener
    {
        @Override
        protected void action()
        {
            window.newdir();
        }
    }

    private final class ReloadActionListener extends MultipleActionListener
    {
        @Override
        protected void action()
        {
            window.mainTable.updateContents();
        }
    }

    private final class ToggleMenuActionListener extends MultipleActionListener
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

    public final class DirectoryFieldActionListener implements ActionListener
    {
        private JTextField directoryField;

        DirectoryFieldActionListener(JTextField directoryField)
        {
            this.directoryField = directoryField;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            File file = new File(directoryField.getText());
            if (file.isDirectory()) {
                window.mainTable.chdir(file);
            } else {
                window.setDirectoryFieldContents(directoryField, window.mainTable.getdir());
            }
        }
    }
}
