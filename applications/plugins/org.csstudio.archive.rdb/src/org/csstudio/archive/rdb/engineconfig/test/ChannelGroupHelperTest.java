package org.csstudio.archive.rdb.engineconfig.test;

import static org.junit.Assert.assertEquals;

import java.sql.Statement;

import org.csstudio.archive.rdb.TestSetup;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;
import org.junit.Test;

/** Retention tests
 *  <p>
 *  Assumes that there is already a manually created entry 9999/Forever
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChannelGroupHelperTest
{
    @Test
    public void test() throws Exception
    {
        final RDBArchiveImpl archive = new RDBArchiveImpl(TestSetup.URL);
        
        final ChannelGroupHelper groups = new ChannelGroupHelper(archive);
        ChannelGroupConfig group = groups.find("Test", 1);
        System.out.println(group);
        
        group = groups.add("Another Test", 2, 0, 9999);
        System.out.println(group);
        
        final Statement statement = archive.getRDB().getConnection().createStatement();
        final int rows = statement.executeUpdate(
                "DELETE FROM chan_grp WHERE grp_id=" + group.getId());
        assertEquals(1, rows);
        statement.close();
        
        archive.close();
    }
}
