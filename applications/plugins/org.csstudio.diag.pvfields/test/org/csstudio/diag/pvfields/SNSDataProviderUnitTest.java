package org.csstudio.diag.pvfields;

import org.csstudio.diag.pvfields.model.SNSDataProvider;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the {@link SNSDataProvider}
 * 
 *  @author Kay Kasemir
 */
public class SNSDataProviderUnitTest
{
    @Before
    public void setup() throws Exception
    {
        TestSetup.setup();
    }

    @Test
    public void testSNSDataProvider() throws Exception
    {
    	final DataProvider provider = new SNSDataProvider();
    	final PVInfo info = provider.lookup(TestSetup.CHANNEL_NAME);
    	System.out.println(info);
    }
}
