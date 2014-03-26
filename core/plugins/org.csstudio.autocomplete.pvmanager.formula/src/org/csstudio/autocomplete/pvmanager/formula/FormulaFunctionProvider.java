/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.FunctionDescriptor;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.autocomplete.tooltips.TooltipData;
import org.epics.pvmanager.formula.FormulaFunction;
import org.epics.pvmanager.formula.FormulaFunctionSet;
import org.epics.pvmanager.formula.FormulaRegistry;

/**
 * PV formula functions provider.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
public class FormulaFunctionProvider implements IAutoCompleteProvider {

	private Map<String, List<FormulaFunction>> functions;

	public FormulaFunctionProvider() {
		functions = new TreeMap<String, List<FormulaFunction>>();
		for (String setName : FormulaRegistry.getDefault().listFunctionSets()) {
			FormulaFunctionSet set = FormulaRegistry.getDefault().findFunctionSet(setName);
			for (FormulaFunction function : set.getFunctions()) {
				List<FormulaFunction> functionList = functions.get(function.getName());
				if (functionList == null) {
					functionList = new ArrayList<FormulaFunction>();
					functions.put(function.getName(), functionList);
				}
				functionList.add(function);
			}
		}
	}

	@Override
	public boolean accept(final ContentType type) {
		if (type == ContentType.FormulaFunction)
			return true;
		return false;
	}

	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc, final int limit) {
		AutoCompleteResult result = new AutoCompleteResult();

		FunctionDescriptor functionDesc = null;
		if (desc instanceof FunctionDescriptor) {
			functionDesc = (FunctionDescriptor) desc;
		} else {
			return result; // empty result
		}
		String nameToFind = functionDesc.getFunctionName();

		// handle proposals
		int count = 0;
		// insertionPos is not yet provided for formula
		// TODO: improve parser
		String originalContent = desc.getOriginalContent();
		int insertionPos = originalContent.lastIndexOf(nameToFind);
		if (!functionDesc.hasOpenBracket()) {
			Proposal topProposal = null;
			String closestMatchingFunction = null;
			for (String functionName : functions.keySet()) {
				if (functionName.startsWith(nameToFind)) {
					Proposal proposal = new Proposal(functionName + "(", false);
					
					String description = functions.get(functionName).get(0).getDescription() + "\n\n";
					for (FormulaFunction ff : functions.get(functionName))
						description += generateSignature(ff);
					proposal.setDescription(description);
					for (FormulaFunction ff : functions.get(functionName))
						proposal.addTooltipData(generateTooltipData(ff, 0));
					
					proposal.addStyle(ProposalStyle.getDefault(0, nameToFind.length() - 1));
					proposal.setInsertionPos(insertionPos);
					proposal.setFunction(true); // display function icon
					result.addProposal(proposal);
					count++;
					if (closestMatchingFunction == null
							|| closestMatchingFunction.compareTo(functionName) > 0) {
						closestMatchingFunction = functionName;
						topProposal = proposal;
					}
				}
			}
			// handle top proposals
			if (closestMatchingFunction != null)
				result.addTopProposal(topProposal);
		}
		result.setCount(count);
		
		// handle tooltip
		if (functionDesc.hasOpenBracket() && !functionDesc.isComplete()) {
			for (String setName : FormulaRegistry.getDefault().listFunctionSets()) {
				FormulaFunctionSet set = FormulaRegistry.getDefault()
						.findFunctionSet(setName);
				for (FormulaFunction function : set.findFunctions(nameToFind)) {
					if (function.getName().equals(nameToFind))
						if (function.getArgumentNames().size() >= functionDesc
								.getArgs().size() || function.isVarArgs())
							result.addTooltipData(generateTooltipData(function,
									functionDesc.getCurrentArgIndex()));
				}
			}
		}
		return result;
	}

	@Override
	public void cancel() {
	}

	private String generateSignature(FormulaFunction function) {
		StringBuffer sb = new StringBuffer();
		if (function.getReturnType() != null) {
			sb.append("<");
			sb.append(function.getReturnType().getSimpleName());
			sb.append("> ");
		}
		sb.append(function.getName() + "(");
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

	private TooltipData generateTooltipData(FormulaFunction function,
			int currentArgIndex) {
		TooltipData td = new TooltipData();
		StringBuilder sb = new StringBuilder();
		sb.append(function.getName() + "(");
		int nbArgs = function.getArgumentNames().size();
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
		sb.append(")");
		td.styles = new ProposalStyle[1];
		td.styles[0] = ProposalStyle.getDefault(from, to);
		td.value = sb.toString();
		return td;
	}

}
