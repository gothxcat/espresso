package com.gothcat.javatests;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.gothcat.javatests.WindowActions.CloseActionListener;

public class Window extends JFrame
{
    private WindowActions actions;
    private CloseActionListener closeAction;
    protected JPanel mainPanel;
    protected JMenuBar menuBar;

    Window()
    {
        actions = new WindowActions(this);
        closeAction = actions.closeAction();
    }

    public void start()
    {
        setAppearance();
        addWidgets();
        actions.addGlobalActions();

        pack();
        setTitle(Resources.TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setAppearance()
    {
        try {
            UIManager.setLookAndFeel(Platform.getLookAndFeel());
        } catch (Exception e) {
            System.out.println("Unable to use system appearance:");
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void addWidgets()
    {
        setJMenuBar(createMenuBar());

        JComponent[] widgets = {
            createLabel(),
            createControls()
        };

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        for (JComponent widget : widgets) {
            mainPanel.add(widget);
        }

        add(mainPanel);
    }

    private JMenuBar createMenuBar()
    {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(Resources.MENU_FILE);
        JMenuItem exitItem = new JMenuItem(Resources.MENU_EXIT);
        
        exitItem.addActionListener(closeAction);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        return (menuBar);
    }

    private JComponent createLabel()
    {
        JPanel labelPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(Resources.LABEL_MESSAGE);
        label.setHorizontalAlignment(JLabel.CENTER);
        labelPanel.add(label);

        return (labelPanel);
    }

    private JComponent createControls()
    {
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton(Resources.BUTTON_OK);
        okButton.addActionListener(closeAction);
        okButton.addKeyListener(closeAction);
        controlPanel.add(okButton);

        return (controlPanel);
    }
}
