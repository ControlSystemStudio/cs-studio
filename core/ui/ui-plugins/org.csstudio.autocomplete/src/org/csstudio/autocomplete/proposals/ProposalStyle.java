/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.proposals;

import org.eclipse.swt.SWT;

/**
 * Used to define a SWT StyleRange on proposal display.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ProposalStyle {

	/** Start index. */
	public int from;
	/** End index. */
	public int to;
	/** SWT Font style */
	public int fontStyle;
	/** SWT Color constant. */
	public int fontColor;

	public ProposalStyle(int from, int to, int fontStyle, int fontColor) {
		this.from = from;
		this.to = to;
		this.fontStyle = fontStyle;
		this.fontColor = fontColor;
	}

	public ProposalStyle(ProposalStyle ps) {
		this.from = ps.from;
		this.to = ps.to;
		this.fontStyle = ps.fontStyle;
		this.fontColor = ps.fontColor;
	}

	public static ProposalStyle getDefault(int from, int to) {
		return new ProposalStyle(from, to, SWT.BOLD, SWT.COLOR_BLUE);
	}

	public static ProposalStyle getError(int from, int to) {
		return new ProposalStyle(from, to, SWT.BOLD, SWT.COLOR_RED);
	}

	public static ProposalStyle getItalic(int from, int to) {
		return new ProposalStyle(from, to, SWT.ITALIC, SWT.COLOR_GRAY);
	}

}
