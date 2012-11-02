package org.csstudio.diag.pvfields;


public class Preferences
{
	/** Timeout for querying {@link DataProvider}s
	 *  @return Milliseconds
	 */
	public static int getTimeout()
	{
		return 60*1000;
	}
}
