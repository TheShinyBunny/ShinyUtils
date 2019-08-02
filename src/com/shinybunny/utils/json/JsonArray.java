package com.shinybunny.utils.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class JsonArray extends Json implements Iterable<Json> {

    private List<Json> elements;

    public JsonArray(JsonHelper helper, List<Json> elements) {
        super(helper);
        this.elements = elements;
    }

    public JsonArray(JsonHelper helper) {
        this(helper,new ArrayList<>());
    }

    public JsonArray() {

    }

    public void add(Object obj) {
        elements.add(Json.of(helper,obj));
    }

    public void set(int index, Object obj) {
        elements.set(index,Json.of(helper,obj));
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
    public Collection<Json> values() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Object getValue() {
        return elements;
    }


    public <T> List<T> map(Function<Json, T> mapper) {
        List<T> list = new ArrayList<>();
        for (Json j : this) {
            list.add(mapper.apply(j));
        }
        return list;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Json> iterator() {
        return elements.iterator();
    }
}
