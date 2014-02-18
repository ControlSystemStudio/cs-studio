/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.proposals.TopProposalFinder;

/**
 * System Data Source content provider
 * Provides all available system functions & system properties.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SysContentProvider implements IAutoCompleteProvider {

	public static final String SYSTEM_FUNCTION = "system"; //$NON-NLS-1$
	public static final String SYSTEM_SEPARATOR = "."; //$NON-NLS-1$

	@Override
	public boolean accept(final ContentType type) {
		if (type == SysContentType.SysFunction)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		SysContentDescriptor sysDesc = null;
		if (desc instanceof SysContentDescriptor) {
			sysDesc = (SysContentDescriptor) desc;
		} else {
			return result; // empty result
		}

		int dotIndex = desc.getValue().indexOf(SYSTEM_SEPARATOR);
		if (dotIndex == -1) {
			result = provideFunctions(sysDesc, limit);
		} else if (desc.getValue().substring(0, dotIndex)
				.equals(SYSTEM_FUNCTION)) {
			result = provideSystemProperties(sysDesc, limit);
		}
		Collections.sort(result.getProposals());

		return result;
	}
	
	private AutoCompleteResult provideFunctions(
			final SysContentDescriptor sysDesc, final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		int count = 0;

		String regex = sysDesc.getValue();
		regex = regex.replaceAll("\\*", ".*");
		regex = regex.replaceAll("\\?", ".");
		Pattern valuePattern = null;
		try {
			valuePattern = Pattern.compile("^" + regex); // start with !
		} catch (Exception e) {
			return result; // empty result
		}

		Proposal topProposal = null;
		String closestMatchingFunction = null;
		int offset = SysContentParser.SYS_SOURCE.length();
		for (String function : SysContentDescriptor.listFunctions()) {
			Matcher m = valuePattern.matcher(function);
			if (m.find()) {
				String fctDisplay = function;
				if (sysDesc.getDefaultDataSource() != SysContentParser.SYS_SOURCE)
					fctDisplay = SysContentParser.SYS_SOURCE + function;
				if (function.equals(SYSTEM_FUNCTION))
					fctDisplay += SYSTEM_SEPARATOR;
				Proposal proposal = new Proposal(fctDisplay, false);
				proposal.setDescription(SysContentDescriptor.getDescription(function));
				proposal.addStyle(ProposalStyle.getDefault(0, offset + m.end() - 1));
				proposal.setInsertionPos(sysDesc.getStartIndex());
				if (count <= limit)
					result.addProposal(proposal);
				count++;
				if (closestMatchingFunction == null
						|| closestMatchingFunction.compareTo(function) > 0) {
					closestMatchingFunction = function;
					topProposal = proposal;
				}
			}
		}
		// handle top proposals
		if (closestMatchingFunction != null)
			result.addTopProposal(topProposal);

		result.setCount(count);
		return result;
	}
	
	private AutoCompleteResult provideSystemProperties(final SysContentDescriptor sysDesc, final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();
		int count = 0;
		
		int dotIndex = sysDesc.getValue().indexOf(SYSTEM_SEPARATOR);
		String propValue = sysDesc.getValue().substring(dotIndex + 1);
		String regex = propValue.replaceAll("\\.", "\\\\.");;
		regex = regex.replaceAll("\\*", ".*");
		regex = regex.replaceAll("\\?", ".");
		Pattern valuePattern = null;
		try {
			valuePattern = Pattern.compile("^" + regex); // start with !
		} catch (Exception e) {
			return result; // empty result
		}
		
		List<String> matchingProperties = new ArrayList<String>();
		Properties systemProperties = System.getProperties();
		Enumeration<?> enuProp = systemProperties.propertyNames();
		int offset = SysContentParser.SYS_SOURCE.length() + 7;
		while (enuProp.hasMoreElements()) {
			String propertyName = (String) enuProp.nextElement();
			String propertyValue = systemProperties.getProperty(propertyName);
			Matcher m = valuePattern.matcher(propertyName);
			if (m.find()) {
				String propDisplay = SYSTEM_FUNCTION + SYSTEM_SEPARATOR + propertyName;
				if (sysDesc.getDefaultDataSource() != SysContentParser.SYS_SOURCE)
					propDisplay = SysContentParser.SYS_SOURCE + propDisplay;
				Proposal proposal = new Proposal(propDisplay, false);
				proposal.setDescription(propertyValue);
				proposal.addStyle(ProposalStyle.getDefault(0, offset + m.end() - 1));
				proposal.setInsertionPos(sysDesc.getStartIndex());
				if (count <= limit)
					result.addProposal(proposal);
				matchingProperties.add(propertyName);
				count++;
			}
		}
		// handle top proposals
		TopProposalFinder tpf = new TopProposalFinder(SYSTEM_SEPARATOR);
		for (Proposal tp : tpf.getTopProposals(propValue, matchingProperties)) {
			String propDisplay = SYSTEM_FUNCTION + SYSTEM_SEPARATOR + tp.getValue();
			if (sysDesc.getDefaultDataSource() != SysContentParser.SYS_SOURCE)
				propDisplay = SysContentParser.SYS_SOURCE + propDisplay;
			Proposal proposal = new Proposal(propDisplay, tp.isPartial());
			String propertyValue = systemProperties.getProperty(tp.getValue());
			proposal.setDescription(propertyValue);
			ProposalStyle tpStyle = tp.getStyles().get(0);
			proposal.addStyle(ProposalStyle.getDefault(tpStyle.from, (offset + tpStyle.to)));
			proposal.setInsertionPos(sysDesc.getStartIndex());
			result.addTopProposal(proposal);
		}
		
		result.setCount(count);
		return result;
	}

	@Override
	public void cancel() {
	}

}
