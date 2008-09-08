package org.csstudio.logbook.sns;

import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.logbook.ILogbook;
import org.csstudio.platform.utility.rdb.RDBUtil;

/**
 * SNS logbook
 * 
 * @author Delphy Nypaver Armstrong
 * @author Kay Kasemir
 */
public class SNSLogbook implements ILogbook
{
    private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
    final private RDBUtil rdb;
    final private String logbook;
    final String badge_number;

    /** Constructor
     *  @param rdb RDB connection
     *  @param user User (for which we'll try to get the badge number)
     *  @param logbook SNS logbook to use
     *  @throws Exception on error
     */
    public SNSLogbook(final RDBUtil rdb, final String user,
            final String logbook) throws Exception
    {
        this.rdb = rdb;
        this.logbook = logbook;
        badge_number = getBadgeNumber(user);
    }

    @SuppressWarnings("nls")
    public void createEntry(String title, String text, String imageName)
            throws Exception
    {
        if (imageName == null)
        {
            final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
                    + "(?, ?, ?, ?, ?)";
            final String category = "";

            // create an sql call for the elog
            final CallableStatement statement = rdb.getConnection().prepareCall(mysql);
            try
            {
                statement.setString(1, badge_number);
                statement.setString(2, logbook);
                statement.setString(3, title);
                statement.setString(4, category);
                statement.setString(5, text);
                statement.executeQuery();
            }
            finally
            {
                statement.close();
            }
        }
        else
        {
            final File imgFile = new File(imageName);
            final int img_size = (int) imgFile.length();
            final FileInputStream img_stream = new FileInputStream(imgFile);
            CallableStatement statement = null;
            try
            {
                final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
                            + "(?, ?, ?, ?, ?, ?, ?, ?)";
                statement = rdb.getConnection().prepareCall(mysql);
                statement.setString(1, badge_number);
                statement.setString(2, logbook);
                statement.setString(3, title);
                statement.setString(4, text);
                statement.setString(5, "I");
                statement.setString(6, imageName);
                statement.setLong(7, 1);
                statement.setBinaryStream(8, img_stream, img_size);
                statement.executeQuery();
            }
            finally
            {
                if (statement != null)
                    statement.close();
                img_stream.close();
            }
        }
    }
    
    /** Get the badge number for the user in the connection dictionary
     *  @return the badge number for the specified user or a default
     */
    @SuppressWarnings("nls")
    private String getBadgeNumber(final String user) throws Exception
    {
        final PreparedStatement statement = rdb.getConnection()
        .prepareStatement("select bn from OPER.EMPLOYEE_V where user_id=?");
        try
        {
            // OPER.EMPLOYEE_V seems to only keep uppercase user_id entries
            statement.setString(1, user.trim().toUpperCase());
            statement.execute();
            final ResultSet result = statement.getResultSet();
            if (result.next())
            {
                final String badge = result.getString("bn");
                if (badge.length() > 1)
                    return badge;
            }
            // No error, but also not found: fall through
        }
        finally
        {
            statement.close();
        }
        return DEFAULT_BADGE_NUMBER;
    }

    public void close()
    {
        rdb.close();
    }
}
