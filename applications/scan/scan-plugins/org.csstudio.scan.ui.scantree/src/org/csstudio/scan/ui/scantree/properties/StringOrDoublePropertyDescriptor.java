/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.util.StringOrDouble;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Descriptor for a property that's either a <code>String</code> or a <code>Double</code>
 *  @author Kay Kasemir
 */
public class StringOrDoublePropertyDescriptor extends TextPropertyDescriptor
{
    /** {@link LabelProvider} that decorates {@link String}s with quotes */
    static class StringOrDoubleLabelProvider extends LabelProvider
    {
        @Override
        public String getText(final Object element)
        {
            return StringOrDouble.quote(element);
        }
    }

    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     */
    public StringOrDoublePropertyDescriptor(final String id, final String label)
    {
        super(id, label);
    }

    /** Decorate Strings with quotes.
     *  {@inheritDoc}
     */
    @Override
    public ILabelProvider getLabelProvider()
    {
        return new StringOrDoubleLabelProvider();
    }

    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        final CellEditor editor = new StringOrDoubleCellEditor(parent);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
