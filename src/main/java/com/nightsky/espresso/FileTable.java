package com.nightsky.espresso;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.nightsky.espresso.FileManager.FileManagerAttribute;
import com.nightsky.espresso.FileManager.FileManagerObject;

public class FileTable extends JTable implements MouseListener, KeyListener {
    private Window window;

    private FileTableModel model;

    private File directory;
    private List<FileManagerObject> contents;

    private List<FileManagerAttribute> fileManagerAttributes;
    private FileManagerAttribute sortAttribute;
    private List<String> tableAttributes;

    private List<FileTableDirectoryListener> directoryChangedListeners;

    public FileTable(Window window) {
        this.window = window;

        fileManagerAttributes = new ArrayList<FileManagerAttribute>(List.of(
            FileManagerAttribute.filename,
            FileManagerAttribute.size,
            FileManagerAttribute.type,
            FileManagerAttribute.dateModified
        ));

        sortAttribute = FileManagerAttribute.filename;

        tableAttributes = new ArrayList<String>(List.of(
            Resources.getString("TABLE_CELL_NAME"),
            Resources.getString("TABLE_CELL_SIZE"),
            Resources.getString("TABLE_CELL_TYPE"),
            Resources.getString("TABLE_CELL_DATEMODIFIED")
        ));

        directoryChangedListeners = new ArrayList<>();

        model = new FileTableModel();
        model.updateColumnsFromList(tableAttributes);
        setModel(model);
        addListeners();
    }

    public FileTable(Window window, File directory) {
        this(window);
        chdir(directory);
    }

    private void addListeners() {
        addMouseListener(this);
        addKeyListener(this);
        getSelectionModel().addListSelectionListener(new FileSelectionListener());
    }

    public File getcwd() {
        return directory;
    }

    public void chdir(File newDirectory) {
        try {
            directory = new File(newDirectory.getCanonicalPath());
        } catch (IOException e) {
            return;
        }

        updateContents();
    }

    public void updir() {
        if (directory != null) {
            File parent = directory.getParentFile();
            if (parent != null)
            chdir(parent);
        }
    }

    public void updateContents() {
        if (directory != null) {
            contents = FileManager.listDirectory(directory, fileManagerAttributes);
            int sortColumn = fileManagerAttributes.indexOf(sortAttribute);

            Collections.sort(contents, new Comparator<FileManagerObject>() {
                public int compare(FileManagerObject a, FileManagerObject b) {
                    String[] attributesA = a.getAttributeStrings();
                    String[] attributesB = b.getAttributeStrings();
                    if (attributesA.length >= sortColumn && attributesB.length >= sortColumn) {
                        return attributesA[sortColumn].compareTo(attributesB[sortColumn]);
                    }

                    return 0;
                }
            });

            clearSelection();
            model.updateDataFromList(contents);

            for (FileTableDirectoryListener listener : directoryChangedListeners) {
                listener.onDirectoryChanged(directory);
            }
        }
    }

    public File getFileAt(int row) {
        if (row > -1 && row < contents.size()) {
            return contents.get(row).file;
        }

        return null;
    }

    public List<File> getSelectedFiles() {
        List<File> files = new ArrayList<>();
        for (int row : getSelectedRows()) {
            files.add(getFileAt(row));
        }

        return files;
    }

    public void addDirectoryChangedListener(FileTableDirectoryListener listener) {
        directoryChangedListeners.add(listener);
    }

    public void removeDirectoryChangedListener(FileTableDirectoryListener listener) {
        directoryChangedListeners.remove(listener);
    }

    public interface FileTableDirectoryListener {
        public void onDirectoryChanged(File directory);
    }

    private class FileSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {
            window.menuBar.trashItem.setEnabled(getSelectedRow() != -1);
        }
    }

    private class FileTableModel extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;

        private void updateColumns(String[] columnNames) {
            this.columnNames = columnNames;
            fireTableStructureChanged();
        }
    
        private void updateData(Object[][] data) {
            this.data = data;
            fireTableDataChanged();
        }

        private void updateColumnsFromList(List<String> objects) {
            updateColumns(objects.toArray(new String[0]));
        }
    
        private void updateDataFromList(List<FileManagerObject> objects) {
            List<String[]> dataList = new ArrayList<String[]>();
            for (FileManagerObject object : objects) {
                dataList.add(object.getAttributeStrings());
            }
    
            updateData(dataList.toArray(new Object[0][]));
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
    
        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return Object.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        Point point = event.getPoint();
        int row = rowAtPoint(point);
        if (event.getClickCount() % 2 == 0 && row != -1) {
            File file = getFileAt(row);
            if (file.isDirectory()) {
                chdir(file);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {};

    @Override
    public void mouseEntered(MouseEvent event) {};

    @Override
    public void mouseExited(MouseEvent event) {};

    @Override
    public void mouseReleased(MouseEvent event) {};

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                int row = getSelectedRow();
                if (row != -1) {
                    File file = getFileAt(row);
                    if (file.isDirectory()) {
                        chdir(file);
                    }
                }
                break;
        }
    };

    @Override
    public void keyReleased(KeyEvent event) {};

    @Override
    public void keyTyped(KeyEvent event) {};
}
