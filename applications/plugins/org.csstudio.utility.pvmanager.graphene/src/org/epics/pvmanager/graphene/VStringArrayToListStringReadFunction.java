/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import java.util.List;
import org.epics.pvmanager.ReadFunction;
import org.epics.vtype.VStringArray;

/**
 *
 * @author carcassi
 */
public class VStringArrayToListStringReadFunction implements ReadFunction<List<String>> {
    
    private final ReadFunction<VStringArray> function;

    public VStringArrayToListStringReadFunction(ReadFunction<VStringArray> function) {
        this.function = function;
    }

    @Override
    public List<String> readValue() {
        VStringArray value = function.readValue();
        if (value == null) {
            return null;
        } else {
            return value.getData();
        }
    }

}
