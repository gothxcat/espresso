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
// import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.nightsky.espresso.WindowActions.ActionKeyListener;

public class Window extends JFrame {
    protected JPanel mainPanel;
    protected JPanel sidePanel;
    protected MenuBar menuBar;
    protected FileTable mainTable;
    protected JLabel detailsLabel;

    private WindowActions actions;
    public ActionKeyListener closeActionListener;
    public ActionKeyListener DeleteActionListener;
    public ActionKeyListener upActionListener;
    public ActionKeyListener newDirectoryActionListener;
    public ActionKeyListener reloadActionListener;
    public ActionListener directoryFieldActionListener;

    private File startDirectory;

    public Window() {
        startDirectory = Platform.userHomeDirectory;
        actions = new WindowActions(this);
    }

    public void start() {
        setAppearance();

        setJMenuBar(createMenuBar());
        menuBar.trashItem.setEnabled(false);

        addComponents();

        pack();
        setTitle(Resources.getString("TITLE"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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
        menuBar = new MenuBar(this);
        menuBar.setVisible(true);
        return menuBar;
    }

    private void addComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);


        /* Table */
        detailsLabel = new JLabel(FileManager.getDetailsString(startDirectory));
        detailsLabel.setHorizontalAlignment(JLabel.RIGHT);
        mainTable = new FileTable(this, startDirectory);
        JScrollPane tableScrollPane = new JScrollPane(mainTable);

        
        /* Side panel */

        // // TODO: directory tree
        // sidePanel = new JPanel();
        // sidePanel.setLayout(new GridBagLayout());
        // constraints.fill = GridBagConstraints.BOTH;
        // constraints.weighty = 1.0;
        // constraints.weightx = 1.0;
        // constraints.gridy = 0;
        // constraints.gridx = 0;
        // constraints.anchor = GridBagConstraints.PAGE_START;
        // // sidePanel.add(directoryTree, constraints);
        // JScrollPane sideScrollPane = new JScrollPane(sidePanel);

        
        // /* Split side/table panels */
        // JSplitPane filePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideScrollPane, tableScrollPane);
        // filePane.setDividerLocation(250);

        JScrollPane filePane = tableScrollPane;


        /* Controls */
        JButton upButton = new JButton(Resources.getString("BUTTON_UP"));
        upButton.addActionListener(upActionListener);
        upButton.addKeyListener(upActionListener);

        JTextField directoryField = new JTextField();
        setDirectoryFieldContents(directoryField, startDirectory);
        directoryField.setCaretColor(Platform.caretColor);

        JButton reloadButton = new JButton(Resources.getString("BUTTON_RELOAD"));
        reloadButton.addActionListener(reloadActionListener);
        reloadButton.addKeyListener(reloadActionListener);


        /* Listeners */
        actions.addDirectoryListeners(mainTable, directoryField, detailsLabel);
        

        /* Panel */
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        
        /* Up button */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        mainPanel.add(upButton, constraints);

        /* Directory field */
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 0.8;
        mainPanel.add(directoryField, constraints);

        /* Reload button */
        constraints.gridy = 0;
        constraints.gridx = 2;
        constraints.weightx = 0;
        mainPanel.add(reloadButton, constraints);

        /* File pane */
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 4;
        constraints.weighty = 1.0;
        mainPanel.add(filePane, constraints);

        /* Details */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.weighty = 0;
        mainPanel.add(detailsLabel, constraints);

        add(mainPanel);
    }

    public void newdir() {
        OptionDialog dialog = new NewDirectoryDialog(this);
        dialog.setVisible(true);

        int result = dialog.getSelectedOption();

        if (result == OptionDialog.OK_OPTION) {
            String filename = dialog.getText();
            File currentDirectory = mainTable.getcwd();

            if (filename != null && currentDirectory != null) {
                File newDirectory = FileManager.newdir(currentDirectory, filename);
                if (newDirectory != null) {
                    mainTable.updateContents();
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
                menuBar.setTrashItemToDelete();
            } else {
                menuBar.setTrashItemToMove();
            }
        } else {
            if (FileManager.getTrashFilesPath().equals(directory.getAbsolutePath())) {
                menuBar.setTrashItemToDelete();
            } else {
                menuBar.setTrashItemToMove();
            }
        }
    }
}
