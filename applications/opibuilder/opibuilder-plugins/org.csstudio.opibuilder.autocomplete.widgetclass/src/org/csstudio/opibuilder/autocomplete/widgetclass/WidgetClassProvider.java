/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.opibuilder.autocomplete.widgetclass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.opibuilder.util.SchemaService;

/**
 *
 * <code>WidgetClassProvider</code> provides the auto complete results based on the widget classes defined in the
 * {@link SchemaService}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class WidgetClassProvider implements IAutoCompleteProvider {

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.autocomplete.IAutoCompleteProvider#accept(org.csstudio.autocomplete.parser.ContentType)
     */
    @Override
    public boolean accept(ContentType type) {
        return type == WidgetClassContentType.TYPE;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.autocomplete.IAutoCompleteProvider#listResult(org.csstudio.autocomplete.parser.ContentDescriptor,
     * int)
     */
    @Override
    public AutoCompleteResult listResult(ContentDescriptor desc, int limit) {
        String type = desc.getAutoCompleteType().value();
        List<String> widgetClasses = SchemaService.getInstance().getAvailableClassesForWidgetType(type);
        AutoCompleteResult result = new AutoCompleteResult();
        if (widgetClasses.isEmpty()) {
            return result;
        }
        final String content = desc.getValue();
        final int idx = desc.getStartIndex();

        final Set<String> accepted = new HashSet<>();
        final int contentLength = content.length();
        // first fetch only those that start with the string
        widgetClasses.stream().filter(wc -> wc.startsWith(content)).limit(limit).map(wc -> {
            Proposal proposal = new Proposal(idx <= 0 ? wc : wc + "\"", content.equals(wc));
            proposal.setInsertionPos(idx);
            proposal.setStartWithContent(idx <= 0);
            proposal.setFunction(idx > 0);
            proposal.addStyle(ProposalStyle.getDefault(0, contentLength-1));
            accepted.add(wc);
            return proposal;
        }).forEach(p -> {
            result.addTopProposal(p);
            result.addProposal(p);
        });

        // if we didn't reach the limit, fetch those classes that contain the content string anywhere
        if (accepted.size() < limit) {
            widgetClasses.stream().filter(wc -> !accepted.contains(wc) && wc.contains(content))
                .limit(limit - accepted.size()).map(wc -> {
                    int index = wc.indexOf(content);
                    Proposal proposal = new Proposal(idx <= 0 ? wc : wc + "\"", false);
                    proposal.setInsertionPos(idx);
                    proposal.setStartWithContent(false);
                    proposal.setFunction(idx > 0);
                    proposal.addStyle(ProposalStyle.getDefault(index, index + contentLength-1));
                    accepted.add(wc);
                    return proposal;
                }).forEach(p -> result.addProposal(p));
        }

        result.setCount(accepted.size());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.autocomplete.IAutoCompleteProvider#cancel()
     */
    @Override
    public void cancel() {
        // ignore
    }
}
