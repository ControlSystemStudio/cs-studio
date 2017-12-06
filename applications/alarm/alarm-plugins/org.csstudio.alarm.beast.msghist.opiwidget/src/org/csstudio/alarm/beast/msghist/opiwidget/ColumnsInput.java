/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import java.util.Arrays;

import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;

/**
 * Wrapper around a {@link PropertyColumnPreference} array that provides clean string representation used in OPI widget's property
 * editor.
 *
 * @author Borut Terpinc
 *
 */
public class ColumnsInput {
    private PropertyColumnPreference[] columns;

    public ColumnsInput(PropertyColumnPreference[] columns) {
        super();
        this.columns = columns;
    }

    public PropertyColumnPreference[] getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return Arrays.toString(columns);
    }
}
