/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteService;
import org.eclipse.jface.fieldassist.IContentProposal;

public class AutoCompleteProposalProvider implements
		IAutoCompleteProposalProvider {

	private final String type;

	public AutoCompleteProposalProvider(String type) {
		this.type = type;
	}

	public ContentProposalList getProposals(String contents, int position,
			int max) {
		ContentProposalList cpl = new ContentProposalList();

		AutoCompleteService cns = AutoCompleteService.getInstance();
		List<AutoCompleteResult> results = cns.get(type, contents);
		for (final AutoCompleteResult result : results) {

			List<IContentProposal> contentProposals = new ArrayList<IContentProposal>();
			for (final String proposal : result.getResults()) {

				contentProposals.add(new IContentProposal() {
					public String getContent() {
						return proposal;
					}

					public String getDescription() {
						return null;
					}

					public String getLabel() {
						return null;
					}

					public int getCursorPosition() {
						return proposal.length();
					}
				});
			}
			cpl.addProposals(result.getProvider(),
					(IContentProposal[]) contentProposals
							.toArray(new IContentProposal[contentProposals
									.size()]), result.getCount());
		}
		return cpl;
	}

	@Override
	public boolean hasProviders() {
		return AutoCompleteService.getInstance().hasProviders(type);
	}

	@Override
	public void cancel() {
		AutoCompleteService.getInstance().cancel(type);
	}

	public String getType() {
		return type;
	}

}
