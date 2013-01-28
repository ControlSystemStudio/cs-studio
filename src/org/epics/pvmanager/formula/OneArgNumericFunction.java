/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.ExpressionLanguage;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VDouble;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFactory;


/**
 *
 * @author carcassi
 */
abstract class OneArgNumericFunction implements ExpressionLanguage.OneArgFunction<VDouble, VNumber>  {

    @Override
    public VDouble calculate(VNumber arg) {
        if (arg == null) {
            return null;
        }
        return ValueFactory.newVDouble(calculate(arg.getValue().doubleValue()), ValueFactory.newTime(Timestamp.now()));
    }
    
    abstract double calculate(double arg);
    
}
