package com.shinybunny.utils.json;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.StringReader;

public class JsonReader extends StringReader {


    private static final ExceptionFactory INVALID_JSON_VALUE = ExceptionFactory.make("Invalid Json value for key ${key} at ${reader}");
    private final JsonHelper helper;

    public JsonReader(JsonHelper helper, String str) {
        super(str,true);
        this.helper = helper;
    }

    public Json read() {
        if (skipIf("{")) {
            return readObject();
        } else if (skipIf("[")) {
            return readArray();
        }
        return null;
    }

    private JsonArray readArray() {
        JsonArray arr = helper.newArray();
        while (!isNext(']')) {
            Object item = readValue();
            if (item == null) {
                throw INVALID_JSON_VALUE.create(this);
            }
            arr.add(item);
            if (!skipIf(",")) {
                skipExpected("]");
                break;
            }
        }
        return arr;
    }

    private Json readObject() {
        Json json = helper.newObject();
        while (!isNext('}')) {
            String key = readQuotedString();
            skipExpected(":");
            Object value = readValue();
            if (value == null) {
                throw INVALID_JSON_VALUE.create(key,this);
            }
            json.set(key,value);
            if (!skipIf(",")) {
                skipExpected("}");
                break;
            }
        }
        return json;
    }

    private Object readValue() {
        if (isNext('"')) {
            return readQuotedString();
        } else if (isNext("true") || isNext("false")) {
            return skipIf("true");
        } else {
            Number n = tryReadNumber();
            if (n != null) {
                return n;
            } else {
                return read();
            }
        }
    }
}
