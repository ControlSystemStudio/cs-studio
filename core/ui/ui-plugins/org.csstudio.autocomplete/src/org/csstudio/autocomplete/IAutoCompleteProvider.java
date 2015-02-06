/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;

/**
 * Interface for auto-complete providers. Each parser is provided via OSGI
 * services. The listResult method is executed by {@link AutoCompleteService} in
 * a dedicated thread.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface IAutoCompleteProvider {

	/** @return <code>true</code> if provider handles this type of content */
	public boolean accept(final ContentType type);

	/**
	 * @return {@link AutoCompleteResult} matching the provided
	 *         {@link ContentDescriptor}
	 */
	public AutoCompleteResult listResult(final ContentDescriptor desc,
			final int limit);

	/**
	 * Called by {@link AutoCompleteService} when the task is canceled.
	 */
	public void cancel();

}
