package org.csstudio.logbook.sns;

import java.sql.Connection;
import java.sql.DriverManager;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;

/** Implementation of the ILogbookFactory.
 *  Plugin.xml declares this class as the logbook factory extension point.
 *  @author nypaver
 *  @author Kay Kasemir
 */
public class SNSLogbookFactory implements ILogbookFactory
{
    /** ILogbookFactory interface */
    public ILogbook connect(final String user, final String password)
            throws Exception
    {
        return connect(Preferences.getURL(), Preferences.getLogBookName(),
                user, password);
    }

    /** Connect to logbook.
     *  <p>
     *  This call is <u>only for unit tests</u>.
     *  Application code should use the
     *   <code>org.csstudio.logbook.sns.LogbookFactory</code>
     *  and not directly call the <code>SNSLogbookFactory</code>.
     *  
     *  @param url
     *  @param logbook
     *  @param user
     *  @param password
     *  @return
     *  @throws Exception
     */
    public ILogbook connect(final String url, final String logbook,
            final String user, final String password) throws Exception
    {
        // Connect to the SNS RDB
        // Get class loader to find the driver
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance(); //$NON-NLS-1$
        final Connection connection =
            DriverManager.getConnection(url, user, password);
        return new SNSLogbook(connection, user, logbook);
    }
}
