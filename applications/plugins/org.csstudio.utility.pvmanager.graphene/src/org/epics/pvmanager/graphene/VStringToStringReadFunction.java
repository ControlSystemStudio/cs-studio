/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VString;

/**
 *
 * @author carcassi
 */
public class VStringToStringReadFunction implements ReadFunction<String> {
    
    private final ReadFunction<VString> function;

    public VStringToStringReadFunction(ReadFunction<VString> function) {
        this.function = function;
    }

    @Override
    public String readValue() {
        VString value = function.readValue();
        if (value == null) {
            return null;
        } else {
            return value.getValue();
        }
    }

}
