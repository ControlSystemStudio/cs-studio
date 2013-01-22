/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.util.NullUtils;

/**
 * Dedicated notification type support.
 * 
 * @param <T> type for which the notifications are prepared
 * @author bknerr
 * @since 17.01.2011
 */
public abstract class NotificationSupport<T> extends TypeSupport<T> {

    /**
     * Creates a new notification type support.
     *
     * @param clazz the type being supported
     */
    public NotificationSupport(Class<T> clazz) {
        super(clazz, NotificationSupport.class);
    }
    
    /**
     * Returns the final value by using the appropriate type support.
     *
     * @param <T> the type of the value
     * @param oldValue the oldValue, which was previously in the previous notification
     * @param newValue the newValue, which was computed during the scanning
     * @return the value to be notified
     */
    public static <T> Notification<T> notification(final T oldValue, final T newValue) {
        @SuppressWarnings("unchecked")
        Class<T> typeClass = (Class<T>) newValue.getClass();
        NotificationSupport<T> support =
            (NotificationSupport<T>) findTypeSupportFor(NotificationSupport.class,
                                                              typeClass);
        return support.prepareNotification(oldValue, newValue);
    }    
    
    /**
     * Given the old and new value, prepare the final value that will be notified.
     * This method is guaranteed to be called in the notification thread (the
     * UI thread). This method may either update the old value or return the new
     * value, depending on whether the type is immutable or what is more efficient.
     *
     * @param oldValue the oldValue, which was previously in the previous notification
     * @param newValue the newValue, which was computed during the scanning
     * @return the value to be notified
     */
    public abstract Notification<T> prepareNotification(final T oldValue, final T newValue);
    
    /**
     * Support for notification of immutable types. Notification is enabled if
     * the value changed according to {@link Object#equals(java.lang.Object) }.
     *
     * @param <T> type for which to add support
     * @param clazz type for which to add support
     * @return support for immutable objects of the given type
     */
    public static <T> NotificationSupport<T> immutableTypeSupport(final Class<T> clazz) {
        return new NotificationSupport<T>(clazz) {
          @Override
          public Notification<T> prepareNotification(final T oldValue, final T newValue) {
              if (NullUtils.equalsOrBothNull(oldValue, newValue)) {
                return new Notification<T>(false, null);
            }
              return new Notification<T>(true, newValue);
          }
        };
    }
}
