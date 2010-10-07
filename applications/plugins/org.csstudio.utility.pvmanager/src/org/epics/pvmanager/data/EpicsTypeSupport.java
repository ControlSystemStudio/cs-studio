/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.NullUtils;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.TimedTypeSupport;
import org.epics.pvmanager.TypeSupport;

/**
 * Adds support for EPICS standard types.
 *
 * @author carcassi
 */
class EpicsTypeSupport {

    private static boolean installed = false;

    static void install() {
        // Install only once
        if (installed)
            return;

        addScalar();
        addMultiScalar();
        addStatistics();
        addList();

        installed = true;
    }

    private static void addScalar() {
        // Add support for all scalars: simply return the new value
        TypeSupport.addTypeSupport(Scalar.class, new TimedTypeSupport<Scalar>() {
            @Override
            public Notification<Scalar> prepareNotification(Scalar oldValue, Scalar newValue) {
                if (NullUtils.equalsOrBothNull(oldValue, newValue))
                    return new Notification<Scalar>(false, null);
                return new Notification<Scalar>(true, newValue);
            }

            @Override
            public TimeStamp extractTimestamp(Scalar object) {
                Time time = (Time) object;
                return time.getTimeStamp();
            }

        });
    }

    private static void addMultiScalar() {
        // Add support for all scalars: simply return the new value
        TypeSupport.addTypeSupport(MultiScalar.class, new TimedTypeSupport<MultiScalar>() {
            @Override
            public Notification<MultiScalar> prepareNotification(MultiScalar oldValue, MultiScalar newValue) {
                if (NullUtils.equalsOrBothNull(oldValue, newValue))
                    return new Notification<MultiScalar>(false, null);
                return new Notification<MultiScalar>(true, newValue);
            }

            @Override
            public TimeStamp extractTimestamp(MultiScalar object) {
                Time time = (Time) object;
                return time.getTimeStamp();
            }

        });
    }

    private static void addStatistics() {
        // Add support for statistics: simply return the new value
        TypeSupport.addTypeSupport(Statistics.class, new TypeSupport<Statistics>() {
            @Override
            public Notification<Statistics> prepareNotification(Statistics oldValue, Statistics newValue) {
                if (NullUtils.equalsOrBothNull(oldValue, newValue))
                    return new Notification<Statistics>(false, null);
                return new Notification<Statistics>(true, newValue);
            }
        });
    }

    private static void addList() {
        TypeSupport.addTypeSupport(List.class, new TypeSupport<List>() {

            @Override
            @SuppressWarnings("unchecked")
            public Notification<List> prepareNotification(List oldValue, List newValue) {
                // Initialize value if never initialized
                if (oldValue == null)
                    oldValue = new ArrayList();

                boolean notificationNeeded = false;

                // Check all the elements in the list and use StandardTypeSupport
                // to understand whether any needs notification.
                // Notification is done only if at least one element needs notification.
                for (int index = 0; index < newValue.size(); index++) {
                    if (oldValue.size() <= index) {
                        oldValue.add(null);
                    }

                    if (newValue.get(index) != null) {
                        Notification itemNotification = TypeSupport.notification(oldValue.get(index), newValue.get(index));
                        if (itemNotification.isNotificationNeeded()) {
                            notificationNeeded = true;
                            oldValue.set(index, itemNotification.getNewValue());
                        }
                    }
                }

                return new Notification<List>(notificationNeeded, oldValue);
            }
        });
    }
    

}
