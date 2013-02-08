package org.csstudio.opibuilder.pvmanager;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.eclipse.swt.widgets.Display;

/**The factory to create a PV for BOY. It will create either Utility PV or PVManager PV
 * which depends on the preference settings.
 * @author Xihui Chen
 *
 */
public class BOYPVFactory{	
	

	/**Create a PV. If it is using PV Manager, buffered all values is false and max update
	 * rate is determined by GUI Refresh cycle. In RAP, this method should be called in UI thread. If not, please give the display
	 * by calling {@link #createPV(String, boolean, Display)}.
	 * @param name name of the PV.
	 * @return the PV
	 * @throws Exception
	 * @see {@link #createPV(String, boolean, int)}
	 */
	public static PV createPV(final String name) throws Exception {
		return createPV(name, false);
	}
	
	/**Create a PV. If it is using PV Manager, max update rate is determined by GUI Refresh cycle.
	 * @param name name of the PV.
	 * @param bufferAllValues if all values should be buffered. Only meaningful if it is using
	 * PV Manager.
	 * @return the PV
	 * @throws Exception
	 * @see {@link #createPV(String, boolean, int)}
	 */
	public static PV createPV(final String name, final boolean bufferAllValues) throws Exception{
		return createPV(name, bufferAllValues, 
				PreferencesHelper.getGUIRefreshCycle());
	}
	
	/**Create a PV based on PV connection layer preference.
	 * @param name name of the PV.
	 * @param bufferAllValues if all values should be buffered. Only meaningful if it is using
	 * PV Manager.
	 * @param updateDuration the fastest update duration.
	 * @return the PV
	 * @throws Exception
	 */
	public static PV createPV(final String name, 
			final boolean bufferAllValues, final int updateDuration) throws Exception{
		switch (PreferencesHelper.getPVConnectionLayer()) {
		case PV_MANAGER:
			return new PVManagerPV(name, bufferAllValues, updateDuration);
		case UTILITY_PV:
		default:
			return PVFactory.createPV(name);			
		}
		
	}

}
