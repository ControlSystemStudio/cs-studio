/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.util.Objects;
import org.epics.pvmanager.ReadFunction;

/**
 *
 * @author carcassi
 */
public class ReadFunctionArgument<T> {
    
    private T value;
    private final ReadFunction<T> function;
    private boolean changed;

    public ReadFunctionArgument(ReadFunction<T> function) {
        this.function = function;
    }

    public ReadFunctionArgument() {
        this(null);
    }
    
    public boolean isDefined() {
        return function != null;
    }
    
    public boolean isMissing() {
        return isDefined() && getValue() == null;
    }
    
    public void readNext() {
        if (function != null) {
            T oldValue = value;
            value = function.readValue();
            changed = !Objects.equals(oldValue, value);
        } else {
            changed = false;
        }
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public T getValue() {
        return value;
    }
    
}
