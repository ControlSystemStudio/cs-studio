package org.csstudio.sns.mpsbypasses.model;

import java.util.Date;

/** Bypass Request information
 *
 *  <p>Who requested a bypass and when?
 *
 *  @author Delphy Armstrong - Original MPSBypassRequestInfo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Request
{
	final private String requestor;
	final private Date date;

	/** Initialize
	 *  @param requestor Who requested the bypass?
	 *  @param date When?
	 *
	 */
	public Request(final String requestor,
			final Date date)
	{
		this.requestor = requestor;
		this.date = date;
	}

	/** @return Name or badge number of the person who requested the bypass */
	public String getRequestor()
	{
		return requestor;
	}

	/** @return Date when the bypass was requested */
	public Date getDate()
	{
		return date;
	}

	/** @return debug representation */
	@Override
    public String toString()
	{
		return requestor + " on " + date;
	}
}

