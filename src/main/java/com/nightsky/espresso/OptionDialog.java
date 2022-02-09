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
        this.setTitle(title);

        this.messageLabel = new JLabel(messageString);
        this.okButton = new JButton(okButtonString);
        this.cancelButton = new JButton(cancelButtonString);

        this.textField = new JTextField();
        this.textField.setCaretColor(Platform.caretColor);

        this.selectedOption = OptionDialog.UNININITIALIZED_VALUE;

        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.textField.addActionListener(this);
        this.addWindowListener(this);

        this.setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        this.add(this.messageLabel, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 3;
        this.add(this.textField, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        this.add(this.okButton, constraints);

        constraints.gridx = 2;
        this.add(this.cancelButton, constraints);
        
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    public void end() {
        if (this.isVisible()) {
            this.setVisible(false);
            this.dispose();
        }
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public String getText() {
        return this.text;
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
        if (source == this.okButton || source == this.textField) {
            this.selectedOption = OptionDialog.OK_OPTION;
            this.text = textField.getText();
            this.end();
        } else if (source == this.cancelButton) {
            this.selectedOption = OptionDialog.CANCEL_OPTION;
            this.end();
        }
    }

    @Override
    public void windowClosing(WindowEvent event) {
        this.end();
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
