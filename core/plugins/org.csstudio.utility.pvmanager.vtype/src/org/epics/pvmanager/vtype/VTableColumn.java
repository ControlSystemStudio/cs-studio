/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.pvmanager.expression.DesiredRateExpressionList;

/**
 * An expression to build a column for an aggregated vTable.
 *
 * @author carcassi
 */
public class VTableColumn {
    
    private final String name;
    private final DesiredRateExpressionList<?> valueExpressions;

    VTableColumn(String name, DesiredRateExpressionList<?> valueExpressions) {
        this.name = name;
        this.valueExpressions = valueExpressions;
    }

    String getName() {
        return name;
    }

    DesiredRateExpressionList<?> getValueExpressions() {
        return valueExpressions;
    }
    
    
}
