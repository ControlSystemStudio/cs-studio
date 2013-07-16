/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.csstudio.autocomplete.Preferences;
import org.csstudio.autocomplete.Proposal;
import org.csstudio.autocomplete.ProposalStyle;
import org.eclipse.swt.SWT;

public class ContentProposalList {

	private String originalValue;
	private List<Proposal> topProposalList;

	// provider => proposals
	private Map<String, Proposal[]> proposalMap;

	// provider => count
	private Map<String, Integer> countMap;

	// index => provider
	private SortedMap<Integer, String> providerMap;

	private int lenght = 0;
	private int expected = 0;
	private int responded = 0;

	private final int maxTopProposals;
	private boolean hasContentMatchingProposal = false;

	public ContentProposalList() {
		topProposalList = new ArrayList<Proposal>();
		proposalMap = new HashMap<String, Proposal[]>();
		countMap = new HashMap<String, Integer>();
		providerMap = new TreeMap<Integer, String>();
		maxTopProposals = Preferences.getMaxTopResults();
	}

	public ContentProposalList(ContentProposalList list) {
		this.originalValue = list.originalValue;
		this.topProposalList = new ArrayList<Proposal>(list.topProposalList);
		this.proposalMap = new HashMap<String, Proposal[]>(list.proposalMap);
		this.countMap = new HashMap<String, Integer>(list.countMap);
		this.providerMap = new TreeMap<Integer, String>(list.providerMap);
		this.lenght = list.lenght;
		this.expected = list.expected;
		this.responded = list.responded;
		this.maxTopProposals = list.maxTopProposals;
		this.hasContentMatchingProposal = list.hasContentMatchingProposal;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public void addTopProposals(List<Proposal> proposals) {
		if (proposals == null || proposals.isEmpty())
			return;
		if (maxTopProposals == 0 && proposals.size() == 1) {
			if (!proposals.get(0).getValue().equals(originalValue)) {
				int index = topProposalList.indexOf(proposals.get(0));
				if (index == -1) {
					if (proposals.get(0).getValue().startsWith(originalValue))
						proposals.get(0).setStartWithContent(true);
					topProposalList.add(proposals.get(0));
				} else
					topProposalList.get(index).increment();
			}
		} else if (maxTopProposals > 0) {
			for (Proposal proposal : proposals) {
				if (!proposal.getValue().equals(originalValue)) {
					int index = topProposalList.indexOf(proposal);
					if (index == -1) {
						if (proposal.getValue().startsWith(originalValue))
							proposal.setStartWithContent(true);
						topProposalList.add(proposal);
					} else
						topProposalList.get(index).increment();
				}
			}
		}
		Collections.sort(topProposalList);
	}

	public List<Proposal> getTopProposalList() {
		List<Proposal> list = new ArrayList<Proposal>();
		if (topProposalList.size() == 0) {
			return list;
		}

		Proposal originalTopProposal = new Proposal(originalValue,
				hasContentMatchingProposal ? false : true);
		originalTopProposal.addStyle(new ProposalStyle(0, originalValue
				.length(), SWT.BOLD, SWT.COLOR_BLUE));

		if (topProposalList.size() == 1 && !originalValue.contains("*")
				&& topProposalList.get(0).getValue().startsWith(originalValue)) {
			list.add(topProposalList.get(0));
			list.add(originalTopProposal);
		} else {
			list.add(originalTopProposal);
			int index = 0;
			for (Proposal tp : topProposalList) {
				if (maxTopProposals == 0)
					list.add(tp);
				else if (index <= maxTopProposals - 1)
					list.add(tp);
				index++;
			}
		}
		
		// We do not display top proposals if the content match a proposal and all
		// provided top proposals match a complete proposal
		boolean allComplete = true;
		for (Proposal tp : list)
			if (tp.isPartial())
				allComplete = false;
		if (allComplete)
			list.clear();

		return list;
	}

	public void addProposals(String provider, Proposal[] proposals,
			Integer count, Integer index) {
		proposalMap.put(provider, proposals);
		countMap.put(provider, count);
		lenght += proposals.length;
		providerMap.put(index, provider);
		for (Proposal p : proposals)
			if (p.getValue().equals(originalValue))
				hasContentMatchingProposal = true;
	}

	public Proposal[] getProposals(String provider) {
		return proposalMap.get(provider);
	}

	public Integer getCount(String provider) {
		return countMap.get(provider);
	}

	public List<String> getProviderList() {
		List<String> list = new ArrayList<String>();
		for (String provider : providerMap.values())
			if (provider != null && !provider.isEmpty())
				list.add(provider);
		return list;
	}

	public int length() {
		return lenght;
	}

	public void clear() {
		topProposalList.clear();
		proposalMap.clear();
		countMap.clear();
		providerMap.clear();
		lenght = 0;
		expected = 0;
		responded = 0;
		hasContentMatchingProposal = false;
	}

	public ContentProposalList clone() {
		return new ContentProposalList(this);
	}

	@Override
	public String toString() {
		return "ContentProposalList [originalValue=" + originalValue
				+ ", topProposalList=" + topProposalList + ", proposalMap="
				+ proposalMap + ", countMap=" + countMap + ", providerMap="
				+ providerMap + ", lenght=" + lenght + ", expected=" + expected
				+ ", responded=" + responded + ", maxTopProposals="
				+ maxTopProposals + "]";
	}

	public void setExpected(int expected) {
		this.expected = expected;
	}

	public void responseReceived() {
		responded++;
	}

	public boolean allResponded() {
		return expected == responded;
	}

}
