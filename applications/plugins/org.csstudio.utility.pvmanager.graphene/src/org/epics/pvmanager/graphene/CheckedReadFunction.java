/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
public class CheckedReadFunction<T> implements ReadFunction<T> {
    
    private final Class<T> clazz;
    private final Class<? extends T>[] classes;
    private final ReadFunction<?> arg;
    private final String argName;

    public CheckedReadFunction(ReadFunction<?> arg, String argName, Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Class can't be null");
        }
        this.clazz = clazz;
        this.classes = null;
        this.arg = arg;
        this.argName = argName;
    }

    @SafeVarargs
    public CheckedReadFunction(ReadFunction<?> arg, String argName, Class<? extends T>... classes) {
        if (classes == null) {
            throw new NullPointerException("Classes can't be null");
        }
        this.clazz = null;
        this.classes = classes;
        this.arg = arg;
        this.argName = argName;
    }
    
    @Override
    public T readValue() {
        Object obj = arg.readValue();
        if (obj == null) {
            return null;
        }

        if (clazz != null) {
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            } else {
                throw new RuntimeException(argName + " must be a " + clazz.getSimpleName() + " (was " + ValueUtil.typeOf(obj).getSimpleName() + ")");
            }
        } else {
            for (Class<? extends T> aClass : classes) {
                if (aClass.isInstance(obj)) {
                    return aClass.cast(obj);
                }
            }
            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<>();
            for (Class<? extends T> aClass : classes) {
                names.add(aClass.getSimpleName());
            }
            throw new RuntimeException(argName + " must be one of " + names.toString() + " (was " + ValueUtil.typeOf(obj).getSimpleName() + ")");
        }
    }
}
