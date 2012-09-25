/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.DBRType;

/**
 * Abstract class for value factory.
 * <p>
 * NOTE: this class is extensible as per Bastian request so that DESY can hook
 * a different type factory. This is a temporary measure until the problem
 * is solved in better, more general way, so that data sources
 * can work only with data source specific types, while allowing
 * conversions to normalized type through operators. The contract of this
 * class is, therefore, expected to change.
 *
 * @author carcassi
 */
public interface TypeFactory<TValue, TEpicsValue, TEpicsMeta> {

    TValue createValue(final TEpicsValue value,
            final TEpicsMeta metadata, boolean disconnected);

    Class getValueType();

    DBRType getEpicsMetaType();

    DBRType getEpicsValueType();

    boolean isArray();
}
