/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import org.csstudio.swt.widgets.figures.SashContainerFigure.SashStyle;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.LineBorder;

/**
 * @author Xihui Chen
 *
 */
public class SashContainerTest extends AbstractWidgetTest {

    @Override
    public Figure createTestWidget() {
        final SashContainerFigure sashContainer = new SashContainerFigure();
        sashContainer.setForegroundColor(ColorConstants.buttonDarker);
        sashContainer.setBackgroundColor(ColorConstants.lightBlue);
        sashContainer.setBorder(new LineBorder());
        sashContainer.setSashWidth(3);
        sashContainer.setHorizontal(true);
        sashContainer.setSashPosition(0.2);
        sashContainer.setSashStyle(SashStyle.RIDGED);
        sashContainer
                .addLayoutListener(new LayoutListener.Stub(){

                    @Override
                    public void postLayout(IFigure container) {
                        System.out.println(sashContainer.getSashPosition()
                                + " " + sashContainer.getSubPanelsBounds()[0]
                                + sashContainer.getSubPanelsBounds()[1]);
                    }

                });
        return sashContainer;
    }

    @Override
    public String[] getPropertyNames() {
        String[] superProps = super.getPropertyNames();
        String[] myProps = new String[] { "sashPosition", "horizontal",
                "sashWidth", "sashStyle" };

        return concatenateStringArrays(superProps, myProps);
    }

    @Override
    public boolean isAutoTest() {
        return false;
    }

}
