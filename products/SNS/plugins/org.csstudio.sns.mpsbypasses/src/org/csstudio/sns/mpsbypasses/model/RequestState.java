package org.csstudio.sns.mpsbypasses.model;

/** Enumerations for the different MPS Bypass Requests
 *
 *  @author Delphy Armstrong - Original MPSBypassRequested
 */
@SuppressWarnings("nls")
public enum RequestState
{
	/** First option is the default */
	All("All"),
	/** Only show requested bypasses */
	Requested("Requested"),
	/** Only show non-requested bypasses */
	NotRequested("Not Requested");

	/** Human-readable representation */
	final private String label;

	/** Initialize
	 *  @param label Human-readable representation
	 */
	private RequestState(final String label)
	{
		this.label = label;
	}

	/** @return Human-readable representation */
	@Override
    public String toString()
	{
		return label;
	}

	/** Return the enum request value of the input request string
	 *
	 * @param name of the request state
	 * @return enum of the request or <code>All</code>
	 */
	public static RequestState fromString(final String name)
	{
		// Search the enums for a match
		for (RequestState requested : RequestState.values())
			if (requested.toString().equalsIgnoreCase(name))
				return requested;
		return All;
	}

	/** @return Request state names */
	public static String[] getNames()
    {
		final RequestState[] values = RequestState.values();
		final String[] names = new String[values.length];
		for (int i=0; i<names.length; ++i)
			names[i] = values[i].toString();
	    return names;
    }

}

