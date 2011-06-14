/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** All the SQL strings in one place.
 *  @author Kay Kasemir
 *  @author Lana Abadie (PostgreSQL in original org.csstudio.archive.rdb code)
 */
@SuppressWarnings("nls")
public class SQL
{
    // 'smpl_eng' table
    final public String smpl_eng_sel_by_name;
	final public String smpl_eng_delete;
	final public String smpl_eng_insert;
	final public String smpl_eng_next_id;
    
    // 'chan_grp' table
    final public String chan_grp_sel_by_eng_id;
    final public String chan_grp_delete_by_engine_id;
	final public String chan_grp_insert;
	final public String chan_grp_next_id;

    // 'channel' table
    final public String channel_sel_by_group_id;
    final public String channel_sel_by_id;
	final public String channel_clear_grp_for_engine;

    // 'sample mode' table
    final public String sample_mode_sel;
    
    // 'sample' table
    final public String sel_last_sample_time_by_id;

    public SQL(final Dialect dialect, final String schema)
    {
	    // 'smpl_eng' table
        smpl_eng_sel_by_name = "SELECT eng_id, descr, url FROM " + schema + "smpl_eng WHERE name=?";
        smpl_eng_delete = "DELETE FROM " + schema + "smpl_eng WHERE eng_id=?";
        smpl_eng_insert = "INSERT INTO " + schema + "smpl_eng(eng_id, name, descr, url) VALUES (?,?,?,?)";
        smpl_eng_next_id = "SELECT MAX(eng_id) FROM " + schema + "smpl_eng";

        // 'chan_grp' table
        chan_grp_sel_by_eng_id = "SELECT grp_id, name, enabling_chan_id FROM " + schema + "chan_grp WHERE eng_id=? ORDER BY name";
        chan_grp_delete_by_engine_id = "DELETE FROM " + schema + "chan_grp WHERE eng_id=?";
        chan_grp_insert = "INSERT INTO " + schema + "chan_grp (grp_id, name, eng_id, enabling_chan_id) VALUES (?,?,?,null)";
        chan_grp_next_id = "SELECT MAX(grp_id) FROM " + schema + "chan_grp";

        // 'channel' table
        channel_sel_by_group_id = "SELECT channel_id, name, smpl_mode_id, smpl_val, smpl_per FROM " + schema + "channel WHERE grp_id=? ORDER BY name";
        channel_sel_by_id = "SELECT name FROM " + schema + "channel WHERE channel_id=?";
        channel_clear_grp_for_engine =
            "UPDATE " + schema + "channel SET grp_id=null WHERE grp_id IN " +
                "(SELECT grp_id FROM " + schema + "chan_grp WHERE eng_id=?)";
       
        // 'sample mode' table
        sample_mode_sel = "SELECT smpl_mode_id, name, descr FROM " + schema + "smpl_mode";

        // 'sample' table
		sel_last_sample_time_by_id = "SELECT MAX(smpl_time) FROM " + schema + "sample WHERE channel_id=?";
    }
}
