package com.shinybunny.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Automatically separate a single string to a list by lines of a maximum size <code>lineLength</code>.
     * @param text The full string
     * @param lineLength The maximum length of a new line
     * @return The separated lines
     */
    public static List<String> autoNewLine(String text, int lineLength) {
        List<String> list = new ArrayList<>();
        boolean flag = true;
        int i = 0;
        do {
            try {
                String sub = text.substring(i, i + lineLength);
                String sub2 = sub.substring(0, sub.lastIndexOf(" "));
                list.add(sub2);
                i += sub2.length() + 1;
            } catch (IndexOutOfBoundsException e) {
                String sub = text.substring(i);
                list.add(sub);
                flag = false;
            }
        } while (flag);
        return list;
    }


    /**
     * Will automatically add commas in numbers. For example, 18728742 will return 18,728,742.
     * @param x The number to format
     * @return The number with commas
     */
    public static String numberComma(int x) {
        return String.format("%,d",x);
    }

    public static String wrapWords(String text, int maxLength) {
        String[] lines = text.split("\n");
        StringBuilder b = new StringBuilder();
        for (String s : lines) {
            if (s.length() <= maxLength) {
                b.append(s).append('\n');
            } else {
                while (s.length() > maxLength) {
                    int lastSpace = s.substring(0,maxLength).lastIndexOf(' ');
                    if (lastSpace < 0) {
                        b.append(s, 0, maxLength);
                        s = s.substring(maxLength);
                    } else {
                        b.append(s, 0, lastSpace);
                        if (s.length() > lastSpace + 1) {
                            s = s.substring(lastSpace + 1);
                        }
                    }
                    b.append('\n');
                }
                b.append(s).append('\n');
            }
        }
        b.deleteCharAt(b.length()-1);
        return b.toString();
    }

    public final static TreeMap<Integer, String> romanSigns;

    static {
        romanSigns = new TreeMap<>();
        romanSigns.put(1000, "M");
        romanSigns.put(900, "CM");
        romanSigns.put(500, "D");
        romanSigns.put(400, "CD");
        romanSigns.put(100, "C");
        romanSigns.put(90, "XC");
        romanSigns.put(50, "L");
        romanSigns.put(40, "XL");
        romanSigns.put(10, "X");
        romanSigns.put(9, "IX");
        romanSigns.put(5, "V");
        romanSigns.put(4, "IV");
        romanSigns.put(1, "I");

    }

    public static String toRoman(int number) {
        int x = romanSigns.floorKey(number);
        if (number == x) {
            return romanSigns.get(number);
        }
        return romanSigns.get(x) + toRoman(number-x);
    }

    /**
     * Does basically what the name says.
     * @param list The list to check
     * @param s The string to find
     * @return Whether the list contains the given string, ignoring case sensitivity.
     */
    public static boolean containsIgnoreCase(Collection<String> list, String s) {
        for (String st : list) {
            if (st.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Will list in a nice string all obj.toString() of all objects in a list
     * @param list The list
     * @return All the string values separated by commas.
     */
    public static <T> String niceList(List<T> list) {
        StringBuilder b = new StringBuilder();
        for (T t : list) {
            b.append(t.toString()).append(", ");
        }
        b.delete(b.length() - 3, b.length());
        if (list.size() > 1) {
            b.replace(b.lastIndexOf(",") - 1, b.lastIndexOf(",") + 2, " and ");
        }
        return b.toString();
    }

    /**
     * Adds a position suffix for numbers. For example, 8 will give 8th, 23 -> 23rd, 911 -> 911st
     * @param x The number to format
     * @return The number string with suffix
     */
    public static String addPosSuffix(int x) {
        return x + getPosSuffix(x);
    }

    public static String getPosSuffix(int x) {
        if (x % 100 > 10 && x % 100 < 14) return "th";
        switch (x % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * Will add a plural S if the specified number isn't 1.
     * @param i The number variable
     * @param s The singular word
     * @return The pluralized word as needed.
     */
    public static String pluralizeIfMultiple(int i, String s) {
        if (i == 1) {
            return s;
        }
        return StringUtils.Plurals.pluralize(s);
    }

    /**
     * Will shrink a string to the maximum length given.
     * @param text The string to shrink
     * @param max The maimum string length
     * @return The given string if max is larger or equal to the string length, or a substring from the start to index 'max'.
     */
    public static String shrinkToLength(String text, int max) {
        if (max >= text.length()) {
            return text;
        } else {
            return text.substring(0,max);
        }
    }

    /**
     * Will convert all object in a list to strings, using the {@link Object#toString()}
     * @param list The list to convert
     * @return a string list of the objects in the original list.
     */


    /**
     * Converts to upper case every letter on the start or after a period.
     * @param text
     * @return
     */
    public static String capitalize(String text) {
        if (!text.contains(".")) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        boolean hasLastPeriod = text.charAt(text.length()-1) == '.';
        if (hasLastPeriod) {
            text = text.substring(0,text.length()-1);
        }
        String[] sentences = text.split("\\.");
        StringBuilder newTextBuilder = new StringBuilder();
        for (String s : sentences) {
            String f = String.valueOf(s.trim().charAt(0)).toUpperCase();
            String sen = f + s.trim().substring(1);
            newTextBuilder.append(sen).append(". ");
        }
        return newTextBuilder.toString().trim();
    }


    public static boolean isAlphanumeric(String text) {
        return text.matches("^[a-zA-Z0-9_]*$");
    }

    public static boolean startsWithIgnoreCase(String text, String prefix) {
        return text.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public static boolean endsWithIgnoreCase(String text, String suffix) {
        return text.toLowerCase().endsWith(suffix.toLowerCase());
    }

    public static <T extends Enum<T>> T getEnumValue(Class<T> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass,name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String pluralize(String word) {
        return StringUtils.Plurals.pluralize(word.toLowerCase());
    }

    public static String pluralize(String word, boolean capitalize) {
        if (capitalize) {
            return capitalize(pluralize(word));
        }
        return pluralize(word);
    }

    public static boolean containsChar(char[] chars, char c) {
        for (char a : chars) {
            if (a == c) return true;
        }
        return false;
    }

    public static class Plurals {
        // @formatter:off
        private static final List<String> unpluralizables = Arrays.asList(
                "equipment", "information", "rice", "money", "species", "series",
                "fish", "sheep", "deer");

        private static final List<StringUtils.Plurals.Replacer> singularizations = Arrays.asList(
                replace("(.*)people$").with("$1person"),
                replace("oxen$").with("ox"),
                replace("children$").with("child"),
                replace("feet$").with("foot"),
                replace("teeth$").with("tooth"),
                replace("geese$").with("goose"),
                replace("(.*)ives?$").with("$1ife"),
                replace("(.*)ves?$").with("$1f"),
                replace("(.*)men$").with("$1man"),
                replace("(.+[aeiou])ys$").with("$1y"),
                replace("(.+[^aeiou])ies$").with("$1y"),
                replace("(.+)zes$").with("$1"),
                replace("([m|l])ice$").with("$1ouse"),
                replace("matrices$").with("matrix"),
                replace("indices$").with("index"),
                replace("(.+[^aeiou])ices$").with("$1ice"),
                replace("(.*)ices$").with("$1ex"),
                replace("(octop|vir)i$").with("$1us"),
                replace("(.+(s|x|sh|ch))es$").with("$1"),
                replace("(.+)s$").with("$1")
        );

        private static final List<StringUtils.Plurals.Replacer> pluralizations = Arrays.asList(
                replace("(.*)person$").with("$1people"),
                replace("ox$").with("oxen"),
                replace("child$").with("children"),
                replace("foot$").with("feet"),
                replace("tooth$").with("teeth"),
                replace("goose$").with("geese"),
                replace("(.*)fe?$").with("$1ves"),
                replace("(.*)man$").with("$1men"),
                replace("(.+[aeiou]y)$").with("$1s"),
                replace("(.+[^aeiou])y$").with("$1ies"),
                replace("(.+z)$").with("$1zes"),
                replace("([m|l])ouse$").with("$1ice"),
                replace("(.+)(e|i)x$").with("$1ices"),
                replace("(octop|vir)us$").with("$1i"),
                replace("(.+(s|x|sh|ch))$").with("$1es"),
                replace("(.+)").with("$1s" )
        );
        // @formatter:on

        /**
         * If possible, ensure the provided word is a singular word form.
         *
         * @return The singular form of the word, or the input if no
         *         rules test.
         */
        public static String singularize(String word) {
            if (unpluralizables.contains(word.toLowerCase())) {
                return word;
            }

            for (final StringUtils.Plurals.Replacer singularization : singularizations) {
                if (singularization.matches(word)) {
                    return singularization.replace();
                }
            }

            return word;
        }

        /**
         * If possible, ensure the provided word is a plural word form.
         *
         * @return The plural form of the word, or the input if no
         *         rules test.
         */
        public static String pluralize(String word) {
            if (unpluralizables.contains(word.toLowerCase())) {
                return word;
            }

            for (final StringUtils.Plurals.Replacer pluralization : pluralizations) {
                if (pluralization.matches(word.toLowerCase())) {
                    return pluralization.replace();
                }
            }

            return word;
        }

        /**
         * A simple helper class with a Builder to provide a little syntactic sugar
         */
        static class Replacer {
            Pattern pattern;
            String replacement;
            Matcher m;

            static class Builder {
                private final Pattern pattern;

                Builder(Pattern pattern) {
                    this.pattern = pattern;
                }

                StringUtils.Plurals.Replacer with(String replacement) {
                    return new StringUtils.Plurals.Replacer(pattern, replacement);
                }
            }

            private Replacer(Pattern pattern, String replacement) {
                this.pattern = pattern;
                this.replacement = replacement;
            }

            boolean matches(String word) {
                m = pattern.matcher(word);
                return m.matches();
            }

            String replace() {
                return m.replaceFirst(replacement);
            }
        }

        static StringUtils.Plurals.Replacer.Builder replace(String pattern) {
            return new StringUtils.Plurals.Replacer.Builder(Pattern.compile(pattern));
        }
    }

}
