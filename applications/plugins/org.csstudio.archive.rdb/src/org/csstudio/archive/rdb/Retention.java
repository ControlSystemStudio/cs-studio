/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import org.csstudio.platform.utility.rdb.StringID;

/** Retention information
 *  <p>
 *  Uses the 'Name' of a string/ID pair for the retention description.
 *  @author Kay Kasemir
 */
public class Retention extends StringID
{
	public Retention(final int id, final String description)
	{
		super(id, description);
	}
	
    @Override
    @SuppressWarnings("nls")
    final public String toString()
    {
        return String.format("Retention '%s' (%d)", getName(), getId());
    }
}
