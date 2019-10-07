package com.shinybunny.utils.fs;

import com.shinybunny.utils.Check;

import java.io.File;

public class FileName {
    private final String fullName;
    private final String path;

    public FileName(File file) {
        this.path = file.getParent();
        this.fullName = file.getName();
    }

    public String getName() {
        return fullName.contains(File.separator) ? fullName.substring(0,fullName.lastIndexOf(File.separatorChar)) : fullName;
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return Check.inRange(fullName.lastIndexOf("."),0,fullName.length()-2) ? fullName.substring(fullName.lastIndexOf(File.separatorChar)+1) : null;
    }
}
