package com.nightsky.espresso;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public abstract class OptionDialog extends JDialog implements ActionListener, WindowListener {
    protected JLabel messageLabel;
    protected JTextField textField;
    protected JButton okButton;
    protected JButton cancelButton;

    protected int selectedOption;
    protected String text;

    public static int UNININITIALIZED_VALUE = -1;
    public static int OK_OPTION = 0;
    public static int CANCEL_OPTION = 1;

    public OptionDialog(JFrame parent,
                        String title,
                        String messageString,
                        String okButtonString,
                        String cancelButtonString) {
        super(parent);
        setTitle(title);

        messageLabel = new JLabel(messageString);
        okButton = new JButton(okButtonString);
        cancelButton = new JButton(cancelButtonString);

        textField = new JTextField();
        textField.setCaretColor(Platform.caretColor);

        selectedOption = UNININITIALIZED_VALUE;

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        textField.addActionListener(this);
        addWindowListener(this);

        setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        add(messageLabel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        add(textField, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        add(okButton, constraints);

        constraints.gridx = 2;
        add(cancelButton, constraints);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public void end() {
        if (isVisible()) {
            setVisible(false);
            dispose();
        }
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean isModal() {
        return true;
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == okButton || source == textField) {
            selectedOption = OK_OPTION;
            text = textField.getText();
            end();
        } else if (source == cancelButton) {
            selectedOption = CANCEL_OPTION;
            end();
        }
    }

    @Override
    public void windowClosing(WindowEvent event) {
        end();
    }

    @Override
    public void windowActivated(WindowEvent event) {}

    @Override
    public void windowClosed(WindowEvent event) {}

    @Override
    public void windowDeactivated(WindowEvent event) {}

    @Override
    public void windowDeiconified(WindowEvent event) {}

    @Override
    public void windowIconified(WindowEvent event) {}

    @Override
    public void windowOpened(WindowEvent event) {}
}
