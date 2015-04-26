/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

/**Use this adapter to provide widget specific information to other widgets.
 * Sometimes a widget may want to know the information of another widget. For example,
 * the array widget want to know which property should be unique for each child.
 * Use this adapter can help to decouple their strong connections. They only need to know the same key value. 
 * @author Xihui Chen
 *
 */
public interface IWidgetInfoProvider {
	
	public Object getInfo(String key);

}
