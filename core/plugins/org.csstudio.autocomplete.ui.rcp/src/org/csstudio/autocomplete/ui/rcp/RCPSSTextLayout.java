/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.rcp;

import org.csstudio.autocomplete.ui.util.SSTextLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class RCPSSTextLayout extends SSTextLayout {

	private TextLayout textLayout;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Display display, String text) {
		textLayout = new TextLayout(display);
		textLayout.setAlignment(SWT.CENTER);
		textLayout.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addStyle(Font font, Color color, int x, int y) {
		TextStyle textStyle = new TextStyle(font, color, null);
		textLayout.setStyle(textStyle, x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handlePaintItemEvent(Event event, int offsetX, int offsetY) {
		textLayout.draw(event.gc, event.x + offsetX, event.y + offsetY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMeasureItemEvent(Event event) {
		Rectangle textLayoutBounds = textLayout.getBounds();
		event.width = textLayoutBounds.width;
		event.height = textLayoutBounds.height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle getBounds() {
		if (textLayout != null)
			return textLayout.getBounds();
		return null;
	}

}
