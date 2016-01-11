/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.ca;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;

/**
 * Channel Access Data Source content provider. Provides options syntax
 * assistance.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class CAContentProvider implements IAutoCompleteProvider {

    @Override
    public boolean accept(final ContentType type) {
        if (type == CAContentType.CAPV)
            return true;
        return false;
    }

    @Override
    public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
        AutoCompleteResult result = new AutoCompleteResult();

        CAContentDescriptor caDesc = null;
        if (desc instanceof CAContentDescriptor) {
            caDesc = (CAContentDescriptor) desc;
        } else {
            return result; // empty result
        }

        if (caDesc.isComplete())
            return result; // empty result

        // handle proposals
        int count = 0;
        if (caDesc.isCompletingOption() && caDesc.getOption() != null) {
            String curOpt = caDesc.getOption();
            Proposal topProposal = null;
            String closestMatchingOption = null;
            for (String option : CAContentDescriptor.listOptions()) {
                if (option.startsWith(curOpt)) {
                    String prefix = caDesc.getPvName() + " ";
                    if (desc.getDefaultDataSource() != CAContentParser.CA_SOURCE) {
                        prefix = CAContentParser.CA_SOURCE + prefix;
                    }
                    int offset = prefix.length();
                    Proposal proposal = new Proposal(prefix + option, false);
                    proposal.addStyle(ProposalStyle.getDefault(0, offset + curOpt.length() - 1));
                    proposal.setInsertionPos(desc.getStartIndex());
                    result.addProposal(proposal);
                    count++;
                    if (closestMatchingOption == null
                            || closestMatchingOption.compareTo(option) > 0) {
                        closestMatchingOption = option;
                        topProposal = proposal;
                    }
                }
            }
            // handle top proposals
            if (closestMatchingOption != null && !curOpt.isEmpty())
                result.addTopProposal(topProposal);
        }
        result.setCount(count);

        // handle tooltip
        TooltipData td = null;
        if (caDesc.isCompletingOption()) {
            td = new TooltipData();
            td.value = "pvname {option}"; //$NON-NLS-1$
            td.styles = new ProposalStyle[1];
            td.styles[0] = ProposalStyle.getDefault(7, 15);
            result.addTooltipData(td);
        } else {
            td = new TooltipData();
            td.value = "pvname"; //$NON-NLS-1$
            td.styles = new ProposalStyle[1];
            td.styles[0] = ProposalStyle.getDefault(0, 6);
            result.addTooltipData(td);

            td = new TooltipData();
            td.value = "pvname {option}"; //$NON-NLS-1$
            td.styles = new ProposalStyle[1];
            td.styles[0] = ProposalStyle.getDefault(0, 6);
            result.addTooltipData(td);
        }

        return result;
    }

    @Override
    public void cancel() {
    }
}
