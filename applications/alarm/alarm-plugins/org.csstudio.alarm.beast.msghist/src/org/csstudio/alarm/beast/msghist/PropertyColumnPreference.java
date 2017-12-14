/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import java.io.Serializable;
import org.eclipse.ui.IMemento;

/**
 * Settings for a "Property" table column: Name of property to display, suggested columns size, ...
 *
 * @author Kay Kasemir
 * @author Borut Terpinc
 */
@SuppressWarnings("nls")
public class PropertyColumnPreference implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The memento tag name of the visible property */
    private static final String M_VISIBLE = "visible"; //$NON-NLS-1$
    /** The memento tag name of the key by which the columns are ordered */
    private static final String M_ORDER_KEY = "orderKey"; //$NON-NLS-1$
    /** The memento tag name of the minimum width of the column */
    private static final String M_MIN_WIDTH = "minWidth"; //$NON-NLS-1$
    /** The memento tag name of the weight of the column */
    private static final String M_WEIGHT = "weight"; //$NON-NLS-1$
    /** The memento tag name of the visible name of the column */
    private static final String M_NAME = "name"; //$NON-NLS-1$

    final String name;
    int size;
    int weight;
    boolean visible;

    /**
     * Initialize
     *
     * @param name
     * @param size
     * @param weight
     * @param visible
     */
    public PropertyColumnPreference(final String name, int size, int weight, boolean visible) {
        this.name = name;
        this.size = size;
        this.weight = weight;
        this.visible = visible;
    }

    /**
     * Parse PropertyColumnPreference from preference string
     *
     * @param pref_string
     *            String as stored in preferences
     * @return PropertyColumnPreference
     * @throws Exception
     *             On error
     */
    public static PropertyColumnPreference fromString(final String pref_string) throws Exception {
        final String[] pieces = pref_string.split(",");
        if (pieces.length != 3)
            throw new Exception("Error in property column preference '" + pref_string + "'");
        try {
            final String name = pieces[0].trim();
            final int size = Integer.parseInt(pieces[1].trim());
            final int weight = Integer.parseInt(pieces[2].trim());
            return new PropertyColumnPreference(name, size, weight, true);
        } catch (NumberFormatException ex) {
            throw new Exception("Cannot parse size, weight from '" + pref_string + "'");
        }
    }

    /**
     * Restore the columns from the memento. The columns are expected to be children of the given memento.
     *
     * @param memento
     *            the source
     * @param defaultColumns
     *            default column properties
     * @return columns restored from the memento with all parameters properly set
     */
    public static PropertyColumnPreference[] restoreColumns(IMemento memento,
            PropertyColumnPreference[] defaultColumns) {
        PropertyColumnPreference[] columns = new PropertyColumnPreference[defaultColumns.length];
        int front = 0;

        for (PropertyColumnPreference column : defaultColumns) {
            IMemento m = memento.getChild(column.getName());

            // column is not in memento. Don't change default values and place
            // it in the first available slot.
            if (m == null) {
                for (; front < defaultColumns.length; front++) {
                    if (columns[front] == null) {
                        columns[front] = column;
                        break;
                    }
                }
                continue;
            }

            Integer size = m.getInteger(M_MIN_WIDTH);
            Integer weight = m.getInteger(M_WEIGHT);
            Integer order = m.getInteger(M_ORDER_KEY);

            column.setVisible(m.getBoolean(M_VISIBLE));
            column.setSize(size == null ? 0 : size);
            column.setWeight(weight == null ? 0 : weight);

            // invalid memento settings. Place column in the first available slot
            if (order >= columns.length || columns[order] != null) {
                for (; front < defaultColumns.length; front++) {
                    if (columns[front] == null) {
                        columns[front] = column;
                        break;
                    }
                }
            } else {
                columns[order] = column;
            }
        }
        return columns;
    }

    /**
     * Save the column property into the given memento. The columns are stored as children of the memento, one child per column.
     * Each child contains information required to restore the current visuble state of the column.
     *
     * @param memento
     *            the destination memento
     * @param columns
     *            the columns to store
     */
    public static void saveColumns(IMemento memento, PropertyColumnPreference[] columns) {
        if (memento == null) {
            return;
        }

        for (int i = 0; i < columns.length; i++) {
            IMemento m = memento.createChild(columns[i].getName());
            m.putBoolean(M_VISIBLE, columns[i].isVisible());
            m.putInteger(M_ORDER_KEY, i);
            m.putInteger(M_WEIGHT, columns[i].getWeight());
            m.putInteger(M_MIN_WIDTH, columns[i].getSize());
            m.putString(M_NAME, columns[i].getName());
        }
    }

    /** @return Property Name */
    public String getName() {
        return name;
    }

    /** @return Minimum column size */
    public int getSize() {
        return size;
    }

    /** @return Column weight for resize */
    public int getWeight() {
        return weight;
    }

    /** @return Whether column is visible */
    public boolean isVisible() {
        return visible;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return name;
    }
}
