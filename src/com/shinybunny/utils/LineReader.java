package com.shinybunny.utils;

import java.util.Arrays;
import java.util.List;

public class LineReader extends StringReader {

    private List<String> lines;
    private int line = 0;

    public LineReader(String... lines) {
        this(Arrays.asList(lines));
    }

    public LineReader(List<String> lines) {
        super(lines.get(0));
        this.lines = lines;
    }

    public String nextLine() {
        line++;
        setPos(0);
        if (hasMoreLines()) {
            string = lines.get(line);
        }
        return string;
    }

    public int lineCount() {
        return lines.size();
    }

    public int getLineNumber() {
        return line;
    }

    public List<String> getLines() {
        return lines;
    }

    public boolean hasMoreLines() {
        return line < lineCount();
    }

    @Override
    public String toString() {
        return super.toString() + " (line " + (line + 1) + ")";
    }

    @Override
    public String getSectionString() {
        return this.toString();
    }

    public String getCurrentLine() {
        return lines.get(line);
    }
}
