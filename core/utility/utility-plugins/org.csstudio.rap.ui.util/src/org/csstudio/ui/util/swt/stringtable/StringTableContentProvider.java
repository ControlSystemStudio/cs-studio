/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for List of String or String[].
 *  <p>
 *  Will provide Integer index values which allow the label provider
 *  and editor to access the correct elements in the List.
 *  @param <T> String or String[]
 *  @author Kay Kasemir, Xihui Chen
 */
class StringTableContentProvider<T> implements IStructuredContentProvider
{
    private static final long serialVersionUID = 8338633557950338463L;
    /** Magic number for the final 'add' element */
    final public static Integer ADD_ELEMENT = new Integer(-1);
    private List<T> items;

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public void inputChanged(final Viewer viewer, final Object old, final Object new_input)
    {
        items = (List<T>) new_input;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getElements(Object arg0)
    {
        int N = items.size();
        final Integer result[] = new Integer[N+1];
        for (int i=0; i<N; ++i)
            result[i] = i;
        result[N] = ADD_ELEMENT;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // NOP
    }
}
