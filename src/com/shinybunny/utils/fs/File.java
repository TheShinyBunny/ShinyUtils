package com.shinybunny.utils.fs;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.Check;

import java.io.*;

public class File extends AbstractFile {
    public File(java.io.File handle) {
        super(handle);
    }

    public File(Folder parent, String name) {
        super(new java.io.File(parent.handle,name));
    }

    public File(String name) {
        super(new java.io.File(name));
    }



    @Override
    protected void create() {
        try {
            handle.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean delete(int unused) {
        return handle.delete();
    }

    public String getContent() {
        return lines().join("\n");
    }

    private Array<String> lines() {
        try (BufferedReader r = Check.notNull(createReader(),"buffered reader of " + this)) {
            return Array.fromStream(r.lines());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Array.empty();
    }

    public BufferedReader createReader() {
        if (deleted) return null;
        try {
            return new BufferedReader(new FileReader(handle));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File of(String name) {
        return new File(name);
    }

    public static File of(java.io.File file) {
        return new File(file);
    }

    public static File of(Folder parent, String name) {
        return new File(parent,name);
    }

    public void setContent(String content) {
        if (deleted) return;
        try (BufferedWriter w = Check.notNull(createWriter(),"buffered write of " + this)){
            w.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLines(Array<String> lines) {
        setContent(lines.join("\n"));
    }

    public BufferedWriter createWriter() {
        try {
            return new BufferedWriter(new FileWriter(handle));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
