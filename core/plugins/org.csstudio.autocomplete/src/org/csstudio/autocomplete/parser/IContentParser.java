/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.parser;

import org.csstudio.autocomplete.AutoCompleteService;

/**
 * Common interface for auto-completed fields content parsers. Used by
 * {@link AutoCompleteService} to parse field content and select providers. Each
 * parser is defined via OSGI services.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public interface IContentParser {

	/**
	 * @return <code>true</code> if this parser handles the described content.
	 */
	public boolean accept(final ContentDescriptor desc);

	/**
	 * @return {@link ContentDescriptor} to be submitted to providers or parsers
	 *         if the replay attribute is set to <code>true</code>.
	 */
	public ContentDescriptor parse(final ContentDescriptor desc);

}
