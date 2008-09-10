package org.csstudio.utility.recordproperty.rdb.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.csstudio.config.savevalue.service.ChangelogService;
import org.csstudio.config.savevalue.service.SaveValueServiceException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.csstudio.utility.recordproperty.Messages;
import org.csstudio.utility.recordproperty.RecordPropertyEntry;
import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * RecordPropertyGetRDB gets data (record fields) from RDB, DAL and RMI.
 * 
 * @author Rok Povsic
 */
public class RecordPropertyGetRDB {
	
	ResultSet resultSet;
	ResultSet resultSetFieldNames;
	
	ArrayList<RecordPropertyEntry> data = new ArrayList<RecordPropertyEntry>();
	
	private String fieldName;
	private String valueRdb;
	private DBConnect connect;
	private DBConnect connectForFieldNames;
	
	private String fieldType;
	
	/**
	 * If DAL does not have any data, it prints this.
	 */
	private String value = Messages.RecordPropertyView_NA;
	
	/**
	 * Record name, that you have to get data of
	 */
	private String record;
	
	private String nameIOC;
	private ChangelogEntry[] entryRMI;
	private String valueRMI;
	
	/**
	 * A string that is displayed when no access.
	 */
	private String na = Messages.RecordPropertyView_NA;
	
	/**
	 * Get if RDB is offline.
	 */
	private String rtype;
	
	RecordPropertyEntry[] stringArray;
	
	/**
	 * The logger.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();

	/**
	 * Gets all possible data that can be collected.
	 * @param _record name of the record
	 * @return stringArray
	 */
	public RecordPropertyEntry[] getData(String _record) {
		record = _record;
		
		record = validateRecord(record);

		if(getRtypeFromDAL()) {
			// nothing is to be done here
		} else {
			if(!getRtypeFromRecordName(record).equals("not-valid")) {
				// nothing is to be done here
			} else {
				// nothing is printed, everything is empty
			}
		}

		if (getDataFromRDB()) {			
			getDataIfRDB();
		} else {
			if(getFieldNamesFromRDBsql()) {
				getFieldNamesFromRDB();
			} else {
				// nothing is printed, everything is grayed out
			}
		}
		
		return stringArray;
				
	}
	
	/**
	 * Gets data if RDB is online.
	 * @return
	 */
	private void getDataIfRDB() {
		
		getDataFromRMI();
        
        try {
        	// Goes through every row and gets data.
			while(resultSet.next()) {
				fieldName = resultSet.getString("FIELD_NAME");
				valueRdb = resultSet.getString("VALUE");
				
				while(resultSetFieldNames.next()) {	
					if(fieldName.equals(resultSetFieldNames.getString("FIELD_NAME"))) {
						fieldType = resultSetFieldNames.getString("FIELD_TYPE");
						
						String badType = "15";
						
						if(!fieldType.equals(badType)) {
							getDataFromDAL();
						} else {
							value = na;
						}
						break;
					}
				}
				
				// If IOC(RMI)(4th column) does not have any data, it prints this.
				valueRMI = na;
				
				// Search if record.fieldName matches one in IOC(RMI) and sets it.
				for (int i = 0; i < entryRMI.length; i++) {
					if((record+"."+fieldName).equals(entryRMI[i].getPvName())) {
						valueRMI = entryRMI[i].getValue();
						break;
					}
				}
				
				// Adds new line to table
				RecordPropertyEntry entry = new RecordPropertyEntry(fieldName, valueRdb, value, valueRMI);
				data.add(entry);
				
				// Set value back to 'not found', to overwrite last value.
				value = na;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        stringArray = (RecordPropertyEntry[])data.toArray(new RecordPropertyEntry[data.size()]);
		
        connect.closeConnection();
	}
	
	/**
	 * Gets data if RDB is not online.
	 */
	private boolean getRtypeFromDAL() {
		ProcessVariableAdressFactory _addressFactory;
		
		IProcessVariableConnectionService _connectionService;
		
		ProcessVariableConnectionServiceFactory _connectionFactory = ProcessVariableConnectionServiceFactory.getDefault();
		
		_addressFactory = ProcessVariableAdressFactory.getInstance();
		
		_connectionService = _connectionFactory.createProcessVariableConnectionService();
		
		try {
			rtype = _connectionService.getValueAsString(_addressFactory
					.createProcessVariableAdress("dal-epics://"+record+".RTYP"));
			return true;
		} catch (ConnectionException e) {
			return false;
		}
	}
		
	/**
	 * Gets data from RDB.
	 */
	private boolean getDataFromRDB() {
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
            
            resultSetFieldNames = connect.executeQuery(
            		"select tv.field_name, tv.field_type from epics_version ev, " +
            		"rec_type rt, type_val tv where ev.epics_id = '4061' and " +
            		"ev.epics_id = rt.epics_id and rt.record_type = '" + rtype + "' and " +
            		"rt.type_id = tv.type_id"
            );

            //Check weather the result set is empty 
            if(!resultSet.isBeforeFirst()) {
            	return false;
            }
            return true;
            
        } catch (SQLException e) {
        	e.printStackTrace();
            return false;
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
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets data from RMI (IOC).
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
			_log.info(this, "Connecting to RMI registry."); //$NON-NLS-1$
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
	}
	
	/**
	 * Gets field names from RDB (SQL).
	 */
	private boolean getFieldNamesFromRDBsql() {
		connectForFieldNames = new DBConnect(new OracleSettings());
		
		connectForFieldNames.openConnection();
		
		try {
            resultSetFieldNames = connectForFieldNames.executeQuery(
            		"select tv.field_name, tv.field_type from epics_version ev, " +
            		"rec_type rt, type_val tv where ev.epics_id = '4061' and " +
            		"ev.epics_id = rt.epics_id and rt.record_type = '" + rtype + "' and " +
            		"rt.type_id = tv.type_id"
            );

            return true;
            
        } catch (SQLException e) {
        	
            return false;
        }
	}
	
	/**
	 * Gets field names from RDB. It is used only when normal RDB connect fails.
	 */
	private void getFieldNamesFromRDB() {
		
		getDataFromRMI();
		
	       try {
	    	   // Goes through every row and gets data.
				while(resultSetFieldNames.next()) {
					fieldName = resultSetFieldNames.getString("FIELD_NAME");
					fieldType = resultSetFieldNames.getString("FIELD_TYPE");
					
					String badType = "15";
					
					if(!fieldType.equals(badType)) {
						getDataFromDAL();
					} else {
						value = na;
					}
					 
					// If IOC(RMI)(4th column) does not have any data, it prints this.
					valueRMI = na;
					
					// Search if record.fieldName matches one in IOC(RMI) and sets it.
					for (int i = 0; i < entryRMI.length; i++) {
						if((record+"."+fieldName).equals(entryRMI[i].getPvName())) {
							valueRMI = entryRMI[i].getValue();
							break;
						}
					}
					
					// Adds new line to table
					RecordPropertyEntry entry = new RecordPropertyEntry(fieldName, "", value, valueRMI);
					data.add(entry);
					
					// Set value back to 'not found', to overwrite last value.
					value = na;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        
	        stringArray = (RecordPropertyEntry[])data.toArray(new RecordPropertyEntry[data.size()]);
			
	        connect.closeConnection();
	}
	
	/**
	 * Deletes last part of record name, if it is less than 6 char and a dot,
	 * using Regular Expressions.
	 * 
	 * Sample:
	 * 		record name: "recordname_type.xxxx"
	 * then
	 * 		returns "recordname_type"
	 * 
	 * @param _record record name
	 * @return fixed record name
	 */
	private String validateRecord(String _record) {
		
		String REGEX = "(\\.[a-zA-Z1-9]{0,6})$";
		
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(_record);
		
		_record = m.replaceAll("");
		
		return _record;
	}
	
	/**
	 * Extracts record type out of a record name by deleting
	 * everything but record type, using Regular Expressions.
	 * 
	 * Sample:
	 * 		record name: "record:.name_type"
	 * then
	 * 		returns: "type"
	 * 
	 * @param _record record name
	 * @return type of a record
	 */
	private String getRtypeFromRecordName(String _record) {
		
		if(_record.indexOf("_") > -1) {
			String REGEX = "^([a-zA-Z1-9\\.:]+_){1,6}";
			
			Pattern p = Pattern.compile(REGEX);
			Matcher m = p.matcher(_record);
			
			_record = m.replaceAll("");
		} else {
			_record = "not-valid";
		}
		
		return _record;
	}
}
