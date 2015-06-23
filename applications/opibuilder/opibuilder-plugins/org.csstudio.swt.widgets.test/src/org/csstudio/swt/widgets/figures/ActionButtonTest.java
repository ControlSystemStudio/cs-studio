/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;


public class ActionButtonTest extends AbstractWidgetTest{

    @Override
    public Figure createTestWidget() {
        return new ActionButtonFigure();
    }


    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        List<String> superPropList = new ArrayList<String>();
        for(String p : superProps){
            if(!p.equals("opaque"))
                superPropList.add(p);
        }
        String[] myProps = new String[]{
                "toggleStyle",
                "textAlignment",
                "imagePath",
                "text",
                "runMode"
        };

        return concatenateStringArrays(superPropList.toArray(new String[]{}), myProps);
    }

    @Override
    public boolean isAutoTest() {
        return true;
    }


}
