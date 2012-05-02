/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

/** Factory for scan {@link DataLog}
 *  @author Kay Kasemir
 */
public class DataLogFactory
{
	/** Plugin ID defined in MANIFEST.MF */
	@SuppressWarnings("nls")
    final public static String ID = "org.csstudio.scan.log";

	/** @return {@link DataLog} */
	public static DataLog getDataLog()
	{
		return new MemoryDataLog();
	}
}
