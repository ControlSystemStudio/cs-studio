/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.loc;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class LocalContentTests {

	@Test
	public void testParse() {
		LocalContentParser parser = new LocalContentParser();
		ContentDescriptor inDesc = new ContentDescriptor();
		inDesc.setAutoCompleteType(AutoCompleteType.PV);
		LocalContentDescriptor outDesc = null;

		inDesc.setDefaultDataSource("epics://");

		inDesc.setValue("loc://");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertFalse(outDesc.isCompletingInitialValue());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV_$(MACCRO)");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV_$(MACCRO)"));
		Assert.assertFalse(outDesc.isCompletingInitialValue());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV<VD");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType().equals("VD"));
		Assert.assertTrue(outDesc.isCompletingVType());
		Assert.assertFalse(outDesc.isCompletingInitialValue());

		inDesc.setValue("loc://myPV<VDouble>(-1.1");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType().equals("VDouble"));
		Assert.assertTrue(outDesc.getInitialValues().size() == 1);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == Double.class);
		Assert.assertTrue(outDesc.isCompletingInitialValue());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV<VDouble> (-1.1,2");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType().equals("VDouble"));
		Assert.assertTrue(outDesc.getInitialValues().size() == 2);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == Double.class);
		Assert.assertTrue(outDesc.isCompletingInitialValue());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV <VDouble>(+1.1,2)");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType().equals("VDouble"));
		Assert.assertTrue(outDesc.getInitialValues().size() == 2);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == Double.class);
		Assert.assertTrue(outDesc.isComplete());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV(\"str");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType() == null);
		Assert.assertTrue(outDesc.getInitialValues().size() == 1);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == String.class);
		Assert.assertTrue(outDesc.isCompletingInitialValue());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://myPV (\"str1\",\"str2\")");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("myPV"));
		Assert.assertTrue(outDesc.getvType() == null);
		Assert.assertTrue(outDesc.getInitialValues().size() == 2);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == String.class);
		Assert.assertTrue(outDesc.isComplete());
		Assert.assertFalse(outDesc.isCompletingVType());

		inDesc.setValue("loc://my_$(MACCRO)_PV (\"str1\", \"str2\")");
		outDesc = (LocalContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == LocalContentType.LocalPV);
		Assert.assertTrue(outDesc.getPvName().equals("my_$(MACCRO)_PV"));
		Assert.assertTrue(outDesc.getvType() == null);
		Assert.assertTrue(outDesc.getInitialValues().size() == 2);
		Assert.assertTrue(outDesc.getInitialValuesTypes().get(0) == String.class);
		Assert.assertTrue(outDesc.isComplete());
		Assert.assertFalse(outDesc.isCompletingVType());
	}

	@Test
	public void testProvider() {
		LocalContentProvider provider = new LocalContentProvider();
		LocalContentDescriptor localDesc = new LocalContentDescriptor();
		localDesc.setAutoCompleteType(AutoCompleteType.Formula);
		localDesc.setContentType(LocalContentType.LocalPV);
		localDesc.setPvName("myPV");
		AutoCompleteResult result = null;

		result = provider.listResult(localDesc, 10);
		Assert.assertTrue(result.getTooltips().size() == 3);
		Assert.assertTrue(result.getTooltips().get(0).styles[0].from == 0);
		Assert.assertTrue(result.getTooltips().get(0).styles[0].to == 6);

		localDesc.setvType("");
		localDesc.setCompletingVType(true);
		result = provider.listResult(localDesc, 10);
		Assert.assertTrue(result.getProposals().size() == LocalContentDescriptor.listVTypes().size());
		Assert.assertTrue(result.getTooltips().size() == 2);
		Assert.assertTrue(result.getTooltips().get(0).styles[0].from == 6);
		Assert.assertTrue(result.getTooltips().get(0).styles[0].to == 12);

		localDesc.setvType("VD");
		localDesc.setCompletingVType(true);
		result = provider.listResult(localDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 2);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("loc://myPV<VDouble>"));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("loc://myPV<VDouble>"));

		localDesc.setvType("VDoubleArray");
		localDesc.setCompletingVType(false);
		localDesc.setCompletingInitialValue(true);
		localDesc.addInitialvalue("-1.1", Double.class);
		localDesc.addInitialvalue("+2.2", Double.class);
		result = provider.listResult(localDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 0);
		Assert.assertTrue(result.getTooltips().size() == 1);
		Assert.assertTrue(result.getTooltips().get(0).value.equals("pvname<"
				+ localDesc.getvType() + ">(" + localDesc.getInitialValueTooltip() + ")"));

	}

}
