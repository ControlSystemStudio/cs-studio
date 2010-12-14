/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.NullUtils;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.TimedTypeSupport;

/**
 * Adds support for control system standard types defined in this package.
 *
 * @author carcassi
 */
public class TypeSupport {

    private static <T> org.epics.pvmanager.TypeSupport<T> immutableTypeSupport(Class<T> clazz) {
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

    /**
     * Installs type support. This should only be called by either DataSources
     * or ExpressionLanguage libraries that require support for these types.
     */
    public static void install() {
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
        org.epics.pvmanager.TypeSupport.addTypeSupport(Scalar.class, immutableTypeSupport(Scalar.class));
    }

    private static void addMultiScalar() {
        // Add support for all multi scalars: simply return the new value
        org.epics.pvmanager.TypeSupport.addTypeSupport(MultiScalar.class, immutableTypeSupport(MultiScalar.class));
    }

    private static void addArray() {
        // Add support for all arrays: simply return the new value
        org.epics.pvmanager.TypeSupport.addTypeSupport(Array.class, immutableTypeSupport(Array.class));
    }

    private static void addStatistics() {
        // Add support for statistics: simply return the new value
        org.epics.pvmanager.TypeSupport.addTypeSupport(Statistics.class, immutableTypeSupport(Statistics.class));
    }

    private static void addList() {
        org.epics.pvmanager.TypeSupport.addTypeSupport(List.class, new org.epics.pvmanager.TypeSupport<List>() {

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
                        Notification itemNotification = org.epics.pvmanager.TypeSupport.notification(oldValue.get(index), newValue.get(index));
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
