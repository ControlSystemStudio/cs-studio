package org.csstudio.diag.pvutil.model;

public interface PVUtilDataAPI
{
	/** Get a starting string filter that will populate the FEC List when the plug-in is started.
	 *  Null should be replaced by an empty string ""
	 *  Some string has to be returned to produce list.
	 * @return String to be used as a filter (never <code>null</code>)
	 */
	public String getStartDeviceID(); 
	
	/** Get an Array of FEC type based on the filter string supplied.
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
