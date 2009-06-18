/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Adapter to provide property support for {@link BasicMessage}s. 
 * (According to the implementation of Joerg Rathlev.)
 *  
 * @author Jan Hatje
 */
public class MessagePropertySource implements IPropertySource2 {

	/**
	 * The node for which this property source provides properties.
	 */
	private BasicMessage _message;
	

	
	/**
	 * Creates a new property source for the given node.
	 * @param node the node.
	 */
	public MessagePropertySource(final BasicMessage message) {
		this._message = message;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final Object getEditableValue() {
		// not editable
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IPropertyDescriptor[] getPropertyDescriptors() {
		HashMap<String, String> propertyValueList = _message.getHashMap();
		Set<String> propertyList = propertyValueList.keySet();
		IPropertyDescriptor[] descriptor = new IPropertyDescriptor[propertyList.size()];
		int i = 0;
		for (String property : propertyList) {
			descriptor[i++] = new PropertyDescriptor(property, property);
		}
		return descriptor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final Object getPropertyValue(final Object id) {
		if (id instanceof String) {
			String result;
			HashMap<String, String> propertyValueList = _message.getHashMap();
			result = propertyValueList.get(id);
			return result;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isPropertySet(final Object id) {
		if (id instanceof String) {
			String result;
			HashMap<String, String> propertyValueList = _message.getHashMap();
			result = propertyValueList.get(id);
			if (result != null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void resetPropertyValue(final Object id) {
		//no reset possible.
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setPropertyValue(final Object id, final Object value) {
		//property can not be changed
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isPropertyResettable(final Object id) {
		return false;
	}
}
