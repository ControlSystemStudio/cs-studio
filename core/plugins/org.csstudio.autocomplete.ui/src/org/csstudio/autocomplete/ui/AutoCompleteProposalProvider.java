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
import org.csstudio.autocomplete.IAutoCompleteResultListener;
import org.eclipse.jface.fieldassist.IContentProposal;

public class AutoCompleteProposalProvider implements
		IAutoCompleteProposalProvider {

	private final String type;
	private ContentProposalList currentList;
	private Long currentId;

	public AutoCompleteProposalProvider(String type) {
		this.type = type;
		this.currentList = new ContentProposalList();
	}

	public void getProposals(String contents, int position, int max,
			final IContentProposalSearchHandler handler) {
		currentId = System.currentTimeMillis();
		synchronized (currentList) {
			currentList.clear();
		}
		AutoCompleteService cns = AutoCompleteService.getInstance();
		int expected = cns.get(currentId, type, contents,
				new IAutoCompleteResultListener() {

					@Override
					public void handleResult(Long uniqueId, Integer index,
							AutoCompleteResult result) {
						if (uniqueId == currentId) {
							synchronized (currentList) {
								currentList.responseReceived();
							}
							if (result == null)
								return;

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
							ContentProposalList cpl = null;
							synchronized (currentList) {
								currentList.addProposals(result.getProvider(),
										(IContentProposal[]) contentProposals.toArray(new IContentProposal[contentProposals.size()]), 
										result.getCount(), index);
								cpl = currentList.clone();
							}
							handler.handleResult(cpl);
							// System.out.println("PROCESSED: " + uniqueId + ", " + index);
						}
					}
				});
		currentList.setExpected(expected);
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
