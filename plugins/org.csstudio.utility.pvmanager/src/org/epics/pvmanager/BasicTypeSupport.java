/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implements support for basic standard java types.
 * <p>
 * For Number and String the immutable type support is used. For List and Map,
 * we check whether any element need notification; if so, a copy is made.
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
        addMap();
        
        // Add support for numbers and strings
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(Number.class));
        TypeSupport.addTypeSupport(NotificationSupport.immutableTypeSupport(String.class));

        installed = true;
    }

    private static void addList() {
        TypeSupport.addTypeSupport(new NotificationSupport<List>(List.class) {

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Notification<List> prepareNotification(List oldValue, final List newValue) {
                // Check all the elements in the list and use StandardTypeSupport
                // to understand whether any needs notification.
                // Notification is done only if at least one element needs notification.
                boolean notificationNeeded = false;
                
                if (oldValue == null || (oldValue.size() != newValue.size())) {
                    notificationNeeded = true;
                }
                
                if (newValue.isEmpty()) {
                    notificationNeeded = false;
                }
                
                int index = 0;
                while (notificationNeeded == false && index < newValue.size()) {
                    if (newValue.get(index) != null) {
                        Notification itemNotification = NotificationSupport.notification(oldValue.get(index), newValue.get(index));
                        if (itemNotification.isNotificationNeeded()) {
                            notificationNeeded = true;
                        }
                    }
                    index++;
                }
                
                if (notificationNeeded) {
                    return new Notification<>(true, (List) Collections.unmodifiableList(new ArrayList<Object>(newValue)));
                } else {
                    return new Notification<>(false, oldValue);
                }
            }
        });
    }

    private static void addMap() {
        TypeSupport.addTypeSupport(new NotificationSupport<Map>(Map.class) {

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Notification<Map> prepareNotification(Map oldValue, final Map newValue) {
                // Check all the elements in the map and use StandardTypeSupport
                // to understand whether any needs notification.
                // Notification is done only if at least one element needs notification.
                boolean notificationNeeded = false;
                
                if (oldValue == null || (oldValue.size() != newValue.size())) {
                    notificationNeeded = true;
                }
                
                int index = 0;
                Iterator<Map.Entry> iterator = newValue.entrySet().iterator();
                while (notificationNeeded == false && iterator.hasNext()) {
                    Entry entry = iterator.next();
                    Object key = entry.getKey();
                    if (entry.getValue() != null) {
                        Notification itemNotification = NotificationSupport.notification(oldValue.get(key), entry.getValue());
                        if (itemNotification.isNotificationNeeded()) {
                            notificationNeeded = true;
                        }
                    }
                }
                
                if (notificationNeeded) {
                    return new Notification<>(true, (Map) Collections.unmodifiableMap(new HashMap<>(newValue)));
                } else {
                    return new Notification<>(false, oldValue);
                }
            }
        });
    }

}
