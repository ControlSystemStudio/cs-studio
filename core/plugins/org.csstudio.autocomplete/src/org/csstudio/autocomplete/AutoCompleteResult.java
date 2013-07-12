/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AutoCompleteResult {

	private List<Proposal> proposals;
	private List<Proposal> topProposals;
	private int count;
	private String provider;

	public AutoCompleteResult() {
		this.proposals = new LinkedList<Proposal>();
		this.topProposals = new LinkedList<Proposal>();
		this.count = 0;
	}

	/**
	 * @see AutoCompleteResult#addProposal(Proposal)
	 */
	@Deprecated
	public void add(String name) {
		Proposal proposal = new Proposal(name, false);
		proposals.add(proposal);
	}

	public void addProposal(Proposal p) {
		proposals.add(p);
	}

	public void addTopProposal(Proposal p) {
		topProposals.add(p);
	}

	public List<String> getProposalsAsString() {
		List<String> strList = new ArrayList<>();
		for (Proposal p : proposals)
			strList.add(p.getValue());
		return strList;
	}

	public List<String> getTopProposalsAsString() {
		List<String> strList = new ArrayList<>();
		for (Proposal p : topProposals)
			strList.add(p.getValue());
		return strList;
	}

	public List<Proposal> getProposals() {
		return proposals;
	}

	public List<Proposal> getTopProposals() {
		return topProposals;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
