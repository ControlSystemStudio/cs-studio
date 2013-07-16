/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.Proposal;
import org.csstudio.autocomplete.TopProposalFinder;

public class DBPVListProvider implements IAutoCompleteProvider {

	public static final String NAME = "DB Files";

	@Override
	public AutoCompleteResult listResult(final String type, final String name,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		String cleanedName = AutoCompleteHelper.clean(name);
		Pattern namePattern = AutoCompleteHelper.convertToPattern(cleanedName);
		if (namePattern == null)
			return result;

		result.setCount(DBContextValueHolder.get().countProposals(namePattern));
		for (Proposal p : DBContextValueHolder.get().findProposals(namePattern, limit))
			result.addProposal(p);

		TopProposalFinder trf = new TopProposalFinder(Preferences.getSeparators());
		for (Proposal p : DBContextValueHolder.get().findTopProposals(trf, name))
			result.addTopProposal(p);

		return result;
	}

	@Override
	public void cancel() {
	}

}
