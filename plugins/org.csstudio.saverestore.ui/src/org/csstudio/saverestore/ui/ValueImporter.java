/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.csstudio.saverestore.data.VDisconnectedData;
import org.diirt.vtype.Time;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VInt;
import org.diirt.vtype.VType;

/**
 *
 * <code>ValueImporter</code> provides a list of values for a given set of pv names at specific time. The origin of
 * values can be anything (e.g. optics server, file) and is up to the implementor how to provide them.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public interface ValueImporter {

    /** The extension point id */
    public static final String EXT_POINT = "org.csstudio.saverestore.ui.valueimporter";

    /**
     * Returns the values for the given list of pv names at the given timestamp. The implementor does not need to value
     * the timestamp. The returned values should be of type VType. They do not need to be all of the same type, some can
     * be {@link VDouble}, others {@link VInt} etc. While not necessary it is desired for the values to be also
     * instances of {@link Time}. If a value for a particular pv is missing it will be shown on the UI as
     * {@link VDisconnectedData}.
     * </p>
     * <p>
     * This method will always be called on a non-UI thread.
     * </p>
     *
     * @param pvNames the list of names for which the values are requested
     * @param timestamp the timestamp at which the values are requested
     * @return a map of name value pairs, where the name is the PV name given by <code>pvNames</code> parameter and
     *         value is the PV value at given <code>timestamp</code>
     */
    Map<String, VType> getValuesForPVs(List<String> pvNames, Instant timestamp);
}
