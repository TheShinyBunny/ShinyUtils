package com.shinybunny.utils.fs;

import com.shinybunny.utils.ExceptionFactory;

import java.util.HashMap;
import java.util.Map;

public class FileExplorer {

    private static final Map<String, AbstractFile> fileMap = new HashMap<>();
    private static final ExceptionFactory PATH_IS_NOT_A_FILE = ExceptionFactory.make("Invalid file path ${path}");
    private static final ExceptionFactory PATH_IS_NOT_A_FOLDER = ExceptionFactory.make("Invalid folder path ${path}");

    public static File getFile(String name, boolean create) {
        if (!isFilePath(name)) {
            throw PATH_IS_NOT_A_FILE.create(name);
        }
        AbstractFile file = fileMap.get(name);
        if (file == null) {
            file = create(name, create);
        }
        return (File) file;
    }

    public static Folder getFolder(String name, boolean create) {
        if (isFilePath(name)) {
            throw PATH_IS_NOT_A_FOLDER.create(name);
        }
        AbstractFile file = fileMap.get(name);
        if (file == null) {
            file = create(name, create);
        }
        return (Folder) file;
    }

    public static Folder getFolder(java.io.File dir) {
        if (!dir.isDirectory()) throw PATH_IS_NOT_A_FOLDER.create(dir);
        return getFolder(dir.getPath(),false);
    }


    public static AbstractFile create(String name, boolean createFile) {
        AbstractFile file = isFilePath(name) ? new File(name) : new Folder(name);
        if (createFile) {
            file.create();
            fileMap.putIfAbsent(file.getFullName(),file);
        }
        return file;
    }

    public static boolean isFilePath(String name) {
        int extensionIndex = name.lastIndexOf('.');
        int pathIndex = name.lastIndexOf('/');
        return extensionIndex > pathIndex;
    }

    public static File getFile(Folder folder, String name) {
        return getFile(folder.getFullName() + "/" + name, false);
    }

    public static Folder getFolder(Folder folder, String name) {
        return getFolder(folder.path + "/" + name, false);
    }

    public static File getFile(java.io.File ioFile) {
        return getFile(ioFile.getPath(), false);
    }

    public static AbstractFile getAbstractFile(java.io.File ioFile) {
        return ioFile.isFile() ? getFile(ioFile) : getFolder(ioFile);
    }

    static void delete(AbstractFile file) {
        fileMap.remove(file.getFullName());
    }
}
