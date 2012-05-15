/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

/** Interface to be implemented by Jython script for {@link ScriptCommand}
 *
 *  @author Kay Kasemir
 */
public interface ScanScript
{
	/** Will be invoked by scan server to execute the script
	 *  @param context Access to logged data, devices, ...
	 */
	public void run(ScriptCommandContext context);
}
