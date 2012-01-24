/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.nsrrc;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;

/** {@link ILogbookFactory} for the {@link NSRRCLogbook}
 *  
 *  <p>plugin.xml registers this as a logbook implementation
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NSRRCLogbookFactory implements ILogbookFactory
{
    @Override
	public String[] getLogbooks() throws Exception
	{
		return new String[] { "operations", "control", "vacuum" };
	}

	@Override
	public String getDefaultLogbook()
	{
		return "operations";
	}

	@Override
	public ILogbook connect(String logbook, String user, String password)
			throws Exception
	{
		return new NSRRCLogbook(logbook);
	}
}
