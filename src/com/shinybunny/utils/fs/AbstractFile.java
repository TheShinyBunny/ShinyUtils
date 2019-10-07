package com.shinybunny.utils.fs;

public abstract class AbstractFile {

    protected java.io.File handle;
    public final FileName name;
    protected boolean deleted = false;

    public AbstractFile(java.io.File handle) {
        this.handle = handle;
        create();
        this.name = new FileName(handle);
    }

    protected abstract void create();

    public void delete() {
        if (this.delete(0)) {
            deleted = true;
        }
    }

    protected abstract boolean delete(int unused);

    public String getName() {
        return name.getName();
    }

    public String getPath() {
        return name.getPath();
    }

    public java.io.File getHandle() {
        return handle;
    }


    public static AbstractFile of(java.io.File file) {
        if (file.isDirectory()) return Folder.of(file);
        return File.of(file);
    }
}
