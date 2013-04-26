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

/** CellEditor for values that offer distinct set of options.
 *
 *  <p>Implementation based on ComboBoxCellEditor of
 *  Eclipse 3.7.1 which is not intended to be sub-classed.
 *  While ComboBoxCellEditor uses Integer for a value,
 *  this editor uses the String as a value.
 *
 *  @author Kay Kasemir
 */
abstract public class OptionListCellEditor extends CellEditor
{
    /** When the user presses 'return' in the combo box,
     *  a property update will be fired.
     *
     *  <p>While in 'live' mode, this can open
     *  a confirmation message dialog ("Change property of live scan?").
     *  When that dialog opens, the combo box looses focus,
     *  which by default would trigger another property update,
     *  and in 'live' mode result in another message dialog...
     *
     *  <p>This flag tracks if we're in the 'return' handling code
     *  to avoid the extra update from loss-of-focus.
     */
    private boolean handling_return_key = false;

    /** Labels to display for available options */
    private String[] labels;

    /** Initialize
     *  @param parent Parent widget
     *  @return Labels that will appear in the editor as available options
     */
    public OptionListCellEditor(final Composite parent, final String[] labels)
    {
        // Parent constructor calls createControl() to create combo
        super(parent);
        this.labels = labels;
        initCombo();
        setValueValid(true);
    }

    /** @return Control correctly casted as {@link CCombo} */
    private CCombo getCombo()
    {
        return (CCombo) getControl();
    }

    /** Initialize combo box content */
    private void initCombo()
    {
        final CCombo combo = getCombo();
        combo.setItems(labels);
        combo.select(0);
    }

    /** The default editor is a read-only combo,
     *  but derived classes can override
     *  @return true if the editor is read-only
     */
    protected boolean isReadOnly()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    final protected Control createControl(final Composite parent)
    {
        final int style = isReadOnly() ? SWT.READ_ONLY : 0;
        final CCombo combo = new CCombo(parent, style);
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
                if (e.keyCode == 13)
                {
                    handling_return_key = true;
                    keyReleaseOccured(e);
                    handling_return_key = false;
                }
                else
                    keyReleaseOccured(e);
            }
        });
        combo.addTraverseListener(new TraverseListener()
        {
            @Override
            public void keyTraversed(final TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN)
                {
                    e.doit = false;
                }
            }
        });

        // Accept when loosing focus (different view selected)
        combo.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(final FocusEvent e)
            {
                if (! handling_return_key)
                    OptionListCellEditor.this.focusLost();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void doSetFocus()
    {
        getControl().setFocus();
    }

    /** @return Currently selected item in the combo as Enum */
    @Override
    final protected Object doGetValue()
    {
        final String label = getCombo().getText();
        return optionForLabel(label);
    }

    /** To be implemented by derived class:
     *  @param label Label that is currently selected in the editor
     *  @return Corresponding value of the property
     */
    abstract protected Object optionForLabel(String label);

    /** @param value Enum-typed value that should define
     *               the current combo selection
     */
    @Override
    final protected void doSetValue(final Object value)
    {
        final String label = labelForOption(value);
        final CCombo combo = getCombo();
        // Possibly select this label in list
        combo.select(getSelectionIndex(label));
        // In any case, display current value
        combo.setText(label);
    }

    /** To be implemented by derived class:
     *  @param value Value of the property
     *  @return Corresponding label to use in the editor
     */
    abstract protected String labelForOption(Object value);

    /** @param label Label that is currently entered/selected in the combo box
     *  @return Index of corresponding combo box entry, using 0 if there is no match
     */
    final protected int getSelectionIndex(final String label)
    {
        for (int i=0; i<labels.length; ++i)
            if (labels[i].equals(label))
                return i;
        return 0;
    }
}
