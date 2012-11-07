package org.csstudio.diag.pvfields.sns;

import org.csstudio.diag.pvfields.DataProvider;
import org.csstudio.diag.pvfields.PVInfo;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/** JUnit test of the {@link SNSDataProvider}
 * 
 *  @author Kay Kasemir
 */
public class SNSDataProviderUnitTest
{
    @Test
    public void testSNSDataProvider() throws Exception
    {
    	final DataProvider provider = new SNSDataProvider();
    	final PVInfo info = provider.lookup(TestSetup.CHANNEL_NAME);
    	System.out.println(info);
    	assertThat(info, not(nullValue()));
    	assertThat(info.getProperties().size(), not(equalTo(0)));
    }
}
