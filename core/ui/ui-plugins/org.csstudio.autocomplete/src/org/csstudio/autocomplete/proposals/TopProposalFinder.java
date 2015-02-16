/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.proposals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;

/**
 * Helper that uses delimiters to find the common matching token in a provided
 * list using delimiters.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TopProposalFinder {

	private String delimiters;

	public TopProposalFinder(String delimiters) {
		this.delimiters = delimiters;
	}

	public List<Proposal> getTopProposals(String name,
			Collection<String> proposals) {
		Set<Proposal> set = new LinkedHashSet<Proposal>();

		if (delimiters == null || delimiters.isEmpty())
			return new ArrayList<Proposal>(set); // empty result

		String cleanedName = AutoCompleteHelper.trimWildcards(name);
		Pattern namePattern = AutoCompleteHelper.convertToPattern(cleanedName);
		Proposal topProposal = null;
		for (String proposal : proposals) {
			Matcher m = namePattern.matcher(proposal);
			if (m.find()) {
				int start = m.end();
				if (start == proposal.length()) {
					topProposal = new Proposal(proposal, false);
				} else {
					topProposal = findToken(proposal, start);
				}
				if (topProposal != null) {
					topProposal.addStyle(ProposalStyle.getDefault(m.start(), m.end() - 1));
					set.add(topProposal);
				}
			}
		}
		return new ArrayList<Proposal>(set);
	}

	private Proposal findToken(String s, int fromIndex) {
		if (fromIndex < 0 || fromIndex >= s.length())
			return null;
		String sub = s.substring(fromIndex);
		StringTokenizer st = new StringTokenizer(sub, delimiters, true);
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			int endIndex = fromIndex + token.length();
			if (endIndex == s.length()) {
				String value = s.substring(0, endIndex);
				if (value != null && !value.isEmpty())
					return new Proposal(value, false);
			}
			boolean hasDelimiter = false;
			for (char d : delimiters.toCharArray())
				if (token.indexOf(d) >= 0)
					hasDelimiter = true;
			if (!hasDelimiter)
				endIndex++;
			String value = s.substring(0, endIndex);
			if (value != null && !value.isEmpty())
				return new Proposal(value, true);
		}
		return null;
	}
}
