/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.internal.data;

import static org.junit.Assert.*;

import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;

@SuppressWarnings("nls")
public class MetaDataTest
{
    @Test
    public void compareEnum()
    {
        final String s1[] = new String[] { "one", "two", "three" };
        final String s2[] = new String[] { "one", "two", "three" };
        final String s3[] = new String[] { "Eins", "Zwei", "Drei" };

        final IEnumeratedMetaData m1 = ValueFactory.createEnumeratedMetaData(s1); 
        final IEnumeratedMetaData m2 = ValueFactory.createEnumeratedMetaData(s2); 
        final IEnumeratedMetaData m3 = ValueFactory.createEnumeratedMetaData(s3);
        
        assertEquals("two", m2.getState(1));
        
        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }

    @Test
    public void compareNumeric()
    {
        final INumericMetaData m1 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m2 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 2, "socks");
        final INumericMetaData m3 = ValueFactory.createNumericMetaData(-10.0, 10.0, 1.0, 8.0, 0.0, 9.0, 20, "socks");
        
        assertEquals("socks", m2.getUnits());
        
        assertEquals(m1, m2);
        assertEquals(m2, m1);
        assertNotSame(m1, m2);
        assertTrue(! m1.equals(m3));
    }
    
    /** Check the metadata comparison inside the value comparison. */
    @Test
    public void compareInValues()
    {
        final ITimestamp stamp = TimestampFactory.now();
        final ISeverity severity = ValueFactory.createMajorSeverity();
        final String status = "Test";
        final INumericMetaData meta_a = ValueFactory.createNumericMetaData(0, 1, 0, 1, 0, 1, 2, "stuff");
        IValue a = new DoubleValue(stamp, severity, status, meta_a,
                IValue.Quality.Original, new double [] { 1.0, 2.0 });
        
        final INumericMetaData meta_b = ValueFactory.createNumericMetaData(0, 1, 0, 1, 0, 1, 2, "stuff");
        IValue b = new DoubleValue(stamp, severity, status, meta_b,
                IValue.Quality.Original, new double [] { 1.0, 2.0 });
        assertEquals(a, b);

        a = new StringValue(stamp, severity, status,
                IValue.Quality.Original, new String [] { "Fred" });

        assertTrue(!a.equals(b));
        b = new StringValue(stamp, severity, status,
                IValue.Quality.Original, new String [] { "Fred" });
        assertEquals(a, b);
    }
}
