package org.csstudio.sns.jms2rdb.perftest;

import java.net.InetAddress;
import java.util.Calendar;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.JMSLogMessage;
import org.csstudio.sns.jms2rdb.rdb.RDBWriter;
import org.junit.Test;

/** Simple RDB 'write' performance test.
 *  <p>
 *  Log messages have about 10 properties.
 *  Test with laptop to local MySQL: about 60..70 msg/sec.
 *  Test with laptop to SNS Oracle 'devl': about 10 msg/sec.
 *  <p>
 *  Using 'batched' inserts for the properties:
 *  Local MySQL: about 100 msg/sec.
 *  SNS Oracle 'devl': about 15..20 msg/sec.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBPerfTest
{
    /** JMS Server URL */
    final private static String URL =
    // TODO Don't put the epics_mon PW into CVS!
//        "jdbc:oracle:thin:epics_mon/PASSWORD@//snsdev3.sns.ornl.gov:1521/devl";
      "jdbc:mysql://localhost/log?user=log&password=$log";
    
    final private static String SCHEMA = ""; //"EPICS.";

    /** Test runtime */
    final private static int SECONDS = 30;

    @Test
    public void perfTest() throws Exception
    {
        // Log4j
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);

        final RDBWriter rdb_writer = new RDBWriter(URL, SCHEMA);

        // Run for some time
        System.out.println("Runtime: " + SECONDS + " seconds");

        final String host = InetAddress.getLocalHost().getHostName();
        final String user = System.getProperty("user.name");
        final long end = System.currentTimeMillis() + SECONDS*1000;
        int count = 0;
        while (System.currentTimeMillis() < end)
        {
            ++count;
            final Calendar now = Calendar.getInstance();
            final JMSLogMessage msg = new JMSLogMessage(
                    Integer.toString(count), now, now,
                    "RDBPerfTest", "run", "RDBPerfTest.java", "JMSLogTool",
                    host, user);
            rdb_writer.write(msg);
        }
        rdb_writer.close();

        // Stats
        System.out.format("Wrote %d messages = %.1f msg/sec\n",
                count, ((double) count)/SECONDS);
    }
}
