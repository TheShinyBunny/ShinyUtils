package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.db.DalMethod;
import com.shinybunny.utils.db.DataSetType;
import com.shinybunny.utils.db.Database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {

    class Handler extends DalMethod<Update> {

        private final Parameter param;
        private DataSetType dataSetType;

        public Handler(Database database, Method method, Update annotation) {
            super(database, method, annotation);
            this.param = method.getParameters()[0];
            this.dataSetType = DataSetType.infer(database,param.getType(),param.getParameterizedType());
        }

        @Override
        public Class<?> getModelClass() {
            return dataSetType.getComponentType(param);
        }

        @Override
        public void postInit() {

        }

        @Override
        public Object invoke(Object[] args) {
            return null;
        }
    }

}
