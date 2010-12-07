/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.TimeWarp;

/** Helper for reading alarm configuration from RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmConfigurationReader
{
    /** RDB Connection */
    final private RDBUtil rdb;

    /** SQL strings */
    final SQL sql;

    /** Re-used statements */
    private PreparedStatement sel_item_by_parent_and_name_statement, sel_guidance_statement, sel_displays_statement, sel_commands_statement;

    /** Initialize
     *  @param rdb RDB connection
     */
    public AlarmConfigurationReader(final RDBUtil rdb, final SQL sql)
    {
        this.rdb = rdb;
        this.sql = sql;
    }

    /** Should be called to close prepared statements etc.
     *  May be called multiple times.
     *  Will <u>not</u> close the RDBUtil.
     */
    public void closeStatements()
    {
        try
        {
            if (sel_item_by_parent_and_name_statement != null)
            {
                sel_item_by_parent_and_name_statement.close();
                sel_item_by_parent_and_name_statement = null;
            }
            if (sel_guidance_statement != null)
            {
                sel_guidance_statement.close();
                sel_guidance_statement = null;
            }
            if (sel_displays_statement != null)
            {
                sel_displays_statement.close();
                sel_displays_statement = null;
            }
            if (sel_commands_statement != null)
            {
                sel_commands_statement.close();
                sel_commands_statement = null;
            }
        }
        catch (SQLException e)
        {
            // Could also ignore: We're closing anyway
            e.printStackTrace();
        }
    }

    /** Read the 'root' element
     *  @param name Name of root (i.e. configuration name)
     *  @return {@link AlarmTreeRoot}
     *  @throws Exception on error
     */
    public AlarmTreeRoot readRoot(final String name) throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_configuration_by_name);
        try
        {
            statement.setString(1, name);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new Exception("Unknown alarm tree root " + name);
            final int id = result.getInt(1);
            return new AlarmTreeRoot(id, name);
        }
        finally
        {
            statement.close();
        }
    }

    /**Get guidance from RDB by id
     * @param id The id of the item in alarmtree
     * @return the guidance messages
     * @throws Exception on error
     */
    private List<GDCDataStructure> readGuidance(final int id) throws Exception
    {
        final List<GDCDataStructure> gdcList = new ArrayList<GDCDataStructure>();
        if (sel_guidance_statement == null)
            sel_guidance_statement = rdb.getConnection().prepareStatement(sql.sel_guidance_by_id);
        sel_guidance_statement.setInt(1, id);
        final ResultSet result = sel_guidance_statement.executeQuery();
        while (result.next())
        {
            final String title = result.getString(1);
            final String details = result.getString(2);
            gdcList.add(new GDCDataStructure(title, details));
        }
        return gdcList;
    }

    /**Get displays from RDB by id
     * @param id The id of the item in alarmtree
     * @return the display links
     * @throws Exception on error
     */
    private List<GDCDataStructure> readDisplays(final int id) throws Exception
    {
        final List<GDCDataStructure> gdcList = new ArrayList<GDCDataStructure>();
        if (sel_displays_statement == null)
            sel_displays_statement = rdb.getConnection().prepareStatement(sql.sel_displays_by_id);
        sel_displays_statement.setInt(1, id);
        final ResultSet result = sel_displays_statement.executeQuery();
        while (result.next())
        {
            final String title = result.getString(1);
            final String details = result.getString(2);
            gdcList.add(new GDCDataStructure(title, details));
        }
        return gdcList;
    }

    /**Get commands from RDB by id
     * @param id The id of the item in alarmtree
     * @return the display links
     * @throws Exception on error
     */
    private List<GDCDataStructure> readCommands(final int id) throws Exception
    {
        final List<GDCDataStructure> gdcList = new ArrayList<GDCDataStructure>();
        if (sel_commands_statement == null)
            sel_commands_statement = rdb.getConnection().prepareStatement(sql.sel_commands_by_id);
        sel_commands_statement.setInt(1, id);
        final ResultSet result = sel_commands_statement.executeQuery();
        while (result.next())
        {
            final String title = result.getString(1);
            final String details = result.getString(2);
            gdcList.add(new GDCDataStructure(title, details));
        }
        return gdcList;
    }

    /** Read and set the GUI info (guidance, displays, commands)
     *  @param item Item to update with GUI info
     *  @throws Exception on error
     */
    public void readGUIInfo(AlarmTree item) throws Exception
    {
        final int id = item.getID();
        item.setGuidance(readGuidance(id));
        item.setDisplays(readDisplays(id));
        item.setCommands(readCommands(id));
    }

    /** Read alarm tree component or PV.
     *  Does <u>not</u> initialize the GUI info nor alarm state
     *  @param name Name of item
     *  @param parent parent item
     * @param severity_mapping
     * @param message_mapping
     *  @return {@link AlarmTreeComponent} or {@link AlarmTreePV}
     *  @throws Exception on error
     */
    public AlarmTree readItem(final String name, final AlarmTree parent,
            final SeverityReader severity_mapping, final MessageReader message_mapping) throws Exception
    {
        if (sel_item_by_parent_and_name_statement == null)
            sel_item_by_parent_and_name_statement =
                rdb.getConnection().prepareStatement(sql.sel_item_by_parent_and_name);
        sel_item_by_parent_and_name_statement.setInt(1, parent.getID());
        sel_item_by_parent_and_name_statement.setString(2, name);
        final ResultSet result = sel_item_by_parent_and_name_statement.executeQuery();
        final AlarmTree item;
        try
        {
            if (!result.next())
                throw new Exception("Unknown alarm tree item " +
                        AlarmTreePath.makePath(parent.getPathName(), name));
            final int id = result.getInt(1);
            final Timestamp config_time = result.getTimestamp(2);
            // Check PV's ID. If null, this is a component, not PV
            result.getInt(3);
            if (result.wasNull())
                item = new AlarmTreeComponent(id, name, parent);
            else
            {
                final AlarmTreePV pv = new AlarmTreePV(id, name, parent);
                configurePVfromResult(pv, result, severity_mapping,
                        message_mapping);
                item = pv;
            }
            if (config_time != null)
                item.setConfigTime(TimeWarp.getCSSTimestamp(config_time));
        }
        finally
        {
            result.close();
        }
        return item;
    }

    /** Configure a PV from RDB columns
     *  @param pv PV to configure
     *  @param result ResultSet with PV info
     *  @param severity_mapping
     *  @param message_mapping
     *  @throws Exception on error
     */
    public void configurePVfromResult(final AlarmTreePV pv,
            final ResultSet result, final SeverityReader severity_mapping,
            final MessageReader message_mapping) throws Exception
    {
        pv.setDescription(result.getString(4));
        pv.setEnabled(result.getBoolean(5));
        pv.setAnnunciating(result.getBoolean(6));
        pv.setLatching(result.getBoolean(7));
        pv.setDelay(result.getInt(8));
        pv.setCount(result.getInt(9));
        pv.setFilter(result.getString(10));

        // If there is severity/status info, use it.
        // Otherwise leave PV "OK" as it was initialized.
        int severity_id = result.getInt(11);
        final SeverityLevel current_severity = result.wasNull()
            ? SeverityLevel.OK
            : severity_mapping.getSeverity(severity_id);

        // Current message was added later, so assume "" if not set
        int status_id = result.getInt(12);
        final String current_message = result.wasNull()
            ? ""
            : message_mapping.getMessage(status_id);

        severity_id = result.getInt(13);
        final SeverityLevel severity = result.wasNull()
            ? SeverityLevel.OK
            : severity_mapping.getSeverity(severity_id);

        status_id = result.getInt(14);
        final String message =  result.wasNull()
            ? ""
            : message_mapping.getMessage(status_id);

        final String value = result.getString(15); // OK to have null value
        final Timestamp sql_time = result.getTimestamp(16);
        if (!result.wasNull())
        {
            final ITimestamp timestamp = TimeWarp.getCSSTimestamp(sql_time);
            pv.setAlarmState(current_severity, current_message, severity, message, value, timestamp);
        }
    }
}
