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
import org.eclipse.draw2d.geometry.PointList;


/**The test for widgets inherited from {@link Shape}
 * @author Xihui Chen
 *
 */
public abstract class AbstractPolyWidgetTest extends AbstractShapeWidgetTest {

    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        String[] shapeProps = new String[]{
                "points",

        };
        return concatenateStringArrays(superProps, shapeProps);
    }


    @Override
    public Object generateTestData(PropertyDescriptor pd, Object seed) {
        if(pd.getName().equals("points")){
                if(seed !=null && seed instanceof Integer){
                    int size = (Integer)seed;
                    PointList pl = new PointList();
                    for(int i=0; i<size%50; i++){
                        pl.addPoint((int) (Math.random()*300),(int) (Math.random()*300));
                    }
                    return pl;
                }else
                    return new PointList(new int[]{12,23,32,12,45,32,109,67,89,65});
        }else if(pd.getName().equals("transparent"))
                if(seed !=null && seed instanceof Integer){
                    return super.generateTestData(pd, (Integer)seed  +1);
        }

        return super.generateTestData(pd, seed);
    }

}
