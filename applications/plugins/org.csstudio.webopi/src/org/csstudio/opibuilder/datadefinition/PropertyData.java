/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
