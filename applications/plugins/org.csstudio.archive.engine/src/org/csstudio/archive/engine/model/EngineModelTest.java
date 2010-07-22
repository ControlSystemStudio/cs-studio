package org.csstudio.archive.engine.model;

import static org.junit.Assert.*;

import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.rdb.RDBArchive;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the engine model
 *  <p>
 *  RDBArchive configuration (schema) might need info from
 *  Eclipse preferences, hence Plug-in test.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EngineModelTest
{
    final public static String url =
        "jdbc:oracle:thin:@//172.31.75.138:1521/prod";
    private static final String user = "sns_reports";
    private static final String password = "sns";

//    private static final String config = "test_ky9";
//    private static final int port = 4813;

    private static final String config = "llrf";
    private static final int port = 4502;

    
    @Test
    public void testReadConfig() throws Exception
    {
        final RDBArchive rdb = RDBArchive.connect(url, user, password);
        final BenchmarkTimer timer = new BenchmarkTimer();
        final EngineModel model = new EngineModel(rdb);
        model.readConfig(config, port);
        timer.stop();
        final int count = model.getChannelCount();
        System.out.println("Channel count: " + count);
        System.out.println("Runtime      : " + timer);
        System.out.println("Channels/sec : " + count/timer.getSeconds());
        rdb.close();
        assertTrue(count > 0);
    }
}
