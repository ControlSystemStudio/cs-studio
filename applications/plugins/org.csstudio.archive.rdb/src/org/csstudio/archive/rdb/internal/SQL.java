package org.csstudio.archive.rdb.internal;

import org.csstudio.utility.rdb.RDBUtil;
import org.csstudio.utility.rdb.RDBUtil.Dialect;

/** All the SQL strings in one place.
 *  @author Kay Kasemir
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
	final public String channel_sel_timerange_by_id;
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
	
	/** Create SQL strings for the given dialect */
	public SQL(final Dialect dialect)
	{
		// Schema prefix for the tables
		String schema;
		if (dialect == RDBUtil.Dialect.Oracle)
			schema = "chan_arch.";
		else
			schema = "";
		
	    // 'smpl_eng' table
        smpl_eng_next_id = "SELECT MAX(eng_id) FROM " + schema + "smpl_eng";
        smpl_eng_insert = "INSERT INTO " + schema + "smpl_eng(eng_id, name, descr, url) VALUES (?,?,?,?)";
        smpl_eng_sel_by_name = "SELECT eng_id, descr, url FROM " + schema + "smpl_eng WHERE name=?";
        smpl_eng_sel_by_id = "SELECT name, descr, url FROM " + schema + "smpl_eng WHERE eng_id=?";
        smpl_eng_delete = "DELETE FROM " + schema + "smpl_eng WHERE eng_id=?";

        // 'chan_grp' table
        chan_grp_delete_by_engine_id = "DELETE FROM " + schema + "chan_grp WHERE eng_id=?";
        chan_grp_next_id = "SELECT MAX(grp_id) FROM " + schema + "chan_grp";
        chan_grp_sel_by_name_and_eng_id = "SELECT grp_id, enabling_chan_id, retent_id FROM " + schema + "chan_grp WHERE name=? AND eng_id=?";
        chan_grp_sel_by_id = "SELECT name, eng_id, enabling_chan_id, retent_id FROM " + schema + "chan_grp WHERE grp_id=?";
        chan_grp_insert = "INSERT INTO " + schema + "chan_grp (grp_id, name, eng_id, enabling_chan_id, retent_id) VALUES (?,?,?,?,?)";
        chan_grp_sel_by_eng_id = "SELECT grp_id, name, enabling_chan_id, retent_id FROM " + schema + "chan_grp WHERE eng_id=?";
        
        chan_grp_set_enable_channel = "UPDATE " + schema + "chan_grp SET enabling_chan_id=? WHERE grp_id=?";
        
        // 'retent' table
        retention_table = "retent";
        retention_id_column = "retent_id";
        retention_name_column = "descr";
        
		// 'channel' table
//        channel_table = schema + "channel";
//		channel_id_colunm = "channel_id";
//	    channel_name_column = "name";
        
        channel_insert = "INSERT INTO " + schema + "channel (channel_id, name, smpl_mode_id, smpl_per) VALUES (?,?,?,?)";
        
        channel_sel_next_id = "SELECT MAX(channel_id) FROM " + schema + "channel";
        
        channel_sel_by_name = 
            "SELECT channel_id, grp_id, smpl_mode_id, smpl_per FROM " + schema + "channel WHERE name=?";
		
		if (dialect == RDBUtil.Dialect.Oracle)
			channel_sel_by_pattern = "SELECT channel_id, name, grp_id, smpl_mode_id, smpl_per FROM " + schema + "channel WHERE REGEXP_LIKE(name, ?, 'i')";
		else
			channel_sel_by_pattern = "SELECT channel_id, name, grp_id, smpl_mode_id, smpl_per FROM " + schema + "channel WHERE name REGEXP ?";
		channel_sel_timerange_by_id = "SELECT MIN(smpl_time), MAX(smpl_time) FROM " + schema + "sample WHERE channel_id=?";
		channel_sel_last_time_by_id = "SELECT MAX(smpl_time) FROM " + schema + "sample WHERE channel_id=?";
		channel_sel_by_group_id = "SELECT channel_id, name, smpl_mode_id, smpl_per FROM " + schema + "channel WHERE grp_id=?";
		channel_set_grp_by_id = "UPDATE channel SET grp_id=? WHERE channel_id=?";        
        channel_clear_grp_for_engine =
            "UPDATE " + schema + "channel SET grp_id=null WHERE grp_id IN " +
                "(SELECT grp_id FROM " + schema + "chan_grp WHERE eng_id=?)";
        channel_set_sampling = "UPDATE " + schema + "channel SET smpl_mode_id=?,smpl_per=? WHERE channel_id=?";
        
        // 'sample mode' table
        sample_mode_sel = "SELECT smpl_mode_id, name, descr FROM " + schema + "smpl_mode";

		// 'enum_metadata' table
	    enum_sel_num_val_by_channel = "SELECT enum_nbr, enum_val FROM "
	        + schema + "enum_metadata WHERE channel_id=? ORDER BY enum_nbr";
	    enum_delete_by_channel = "DELETE FROM " + schema + "enum_metadata WHERE channel_id=?";
	    enum_insert_channel_num_val = "INSERT INTO " + schema
	        + "enum_metadata(channel_id,enum_nbr, enum_val) VALUES(?,?,?)";
	    
	    // 'num_metadata' table
	    numeric_meta_sel_by_channel = "SELECT low_disp_rng, high_disp_rng," +
	    		" low_warn_lmt, high_warn_lmt," +
	    		" low_alarm_lmt, high_alarm_lmt," +
	    		" prec, unit FROM " + schema + "num_metadata WHERE channel_id=?";
	    numeric_meta_insert = "INSERT INTO " + schema + "num_metadata " +
	    		"(channel_id, low_disp_rng, high_disp_rng," +
                " low_warn_lmt, high_warn_lmt," +
                " low_alarm_lmt, high_alarm_lmt," +
                " prec, unit) VALUES (?,?,?,?,?,?,?,?,?)";
        numeric_meta_delete_by_channel = "DELETE FROM "
            + schema + "num_metadata WHERE channel_id=?";
		
		// 'severity' table
        severity_table = schema + "severity";
        severity_id_column = "severity_id";
        severity_name_column = "name";
		
		// 'status' table
	    status_table = schema + "status";
        status_name_column = "name";
	    status_id_column = "status_id";

		// 'sample' table
		if (dialect == RDBUtil.Dialect.Oracle)
		{
			sample_sel_initial_by_id_time =
			"SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, ROWNUM FROM" +
			"  (SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val " +
			"   FROM " + schema + "sample WHERE channel_id=? AND smpl_time<=?" +
			"   ORDER BY smpl_time DESC)" +
			"  WHERE ROWNUM=1";
            sample_sel_by_id_start_end =
                "SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val FROM " + schema + "sample" +
                "   WHERE channel_id=?" +
                "     AND smpl_time>? AND smpl_time<=?" +
                "   ORDER BY smpl_time";
            sample_sel_array_vals = "SELECT float_val FROM " + schema + "array_val" +
            " WHERE channel_id=? AND smpl_time=? ORDER BY seq_nbr";
            sample_insert_double =
                "INSERT /*+APPEND */ INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, float_val)" +
                "VALUES (?,?,?,?,?)";
            sample_insert_double_array_element =
                "INSERT /*+APPEND */ INTO " + schema + "array_val " +
                "(channel_id, smpl_time, seq_nbr, float_val)" +
                "VALUES (?,?,?,?)";
            sample_insert_int =
                "INSERT /*+APPEND */ INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, num_val)" +
                "VALUES (?,?,?,?,?)";
            sample_insert_string =
                "INSERT /*+APPEND */ INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, str_val)" +
                "VALUES (?,?,?,?,?)";
		}
		else
		{
		    // The MySQL-only nanosecs are listed last to preserve the order
		    // of common columns
			sample_sel_initial_by_id_time =
			"SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs" +
			"   FROM " + schema + "sample WHERE channel_id=? AND smpl_time<=?" +
			"   ORDER BY smpl_time DESC, nanosecs DESC LIMIT 1";
            sample_sel_by_id_start_end =
                "SELECT smpl_time, severity_id, status_id, num_val, float_val, str_val, nanosecs FROM " + schema + "sample" +
                "   WHERE channel_id=?" +
                "     AND smpl_time>? AND smpl_time<=?" +
                "   ORDER BY smpl_time, nanosecs";
            sample_sel_array_vals = "SELECT float_val FROM " + schema + "array_val" +
            " WHERE channel_id=? AND smpl_time=? AND nanosecs=? ORDER BY seq_nbr";
            sample_insert_double =
                "INSERT INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_double_array_element =
                "INSERT INTO " + schema + "array_val " +
                "(channel_id, smpl_time, seq_nbr, float_val, nanosecs)" +
                "VALUES (?,?,?,?,?)";
            sample_insert_int =
                "INSERT INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, num_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
            sample_insert_string =
                "INSERT INTO " + schema + "sample " +
                "(channel_id, smpl_time, severity_id, status_id, str_val, nanosecs)" +
                "VALUES (?,?,?,?,?,?)";
		}
	}
}
