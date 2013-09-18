/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.eclipse.swt.widgets.Composite;

/** CellEditor for Enum-type value
 *
 *  <p>Implementation based on ComboBoxCellEditor of
 *  Eclipse 3.7.1 which is not intended to be sub-classed.
 *  While ComboBoxCellEditor uses Integer for a value,
 *  this editor uses the Enum itself as a value,
 *  utilizing the toString() (not the name()!) representation for options.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EnumCellEditor extends OptionListCellEditor
{
    final private Class<? extends Enum<?>> enum_type;

    /** Initialize
     *  @param parent Parent widget
     *  @param enum_type Enum that defines the possible values of this editor
     */
    public EnumCellEditor(final Composite parent, final Class<? extends Enum<?>> enum_type)
    {
        super(parent, getLabels(enum_type));
        this.enum_type = enum_type;
    }

    /** @param devices Enum type to edit
     *  @return Labels to use in editor
     */
    private static String[] getLabels(final Class<? extends Enum<?>> enum_type)
    {
        final Enum<?>[] enum_vals = enum_type.getEnumConstants();
        final String[] labels = new String[enum_vals.length];
        for (int i=0; i<labels.length; ++i)
            labels[i] = enum_vals[i].toString();
        return labels;
    }

    /** Turn label into enum value */
    @Override
    protected Object optionForLabel(final String label)
    {
        final Enum<?>[] options = enum_type.getEnumConstants();
        return options[getSelectionIndex(label)];
    }

    /** Turn enum value into label */
    @Override
    protected String labelForOption(final Object value)
    {
        if (! (value instanceof Enum))
            throw new Error("EnumCellEditor called with " + value.getClass().getName());
        return value.toString();
    }
}
