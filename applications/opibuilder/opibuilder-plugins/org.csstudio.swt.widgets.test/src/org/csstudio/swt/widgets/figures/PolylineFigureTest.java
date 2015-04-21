/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.eclipse.draw2d.Figure;


/**
 * @author Xihui Chen
 *
 */
public class PolylineFigureTest extends AbstractPolyWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new PolylineFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"antiAlias",
				"horizontalFill",
				"fill",
				"transparent",
				"fillArrow",
				"arrowLineLength",
				"arrowType"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}		
	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {
		if(pd.getName().equals("arrowType") && seed !=null && seed instanceof Integer)
				return PolylineFigure.ArrowType.values()[(Integer)seed%4];
		
		return super.generateTestData(pd, seed);
	}
}
