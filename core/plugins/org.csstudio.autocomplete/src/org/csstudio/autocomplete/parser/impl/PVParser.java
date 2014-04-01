/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser.impl;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.parser.IContentParser;
import org.csstudio.autocomplete.parser.PVDescriptor;

/**
 * Common non-simulated PV parser (ca://, epics://, pva://)
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class PVParser implements IContentParser {
	
	public static final String CA_SOURCE = "ca://";
	public static final String EPICS_SOURCE = "epics://";
	public static final String PVA_SOURCE = "pva://";

	private final static Pattern hasField = Pattern.compile("(.*)\\.(.*)");
	private final static Pattern hasOptions = Pattern
			.compile("(.*) \\{(.*)\\}?");

	@Override
	public boolean accept(final ContentDescriptor desc) {
		if (desc.getValue().startsWith(AutoCompleteConstants.FORMULA_PREFIX))
			return false;
		if (desc.getValue().startsWith(CA_SOURCE)
				|| (desc.getValue().indexOf("://") == -1 && CA_SOURCE
						.equals(desc.getDefaultDataSource())))
			return true;
		if (desc.getValue().startsWith(EPICS_SOURCE)
				|| (desc.getValue().indexOf("://") == -1 && EPICS_SOURCE
						.equals(desc.getDefaultDataSource())))
			return true;
		if (desc.getValue().startsWith(PVA_SOURCE)
				|| (desc.getValue().indexOf("://") == -1 && PVA_SOURCE
						.equals(desc.getDefaultDataSource())))
			return true;
		return false;
	}

	@Override
	public ContentDescriptor parse(final ContentDescriptor desc) {
		PVDescriptor currentDesc = new PVDescriptor();
		int startIndex = 0;
		String contentToParse = desc.getValue();
		if (contentToParse.startsWith(CA_SOURCE)) {
			contentToParse = contentToParse.substring(CA_SOURCE.length());
			startIndex = CA_SOURCE.length();
		}
		if (contentToParse.startsWith(EPICS_SOURCE)) {
			contentToParse = contentToParse.substring(EPICS_SOURCE.length());
			startIndex = EPICS_SOURCE.length();
		}
		if (contentToParse.startsWith(PVA_SOURCE)) {
			contentToParse = contentToParse.substring(PVA_SOURCE.length());
			startIndex = PVA_SOURCE.length();
		}
		currentDesc.setContentType(ContentType.PVName);
		currentDesc.setStartIndex(startIndex);
		currentDesc.setValue(contentToParse);
		parsePV(currentDesc, contentToParse);
		return currentDesc;
	}

	private void parsePV(PVDescriptor desc, String contentToParse) {
		String jcaChannelName = contentToParse.trim();

		boolean hasOption = false;
		Matcher matcher = hasOptions.matcher(contentToParse);
		if (matcher.matches()) {
			hasOption = true;
			desc.setContentType(ContentType.PVParam);
			jcaChannelName = matcher.group(1).trim();
			String clientOptions = matcher.group(2).trim();
			// TODO: use jackson ? (JSON parser)
			StringTokenizer st = new StringTokenizer(clientOptions, ",", false);
			if (st.hasMoreTokens()) {
				String option = st.nextToken().trim();
				if (option.indexOf(':') > 0) {
					String name = option.substring(0, option.indexOf(':')).trim();
					String value = option.substring(option.indexOf(':') + 1).trim();
					desc.addParam(name, value);
				} else {
					desc.addParam(option, null);
				}
			}
		}
		matcher = hasField.matcher(jcaChannelName);
		if (matcher.matches()) {
			if (!hasOption)
				desc.setContentType(ContentType.PVField);
			desc.setName(matcher.group(1).trim());
			desc.setField(matcher.group(2).trim());
		} else {
			desc.setName(jcaChannelName);
		}
	}
	
}
