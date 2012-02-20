/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.reader.MergingValueIterator;
import org.csstudio.archive.reader.SpreadsheetIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.junit.Test;

/** JUnit test of the value iterators
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueIteratorUnitTest
{
    /** Value iterator that produces demo samples 1 ... 10 */
    static class DemoDataIterator implements ValueIterator
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
        public IValue next() throws Exception
        {
            ++i;
            return ValueFactory.createStringValue(TimestampFactory.fromDouble(start_time + i),
                    ValueFactory.createOKSeverity(), "Test",
                    IValue.Quality.Original, new String[] { name + " " + i });
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

    /** Demo the DemoDataIterator */
    @Test
    public void testDemoDataIterator() throws Exception
    {
        System.out.println("DemoDataIterator");
        final ValueIterator iter = new DemoDataIterator("Demo");
        int count = 0;
        while (iter.hasNext())
        {
            System.out.println(iter.next());
            ++count;
        }
        assertEquals(10, count);
        iter.close();
    }

    /** Merge two DemoDataIterators */
    @Test
    public void testMergingValueIterator() throws Exception
    {
        System.out.println("MergingValueIterator");
        final DemoDataIterator iter1 = new DemoDataIterator("A");
        final DemoDataIterator iter2 = new DemoDataIterator("B");
        final ValueIterator merge = new MergingValueIterator(
                new ValueIterator[] { iter1, iter2 });
        int count = 0;
        while (merge.hasNext())
        {
            System.out.println(merge.next());
            ++count;
        }
        assertEquals(20, count);
        assert(iter1.isOpen());
        assert(iter2.isOpen());
        merge.close();
        assert(! iter1.isOpen());
        assert(! iter2.isOpen());
    }

    /** Show two DemoDataIterators in a spreadsheet.
     *  Time stamps align perfectly
     */
    @Test
    public void testSpreadsheetIterator() throws Exception
    {
        runSheetTest(0);
    }

    /** Show two DemoDataIterators in a spreadsheet.
     *  Second iter lags first one in time.
     */
    @Test
    public void testSpreadsheetIterator2() throws Exception
    {
        runSheetTest(5);
    }

    private void runSheetTest(final int time_lag) throws Exception
    {
        System.out.println("SpreadsheetIterator");
        final DemoDataIterator iter1 = new DemoDataIterator("A");
        final DemoDataIterator iter2 = new DemoDataIterator("B", time_lag);
        final SpreadsheetIterator sheet = new SpreadsheetIterator(
                new ValueIterator[] { iter1, iter2 });
        int count = 0;
        while (sheet.hasNext())
        {
            System.out.print(sheet.getTime());
            final IValue[] values = sheet.next();
            for (IValue value : values)
                System.out.print("\t" + (value == null ? "-" : value.format()));
            System.out.println();
            ++count;
        }
        assertEquals(10 + time_lag, count);
        assert(iter1.isOpen());
        assert(iter2.isOpen());
        sheet.close();
        assert(! iter1.isOpen());
        assert(! iter2.isOpen());
    }
}
