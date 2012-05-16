/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

/** Base for Jython scripts that are executed by {@link ScriptCommand}
 *
 *  TODO Document 'naming standard' for file and class names
 *  <pre>
 *  class MyScript(ScanScript):
 *      def getDeviceNames(self):
 *       return [ "result1", "result2" ]
 *
 *   def run(self, context):
 *       [x, y] = context.getData("xpos", "signal")
 *       context.write("result1", x[0])
 *       context.write("result2", y[0])
 *  </pre>
 *  @author Kay Kasemir
 */
public class ScanScript
{
    /** List device names to which the scan should connect,
     *  allowing the script to then 'write' to them.
     *  @return Device (alias) names used by the script
     */
    public String[] getDeviceNames()
    {
        return new String[0];
    }

	/** Will be invoked by scan server to execute the script
	 *  @param context Access to logged data, devices, ...
	 */
	public void run(final ScriptCommandContext context)
	{
		// Default doesn't do anything
		// Script can read log data, perform computation,
		// write result to devices
	}
}
