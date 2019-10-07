package com.shinybunny.utils.db;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.db.annotations.Select;
import com.shinybunny.utils.db.annotations.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * The DalMethod class defines a behaviour for a {@link Dal} method annotated with <code>A</code>.<br/>
 * Using a {@link Proxy}, an instance of the DAL interface is created using {@link Database#createDal(Class)}.
 * This object is created for each method in a DAL class, according to the method's annotation.<br/>
 * When the represented Method in the Proxy instance is called, the {@link java.lang.reflect.InvocationHandler} (the Dal class)
 * calls {@link #invoke(Object[])} for this method handler.
 * @param <A> The annotation type a dal method needs in order to use this DalMethod.
 */
public abstract class DalMethod<A extends Annotation> {

    private static List<MethodEntry<?>> dalMethods = new ArrayList<>();

    private static final ExceptionFactory UNKNOWN_MODEL_CLASS_EXCEPTION = ExceptionFactory.make("Type ${type} is not a model class. In dal method ${method}");

    static {
        registerBuiltin();
    }

    private static void registerBuiltin() {
        register(Select.class,Select.Handler::new);
        register(Update.class,Update.Handler::new);
    }

    /**
     * Register a new, custom DalMethod entry.<br/>
     * @param annotationType The annotation type to bind to this entry
     * @param constructor The {@link #DalMethod(Database, Method, Annotation)} constructor for creating a DalMethod for a method in a DAL class
     * @param <A> Annotation type parameter
     */
    public static <A extends Annotation> void register(Class<A> annotationType, Constructor<A> constructor) {
        dalMethods.add(new MethodEntry<>(annotationType, constructor));
    }

    protected final A annotation;
    protected Database database;
    protected Method method;
    public Model model;

    /**
     * Creates a new DalMethod.
     * @param database The database this method's dal belongs to.
     * @param method The actual method instance this handler is implementing
     * @param annotation The annotation instance that made the dal use this method.
     */
    public DalMethod(Database database, Method method, A annotation) {
        this.database = database;
        this.method = method;
        this.annotation = annotation;
    }

    /**
     * Gets or infers the model class. The class is then searched for a matching {@link Model} object.<br/>
     * This method is called right after the constructor, and before {@link #postInit()}.
     * @return The class of the model object
     */
    public abstract Class<?> getModelClass();

    private void initModel() {
        if (model != null) return;
        Class<?> modelClass = this.getModelClass();
        if (modelClass == null) {
            throw UNKNOWN_MODEL_CLASS_EXCEPTION.create(null,method);
        }
        Model model = database.getModel(modelClass);
        if (model == null) {
            throw UNKNOWN_MODEL_CLASS_EXCEPTION.create(modelClass,method);
        }
        this.model = model;
    }

    /**
     * Initializes all constant logic for this method and saves it for multiple uses.<br/>
     * Called after {@link #model} is set to the {@link Model} object of the returned class from {@link #getModelClass()}.
     */
    public abstract void postInit();

    /**
     * Invokes the method to the database. Called from the {@link java.lang.reflect.InvocationHandler} of the Dal proxy class.
     * @param args The values passed to the {@link #method} this object represents.
     * @return The return value of the represented method.
     */
    public abstract Object invoke(Object[] args);

    public static DalMethod<?> create(Database db, Method m) {
        for (MethodEntry<?> e : dalMethods) {
            DalMethod<?> dm = e.construct(db,m);
            if (dm != null) return dm;
        }
        return null;
    }

    private static class MethodEntry<A extends Annotation> {

        private final Class<A> annotationType;
        private final Constructor<A> constructor;

        public MethodEntry(Class<A> annotationType, Constructor<A> constructor) {

            this.annotationType = annotationType;
            this.constructor = constructor;
        }

        public DalMethod<A> construct(Database db, Method m) {
            A a = m.getAnnotation(annotationType);
            if (a == null) return null;
            DalMethod<A> dm = constructor.construct(db,m,a);
            dm.initModel();
            dm.postInit();
            return dm;
        }
    }

    @FunctionalInterface
    public interface Constructor<A extends Annotation> {

        DalMethod<A> construct(Database db, Method m, A annotation);

    }
}
