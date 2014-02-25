/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.tooltips;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;

/**
 * Part of {@link TooltipContent} provided via {@link AutoCompleteResult} or
 * {@link Proposal}. This class represents a single line of the final tool-tip
 * content (see {@link TooltipContent}). The usage is 1 {@link TooltipData} per
 * function.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TooltipData {

	/** Value that will be concatenated. Represents a single line. */
	public String value;
	/** SWT StyleRange that will be applied to value. */
	public ProposalStyle[] styles;

}
