/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Display;

/**Tooltip label which will show the latest tooltip value from its widget model.
 * @author Xihui Chen
 *
 */
public class TooltipLabel extends Figure {
	
	private AbstractWidgetModel widgetModel;
	
	public TooltipLabel(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
	}
	
	@Override
	protected void paintClientArea(Graphics graphics) {		
		super.paintClientArea(graphics);
		graphics.drawText(widgetModel.getTooltip(),1, 1);
	}
	
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return FigureUtilities.getTextExtents(
				widgetModel.getTooltip(), Display.getDefault().getSystemFont()).expand(2,2);
	}
	
}
