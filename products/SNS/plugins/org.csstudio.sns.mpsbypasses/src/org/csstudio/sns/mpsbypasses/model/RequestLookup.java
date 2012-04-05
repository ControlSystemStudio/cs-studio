package org.csstudio.sns.mpsbypasses.model;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

/** Bypass Request lookup: Who requested a bypass at some time?
 *  @author Delphy Armstrong - SQL in original MPSBypassModel
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RequestLookup
{
	/** Map of device IDs to {@link Request}s */
	final private Map<String, Request> requests = new HashMap<String, Request>();

	/** Initialize
 	 *  @param monitor Progress monitor
	 *  @param connection RDB connection
	 *  @throws Exception on error
	 */
	public RequestLookup(final IProgressMonitor monitor, final Connection connection) throws Exception
	{
		getRequests(monitor, connection);
	}

	private void getRequests(final IProgressMonitor monitor, final Connection connection) throws Exception
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

	public void dump(final PrintStream out)
    {
		for (Entry<String, Request> entry : requests.entrySet())
		{
			final String device = entry.getKey();
			final String requestor = entry.getValue().getRequestor();
			final Date date = entry.getValue().getDate();
			out.println(device + " by " + requestor + " on " + date);
		}
    }
}
