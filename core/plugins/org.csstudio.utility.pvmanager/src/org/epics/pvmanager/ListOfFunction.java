/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.Function;
import java.util.ArrayList;
import java.util.List;

/**
 * A function that takes many inputs and creates a list with them.
 *
 * @author carcassi
 */
class ListOfFunction extends Function<List> {

    List<Function> functions;

    public ListOfFunction(List<Function> functions) {
        this.functions = functions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List getValue() {
        List list = new ArrayList();
        for (Function function : functions) {
            list.add(function.getValue());
        }
        return list;
    }

}
