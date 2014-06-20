/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.graphene;

import org.epics.util.array.ListDouble;
import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public class ListDoubleView extends ListDouble {
    
    private ListNumber list;

    public ListDoubleView(ListNumber list) {
        this.list = list;
    }

    @Override
    public double getDouble(int index) {
        return list.getDouble(index);
    }

    @Override
    public int size() {
        return list.size();
    }
    
}
