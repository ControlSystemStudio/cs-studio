/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.loc;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;

/**
 * Local Data Source content provider.
 * Provides all available VType & content syntax assistance. 
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class LocalContentProvider implements IAutoCompleteProvider {

	@Override
	public boolean accept(final ContentType type) {
		if (type == LocalContentType.LocalPV)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc, final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		LocalContentDescriptor locDesc = null;
		if (desc instanceof LocalContentDescriptor) {
			locDesc = (LocalContentDescriptor) desc;
		} else {
			return result; // empty result
		}

		if (locDesc.isComplete())
			return result; // empty result

		// handle proposals
		int count = 0;
		if (locDesc.isCompletingVType() && locDesc.getvType() != null) {
			String type = locDesc.getvType();
			Proposal topProposal = null;
			String closestMatchingType = null;
			for (String vType : LocalContentDescriptor.listVTypes()) {
				if (vType.startsWith(type)) {
					String prefix = locDesc.getPvName() + LocalContentParser.VTYPE_START;
					if (desc.getDefaultDataSource() != LocalContentParser.LOCAL_SOURCE)
						prefix = LocalContentParser.LOCAL_SOURCE + prefix;
					int offset = prefix.length();
					Proposal proposal = new Proposal(prefix + vType + LocalContentParser.VTYPE_END, false);
					proposal.setDescription(LocalContentDescriptor.getVTypeDescription(vType));
					proposal.addStyle(ProposalStyle.getDefault(0, offset + type.length() - 1));
					proposal.setInsertionPos(desc.getStartIndex());
					result.addProposal(proposal);
					count++;
					if (closestMatchingType == null
							|| closestMatchingType.compareTo(vType) > 0) {
						closestMatchingType = vType;
						topProposal = proposal;
					}
				}
			}
			// handle top proposals
			if (closestMatchingType != null && !type.isEmpty())
				result.addTopProposal(topProposal);
		}
		result.setCount(count);

		// handle tooltip
		TooltipData td = null;
		if (locDesc.isCompletingInitialValue()) {
			td = new TooltipData();
			td.value = "pvname"; //$NON-NLS-1$
			String vType = locDesc.getvType();
			if (vType != null) {
				td.value += LocalContentParser.VTYPE_START + locDesc.getvType()
						+ LocalContentParser.VTYPE_END;
			}
			td.value += LocalContentParser.INITIAL_VALUE_START;
			int start = td.value.length();
			td.value += locDesc.getInitialValueTooltip();
			int end = td.value.length();
			td.value += LocalContentParser.INITIAL_VALUE_END;
			td.styles = new ProposalStyle[1];
			if (locDesc.checkParameters())
				td.styles[0] = ProposalStyle.getDefault(start, end);
			else td.styles[0] = ProposalStyle.getError(start, end);
			result.addTooltipData(td);

		} else if (locDesc.isCompletingVType()) {
			td = new TooltipData();
			td.value = "pvname<type>"; //$NON-NLS-1$
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getDefault(6, 12);
			result.addTooltipData(td);

			td = new TooltipData();
			td.value = "pvname<type>(initialValue)"; //$NON-NLS-1$
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getDefault(6, 12);
			result.addTooltipData(td);

		} else {
			int from = 6, to = 12; // bold <type>
			if (locDesc.getvType() == null) { // bold pvname
				from = 0;
				to = 6;
				td = new TooltipData();
				td.value = "pvname"; //$NON-NLS-1$
				td.styles = new ProposalStyle[1];
				td.styles[0] = ProposalStyle.getDefault(from, to);
				result.addTooltipData(td);
			}

			td = new TooltipData();
			td.value = "pvname<type>"; //$NON-NLS-1$
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getDefault(from, to);
			result.addTooltipData(td);

			td = new TooltipData();
			td.value = "pvname<type>(initialValue)"; //$NON-NLS-1$
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getDefault(from, to);
			result.addTooltipData(td);
		}

		return result;
	}

	@Override
	public void cancel() {
	}

}
