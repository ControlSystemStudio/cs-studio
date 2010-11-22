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

    private static <T> TypeSupport<T> immutableTypeSupport(Class<T> clazz) {
        return new TimedTypeSupport<T>() {

            @Override
            public Notification<T> prepareNotification(T oldValue, T newValue) {
                if (NullUtils.equalsOrBothNull(oldValue, newValue))
                    return new Notification<T>(false, null);
                return new Notification<T>(true, newValue);
            }

            @Override
            public TimeStamp extractTimestamp(T object) {
                return ((Time) object).getTimeStamp();
            }
        };
    }

    private static boolean installed = false;

    static void install() {
        // Install only once
        if (installed)
            return;

        addScalar();
        addMultiScalar();
        addStatistics();
        addArray();
        addList();

        installed = true;
    }

    private static void addScalar() {
        // Add support for all scalars: simply return the new value
        TypeSupport.addTypeSupport(Scalar.class, immutableTypeSupport(Scalar.class));
    }

    private static void addMultiScalar() {
        // Add support for all multi scalars: simply return the new value
        TypeSupport.addTypeSupport(MultiScalar.class, immutableTypeSupport(MultiScalar.class));
    }

    private static void addArray() {
        // Add support for all arrays: simply return the new value
        TypeSupport.addTypeSupport(Array.class, immutableTypeSupport(Array.class));
    }

    private static void addStatistics() {
        // Add support for statistics: simply return the new value
        TypeSupport.addTypeSupport(Statistics.class, immutableTypeSupport(Statistics.class));
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
