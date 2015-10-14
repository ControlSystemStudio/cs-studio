/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.imports;

import java.util.Iterator;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.diirt.vtype.VType;

/** {@link ValueIterator} for {@link List} of {@link IValue}
 *  @author Kay Kasemir
 */
public class ArrayValueIterator implements ValueIterator
{
    final private Iterator<VType> iter;

    public ArrayValueIterator(final List<VType> values)
    {
        iter = values.iterator();
    }

    @Override
    public boolean hasNext()
    {
        return iter.hasNext();
    }

    @Override
    public VType next() throws Exception
    {
        return iter.next();
    }

    @Override
    public void close()
    {
        // NOP
    }
}
