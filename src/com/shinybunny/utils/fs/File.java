package com.shinybunny.utils.fs;

import com.shinybunny.utils.LineReader;
import com.shinybunny.utils.StringReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class File extends AbstractFile {

    private String extension;

    File(Folder parent, String name) {
        this(parent.path, name);
    }

    File(String path, String name) {
        this(path + "/" + name);
    }

    File(String fullName) {
        this(new java.io.File(fullName));
    }

    File(java.io.File handle) {
        super(handle);
        this.extension = handle.getName().substring(handle.getName().lastIndexOf('.') + 1);
    }

    public static File of(String path) {
        return FileExplorer.getFile(path, false);
    }

    public static File getOrCreate(String path) {
        return FileExplorer.getFile(path, true);
    }

    public static File from(java.io.File ioFile) {
        return FileExplorer.getFile(ioFile);
    }

    public static File of(Folder parent, String name) {
        return FileExplorer.getFile(parent,name);
    }

    @Override
    public File create() {
        return (File) super.create();
    }

    @Override
    protected void create(java.io.File handle) {
        try {
            handle.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StringReader read() {
        return StringReader.of(this);
    }

    public LineReader readLines() {
        return new LineReader(getLines());
    }



    public String getExtension() {
        return extension;
    }

    public String getNameAndExtension() {
        return name + "." + extension;
    }

    public List<String> getLines() {
        BufferedReader reader = getReader();
        List<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public String getContent() {
        BufferedReader reader = getReader();
        StringBuilder b = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                b.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b.toString();
    }

    public BufferedReader getReader() {
        try {
            return new BufferedReader(new FileReader(handle));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public File getRelative(String fileName) {
        return getParent().subFile(fileName);
    }

    @Override
    public String getFullName() {
        return super.getFullName() + "." + extension;
    }

}
