package com.nightsky.espresso;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.nightsky.espresso.WindowActions.ActionKeyListener;

public class Window extends JFrame {
    protected JPanel mainPanel;
    protected MenuBar menuBar;
    protected FileTable mainTable;
    protected JLabel detailsLabel;

    private WindowActions actions;
    public ActionKeyListener closeActionListener;
    public ActionKeyListener DeleteActionListener;
    public ActionKeyListener upActionListener;
    public ActionKeyListener newDirectoryActionListener;
    public ActionKeyListener reloadActionListener;
    public ActionKeyListener toggleMenuActionListener;
    public ActionListener directoryFieldActionListener;

    private File startDirectory;

    public Window() {
        this.startDirectory = new File(".");
        this.actions = new WindowActions(this);
    }

    public void start() {
        this.setAppearance();

        this.setJMenuBar(createMenuBar());
        this.menuBar.trashItem.setEnabled(false);

        this.addComponents();

        this.pack();
        this.setTitle(Resources.getString("TITLE"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void setAppearance() {
        try {
            UIManager.setLookAndFeel(Platform.getLookAndFeel());
        } catch (Exception e) {
            System.out.println(Resources.getString("EXCEPT_SYSTEM_APPEARANCE") + ":");
            System.out.println(e.getLocalizedMessage());
        }
    }

    private JMenuBar createMenuBar() {
        this.menuBar = new MenuBar(this);
        this.menuBar.setVisible(true);
        return this.menuBar;
    }

    private void addComponents() {
        /* Table */
        this.detailsLabel = new JLabel(FileManager.getDetailsString(this.startDirectory));
        this.mainTable = new FileTable(this, this.startDirectory);
        JScrollPane tableScrollPane = new JScrollPane(this.mainTable);


        /* Controls */
        JButton upButton = new JButton(Resources.getString("BUTTON_UP"));
        upButton.addActionListener(this.upActionListener);
        upButton.addKeyListener(this.upActionListener);

        JTextField directoryField = new JTextField();
        setDirectoryFieldContents(directoryField, this.startDirectory);
        directoryField.setCaretColor(Platform.caretColor);

        JButton reloadButton = new JButton(Resources.getString("BUTTON_RELOAD"));
        reloadButton.addActionListener(this.reloadActionListener);
        reloadButton.addKeyListener(this.reloadActionListener);


        /* Listeners */
        this.actions.addDirectoryListeners(this.mainTable, directoryField, this.detailsLabel);
        

        /* Panel */
        this.mainPanel = new JPanel(new GridBagLayout());
        this.mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        
        /* Up button */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        this.mainPanel.add(upButton, constraints);

        /* Directory field */
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 0.8;
        this.mainPanel.add(directoryField, constraints);

        /* Reload button */
        constraints.gridy = 0;
        constraints.gridx = 2;
        constraints.weightx = 0;
        this.mainPanel.add(reloadButton, constraints);

        /* Table */
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 1.0;
        this.mainPanel.add(tableScrollPane, constraints);

        /* Details */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0;
        this.mainPanel.add(this.detailsLabel, constraints);

        this.add(this.mainPanel);
    }

    public void newdir() {
        OptionDialog dialog = new NewDirectoryDialog(this);
        dialog.setVisible(true);

        int result = dialog.getSelectedOption();

        if (result == OptionDialog.OK_OPTION) {
            String filename = dialog.getText();
            File currentDirectory = this.mainTable.getdir();

            if (filename != null && currentDirectory != null) {
                File newDirectory = FileManager.newdir(currentDirectory, filename);
                if (newDirectory != null) {
                    this.mainTable.updateContents();
                }
            }
        }
    }

    public void setDirectoryFieldContents(JTextField field, File directory) {
        try {
            field.setText(directory.getCanonicalPath());
        } catch (IOException e) {
            field.setText(directory.getPath());
        }
    }

    public void setTrashStatus(File directory) {
        if (Platform.isWindows()) {
            if (FileManager.getTrashFilesPath().equals(directory.getName())) {
                this.menuBar.setTrashItemToDelete();
            } else {
                this.menuBar.setTrashItemToMove();
            }
        } else {
            if (FileManager.getTrashFilesPath().equals(directory.getAbsolutePath())) {
                this.menuBar.setTrashItemToDelete();
            } else {
                this.menuBar.setTrashItemToMove();
            }
        }
    }
}
