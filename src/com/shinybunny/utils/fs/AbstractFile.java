package com.shinybunny.utils.fs;

import java.io.File;

public abstract class AbstractFile {

    private Folder parent;
    protected java.io.File handle;
    protected String name;
    protected final String path;
    protected boolean exists;

    AbstractFile(Folder parent, String name) {
        this(parent.path, name);
        this.parent = parent;
    }

    AbstractFile(String path, String name) {
        this(path + "/" + name);
    }

    AbstractFile(String fullName) {
        this(new File(fullName));
    }

    AbstractFile(java.io.File handle) {
        this.handle = handle;
        this.path = handle.getParent();
        this.name = this instanceof Folder ? handle.getName() : handle.getName().substring(0,handle.getName().lastIndexOf('.'));
        this.exists = handle.exists();
    }

    public AbstractFile create() {
        if (!exists) {
            create(handle);
            exists = true;
        }
        return this;
    }

    protected abstract void create(File handle);

    public File getHandle() {
        return handle;
    }

    public Folder getParent() {
        return parent == null ? parent = FileExplorer.getFolder(path, false) : parent;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getFullName() {
        return path + "/" + name;
    }

    public boolean delete() {
        if (handle.delete()) {
            exists = false;
            FileExplorer.delete(this);
            return true;
        }
        return false;
    }

    public boolean exists() {
        return exists;
    }

    @Override
    public String toString() {
        return getFullName() + (exists() ? "" : " (doesn't exist)");
    }

}
