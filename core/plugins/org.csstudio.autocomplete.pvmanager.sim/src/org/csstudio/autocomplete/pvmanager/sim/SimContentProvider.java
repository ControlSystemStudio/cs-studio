/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.FunctionDescriptor;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;

/**
 * Simulation Data Source content provider.
 * Provides all available functions & theirs tooltips.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SimContentProvider implements IAutoCompleteProvider {

	@Override
	public boolean accept(final ContentType type) {
		if (type == SimContentType.SimFunction)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc,
			final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		FunctionDescriptor functionDesc = null;
		if (desc instanceof FunctionDescriptor) {
			functionDesc = (FunctionDescriptor) desc;
		} else {
			return result; // empty result
		}

		String functionName = functionDesc.getFunctionName();
		DSFunctionSet set = DSFunctionRegistry.getDefault().findFunctionSet(
				SimDSFunctionSet.name);

		// handle proposals
		int count = 0;
		if (!functionDesc.hasOpenBracket()) {
			String regex = functionName;
			regex = regex.replaceAll("\\*", ".*");
			regex = regex.replaceAll("\\?", ".");
			Pattern valuePattern = null;
			try {
				valuePattern = Pattern.compile("^" + regex); // start with !
			} catch (Exception e) {
				return result; // empty result
			}
			
			Proposal topProposal = null;
			DSFunction closestMatchingFunction = null;
			int offset = SimContentParser.SIM_SOURCE.length();
			for (DSFunction function : set.getFunctions()) {
				Matcher m = valuePattern.matcher(function.getName());
				if (m.find()) {
					String proposalStr = function.getName();
					if (hasMandatoryArgument(function))
						proposalStr += "(";
					if (desc.getDefaultDataSource() != SimContentParser.SIM_SOURCE)
						proposalStr = SimContentParser.SIM_SOURCE + proposalStr;
					Proposal proposal = new Proposal(proposalStr, false);
					String description = function.getDescription() + "\n\n" + generateSignature(function);
					for (DSFunction poly : function.getPolymorphicFunctions())
						description += "\n" + generateSignature(poly);
					proposal.setDescription(description);
					int currentArgIndex = -1;
					if (hasMandatoryArgument(function))
						currentArgIndex = 0;
					proposal.addTooltipData(generateTooltipData(function, currentArgIndex));
					for (DSFunction poly : function.getPolymorphicFunctions())
						proposal.addTooltipData(generateTooltipData(poly, currentArgIndex));
					proposal.addStyle(ProposalStyle.getDefault(0, offset + m.end() - 1));
					proposal.setInsertionPos(desc.getStartIndex());
					result.addProposal(proposal);
					count++;
					if (closestMatchingFunction == null
							|| closestMatchingFunction.getName().compareTo(function.getName()) > 0) {
						closestMatchingFunction = function;
						topProposal = proposal;
					}
				}
			}
			// handle top proposals
			if (closestMatchingFunction != null && !functionName.isEmpty())
				result.addTopProposal(topProposal);
		}
		result.setCount(count);
		
		// handle tooltip
		if (!functionDesc.isComplete()) {
			for (DSFunction function : set.findFunctions(functionName)) {
				// no tooltip for incomplete functions => use proposals
				if (function.getName().equals(functionName)) {
					if (checkToken(function, functionDesc))
						result.addTooltipData(generateTooltipData(function,
								functionDesc.getCurrentArgIndex()));
					for (DSFunction poly : function.getPolymorphicFunctions())
						if (checkToken(poly, functionDesc))
							result.addTooltipData(generateTooltipData(poly,
									functionDesc.getCurrentArgIndex()));
				}
			}
		}
		return result;
	}

	@Override
	public void cancel() {
	}

	private String generateSignature(DSFunction function) {
		StringBuffer sb = new StringBuffer();
		sb.append("sim://" + function.getName() + "(");
		int nbArgs = function.getArgumentNames().size();
		for (int i = 0; i < nbArgs; i++) {
			sb.append("<");
			sb.append(function.getArgumentTypes().get(i).getSimpleName());
			sb.append(">");
			sb.append(function.getArgumentNames().get(i));
			if (i < nbArgs - 1)
				sb.append(", ");
		}
		if (function.isVarArgs())
			sb.append(",...");
		sb.append(")");
		return sb.toString();
	}

	private boolean hasMandatoryArgument(DSFunction function) {
		if (function.getNbArgs() == 0)
			return false;
		for (DSFunction poly : function.getPolymorphicFunctions())
			if (poly.getNbArgs() == 0)
				return false;
		return true;
	}

	private boolean checkToken(DSFunction function, FunctionDescriptor token) {
		if (token.hasOpenBracket() && function.getNbArgs() == 0)
			return false; // sim://noise( => no tooltip for sim://noise
		if (token.hasOpenBracket()
				&& function.getArgumentNames().size() < token.getArgs().size()
				&& !function.isVarArgs())
			return false; // too much arguments
		return true;
	}

	private TooltipData generateTooltipData(DSFunction function,
			int currentArgIndex) {
		// build content
		TooltipData td = new TooltipData();
		StringBuilder sb = new StringBuilder();
		sb.append(function.getName());
		int nbArgs = function.getNbArgs();
		if (nbArgs > 0)
			sb.append("(");
		int from = 0, to = 0;
		for (int i = 0; i < nbArgs; i++) {
			if (i == currentArgIndex) {
				from = sb.length();
			}
			sb.append("<");
			sb.append(function.getArgumentTypes().get(i).getSimpleName());
			sb.append(">");
			sb.append(function.getArgumentNames().get(i));
			if (i == currentArgIndex) {
				to = sb.length();
			}
			if (i < nbArgs - 1)
				sb.append(", ");
		}
		if (function.isVarArgs()) {
			if (currentArgIndex >= nbArgs) {
				from = sb.length();
				to = from + 4;
			}
			sb.append(",...");
		}
		if (nbArgs > 0)
			sb.append(")");
		if (function.getTooltip() != null) {
			td.styles = new ProposalStyle[2];
			td.styles[0] = ProposalStyle.getDefault(from, to);
			from = sb.length() + 1;
			sb.append(" " + function.getTooltip());
			to = sb.length();
			td.styles[1] = ProposalStyle.getItalic(from, to);
		} else {
			td.styles = new ProposalStyle[1];
			td.styles[0] = ProposalStyle.getDefault(from, to);
		}
		td.value = sb.toString();

		return td;
	}

}
