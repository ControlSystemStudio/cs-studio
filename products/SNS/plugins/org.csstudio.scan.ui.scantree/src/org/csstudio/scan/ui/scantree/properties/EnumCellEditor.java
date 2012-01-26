/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
public class EnumCellEditor extends CellEditor
{
    final Class<? extends Enum<?>> enum_type;
    final private String[] labels;
    private CCombo combo;

    /** Initialize
     *  @param parent Parent widget
     *  @param enum_type Enum that defines the possible values of this editor
     */
    public EnumCellEditor(final Composite parent, final Class<? extends Enum<?>> enum_type)
    {
        super(parent);
        this.enum_type = enum_type;
        
        final Enum<?>[] enum_vals = enum_type.getEnumConstants();
        labels = new String[enum_vals.length];
        for (int i=0; i<labels.length; ++i)
            labels[i] = enum_vals[i].toString();
        
        combo.setItems(labels);
        combo.select(0);
        setValueValid(true);
    }

    /** {@inheritDoc} */
    @Override
    protected Control createControl(final Composite parent)
    {
        combo = new CCombo(parent, SWT.READ_ONLY);
        combo.setFont(parent.getFont());
        
        addGuiTweaks(combo);
        
        return combo;
    }

    /** Tweaks to the CCombo behavior, copied from AbstractComboBoxCellEditor */
    private void addGuiTweaks(final CCombo combo)
    {
        // Allow 'return' to accept, 'escape' to abort the selection
        combo.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                keyReleaseOccured(e);
            }
        });
        combo.addTraverseListener(new TraverseListener()
        {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        // Accept when loosing focus (different view selected)
        combo.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e) {
                EnumCellEditor.this.focusLost();
            }
        });
    }
    
    /** Applies the currently selected value and deactivates the cell editor */
    void applyEditorValueAndDeactivate()
    {
        final Object newValue = doGetValue();
        markDirty();
        final boolean isValid = isCorrect(newValue);
        setValueValid(isValid);

        fireApplyEditorValue();
        deactivate();
    }

    /** {@inheritDoc} */
    @Override
    protected void doSetFocus()
    {
        combo.setFocus();
    }
    
    /** @return Currently selected item in the combo as Enum */
    @Override
    protected Object doGetValue()
    {
        final String label = combo.getText();
        final Enum<?>[] options = enum_type.getEnumConstants();
        return options[getSelectionIndex(label)];
    }

    /** @param value Enum-typed value that should define
     *               the current combo selection 
     */
    @Override
    protected void doSetValue(final Object value)
    {
        if (! (value instanceof Enum))
            throw new Error("EnumCellEditor called with " + value.getClass().getName());
        
        combo.select(getSelectionIndex(value.toString()));
    }

    /** @param label Label of enum value
     *  @return Index of corresponding combo box entry
     */
    private int getSelectionIndex(final String label)
    {
        for (int i=0; i<labels.length; ++i)
            if (labels[i].equals(label))
                return i;
        return 0;
    }
}
