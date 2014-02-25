/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sys;

import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class SysContentTests {

	@Test
	public void testParse() {
		SysContentParser parser = new SysContentParser();
		ContentDescriptor inDesc = new ContentDescriptor();
		inDesc.setAutoCompleteType(AutoCompleteType.PV);
		SysContentDescriptor outDesc = null;

		inDesc.setDefaultDataSource("epics://");

		inDesc.setValue("sys://");
		outDesc = (SysContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SysContentType.SysFunction);
		Assert.assertTrue(outDesc.getValue().isEmpty());

		inDesc.setValue("sys://s*");
		outDesc = (SysContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SysContentType.SysFunction);
		Assert.assertTrue(outDesc.getValue().equals("s*"));

		inDesc.setValue("sys://system.*");
		outDesc = (SysContentDescriptor) parser.parse(inDesc);
		Assert.assertTrue(outDesc.getContentType() == SysContentType.SysFunction);
		Assert.assertTrue(outDesc.getValue().equals("system.*"));
	}

	@Test
	public void testProvider() {
		SysContentProvider provider = new SysContentProvider();
		SysContentDescriptor sysDesc = new SysContentDescriptor();
		sysDesc.setAutoCompleteType(AutoCompleteType.Formula);
		sysDesc.setContentType(SysContentType.SysFunction);
		AutoCompleteResult result = null;

		sysDesc.setValue("");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getProposals().size() == SysContentDescriptor.listFunctions().size());

		sysDesc.setValue("*");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getProposals().size() == SysContentDescriptor.listFunctions().size());

		sysDesc.setValue("s");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 1);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sys://system."));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sys://system."));

		sysDesc.setValue("system.us");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sys://system.user."));

		sysDesc.setValue("system.us*");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sys://system.user."));

		sysDesc.setValue("system.user.*ome");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 1);
		Assert.assertTrue(result.getProposals().get(0).getValue().equals("sys://system.user.home"));
		Assert.assertTrue(result.getTopProposals().size() == 1);
		Assert.assertTrue(result.getTopProposals().get(0).getValue().equals("sys://system.user.home"));

		sysDesc.setValue("system.user..*ome");
		result = provider.listResult(sysDesc, 10);
		Assert.assertTrue(result.getProposals().size() == 0);
	}
}
