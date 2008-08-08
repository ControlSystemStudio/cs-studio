package org.csstudio.utility.recordproperty.rdb.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.utility.recordproperty.RecordPropertyEntry;
import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;

/**
 * RecordPropertyGetRDB gets data from database.
 * 
 * @author Rok Povsic
 */
public class RecordPropertyGetRDB {

	ResultSet resultSet;
	
	ArrayList<RecordPropertyEntry> data = new ArrayList<RecordPropertyEntry>();
	
	public RecordPropertyEntry[] getData() {
		/* Test data for table.
		RecordPropertyEntry[] data = {
				new RecordPropertyEntry("Test1", "Test", "Test", "Test"),
				new RecordPropertyEntry("Test2", "Test", "Test", "Test"),
				new RecordPropertyEntry("Test3", "Test", "Test", "Test")
		};
		*/
		
		String record = "TS2ATH9V103_bi";
		
		DBConnect connect = new DBConnect(new OracleSettings());
		
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
        
        try {
			while(resultSet.next()) {
				String var1 = resultSet.getString("FIELD_NAME");
				String var2 = resultSet.getString("VALUE");
				
				String value = "Field value not found";
				
				// getting the third column, value, from DAL
				ProcessVariableAdressFactory _addressFactory;
				
				IProcessVariableConnectionService _connectionService;
				
				ProcessVariableConnectionServiceFactory _connectionFactory = ProcessVariableConnectionServiceFactory.getDefault();
				
				_addressFactory = ProcessVariableAdressFactory.getInstance();
				
				_connectionService = _connectionFactory.createProcessVariableConnectionService();
				
				try {
					value = _connectionService.getValueAsString(_addressFactory
							.createProcessVariableAdress("dal-epics://"+record+"."+var1));
				} catch (ConnectionException e) {
					CentralLogger.getInstance().getLogger(this).info("Field value not found: " + record + "." + var1);
//					e.printStackTrace();
				}
				
				RecordPropertyEntry entry = new RecordPropertyEntry(var1, var2, value, "test");
				data.add(entry);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        RecordPropertyEntry[] stringArray = (RecordPropertyEntry[])data.toArray(new RecordPropertyEntry[data.size()]);
		
        connect.closeConnection();
        
		return stringArray;
				
	}
}
