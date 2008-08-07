package org.csstudio.utility.recordproperty.rdb.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.csstudio.utility.recordproperty.rdb.config.OracleSettings;
import org.csstudio.utility.recordproperty.rdb.config.IOracleSettings;

/**
 * Enables opening and closing of the connection for the given {@link IOracleSettings}.
 *
 * @author Alen Vrecko
 */
public class DBConnect {

	private IOracleSettings settings;
	private Connection connection;
	
	public static String record = "alarmTest:RAMPA_calc";
	
	// Test of database connection.
	/*
	public static void main(String[] args) {

        DBConnect connect = new DBConnect(new OracleSettings());

        connect.openConnection();

        try {
            ResultSet resultSet = connect.executeQuery("With LVL0 as (Select tv.field_name||tv.prompt as checkValue " +
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
		"order by field_index ");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connect.closeConnection();
        
        System.out.println("Ok");

    }
    */
	
	public DBConnect(IOracleSettings _settings) {
		settings = _settings;
	}
	
	public void openConnection() {
        try {
            DriverManager.registerDriver(settings.getDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Missing oracle driver jar - missing dependency. Should not happen");
        }
        try {
            connection = DriverManager.getConnection(settings.getConnection(),
                    settings.getUsername(), settings.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public void closeConnection() {

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {

            throw new RuntimeException("Fail to close connection ", e);
        }
    }
	
	public ResultSet executeQuery(String query) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("Cannot execute query while connection is not establised");
        }

        return connection.createStatement().executeQuery(query);
    }

	public Connection getConnection() {
        return connection;
    }

    public IOracleSettings getSettings() {
        return settings;
    }
}
