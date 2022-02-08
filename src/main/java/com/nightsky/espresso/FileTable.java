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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.nightsky.espresso.FileManager.FileManagerAttribute;
import com.nightsky.espresso.FileManager.FileManagerObject;

public class FileTable extends JTable implements TableModel, MouseListener, KeyListener {
    private Window window;

    private File directory;
    private List<FileManagerObject> contents;

    private List<FileManagerAttribute> fileManagerAttributes;
    private FileManagerAttribute sortAttribute;
    private List<String> tableAttributes;

    private FileTableDirectoryListener directoryListener;

    private String[] modelColumnNames;
    private Object[][] modelData;

    public FileTable(Window window) {
        this.window = window;

        this.fileManagerAttributes = new ArrayList<FileManagerAttribute>(List.of(
            FileManagerAttribute.filename,
            FileManagerAttribute.size,
            FileManagerAttribute.type,
            FileManagerAttribute.dateModified
        ));

        this.sortAttribute = FileManagerAttribute.filename;

        this.tableAttributes = new ArrayList<String>(List.of(
            Resources.getString("TABLE_CELL_NAME"),
            Resources.getString("TABLE_CELL_SIZE"),
            Resources.getString("TABLE_CELL_TYPE"),
            Resources.getString("TABLE_CELL_DATEMODIFIED")
        ));

        this.updateModelColumnsFromList(this.tableAttributes);
        this.setModel(this);
        this.addListeners();
    }

    public FileTable(Window window, File directory) {
        this(window);
        this.chdir(directory);
    }

    private void addListeners() {
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.getSelectionModel().addListSelectionListener(new FileSelectionListener());
    }

    public File getdir() {
        return this.directory;
    }

    public void chdir(File newDirectory) {
        try {
            this.directory = new File(newDirectory.getCanonicalPath());
        } catch (IOException e) {
            return;
        }

        this.updateContents();
    }

    public void updir() {
        if (this.directory != null) {
            File parent = this.directory.getParentFile();
            if (parent != null)
            this.chdir(parent);
        }
    }

    public void updateContents() {
        if (this.directory != null) {
            this.contents = FileManager.listDirectory(this.directory, this.fileManagerAttributes);
            int sortColumn = this.fileManagerAttributes.indexOf(this.sortAttribute);

            Collections.sort(this.contents, new Comparator<FileManagerObject>() {
                public int compare(FileManagerObject a, FileManagerObject b) {
                    String[] attributesA = a.getAttributeStrings();
                    String[] attributesB = b.getAttributeStrings();
                    if (attributesA.length >= sortColumn && attributesB.length >= sortColumn) {
                        return attributesA[sortColumn].compareTo(attributesB[sortColumn]);
                    }

                    return 0;
                }
            });

            this.updateModelDataFromList(this.contents);

            if (this.directoryListener != null) {
                this.directoryListener.onDirectoryChanged(this.directory);
            }
        }

        this.clearSelection();
        this.revalidate();
    }

    public File getFileAt(int row) {
        if (row > -1 && row < this.contents.size()) {
            return this.contents.get(row).file;
        }

        return null;
    }

    public List<File> getSelectedFiles() {
        List<File> files = new ArrayList<>();
        for (int row : this.getSelectedRows()) {
            files.add(getFileAt(row));
        }

        return files;
    }

    @Override
    public boolean editCellAt(int row, int column, java.util.EventObject event) {
        return false;
    }

    private void updateModelColumns(String[] columnNames) {
        this.modelColumnNames = columnNames;
    }

    private void updateModelData(Object[][] data) {
        this.modelData = data;
    }

    private void updateModelColumnsFromList(List<String> objects) {
        this.updateModelColumns(objects.toArray(new String[0]));
    }

    private void updateModelDataFromList(List<FileManagerObject> objects) {
        List<String[]> dataList = new ArrayList<String[]>();
        for (FileManagerObject object : objects) {
            dataList.add(object.getAttributeStrings());
        }

        this.updateModelData(dataList.toArray(new Object[0][]));
    }

    @Override
    public int getColumnCount() {
        return this.modelColumnNames.length;
    }

    @Override
    public int getRowCount() {
        return this.modelData.length;
    }

    @Override
    public String getColumnName(int col) {
        return this.modelColumnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return this.modelData[row][col];
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {}

    @Override
    public void removeTableModelListener(TableModelListener listener) {}

    public void registerDirectoryListener(FileTableDirectoryListener listener) {
        this.directoryListener = listener;
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

    @Override
    public void mousePressed(MouseEvent event) {
        Point point = event.getPoint();
        int row = this.rowAtPoint(point);
        if (event.getClickCount() % 2 == 0 && row != -1) {
            File file = this.getFileAt(row);
            if (file.isDirectory()) {
                this.chdir(file);
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
