package org.csstudio.alarm.beast.msghist.rdb;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/** JUnit test of LogRDB, gives basic 'read' performance.
 *  <p>
 *  Networked MySQL: >500 msg/sec
 *  SNS Oracle 'prod': ~45 msg/sec.
 *  <p>
 *  With MySQL, at one time it was faster after 
 *    CREATE INDEX message_content_message_ids ON message_content (message_id);
 *  <p>
 *  With single filters for properties in the MESSAGE table,
 *  read performance is roughly the same.
 *  For properties in MESSAGE_COLUMN, it can be slow, bad or downright
 *  terrible.
 *  <p>
 *  Oracle performance degrades when more msgs are in the RDB
 *  (this was for ~2000). An index on DATUM might help.
 *  <p>
 *  For similar 'write' test, see org.csstudio.sns.jms2rdb
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MessageRDBTest
{
	// Configure RDB connection info
//    private static final String URL = "jdbc:mysql://titan-terrier.sns.ornl.gov/log";
//    private static final String USER = "log";
//    private static final String PASSWORD = "$log";
//    private static final String SCHEMA = "";
    
    private static final String URL = "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
    private static final String USER = "sns_reports";
    private static final String PASSWORD = "sns";
    private static final String SCHEMA = "EPICS";

    /** Days to read in this test */
    private static final int DAYS_TO_READ = 1;
    
    /** Basic read with filter */
    @Test
    public void testLogRDB() throws Exception
    {
        final MessageRDB log_rdb = new MessageRDB(URL, USER, PASSWORD, SCHEMA);
        
        final Calendar end = Calendar.getInstance();
        final Calendar start = (Calendar) end.clone();
        start.add(Calendar.DATE, -DAYS_TO_READ);
        
        final MessagePropertyFilter filters[] = new MessagePropertyFilter[]
        {
              new MessagePropertyFilter("TYPE", "log"),
//              new MessagePropertyFilter("TYPE", "alarm"),
//              new MessagePropertyFilter("SEVERITY", "%"),
//              new MessagePropertyFilter("TEXT", "%19%"),
        };
        
        final BenchmarkTimer timer = new BenchmarkTimer();
        final Message messages[] = log_rdb.getMessages(
              new NullProgressMonitor(), start, end, filters, 50000);
        timer.stop();

        for (Message message : messages)
        {
            System.out.println(message);
            System.out.println("----------");
        }

        System.out.format("Read %d messages; %.1f msg/second\n",
                messages.length, messages.length / timer.getSeconds());
        assertTrue("Got some messages", messages.length > 0);

    }
}
