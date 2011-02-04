/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.types;

import org.epics.pvmanager.NullUtils;
import org.epics.pvmanager.TypeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds support for standard types.
 * <p>
 * Support for collections is at the level of raw types. Adding support for
 * generic types was not investigated but the feeling is that it would only
 * complicate the implementation. Given that the type checking is really
 * done when constructing the expression, the type checking is relegated
 * to the expression language part of the API.
 *
 * @author carcassi
 */
class StandardTypeSupport {
    
    // TODO (carcassi) : Replaced by notification type support

//    private static boolean installed = false;
//
//    static void install() {
//        // Install only once
//        if (installed)
//            return;
//
//        addList();
//
//        installed = true;
//    }
//    
//    private static void addList() {
//        TypeSupport.addTypeSupport(List.class, new TypeSupport<List>() {
//
//            @Override
//            @SuppressWarnings("unchecked")
//            public Notification<List> prepareNotification(List oldValue, List newValue) {
//                // Initialize value if never initialized
//                if (oldValue == null)
//                    oldValue = new ArrayList();
//
//                boolean notificationNeeded = false;
//
//                // Check all the elements in the list and use StandardTypeSupport
//                // to understand whether any needs notification.
//                // Notification is done only if at least one element needs notification.
//                for (int index = 0; index < newValue.size(); index++) {
//                    if (oldValue.size() <= index) {
//                        oldValue.add(null);
//                    }
//
//                    if (newValue.get(index) != null) {
//                        Notification itemNotification = TypeSupport.notification(oldValue.get(index), newValue.get(index));
//                        if (itemNotification.isNotificationNeeded()) {
//                            notificationNeeded = true;
//                            oldValue.set(index, itemNotification.getNewValue());
//                        }
//                    }
//                }
//
//                return new Notification<List>(notificationNeeded, oldValue);
//            }
//        });
//    }

}
