/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.AlarmTreeRoot;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** A 'global' alarm
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GlobalAlarm
{
    final private String path;
    final private SeverityLevel severity;
    final private String message;
    final private ITimestamp timestamp;

    /** Initialize
     *  @param info AlarmUpdateInfo with details
     */
    public GlobalAlarm(final AlarmUpdateInfo info)
    {
        this(info.getNameOrPath(), info.getSeverity(),
                info.getMessage(), info.getTimestamp());
    }

    /** Initialize
     *  @param path
     *  @param severity
     *  @param message
     *  @param timestamp
     */
    public GlobalAlarm(final String path, final SeverityLevel severity, final String message,
            final ITimestamp timestamp)
    {
        this.path = path;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getPath()
    {
        return path;
    }

    public SeverityLevel getSeverity()
    {
        return severity;
    }

    public String getMessage()
    {
        return message;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "GlobalAlarm: " + timestamp + " '" + path + "' (" + severity.getDisplayName() + "/" + message + ")";
    }

    /** Read RDB information: Description, guidance, ...
     *  @param rdb
     *  @throws Exception
     */
    public void readInformation(final RDBUtil rdb) throws Exception
    {
        final SQL sql = new SQL(rdb);


        final String path_elements[] = AlarmTreePath.splitPath(getPath());

        // Get 'root' element
        final AlarmTreeRoot root;
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_configuration_by_name);
        try
        {
            statement.setString(1, path_elements[0]);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new Exception("Unknown alarm tree root " + path_elements[0]);
            final int id = result.getInt(1);
            root = new AlarmTreeRoot(id, path_elements[0]);
        }
        finally
        {
            statement.close();
        }
        // TODO Get display, guidance, commands
        // TODO Get path_elements[1+]
        System.out.println(root);
    }
}
