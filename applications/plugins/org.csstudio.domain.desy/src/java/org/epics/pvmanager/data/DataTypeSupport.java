/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.NotificationSupport;
import org.epics.pvmanager.TimeSupport;
import org.epics.pvmanager.TypeSupport;
import org.epics.pvmanager.util.TimeStamp;

/**
 * Adds support for control system standard types defined in this package.
 *
 * @author carcassi
 */
public final class DataTypeSupport {

    private static boolean installed = false;

    /**
     * Installs type support. This should only be called by either DataSources
     * or ExpressionLanguage libraries that require support for these types.
     */
    public static void install() {
        // Install only once
        if (installed) {
            return;
        }

        // Add time support for everything
        TimeSupport.addTypeSupport(new TimeSupport<Time>(Time.class) {

            @Override
            public TimeStamp extractTimestamp(final Time object) {
                return object.getTimeStamp();
            }
        });

        // Add notification support for all immutable types
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(VType.class));

        installed = true;
    }

    /**
     * Constructor.
     */
    private DataTypeSupport() {
        // Don't instantiate, utility class.
    }
}
