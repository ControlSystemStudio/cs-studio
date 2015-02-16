/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sim;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.FunctionDescriptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SimContentTests {

	@Test
	public void testParse() {
		SimContentParser parser = new SimContentParser();
		ContentDescriptor inDesc = new ContentDescriptor();
		inDesc.setAutoCompleteType(AutoCompleteType.PV);
		FunctionDescriptor outDesc = null;

		inDesc.setDefaultDataSource("epics://");

		inDesc.setValue("sim://");
		outDesc = (FunctionDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SimContentType.SimFunction);

		inDesc.setValue("sim://ramp");
		outDesc = (FunctionDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SimContentType.SimFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("ramp"));
		Assert.assertFalse(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());

		inDesc.setValue("sim://ramp(");
		outDesc = (FunctionDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SimContentType.SimFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("ramp"));
		Assert.assertTrue(outDesc.getArgs().size() == 0);
		Assert.assertTrue(outDesc.getCurrentArgIndex() == 0);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());

		inDesc.setValue("sim://ramp (-1.1,+2.2,3");
		outDesc = (FunctionDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SimContentType.SimFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("ramp"));
		Assert.assertTrue(outDesc.getArgs().size() == 3);
		Assert.assertTrue(outDesc.getCurrentArgIndex() == 2);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());

		inDesc.setValue("sim://ramp(-1.1, +2.2 ,3)");
		outDesc = (FunctionDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SimContentType.SimFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("ramp"));
		Assert.assertTrue(outDesc.getArgs().size() == 3);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertTrue(outDesc.isComplete());
	}

	@Test
	public void testProvider() {
		DSFunctionRegistry.getDefault().registerDSFunctionSet(new TestFunctionSet());

		SimContentProvider provider = new SimContentProvider();
		FunctionDescriptor functionDesc = new FunctionDescriptor();
		functionDesc.setAutoCompleteType(AutoCompleteType.Formula);
		functionDesc.setContentType(SimContentType.SimFunction);
		AutoCompleteResult result = null;

		functionDesc.setFunctionName("*");
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 3);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sim://const("));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sim://const("));

		functionDesc.setFunctionName("c");
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 2);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sim://const("));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sim://const("));

		functionDesc.setFunctionName("c*");
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 2);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sim://const("));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sim://const("));

		functionDesc.setFunctionName("const");
		functionDesc.setOpenBracket(true);
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getTooltips().size() == 4);
		Assert.assertTrue(result.getTooltips().get(0).value.equals("const(<Double>number)"));

		functionDesc.addArgument(1);
		functionDesc.addArgument(2);
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getTooltips().size() == 2);
		Assert.assertTrue(result.getTooltips().get(0).value.equals("const(<Double>args,...)"));
	}
}
