package com.shinybunny.utils.reflection;

import com.shinybunny.utils.Array;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeParameter {

    private TypeVariable<?> variable;

    public TypeParameter(TypeVariable<?> tv) {
        this.variable = tv;
    }

    public String getName() {
        return variable.getName();
    }

    public Constraint getConstraint() {
        Type[] bounds = variable.getBounds();
        if (bounds.length == 0) {
            return Constraint.NONE;
        }
        Type t = bounds[0];
        if (t instanceof WildcardType) {
            Type[] lower = ((WildcardType) t).getLowerBounds();
            Type[] upper = ((WildcardType) t).getUpperBounds();
            if (lower.length == 0) {
                if (upper.length == 0)
                    return Constraint.NONE;
                return Constraint.EXTENDS;
            }
            return Constraint.SUPER;
        }
        return Constraint.EXTENDS;
    }

    public Array<ReflectedType> getConstraintTypes() {
        return new Array<>(variable.getBounds()).map(t->(Class<?>)t).map(ReflectedType::of);
    }

    public enum Constraint {
        EXTENDS, SUPER, NONE;
    }
}
