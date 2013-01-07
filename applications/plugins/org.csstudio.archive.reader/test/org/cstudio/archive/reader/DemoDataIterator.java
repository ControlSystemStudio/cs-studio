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
class DemoDataIterator implements ValueIterator
{
    final private String name;
    final private int start_time;
    private int i = 0;
    private boolean open = true;

    public DemoDataIterator(final String name, final int start_time)
    {
        this.name = name;
        this.start_time = start_time;
    }

    public DemoDataIterator(final String name)
    {
        this(name, 0);
    }


    @Override
    public boolean hasNext()
    {
        return i < 10;
    }

    @Override
    public VType next() throws Exception
    {
        ++i;
        return new ArchiveVString(Timestamp.of(start_time + i, 0), AlarmSeverity.NONE, "OK", name + " " + i);
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
