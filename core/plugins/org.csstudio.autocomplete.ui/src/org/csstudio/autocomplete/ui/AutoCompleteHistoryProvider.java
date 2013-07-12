/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.Preferences;
import org.csstudio.autocomplete.Proposal;
import org.csstudio.autocomplete.ProposalStyle;
import org.csstudio.autocomplete.TopProposalFinder;
import org.eclipse.swt.SWT;

public class AutoCompleteHistoryProvider implements IAutoCompleteProvider {

	public static final String NAME = "History";

	@Override
	public AutoCompleteResult listResult(final String type, final String name,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		String cleanedName = AutoCompleteHelper.clean(name);
		Pattern namePattern = AutoCompleteHelper.convertToPattern(Pattern.quote(cleanedName));
		if (namePattern == null)
			return result;

		int added = 0;
		int count = 0;
		LinkedList<String> fifo = Activator.getDefault().getHistory(type);
		if (fifo == null)
			return result; // Empty result
		for (String entry : fifo) {
			Matcher m = namePattern.matcher(entry);
			if (m.find()) {
				if (added < limit) {
					Proposal proposal = new Proposal(entry, false);
					proposal.addStyle(new ProposalStyle(m.start(), m.end() - 1,
							SWT.BOLD, SWT.COLOR_BLUE));
					result.addProposal(proposal);
					added++;
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
