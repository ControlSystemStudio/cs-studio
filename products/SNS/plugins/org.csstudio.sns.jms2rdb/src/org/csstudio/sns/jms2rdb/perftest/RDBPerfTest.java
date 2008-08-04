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
 *  Log messages have about 10 properties;
 *  3 in message table, 7 as message content.
 *  
 *  Using 'batched' inserts for the properties.
 *  
 *  Local or networked MySQL: about 150 msg/sec.
 *  SNS Oracle 'devl': about 50 msg/sec.
 *
 *  For a similar 'read' test, see org.csstudio.sns.msghist 
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBPerfTest
{
    /** JMS Server URL */
    // TODO Don't put the epics_mon PW into CVS!
//    final private static String URL =
//        "jdbc:oracle:thin:epics_mon/PASSWORD@//snsdb1.sns.ornl.gov:1521/prod";
//    final private static String SCHEMA = "EPICS";

  final private static String URL =
    "jdbc:mysql://titan-terrier.sns.ornl.gov/log?user=log&password=$log";
    final private static String SCHEMA = ""; 
    
    /** Test runtime */
    final private static int SECONDS = 60;

    @Test
    public void perfTest() throws Exception
    {
        // Log4j
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);

        final RDBWriter rdb_writer = new RDBWriter(URL, SCHEMA);

        // Run for some time
        System.out.println("URL    : " + URL);
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
                    Integer.toString(count),
                    Level.INFO.toString(), now, now,
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
