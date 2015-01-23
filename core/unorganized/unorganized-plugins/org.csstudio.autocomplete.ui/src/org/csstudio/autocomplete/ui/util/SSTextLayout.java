/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SSTextLayout {

	public void init(Display display, String text) {
		return;
	}

	public void addStyle(Font font, Color color, int x, int y) {
		return;
	}

	public void handlePaintItemEvent(Event event, int offsetX, int offsetY) {
		return;
	}

	public void handleMeasureItemEvent(Event event) {
		return;
	}

	public Rectangle getBounds() {
		return new Rectangle(0, 0, 0, 0);
	}

}
