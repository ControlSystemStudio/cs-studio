/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;


/**
 * A property, which is able to handle Double values.
 * 
 * @author Xihui Chen, Sven Wende (similar class as in SDS)
 * 
 */
public final class DoubleProperty extends AbstractWidgetProperty {


	/**
	 * Lower border for the property value.
	 */
	private double min;

	/**
	 * Upper border for the property value.
	 */
	private double max;
	
	/**Double Property Constructor. The property value type is double.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 */
	public DoubleProperty(String propId, String description,
			WidgetPropertyCategory category, double defaultValue) {
		super(propId, description, category, Double.valueOf(defaultValue));
		min = -Double.MAX_VALUE;
		max = Double.MAX_VALUE;
	}
	
	
	/**Color Property Constructor. The property value type is {@link OPIColor}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 * @param min the minimum allowed double value.
	 * @param max the maximum allowed double value.
	 */
	public DoubleProperty(String propId, String description,
			WidgetPropertyCategory category, double defaultValue,
			double min, double max) {
		super(propId, description, category, Double.valueOf(defaultValue));
		this.min = min;
		this.max = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object checkValue(final Object value) {
		if(value == null)
			return null;

		Double acceptedValue = null;

		// check type
		if (!(value instanceof Double)) {
			if (value instanceof Number) {
				acceptedValue = ((Number) value).doubleValue();
			} else {
				try {
					acceptedValue = Double.parseDouble(value.toString());
				} catch (NumberFormatException e) {
					acceptedValue = null;
				}
			}
		} else {
			acceptedValue = (Double) value;
		}

		// check borders
		if (acceptedValue != null) {
			if (acceptedValue > max) {
				acceptedValue = max;
			} else if (acceptedValue < min) {
				acceptedValue = min;
			}
		}

		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getDoublePropertyDescriptor(prop_id, description);
	}

	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(getPropertyValue().toString());
	}
	
	@Override
	public Object readValueFromXML(Element propElement) {
		return Double.parseDouble(propElement.getValue());
	}
	
	@Override
	public boolean configurableByRule() {
		return true;
	}
}
