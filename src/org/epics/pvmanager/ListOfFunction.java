/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * A function that takes many inputs and creates a list with them.
 *
 * @author carcassi
 */
class ListOfFunction implements ReadFunction<List> {

    List<ReadFunction> functions;

    public ListOfFunction(List<ReadFunction> functions) {
        this.functions = functions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List readValue() {
        List list = new ArrayList();
        for (ReadFunction function : functions) {
            list.add(function.readValue());
        }
        return list;
    }

}
