package org.csstudio.sns.mpsbypasses.model;

/** Enumerations for the different MPS Bypass States
 *
 *  @author Delphy Armstrong- original
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum BypassState
{
	/** 'All', the first enum, is the default */
	All("All"),
	Bypassed("Bypassed"),
	Bypassable("Bypassable"),
	NotBypassable("Not Bypassable"),
	Disconnected("Disconnected"),
	InError("In Error");

	/** Human-readable representation */
	final private String label;

	/** Initialize
	 *  @param label Human-readable representation
	 */
	private BypassState(final String label)
	{
		this.label = label;
	}

	/** @return Human-readable representation */
	@Override
    public String toString()
	{
		return label;
	}

	/** Return the enum state of the input bypass state name
	 *
	 * @param name of the state
	 * @return enum of the state or 'All' if name is invalid
	 */
	public static BypassState fromString(final String name)
	{
		// Search the MPSBypassState enums for a match on the input state name
		for (BypassState state : BypassState.values())
			if (state.toString().equalsIgnoreCase(name))
				return state;
		return All;
	}

	/** @return Bypass state names */
	public static String[] getNames()
    {
		final BypassState[] values = BypassState.values();
		final String[] names = new String[values.length];
		for (int i=0; i<names.length; ++i)
			names[i] = values[i].toString();
	    return names;
    }
}
