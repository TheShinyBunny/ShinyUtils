package com.shinybunny.utils.db;

import com.shinybunny.utils.Name;

import java.lang.reflect.*;
import java.util.Collection;

public class Dal implements InvocationHandler {

    private final Database db;

    public Dal(Database db) {
        this.db = db;
    }

    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return the value to return from the method invocation on the
     * proxy instance.  If the declared return type of the interface
     * method is a primitive type, then the value returned by
     * this method must be an instance of the corresponding primitive
     * wrapper class; otherwise, it must be a type assignable to the
     * declared return type.  If the value returned by this method is
     * {@code null} and the interface method's return type is
     * primitive, then a {@code NullPointerException} will be
     * thrown by the method invocation on the proxy instance.  If the
     * value returned by this method is otherwise not compatible with
     * the interface method's declared return type as described above,
     * a {@code ClassCastException} will be thrown by the method
     * invocation on the proxy instance.
     * @throws Throwable the exception to throw from the method
     *                   invocation on the proxy instance.  The exception's type must be
     *                   assignable either to any of the exception types declared in the
     *                   {@code throws} clause of the interface method or to the
     *                   unchecked exception types {@code java.lang.RuntimeException}
     *                   or {@code java.lang.Error}.  If a checked exception is
     *                   thrown by this method that is not assignable to any of the
     *                   exception types declared in the {@code throws} clause of
     *                   the interface method, then an
     *                   {@link UndeclaredThrowableException} containing the
     *                   exception that was thrown by this method will be thrown by the
     *                   method invocation on the proxy instance.
     * @see UndeclaredThrowableException
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Select select = method.getAnnotation(Select.class);
        Delete delete = method.getAnnotation(Delete.class);
        Insert insert = method.getAnnotation(Insert.class);
        Update update = method.getAnnotation(Update.class);

        if (select != null) {
            return select(select,method,args);
        } else if (delete != null) {
            return delete(delete,method,args);
        } else if (insert != null) {
            return insert(insert,method,args);
        } else if (update != null) {
            return update(update,method,args);
        }
        return null;
    }

    private Object update(Update update, Method method, Object[] args) {
        Object model = args[0];
        Table table = db.getTable(model.getClass());
        table.update(db.mapModel(model),Where.test(table.getPrimaryKey().getName(),Operator.EQUALS,db.getPrimaryKey(model)));
        return null;
    }

    private Object insert(Insert insert, Method method, Object[] args) {
        // TODO: 01/06/2019 insert varargs and arrays
        Class<?> type = args[0].getClass();
        Table table = db.getTable(type);
        Object generated = table.insert(db.mapModel(args[0]));
        if (generated != null) {
            Field f = db.getPrimaryKey(type);
            f.setAccessible(true);
            try {
                f.set(args[0], generated);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object delete(Delete delete, Method method, Object[] args) {
        return null;
    }

    private Object select(Select select, Method method, Object[] args) {
        Type model = select.model();
        if (model == Object.class) {
            model = method.getGenericReturnType();
        }
        Class<?> returnType = select.model() == Object.class ? method.getReturnType() : select.model();
        Table table = getTable(model);
        SelectStatement s = table.select();
        if (!select.min().isEmpty()) {
            s.field(Selectors.min(select.min()).as("Singleton"));
        } else if (!select.max().isEmpty()) {
            s.field(Selectors.max(select.max()).as("Singleton"));
        }
        if (select.limit() > 0) {
            if (select.limit() > 1 && !isMultipleData(returnType)) {
                // can't have multiple data with 1 returned data
            }
            s.limit(select.limit());
        }
        for (OrderBy o : select.orderBy()) {
            s.orderBy(o.value(),o.dir());
        }
        Where.Chain chain = Where.chain();
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter p = method.getParameters()[i];
            chain.and(parameterToWhere(p,args[i]));
        }
        s.where(chain);
        QueryResult result = s.execute();
        if (isMultipleData(returnType)) {
            if (returnType.isArray()) {
                return result.getRows().map(r->db.buildModel(r,returnType.getComponentType())).toArray();
            } else {
                ParameterizedType pt = (ParameterizedType)model;
                return result.getRows().map(r->db.buildModel(r, ((Class<?>) pt.getActualTypeArguments()[0]))).toList();
            }
        }
        ResultRow row = result.first();
        if (row.contains("Singleton")) {
            return row.get("Singleton",Double.TYPE);
        }
        return db.buildModel(row, returnType);
    }

    private Where parameterToWhere(Parameter p, Object value) {
        String name = Name.Helper.getName(p);
        if (!p.isNamePresent() && !p.isAnnotationPresent(Name.class)) {

        }
        Compare comp = p.getAnnotation(Compare.class);
        Operator op = Operator.EQUALS;
        if (comp != null) {
            op = comp.value();
        }
        return Where.test(name,op,value);
    }

    private boolean isMultipleData(Class<?> type) {
        return Collection.class.isAssignableFrom(type) || type.isArray();
    }

    private Table getTable(Type type) {
        Class<?> model = getActualModel(type);
        if (model == null) {

        }
        Table table = db.getTable(model);
        if (table == null) {
            // not a data model
        }
        return table;
    }

    private Class<?> getActualModel(Type type) {
        if (type instanceof ParameterizedType) {
            if (Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType)type).getRawType())) {
                return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
            return null;
        }
        if (((Class<?>)type).isArray()) {
            return ((Class<?>) type).getComponentType();
        }
        return (Class<?>) type;
    }
}
