package com.shinybunny.utils.reflection;

import java.lang.reflect.Modifier;

public enum Privacy {
    PRIVATE(Modifier.PRIVATE), PACKAGE_PRIVATE(0), PROTECTED(Modifier.PROTECTED), PUBLIC(Modifier.PUBLIC);

    private final int mod;

    Privacy(int mod) {
        this.mod = mod;
    }

    public static Privacy get(int mod) {
        for (Privacy p : values()) {
            if ((mod & p.mod) != 0) return p;
        }
        return PACKAGE_PRIVATE;
    }
}
