/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.sys;

import org.csstudio.autocomplete.AutoCompleteConstants;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.IContentParser;

/**
 * System Data Source content parser.
 * 
 * @author Fred Arnaud (Sopra Group)
 */
public class SysContentParser implements IContentParser {

	public static final String SYS_SOURCE = "sys://"; //$NON-NLS-1$

	@Override
	public boolean accept(final ContentDescriptor desc) {
		if (desc.getValue().startsWith(AutoCompleteConstants.FORMULA_PREFIX))
			return false;
		if (desc.getValue().startsWith(SYS_SOURCE)
				|| (desc.getValue().indexOf(AutoCompleteConstants.DATA_SOURCE_NAME_SEPARATOR) == -1 
				&& SYS_SOURCE.equals(desc.getDefaultDataSource())))
			return true;
		return false;
	}

	@Override
	public ContentDescriptor parse(final ContentDescriptor desc) {
		int startIndex = 0;
		String contentToParse = desc.getValue();
		if (contentToParse.startsWith(SYS_SOURCE)) {
			contentToParse = contentToParse.substring(SYS_SOURCE.length());
			// startIndex = SYS_SOURCE.length();
		}
		SysContentDescriptor currentDesc = new SysContentDescriptor();
		currentDesc.setContentType(SysContentType.SysFunction);
		currentDesc.setStartIndex(startIndex);
		currentDesc.setValue(contentToParse);
		return currentDesc;
	}

}
