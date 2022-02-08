package com.nightsky.espresso;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public abstract class OptionDialog extends JDialog {
    protected JFrame parent;
    protected int selectedOption;
    protected JTextField inputField;

    public static int NULL_OPTION = -1;
    public static int OK_OPTION = JOptionPane.OK_OPTION;
    public static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

    public OptionDialog(JFrame parent) {
        this.parent = parent;
        this.selectedOption = OptionDialog.NULL_OPTION;
        this.inputField = new JTextField();
    }

    public int getSelectedOption() {
        return this.selectedOption;
    }

    public String getText() {
        return this.inputField.getText();
    }
}
