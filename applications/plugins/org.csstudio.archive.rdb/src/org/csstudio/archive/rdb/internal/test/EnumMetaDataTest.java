package org.csstudio.archive.rdb.internal.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.archive.rdb.internal.EnumMetaDataHelper;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.ValueFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test EnumMetaDataHelper
 *  @author Kay Kasemir
 */
public class EnumMetaDataTest
{
    private static RDBArchive archive;

    @BeforeClass
    public static void connect() throws Exception
    {
        archive = RDBArchive.connect(TestSetup.URL, TestSetup.USER, TestSetup.PASSWORD);
    }
    
    @AfterClass
    public static void disconnect()
    {
        archive.close();
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGet() throws Exception
    {
        final ChannelConfig channel = archive.getChannel(TestSetup.TestType.ENUM.getPvName());
        assertNotNull(channel);
        
        IEnumeratedMetaData meta = EnumMetaDataHelper.get(archive, channel);
        
        meta = ValueFactory.createEnumeratedMetaData(
                new String [] { "One", "Two" });
        EnumMetaDataHelper.set(archive, channel, meta);

        meta = EnumMetaDataHelper.get(archive, channel);
        assertEquals(2, meta.getStates().length);
        assertEquals("One", meta.getState(0));
    }
}
