/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

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
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public String[] getLogbooks() throws Exception
    {
        final ArrayList<String> names = new ArrayList<String>();
        final RDBUtil rdb = RDBUtil.connect(Preferences.getURL(),
                Preferences.getLogListUser(), Preferences.getLogListPassword(),
                false);
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet result = statement.executeQuery(
                    "SELECT oper_grp_nm FROM oper.oper_grp " +
	                "WHERE elog_ind='Y' ORDER BY oper_grp_nm");
            while (result.next())
                names.add(result.getString(1));
        }
        finally
        {
            statement.close();
            rdb.close();
        }
        // Convert into plain array
        final String ret_val[] = new String[names.size()];
        return names.toArray(ret_val);
    }

    /** {@inheritDoc} */
    @Override
    public String getDefaultLogbook()
    {
        return Preferences.getDefaultLogbook();
    }

    /** {@inheritDoc} */
    @Override
    public ILogbook connect(final String logbook, final String user, final String password)
            throws Exception
    {
        return connect(Preferences.getURL(), logbook, user, password);
    }

    /** Connect to logbook.
     *  <p>
     *  This call is <u>only for unit tests</u>.
     *  Application code should use the
     *   <code>org.csstudio.logbook.sns.LogbookFactory</code>
     *  and not directly call the <code>SNSLogbookFactory</code>.
     *
     *  @param url RDB URL
     *  @param logbook Logbook name
     *  @param user   RDB user
     *  @param password RDB password
     *  @return ILogbook
     *  @throws Exception
     */
    @SuppressWarnings("nls")
    public ILogbook connect(final String url, final String logbook,
            final String user, final String password) throws Exception
    {
        // Connect to the SNS RDB
        if (user.length() <= 0)
            throw new Exception("Empty user name");
        if (password.length() <= 0)
            throw new Exception("Empty password");
        if (url == null)
            throw new Exception("Missing logbook URL");
        if (logbook == null)
            throw new Exception("Missing logbook name");
        final RDBUtil rdb = RDBUtil.connect(url, user, password, false);
        return new SNSLogbook(rdb, user, logbook);
    }
}
