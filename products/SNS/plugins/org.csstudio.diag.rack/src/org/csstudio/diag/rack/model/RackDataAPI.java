package org.csstudio.diag.rack.model;

public interface RackDataAPI {
	
	/** Get the rack height in U
	 *
	 * @return int representing height in number of U(never <code>null</code>).
	 */
	public int getRackHeight();  
	
		/** Get an Array of Rack Names based on the filter string supplied.
	 *  No filter should supply complete list of Racks
	 *
	 *  @return Array of Strings of Rack Names (never <code>null</code>)
	 */
	public String [] getRackNames(String filter) throws Exception;
	

	/** Get an Array of FEC type based on the filter string supplied.
	 *  @param filter FEC name filter or <code>null</code>
	 *  @return Array of FECs (never <code>null</code>)
	 */

	public RackList [] getRackListing(String rackName, String slotPosInd) throws Exception;

	/** Get a String which indicates the Name or text 
	 * displayed above the rack.
	 *  @return String depicting rack title
	 */

	public String getRackName() throws Exception;

	
	
}
