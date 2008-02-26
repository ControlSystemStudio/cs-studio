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

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

public class DoubleValueTest
{
    @Test
    public void specialComparisons()
    {
        // This one is obvious
        assertEquals(3.14, 3.14, 0.001);
        
        // These are also the same
        assertTrue("+inf comparison",
                Double.POSITIVE_INFINITY == Double.POSITIVE_INFINITY);
        assertTrue("-inf comparison",
                Double.NEGATIVE_INFINITY == Double.NEGATIVE_INFINITY);

        // Beware of this one!!
        assertTrue("NaN differs from anything", Double.NaN != Double.NaN);
    }

    @Test
    public void arrayCompare()
    {
        final double a[] = 
            new double[] { 3.14, Double.NaN, Double.POSITIVE_INFINITY, 10.0 };
        final double b[] =
            new double[] { 3.14, Double.NaN, Double.POSITIVE_INFINITY, 10.0 };
        
        // Compare individual values the same way as DoubleValue.equals()
        for (int i=0; i<a.length; ++i)
        {   
            if (Double.isNaN(a[i])   &&   Double.isNaN(b[i]))
                continue; // OK, we use NaN == NaN
            if (a[i] != b[i])
                assertTrue("Error", false);
        }
        
        // Full DoubleValue comparison
        final ITimestamp time = TimestampFactory.now();
        final INumericMetaData meta =
            new NumericMetaData(0.0, 10.0, 2.0, 8.0, 1.0, 9.0, 2, "a.u.");
        final ISeverity sevr = SeverityInstances.ok;
        final DoubleValue va =
            new DoubleValue(time, sevr, "OK", meta, IValue.Quality.Original, a);
        final DoubleValue vb =
            new DoubleValue(time, sevr, "OK", meta, IValue.Quality.Original, b);
        
        assertTrue(va.equals(vb));
    }
}
