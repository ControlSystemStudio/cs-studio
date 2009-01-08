package org.csstudio.platform.utility.rdb;

import static org.junit.Assert.*;

import org.junit.Test;

/** Test of the StringIDHelper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StringIDHelperTest
{
    @Test
    public void test() throws Exception
    {
        final RDBUtil rdb = RDBUtil.connect(TestSetup.URL, true);
        
        final StringIDHelper helper =
            new StringIDHelper(rdb, "RETENT", "RETENT_ID", "DESCR");
        
        StringID retent = helper.find("forever");
        assertNull(retent);
        
        retent = helper.find(9999);
        assertNull(retent);

        retent = helper.add("1 Month");
        assertEquals("1 Month", retent.getName());
        System.out.println(retent);

        retent = helper.add("2 Months");
        assertEquals("2 Months", retent.getName());
        System.out.println(retent);
        
        helper.dispose();
        
        rdb.close();
    }
}
