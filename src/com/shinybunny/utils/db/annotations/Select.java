package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.IntKeyMap;
import com.shinybunny.utils.Pair;
import com.shinybunny.utils.db.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.shinybunny.utils.ExceptionFactory.make;

/**
 * The <code>@Select</code> annotation is used for dal classes to query the database and automatically adapt to a custom data model.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {

    /**
     * Defines a limit to the number of items to return.
     * Defaults to 0, which selects all results.
     */
    int limit() default 0;

    /**
     * Sorts the results by the specified {@link OrderBy} annotations.
     */
    OrderBy[] orderBy() default {};

    /**
     * Defines what columns to return, defaults to all columns to create a full model.
     */
    String[] column() default "*";

    /**
     * Constant WHERE clauses to apply to the query. Grouped using AND clauses
     */
    Compare[] where() default {};

    /**
     * Constant WHERE groups. All OrGroup are combined with AND, and every Compare in a group is combine with OR.
     */
    OrGroup[] orGroups() default {};

    class Handler extends DalMethod<Select> {


        private static final ExceptionFactory WHERE_PARAM_WITHOUT_NAME_EXCEPTION = make("Parameter '${defName}' in method ${method} has no implicit name, can't map to a model field.");
        private static final ExceptionFactory UNKNOWN_COLUMN_EXCEPTION = make("No such column ${name} exists in model ${type}");
        private Where constantWhere;
        private IntKeyMap<Pair<String, Compare>> compareMap = new IntKeyMap<>();
        private DataSetType resultType;

        public Handler(Database database, Method method, Select annotation) {
            super(database, method, annotation);
            this.resultType = DataSetType.infer(database,method.getReturnType(),method.getGenericReturnType());
        }

        @Override
        public Class<?> getModelClass() {
            return resultType.getComponentType(method);
        }

        @Override
        public void postInit() {
            Compare[] compares = annotation.where();
            if (compares.length == 0) {
                OrGroup[] groups = annotation.orGroups();
                if (groups.length == 0) return;
                Where.Chain chain = Where.chain();
                for (OrGroup g : groups) {
                    Where.Chain orChain = Where.chain();
                    for (Compare c : g.value()) {
                        String col = model.getColumn(c.value());
                        if (col == null) {
                            throw UNKNOWN_COLUMN_EXCEPTION.create(c.value(),model);
                        }
                        orChain.or(Where.test(col,c.op(),c.to()));
                    }
                    chain.and(orChain.build());
                }
                this.constantWhere = chain.build();
            } else {
                Where.Chain chain = Where.chain();
                for (Compare c : compares) {
                    String col = model.getColumn(c.value());
                    if (col == null) {
                        throw UNKNOWN_COLUMN_EXCEPTION.create(c.value(),model);
                    }
                    chain.or(Where.test(col,c.op(),c.to()));
                }
                this.constantWhere = chain.build();
            }
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                String fieldName;
                Compare c = p.getAnnotation(Compare.class);
                if (c != null) {
                    fieldName = c.value();
                } else if (p.isNamePresent()) {
                    fieldName = p.getName();
                } else {
                    throw WHERE_PARAM_WITHOUT_NAME_EXCEPTION.create(p.getName(), method);
                }
                String col = model.getColumn(fieldName);
                if (col == null) {
                    throw UNKNOWN_COLUMN_EXCEPTION.create(fieldName, model);
                }
                compareMap.put(i,Pair.of(col, c));
            }
        }

        public QueryResult getQueryResult(Object[] args) {
            SelectStatement statement = model.getTable().select();
            if (annotation.column().length > 0 && !annotation.column()[0].equals("*")) {
                statement.columns(annotation.column());
            }
            for (OrderBy ob : annotation.orderBy()) {
                statement.orderBy(ob.value(),ob.dir());
            }
            if (annotation.limit() > 0) {
                statement.limit(annotation.limit());
            }
            Where.Chain chain = Where.chain();
            for (int i = 0; i < compareMap.size(); i++) {
                Pair<String,Compare> comp = compareMap.get(i);
                Operator op = Operator.EQUALS;
                if (comp.getSecond() != null) {
                    op = comp.getSecond().op();
                }
                chain.and(Where.test(comp.getFirst(), op, args[i]));
            }
            Where where = chain.build();
            if (where == null) {
                if (constantWhere != null) {
                    statement.where(constantWhere);
                }
            } else {
                if (constantWhere == null) {
                    statement.where(chain.build());
                } else {
                    statement.where(Where.and(constantWhere,where));
                }
            }
            return statement.execute();
        }

        @Override
        public Object invoke(Object[] args) {
            QueryResult result = getQueryResult(args);
            return model.deserialize(result, resultType);
        }
    }
}
