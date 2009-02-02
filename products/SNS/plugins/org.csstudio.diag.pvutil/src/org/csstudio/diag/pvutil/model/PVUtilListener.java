package org.csstudio.diag.pvutil.model;

/** Listener interface to PVUtilDataAPI */
public interface PVUtilListener
{
	/** value to indicate which data set changed  */
	public enum ChangeEvent
	{
		/** The FEC info changed */
		FEC_CHANGE,
		/** The FEC info changed */
		PV_CHANGE
	}
    /** Something in the IOC model changed: New entries, deleted entries, renamed entries, ... */
    public void pvUtilChanged(ChangeEvent what);
}
