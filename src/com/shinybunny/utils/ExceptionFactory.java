package com.shinybunny.utils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>ExceptionFactory</code> class is used to create exception templates for re-use of exceptions.<br/>
 * This API allows the use of parameters in the exception message, making it simple to format a message with the context of the exception.<br/>
 * To create an ExceptionFactory instance, use one of the constructors, or just use {@link ExceptionFactory#make(String, String...)}
 */
public class ExceptionFactory {

    private static final String ARG_PATTERN = "\\$\\{(\\w+)}";

    private final Constructor constructor;
    private final String[] fields;
    private String msg;
    private Map<String, Function<Object, String>> converters;
    private Map<String, Function<ArgumentGetter, Object>> lazyEvals;

    public ExceptionFactory(String msg) {
        this(msg,ResultException::new);
    }

    public ExceptionFactory(String msg, Constructor constructor) {
        this(msg,constructor,new String[0]);
    }

    public ExceptionFactory(String msg, Constructor constructor, String... fields) {
        this.constructor = constructor;
        this.msg = msg;
        this.fields = fields;
        converters = new HashMap<>();
        lazyEvals = new HashMap<>();
    }

    public <T> ExceptionFactory convert(String argName, Function<T,String> toString) {
        this.converters.put(argName,obj-> toString.apply((T) obj));
        return this;
    }

    public ExceptionFactory lazyEval(String argName, Function<ArgumentGetter,Object> function) {
        this.lazyEvals.put(argName,function);
        return this;
    }

    public ResultException create() {
        return constructor.create(this, new HashMap<>());
    }

    public ResultException create(Object... paramsInOrder) {
        List<Object> list = new ArrayList<>(Arrays.asList(paramsInOrder));
        Map<String,Object> params = new HashMap<>();
        String s = msg;
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            Matcher m = Pattern.compile(ARG_PATTERN).matcher(s);
            if (m.find()) {
                String name = m.group(1);
                params.put(name,o);
                s = s.substring(m.end());
                list.remove(i);
                i--;
            } else {
                break;
            }
        }
        if (!list.isEmpty()) {
            int i = 0;
            for (String f : fields) {
                params.put(f,list.get(i));
                i++;
                if (i >= list.size()) break;
            }
        }
        return constructor.create(this,params);
    }

    /**
     * Constructs a new ExceptionFactory with the specified message. Use the pattern ${name} in the message to add arguments.<br/>
     * Those arguments can be later bound by passing their values in chronological order in the {@link #create(Object...)} method,
     * or by their name in {@link ResultException#set(String, Object)}
     * @param msg The message to use in the exception
     * @param fields (Optional) fields for detailed parameters (will be added in new lines after the main message). Bind them to values later using {@link ResultException#set(String, Object)}.
     * @return A new Exception factory for future use.
     */
    public static ExceptionFactory make(String msg, String... fields) {
        return new ExceptionFactory(msg,ResultException::new,fields);
    }

    public static class ResultException extends RuntimeException {
        private final ExceptionFactory factory;

        private Map<String, Function<Object, String>> converters;
        private String msg;
        private Map<String,Object> params;
        private Map<String,Object> fields;

        public ResultException(ExceptionFactory factory, Map<String, Object> params) {
            this.factory = factory;
            this.msg = factory.msg;
            this.converters = factory.converters;
            this.params = params;
            for (Map.Entry<String, Function<ArgumentGetter, Object>> lazy : factory.lazyEvals.entrySet()) {
                Object o = lazy.getValue().apply(new ArgumentGetter() {
                    @Override
                    public <T> T get(String name, Class<T> t) {
                        Object o = params.get(name);
                        if (t.isAssignableFrom(o.getClass())) {
                            return (T) o;
                        }
                        return null;
                    }
                });
                params.put(lazy.getKey(),o);
            }
            fields = new HashMap<>();
            for (String f : factory.fields) {
                Object o = params.remove(f);
                fields.put(f,o);
            }
            for (Map.Entry<String,Object> p : params.entrySet()) {
                replace(p.getKey(),p.getValue());
            }
        }

        public ResultException set(String arg, Object value) {
            if (fields.containsKey(arg)) {
                fields.put(arg,value);
            } else {
                replace(arg, value);
            }
            params.put(arg, value);
            return this;
        }

        private void replace(String arg, Object value) {
            msg = msg.replaceAll("\\$\\{" + arg + "}",convert(arg,value));
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public ExceptionFactory getFactory() {
            return factory;
        }

        public Object get(String arg) {
            return params.get(arg);
        }

        private String convert(String arg, Object value) {
            if (converters.containsKey(arg)) {
                return converters.get(arg).apply(value);
            }
            return value.toString().replaceAll("\\$","\\\\\\$");
        }

        @Override
        public String getMessage() {
            StringBuilder msg = new StringBuilder(this.msg);
            for (Map.Entry<String,Object> e : fields.entrySet()) {
                msg.append("\n").append(e.getKey()).append(": ").append(convert(e.getKey(), e.getValue()));
            }
            return msg.toString();
        }

        public boolean instanceOf(ExceptionFactory exception) {
            return factory == exception;
        }

        public static ExceptionFactory factory(String msg, Constructor constructor) {
            return new ExceptionFactory(msg,constructor);
        }

        public ResultException causedBy(Throwable e) {
            initCause(e);
            return this;
        }
    }

    @FunctionalInterface
    public interface Constructor {
        ResultException create(ExceptionFactory factory, Map<String,Object> params);
    }


    @FunctionalInterface
    public interface ArgumentGetter {

        <T> T get(String name, Class<T> t);

    }

}
