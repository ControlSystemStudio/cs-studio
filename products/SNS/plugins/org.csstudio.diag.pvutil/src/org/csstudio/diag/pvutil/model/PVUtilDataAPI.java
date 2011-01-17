/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvutil.model;

/** API to be implemented by providers of the
 *  extension point org.csstudio.diag.pvutil.pvutildata
 *  <p>
 *  Provides information about FECs and the PVs on them.
 * 
 *  @author Kay Kasemir
 *  @author Dave Purcell
 */
public interface PVUtilDataAPI
{
    /** ID of the extension point for providing PVUtilDataAPI */
    final public static String DATA_EXT_ID = "org.csstudio.diag.pvutil.pvutildata";//$NON-NLS-1$

	/** Get a starting string filter that will populate the FEC List when the plug-in is started.
	 *  Null should be replaced by an empty string ""
	 *  Some string has to be returned to produce list.
	 * @return String to be used as a filter (never <code>null</code>)
	 */
	public String getStartDeviceID(); 
	
	/** Get an Array of FECs based on the filter string supplied.
	 *  @param filter FEC name filter or <code>null</code>
	 *  @return Array of FECs (never <code>null</code>)
	 */
	public FEC [] getFECs(String filter) throws Exception;

	/** Get an Array of PV type using a specific device ID
	 *   or a PV filter.
	 *  @param deviceID whose PVs will be returned - Can be null
	 *  @param filterPV for which to locate PVs - Can be null<br>
	 *  Null deviceID returns all PVs matching the filterPV pattern<br>
	 *  Null filterPV returns all PVs for a given deviceID.  The deviceID will be a DEVICE<br> 
	 *  Note that both parameters = to null returns all PVs<br>
	 *  @return Array of PVs (never <code>null</code>)
	 */
	public PV [] getPVs(String deviceID, String filterPV) throws Exception;


}
