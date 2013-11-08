/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.rcp;

import org.csstudio.autocomplete.ui.util.SSStyledText;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RCPSSStyledText extends SSStyledText {

	private StyledText text;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control init(Composite parent, int style, Object layoutData) {
		text = new StyledText(parent, style);
		text.setLayoutData(layoutData);
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String content) {
		text.setText(content);
		text.pack();
		// need right margin to avoid bold text to overflow
		text.setSize(text.getSize().x + 20, text.getSize().y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStyle(Color color, int fontStyle, int start, int lenght) {
		StyleRange styleRange = new StyleRange();
		styleRange.fontStyle = fontStyle;
		styleRange.foreground = color;
		styleRange.start = start;
		styleRange.length = lenght;
		text.setStyleRange(styleRange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getSize() {
		return text.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return text != null && !text.isDisposed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFocus() {
		if (text == null || text.isDisposed()) {
			return false;
		}
		return text.getShell().isFocusControl() || text.isFocusControl();
	}

}
