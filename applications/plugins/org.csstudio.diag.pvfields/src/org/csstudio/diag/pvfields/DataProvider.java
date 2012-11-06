/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

/** Interface to code the provides {@link PVInfo}
 *  @author Kay Kasemir
 */
public interface DataProvider
{
	/** ID of extension point for {@link DataProvider} implementations */
	final public static String ID = "org.csstudio.diag.pvfields.dataprovider";
	
	/** Perform lookup
	 *  <p>Will be invoked in background thread,
	 *  does not need to start its own thread for long-running activities.
	 *  @param name Name of PV/Channel
	 *  @return {@link PVInfo}
	 *  @throws Exception on error
	 */
    public PVInfo lookup(String name) throws Exception;
}
