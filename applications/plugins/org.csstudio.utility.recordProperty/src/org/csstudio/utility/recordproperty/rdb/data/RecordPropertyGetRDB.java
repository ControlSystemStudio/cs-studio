package org.csstudio.utility.recordproperty.rdb.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.csstudio.utility.recordproperty.Activator;
import org.csstudio.utility.recordproperty.RecordPropertyEntry;
import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * RecordPropertyGetRDB gets data (record fields) from RDB, DAL and RMI.
 * 
 * @author Rok Povsic
 */
public class RecordPropertyGetRDB {

	ResultSet resultSet;
	
	ArrayList<RecordPropertyEntry> data = new ArrayList<RecordPropertyEntry>();
	
	private String fieldName;
	private String valueRdb;
	private DBConnect connect;
	
	/**
	 * If DAL does not have any data, it prints this.
	 */
	private String value = "Field value not found";
	
	/**
	 * Record name, that you have to get data of
	 */
	private String record;
	
	private String nameIOC;
	private ChangelogEntry[] entryRMI;
	private String valueRMI = "No value found";
	
	/**
	 * The logger.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();
		
	public RecordPropertyEntry[] getData(String _record) {
		record = _record;
		
		getDataFromRDB();
		
		getDataFromRMI();
        
        try {
			while(resultSet.next()) {
			 fieldName = resultSet.getString("FIELD_NAME");
			 valueRdb = resultSet.getString("VALUE");
								
				getDataFromDAL();
				
				for (int i = 0; i < entryRMI.length; i++) {
					if((record+"."+fieldName).equals(entryRMI[i].getPvName())) {
						valueRMI = entryRMI[i].getValue();
						break;
					} else {
						valueRMI = "Field value not found";
					}
				}
				
				RecordPropertyEntry entry = new RecordPropertyEntry(fieldName, valueRdb, value, valueRMI);
				data.add(entry);
				
				value = "Field value not found";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        RecordPropertyEntry[] stringArray = (RecordPropertyEntry[])data.toArray(new RecordPropertyEntry[data.size()]);
		
        connect.closeConnection();
        
		return stringArray;
				
	}
	
	/**
	 * Gets data from RDB.
	 */
	private void getDataFromRDB() {
		connect = new DBConnect(new OracleSettings());
		
		connect.openConnection();
		
		try {
            resultSet = connect.executeQuery(
            		"With LVL0 as (Select tv.field_name||tv.prompt as checkValue " +
	                "from instance_values iv, type_val tv, instance_records tmp " +
	                "where (iv.instance_record_id = tmp.instance_record_id) and " +
	                      "(tmp.instance_record = '" + record + "') and " +
	                      "(iv.type_id = tv.type_id) and (iv.field_index = tv.field_index) " +
	                "group by tv.field_name||tv.prompt), " +
	     "LVL1 as (Select tv.field_name||tv.prompt as checkValue " +
	                "from type_val tv, project_values pv, instance_records tmp " +
	                "where (pv.prototype_record_id = tmp.prototype_record_id) and " +
	                      "(tmp.instance_record = '" + record + "') and " +
	                      "(tv.type_id = pv.type_id) and (tv.field_index = pv.field_index) " +
	                "group by tv.field_name||tv.prompt " +
	               "minus " +
	              "select checkValue from LVL0), " +
	     "LVL2 as (Select tv.field_name||tv.prompt as checkValue " +
	                "from type_val tv, instance_records tmp " +
	                "where (tv.type_id = tmp.type_id) and " +
	                      "(tmp.instance_record = '" + record + "') " +
	                "group by tv.field_name||tv.prompt " +
	               "minus " +
	              "select checkValue from LVL0 " +
	               "minus " +
	              "select checkValue from LVL1) " +
			
		"Select 0 lvl, tv.field_index, tv.field_name, tv.prompt, iv.value " +
		  "from instance_values iv, type_val tv, instance_records tmp " +
		  "where (iv.instance_record_id = tmp.instance_record_id) and " +
		        "(tmp.instance_record = '" + record + "') and " +
		        "(iv.type_id = tv.type_id) and (iv.field_index = tv.field_index) " +
		 "union " +
		"select 1 lvl, tv.field_index, tv.field_name, tv.prompt, pv.value " + 
		  "from type_val tv, project_values pv, instance_records tmp " +
		  "where (pv.prototype_record_id = tmp.prototype_record_id) and " +
		        "(tmp.instance_record = '" + record + "') and " +
		        "(tv.type_id = pv.type_id) and (tv.field_index = pv.field_index) and " +
		        "(tv.field_name||tv.prompt in (select checkValue from LVL1)) " +
		 "union " +
		"select 2 lvl, tv.field_index, tv.field_name, tv.prompt, tv.default_value as value " +
		  "from type_val tv, instance_records tmp " +
		  "where (tv.type_id = tmp.type_id) and (tmp.instance_record = '" + record + "') and " +
		        "(tv.field_name||tv.prompt in (select checkValue from LVL2)) " +
		"order by field_index "
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Gets data from DAL.
	 */
	private void getDataFromDAL() {
		ProcessVariableAdressFactory _addressFactory;
		
		IProcessVariableConnectionService _connectionService;
		
		ProcessVariableConnectionServiceFactory _connectionFactory = ProcessVariableConnectionServiceFactory.getDefault();
		
		_addressFactory = ProcessVariableAdressFactory.getInstance();
		
		_connectionService = _connectionFactory.createProcessVariableConnectionService();
		
		try {
			value = _connectionService.getValueAsString(_addressFactory
					.createProcessVariableAdress("dal-epics://"+record+"."+fieldName));
		} catch (ConnectionException e) {
			CentralLogger.getInstance().getLogger(this).info("Field value not found: " + record + "." + fieldName);
//			e.printStackTrace();
		}
	}
	
	/**
	 * Gets data from RMI.
	 */
	private void getDataFromRMI() {
		Registry reg;
		
		try{
			nameIOC = IocFinder.getIoc(record);
			
			IPreferencesService prefs = Platform.getPreferencesService();
			String registryHost = prefs.getString(
					"org.csstudio.config.savevalue.ui",
					"RmiRegistryServer",
					null, null);
			CentralLogger.getInstance().getLogger(this).info("Connecting to RMI registry."); //$NON-NLS-1$
			reg = LocateRegistry.getRegistry(registryHost);
			
			ChangelogService cs = (ChangelogService) reg
			.lookup("SaveValue.changelog"); //$NON-NLS-1$
			entryRMI = cs.readChangelog(nameIOC);
			
						
			
		} catch (RemoteException e) {
			_log.error(this, "Could not connect to RMI registry", e); //$NON-NLS-1$

		} catch (NotBoundException e) {
			_log.error(this, "Changelog Service not bound in RMI registry", e); //$NON-NLS-1$

		} catch (SaveValueServiceException e) {
			_log.error(this, "Server reported an error reading the changelog", e); //$NON-NLS-1$

		}
		
		/*
		Registry reg;
		
		try {
			IPreferencesService prefs = Platform.getPreferencesService();
			String registryHost = prefs.getString(
					Activator.PLUGIN_ID,
					"RmiRegistryServer",
					null, null);
			CentralLogger.getInstance().getLogger(this).info("Connecting to RMI registry."); //$NON-NLS-1$
			
			
				reg = LocateRegistry.getRegistry(registryHost);
			
			RecordPropertyService rps = (RecordPropertyService) reg
			.lookup("SaveValue.changelog"); //$NON-NLS-1$
			entryRMI = rps.readRecordProperty(record);
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/
	}
}
