/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figureparts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;

/**
 * A label whose background could be set with alpha. 
 * Alpha may range from 0 to 255. A value of 0 is completely transparent
 * @author Xihui Chen
 *
 */
public class AlphaLabel extends Label {

	private int alpha = 100;	

	@Override
	public boolean isOpaque() {		
		return false;
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		graphics.pushState();
		graphics.setAlpha(alpha);
		graphics.fillRectangle(bounds);
		graphics.popState();
		super.paintFigure(graphics);
	}
	
	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
}
