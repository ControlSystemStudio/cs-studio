/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

/** API to be implemented by providers of the
 *  extension point org.csstudio.diag.pvfields.pvfielddata
 *  <p>
 *  Provides information about the fields of the PVs.
 * 
 *  @author Dave Purcell
 */
public interface PVFieldsAPI
{
	/** ID of the extension point for providing PVFieldsAPI */
    final public static String DATA_EXT_ID = "org.csstudio.diag.pvfields.pvfielddata";//$NON-NLS-1$
    
	/** Get information for one or multiple Process Variables and 
	 *  potentially a specific field or fields.
     *  @param pv_name filter for which to locate associated fields - Can NOT be null<br>
     *  @param field epics field(s) to be limit returned list.  Multiples comma delimited.  Can be null<br>
     *   
     *  @return PVInfo (never <code>null</code>)
     */
    public PVInfo [] getPVInfo(String pv_name, String field) throws Exception;


}
