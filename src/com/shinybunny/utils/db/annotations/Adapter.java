package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.MethodRef;
import com.shinybunny.utils.reflection.Reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Adapter {

    MethodRef serializer();

    MethodRef deserializer();

    class Helper {

        private static final ExceptionFactory MULTIPLE_METHODS_FOUND = ExceptionFactory.make("Multiple methods found in ${class} with the name ${name}, please provide parameter types to @Adapter.");
        private static final ExceptionFactory NO_METHOD_FOUND = ExceptionFactory.make("No method named ${name} found in ${class} for @Adapter.");

        public static Object serialize(Field f, Object holder) {
            Object value = Reflection.get(f,holder);
            if (f.isAnnotationPresent(Adapter.class)) {
                MethodRef method = f.getAnnotation(Adapter.class).serializer();
                return invokeMethod(f.getType(),value,method);
            }
            return value;
        }

        public static Object deserialize(Field f, Object obj) {
            if (f.isAnnotationPresent(Adapter.class)) {
                MethodRef method = f.getAnnotation(Adapter.class).deserializer();
                return invokeMethod(f.getType(),null,method,obj);
            }
            return obj;
        }

        private static Object invokeMethod(Class<?> defType, Object instance, MethodRef ref, Object... args) {
            String name = ref.value();
            Class<?> holder = defType;
            Object holderInstance = instance;
            if (ref.clazz() != Object.class) {
                holder = ref.clazz();
                holderInstance = null;
            }
            Method method = null;
            if (ref.params().length == 0) {
                for (Method m : holder.getDeclaredMethods()) {
                    if (m.getName().equals(name)) {
                        if (method != null) {
                            throw MULTIPLE_METHODS_FOUND.create(holder,name);
                        }
                        method = m;
                    }
                }
            } else {
                try {
                    method = holder.getDeclaredMethod(name,ref.params());
                } catch (NoSuchMethodException ignored) {}
            }
            if (method == null) {
                throw NO_METHOD_FOUND.create(name,holder);
            }
            try {
                Object[] a = instance == null ? args : holderInstance == null ? new Object[]{instance} : new Object[0];
                return method.invoke(holderInstance,a);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }


    }

}
