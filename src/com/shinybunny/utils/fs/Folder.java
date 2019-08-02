package com.shinybunny.utils.fs;

import java.util.ArrayList;
import java.util.List;

public class Folder extends AbstractFile {

    Folder(Folder parent, String name) {
        this(parent.path, name);
    }

    Folder(String path, String name) {
        this(path + "/" + name);
    }

    Folder(String fullName) {
        this(new java.io.File(fullName));
    }

    Folder(java.io.File handle) {
        super(handle);
    }

    @Override
    public Folder create() {
        return (Folder) super.create();
    }

    @Override
    protected void create(java.io.File handle) {
        handle.mkdirs();
    }

    public File subFile(String name, String extension) {
        return subFile(name + "." + extension);
    }

    public File subFile(String name) {
        return FileExplorer.getFile(this,name);
    }

    public Folder subFolder(String name) {
        return FileExplorer.getFolder(this,name);
    }

    public List<Folder> getSubFolders() {
        List<Folder> folders = new ArrayList<>();
        for (java.io.File f : handle.listFiles()) {
            if (f.isDirectory()) {
                folders.add(FileExplorer.getFolder(f));
            }
        }
        return folders;
    }

    public List<File> getSubFiles() {
        List<File> files = new ArrayList<>();
        for (java.io.File f : handle.listFiles()) {
            if (f.isFile()) {
                files.add(FileExplorer.getFile(f));
            }
        }
        return files;
    }

    public List<AbstractFile> getChildren() {
        List<AbstractFile> children = new ArrayList<>();
        for (java.io.File f : handle.listFiles()) {
            children.add(FileExplorer.getAbstractFile(f));
        }
        return children;
    }
}
