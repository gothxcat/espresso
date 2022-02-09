package com.nightsky.espresso;

import javax.swing.JFrame;

public class NewDirectoryDialog extends OptionDialog {
    public NewDirectoryDialog(JFrame parent) {
        super(parent,
            Resources.getString("TITLE_NEW"),
            Resources.getString("LABEL_NEW"),
            Resources.getString("OPTION_CREATE"),
            Resources.getString("OPTION_CANCEL"));
    }
}
