/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.jdom.Element;

/**
 * The property which contains a {@link IValue}. This property won't be shown in
 * property view.
 *
 * @author Xihui Chen
 *
 */
public class PVValueProperty extends AbstractWidgetProperty {

    /**The property is used to store pv values. The value type is {@link VType}.
     * @param prop_id the property ID.
     * @param defaultValue the default value.
     */
    public PVValueProperty(String prop_id, VType defaultValue) {
        super(prop_id, prop_id, null, defaultValue);
        setVisibleInPropSheet(false);
    }

    @Override
    public Object checkValue(Object value) {
        if(value == null)
            return null;
        VType acceptableValue = null;
        if(value instanceof VType)
            acceptableValue = (VType) value;
        else if(value instanceof Double || value instanceof Float || value instanceof Long){
            acceptableValue = ValueFactory.newVDouble(
                    (value instanceof Double? (Double)value : (value instanceof Float? (Float)value:(Long)value)));
        }else if(value instanceof String){
            acceptableValue = ValueFactory.newVString(
                    (String)value, ValueFactory.alarmNone(), ValueFactory.timeNow());
        }else if(value instanceof Integer || value instanceof Short
                || value instanceof Boolean
                || value instanceof Byte || value instanceof Character){
            int r = 0;
            //TODO: change it to VLong when VLong is added to VType.
//            if(value instanceof Long)
//                r = (Long)value;
            if(value instanceof Integer)
                r = (Integer)value;
            else if(value instanceof Short)
                r = (Short)value;
            else if(value instanceof Boolean)
                r= ((Boolean)value)?1:0;
            else if(value instanceof Byte)
                r=(Byte)value;
            else if(value instanceof Character)
                r=(Character)value;

            acceptableValue = ValueFactory.newVInt(
                    r, ValueFactory.alarmNone(), ValueFactory.timeNow(), ValueFactory.displayNone());

        }

        return acceptableValue;
    }

    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        return null;
    }

    @Override
    public void writeToXML(Element propElement) {
    }

    @Override
    public Object readValueFromXML(Element propElement) {
        return null;
    }

    @Override
    public boolean configurableByRule() {
        return true;
    }

    @Override
    public boolean onlyAcceptExpressionInRule() {
        return true;
    }

}
