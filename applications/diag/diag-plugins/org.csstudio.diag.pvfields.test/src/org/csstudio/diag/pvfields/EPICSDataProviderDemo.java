package org.csstudio.diag.pvfields;

import org.csstudio.diag.pvfields.model.EPICSDataProvider;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the {@link EPICSDataProvider}
 *
 *  @author Kay Kasemir
 */
public class EPICSDataProviderDemo
{
    @Before
    public void setup() throws Exception
    {
        SetupHelper.setup();
    }

    @Test
    public void testEPICSDataProvider() throws Exception
    {
        final DataProvider provider = new EPICSDataProvider();
        final PVInfo info = provider.lookup(SetupHelper.CHANNEL_NAME);
        System.out.println(info);
    }
}
