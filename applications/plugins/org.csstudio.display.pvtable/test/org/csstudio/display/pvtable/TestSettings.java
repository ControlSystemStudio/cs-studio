/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.loc.LocalDataSource;

/** Test settings
 *  @author Kay Kasemir
 */
public class TestSettings
{
	final public static String NAME = "loc://x";

	public static void setup()
	{
		PVManager.setDefaultDataSource(new LocalDataSource());
	}
}
