package com.nightsky.espresso;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.nightsky.espresso.FileTable.FileTableDirectoryListener;

public class WindowActions {
    private Window window;

    public WindowActions(Window window) {
        this.window = window;
        addListeners();
    }

    private void addListeners() {
        this.window.closeActionListener = new CloseActionListener();
        this.window.DeleteActionListener = new DeleteActionListener();
        this.window.upActionListener = new UpActionListener();
        this.window.newDirectoryActionListener = new NewDirectoryActionListener();
        this.window.reloadActionListener = new ReloadActionListener();
        this.window.toggleMenuActionListener = new ToggleMenuActionListener();
    }

    public void addDirectoryListeners(FileTable table, JTextField directoryField, JLabel detailsLabel) {
        DirectoryListener directoryListener = new DirectoryListener(this.window, directoryField, detailsLabel);

        this.window.mainTable.registerDirectoryListener(directoryListener);
        directoryListener.onDirectoryChanged(this.window.mainTable.getdir());

        this.window.directoryFieldActionListener = new DirectoryFieldActionListener(directoryField);
        directoryField.addActionListener(this.window.directoryFieldActionListener);
    }

    private class DirectoryListener implements FileTableDirectoryListener {
        private JTextField directoryField;
        private JLabel detailsLabel;
        private DirectoryWatcherThread directoryWatcherThread;

        public DirectoryListener(Window window, JTextField directoryField, JLabel detailsLabel) {
            this.directoryField = directoryField;
            this.detailsLabel = detailsLabel;
        };

        @Override
        public void onDirectoryChanged(File directory)
        {
            detailsLabel.setText(FileManager.getDetailsString(directory));
            window.setDirectoryFieldContents(this.directoryField, directory);
            window.setTrashStatus(directory);

            try {
                WatchService directoryWatcher = FileSystems.getDefault().newWatchService();
                
                directory.toPath().register(directoryWatcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

                if (directoryWatcherThread != null) {
                    directoryWatcherThread.finish();
                }

                directoryWatcherThread = new DirectoryWatcherThread(directoryWatcher, window.mainTable);
                directoryWatcherThread.start();
            } catch (IOException e) {
                return;
            }
        }
    }

    public abstract class ActionKeyListener implements ActionListener, KeyListener {
        protected abstract void action();

        @Override
        public void actionPerformed(ActionEvent e) {
            this.action();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                this.action();
            }
        }

        @Override
        public void keyReleased(KeyEvent arg) {}

        @Override
        public void keyTyped(KeyEvent arg) {}
    }

    private final class DeleteActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            List<File> files = window.mainTable.getSelectedFiles();
            if (files.size() > 0) {
                if (FileManager.moveToTrash(files)) {
                    window.mainTable.updateContents();
                } else {
                    Object[] options = {
                        Resources.getString("OPTION_CANCEL"),
                        Resources.getString("OPTION_DELETE")
                    };

                    String prompt = Resources.getString("LABEL_TRASH_FAILED")
                        + " " + Resources.getString("LABEL_PROMPT_DELETE");

                    int dialogResult = JOptionPane.showOptionDialog(
                        window,
                        prompt,
                        Resources.getString("LABEL_INFO_DELETE"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[1]
                    );

                    if (dialogResult == JOptionPane.YES_OPTION) {
                        if (FileManager.delete(files)) {
                            window.mainTable.updateContents();
                        }
                    }
                }
            }
        }
    }

    private final class CloseActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            window.dispose();
        }
    }

    private final class UpActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            window.mainTable.updir();
        }
    }

    private final class NewDirectoryActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            window.newdir();
        }
    }

    private final class ReloadActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            window.mainTable.updateContents();
        }
    }

    private final class ToggleMenuActionListener extends ActionKeyListener {
        @Override
        protected void action() {
            if (window.menuBar.isVisible()) {
                window.menuBar.setVisible(false);
                window.mainPanel.requestFocus();
            } else {
                window.menuBar.setVisible(true);
                window.menuBar.requestFocus();
            }
        }
    }

    public final class DirectoryFieldActionListener implements ActionListener {
        private JTextField directoryField;

        DirectoryFieldActionListener(JTextField directoryField) {
            this.directoryField = directoryField;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            File file = new File(directoryField.getText());
            if (file.isDirectory()) {
                window.mainTable.chdir(file);
            } else {
                window.setDirectoryFieldContents(directoryField, window.mainTable.getdir());
            }
        }
    }

    private final class DirectoryWatcherThread extends Thread {
        private WatchService service;
        private FileTable table;
        private boolean isFinished;

        DirectoryWatcherThread(WatchService service, FileTable table) {
            this.service = service;
            this.table = table;
            this.isFinished = false;
        }

        @Override
        public void run() {
            try {
                WatchKey key;
                while ((key = this.service.take()) != null && !this.isFinished) {
                    Iterator<WatchEvent<?>> eventsIterator = key.pollEvents().iterator();
                    while (eventsIterator.hasNext()) {
                        this.table.updateContents();
                        eventsIterator.next();
                    }

                    key.reset();
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        public void finish() {
            this.isFinished = true;
        }
    }
}
