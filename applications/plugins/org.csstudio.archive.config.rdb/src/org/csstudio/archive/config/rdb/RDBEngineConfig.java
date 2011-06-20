/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import java.net.URISyntaxException;

import org.csstudio.archive.config.EngineConfig;

/** RDB implementation of EngineConfig
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBEngineConfig extends EngineConfig
{
	final private int id;
	
	/** Initialize
	 *  @param id
	 *  @param name
	 *  @param description
	 *  @param url
	 *  @throws URISyntaxException 
	 */
	public RDBEngineConfig(final int id, final String name, final String description, final String url) throws URISyntaxException
    {
		super(name, description, url);
		this.id = id;
    }

	/** @return RDB ID of engine */
	public int getId()
    {
    	return id;
    }

	/** @return Debug representation */
    @Override
    public String toString()
	{
    	return super.toString() + " [" + id + "]";
	}
}
