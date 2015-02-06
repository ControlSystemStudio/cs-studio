/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.test;

import static org.junit.Assert.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.internal.DoubleValue;
import org.csstudio.data.values.internal.NumericMetaData;
import org.csstudio.data.values.internal.SeverityInstances;
import org.junit.Test;

/** Unit test of IValue Serialization
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SerializationTest
{
    volatile Object received_obj = null;

    @Test
    public void testDoubleValue() throws Exception
    {
        final double a[] =
            new double[] { 3.14, Double.NaN, Double.POSITIVE_INFINITY, 10.0 };
        final ITimestamp time = TimestampFactory.now();
        final INumericMetaData meta =
            new NumericMetaData(0.0, 10.0, 2.0, 8.0, 1.0, 9.0, 2, "a.u.");
        final ISeverity sevr = SeverityInstances.ok;
        final IDoubleValue sent_obj =
            new DoubleValue(time, sevr, "OK", meta, IValue.Quality.Original, a);

        // Create piped in and out
        final PipedOutputStream out = new PipedOutputStream();
        final PipedInputStream in = new PipedInputStream(out);

        // Check that they're connected
        out.write("Hello".getBytes());
        out.flush();
        byte[] buf = new byte[5];
        in.read(buf);
        final String test_text = new String(buf);
        assertEquals("Hello", test_text);

        // Add object streams
        final ObjectOutputStream obj_out = new ObjectOutputStream(out);
        final ObjectInputStream obj_in = new ObjectInputStream(in);

        final Thread receiver = new Thread("Receiver")
        {
            @Override
            public void run()
            {
                try
                {
                    received_obj = obj_in.readObject();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        receiver.start();

        obj_out.writeObject(sent_obj);
        obj_out.flush();
        receiver.join(2000);

        assertNotNull(received_obj);
        System.out.println("Received " + received_obj.getClass().getName());
        System.out.println(received_obj);
        assertTrue(received_obj instanceof IDoubleValue);
        assertNotSame(received_obj, sent_obj);
        assertEquals(received_obj, sent_obj);
        in.close();
    }
}
