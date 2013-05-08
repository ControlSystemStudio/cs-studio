/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.graphene;

import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
public class CheckedReadFunction<T> implements ReadFunction<T> {
    
    private final Class<T> clazz;
    private final ReadFunction<?> arg;
    private final String argName;

    public CheckedReadFunction(Class<T> clazz, ReadFunction<?> arg, String argName) {
        this.clazz = clazz;
        this.arg = arg;
        this.argName = argName;
    }
    
    @Override
    public T readValue() {
        Object obj = arg.readValue();
        if (obj == null) {
            return null;
        }

        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            throw new RuntimeException(argName + " must be a " + clazz.getSimpleName() + " (was " + ValueUtil.typeOf(obj).getSimpleName() + ")");
        }
    }
}
