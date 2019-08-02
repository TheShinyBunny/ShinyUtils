package com.shinybunny.utils;

import com.shinybunny.utils.fs.File;

import java.util.Stack;
import java.util.function.Predicate;

public class StringReader {

    public final ExceptionFactory EXPECTED_TOKEN = factory("Expected token '${token}'\nat ${reader}");
    public final ExceptionFactory EXPECTED_IDENTIFIER = factory("Expected identifier\nat ${reader}");
    public final ExceptionFactory EXPECTED_ONE_OF = factory("Expected one of ${tokens}\nat ${reader}").convert("tokens", (String[] tokens)->String.join(", ",tokens));
    public final ExceptionFactory EXPECTED_NUMBER = factory("Expected number\nat ${reader}");
    public final ExceptionFactory INVALID_INTEGER = factory("Invalid integer ${num}\nat ${reader}");
    public final ExceptionFactory INVALID_DOUBLE = factory("Invalid number ${num}\nat ${reader}");
    public static final Predicate<Character> allowedInInt = c->c >= '0' && c <= '9';
    public static final Predicate<Character> allowedInDouble = c->{
        if (c >= '0' && c <= '9') return true;
        return c == '.' || c == '-';
    };

    private ExceptionFactory factory(String msg) {
        return ExceptionFactory.make(msg).lazyEval("reader",args->this.getSectionString());
    }

    protected String string;
    private int pos;
    private boolean ignoreSpaces;
    private Stack<Integer> restorePoints;

    public StringReader(String str) {
        this(str,false);
    }

    public static StringReader of(String s) {
        return new StringReader(s);
    }

    public static StringReader of(File f) {
        return new StringReader(f.getContent());
    }

    public StringReader(String str, boolean ignoreSpaces) {
        this.string = str;
        this.ignoreSpaces = ignoreSpaces;
        this.restorePoints = new Stack<>();
    }

    public StringReader ignoreSpaces(boolean ignore) {
        this.ignoreSpaces = ignore;
        return this;
    }

    public char readChar() {
        char c = peek();
        pos++;
        return c;
    }

    public boolean canRead() {
        return pos < string.length();
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int distance) {
        if (pos + distance >= string.length()) {
            return (char) -1;
        }
        return string.charAt(pos + distance);
    }

    public String readTo(char... chars) {
        skipSpaceIfIgnored();
        String s = "";
        while (canRead() && !isAnyNext(chars)) {
            s += readChar();
        }
        return s;
    }

    public boolean isNext(char c) {
        skipSpaceIfIgnored();
        return peek() == c;
    }

    public boolean isNext(String s) {
        skipSpaceIfIgnored();
        if (canRead()) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (peek(i) != c || i >= string.length()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void skipSpaceIfIgnored() {
        if (ignoreSpaces) {
            skipSpace();
        }
    }


    public void skip() {
        pos++;
    }

    public void skip(int count) {
        pos += count;
    }

    public void skipAll(char... c) {
        while (canRead() && StringUtils.containsChar(c,peek()))
            skip();
    }

    public void skipSpace() {
        skipAll(' ','\n');
    }

    public int getPos() {
        return pos;
    }

    public void skipExpected(String s) {
        if (isNext(s)) {
            skip(s.length());
        } else {
            throw EXPECTED_TOKEN.create(s);
        }
    }

    public boolean isAnyNext(char... chars) {
        for (char c : chars) {
            if (isNext(c)) return true;
        }
        return false;
    }

    public String readIdentifier() {
        skipSpaceIfIgnored();
        String word = "";
        while (canRead() && Character.isJavaIdentifierPart(peek())) {
            word += readChar();
        }
        if (word.isEmpty()) throw EXPECTED_IDENTIFIER.create();
        return word;
    }

    public String readLettersOrUnderscore() {
        skipSpaceIfIgnored();
        String word = "";
        while (canRead() && (Character.isLetter(peek()) || peek() == '_')) {
            word += readChar();
        }
        if (word.isEmpty()) throw EXPECTED_IDENTIFIER.create();
        return word;
    }

    public String readOneOf(String... words) {
        for (String s : words) {
            if (skipIf(s)) return s;
        }
        throw EXPECTED_ONE_OF.create((Object) words);
    }

    public boolean skipIf(String s) {
        if (isNext(s)) {
            skip(s.length());
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String before = pos == 0 ? "" : string.substring(0,Math.min(pos,string.length()));
        String after = pos >= string.length() - 1 ? "" : string.substring(pos+1);
        char curr = pos >= string.length() ? ' ' : peek();
        return before + "[" + curr + "]" + after;
    }

    public String getSectionString() {
        String toString = toString();
        int start = Math.max(pos - 20,0);
        int end = Math.min(pos + 20,toString.length());
        return toString.substring(start,end);
    }

    public int readInt() {
        String s = "";
        if (skipIf("-")) {
            s += "-";
        }
        s += readWhile(allowedInInt);
        if (s.isEmpty()) {
            throw EXPECTED_NUMBER.create();
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw INVALID_INTEGER.create(s);
        }
    }

    public double readDouble() {
        String s = "";
        if (skipIf("-")) {
            s += "-";
        }
        s += readWhile(allowedInDouble);
        if (s.isEmpty()) {
            throw EXPECTED_NUMBER.create();
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw INVALID_DOUBLE.create(s);
        }
    }

    public Number tryReadNumber() {
        savePos();
        try {
            return readDouble();
        } catch (Exception e) {
            resave();
            try {
                return readInt();
            } catch (Exception e2) {
                restore();
                return null;
            }
        }
    }

    public String readWhile(Predicate<Character> predicate) {
        String s = "";
        while (canRead() && predicate.test(peek()))
            s += readChar();
        return s;
    }

    public String readQuotedString() {
        skipExpected("\"");
        String s = readToEscaped('"','\\');
        skipExpected("\"");
        return s;
    }

    public String readToEscaped(char c, char escapeChar) {
        String s = "";
        boolean skipped = ignoreSpaces;
        ignoreSpaces = false;
        boolean esc = false;
        while (canRead() && (esc || !isNext(c))) {
            char cur = readChar();
            esc = false;
            if (cur == escapeChar) {
                esc = true;
            } else {
                s += cur;
            }
        }
        ignoreSpaces = skipped;
        return s;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void savePos() {
        restorePoints.push(pos);
    }

    public void restore() {
        pos = restorePoints.pop();
    }

    public void resave() {
        restore();
        savePos();
    }

    public String getRest() {
        return canRead() ? substring(pos,string.length()) : "";
    }

    public String substring(int from, int to) {
        return string.substring(from,to);
    }

    public String getOriginalString() {
        return string;
    }
}
