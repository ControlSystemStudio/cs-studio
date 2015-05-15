/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class TankTest extends AbstractMarkedWidgetTest{

    @Override
    public Figure createTestWidget() {
        return new TankFigure();
    }


    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        String[] myProps = new String[]{
                "fillColor",
                "fillBackgroundColor",
                "effect3D"
        };

        return concatenateStringArrays(superProps, myProps);
    }

    @Override
    public boolean isAutoTest() {
        return true;
    }



}
