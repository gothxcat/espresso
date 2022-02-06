package com.nightsky.espresso;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    public static List<FileManagerObject> listDirectory(File directory, List<FileManagerAttribute> attributes)
    {
        List<FileManagerObject> fileList = new ArrayList<FileManagerObject>();
        String[] filenames = directory.list();
        for (String filename : filenames) {
            File file = new File(Paths.get(directory.getPath(), filename).toAbsolutePath().toString());
            FileManagerObject fileObject = new FileManagerObject(file, attributes.toArray(new FileManagerAttribute[0]));

            for (FileManagerAttribute attribute : attributes) {
                switch (attribute) {
                    case filename:
                        fileObject.setAttribute(FileManagerAttribute.filename,
                            filename
                        );
                        break;
                    case size:
                        fileObject.setAttribute(FileManagerAttribute.size,
                            formatSize(file.length())
                        );
                        break;
                    case type:
                        fileObject.setAttribute(FileManagerAttribute.type,
                            getFileTypeString(file)
                        );
                        break;
                    case dateModified:
                        ZonedDateTime zoneDateModified = zoneDateFromEpoch(file.lastModified());
                        fileObject.setAttribute(FileManagerAttribute.dateModified,
                            zoneDateModified.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
                        );
                        break;
                }
            }

            fileList.add(fileObject);
        }
        
        return fileList;
    }

    public static String formatSize(long size)
    {
        StringBuilder string = new StringBuilder(Long.toString(size));
        for (int i = string.length() - 3; i > 0; i -= 3)
            string.insert(i, ',');
        
        return string.toString();
    }

    public static ZonedDateTime zoneDateFromEpoch(long msSinceEpoch)
    {
        return Instant.ofEpochMilli(msSinceEpoch).atZone(Platform.zoneId);
    }

    private static String getFileTypeString(File file)
    {
        return file.isDirectory() ? Resources.getString("TABLE_CELL_DIRECTORY") : Resources.getString("TABLE_CELL_FILE");
    }

    public static File newdir(File currentDirectory, String filename)
    {
        File newDirectory = new File(Paths.get(currentDirectory.getPath(), filename).toAbsolutePath().toString());
        if (newDirectory.mkdir())
            return newDirectory;
        else
            return null;
    }

    public static String getDetails(File directory)
    {
        long size = 0;
        int directoryCount = 0;
        int fileCount = 0;
        for (File file : directory.listFiles()) {
            size += file.length();
            if (file.isDirectory())
                directoryCount++;
            else
                fileCount++;
        }

        String directoryString = "";
        if (directoryCount == 1)
            directoryString = Integer.toString(directoryCount) + " " + Resources.getString("LABEL_DIRECTORY_SINGLE");
        else if (directoryCount > 0)
            directoryString = Integer.toString(directoryCount) + " " + Resources.getString("LABEL_DIRECTORY_MULTIPLE");

        String fileString = "";
        if (fileCount == 1)
            fileString = Integer.toString(fileCount) + " " + Resources.getString("LABEL_FILE_SINGLE");
        else if (fileCount > 0)
            fileString = Integer.toString(fileCount) + " " + Resources.getString("LABEL_FILE_MULTIPLE");

        String sizeString = "";
        if (size > 0)
            sizeString = formatSize(size) + " " + Resources.getString("LABEL_BYTES");

        String details;
        if (directoryCount > 0 && fileCount > 0)
            details = directoryString + ", " + fileString + "; " + sizeString;
        else if (directoryCount > 0)
            details = directoryString + "; " + sizeString;
        else if (fileCount > 0)
            details = fileString + "; " + sizeString;
        else
            details = "0 " + Resources.getString("LABEL_ITEMS");

        return details;
    }

    public static enum FileManagerAttribute
    {
        filename,
        size,
        type,
        dateModified
    }

    public static class FileManagerObject
    {
        File file;
        Map<FileManagerAttribute,String> attributes;
        FileManagerAttribute[] attributeOrder;

        FileManagerObject(File file, FileManagerAttribute[] attributeOrder)
        {
            this.file = file;
            this.attributes = new HashMap<FileManagerAttribute,String>();
            this.setAttributeOrder(attributeOrder);
        }
        
        public void setAttributeOrder(FileManagerAttribute[] attributeOrder)
        {
            this.attributeOrder = attributeOrder;
        }

        public void setAttribute(FileManagerAttribute attribute, String data)
        {
            attributes.put(attribute, data);
        }

        public String[] getAttributeStrings()
        {
            List<String> attributeList = new ArrayList<String>();
            for (FileManagerAttribute attribute : attributeOrder) {
                attributeList.add(attributes.get(attribute));
            }

            return attributeList.toArray(new String[0]);
        }
    }
}
