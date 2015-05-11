/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import java.util.List;
import java.util.Map;


/** Information about a PV
 * 
 *  <p>Properties are static name/value pairs.
 *  <p>PVFields represent fields of the PV that change.
 *  
 *  @author Kay Kasemir
 */
public class PVInfo
{
	final private Map<String, String> properties;
	final private List<PVField> fields;
	
	public PVInfo(Map<String, String> properties, List<PVField> fields)
	{
		this.properties = properties;
		this.fields = fields;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public List<PVField> getFields()
	{
		return fields;
	}

	@Override
	public String toString()
	{
		return "PVInfo\nProperties = " + properties + "\nFields = " + fields;
	}
}
