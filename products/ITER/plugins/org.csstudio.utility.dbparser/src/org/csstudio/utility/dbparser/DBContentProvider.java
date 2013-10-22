/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.PVDescriptor;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.proposals.TopProposalFinder;
import org.csstudio.utility.dbparser.data.Field;
import org.csstudio.utility.dbparser.data.Record;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DBContentProvider implements IAutoCompleteProvider {

	public static final String NAME = "DB Files";

	@Override
	public boolean accept(final ContentType type) {
		if (type == ContentType.PVName
				|| type == ContentType.PVField
				|| type == ContentType.PVParam)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		PVDescriptor pvDesc = null;
		if (desc instanceof PVDescriptor) {
			pvDesc = (PVDescriptor) desc;
		} else {
			return result; // empty result
		}

		if (pvDesc.getContentType() == ContentType.PVName) {
			String cleanedName = AutoCompleteHelper.trimWildcards(pvDesc.getName());
			Pattern namePattern = AutoCompleteHelper.convertToPattern(cleanedName);
			if (namePattern == null)
				return result;
			// handle proposals
			int count = 0;
			for (String rec : DBContext.get().listRecords()) {
				Matcher m = namePattern.matcher(rec);
				if (m.find()) {
					if (count <= limit) {
						Proposal proposal = new Proposal(rec, false);
						proposal.addStyle(ProposalStyle.getDefault(m.start(), m.end() - 1));
						proposal.setInsertionPos(pvDesc.getStartIndex());
						result.addProposal(proposal);
					}
					count++;
				}
			}
			result.setCount(count);
			// handle top proposals
			TopProposalFinder trf = new TopProposalFinder(Preferences.getSeparators());
			List<Proposal> tops = trf.getTopProposals(cleanedName, DBContext.get().listRecords());
			for (Proposal p : tops) {
				p.setInsertionPos(pvDesc.getStartIndex());
				result.addTopProposal(p);
			}
		}
		if (pvDesc.getContentType() == ContentType.PVField) {
			// handle proposals
			int count = 0;
			String closestMatchingField = null;
			Proposal topProposal = null;
			Record rec = DBContext.get().getRecord(pvDesc.getName());
			int offset = pvDesc.getName().length() + 1;
			for (Field f : rec.getFields()) {
				if (f.getType().startsWith(pvDesc.getField())) {
					Proposal proposal = new Proposal(pvDesc.getName() + "." + f.getType(), false);
					proposal.addStyle(ProposalStyle.getDefault(0, offset + pvDesc.getField().length()));
					proposal.setInsertionPos(pvDesc.getStartIndex());
					result.addProposal(proposal);
					count++;
					if (closestMatchingField == null
							|| closestMatchingField.compareTo(f.getType()) > 0) {
						closestMatchingField = f.getType();
						topProposal = proposal;
					}
				}
			}
			result.setCount(count);
			// handle top proposals
			if (closestMatchingField != null)
				result.addTopProposal(topProposal);
		}
		// TODO: handle params ?
		Collections.sort(result.getProposals());
		return result;
	}

	@Override
	public void cancel() {
	}

}
