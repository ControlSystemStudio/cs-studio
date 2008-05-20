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
    public ILogbook connect(final String user, final String password)
            throws Exception
    {
        // Connect to the SNS RDB
        // Get class loader to find the driver
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance(); //$NON-NLS-1$
        final String url = Preferences.getURL();
        final Connection connection =
            DriverManager.getConnection(url, user, password);
        return new SNSLogbook(connection, user, Preferences.getLogBookName());
    }
}
