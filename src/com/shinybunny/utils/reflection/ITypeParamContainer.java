package com.shinybunny.utils.reflection;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public interface ITypeParamContainer {

    default Map<String, TypeParameter> getTypeParameters() {
        Map<String,TypeParameter> parameters = new HashMap<>();
        for (TypeVariable<?> tv : _typeVariables()) {
            parameters.put(tv.getName(),new TypeParameter(tv));
        }
        return parameters;
    }

    default TypeParameter getTypeParameter() {
        if (_typeVariables().length > 0) {
            return new TypeParameter(_typeVariables()[0]);
        }
        return null;
    }

    TypeVariable<?>[] _typeVariables();

}
