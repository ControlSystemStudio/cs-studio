/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.model;

/** API to be implemented by extensions to org.csstudio.diag.rack.rackutildata
 * 
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
public interface RackDataAPI
{
    /** Extension point ID */
    final public static String DATA_EXT_ID = "org.csstudio.diag.rack.rackutildata";//$NON-NLS-1$

	/** Get the rack height in U
	 *
	 * @return int representing height in number of "U".
	 */
	public int getRackHeight();  
	
	/** Get an Array of Rack Names based on the filter string supplied.
	 *  No filter should supply complete list of Racks
	 *
	 *  @return Array of Strings of Rack Names (never <code>null</code>)
	 */
	public String [] getRackNames(String filter) throws Exception;
	
	/** TODO Looks wrong
	 *  Get an Array of FEC type based on the filter string supplied.
	 *  @param filter FEC name filter or <code>null</code>
	 *  @return Array of FECs (never <code>null</code>)
	 */
	public RackList [] getRackListing(String rackName, String slotPosInd) throws Exception;

	/** Get a String which indicates the Name or text displayed above the rack.
	 *  @return String depicting rack title
	 */
	public String getRackName() throws Exception;
}
