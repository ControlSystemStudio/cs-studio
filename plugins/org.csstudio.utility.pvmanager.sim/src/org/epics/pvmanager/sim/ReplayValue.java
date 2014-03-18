/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/**
 * Value object for replay function. Adds introspection utilities to substitute
 * values from another ReplayValue.
 * <p>
 * Classes extending this class must include a default constructor.
 *
 * @author carcassi
 */
class ReplayValue {

    // Introspected fields for ReplyValue classes
    private static Map<Class<?>, List<Field>> fields = new ConcurrentHashMap<Class<?>, List<Field>>();
    private static final Logger log = Logger.getLogger(ReplayValue.class.getName());

    // TimeStamp support
    @XmlAttribute @XmlJavaTypeAdapter(value=XmlTimeStampAdapter.class)
    Timestamp timeStamp;

    public Timestamp getTimestamp() {
        return timeStamp;
    }

    /**
     * Uses reflection to determine the non-static fields.
     *
     * @param clazz a ReplayValue class
     * @param props list of properties to add
     * @return the props argument, filled with all fields
     */
    private static List<Field> calculateFields(Class<?> clazz, List<Field> props) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                props.add(field);
            }
        }
        
        if (clazz.getSuperclass() != null) {
            calculateFields(clazz.getSuperclass(), props);
        }

        return props;
    }

    /**
     * The non-static fields. Uses reflection the first time, then uses the
     * cached values.
     *
     * @return a list of fields
     */
    private List<Field> properties() {
        List<Field> props = fields.get(getClass());
        if (props == null) {
            props = calculateFields(getClass(), new ArrayList<Field>());
            fields.put(getClass(), props);
        }
        return props;
    }

    /**
     * Returns a new ReplayObject with the same data.
     *
     * @return a copy of this object
     */
    ReplayValue copy() {
        try {
            ReplayValue copy = getClass().newInstance();
            copy.updateValue(this);
            return copy;
        } catch (InstantiationException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new RuntimeException("Can't copy ReplayValue", ex);
        } catch (IllegalAccessException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new RuntimeException("Can't copy ReplayValue", ex);
        }
    }

    /**
     * Changes the time by adding the given duration.
     *
     * @param duration a time duration
     */
    void adjustTime(TimeDuration duration) {
        timeStamp = timeStamp.plus(duration);
    }

    /**
     * Updates all fields with the values found in the argument's fields.
     *
     * @param obj another value of the same type
     */
    void updateValue(ReplayValue obj) {
        if (!getClass().isInstance(obj)) {
            throw new RuntimeException("Updating value " + this + " from different class " + obj);
        }

        for (Field field : properties()) {
            try {
                Object newValue = field.get(obj);
                if (newValue != null) {
                    field.set(this, newValue);
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Field " + field + " is not accessible", ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Field " + field + " had an inconsistent value", ex);
            }
        }
    }

    /**
     * Updates fields that currently have null values with the values found
     * in the argument's fields.
     *
     * @param obj another value of the same type
     */
    void updateNullValues(ReplayValue obj) {
        if (!getClass().isInstance(obj)) {
            throw new RuntimeException("Updating value " + this + " from different class " + obj);
        }

        for (Field field : properties()) {
            try {
                Object oldValue = field.get(this);
                if (oldValue == null) {
                    field.set(this, field.get(obj));
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException("Field " + field + " is not accessible", ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Field " + field + " had an inconsistent value", ex);
            }
        }
    }

}
