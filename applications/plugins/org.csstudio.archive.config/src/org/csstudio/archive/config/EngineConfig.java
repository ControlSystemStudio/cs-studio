/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

import java.net.URI;
import java.net.URISyntaxException;

/** Sample engine description
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineConfig
{
	final private String name;
	final private String description;
	final private URI url;

	/** Initialize
	 *  @param name Engine name
	 *  @param description .. description
	 *  @param url Engine's web server URL
	 *  @throws URISyntaxException if URL is malformed
	 */
	public EngineConfig(final String name, final String description, final String url) throws URISyntaxException
    {
	    this.name = name;
	    this.description = description;
		this.url = new URI(url);
    }

	/** @return Engine name */
	public String getName()
    {
    	return name;
    }

	/** @return Engine Description */
	public String getDescription()
    {
    	return description;
    }
	
	/** @return Engine's web server URL */
	public URI getURL()
    {
    	return url;
    }
	
	/** @return Debug representation */
    @Override
    public String toString()
	{
		return "Engine '" + name + "' (" + description + ") at " + url;
	}
}
