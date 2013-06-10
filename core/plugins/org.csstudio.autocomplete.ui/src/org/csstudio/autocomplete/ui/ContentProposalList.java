/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.fieldassist.IContentProposal;

public class ContentProposalList {

	// provider => proposals
	private Map<String, IContentProposal[]> proposalMap;
	// provider => count
	private Map<String, Integer> countMap;
	// index => provider
	private SortedMap<Integer, String> providerMap;
	private int lenght = 0;
	
	private int expected = 0;
	private int responded = 0;

	public ContentProposalList() {
		proposalMap = new HashMap<String, IContentProposal[]>();
		countMap = new HashMap<String, Integer>();
		providerMap = new TreeMap<Integer, String>();
	}

	public ContentProposalList(ContentProposalList list) {
		this.proposalMap = new HashMap<String, IContentProposal[]>(list.proposalMap);
		this.countMap = new HashMap<String, Integer>(list.countMap);
		this.providerMap = new TreeMap<Integer, String>(list.providerMap);
		this.lenght = list.lenght;
		this.expected = list.expected;
		this.responded = list.responded;
	}

	public void addProposals(String provider, IContentProposal[] proposals,
			Integer count, Integer index) {
		proposalMap.put(provider, proposals);
		countMap.put(provider, count);
		lenght += proposals.length;
		providerMap.put(index, provider);
	}

	public IContentProposal[] getProposals(String provider) {
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
		proposalMap.clear();
		countMap.clear();
		providerMap.clear();
		lenght = 0;
		expected = 0;
		responded = 0;
	}
	
	public ContentProposalList clone() {
		return new ContentProposalList(this);
	}

	@Override
	public String toString() {
		return "ContentProposalList [proposalMap=" + proposalMap
				+ ", countMap=" + countMap + ", providerMap=" + providerMap
				+ ", lenght=" + lenght + "]";
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
