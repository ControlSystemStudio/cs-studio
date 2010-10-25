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
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.utility.rdb.CachingStringIDHelper;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.TimeWarp;
import org.eclipse.osgi.util.NLS;

/** <p>Alarm Configuration as stored in RDB. It is identified
 *  by rdb_url plus root_component, which means all operations 
 *  with this object is related to its root_component.</p>
 *  <p>This class can read and write the configuration which is stored in RDB.</p>
 *  <p>The AlarmClientModel combines this with JMS communication,
 *  inform listeners etc.</p>
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmConfiguration
{
    /** Connection to configuration/state snapshot. */
    final private RDBUtil rdb;
    
    /** SQL strings */
    final private SQL sql;

    /** Mapping of severity IDs to strings */
    final private CachingStringIDHelper severity_mapping;

    /** Mapping of message IDs to strings */
    final private CachingStringIDHelper message_mapping;
    
    /** Root of the alarm tree.  */
    final private AlarmTreeRoot config_tree;

    /** Hash of all PVs in config_tree that maps PV name to PV */ 
    private HashMap<String, AlarmTreePV> pvs = new HashMap<String, AlarmTreePV>();

    /** Re-used statements */
    private PreparedStatement sel_items_by_parent_statement,
        sel_pv_by_id_statement, sel_pvs_by_parent_statement,
        sel_guidance_statement, sel_displays_statement, sel_commands_statement;

    /** Initialize
     *  @param url RDB URL
     *  @param root_name Name of root element
     *  @throws Exception on error
     */
    public AlarmConfiguration(final String url,
            final String root_name) throws Exception
    {
        this(url, root_name, false);
    }
 
    /** Initialize
     *  @param url RDB URL
     *  @param root_name Name of root element
     *  @param create Set true to create new tree if nothing found
     *  @throws Exception on error
     */
    public AlarmConfiguration(final String url,
            final String root_name,
            final boolean create) throws Exception
    {
       this(url, null, null, root_name, create);
    }

    /** Initialize
     *  @param url RDB URL
     *  @param user	RDB user name
     *  @param password RDB password
     *  @param root_name Name of root element
     *  @param create Set true to create new tree if nothing found
     *  @throws Exception on error
     */
    public AlarmConfiguration(final String url, final String user,
    		final String password,
            final String root_name,
            final boolean create) throws Exception
    {
        // Allow auto-reconnect...
        rdb = RDBUtil.connect(url, user, password, true);
        // .. but disable it while reading initial config. because that
        // can be 10% faster
        rdb.setAutoReconnect(false);
        sql = new SQL(rdb);
        severity_mapping = new CachingStringIDHelper(rdb,
                    sql.schema_prefix + sql.severity_table,
                    sql.severity_id_col, sql.severity_name_col);
        message_mapping = new CachingStringIDHelper(rdb,
                    sql.schema_prefix + sql.message_table,
                    sql.message_id_col, sql.message_name_col);
        config_tree = readAlarmTree(root_name, create);
        closeStatements();
        // Re-enable auto-connect
        rdb.setAutoReconnect(true);
    }
    
    /** List all configuration 'root' element names
     *  @return Array of 'root' elements
     *  @throws Exception on error
     */
    public String[] listConfigurations() throws Exception
    {
		final Statement statement = rdb.getConnection().createStatement();
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

	/** Must be called to release resources */
    public void close()
    {
        closeStatements();
        severity_mapping.dispose();
        message_mapping.dispose();
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
        return new AlarmTreeRoot(id, root_name);
    }

    /** Get alarm tree configuration
     *  @param root_name Name of root component
     *  @param create Create empty alarm tree?
     *  @return AlarmTreeRoot
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    private AlarmTreeRoot readAlarmTree(final String root_name,
            final boolean create) throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.sel_item_by_name);
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
            final Object parent = result.getObject(2);
            if (parent != null)
                throw new Exception("Root element " + root_name +
                                    " (id " + id + ") has parent");
            final AlarmTreeRoot root = createAlarmTreeRoot(id, root_name);
            
            root.setGuidance(readGuidance(id));
            root.setDisplays(readDisplays(id));
            root.setCommands(readCommands(id));
            readChildren(root);
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
     *  @throws Exception on error
     */
    private void readChildren(final AlarmTree parent) throws Exception
    {
        if (sel_items_by_parent_statement == null)
            sel_items_by_parent_statement =
                rdb.getConnection().prepareStatement(sql.sel_items_by_parent);
        sel_items_by_parent_statement.setInt(1, parent.getID());
        final ResultSet result = sel_items_by_parent_statement.executeQuery();
        
        final ArrayList<AlarmTreeComponent> nodes = new ArrayList<AlarmTreeComponent>();
        while (result.next())
        {
            final int id = result.getInt(1);
            //If this is a component, not a PV...
            if (!isPV(id))
            {
                final String name = result.getString(2);
                final AlarmTreeComponent node = new AlarmTreeComponent(id, name, parent);               
                final Timestamp config_time = result.getTimestamp(3);
                if (config_time != null)
                    node.setConfigTime(TimeWarp.getCSSTimestamp(config_time));
                node.setGuidance(readGuidance(id));
                node.setDisplays(readDisplays(id));
                node.setCommands(readCommands(id));              
                nodes.add(node);
            }
        }
        result.close();

        // Recurse to children of child entry
        // Cannot do that inside the above while() because that would reuse
        // the statement of the current ResultSet
        for (AlarmTreeComponent node : nodes)
            readChildren(node);
        nodes.clear();

        readPVs(parent);
    }

    /** Read configuration of PVs
     *  @param parent Parent node. PVs get added to it.
     *  @throws Exception on error
     */
    private void readPVs(final AlarmTree parent) throws Exception
    {
        if (sel_pvs_by_parent_statement == null)
            sel_pvs_by_parent_statement =
                    rdb.getConnection().prepareStatement(sql.sel_pvs_by_parent);
        sel_pvs_by_parent_statement.setInt(1, parent.getID());
        final ResultSet result = sel_pvs_by_parent_statement.executeQuery();
        while (result.next())
        {
            final int id = result.getInt(1);
            final String name = result.getString(2);
            final AlarmTreePV pv = new AlarmTreePV(id, name, parent);
            pvs.put(name, pv);
            
            pv.setGuidance(readGuidance(id));
            pv.setDisplays(readDisplays(id));
            pv.setCommands(readCommands(id));
            
            pv.setDescription(result.getString(3));
           
            pv.setEnabled(result.getBoolean(4));
            pv.setAnnunciating(result.getBoolean(5));
            pv.setLatching(result.getBoolean(6));
            pv.setDelay(result.getInt(7));
            pv.setCount(result.getInt(8));
            pv.setFilter(result.getString(9));
            
            final Timestamp config_time = result.getTimestamp(16);
            if (config_time != null)
                pv.setConfigTime(TimeWarp.getCSSTimestamp(config_time));
            
            // If there is severity/status info, use it.
            // Otherwise leave PV "OK" as it was initialized.
            int severity_id = result.getInt(10);
            if (result.wasNull())
                continue;
            final SeverityLevel current_severity = SeverityLevel.parse(severity_mapping.find(severity_id).getName());

            // Current message was added later, so assume "" if not set
            int status_id = result.getInt(11);
            final String current_message = result.wasNull()
                ? "" //$NON-NLS-1$
                : message_mapping.find(status_id).getName();

            severity_id = result.getInt(12);
            if (result.wasNull())
                continue;
            final SeverityLevel severity = SeverityLevel.parse(severity_mapping.find(severity_id).getName());
            
            status_id = result.getInt(13);
            if (result.wasNull())
                continue;
            final String message = message_mapping.find(status_id).getName();
            final String value = result.getString(14); // OK to have null value
            final Timestamp sql_time = result.getTimestamp(15);
            if (result.wasNull())
                continue;
            final ITimestamp timestamp = TimeWarp.getCSSTimestamp(sql_time);
            pv.setAlarmState(current_severity, current_message, severity, message, value, timestamp);      
        }
    }
    
    /** Add a component to the model and RDB
     *  @param parent AlarmTreeRoot or ..Component under which to add the component
     *  @param name Name of the new component
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public AlarmTreeComponent addComponent(final AlarmTree parent,
            final String name) throws Exception
    {
        if (! ((parent instanceof AlarmTreeRoot) ||
               (parent instanceof AlarmTreeComponent)))
           throw new Exception(parent.getName() + " is not root nor Component");
        return (AlarmTreeComponent) addRootOrComponent(parent, name);
    }

    /** Create a new tree root, or add (sub)component to existing root
     *  or component.
     * @param parent Parent element in tree. <code>null</code> for root
     * @param name Name of component to add
     * @return AlarmTreeRoot or Component
     * @throws Exception on error
     */
    @SuppressWarnings("nls")
    private AlarmTree addRootOrComponent(
            final AlarmTree parent, final String name) throws Exception
    {
        if (parent instanceof AlarmTreePV)
            throw new Exception("Cannot add sub-element to PV " +
                    parent.getName());
        if (parent != null  &&  parent.getChild(name) != null)
            throw new Exception("Alarm configuration element " +
                    parent.getName() + " already has sub-element" + name);

        final int id = getNextItemID();
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.insert_item);
        try
        {
            statement.setInt(1, id);
            //if added a root, set its parent as NULL
            if (parent == null)
                statement.setNull(2, Types.INTEGER);
            else
                statement.setInt(2, parent.getID());
            statement.setString(3, name);
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        finally
        {
            statement.close();
        }
        //if added a root...
        if (parent == null)
            return createAlarmTreeRoot(id, name);
        return new AlarmTreeComponent(id, name, parent);
    }

    /** @return Next RDB ID for alarm component
     *  @throws Exception on error
     */
    private int getNextItemID() throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
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
    public AlarmTreePV addPV(final AlarmTreeComponent parent,
                      final String name) throws Exception
    {
        final AlarmTreePV found = findPV(name);
        if (found != null)
            throw new Exception(name + " already under " + found.getPathName());        
       
        final int id = getNextItemID();
        final PreparedStatement insertAsItem =
            rdb.getConnection().prepareStatement(sql.insert_item);        
        final PreparedStatement insertAsPV =
            rdb.getConnection().prepareStatement(sql.insert_pv);
        final String description = name;
        final boolean latch = true;
        final boolean annunciate = false;
        // Not used, but new PV starts out disabled, which makes sense
        // until it's "configured"
        // final boolean enabled = false;
      
        try
        { 
        	//Insert the PV as an item in the alarmtree table
            insertAsItem.setInt(1, id);
            insertAsItem.setInt(2, parent.getID());
            insertAsItem.setString(3, name);
            insertAsItem.executeUpdate();
            //Insert the PV into the pv table
            insertAsPV.setInt(1, id);
            insertAsPV.setString(2, description);
            insertAsPV.setBoolean(3, annunciate);
            insertAsPV.setBoolean(4, latch);
            insertAsPV.executeUpdate();           
            
            rdb.getConnection().commit();
            
        }catch (SQLException e) {
			rdb.getConnection().rollback();
			System.out.println("add PV into RDB failed!");
			throw e;
		}
        finally
        {
            insertAsItem.close();
            insertAsPV.close();
        }
        
        final AlarmTreePV pv = new AlarmTreePV(id, name, parent);
        pvs.put(name, pv);
        return pv;
    }


    /** Change an items configuration in RDB, includes guidance, displays, commands and config_time.
     *  @param item Item to configure (which already exists, won't be created)
     *  @param guidance Guidance strings
     *  @param displays Related displays
     *  @param commands Commands
     *  @throws Exception on error
     */
    public void configureItem(final AlarmTree item,
            final List<GDCDataStructure> guidance, final List<GDCDataStructure> displays,
            final List<GDCDataStructure> commands) throws Exception
    {
    	//update guidance, displays and commands
    	updateGDC(item.getID(), guidance, sql.delete_guidance_by_id, sql.insert_guidance);
    	updateGDC(item.getID(), displays, sql.delete_displays_by_id, sql.insert_display);
    	updateGDC(item.getID(), commands, sql.delete_commands_by_id, sql.insert_command);   	
    	
    	//update config_time
    	final PreparedStatement statement = 
    		rdb.getConnection().prepareStatement(sql.update_item_config_time);
    	
    	try {
    		final Timestamp config_time = new Timestamp(new Date().getTime());
    		statement.setTimestamp(1, config_time);
    		statement.setInt(2, item.getID());
    		statement.executeUpdate();
    		rdb.getConnection().commit();
    		// Update item's config time after RDB commit succeeded
            item.setConfigTime(TimeWarp.getCSSTimestamp(config_time));
    	} finally {
    		statement.close();
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
     *  @throws Exception on error
     */
    public void configurePV(final AlarmTreePV pv, final String description,
        final boolean enabled, final boolean annunciate, final boolean latch,
        final int delay, final int count, final String filter,
        final List<GDCDataStructure> guidance, final List<GDCDataStructure> displays,
        final List<GDCDataStructure> commands) throws Exception
    {
        configureItem(pv, guidance, displays, commands);
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.update_pv_config);
        try
        {
            statement.setString(1, description);
            statement.setBoolean(2, enabled);
            statement.setBoolean(3, annunciate);
            statement.setBoolean(4, latch);
            statement.setInt(5, delay);
            statement.setInt(6, count);
            statement.setString(7, filter);
            statement.setInt(8, pv.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        finally
        {
            statement.close();
        }
    }
    
    /** Change item's name
     *  @param item Item to change
     *  @param new_name New name for the item
     *  @throws Exception on error
     */
    public void rename(final AlarmTree item, final String new_name) throws Exception
    {
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.rename_item);
        try
        {
            statement.setString(1, new_name);
            statement.setInt(2, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        finally
        {
            statement.close();
        }
    }

    /** Change item's location in alarm configuration hierarchy
     *  @param item Item to move
     *  @param new_path New path for the item
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public void move(final AlarmTree item, final String new_path) throws Exception
    {
        // Locate new parent ID
        final AlarmTree parent = config_tree.getItemByPath(new_path);
        if (parent == null)
            throw new Exception(NLS.bind("Unknown alarm configuration path {0}",
                                         new_path));
        // Update parent of item (PV or component)
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.move_item);
        try
        {
            statement.setInt(1, parent.getID());
            statement.setInt(2, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }
        finally
        {
            statement.close();
        }
    }

    /** Remove all items from configuration.
     *  (The root element that identifies the configuration remains)
     *  @throws Exception on error
     */
    public void removeAllItems() throws Exception
    {
        while (config_tree.getChildCount() > 0)
            remove(config_tree.getChild(0));
    }

    /** Remove item and all sub-items from alarm tree.
     *  @param item Item to remove
     *  @throws Exception on error
     */
    public void remove(final AlarmTree item) throws Exception
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
    private void removeSubtree(final AlarmTree item) throws Exception
    {
        // First recurse down
        while (item.getChildCount() > 0)
        {
            final AlarmTree child = item.getChild(0);
            if (child instanceof AlarmTreePV)
                removePV((AlarmTreePV) child);
            else
                removeSubtree(child);
        }
        // Then remove item itself
      
        
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql.delete_component_by_id);
        try
        {
        	deleteGDCWithoutCommit(item.getID());
            statement.setInt(1, item.getID());
            statement.executeUpdate();
            rdb.getConnection().commit();
        }catch (SQLException e) {
			rdb.getConnection().rollback();			
			throw e;
        }finally
        {
            statement.close();
        }
        if (item.getParent() != null)
            item.getParent().removeChild(item);
    }

    /** Remove PV from alarm tree.
     *  <p>
     *  Does not send events.
     *  @param pv PV to remove
     *  @throws Exception on error
     */
    private void removePV(final AlarmTreePV pv) throws Exception
    {
        final PreparedStatement delPVStatement =
            rdb.getConnection().prepareStatement(sql.delete_pv_by_id);
        final PreparedStatement delCMPNTStatement =
            rdb.getConnection().prepareStatement(sql.delete_component_by_id);
        
        try
        {
        	deleteGDCWithoutCommit(pv.getID());
           
        	delPVStatement.setInt(1, pv.getID());
            delPVStatement.executeUpdate();
            
            delCMPNTStatement.setInt(1, pv.getID());
            delCMPNTStatement.executeUpdate();           
            
            rdb.getConnection().commit();
        }catch (SQLException e) {
			rdb.getConnection().rollback();			
			throw e;
        }
        finally
        {
            delPVStatement.close();
            delCMPNTStatement.close();
        }
        pvs.remove(pv.getName());
        if (pv.getParent() != null)
            pv.getParent().removeChild(pv);
    }

    /** Update PV configuration from RDB
     *  @param path Path name of the added/removed/changed item or null
     */
    @SuppressWarnings("nls")
    public void readPVConfig(final AlarmTreePV pv) throws Exception
    {
        if (sel_pv_by_id_statement == null)
            sel_pv_by_id_statement = rdb.getConnection()
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
        
        pv.setGuidance(readGuidance(pv.getID()));
        pv.setDisplays(readDisplays(pv.getID()));
        pv.setCommands(readCommands(pv.getID()));
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

    
    /**Update guidance/displays/commands in RDB by id
     * @param id The id of the item in alarmtree.
     * @param gdcList guidance/displays/commands ArrayList.
     * @param del_gdc_sql The sql update sentence for deleting old GDC.
     * @param insert_gdc_sql The sql update sentence for inserting new GDC.
     * @throws SQLException
     */
    private void updateGDC(final int id, final List<GDCDataStructure> gdcList, 
    		final String del_gdc_sql, final String insert_gdc_sql) throws Exception {
    	
    	final PreparedStatement deleteGDC = 
    		rdb.getConnection().prepareStatement(del_gdc_sql);
    	final PreparedStatement insertGDC =
			rdb.getConnection().prepareStatement(insert_gdc_sql);
    	
    		try {
				deleteGDC.setInt(1, id);
				deleteGDC.executeUpdate();
				int order = 0;
				if(gdcList != null && !gdcList.isEmpty()) {
					for(GDCDataStructure gdc : gdcList) {
						insertGDC.setInt(1, id);
						insertGDC.setInt(2, order);
						insertGDC.setString(3, gdc.getTitle());
						insertGDC.setString(4, gdc.getDetails());
						insertGDC.executeUpdate();
						order++;
					}
				}
				rdb.getConnection().commit();
			} catch (Exception e) {
				rdb.getConnection().rollback();
				throw e;
			} finally {
				deleteGDC.close();
				insertGDC.close();
			}
    }
    
    /** Delete all guidance, displays, commands for an item
     *  @param id Item ID
     *  @throws Exception on error
     */
    private void deleteGDCWithoutCommit(final int id) throws Exception
    {
        final PreparedStatement delGuidanceStatement =
            rdb.getConnection().prepareStatement(sql.delete_guidance_by_id);
        final PreparedStatement delDisplayStatement =
            rdb.getConnection().prepareStatement(sql.delete_displays_by_id);
        final PreparedStatement delCommandsStatement =
            rdb.getConnection().prepareStatement(sql.delete_commands_by_id);
        try
        {
            delGuidanceStatement.setInt(1, id);
            delGuidanceStatement.executeUpdate();
            delDisplayStatement.setInt(1, id);
            delDisplayStatement.executeUpdate();
            delCommandsStatement.setInt(1, id);
            delCommandsStatement.executeUpdate();
        }
        finally 
        {
        	delGuidanceStatement.close();
        	delDisplayStatement.close();
        	delCommandsStatement.close();       	
        }
    }
    
    /** Whether the item marked by <code>id</code> is a PV or not.
     * @param id item id in alarmtree.
     * @return true if it is a PV, false if it is not a PV.
     * @throws Exception
     */
    private boolean isPV(final int id) throws Exception
    {
        if (sel_pv_by_id_statement == null)
            sel_pv_by_id_statement = rdb.getConnection()
                .prepareStatement(sql.sel_pv_by_id);
        sel_pv_by_id_statement.setInt(1, id);
        final ResultSet result = sel_pv_by_id_statement.executeQuery();
        return result.next();
    }

    /** Close prepared statements that are lazily created when reading config */
    private void closeStatements()
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
            if (sel_pvs_by_parent_statement != null)
            {
                sel_pvs_by_parent_statement.close();
                sel_pvs_by_parent_statement = null;
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
}
