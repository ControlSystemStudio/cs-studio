/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.history;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.preferences.Preferences;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.proposals.TopProposalFinder;
import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.autocomplete.ui.AutoCompleteUIPlugin;

/**
 * History proposal provider. Retrieves matching proposals from history. Always
 * handles the full auto-completed content by using {@link ContentDescriptor}
 * originalValue and ignore wildcards.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteHistoryProvider implements IAutoCompleteProvider {

	public static final String NAME = "History";

	@Override
	public boolean accept(final ContentType type) {
		return true;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc,
			final int limit) {
		String content = desc.getOriginalContent();
		int startIndex = 0;
		if (desc.getContentType().equals(ContentType.PVName)) {
			content = desc.getValue();
			startIndex = desc.getStartIndex();
		}
		AutoCompleteResult result = new AutoCompleteResult();
		String cleanedName = AutoCompleteHelper.trimWildcards(content);
		Pattern namePattern = AutoCompleteHelper.convertToPattern(cleanedName);
		if (namePattern == null)
			return result;

		String entryType = AutoCompleteTypes.PV;
		if (content.startsWith("="))
			entryType = AutoCompleteTypes.Formula;

		LinkedList<String> fifo = new LinkedList<String>();
		fifo.addAll(AutoCompleteUIPlugin.getDefault().getHistory(entryType));
		if (fifo.isEmpty())
			return result; // Empty result

		int count = 0;
		for (String entry : fifo) {
			Matcher m = namePattern.matcher(entry);
			if (m.find()) {
				if (count < limit) {
					Proposal proposal = new Proposal(entry, false);
					proposal.addStyle(ProposalStyle.getDefault(m.start(), m.end() - 1));
					proposal.setInsertionPos(startIndex);
					result.addProposal(proposal);
				}
				count++;
			}
		}
		result.setCount(count);

		TopProposalFinder trf = new TopProposalFinder(Preferences.getSeparators());
		for (Proposal p : trf.getTopProposals(Pattern.quote(cleanedName), fifo))
			result.addTopProposal(p);

		return result;
	}

	@Override
	public void cancel() {
	}

}
