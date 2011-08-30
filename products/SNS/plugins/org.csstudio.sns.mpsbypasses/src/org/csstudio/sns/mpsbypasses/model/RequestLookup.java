package org.csstudio.sns.mpsbypasses.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Bypass Request lookup: Who requested a bypass at some time?
 *  @author Delphy Armstrong - SQL in original MPSBypassModel
 *  @author Kay Kasemir
 */
public class RequestLookup
{
	/** Map of device IDs to {@link Request}s */
	final private Map<String, Request> requests = new HashMap<String, Request>();
	
	/** Initialize
	 *  @param connection RDB connection
	 *  @throws Exception on error
	 */
	public RequestLookup(final Connection connection) throws Exception
	{
		this(connection, false);
	}

	/** Initialize
	 *  @param connection RDB connection
	 *  @param debug Display request info?
	 *  @throws Exception on error
	 */
	public RequestLookup(final Connection connection, final boolean debug) throws Exception
	{
		getRequests(connection, debug);
	}

	private void getRequests(final Connection connection, final boolean debug) throws Exception
    {
		final PreparedStatement statement = connection.prepareStatement(
			"SELECT d.DVC_ID, r.ADD_BN, r.ADD_DTE, e.first_nm, e.middle_nm, e.last_name" +
			" FROM oper.bypass_req r" +
			" JOIN oper.bypass_dvc d ON r.bypass_nbr = d.bypass_nbr" +
			" LEFT OUTER JOIN oper.employee e ON e.bn = r.ADD_BN" +
			" WHERE r.status_cd = 'I'");
		
		try
		{
			final ResultSet result = statement.executeQuery();
			while (result.next())
			{
				final String device = result.getString(1);
				final String badge = result.getString(2);
				final Date date = result.getDate(3);
				// Name info can be null
				String first = result.getString(4);
				if (result.wasNull())
					first = null;
				String last = result.getString(6);
				if (result.wasNull())
					last = null;
				// Use name if possible, fall back to badge number
				final String requestor;
				if (first != null  &&  last != null)
					requestor = first + " " + last + " (" + badge + ")";
				else
					requestor = "Badge " + badge;
				
				if (debug)
					System.out.println(device + " by " + requestor + " on " + date);
				requests.put(device, new Request(requestor, date));
			}
		}
		finally
		{
			statement.close();
		}
    }
	
	/** Check if there is a bypass request
	 *  @param device_id Device ID
	 *  @return {@link Request} that asked for a bypass or <code>null</code>
	 */
	public Request getRequestor(final String device_id)
	{
		return requests.get(device_id);
	}
}
