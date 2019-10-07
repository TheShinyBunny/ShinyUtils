package com.shinybunny.utils.json;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.IBasicArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class JsonArray extends Json implements IBasicArray<Json> {

    private Array<Json> elements;

    public JsonArray(JsonHelper helper, Array<Json> elements) {
        super(helper);
        this.elements = elements;
    }

    public JsonArray(JsonHelper helper) {
        this(helper,new Array<>());
    }

    public JsonArray() {
        elements = new Array<>();
    }

    public void add(Object obj) {
        elements.add(Json.of(helper,obj));
    }

    public Json set(int index, Object obj) {
        Json prev = elements.get(index);
        elements.set(index,Json.of(helper,obj));
        return prev;
    }

    @Override
    public JsonElementType getType() {
        return JsonElementType.ARRAY;
    }

    public Json get(int index) {
        if (index < 0) {
            index = size() + index;
        }
        if (index > size()) {
            throw JsonHelperException.ARRAY_INDEX_OUT_OF_BOUNDS.create(index,this);
        }
        return elements.get(index);
    }

    @Override
    public Array<Json> values() {
        return elements;
    }

    public int size() {
        return elements.length();
    }


    public <T> List<T> map(Function<Json, T> mapper) {
        List<T> list = new ArrayList<>();
        for (Json j : this.values()) {
            list.add(mapper.apply(j));
        }
        return list;
    }
}
