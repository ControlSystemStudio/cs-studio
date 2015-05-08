package org.csstudio.opibuilder.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.simplepv.AbstractPVFactory;
import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.SimplePVLayer;
import org.eclipse.swt.widgets.Display;

/**The factory to create a PV for BOY. It will create either Utility PV or PVManager PV
 * which depends on the preference settings.
 * @author Xihui Chen
 *
 */
public class BOYPVFactory{

    /**
     * The default background thread for PV change event notification.
     */
    private final static ExecutorService BOY_PV_THREAD = Executors
            .newSingleThreadExecutor();

    private final static ExceptionHandler exceptionHandler = new ExceptionHandler() {
        @Override
        public void handleException(Exception ex) {
            ErrorHandlerUtil.handleError("Error from pv connection layer: ", ex);
        }
    };

    /**Create a PV. If it is using PV Manager, buffered all values is false and max update
     * rate is determined by GUI Refresh cycle. In RAP, this method should be called in UI thread. If not, please give the display
     * by calling {@link #createPV(String, boolean, Display)}.
     * @param name name of the PV.
     * @return the PV
     * @throws Exception
     * @see {@link #createPV(String, boolean, int)}
     */
    public static IPV createPV(final String name) throws Exception {
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
    public static IPV createPV(final String name, final boolean bufferAllValues) throws Exception{
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
    public static IPV createPV(final String name,
            final boolean bufferAllValues, final int updateDuration) throws Exception{

            String pvConnectionLayer = PreferencesHelper.getPVConnectionLayer();
            if(pvConnectionLayer == null || pvConnectionLayer.isEmpty())
                throw new Exception("PV connection layer is not configured in preference.");
            AbstractPVFactory pvFactory = SimplePVLayer.getPVFactory
                    (pvConnectionLayer);
            if(pvFactory == null)
                throw new Exception("No such PVFactory extension available: " + pvConnectionLayer);
            return pvFactory.createPV(
                    name, false, updateDuration, bufferAllValues,  BOY_PV_THREAD, exceptionHandler);
    }

}
