/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.introspection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**An introspectable object can give its bean information. All CSS widgets in this library
 * must implement this interface. 
 * @author Xihui Chen
 *
 */
public interface Introspectable {

	public BeanInfo getBeanInfo() throws IntrospectionException;
	
}
