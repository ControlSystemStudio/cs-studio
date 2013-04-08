/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.pvnames.ChannelNameService;
import org.csstudio.pvnames.PVListResult;
import org.eclipse.jface.fieldassist.IContentProposal;

public class PVContentProposalProvider implements IPVContentProposalProvider {

	public PVContentProposalList getProposals(String contents, int position,
			int max) {
		PVContentProposalList cpl = new PVContentProposalList();

		ChannelNameService cns = ChannelNameService.getInstance();
		List<PVListResult> results = cns.get(contents, max);
		for (final PVListResult result : results) {

			List<IContentProposal> contentProposals = new ArrayList<IContentProposal>();
			for (final String proposal : result.getPvs()) {

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
							.toArray(new IContentProposal[contentProposals.size()]), 
							result.getCount());
		}
		return cpl;
	}

	@Override
	public boolean hasProviders() {
		return ChannelNameService.getInstance().hasProviders();
	}

	@Override
	public void cancel() {
		ChannelNameService.getInstance().cancel();
	}

}
