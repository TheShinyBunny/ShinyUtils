package com.shinybunny.utils.json;

import com.shinybunny.utils.MapUtils;

public class PrettyPrinter {

    public static String print(Json json, int spaces) {
        return toPrettyString(json,spaces(spaces),0);
    }

    private static String toPrettyString(Json json, String spaces, int indent) {
        StringBuilder b = new StringBuilder();
        if (json.isObject()) {
            b.append("{\n");
            b.append(MapUtils.remap(json.getEntries(),e->tabs(spaces,indent + 1) + '"' + e.getKey() + "\": " + toPrettyString(e.getValue(),spaces,indent + 1)).join(",\n"));
            b.append('\n').append(tabs(spaces,indent)).append('}');
        } else if (json.isArray()) {
            if (json.values().every(Json::isPrimitive)) {
                b.append('[').append(json.values().join(", ")).append(']');
            } else {
                b.append("[\n");
                b.append(json.values().map(j -> tabs(spaces,indent + 1) + toPrettyString(j,spaces,indent + 1)).join(",\n"));
                b.append('\n').append(tabs(spaces,indent)).append(']');
            }
        } else {
            b.append(json.toString());
        }
        return b.toString();
    }

    private static String tabs(String spaces, int indent) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            b.append(spaces);
        }
        return b.toString();
    }

    private static String spaces(int spaces) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            s.append(" ");
        }
        return s.toString();
    }

}
