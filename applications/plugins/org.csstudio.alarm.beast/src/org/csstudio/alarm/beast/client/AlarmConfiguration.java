/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.alarm.beast.Activator;
import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SQL;
import org.csstudio.apputil.time.DelayCheck;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

/** Alarm Configuration as stored in RDB. It is identified
 *  by rdb_url plus root_component, which means all operations
 *  with this object is related to its root_component.
 *
 *  <p>This class can read and write the configuration which is stored in RDB.
 *
 *  <p>The AlarmClientModel combines this with JMS communication,
 *  inform listeners etc.
 *
 *  <p>See NOTE ON SYNCHRONIZATION in AlarmClientModel
 *
 *  @author Kay Kasemir, Xihui Chen
 *  @author Lana Abadie - Disable autocommit as needed.
 */
public class AlarmConfiguration
{
    /** Connection to configuration/state snapshot. */
    final private RDBUtil rdb;

    /** Use auto-reconnect? */
    final private boolean auto_reconnect;

    /** SQL strings */
    final private SQL sql;

    /** Mapping of severity IDs to strings */
    final private SeverityReader severity_mapping;

    /** Mapping of message IDs to strings */
    final private MessageReader message_mapping;

    final private AlarmConfigurationReader config_reader;

    /** Root of the alarm tree.
     *  SYNC on this for access
     */
    private AlarmTreeRoot config_tree = null;

    /** Hash of all PVs in config_tree that maps PV name to PV */
    private HashMap<String, AlarmTreePV> pvs = new HashMap<String, AlarmTreePV>();

    /** Re-used statements */
    private PreparedStatement sel_items_by_parent_statement, sel_pv_by_id_statement;


    /** Initialize
     *  @param url RDB URL
     *  @param user    RDB user name
     *  @param password RDB password
     *  @param schema
     *  @param auto_reconnect
     *  @throws Exception on error
     */
    public AlarmConfiguration(final String url, final String user,
            final String password, final String schema, final boolean auto_reconnect) throws Exception
    {
        // Allow auto-reconnect?
        this.auto_reconnect = auto_reconnect;
        try
        {
        	rdb = RDBUtil.connect(url, user, password, auto_reconnect);
        }
        catch (Exception ex)
        {
        	throw new Exception(NLS.bind(Messages.DatabaseConnectionErrorFmt, ex.getMessage()), ex);
        }

        // Disable it while reading initial config. because that
        // can be 10% faster
        if (auto_reconnect)
            rdb.setAutoReconnect(false);
        sql = new SQL(rdb, schema);
        severity_mapping = new SeverityReader(rdb, sql);
        message_mapping = new MessageReader(rdb, sql);
        config_reader = new AlarmConfigurationReader(rdb, sql);

        // Re-enable auto-connect if that was requested
        if (auto_reconnect)
            rdb.setAutoReconnect(true);
    }

    /** Initialize
     *  @param url RDB URL
     *  @param user    RDB user name
     *  @param password RDB password
     *  @param schema
     *  @throws Exception on error
     */
    public AlarmConfiguration(final String url, final String user,
            final String password, final String schema) throws Exception
    {
        this(url, user, password, schema, true);
    }


    /** List all configuration 'root' element names
     *  @return Array of 'root' elements
     *  @throws Exception on error
     */
    public String[] listConfigurations() throws Exception
    {
        final Connection connection = rdb.getConnection();
        final Statement statement = connection.createStatement();
        final List<String> names = new ArrayList<String>();
        try
        {
            final ResultSet result = statement.executeQuery(sql.sel_configurations);
            while (result.next())
                names.add(result.getString(1));
        }
        finally
        {

            statement.close();
        }
        // Convert to plain array
        return names.toArray(new String[names.size()]);
    }


    /** Read configuration.
     *  @param root_name Name of root element.
     *  @param create Set <code>true</code> to create new tree if nothing found
     *  @param monitor Progress monitor
     */
    public void readConfiguration(final String root_name, final boolean create,
            final IProgressMonitor monitor) throws Exception
    {
        if (auto_reconnect)
            rdb.setAutoReconnect(false);
        final AlarmTreeRoot new_config;
        final DelayCheck monitor_update_delay = new DelayCheck(1, TimeUnit.SECONDS);
        try
        {
            new_config = readAlarmTree(root_name, create, monitor, monitor_update_delay);
            closeStatements();
        }
        finally
        {
            if (auto_reconnect)
                rdb.setAutoReconnect(true);
        }
        synchronized (this)
        {
            config_tree = new_config;
        }
    }

    /** Must be called to release resources */
    public void close()
    {
        closeStatements();
        rdb.close();
        pvs.clear();
    }

    /** @return root of the alarm tree configuration */
    synchronized public AlarmTreeRoot getAlarmTree()
    {
        return config_tree;
    }

    /** Locate PV by name
     *  @param name Name of PV to locate. May be <code>null</code>.
     *  @return PV or <code>null</code> when not found
     */
    public synchronized AlarmTreePV findPV(final String name)
    {
        if (name == null)
            return null;
        return pvs.get(name);
    }

    /** Create the root element.
     *  <p>
     *  Per default, it's a plain AlarmTreeRoot.
     *  ClientModel will use an AlarmClientModelRoot
     *  @param id RDB ID of root element
     *  @param root_name Name of the root element
     *  @return
     */
    protected AlarmTreeRoot createAlarmTreeRoot(final int id, final String root_name)
    {
        return new AlarmTreeRoot(root_name, id);
    }

    /** Get alarm tree configuration
     *  @param root_name Name of root component
     *  @param create Create empty alarm tree?
     *  @param monitor Progress monitor
     *  @param monitor_update_delay Delay for updates to monitor
     *  @return AlarmTreeRoot
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    private AlarmTreeRoot readAlarmTree(final String root_name,
            final boolean create,
            final IProgressMonitor monitor, final DelayCheck monitor_update_delay) throws Exception
    {
        final Connection connection = rdb.getConnection();
        final PreparedStatement statement =
            connection.prepareStatement(sql.sel_configuration_by_name);
        try
        {
            statement.setString(1, root_name);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
            {
                if (!create)
                    throw new Exception("Unknown alarm tree root " + root_name);
                // Create new, empty alarm tree
                return (AlarmTreeRoot) addRootOrComponent(null, root_name);
            }
            final int id = result.getInt(1);
            final AlarmTreeRoot root = createAlarmTreeRoot(id, root_name);
            config_reader.readGuidanceDisplaysCommands(root);
            readChildren(root, monitor, monitor_update_delay);
            return root;
        }
        finally
        {
            statement.close();
            closeStatements();
        }
    }

    /** Read configuration for child elements
     *  @param parent Parent node. Children get added to it.
     *  @param monitor Progress monitor
     *  @param monitor_update_delay Delay for updates to monitor
     *  @throws Exception on error
     */
    private void readChildren(final AlarmTreeItem parent,
            final IProgressMonitor monitor, final DelayCheck monitor_update_delay) throws Exception
    {
        if (sel_items_by_parent_statement == null)
            sel_items_by_parent_statement =
                rdb.getConnection().prepareStatement(sql.sel_items_by_parent);
        sel_items_by_parent_statement.setInt(1, parent.getID());
        final ResultSet result = sel_items_by_parent_statement.executeQuery();

        final List<AlarmTreeItem> recurse_items = new ArrayList<AlarmTreeItem>();
        while (result.next()  &&  !monitor.isCanceled())
        {
            final int id = result.getInt(1);
            final String name = result.getString(17);
            final Timestamp config_time = result.getTimestamp(2);
            final AlarmTreeItem item;
            // Check PV's ID. If null, this is a component, not PV
            result.getInt(3);
            if (result.wasNull())
            {   // Component (area, system), not a PV
                item = new AlarmTreeItem(parent, name, id);
                recurse_items.add(item);
            }
            else
            {
                final AlarmTreePV pv = new AlarmTreePV(parent, name, id);
                pvs.put(name, pv);
                // Periodically update progress monitor
                if (monitor_update_delay.expired())
                {
                    final int count = pvs.size();
                    monitor.subTask(NLS.bind(Messages.ReadConfigProgressFmt, count));
                }
                config_reader.configurePVfromResult(pv, result, severity_mapping, message_mapping);
                item = pv;
            }
            if (config_time != null)
                item.setConfigTime(TimestampFactory.fromSQLTimestamp(config_time));
            config_reader.readGuidanceDisplaysCommands(item);
        }
        result.close();

        // Recurse to children
        // Cannot do that inside the above while() because that would reuse
        // the statement of the current ResultSet
        for (AlarmTreeItem item : recurse_items)
            readChildren(item, monitor, monitor_update_delay);
        recurse_items.clear();
    }

    /** Add a component to the model and RDB
     *  @param parent AlarmTreeRoot or ..Component under which to add the component
     *  @param name Name of the new component
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public AlarmTreeItem addComponent(final AlarmTreeItem parent,
            final String name) throws Exception
    {
        if (parent instanceof AlarmTreePV)
           throw new Exception("Cannot add subtree to PV " + parent.getPathName());
        return (AlarmTreeItem) addRootOrComponent(parent, name);
    }

    /** Check if alarm configuration already contains an item
     *  of given parent and name
     *
     *  @param parent Parent item, must not be <code>null</code>
     *  @param name Name of item to be added
     *  @throws Exception if there is already an item with same name
     */
    @SuppressWarnings("nls")
    private void checkDuplicatePath(final AlarmTreeItem parent,
            final String name) throws Exception
    {
        if (parent == null)
            throw new Exception("Parent item is null");

        final String path = AlarmTreePath.makePath(parent.getPathName(), name);
        final AlarmTreeItem existing_item = config_tree.getItemByPath(path);
        if (existing_item != null)
            throw new Exception("Alarm configuration already contains an element with path "  + path);
    }

    /** Create a new tree root, or add (sub)component to existing root
     *  or component.
     * @param parent Parent element in tree. <code>null</code> for root
     * @param name Name of component to add
     * @return AlarmTreeRoot or Component
     * @throws Exception on error
     */
    @SuppressWarnings("nls")
    private AlarmTreeItem addRootOrComponent(
            final AlarmTreeItem parent, final String name) throws Exception
    {
        if (parent instanceof AlarmTreePV)
            throw new Exception("Cannot add sub-element to PV " +
                    parent.getName());

        // For all but root check the path
        if (parent != null)
            checkDuplicatePath(parent, name);

        rdb.getConnection().setAutoCommit(false);
        final int id = getNextItemID();
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.insert_item);
        try
        {
            statement.setInt(1, id);
            // If adding a root, set its parent to NULL
            if (parent == null)
                statement.setNull(2, Types.INTEGER);
            else
                statement.setInt(2, parent.getID());
            statement.setString(3, name);
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
        // If added a root...
        if (parent == null)
            return createAlarmTreeRoot(id, name);
        return new AlarmTreeItem(parent, name, id);
    }

    /** @return Next RDB ID for alarm component
     *  @throws Exception on error
     */
    private int getNextItemID() throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            // TODO Use sequence or auto-inc ID to allow concurrent modifications?
            final ResultSet result = statement.executeQuery(sql.sel_last_item_id);
            if (! result.next())
                throw new Exception("Cannot get next component ID"); //$NON-NLS-1$
            return result.getInt(1) + 1;
        }
        finally
        {
            statement.close();
        }
    }

    /** Add a PV to the model and config storage (RDB)
     *  @param parent Component under which to add the PV
     *  @param name Name of the new PV
     *  @return AlarmTreePV that was added
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public AlarmTreePV addPV(final AlarmTreeItem parent,
                      final String name) throws Exception
    {
        // Check if item with that path already exists
        checkDuplicatePath(parent, name);

        // Check if same PV is already found elsewhere in this tree
        final AlarmTreePV found = findPV(name);
        if (found != null)
            throw new Exception(name + " already under " + found.getPathName());

        rdb.getConnection().setAutoCommit(false);
        final int id = getNextItemID();
        final PreparedStatement insert_item_statement = rdb.getConnection().prepareStatement(sql.insert_item);
        final PreparedStatement insert_pv_statement = rdb.getConnection().prepareStatement(sql.insert_pv);
        final String description = name;
        final boolean latch = true;
        final boolean annunciate = false;
        // Not used, but new PV starts out disabled, which makes sense
        // until it's "configured"
        // final boolean enabled = false;

        try
        {
            //Insert the PV as an item in the alarmtree table
            insert_item_statement.setInt(1, id);
            insert_item_statement.setInt(2, parent.getID());
            insert_item_statement.setString(3, name);
            insert_item_statement.executeUpdate();
            //Insert the PV into the pv table
            insert_pv_statement.setInt(1, id);
            insert_pv_statement.setString(2, description);
            insert_pv_statement.setBoolean(3, annunciate);
            insert_pv_statement.setBoolean(4, latch);
            insert_pv_statement.executeUpdate();

            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            insert_item_statement.close();
            insert_pv_statement.close();
            rdb.getConnection().setAutoCommit(true);
        }

        final AlarmTreePV pv = new AlarmTreePV(parent, name, id);
        pvs.put(name, pv);
        return pv;
    }


    /** Change an items configuration in RDB, includes guidance, displays, commands and config_time.
     *  @param item Item to configure (which already exists, won't be created)
     *  @param guidance Guidance strings
     *  @param displays Related displays
     *  @param commands Commands
     *  @param automated_actions Automated actions
     *  @throws Exception on error
     */
    public void configureItem(final AlarmTreeItem item,
            final GDCDataStructure guidance[], final GDCDataStructure displays[],
            final GDCDataStructure commands[], final AADataStructure automated_actions[]) throws Exception
    {
        // Prepare statements
        final Connection connection = rdb.getConnection();
		final PreparedStatement    delete_guidance_by_id = connection.prepareStatement(sql.delete_guidance_by_id);
        final PreparedStatement    insert_guidance = connection.prepareStatement(sql.insert_guidance);
        final PreparedStatement    delete_displays_by_id = connection.prepareStatement(sql.delete_displays_by_id);
        final PreparedStatement    insert_display = connection.prepareStatement(sql.insert_display);
        final PreparedStatement    delete_commands_by_id = connection.prepareStatement(sql.delete_commands_by_id);
        final PreparedStatement    insert_command = connection.prepareStatement(sql.insert_command);
        final PreparedStatement    delete_auto_actions_by_id = connection.prepareStatement(sql.delete_auto_actions_by_id);
        final PreparedStatement    insert_auto_actions = connection.prepareStatement(sql.insert_auto_action);

        //update guidance, displays and commands
        connection.setAutoCommit(false);
        try
        {
            updateGDC(item.getID(), guidance, delete_guidance_by_id, insert_guidance);
            updateGDC(item.getID(), displays, delete_displays_by_id, insert_display);
            updateGDC(item.getID(), commands, delete_commands_by_id, insert_command);
            updateAA(item.getID(), automated_actions, delete_auto_actions_by_id, insert_auto_actions);
            connection.commit();
        }
        catch (Exception ex)
        {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        finally
        {
            delete_guidance_by_id.close();
            delete_displays_by_id.close();
            delete_commands_by_id.close();
            delete_auto_actions_by_id.close();

            insert_guidance.close();
            insert_display.close();
            insert_command.close();
            insert_auto_actions.close();
        }

        // Update item's config time after RDB commit succeeded
        final PreparedStatement    update_item_config_time = connection.prepareStatement(sql.update_item_config_time);
        try
        {
            final Timestamp config_time = new Timestamp(new Date().getTime());
            update_item_config_time.setTimestamp(1, config_time);
            update_item_config_time.setInt(2, item.getID());
            update_item_config_time.executeUpdate();
            connection.commit();
            item.setConfigTime(TimestampFactory.fromSQLTimestamp(config_time));
        }
        catch (SQLException ex)
        {
            connection.rollback();
            throw ex;
        }
        finally
        {
            update_item_config_time.close();
            connection.setAutoCommit(true);
        }
    }

    /** Change a PV's configuration in RDB.
     *  @param pv PV
     *  @param description Description
     *  @param enabled Are alarms enabled?
     *  @param annunciate Annunciate or not?
     *  @param latch Latch highest alarms?
     *  @param delay Alarm delay [seconds]
     *  @param count Count of severity != OK within delay to detect as alarm
     *  @param filter Filter expression for enablement
     *  @param guidance Guidance strings
     *  @param displays Related displays
     *  @param commands Commands
     *  @param automated_actions Actions
     *  @throws Exception on error
     */
    public void configurePV(final AlarmTreePV pv, final String description,
        final boolean enabled, final boolean annunciate, final boolean latch,
        final int delay, final int count, final String filter,
        final GDCDataStructure guidance[], final GDCDataStructure displays[],
        final GDCDataStructure commands[], final AADataStructure automated_actions[]) throws Exception
    {
        configureItem(pv, guidance, displays, commands, automated_actions);
        rdb.getConnection().setAutoCommit(false);
        final PreparedStatement    update_pv_config_statement = rdb.getConnection().prepareStatement(sql.update_pv_config);
        try
        {
            update_pv_config_statement.setString(1, description);
            update_pv_config_statement.setBoolean(2, enabled);
            update_pv_config_statement.setBoolean(3, annunciate);
            update_pv_config_statement.setBoolean(4, latch);
            update_pv_config_statement.setInt(5, delay);
            update_pv_config_statement.setInt(6, count);
            update_pv_config_statement.setString(7, filter);
            update_pv_config_statement.setInt(8, pv.getID());
            update_pv_config_statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            update_pv_config_statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
    }

    /** Change item's name
     *  @param item Item to change
     *  @param new_name New name for the item
     *  @throws Exception on error
     */
    public void rename(final AlarmTreeItem item, final String new_name) throws Exception
    {
        rdb.getConnection().setAutoCommit(false);
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.rename_item);
        try
        {
            statement.setString(1, new_name);
            statement.setInt(2, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
    }

    /** Change item's location in alarm configuration hierarchy
     *  @param item Item to move
     *  @param new_path New path for the item
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public void move(final AlarmTreeItem item, final String new_path) throws Exception
    {
        // Locate new parent ID
        final AlarmTreeItem parent;
        synchronized (this)
        {
            parent = config_tree.getItemByPath(new_path);
        }
        if (parent == null)
            throw new Exception(NLS.bind("Unknown alarm configuration path {0}",
                                         new_path));
        // Update parent of item (PV or component)
        rdb.getConnection().setAutoCommit(false);
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.move_item);
        try
        {
            statement.setInt(1, parent.getID());
            statement.setInt(2, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
    }

    /** Remove all items from configuration.
     *  (The root element that identifies the configuration remains)
     *  @throws Exception on error
     */
    public synchronized void removeAllItems() throws Exception
    {
        while (config_tree.getChildCount() > 0)
            remove(config_tree.getClientChild(0));
    }

    /** Remove item and all sub-items from alarm tree.
     *  @param item Item to remove
     *  @throws Exception on error
     */
    public synchronized void remove(final AlarmTreeItem item) throws Exception
    {
        if (item instanceof AlarmTreePV)
            removePV((AlarmTreePV) item);
        else
            removeSubtree(item);
    }

    /** Recursively remove subtree.
     *  <p>
     *  Does not send events.
     *  @param item Item to remove with child-items
     *  @throws Exception on error
     */
    private void removeSubtree(final AlarmTreeItem item) throws Exception
    {
        // First recurse down
        while (item.getChildCount() > 0)
        {
            final AlarmTreeItem child = item.getClientChild(0);
            if (child instanceof AlarmTreePV)
                removePV((AlarmTreePV) child);
            else
                removeSubtree(child);
        }
        // Then remove item itself
        rdb.getConnection().setAutoCommit(false);

        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.delete_component_by_id);
        try
        {
            deleteGDCWithoutCommit(item.getID());
            deleteAAWithoutCommit(item.getID());
            statement.setInt(1, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            statement.close();
            rdb.getConnection().setAutoCommit(true);
        }
        item.detachFromParent();
    }

    /** Remove PV from alarm tree.
     *  <p>
     *  Does not send events.
     *  @param pv PV to remove
     *  @throws Exception on error
     */
    private void removePV(final AlarmTreePV pv) throws Exception
    {
        rdb.getConnection().setAutoCommit(false);

        final PreparedStatement delPVStatement =
            rdb.getConnection().prepareStatement(sql.delete_pv_by_id);
        final PreparedStatement delCMPNTStatement =
            rdb.getConnection().prepareStatement(sql.delete_component_by_id);

        try
        {
            deleteGDCWithoutCommit(pv.getID());
            deleteAAWithoutCommit(pv.getID());

            delPVStatement.setInt(1, pv.getID());
            delPVStatement.executeUpdate();

            delCMPNTStatement.setInt(1, pv.getID());
            delCMPNTStatement.executeUpdate();

            rdb.getConnection().commit();
        }
        catch (SQLException ex)
        {
            rdb.getConnection().rollback();
            throw ex;
        }
        finally
        {
            delPVStatement.close();
            delCMPNTStatement.close();
            rdb.getConnection().setAutoCommit(true);
        }
        pvs.remove(pv.getName());
        pv.detachFromParent();
    }

    /** Update PV configuration from RDB
     *  @param path Path name of the added/removed/changed item or null
     */
    @SuppressWarnings("nls")
    public void readPVConfig(final AlarmTreePV pv) throws Exception
    {
        final Connection connection = rdb.getConnection();
        if (sel_pv_by_id_statement == null)
            sel_pv_by_id_statement = connection
                .prepareStatement(sql.sel_pv_by_id);
        sel_pv_by_id_statement.setInt(1, pv.getID());
        final ResultSet result = sel_pv_by_id_statement.executeQuery();
        if (! result.next())
            throw new Exception("PV " + pv.getPathName() + " not found");
        pv.setDescription(result.getString(1));

        pv.setEnabled(result.getBoolean(2));
        pv.setAnnunciating(result.getBoolean(3));
        pv.setLatching(result.getBoolean(4));
        pv.setDelay(result.getInt(5));
        pv.setCount(result.getInt(6));
        pv.setFilter(result.getString(7));
        config_reader.readGuidanceDisplaysCommands(pv);
    }

    /**Update guidance/displays/commands in RDB by id
     * @param id The id of the item in alarmtree.
     * @param gdcList guidance/displays/commands ArrayList.
     * @param deleteGDC The statement for deleting old GDC.
     * @param insertGDC The statement for inserting new GDC.
     * @throws SQLException
     */
    private void updateGDC(final int id, final GDCDataStructure gdcList[],
            final PreparedStatement deleteGDC, final PreparedStatement insertGDC) throws Exception
    {
        try
        {
            deleteGDC.setInt(1, id);
            deleteGDC.executeUpdate();
            int order = 0;
            if (gdcList != null && gdcList.length > 0)
            {
                for(GDCDataStructure gdc : gdcList)
                {
                    insertGDC.setInt(1, id);
                    insertGDC.setInt(2, order);
                    insertGDC.setString(3, gdc.getTitle());
                    insertGDC.setString(4, gdc.getDetails());
                    insertGDC.executeUpdate();
                    order++;
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**Update automated actions in RDB by id
     * @param id The id of the item in alarmtree.
     * @param aaList automated actions ArrayList.
     * @param deleteAA The statement for deleting old AA.
     * @param insertAA The statement for inserting new AA.
     * @throws SQLException
     */
	private void updateAA(final int id, final AADataStructure aaList[],
			final PreparedStatement deleteAA, final PreparedStatement insertAA)
			throws Exception {
		try {
			deleteAA.setInt(1, id);
			deleteAA.executeUpdate();
			int order = 0;
			if (aaList != null && aaList.length > 0)
			{
				for (AADataStructure aa : aaList)
				{
					insertAA.setInt(1, id);
					insertAA.setInt(2, order);
					insertAA.setString(3, aa.getTitle());
					insertAA.setString(4, aa.getDetails());
					insertAA.setInt(5, aa.getDelay());
					insertAA.executeUpdate();
					order++;
				}
			}
		} catch (Exception ex)
		{
			throw ex;
		}
	}

    /** Delete all guidance, displays, commands for an item
     *  @param id Item ID
     *  @throws Exception on error
     */
    private void deleteGDCWithoutCommit(final int id) throws Exception
    {
        final PreparedStatement delete_guidance_by_id = rdb.getConnection().prepareStatement(sql.delete_guidance_by_id);
        final PreparedStatement delete_displays_by_id = rdb.getConnection().prepareStatement(sql.delete_displays_by_id);
        final PreparedStatement delete_commands_by_id = rdb.getConnection().prepareStatement(sql.delete_commands_by_id);
        try
        {
            delete_guidance_by_id.setInt(1, id);
            delete_guidance_by_id.executeUpdate();
            delete_displays_by_id.setInt(1, id);
            delete_displays_by_id.executeUpdate();
            delete_commands_by_id.setInt(1, id);
            delete_commands_by_id.executeUpdate();
        }
        finally
        {
            delete_guidance_by_id.close();
            delete_displays_by_id.close();
            delete_commands_by_id.close();
        }
    }

    /** Delete all automated actions for an item
     *  @param id Item ID
     *  @throws Exception on error
     */
    private void deleteAAWithoutCommit(final int id) throws Exception
    {
        final PreparedStatement delete_auto_actions_by_id = rdb.getConnection().prepareStatement(sql.delete_auto_actions_by_id);
        try
        {
            delete_auto_actions_by_id.setInt(1, id);
            delete_auto_actions_by_id.executeUpdate();
        }
        finally
        {
            delete_auto_actions_by_id.close();
        }
    }

    /** Close prepared statements that are lazily created when reading/writing config.
     *  Should be called when 'done' with a transaction because the config will
     *  automatically re-connect to the RDB, but not re-create all statements
     *  in such a case.
     */
    @SuppressWarnings("nls")
    public void closeStatements()
    {
        try
        {
            if (sel_items_by_parent_statement != null)
            {
                sel_items_by_parent_statement.close();
                sel_items_by_parent_statement = null;
            }
            if (sel_pv_by_id_statement != null)
            {
                sel_pv_by_id_statement.close();
                sel_pv_by_id_statement = null;
            }
        }
        catch (SQLException ex)
        {
            // Could also ignore: We're closing anyway
            Activator.getLogger().log(Level.INFO, "JDBC close failed", ex);
        }
        config_reader.closeStatements();
    }
}
