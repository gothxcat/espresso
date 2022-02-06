package com.nightsky.espresso;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public abstract class AbstractInputDialog
{
    protected Component rootPane;
    protected String title;
    protected String[] options;
    protected int defaultOptionIndex;
    protected int optionType;
    protected int messageType;
    protected List<Component> components;
    protected JTextField inputField;

    protected void setRootPane(Component rootPane)
    {
        this.rootPane = rootPane;
    }

    protected void setTitle(String title)
    {
        this.title = title;
    }

    protected void setOptions(String[] options)
    {
        this.options = options;
    }

    protected void setDefaultOptionIndex(int index)
    {
        this.defaultOptionIndex = index;
    }

    protected void setOptionType(int type)
    {
        this.optionType = type;
    }

    protected void setMessageType(int type)
    {
        this.messageType = type;
    }

    protected void initComponents()
    {
        components = new ArrayList<>();
    }

    protected void addComponent(Component component)
    {
        this.components.add(component);
    }

    protected void setInputField(JTextField field)
    {
        this.inputField = field;
    }

    public int show()
    {
        return JOptionPane.showOptionDialog(
            rootPane,
            components.toArray(),
            title,
            optionType,
            messageType,
            null,
            options,
            inputField
        );
    }

    public String getText()
    {
        return inputField.getText();
    }
}
