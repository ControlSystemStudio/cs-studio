/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import org.csstudio.autocomplete.ui.content.ContentProposalAdapter;
import org.csstudio.autocomplete.ui.content.IContentProposalSearchHandler;

/**
 * Provides auto-completion results to {@link ContentProposalAdapter}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface IAutoCompleteProposalProvider {

	/**
	 * Requests providers for proposals and notify the handler each time a
	 * provider answers.
	 * 
	 * @param contents the content to complete.
	 * @param handler see {@link IContentProposalSearchHandler}.
	 */
	public void getProposals(final String contents,
			final IContentProposalSearchHandler handler);

	/** @return <code>true</code> if at least one provider is defined. */
	public boolean hasProviders();

	/** Cancel the current request. */
	public void cancel();

	/** @return current type, see {@link AutoCompleteTypes}. */
	public String getType();

}
