/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Constants that can be used across different values.
 *
 * @author carcassi
 */
class Constants {

    /**
     * Empty set of no alarms.
     */
    static final Set<String> NO_ALARMS =
            Collections.emptySet();

    /**
     * List of possible alarms. Should be used for all simulated values.
     */
    static final List<String> POSSIBLE_ALARM_STATUS =
            Collections.unmodifiableList(Arrays.asList("BROKEN_SIMULATOR"));

    /**
     * Common number format for all VDoubles.
     */
    static final NumberFormat DOUBLE_FORMAT = new DecimalFormat();
}
