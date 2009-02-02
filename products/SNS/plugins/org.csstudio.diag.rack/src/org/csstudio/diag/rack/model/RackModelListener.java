package org.csstudio.diag.rack.model;

/** Listener interface to RackDataAPI */
public interface RackModelListener
{
	/** value to indicate which data set changed  */
	public enum ChangeEvent
	{
		/** The list of racks has changed */
		RACKLIST,
		/** The contents of the rack have changed based on a specific rack name */
		DVCLIST,
		/** The contents of the rack have changed based on a child supported by the rack equipment*/
		PARENT
		
	}
    /** Something in the IOC model changed: New entries, deleted entries, renamed entries, ... */
    public void rackUtilChanged(ChangeEvent what);
}
