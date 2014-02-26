/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.content;

import java.util.List;

import org.csstudio.autocomplete.tooltips.TooltipData;
import org.csstudio.autocomplete.ui.IAutoCompleteProposalProvider;

/**
 * Handle results from {@link IAutoCompleteProposalProvider}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface IContentProposalSearchHandler {

	public void handleResult(final ContentProposalList proposalList);

	public void handleTooltips(final List<TooltipData> tooltips);
}
