/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.PropertyDescriptor;
import java.util.Arrays;


/**
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceFigureTest extends AbstractWidgetTest {

    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();

        String[] myProps = new String[]{
                "selectedColor",
                "state",
                "states",
                "horizontal"
        };
        return concatenateStringArrays(superProps, myProps);
    }


    @Override
    public Object generateTestData(PropertyDescriptor pd, Object seed) {
        if(pd.getName().equals("states"))
            return Arrays.asList("choice 1", "choice 2", "Choice 3");
        return super.generateTestData(pd, seed);
    }

}
