/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

public class ProposalStyle {

	public int from;
	public int to;
	public int fontStyle;
	public int fontColor;

	public ProposalStyle(int from, int to, int fontStyle, int fontColor) {
		this.from = from;
		this.to = to;
		this.fontStyle = fontStyle;
		this.fontColor = fontColor;
	}

}
