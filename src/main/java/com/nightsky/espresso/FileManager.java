package com.nightsky.espresso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jna.platform.FileUtils;

public class FileManager {
    public static List<FileManagerObject> listDirectory(File directory, List<FileManagerAttribute> attributes)
    {
        List<FileManagerObject> fileList = new ArrayList<FileManagerObject>();
        String[] filenames = directory.list();
        for (String filename : filenames) {
            File file = Paths.get(directory.getPath(), filename).toAbsolutePath().toFile();
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
        File newDirectory = Paths.get(currentDirectory.getPath(), filename).toAbsolutePath().toFile();
        if (newDirectory.mkdir()) {
            return newDirectory;
        } else {
            return null;
        }
    }

    public static String getDetails(File directory)
    {
        long size = 0;
        int directoryCount = 0;
        int fileCount = 0;
        for (File file : directory.listFiles()) {
            size += file.length();
            if (file.isDirectory()) {
                directoryCount++;
            } else {
                fileCount++;
            }
        }

        String directoryString = "";
        if (directoryCount == 1) {
            directoryString = Integer.toString(directoryCount) + " " + Resources.getString("LABEL_DIRECTORY_SINGLE");
        } else if (directoryCount > 0) {
            directoryString = Integer.toString(directoryCount) + " " + Resources.getString("LABEL_DIRECTORY_MULTIPLE");
        }

        String fileString = "";
        if (fileCount == 1) {
            fileString = Integer.toString(fileCount) + " " + Resources.getString("LABEL_FILE_SINGLE");
        } else if (fileCount > 0) {
            fileString = Integer.toString(fileCount) + " " + Resources.getString("LABEL_FILE_MULTIPLE");
        }

        String sizeString = "";
        if (size > 0) {
            sizeString = formatSize(size) + " " + Resources.getString("LABEL_BYTES");
        }

        String details;
        if (directoryCount > 0 && fileCount > 0) {
            details = directoryString + ", " + fileString + "; " + sizeString;
        } else if (directoryCount > 0) {
            details = directoryString + "; " + sizeString;
        } else if (fileCount > 0) {
            details = fileString + "; " + sizeString;
        } else {
            details = "0 " + Resources.getString("LABEL_ITEMS");
        }

        return details;
    }

    public static boolean moveToTrash(File file)
    {
        FileUtils fileUtils = FileUtils.getInstance();
        if (fileUtils.hasTrash()) {
            return moveToTrashJNA(fileUtils, file);
        } else if (Platform.isUnix()) {
            return moveToTrashUnix(file);
        } else {
            return false;
        }
    }

    private static boolean moveToTrashJNA(FileUtils fileUtils, File file)
    {
        try {
            fileUtils.moveToTrash(new File[] { file });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean moveToTrashUnix(File file)
    {
        String dataHome = System.getenv("XDG_DATA_HOME");
        if (dataHome.length() == 0) {
            String home = System.getProperty("user.home");
            if (home.length() == 0) {
                home = "~";
            }
            dataHome = Paths.get(home, ".local", "share").toString();
        }

        Path trashPath = Paths.get(dataHome, "Trash").toAbsolutePath();
        File trashDirectory = new File(trashPath.toString());
        if (!trashDirectory.exists()) {
            if (!trashDirectory.mkdirs()) {
                return false;
            }
        }

        Path trashInfoPath = Paths.get(trashPath.toString(), "info");
        File trashInfo = trashInfoPath.toFile();
        if (!trashInfo.exists()) {
            if (!trashInfo.mkdirs()) {
                return false;
            }
        } else if (!trashInfo.isDirectory()) {
            if (!trashInfo.delete()) {
                return false;
            }
            if (!trashInfo.mkdirs()) {
                return false;
            }
        }

        Path trashFilesPath = Paths.get(trashPath.toString(), "files");
        Path filePath = file.toPath().toAbsolutePath();
        Path newPath = Paths.get(trashFilesPath.toString(), filePath.getFileName().toString()).toAbsolutePath();
        if (Files.exists(newPath)) {
            int count = 2;
            while (Files.exists(Paths.get(newPath.toString(), "." + Integer.toString(count)).toAbsolutePath())) {
                count++;
            }

            newPath = Paths.get(trashPath.toString(), "files", filePath.getFileName().toString() + "." + Integer.toString(count)).toAbsolutePath();
        } else {
            File trashFiles = trashFilesPath.toFile();
            if (!trashFiles.exists()) {
                if (!trashFiles.mkdirs()) {
                    return false;
                }
            } else if (!trashFiles.isDirectory()) {
                if (!trashFiles.delete()) {
                    return false;
                }
                if (!trashFiles.mkdirs()) {
                    return false;
                }
            }
        }

        Path infoFilePath = Paths.get(trashInfoPath.toString(), newPath.getFileName().toString() + ".trashinfo").toAbsolutePath();
        Set<PosixFilePermission> infoFilePermissions = PosixFilePermissions.fromString("rw-------");
        FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(infoFilePermissions);

        try {
            Files.createFile(infoFilePath, permissions);

            BufferedWriter writer = new BufferedWriter(new FileWriter(infoFilePath.toFile()));
            String fileString = escape(filePath.toString());
            String dateString = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss").withZone(Platform.zoneId).format(Instant.now());

            writer.write("[Trash Info]\n");
            writer.write("Path=" + fileString + "\n");
            writer.write("DeletionDate=" + dateString + "\n");

            writer.close();
        } catch (IOException e) {
            return false;
        }

        try {
            Files.move(filePath, newPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(filePath, newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException f) {
                return false;
            }
        }

        return true;
    }

    public static boolean delete(File file)
    {
        try {
            Files.delete(file.toPath().toAbsolutePath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String escape(String s){
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'");
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
