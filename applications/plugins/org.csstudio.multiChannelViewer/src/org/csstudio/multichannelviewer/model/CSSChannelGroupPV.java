package org.csstudio.multichannelviewer.model;

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.VMultiDouble;

public class CSSChannelGroupPV {

	// PvManager PV to define the connection with the list of specified pv's
	private PV<VMultiDouble> pv;

	/**
	 * Add a listener to the PV which listens to events from the PV.
	 * 
	 * @param listener
	 */
	public void addPvListener(PVValueChangeListener listener) {
		if (pv != null)
			pv.addPVValueChangeListener(listener);
	}

	/**
	 * remove the listener from the PV
	 * 
	 * @param listener
	 */
	public void removePvListener(PVValueChangeListener listener) {
		pv.removePVValueChangeListener(listener);
	}
}
