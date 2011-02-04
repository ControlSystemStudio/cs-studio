/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb.internal;

import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** All the SQL strings in one place.
 *  @author Kay Kasemir
 *  @author Lana Abadie (PostgreSQL)
 */
@SuppressWarnings("nls")
public class SQL
{
    // 'smpl_eng' table
    final public String smpl_eng_next_id;
    final public String smpl_eng_insert;
    final public String smpl_eng_sel_by_name;
    final public String smpl_eng_sel_by_id;
    final public String smpl_eng_delete;

    // 'chan_grp' table
    final public String chan_grp_delete_by_engine_id;
    final public String chan_grp_next_id;
    final public String chan_grp_sel_by_name_and_eng_id;
    final public String chan_grp_sel_by_id;
    final public String chan_grp_insert;
    final public String chan_grp_sel_by_eng_id;
    final public String chan_grp_set_enable_channel;
 
    // 'retent' table
    final public String retention_table;
    final public String retention_id_column;
    final public String retention_name_column;

    // 'channel' table
    final public String channel_insert;
    final public String channel_sel_next_id;
    final public String channel_sel_by_name;
	final public String channel_sel_by_pattern;
	final public String channel_sel_last_time_by_id;
    final public String channel_sel_by_group_id;
    final public String channel_set_grp_by_id;
    final public String channel_clear_grp_for_engine;
    final public String channel_set_sampling;

    // 'sample mode' table
    final public String sample_mode_sel;
    
    // 'enum_metadata' table
    final public String enum_sel_num_val_by_channel;
    final public String enum_delete_by_channel;
    final public String enum_insert_channel_num_val;
	
    // 'num_metadata' table
    final public String numeric_meta_sel_by_channel;
    final public String numeric_meta_insert;
    final public String numeric_meta_delete_by_channel;
    
    // 'severity' table
    final public String severity_table;
    final public String severity_id_column;
    final public String severity_name_column;
	
	// 'status' table
    final public String status_table;
    final public String status_id_column;
    final public String status_name_column;

	// 'sample' table
	final public String sample_sel_initial_by_id_time;
	final public String sample_sel_by_id_start_end;
	final public String sample_sel_array_vals;
	final public String sample_insert_double;
	final public String sample_insert_double_array_element;
	final public String sample_insert_int;
	final public String sample_insert_string;
	
	/** Create SQL strings
	 *  @param dialect RDB dialect
	 *  @param use_staging Use the 'main' tables or the staging tables?
	 */
	public SQL(final Dialect dialect, boolean use_staging)
	{
		// Schema prefix for the tables
	    String prefix = "";
	    if (dialect == RDBUtil.Dialect.Oracle)
	    {
	        final String schema = RDBArchivePreferences.getSchema();
	        if (schema != null  &&  schema.length() > 0)
	            prefix = schema + ".";
	    }
	    
	    // Name of the sample and array_val tables
	    final String sample = use_staging ? "sample_stage" : "sample";
        final String array_val = use_staging ? "array_val_stage" : "array_val";
		
	    // 'smpl_eng' table
        smpl_eng_next_id = "SELECT MAX(eng_id) FROM " + prefix + "smpl_eng";
        smpl_eng_insert = "INSERT INTO " + prefix + "smpl_eng(eng_id, name, descr, url) VALUES (?,?,?,?)";
        smpl_eng_sel_by_name = "SELECT eng_id, descr, url FROM " + prefix + "smpl_eng WHERE name=?";
        smpl_eng_sel_by_id = "SELECT name, descr, url FROM " + prefix + "smpl_eng WHERE eng_id=?";
        smpl_eng_delete = "DELETE FROM " + prefix + "smpl_eng WHERE eng_id=?";

        // 'chan_grp' table
        chan_grp_delete_by_engine_id = "DELETE FROM " + prefix + "chan_grp WHERE eng_id=?";
        chan_grp_next_id = "SELECT MAX(grp_id) FROM " + prefix + "chan_grp";
        chan_grp_sel_by_name_and_eng_id = "SELECT grp_id, enabling_chan_id FROM " + prefix + "chan_grp WHERE name=? AND eng_id=?";
        chan_grp_sel_by_id = "SELECT name, eng_id, enabling_chan_id FROM " + prefix + "chan_grp WHERE grp_id=?";
        chan_grp_insert = "INSERT INTO " + prefix + "chan_grp (grp_id, name, eng_id, enabling_chan_id) VALUES (?,?,?,?)";
        chan_grp_sel_by_eng_id = "SELECT grp_id, name, enabling_chan_id FROM " + prefix + "chan_grp WHERE eng_id=? ORDER BY name";
        
        chan_grp_set_enable_channel = "UPDATE " + prefix + "chan_grp SET enabling_chan_id=? WHERE grp_id=?";
        
        // 'retent' table
        retention_table = prefix + "retent";
        retention_id_column = "retent_id";
        retention_name_column = "descr";
        
		// 'channel' table
//        channel_table = schema + "channel";
//		channel_id_colunm = "channel_id";
//	    channel_name_column = "name";
        
        channel_insert = "INSERT INTO " + prefix + "channel (channel_id, name, smpl_mode_id, smpl_val, smpl_per) VALUES (?,?,?,?,?)";
        
        channel_sel_next_id = "SELECT MAX(channel_id) FROM " + prefix + "channel";
        
        channel_sel_by_name = 
            "SELECT channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per FROM " + prefix + "channel WHERE name=?";
		
		if (dialect == RDBUtil.Dialect.Oracle)
			channel_sel_by_pattern = "SELECT channel_id, name, grp_id, smpl_mode_id, smpl_val, smpl_per FROM " + prefix + "channel WHERE REGEXP_LIKE(name, ?, 'i') ORDER BY name";
		else if (dialect == RDBUtil.Dialect.PostgreSQL)
			channel_sel_by_pattern = "SELECT channel_id, name, grp_id, smpl_mode_id, smpl_val, smpl_per FROM " + prefix + "channel WHERE name ~* ORDER BY name";
		else
			channel_sel_by_pattern = "SELECT channel_id, name, grp_id, smpl_mode_id, smpl_val, smpl_per FROM " + prefix + "channel WHERE name REGEXP ? ORDER BY name";
		channel_sel_last_time_by_id = "SELECT MAX(smpl_time) FROM " + prefix + sample + " WHERE channel_id=?";
		channel_sel_by_group_id = "SELECT channel_id, name, smpl_mode_id, smpl_val, smpl_per FROM " + prefix + "channel WHERE grp_id=? ORDER BY name";
		channel_set_grp_by_id = "UPDATE " + prefix + "channel SET grp_id=? WHERE channel_id=?";        
        channel_clear_grp_for_engine =
            "UPDATE " + prefix + "channel SET grp_id=null WHERE grp_id IN " +
                "(SELECT grp_id FROM " + prefix + "chan_grp WHERE eng_id=?)";
        channel_set_sampling = "UPDATE " + prefix + "channel SET smpl_mode_id=?,smpl_val=?,smpl_per=? WHERE channel_id=?";
        
        // 'sample mode' table
        sample_mode_sel = "SELECT smpl_mode_id, name, descr FROM " + prefix + "smpl_mode";

		// 'enum_metadata' table
	    enum_sel_num_val_by_channel = "SELECT enum_nbr, enum_val FROM "
	        + prefix + "enum_metadata WHERE channel_id=? ORDER BY enum_nbr";
	    enum_delete_by_channel = "DELETE FROM " + prefix + "enum_metadata WHERE channel_id=?";
	    enum_insert_channel_num_val = "INSERT INTO " + prefix
	        + "enum_metadata(channel_id,enum_nbr, enum_val) VALUES(?,?,?)";
	    
	    // 'num_metadata' table
	    numeric_meta_sel_by_channel = "SELECT low_disp_rng, high_disp_rng," +
	    		" low_warn_lmt, high_warn_lmt," +
	    		" low_alarm_lmt, high_alarm_lmt," +
	    		" prec, unit FROM " + prefix + "num_metadata WHERE channel_id=?";
	    numeric_meta_insert = "INSERT INTO " + prefix + "num_metadata " +
	    		"(channel_id, low_disp_rng, high_disp_rng," +
                " low_warn_lmt, high_warn_lmt," +
                " low_alarm_lmt, high_alarm_lmt," +
                " prec, unit) VALUES (?,?,?,?,?,?,?,?,?)";
        numeric_meta_delete_by_channel = "DELETE FROM "
            + prefix + "num_metadata WHERE channel_id=?";
		
		// 'severity' table
        severity_table = prefix + "severity";
        severity_id_column = "severity_id";
        severity_name_column = "name";
		
		// 'status' table
	    status_table = prefix + "status";
        status_name_column = "name";
	    status_id_column = "status_id";

		// 'sample' table
		if (dialect == RDBUtil.Dialect.Oracle)
		{
			sample_sel_initial_by_id_time =
			"SELECT * FROM" +
			"  (SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val " +
			"   FROM " + prefix + sample + " WHERE channel_id=? AND smpl_time<=?" +
			"   ORDER BY smpl_time DESC)" +
			"  WHERE ROWNUM=1";
            sample_sel_by_id_start_end =
                "SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val FROM " + prefix + sample +
                "   WHERE channel_id=?" +
                "     AND smpl_time>? AND smpl_time<=?" +
                "   ORDER BY smpl_time";
            sample_sel_array_vals = "SELECT float_val FROM " + prefix + array_val +
            " WHERE channel_id=? AND smpl_time=? ORDER BY seq_nbr";
            sample_insert_double =
                "INSERT INTO " + prefix + sample +
                " (channel_id, smpl_time, severity_id, status_id, float_val)" +
                " VALUES (?,?,?,?,?)";
            sample_insert_double_array_element =
                "INSERT INTO " + prefix + array_val +
                " (channel_id, smpl_time, seq_nbr, float_val)" +
                " VALUES (?,?,?,?)";
            sample_insert_int =
                "INSERT INTO " + prefix + sample +
                " (channel_id, smpl_time, severity_id, status_id, num_val)" +
                " VALUES (?,?,?,?,?)";
            sample_insert_string =
                "INSERT INTO " + prefix + sample +
                " (channel_id, smpl_time, severity_id, status_id, str_val)" +
                " VALUES (?,?,?,?,?)";
		}
        else if (dialect == RDBUtil.Dialect.PostgreSQL)
        {
        	// Nanosecs are listed last to preserve the order of common columns
        	sample_sel_initial_by_id_time =
	        	"SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs" +
	        	"   FROM " + prefix + "sample WHERE channel_id=? AND smpl_time<=?" +
	        	"   ORDER BY smpl_time DESC, nanosecs DESC LIMIT 1";
        	sample_sel_by_id_start_end =
        		"SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs FROM " + prefix + "sample" +
	            "   WHERE channel_id=?" +
	            "     AND smpl_time>? AND smpl_time<=?" +
	            "   ORDER BY smpl_time, nanosecs";
            sample_sel_array_vals = "SELECT float_val FROM " + prefix + "array_val" +
            	" WHERE channel_id=? AND smpl_time=? AND nanosecs=? ORDER BY seq_nbr";
            sample_insert_double =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_double_array_element =
                "INSERT INTO " + prefix + "array_val " +
                "(channel_id, smpl_time, seq_nbr, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?)";
            sample_insert_int =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, num_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_string =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, str_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
        }
        else
		{
		    // Nanosecs are listed last to preserve the order of common columns
			sample_sel_initial_by_id_time =
				"SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs" +
				"   FROM " + prefix + "sample WHERE channel_id=? AND smpl_time<=?" +
				"   ORDER BY smpl_time DESC, nanosecs DESC LIMIT 1";
            sample_sel_by_id_start_end =
                "SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs FROM " + prefix + "sample" +
                "   WHERE channel_id=?" +
                "     AND smpl_time>? AND smpl_time<=?" +
                "   ORDER BY smpl_time, nanosecs";
            sample_sel_array_vals = "SELECT float_val FROM " + prefix + "array_val" +
            	" WHERE channel_id=? AND smpl_time=? AND nanosecs=? ORDER BY seq_nbr";
            sample_insert_double =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_double_array_element =
                "INSERT INTO " + prefix + "array_val " +
                "(channel_id, smpl_time, seq_nbr, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?)";
            sample_insert_int =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, num_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_string =
                "INSERT INTO " + prefix + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, str_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
		}
	}
}
