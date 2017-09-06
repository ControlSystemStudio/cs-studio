/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Display;

/**
 * Tooltip label which will show the latest tooltip value from its widget model.
 *
 * @author Xihui Chen
 *
 */
public class TooltipLabel extends Figure {

    private AbstractWidgetModel widgetModel;
    private AbstractBaseEditPart editPart;
    private String tooltipText;

    public TooltipLabel(AbstractWidgetModel widgetModel) {
        this.widgetModel = widgetModel;
    }

    public TooltipLabel(AbstractBaseEditPart editPart) {
        this.widgetModel = editPart.getWidgetModel();
        this.editPart = editPart;
    }

    @Override
    protected void paintClientArea(Graphics graphics) {
        super.paintClientArea(graphics);
        if (widgetModel == null) {
            return;
        }
        if (tooltipText == null) {
            tooltipText = getConnectionText() + widgetModel.getTooltip();
        }
        graphics.drawText(tooltipText, 1, 1);
    }

    @Override
    public Dimension getPreferredSize(int wHint, int hHint) {

        if (widgetModel == null) {
            return new Dimension(wHint, hHint);
        }
        tooltipText = getConnectionText() + widgetModel.getTooltip();
        return FigureUtilities.getTextExtents(tooltipText, Display.getDefault().getSystemFont()).expand(2, 2);
    }

    private String getConnectionText(){
        if (editPart == null || editPart.getConnectionHandler() == null || editPart.getConnectionHandler().getToolTipText() == null){
        return "";
        }
        return editPart.getConnectionHandler().getToolTipText();
    }

}
