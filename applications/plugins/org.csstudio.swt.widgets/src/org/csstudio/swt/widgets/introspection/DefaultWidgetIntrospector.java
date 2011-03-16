/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.introspection;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.Figure;

/**The default widget introspector, which will filter out the non widget properties from {@link Figure}.
 * @author Xihui Chen
 *
 */
public class DefaultWidgetIntrospector {
	public static String[] FIGURE_NON_PROPERTIES = new String[]{
		"children",
		"class",
		"clientArea",
		"coordinateSystem",
		"clippingStrategy",
		"focusTraversable",
		"insets",
		"layoutManager",
		"localBackgroundColor",
		"localForegroundColor",
		"maximumSize",
		"minimumSize",
		"mirrored",
		"parent",
		"preferredSize",
		"requestFocusEnabled",
		"toolTip",
		"showing",
		"updateManager",
		"valid",
		"beanInfo"		
	};

	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		Introspector.flushFromCaches(beanClass);
		BeanInfo bi = Introspector.getBeanInfo(beanClass);
		BeanDescriptor bd = bi.getBeanDescriptor();
		MethodDescriptor mds[] = bi.getMethodDescriptors();
		EventSetDescriptor esds[] = bi.getEventSetDescriptors();
		PropertyDescriptor pds[] = bi.getPropertyDescriptors();

		List<PropertyDescriptor> filteredPDList = new ArrayList<PropertyDescriptor>();
		
		List<String> nonPropList = Arrays.asList(getNonProperties());
		for(PropertyDescriptor pd : pds){
			if(!nonPropList.contains(pd.getName()) && pd.getWriteMethod() != null && pd.getReadMethod() != null)
				filteredPDList.add(pd);
		}
		
		int defaultEvent = bi.getDefaultEventIndex();
		int defaultProperty = bi.getDefaultPropertyIndex();

	     return new GenericBeanInfo(bd, esds, defaultEvent, 
	    		 filteredPDList.toArray(new PropertyDescriptor[filteredPDList.size()]),
				defaultProperty, mds, null);
		
	}
	
	public String[] getNonProperties(){
		return FIGURE_NON_PROPERTIES;
	}

	public String[] concatenateStringArrays(String[] A, String[] B){
		String[] C= new String[A.length+B.length]; 
		   System.arraycopy(A, 0, C, 0, A.length); 
		   System.arraycopy(B, 0, C, A.length, B.length); 		 
		   return C;
	}
	
}
