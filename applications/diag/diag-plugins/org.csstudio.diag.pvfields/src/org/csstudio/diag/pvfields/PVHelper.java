/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

/** Helper for handling PV names
 *  @author Kay Kasemir
 */
public class PVHelper
{
	/** @param pv_name PV Name
	 *  @return Field name
     */
    final public static String getField(final String pv_name)
    {
    	final int sep = pv_name.indexOf('.');
    	if (sep > 0)
    		return pv_name.substring(sep+1);
    	// No field name? Assume "VAL"
    	return "VAL";
    }

    /** @param name Full name: record.field
     *  @return record
     */
	public static String getPV(final String name)
	{
		final int dot = name.lastIndexOf('.');
		if (dot > 0)
			return name.substring(0, dot);
		return name;
	}
 }
