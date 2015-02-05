/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
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
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.IAutoCompleteResultListener;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.ui.content.ContentProposalList;
import org.csstudio.autocomplete.ui.content.IContentProposalSearchHandler;

/**
 * Implements {@link IAutoCompleteProposalProvider} and manages the
 * {@link ContentProposalList} with results from {@link AutoCompleteService}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteProposalProvider implements
		IAutoCompleteProposalProvider {

	private final String type;
	private ContentProposalList currentList;
	private Long currentId;

	public AutoCompleteProposalProvider(String type) {
		this.type = type;
		this.currentList = new ContentProposalList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getProposals(final String contents,
			final IContentProposalSearchHandler handler) {
		currentId = System.currentTimeMillis();
		synchronized (currentList) {
			currentList.clear();
			currentList.setOriginalValue(contents);
		}
		AutoCompleteService cns = AutoCompleteService.getInstance();
		int expected = cns.get(currentId, AutoCompleteType.valueOf(type),
				contents, new IAutoCompleteResultListener() {

					@Override
					public void handleResult(Long uniqueId, Integer index,
							AutoCompleteResult result) {
						if (uniqueId == currentId) {
							synchronized (currentList) {
								currentList.responseReceived();
							}
							if (result == null)
								return;

							List<Proposal> contentProposals = new ArrayList<Proposal>();
							if (result.getProposals() != null)
								for (final Proposal proposal : result.getProposals())
									contentProposals.add(proposal);
							Proposal[] contentProposalsArray = contentProposals
									.toArray(new Proposal[contentProposals.size()]);

							List<Proposal> topContentProposals = new ArrayList<Proposal>();
							if (result.getTopProposals() != null)
								for (final Proposal proposal : result.getTopProposals())
									topContentProposals.add(proposal);

							ContentProposalList cpl = null;
							synchronized (currentList) {
								if (result.getProvider() != null)
									currentList.addProposals(result.getProvider(), contentProposalsArray, result.getCount(), index);
								currentList.addTopProposals(topContentProposals);
								cpl = currentList.clone();
							}
							handler.handleResult(cpl);
							handler.handleTooltips(result.getTooltips());
							// System.out.println("PROCESSED: " + uniqueId + ", " + index);
						}
					}
				});
		currentList.setExpected(expected);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasProviders() {
		return AutoCompleteService.getInstance().hasProviders(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancel() {
		AutoCompleteService.getInstance().cancel(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return type;
	}

}
