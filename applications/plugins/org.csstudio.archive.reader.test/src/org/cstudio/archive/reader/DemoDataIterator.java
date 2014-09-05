/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVString;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.util.time.Timestamp;

/** Value iterator that produces demo samples 1 ... 10
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class DemoDataIterator implements ValueIterator
{
    final private VType[] values;
    private int i = 0;
    private boolean open = true;

    public static DemoDataIterator forStrings(final String name, final int start_time)
    {
        final VType[] values = new VType[10];
        for (int i=0; i<10; ++i)
            values[i] = new ArchiveVString(Timestamp.of(start_time + i + 1, 0), AlarmSeverity.NONE, "OK", name + " " + (i + 1));
        return new DemoDataIterator(values);
    }

    public static DemoDataIterator forStrings(final String name)
    {
        return forStrings(name, 0);
    }
    
    public DemoDataIterator(final VType[] values)
    {
        this.values = values;
    }

    @Override
    public boolean hasNext()
    {
        return i < values.length;
    }

    @Override
    public VType next() throws Exception
    {
        return values[i++];
    }

    @Override
    public void close()
    {
        if (!open)
            throw new IllegalStateException("Closed twice");
        open = false;
    }

    public boolean isOpen()
    {
        return open;
    }
}
