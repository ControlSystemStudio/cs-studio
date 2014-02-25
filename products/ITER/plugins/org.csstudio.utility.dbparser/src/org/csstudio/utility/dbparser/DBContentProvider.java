/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
import org.csstudio.utility.dbdparser.data.Field;
import org.csstudio.utility.dbdparser.data.RecordType;
import org.csstudio.utility.dbdparser.data.Template;
import org.csstudio.utility.dbdparser.util.DbdUtil;
import org.csstudio.utility.dbparser.data.Record;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DBContentProvider implements IAutoCompleteProvider {

	public static final String NAME = "DB Files";
	private final Map<String, List<Field>> fieldMap;

	public DBContentProvider() {
		fieldMap = new HashMap<String, List<Field>>();
		try {
			Template dbdTemplate = DbdUtil.generateTemplate();
			for (RecordType recordType : dbdTemplate.getRecordTypes())
				fieldMap.put(recordType.getName(), recordType.getFields());
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE,
					"Failed to generate DBD config template: " + e.getMessage());
		}
	}

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
		if (pvDesc.getContentType() == ContentType.PVField
				&& pvDesc.getName() != null && !pvDesc.getName().isEmpty()
				&& DBContext.get().getRecord(pvDesc.getName()) != null) {
			// handle proposals
			int count = 0;
			String closestMatchingField = null;
			Proposal topProposal = null;
			Record rec = DBContext.get().getRecord(pvDesc.getName());
			final String type = rec.getType();
			int offset = pvDesc.getName().length() + 1;
			if (fieldMap.get(type) != null) {
				for (Field f : fieldMap.get(type)) {
					if (f.getName().startsWith(pvDesc.getField())) {
						if (count <= limit) {
							Proposal proposal = new Proposal(pvDesc.getName() + "." + f.getName(), false);
							proposal.setDescription(f.getType());
							proposal.addStyle(ProposalStyle.getDefault(0, offset + pvDesc.getField().length()));
							proposal.setInsertionPos(pvDesc.getStartIndex());
							result.addProposal(proposal);
							if (closestMatchingField == null
									|| closestMatchingField.compareTo(f.getName()) > 0) {
								closestMatchingField = f.getName();
								topProposal = proposal;
							}
						}
						count++;
					}
				}
				result.setCount(count);
				// handle top proposals
				if (closestMatchingField != null)
					result.addTopProposal(topProposal);
			} else {
				Activator.getLogger().log(Level.WARNING,
						"Record type '" + type + "' of '" + pvDesc.getName() + "' not found.");
			}
		}
		// TODO: handle params ?
		Collections.sort(result.getProposals());
		return result;
	}

	@Override
	public void cancel() {
	}

}
