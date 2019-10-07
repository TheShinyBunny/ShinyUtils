package com.shinybunny.utils.fs;

import com.shinybunny.utils.Array;

public class Folder extends AbstractFile {
    public Folder(java.io.File handle) {
        super(handle);
    }

    public Folder(String name) {
        super(new java.io.File(name));
    }

    @Override
    protected void create() {
        handle.mkdirs();
    }

    @Override
    protected boolean delete(int unused) {
        children().forEach(AbstractFile::delete);
        return handle.delete();
    }

    public Array<AbstractFile> children() {
        return Array.of(handle.listFiles()).map(AbstractFile::of);
    }

    public static Folder of(String name) {
        return new Folder(name);
    }

    public static Folder of(java.io.File file) {
        return new Folder(file);
    }

    public File child(String name) {
        return new File(this,name);
    }
}
