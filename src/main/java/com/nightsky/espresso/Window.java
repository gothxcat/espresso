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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.nightsky.espresso.FileTable.FileTableDirectoryListener;
import com.nightsky.espresso.WindowActions.AbstractMultipleActionListener;

public class Window extends JFrame
{
    protected JPanel mainPanel;
    protected MenuBar menuBar;
    protected FileTable mainTable;
    protected JLabel detailsLabel;

    private WindowActions actions;
    public AbstractMultipleActionListener closeActionListener;
    public AbstractMultipleActionListener trashActionListener;
    public AbstractMultipleActionListener upActionListener;
    public AbstractMultipleActionListener newDirectoryActionListener;
    public AbstractMultipleActionListener reloadActionListener;
    public AbstractMultipleActionListener toggleMenuActionListener;
    private ActionListener directoryFieldActionListener;

    private File startDirectory;

    Window()
    {
        startDirectory = new File(".");
        actions = new WindowActions(this);
    }

    public void start()
    {
        setAppearance();

        setJMenuBar(createMenuBar());
        this.menuBar.trashItem.setEnabled(false);

        addComponents();

        pack();
        setTitle(Resources.getString("TITLE"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setAppearance()
    {
        try {
            UIManager.setLookAndFeel(Platform.getLookAndFeel());
        } catch (Exception e) {
            System.out.println(Resources.getString("EXCEPT_SYSTEM_APPEARANCE") + ":");
            System.out.println(e.getLocalizedMessage());
        }
    }

    private JMenuBar createMenuBar()
    {
        menuBar = new MenuBar(this);
        menuBar.setVisible(true);
        return menuBar;
    }

    private void addComponents()
    {
        /* Table */
        detailsLabel = new JLabel(FileManager.getDetails(startDirectory));
        mainTable = new FileTable(startDirectory);
        JScrollPane tableScrollPane = new JScrollPane(mainTable);


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
        addDirectoryListeners(mainTable, directoryField, detailsLabel);
        

        /* Panel */
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        
        /* Up button */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        mainPanel.add(upButton, constraints);

        /* Directory field */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 0.8;
        mainPanel.add(directoryField, constraints);

        /* Reload button */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 2;
        constraints.weightx = 0;
        mainPanel.add(reloadButton, constraints);

        /* Table */
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 1.0;
        mainPanel.add(tableScrollPane, constraints);

        /* Details */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0;
        mainPanel.add(detailsLabel, constraints);

        add(mainPanel);
    }

    public void newdir()
    {
        AbstractInputDialog dialog = new NewDirectoryDialog(this);
        int result = dialog.show();

        if (result == JOptionPane.OK_OPTION) {
            String filename = dialog.getText();
            File currentDirectory = mainTable.getdir();
            if (filename != null && currentDirectory != null) {
                File newDirectory = FileManager.newdir(currentDirectory, filename);
                if (newDirectory != null) {
                    mainTable.updateContents();
                }
            }
        }
    }

    private void addDirectoryListeners(FileTable table, JTextField directoryField, JLabel detailsLabel)
    {
        table.registerDirectoryListener(new FileTableDirectoryListener(){
            @Override
            public void onDirectoryChanged(File directory)
            {
                detailsLabel.setText(FileManager.getDetails(directory));
                setDirectoryFieldContents(directoryField, directory);
            }
        });

        directoryFieldActionListener = actions.new DirectoryFieldActionListener(directoryField);
        directoryField.addActionListener(directoryFieldActionListener);
    }

    public void setDirectoryFieldContents(JTextField field, File directory)
    {
        try {
            field.setText(directory.getCanonicalPath());
        } catch (IOException e) {
            field.setText(directory.getPath());
        }
    }
}
