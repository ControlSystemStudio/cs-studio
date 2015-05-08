/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class TabTest extends AbstractWidgetTest{

    @Override
    public Figure createTestWidget() {
        TabFigure tabFigure = new TabFigure();
        tabFigure.addTab("Tab 0");
        tabFigure.addTab("Tab 1");
        tabFigure.addTab("Tab 2");
        tabFigure.addTab("Tab 3");

        return tabFigure;
    }


    @Override
    public String[] getPropertyNames() {
        String[] superProps =  super.getPropertyNames();
        String[] myProps = new String[]{
                "activeTabIndex"
        };

        return concatenateStringArrays(superProps, myProps);
    }

    @Override
    public boolean isAutoTest() {
        return true;
    }

}
