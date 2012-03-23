/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * The property which contains a {@link IValue}. This property won't be shown in
 * property view.
 *
 * @author Xihui Chen
 *
 */
public class PVValueProperty extends AbstractWidgetProperty {

	/**The property is used to store pv values. The value type is {@link IValue}.
	 * @param prop_id the property ID.
	 * @param defaultValue the default value.
	 */
	public PVValueProperty(String prop_id, IValue defaultValue) {
		super(prop_id, prop_id, null, defaultValue);
		setVisibleInPropSheet(false);
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		IValue acceptableValue = null;
		if(value instanceof IValue)
			acceptableValue = (IValue) value;
		else if(value instanceof Double){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			acceptableValue = ValueFactory.createDoubleValue(
					TimestampFactory.now(), severity, severity.toString(),
					null, Quality.Original, new double[]{(Double)value});
		}else if(value instanceof String){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			acceptableValue = ValueFactory.createStringValue(
					TimestampFactory.now(), severity, severity.toString(),
					Quality.Original, new String[]{(String)value});
		}else if(value instanceof Long || value instanceof Integer){
	        final ISeverity severity = ValueFactory.createOKSeverity();
			acceptableValue = ValueFactory.createLongValue(
					TimestampFactory.now(), severity, severity.toString(),
					null, Quality.Original, new long[]{(value instanceof Integer? ((Integer)value).longValue():(Long)value)});
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
