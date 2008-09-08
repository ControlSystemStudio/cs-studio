package org.csstudio.logbook.sns;

import org.csstudio.logbook.ILogbook;
import org.csstudio.logbook.ILogbookFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Implementation of the ILogbookFactory.
 *  Plugin.xml declares this class as the logbook factory extension point.
 *  @author Delphy Nypaver Armstrong
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
    @SuppressWarnings("nls")
    public ILogbook connect(final String url, final String logbook,
            final String user, final String password) throws Exception
    {
        // Connect to the SNS RDB
        if (url == null)
            throw new Exception("Missing logbook URL");
        if (logbook == null)
            throw new Exception("Missing logbook name");
        final RDBUtil rdb = RDBUtil.connect(url, user, password);
        return new SNSLogbook(rdb, user, logbook);
    }
}
