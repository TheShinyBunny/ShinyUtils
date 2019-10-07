package com.shinybunny.utils.json;

import com.shinybunny.utils.ExceptionFactory;

import java.util.Arrays;
import java.util.Map;

public class JsonHelperException extends ExceptionFactory.ResultException {

    public static final ExceptionFactory PATH_SEPARATOR_LAST_CHAR = create("Invalid Json path: Cannot have path separator ${separator} as the last character in path ${path}");
    public static final ExceptionFactory UNKNOWN_KEY = create("Key ${key} not found in json ${json}");
    public static final ExceptionFactory ARRAY_INDEXER_NOT_CLOSED = create("Array indexer in path ${path} is not closed");
    public static final ExceptionFactory INVALID_EXPECTED_TYPE = create("Invalid type of json element ${element}: expected ${type} but found ${foundType}").lazyEval("foundType",args->args.get("element",Json.class).getType());
    public static final ExceptionFactory INVALID_INDEX = create("Invalid array index in ${path}: \"${index}\"");
    public static final ExceptionFactory ELEMENT_IS_NOT_ARRAY = create("Element ${json} was expected to be an array");
    public static final ExceptionFactory ARRAY_INDEX_OUT_OF_BOUNDS = create("Index ${index} in array ${arr} is out of bounds.");
    public static final ExceptionFactory ELEMENT_IS_NOT_OBJECT = create("Element ${json} was expected to be a json object");
    public static final ExceptionFactory DESERIALIZATION_ERROR = create("An error occurred while deserializing json ${json} to type ${type}");
    public static final ExceptionFactory SERIALIZATION_ERROR = create("An error occurred while serializing type ${type}");
    public static final ExceptionFactory INVALID_ENUM_EXCEPTION = create("Invalid enum provided: ${value}. Expected one of ${elements}").convert("elements",(e)-> Arrays.toString((Object[])e));
    public static final ExceptionFactory COULD_NOT_DESERIALIZE = create("Could not deserialize json ${json} to type ${type}");
    public static final ExceptionFactory COULD_NOT_SERIALIZE = create("Could not serialize object ${obj} of type ${type}");
    public static final ExceptionFactory OBJECT_NOT_SERIALIZABLE = create("Object ${obj} of type ${type} has no adapter and is not annotated with @JsonAdaptable.").lazyEval("type",args->args.get("obj",Object.class).getClass().getName());

    public JsonHelperException(ExceptionFactory factory, Map<String, Object> params) {
        super(factory, params);
    }

    private static ExceptionFactory create(String msg) {
        return factory(msg,JsonHelperException::new);
    }
}
