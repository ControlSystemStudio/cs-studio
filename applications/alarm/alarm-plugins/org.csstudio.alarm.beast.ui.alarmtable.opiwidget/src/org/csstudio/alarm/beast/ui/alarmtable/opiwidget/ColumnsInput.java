/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import java.util.Arrays;

import org.csstudio.alarm.beast.ui.alarmtable.ColumnWrapper;
import org.csstudio.alarm.beast.ui.alarmtable.Preferences;

/**
 * <code>ColumnsInput</code> is a wrapper for the {@link ColumnWrapper} used in combination with
 * the {@link ColumnsProperty}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
class ColumnsInput {

    private ColumnWrapper[] columns;

    /**
     * Constructs a new columns input object using the preferences to initialise the underlying column wrapper array.
     */
    ColumnsInput() {
        this(ColumnWrapper.fromSaveArray(Preferences.getColumns()));
    }

    /**
     * Constructs a new columns input object that wraps the given array.
     * @param w the array to wrap
     */
    ColumnsInput(ColumnWrapper[] w) {
        setColumns(w);
    }

    /**
     * Set the array to wrap.
     *
     * @param columns the columns
     */
    void setColumns(ColumnWrapper[] columns) {
        this.columns = columns;
    }

    /**
     * @return the wrapped columns array
     */
    ColumnWrapper[] getColumns() {
        return columns;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Arrays.toString(columns);
    }
}
