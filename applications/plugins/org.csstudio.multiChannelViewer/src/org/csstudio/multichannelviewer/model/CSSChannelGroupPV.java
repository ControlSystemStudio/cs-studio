package org.csstudio.multichannelviewer.model;

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VMultiDouble;

public class CSSChannelGroupPV {

	// PvManager PV to define the connection with the list of specified pv's
	private PVReader<VMultiDouble> pv;

	/**
	 * Add a listener to the PV which listens to events from the PV.
	 * 
	 * @param listener
	 */
	public void addPvListener(PVReaderListener listener) {
		if (pv != null)
			pv.addPVReaderListener(listener);
	}

	/**
	 * remove the listener from the PV
	 * 
	 * @param listener
	 */
	public void removePvListener(PVReaderListener listener) {
		pv.removePVReaderListener(listener);
	}
}
