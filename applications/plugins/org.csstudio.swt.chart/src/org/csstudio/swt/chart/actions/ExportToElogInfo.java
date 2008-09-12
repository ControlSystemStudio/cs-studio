package org.csstudio.swt.chart.actions;

/** Info needed for making an elog entry.
 *  @author Kay Kasemir
 */
public class ExportToElogInfo
{
    final private String user, password, logbook, title, body;

    /** Constructor */
    public ExportToElogInfo(final String user, final String password,
            final String logbook,
            final String title, final String body)
    {
        this.user = user.trim();
        this.password = password.trim();
        this.logbook = logbook != null ? logbook.trim() : null;
        this.title = title.trim();
        this.body = body.trim();
    }

    /** @return user name */
    public String getUser()
    {
        return user;
    }

    /** @return password */
    public String getPassword()
    {
        return password;
    }
    
    /** @return logbook */
    public String getLogbook()
    {
        return logbook;
    }

    /** @return Title of entry */
    public String getTitle()
    {
        return title;
    }

    /** @return Text (body) of entry */
    public String getBody()
    {
        return body;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format("User/pw: '%s'/'%s'\n" +
        		             "Title  : '%s'\n" +
        		             "Body   :\n'%s'\n",
                user, password, title, body);
    }
    
}
