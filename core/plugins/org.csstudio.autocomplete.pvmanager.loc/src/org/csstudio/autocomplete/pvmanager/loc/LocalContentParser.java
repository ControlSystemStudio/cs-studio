/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.loc;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.IContentParser;
import org.csstudio.autocomplete.parser.engine.ExprLexer;
import org.csstudio.autocomplete.parser.engine.ExprToken;
import org.csstudio.autocomplete.parser.engine.ExprTokenType;

/**
 * Local Data Source content parser.
 * 
 * @author Fred Arnaud (Sopra Group)
 */
public class LocalContentParser implements IContentParser {

	public static final String LOCAL_SOURCE = "loc://";
	public static final String VTYPE_START = "<";
	public static final String VTYPE_END = ">";
	public static final String INITIAL_VALUE_START = "(";
	public static final String INITIAL_VALUE_END = ")";

	private LocalContentDescriptor currentDescriptor;
	private String contentToParse;

	@Override
	public boolean accept(final ContentDescriptor desc) {
		if (desc.getValue().startsWith(AutoCompleteConstants.FORMULA_PREFIX))
			return false;
		if (desc.getValue().startsWith(LOCAL_SOURCE)
				|| (desc.getValue().indexOf(AutoCompleteConstants.DATA_SOURCE_NAME_SEPARATOR) == -1 
				&& LOCAL_SOURCE.equals(desc.getDefaultDataSource())))
			return true;
		return false;
	}

	@Override
	public ContentDescriptor parse(final ContentDescriptor desc) {
		int startIndex = 0;
		contentToParse = desc.getValue();
		if (contentToParse.startsWith(LOCAL_SOURCE)) {
			contentToParse = contentToParse.substring(LOCAL_SOURCE.length());
			// startIndex = LOCAL_SOURCE.length();
		}
		currentDescriptor = new LocalContentDescriptor();
		currentDescriptor.setContentType(LocalContentType.LocalPV);
		currentDescriptor.setStartIndex(startIndex);
		currentDescriptor.setValue(contentToParse);
		parseLocContent(contentToParse);
		return currentDescriptor;
	}

	private void parseLocContent(String locContent) {
		String pvName = null;
		String vType = null;

		// handle VType
		int ltIndex = locContent.indexOf(VTYPE_START);
		int gtIndex = locContent.indexOf(VTYPE_END);
		if (ltIndex > 0) { // pvname<
			pvName = locContent.substring(0, ltIndex);
			if (gtIndex > 0 && gtIndex > ltIndex) {
				vType = locContent.substring(ltIndex + 1, gtIndex);
			} else { // complete VType
				vType = locContent.substring(ltIndex + 1);
				// currentDescriptor.setStartIndex(currentDescriptor.getStartIndex() + ltIndex + 1);
				currentDescriptor.setCompletingVType(true);
			}
		}
		currentDescriptor.setvType(vType == null ? null : vType.trim());

		// handle initialValue (ignore macros)
		Pattern pattern = Pattern.compile("[^\\$]\\(");
		Matcher matcher = pattern.matcher(locContent);
		if (matcher.find()) {
			currentDescriptor.setCompletingInitialValue(true);
			if (pvName == null) // no VType found
				pvName = locContent.substring(0, matcher.start() + 1);
			try {
				parseInitialValues(locContent.substring(matcher.start() + 1));
			} catch (IOException ex) {
				// TODO something
			}
		}
		currentDescriptor.setPvName(pvName == null ? locContent.trim() : pvName.trim());
	}

	private void parseInitialValues(String content) throws IOException {
		ExprToken e = null;
		ExprLexer lexer = new ExprLexer(content);
		while ((e = lexer.next()) != null) {
			if (e.type == ExprTokenType.Comma)
				continue;
			if (e.type == ExprTokenType.CloseBracket) {
				currentDescriptor.setComplete(true);
				break;
			}
			switch (e.type) {
			case Decimal:
				currentDescriptor.addInitialvalue(e.val, Double.class);
				break;
			case Integer:
				currentDescriptor.addInitialvalue(e.val, Double.class);
				break;
			case String:
				currentDescriptor.addInitialvalue(e.val, String.class);
				break;
			default:
				break;
			}
		}
	}

}
