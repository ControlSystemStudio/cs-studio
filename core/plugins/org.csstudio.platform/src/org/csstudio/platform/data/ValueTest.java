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
 package org.csstudio.platform.data;

import junit.framework.TestCase;

import org.csstudio.platform.data.IValue.Quality;
import org.junit.Test;

/** Some very basic tests of the Sample implementation.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ValueTest extends TestCase
{
    @Test
	public void testEquality() throws Exception
	{
        ISeverity ok = ValueFactory.createOKSeverity();
        ISeverity bad = ValueFactory.createInvalidSeverity();
		ITimestamp now = TimestampFactory.now();

        INumericMetaData meta =
            ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 3, "socks");
        System.out.println(meta.toString());
        
        double values[];
		values = new double[1];
		values[0] = 3.14;
		final Quality quality = IValue.Quality.Original;
        IValue a = ValueFactory.createDoubleValue(now, ok, "OK", meta,
                                                  quality, values);

        values = new double[1];
		values[0] = 3.14;
        IValue b = ValueFactory.createDoubleValue(now, ok, "OK", meta,
                                                  quality, values);
        
        values = new double[1];
		values[0] = 42.0;
        IValue c = ValueFactory.createDoubleValue(now, bad, "Error", meta,
                                                  quality, values);
        
		assertFalse(a == b);
		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertFalse(a.equals(c));
		assertFalse(b.equals(c));
        
        assertEquals("3.140", a.format());
        assertEquals("42.000", c.format());

        assertEquals("3.1400", a.format(IValue.Format.Decimal, 4));
        assertEquals("3", a.format(IValue.Format.Decimal, 0));
        assertEquals("3.14E0", a.format(IValue.Format.Exponential, 2));
        assertEquals("3.140E0", a.format(IValue.Format.Exponential, 3));
        
        IEnumeratedMetaData enum_meta = ValueFactory.createEnumeratedMetaData(
            new String[] { "One", "Two" } );
        System.out.println(enum_meta.toString());
        IValue en = ValueFactory.createEnumeratedValue(now, ok, "OK", enum_meta,
                                                 quality, new int[] { 1 });
        assertEquals("Two (1)", en.format());
    }
}
