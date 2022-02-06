package com.nightsky.espresso;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.nightsky.espresso.FileTable.FileTableDirectoryListener;
import com.nightsky.espresso.WindowActions.AbstractComponentActionListener;

public class Window extends JFrame
{
    protected JPanel mainPanel;
    protected JMenuBar menuBar;
    protected FileTable mainTable;
    protected JLabel detailsLabel;

    private WindowActions actions;
    private AbstractComponentActionListener closeActionListener;
    private AbstractComponentActionListener upActionListener;
    private AbstractComponentActionListener newDirectoryActionListener;
    private AbstractComponentActionListener reloadActionListener;
    private AbstractComponentActionListener toggleMenuActionListener;

    private File startDirectory;

    Window()
    {
        startDirectory = new File(".");

        actions = new WindowActions(this);
        closeActionListener = actions.new CloseActionListener();
        upActionListener = actions.new UpActionListener();
        newDirectoryActionListener = actions.new NewDirectoryActionListener();
        reloadActionListener = actions.new ReloadActionListener();
        toggleMenuActionListener = actions.new ToggleMenuActionListener();
    }

    public void start()
    {
        setAppearance();
        setJMenuBar(createMenuBar());
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
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(Resources.getString("MENU_FILE"));

        JMenuItem newDirectoryItem = new JMenuItem(Resources.getString("MENU_NEW"));
        newDirectoryItem.addActionListener(newDirectoryActionListener);
        newDirectoryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        fileMenu.add(newDirectoryItem);

        JMenuItem exitItem = new JMenuItem(Resources.getString("MENU_EXIT"));
        exitItem.addActionListener(closeActionListener);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(exitItem);

        JMenu goMenu = new JMenu(Resources.getString("MENU_GO"));

        JMenuItem upItem = new JMenuItem(Resources.getString("MENU_UP"));
        upItem.addActionListener(upActionListener);
        upItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK));
        goMenu.add(upItem);

        JMenu viewMenu = new JMenu(Resources.getString("MENU_VIEW"));

        JMenuItem reloadMenuItem = new JMenuItem(Resources.getString("MENU_RELOAD"));
        reloadMenuItem.addActionListener(reloadActionListener);
        reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        viewMenu.add(reloadMenuItem);

        JMenuItem toggleMenuItem = new JMenuItem(Resources.getString("MENU_TOGGLE_MENU"));
        toggleMenuItem.addActionListener(toggleMenuActionListener);
        toggleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        viewMenu.add(toggleMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(goMenu);
        menuBar.add(viewMenu);
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

        String currentPath;
        try {
            currentPath = startDirectory.getCanonicalPath();
        } catch (IOException e) {
            currentPath = startDirectory.getPath();
        }

        JTextField directoryField = new JTextField(currentPath);
        directoryField.setLayout(new BorderLayout());
        directoryField.setEditable(false);
        mainTable.registerDirectoryListener(new FileTableDirectoryListener(){
            @Override
            public void onDirectoryChanged(File directory)
            {
                detailsLabel.setText(FileManager.getDetails(directory));

                try {
                    directoryField.setText(directory.getCanonicalPath());
                } catch (IOException e) {
                    return;
                }
            }
        });

        JButton refreshButton = new JButton(Resources.getString("BUTTON_RELOAD"));
        refreshButton.addActionListener(reloadActionListener);
        refreshButton.addKeyListener(reloadActionListener);
        directoryField.add(refreshButton, BorderLayout.LINE_END);
        

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

        /* Table */
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 1.0;
        mainPanel.add(tableScrollPane, constraints);

        /* Details */
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0;
        mainPanel.add(detailsLabel, constraints);

        add(mainPanel);
    }

    public void newdir()
    {
        String filename = JOptionPane.showInputDialog(
            this,
            Resources.getString("DIALOG_NEW"),
            "Customized Dialog",
            JOptionPane.PLAIN_MESSAGE
        );

        File currentDirectory = mainTable.getdir();
        if (filename != null && currentDirectory != null) {
            File newDirectory = FileManager.newdir(currentDirectory, filename);
            if (newDirectory != null) {
                mainTable.updateContents();
            }
        }
    }
}
