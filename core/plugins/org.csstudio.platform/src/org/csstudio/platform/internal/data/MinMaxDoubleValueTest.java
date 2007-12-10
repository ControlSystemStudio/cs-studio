package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/** Simple test of value formatting.
 *  @author Kay Kasemir
 */
public class MinMaxDoubleValueTest
{
    @SuppressWarnings("nls")
    @Test
    public void testMinMaxDouble()
    {
        INumericMetaData meta = ValueFactory.createNumericMetaData(
                        0.0, 10.0, 2.0, 8.0, 1.0, 9.0, 2, "socks");
        IMinMaxDoubleValue value = ValueFactory.createMinMaxDoubleValue(
                        TimestampFactory.now(),
                        SeverityInstances.minor,
                        "OK",
                        meta,
                        IValue.Quality.Interpolated,
                        new double[] { 3.14 },  3.1, 3.2);
        final String txt = value.toString();
        System.out.println(txt);
        // Compare all but the time stamp
        assertEquals("\t3.14 [ 3.1 ... 3.2 ]\tMINOR, OK", txt.substring(29));
    }
}
