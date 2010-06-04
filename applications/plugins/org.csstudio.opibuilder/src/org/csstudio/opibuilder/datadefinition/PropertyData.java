package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;

/**Hold place for temp property value.
 * @author Xihui Chen
 *
 */
public class PropertyData{
	public AbstractWidgetProperty property;
	public Object tmpValue;
	public PropertyData(AbstractWidgetProperty property, Object value) {
		this.property = property;
		this.tmpValue = value;
	}

}