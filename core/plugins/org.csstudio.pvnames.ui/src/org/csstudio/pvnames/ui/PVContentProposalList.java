/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;

public class PVContentProposalList {

	// provider => proposals
	private Map<String, IContentProposal[]> proposalMap;
	// provider => count
	private Map<String, Integer> countMap;
	private int lenght = 0;

	public PVContentProposalList() {
		proposalMap = new HashMap<String, IContentProposal[]>();
		countMap = new HashMap<String, Integer>();
	}

	public void addProposals(String provider, IContentProposal[] proposals,
			Integer count) {
		proposalMap.put(provider, proposals);
		countMap.put(provider, count);
		lenght += proposals.length;
	}

	public IContentProposal[] getProposals(String provider) {
		return proposalMap.get(provider);
	}

	public Integer getCount(String provider) {
		return countMap.get(provider);
	}

	public Set<String> getProviderList() {
		Set<String> set = new HashSet<String>();

		// Always return History first if exists
		String history_provider = PVContentHistoryProvider.NAME;
		if (proposalMap.containsKey(history_provider)) {
			set.add(history_provider);
		}
		for (String provider : proposalMap.keySet()) {
			if (!provider.equals(history_provider))
				set.add(provider);
		}
		return set;
	}

	public int length() {
		return lenght;
	}

}
