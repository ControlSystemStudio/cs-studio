/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.TopProposalFinder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteTests {

	@Test
	public void testTopProposalFinder() {
		TopProposalFinder trf = new TopProposalFinder("-:");

		String name = null;
		List<String> results = new ArrayList<String>();
		List<Proposal> topResults = null;

		name = "C";
		results = new ArrayList<String>();
		results.add("CWS-PTU:PV1");
		results.add("CWS-PTV:PV2");
		results.add("CWS-PTW:PV3");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 1);
		Assert.assertTrue("CWS-".equals(topResults.get(0).getValue()));
		results.clear();

		name = "CWS";
		results = new ArrayList<String>();
		results.add("CWS-PTU:PV1");
		results.add("CWS-PTV:PV2");
		results.add("CWS-PTW:PV3");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 1);
		Assert.assertTrue("CWS-".equals(topResults.get(0).getValue()));
		results.clear();

		name = "CW";
		results = new ArrayList<String>();
		results.add("CWS-PTU:PV1");
		results.add("CWS-PTV:PV2");
		results.add("CWT-PTW:PV3");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 2);
		Assert.assertTrue("CWS-".equals(topResults.get(0).getValue()));
		Assert.assertTrue("CWT-".equals(topResults.get(1).getValue()));
		results.clear();

		name = "CWS-P";
		results = new ArrayList<String>();
		results.add("CWS-PTU:PV1");
		results.add("CWS-PTU:PV2");
		results.add("CWS-PTV:PV3");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 2);
		Assert.assertTrue("CWS-PTU:".equals(topResults.get(0).getValue()));
		Assert.assertTrue("CWS-PTV:".equals(topResults.get(1).getValue()));
		results.clear();

		name = "CWS-P";
		results = new ArrayList<String>();
		results.add("CWS-PTU:PV1-THX138");
		results.add("CWS-PTU:PV1-THX138");
		results.add("CWS-PTV:PV1-THX138");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 2);
		Assert.assertTrue("CWS-PTU:".equals(topResults.get(0).getValue()));
		Assert.assertTrue("CWS-PTV:".equals(topResults.get(1).getValue()));
		results.clear();

		name = "UTIL-C4PS-AF92:";
		results = new ArrayList<String>();
		results.add("UTIL-C4PS-AF92:BUSBAR1");
		results.add("UTIL-C4PS-AF92:BUSBAR2");
		results.add("UTIL-C4PS-AF92:BUSBAR2");

		topResults = trf.getTopProposals(name, results);
		Assert.assertTrue(topResults.size() == 2);
		Assert.assertTrue("UTIL-C4PS-AF92:BUSBAR1".equals(topResults.get(0).getValue()));
		Assert.assertTrue("UTIL-C4PS-AF92:BUSBAR2".equals(topResults.get(1).getValue()));
		results.clear();
	}

	@Test
	public void testHelper() {
		String content = "*C4PS*.CALC";
		Pattern pattern = AutoCompleteHelper.convertToPattern(content);
		
		Matcher matcher = pattern.matcher("UTIL-C4PS-AF92:BUSBAR1.CALC");
		Assert.assertTrue(matcher.matches());
		
		matcher = pattern.matcher("UTIL-C4PS-AF92:BUSBAR12CALC");
		Assert.assertFalse(matcher.matches());
	}
	
}
