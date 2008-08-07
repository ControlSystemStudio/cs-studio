package org.csstudio.utility.recordproperty.rdb.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.csstudio.utility.recordproperty.RecordPropertyEntry;
import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;

public class RecordPropertyGetRDB {

	ResultSet resultSet;
	
	ArrayList<RecordPropertyEntry> data;
	
	public RecordPropertyEntry[] getData() {
		/* Test data for table.
		RecordPropertyEntry[] data = {
				new RecordPropertyEntry("Test1", "Test", "Test", "Test"),
				new RecordPropertyEntry("Test2", "Test", "Test", "Test"),
				new RecordPropertyEntry("Test3", "Test", "Test", "Test")
		};
		*/
		
		String record = "alarmTest:RAMPA_calc";
		
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
        
        // ERROR HERE
        try {
			while(resultSet.next()) {
				String var = resultSet.getString("tv.field_name");
				data.add(new RecordPropertyEntry(var, "test", "test", "test"));
			}
		} catch (SQLException e) {

		}

        connect.closeConnection();
        
        RecordPropertyEntry[] stringArray = (RecordPropertyEntry[])data.toArray(new RecordPropertyEntry[data.size()]);
		
		return stringArray;
	}
}
