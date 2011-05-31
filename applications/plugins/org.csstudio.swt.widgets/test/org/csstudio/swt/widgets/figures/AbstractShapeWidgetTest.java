/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import java.beans.PropertyDescriptor;

import org.eclipse.draw2d.Shape;
import org.eclipse.swt.SWT;


/**The test for widgets inherited from {@link Shape}
 * @author Xihui Chen
 *
 */
public abstract class AbstractShapeWidgetTest extends AbstractWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		String[] shapeProps = new String[]{
				"alpha",
				"lineCap",
				"lineStyle",
				"lineWidth"
		};
		return concatenateStringArrays(superProps, shapeProps);
	}
	
	
	@Override
	public Object generateTestData(PropertyDescriptor pd, Object seed) {				
		if(pd.getName().equals("lineCap")){
				if(seed !=null && seed instanceof Integer){	
					if(((Integer)seed)%3 == 0)
						return SWT.CAP_FLAT;
					else if((((Integer)seed)%3 == 1))
						return SWT.CAP_ROUND;
					else if((((Integer)seed)%3 == 2))
						return SWT.CAP_SQUARE;
				}else
					return SWT.CAP_FLAT;
		}else if(pd.getName().equals("lineStyle")){
			if(seed !=null && seed instanceof Integer){	
				return (Integer)seed%5+1;		
			}else
				return SWT.LINE_SOLID;
		}else if(pd.getName().equals("lineWidth")){
			if(seed !=null && seed instanceof Integer){	
				return (Integer)seed%20;		
			}
		}
		
		return super.generateTestData(pd, seed);
	}
}
