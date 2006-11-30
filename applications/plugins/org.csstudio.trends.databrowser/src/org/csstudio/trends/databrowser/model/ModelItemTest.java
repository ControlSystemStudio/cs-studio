package org.csstudio.trends.databrowser.model;

import junit.framework.TestCase;

/** Test for ChartItem
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelItemTest extends TestCase
{
    public void testChartItem() throws Exception
    {
        ModelItem item = new ModelItem(null, "fred",
                        1024, 0, 0, 0, 0, 0, 0, 0, false);
        item.start();
        Thread.sleep(2000);
        item.stop();
    }
}
