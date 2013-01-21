/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.formula;

import org.epics.pvmanager.ExpressionLanguage;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.util.time.Timestamp;


/**
 *
 * @author carcassi
 */
abstract class TwoArgNumericFunction implements ExpressionLanguage.TwoArgFunction<VDouble, VNumber, VNumber>  {

    @Override
    public VDouble calculate(VNumber arg1, VNumber arg2) {
        return ValueFactory.newVDouble(calculate(arg1.getValue().doubleValue(), arg2.getValue().doubleValue()), ValueFactory.newTime(Timestamp.now()));
    }
    
    abstract double calculate(double arg1, double arg2);
    
}
