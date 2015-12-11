package org.csstudio.saverestore.ui;

import java.util.List;
import java.util.Map;

/**
 *
 * <code>ReadbackProvider</code> provides readback pv names for given setpoint pv names. The source of readback names
 * can be anything.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface ReadbackProvider {

    /** The extension point id */
    public static final String EXT_POINT = "org.csstudio.saverestore.ui.readbackprovider";

    /**
     * Returns the list of readback PV names for the given list of setpoint PV names. For every setpoint PV that a
     * readback is known there should be an entry in the returned map.
     * </p>
     * <p>
     * This method will never be called from the UI thread.
     * </p>
     *
     * @param setPointNames the list of pv names for which the readback names are requested
     * @return a map of key-value pairs, where the key is a setpoint name and value is the corresponding readback name
     */
    Map<String, String> getReadbacks(List<String> setpointNames);
}
