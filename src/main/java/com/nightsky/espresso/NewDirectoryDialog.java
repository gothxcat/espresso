package com.nightsky.espresso;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class NewDirectoryDialog extends AbstractInputDialog
{
    NewDirectoryDialog(Component rootPane)
    {
        setRootPane(rootPane);
        setTitle(Resources.getString("TITLE_NEW"));
        setOptions(new String[]{
            Resources.getString("OPTION_CREATE"),
            Resources.getString("OPTION_CANCEL")
        });
        setDefaultOptionIndex(0);
        setOptionType(JOptionPane.OK_CANCEL_OPTION);
        setMessageType(JOptionPane.PLAIN_MESSAGE);

        initComponents();

        addComponent(new JLabel(Resources.getString("LABEL_NEW")));

        setInputField(new JTextField());
        inputField.setCaretColor(Platform.caretColor);
        addComponent(inputField);
    }
}
