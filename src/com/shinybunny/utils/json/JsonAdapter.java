package com.shinybunny.utils.json;

import com.shinybunny.utils.PojoHelper;

public interface JsonAdapter<T> extends JsonSerializer<T>,JsonDeserializer<T> {

    static <T> Json serializeCasted(JsonAdapter<T> adapter, Object obj, JsonHelper helper) {
        return adapter.serialize((T) obj,helper);
    }

    static <T> JsonAdapter<T> from(Class<T> type, JsonSerializer<T> serializer) {
        return new JsonAdapter<T>() {
            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T deserialize(Json json, JsonHelper helper) {
                throw JsonHelperException.COULD_NOT_DESERIALIZE.create(json,type);
            }

            @Override
            public Json serialize(T obj, JsonHelper helper) {
                return serializer.serialize(obj, helper);
            }
        };
    }

    static <T> JsonAdapter<T> from(Class<T> type, JsonDeserializer<T> deserializer) {
        return new JsonAdapter<T>() {
            @Override
            public Class<T> getType() {
                return type;
            }

            @Override
            public T deserialize(Json json, JsonHelper helper) {
                return deserializer.deserialize(json, helper);
            }

            @Override
            public Json serialize(T obj, JsonHelper helper) {
                throw JsonHelperException.COULD_NOT_SERIALIZE.create(obj,type);
            }
        };
    }

    Class<T> getType();

    class DefaultAdapter<T> implements JsonAdapter<T> {
        private final Class<T> type;

        public DefaultAdapter(Class<T> type) {
            this.type = type;
        }

        @Override
        public Json serialize(T obj, JsonHelper helper) {
            try {
                return PojoHelper.serializeObject(obj, helper.newObject(), Json::set);
            } catch (IllegalAccessException e) {
                throw JsonHelperException.SERIALIZATION_ERROR.create(type);
            }
        }

        @Override
        public T deserialize(Json json, JsonHelper helper) {
            try {
                return PojoHelper.deserializeObject(type, json.keys(), json::get);
            } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                throw JsonHelperException.DESERIALIZATION_ERROR.create(json,type).causedBy(e);
            }
        }

        @Override
        public Class<T> getType() {
            return type;
        }
    }
}
