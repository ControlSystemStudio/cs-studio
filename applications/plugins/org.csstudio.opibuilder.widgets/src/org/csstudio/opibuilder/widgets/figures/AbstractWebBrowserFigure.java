/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.browser.Browser;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWebBrowserFigure extends AbstractSWTWidgetFigure {	
	
	public AbstractWebBrowserFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}

	public abstract void setUrl(String url);

	@Override
	protected void paintClientArea(Graphics graphics) {			
		//draw this so that it can be seen in the outline view
		if(!runmode){
			graphics.setBackgroundColor(
					CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE));
			graphics.fillRectangle(getClientArea());
		}
		super.paintClientArea(graphics);	
	}
	
	public abstract Browser getBrowser() ;
	
}
