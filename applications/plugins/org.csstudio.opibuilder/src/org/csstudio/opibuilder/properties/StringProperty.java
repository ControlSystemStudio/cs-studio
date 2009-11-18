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

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.support.MultiLineTextPropertyDescriptor;
import org.csstudio.opibuilder.util.MacrosUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

/**The widget property for string. It also accept macro string $(macro).
 * @author Xihui Chen, Sven Wende (part of the code is copied from SDS)
 *
 */
public class StringProperty extends AbstractWidgetProperty {
	
	private boolean multiLine;

	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		this(prop_id, description, category, defaultValue, false);
	}
	
	public StringProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue, boolean multiLine) {
		super(prop_id, description, category, defaultValue);
		this.multiLine = multiLine;
	}
	

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		
		String acceptedValue = null;

		if (value instanceof String) 
			acceptedValue = (String) value;
		else
			acceptedValue = value.toString();
		
		
		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(multiLine)
			return new MultiLineTextPropertyDescriptor(prop_id, description);
		else
			return new TextPropertyDescriptor(prop_id, description);
	}

	@Override
	public void writeToXML(Element propElement) {		
		String reShapedString = 
			getPropertyValue().toString().replaceAll("\\x0D\\x0A?", new String(new byte[]{13,10}));
		propElement.setText(reShapedString);
	}
	


	@Override
	public Object readValueFromXML(Element propElement) {
		return propElement.getValue();
	}
	
	@Override
	public Object getPropertyValue() {
		if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE)
			return MacrosUtil.replaceMacros(
					widgetModel, (String) super.getPropertyValue());
		else
			return super.getPropertyValue();
	}
	
	
	
	
	
	

}
