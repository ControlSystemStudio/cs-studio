/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements support for basic standard java types.
 *
 * @author carcassi
 */
public class BasicTypeSupport {

    private BasicTypeSupport() {
        // Don't instantiate, utility class.
    }

    private static volatile boolean installed = false;

    /**
     * Installs type support.
     */
    public static void install() {
        // Install only once
        if (installed) {
            return;
        }

        // Add support for lists
        addList();
        
        // Add support for maps
        // TODO should actually return immutable maps?
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(Map.class));
        
        // Add support for numbers and strings
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(Number.class));
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(String.class));

        installed = true;
    }

    private static void addList() {
        // TODO this is actually very broken:
        // Clients can modify the list contents! (should return immutable list)
        // PVManager is modifying the same list! (client will see dirty changes)
        TypeSupport.addTypeSupport(new NotificationSupport<List>(List.class) {

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Notification<List> prepareNotification(List oldValue, final List newValue) {
                // Initialize value if never initialized
                if (oldValue == null) {
                    oldValue = new ArrayList();
                }

                boolean notificationNeeded = false;

                // Check all the elements in the list and use StandardTypeSupport
                // to understand whether any needs notification.
                // Notification is done only if at least one element needs notification.
                for (int index = 0; index < newValue.size(); index++) {
                    if (oldValue.size() <= index) {
                        oldValue.add(null);
                    }

                    if (newValue.get(index) != null) {
                        Notification itemNotification = NotificationSupport.notification(oldValue.get(index), newValue.get(index));
                        if (itemNotification.isNotificationNeeded()) {
                            notificationNeeded = true;
                            oldValue.set(index, itemNotification.getNewValue());
                        }
                    }
                }

                // Shrink the list if more elements are there
                while (oldValue.size() > newValue.size()) {
                    oldValue.remove(oldValue.size() - 1);
                }

                return new Notification<List>(notificationNeeded, oldValue);
            }
        });
    }

}
