package com.shinybunny.utils.db.providers;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.db.Database;

public abstract class DatabaseProvider {

    protected final ExceptionFactory UNSUPPORTED_OPERATION_EXCEPTION = ExceptionFactory.make("This database provider doesn't support ${operation}","provider").lazyEval("provider",(getter)->this.getClass());

    public abstract void connect();

    public abstract Database getDatabase(String name);

}
