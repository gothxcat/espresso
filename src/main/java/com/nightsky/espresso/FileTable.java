package com.nightsky.espresso;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nightsky.espresso.FileManager.FileManagerAttribute;
import com.nightsky.espresso.FileManager.FileManagerObject;

public class FileTable extends JTable {
    private FileTableModel model;
    private File directory;
    private List<FileManagerObject> contents;

    private List<FileManagerAttribute> fileManagerAttributes;
    private FileManagerAttribute sortAttribute = FileManagerAttribute.filename;
    private List<String> tableAttributes;

    private FileTableDirectoryListener directoryListener;

    FileTable()
    {
        fileManagerAttributes = new ArrayList<FileManagerAttribute>(List.of(
            FileManagerAttribute.filename,
            FileManagerAttribute.size,
            FileManagerAttribute.type,
            FileManagerAttribute.dateModified
        ));

        tableAttributes = new ArrayList<String>(List.of(
            Resources.getString("TABLE_CELL_NAME"),
            Resources.getString("TABLE_CELL_SIZE"),
            Resources.getString("TABLE_CELL_TYPE"),
            Resources.getString("TABLE_CELL_DATEMODIFIED")
        ));

        model = new FileTableModel(tableAttributes.toArray(new String[0]));
        setModel(model);
        addMouseListener(new DoubleClickListener(this));
    }

    FileTable(File directory)
    {
        this();
        chdir(directory);
    }

    public File getdir()
    {
        return directory;
    }

    public void chdir(File directory)
    {
        try {
            this.directory = new File(directory.getCanonicalPath());
        } catch (IOException e) {
            return;
        }

        updateContents();
    }

    public void updir()
    {
        if (directory != null) {
            File parent = directory.getParentFile();
            if (parent != null)
                chdir(parent);
        }
    }

    public void updateContents()
    {
        if (directory != null) {
            contents = FileManager.listDirectory(directory, fileManagerAttributes);
            int sortColumn = fileManagerAttributes.indexOf(sortAttribute);

            Collections.sort(contents, new Comparator<FileManagerObject>() {
                public int compare(FileManagerObject a, FileManagerObject b)
                {
                    String[] attributesA = a.getAttributeStrings();
                    String[] attributesB = b.getAttributeStrings();
                    if (attributesA.length >= sortColumn && attributesB.length >= sortColumn)
                        return attributesA[sortColumn].compareTo(attributesB[sortColumn]);
                    return 0;
                }
            });

            model.updateDataFromList(contents);

            if (directoryListener != null)
                directoryListener.onDirectoryChanged(directory);
        }

        clearSelection();
        revalidate();
    }

    public File getFileAt(int row)
    {
        if (row > -1 && row < contents.size())
            return contents.get(row).file;
        return null;
    }

    @Override
    public boolean editCellAt(int row, int column, java.util.EventObject event) {
        return false;
    }

    private class FileTableModel extends AbstractTableModel
    {
        private String[] columnNames;
        private Object[][] data;

        FileTableModel(String[] columnNames)
        {
            updateColumns(columnNames);
            updateData(new Object[0][]);
        }

        public void updateColumns(String[] columnNames)
        {
            this.columnNames = columnNames;
        }

        public void updateData(Object[][] data)
        {
            this.data = data;
        }

        public void updateDataFromList(List<FileManagerObject> objects)
        {
            List<String[]> dataList = new ArrayList<String[]>();
            for (FileManagerObject object : objects)
                dataList.add(object.getAttributeStrings());

            updateData(dataList.toArray(new Object[0][]));
        }

        @Override
        public int getColumnCount()
        {
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
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
    }

    private class DoubleClickListener extends MouseAdapter
    {
        private FileTable table;

        DoubleClickListener(FileTable table)
        {
            super();
            this.table = table;
        }

        @Override
        public void mousePressed(MouseEvent event)
        {
            Point point = event.getPoint();
            int row = table.rowAtPoint(point);
            if (event.getClickCount() % 2 == 0 && row != -1) {
                File file = table.getFileAt(row);
                if (file.isDirectory())
                    table.chdir(file);
            }
        }
    }

    public void registerDirectoryListener(FileTableDirectoryListener listener)
    {
        directoryListener = listener;
    }

    public interface FileTableDirectoryListener
    {
        public void onDirectoryChanged(File directory);
    }
}
