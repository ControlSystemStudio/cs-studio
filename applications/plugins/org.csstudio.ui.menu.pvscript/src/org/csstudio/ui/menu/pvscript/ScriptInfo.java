/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

/** Information about a script
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptInfo
{
	final private String description;
	final private String script;

	/** Initialize
	 *  @param description Description of script for menu
	 *  @param script Actual script (may contain full path)
	 */
	public ScriptInfo(final String description, final String script)
	{
		this.description = description;
		this.script = script;
	}

	/** @return Description of script for menu */
	public String getDescription()
	{
		return description;
	}

	/** @return Actual script (may contain full path) */
	public String getScript()
	{
		return script;
	}
	
	/** @return Debug representation */
    @Override
    public String toString()
	{
		return description + ": Command '" + script + "'";
	}
}
