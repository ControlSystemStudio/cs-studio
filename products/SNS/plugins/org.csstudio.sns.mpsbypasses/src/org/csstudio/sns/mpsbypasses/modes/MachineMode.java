package org.csstudio.sns.mpsbypasses.modes;

/** Enumerations for the different MPS Bypass Machine Modes
 *
 *  @author Delphy Armstrong - original
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum MachineMode
{
	/** Site, the first element, will be used by default in the GUI.
	 *  It will also be skipped when monitoring PVs.
	 */
	Site("Site"),
	// Rest are valid machine modes, their name matches sections
	// of associated PVs
	MEBT_BS("MEBT Beam Stop"),
	CCL_BS("CCL Beam Stop"),
	LinDmp("Linac Dump"),
	InjDmp("Injection Dump"),
	Ring("Ring"),
	ExtDmp("Extraction Dump"),
	Tgt("Target");

	/** Human-readable representation */
	final private String label;

	/** Initialize
	 *  @param label Human-readable representation
	 */
	private MachineMode(final String label)
	{
		this.label = label;
	}

	/** @return Human-readable representation */
	@Override
    public String toString()
	{
		return label;
	}

	/** Return the enum machine mode of the input String's machine mode enum
	 *
	 * @param name of the machine mode
	 * @return enum of the machine mode, defaulting to Site for invalid name
	 */
	public static MachineMode fromString(final String name)
	{
		for (MachineMode mode : MachineMode.values())
			if (mode.toString().equalsIgnoreCase(name))
				return mode;
		return Site;
	}

	/** @return Machine mode names */
	public static String[] getNames()
    {
		final MachineMode[] values = MachineMode.values();
		final String[] names = new String[values.length];
		for (int i=0; i<names.length; ++i)
			names[i] = values[i].toString();
	    return names;
    }
}
