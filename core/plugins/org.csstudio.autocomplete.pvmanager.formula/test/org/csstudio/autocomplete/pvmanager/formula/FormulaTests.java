/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.formula;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.FunctionDescriptor;
import org.epics.pvmanager.formula.FormulaRegistry;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class FormulaTests {

	@Test
	public void testParseBasic() {
		FormulaContentParser parser = new FormulaContentParser();
		ContentDescriptor formula = new ContentDescriptor();
		formula.setAutoCompleteType(AutoCompleteType.Formula);
		FunctionDescriptor outDesc = null;

		formula.setValue("=A+func((B*C),2.2,'CWS-");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue(outDesc.getValue().equals("CWS-"));
		Assert.assertTrue(outDesc.isReplay());

		formula.setValue("=A+func((B*C),2.2,'");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.FormulaFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("func"));
		Assert.assertTrue(outDesc.getCurrentArgIndex() == 2);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());
		Assert.assertFalse(outDesc.isReplay());

		formula.setValue("=A+func((B*C), -2.2 ,\"");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.FormulaFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("func"));
		Assert.assertTrue(outDesc.getCurrentArgIndex() == 2);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());
		Assert.assertFalse(outDesc.isReplay());

		formula.setValue("=A+func((B*C), +2.2 ,\"str\")");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc == null);

		formula.setValue("=A + func() * bo");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.FormulaFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("bo"));
		Assert.assertTrue(outDesc.getCurrentArgIndex() == -1);
		Assert.assertFalse(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());
		Assert.assertFalse(outDesc.isReplay());

		formula.setValue("=A + func() * bo(");
		outDesc = (FunctionDescriptor) parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.FormulaFunction);
		Assert.assertTrue(outDesc.getFunctionName().equals("bo"));
		Assert.assertTrue(outDesc.getArgs().size() == 0);
		Assert.assertTrue(outDesc.getCurrentArgIndex() == 0);
		Assert.assertTrue(outDesc.hasOpenBracket());
		Assert.assertFalse(outDesc.isComplete());
		Assert.assertFalse(outDesc.isReplay());
	}

	@Test
	public void testParseOperations() {
		// See specs CSS -> Debugging -> Formula Functions
		FormulaContentParser parser = new FormulaContentParser();
		ContentDescriptor formula = new ContentDescriptor();
		formula.setAutoCompleteType(AutoCompleteType.Formula);
		ContentDescriptor outDesc = null;

		formula.setValue("=!'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=!('PV'!='UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()%'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()&'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=bar()&&'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=1+'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=bar()*'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=-'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()+-'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()-'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()/'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()<'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()>'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()<='UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()>='UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=!(foo()=='UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("='PV'==foo()?bar():'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=3*2^'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()|'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));

		formula.setValue("=foo()||'UTIL-");
		outDesc = parser.parse(formula);
		Assert.assertTrue(outDesc.getContentType() == ContentType.PV);
		Assert.assertTrue("UTIL-".equals(outDesc.getValue()));
	}

	@Test
	public void testProvider() {
		FormulaRegistry.getDefault().registerFormulaFunctionSet(new TestFunctionSet());

		FormulaFunctionProvider provider = new FormulaFunctionProvider();
		FunctionDescriptor functionDesc = new FunctionDescriptor();
		functionDesc.setAutoCompleteType(AutoCompleteType.Formula);
		functionDesc.setContentType(ContentType.FormulaFunction);
		AutoCompleteResult result = null;

		functionDesc.setFunctionName("s");
		functionDesc.setOriginalContent("=1+s");
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 2);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sin1("));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sin1("));

		functionDesc.setFunctionName("sin1");
		functionDesc.setOpenBracket(true);
		functionDesc.setOriginalContent("=1+sin1(");
		result = provider.listResult(functionDesc, 10);
		Assert.assertTrue(result.getTooltips().size() == 1);
		Assert.assertTrue(result.getTooltips().get(0).value.equals("sin1(<Integer>arg)"));
	}

}
