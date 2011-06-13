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
    
    // 'chan_grp' table
    final public String chan_grp_sel_by_eng_id;

    // 'channel' table
    final public String channel_sel_by_group_id;
    final public String channel_sel_by_id;

    // 'sample mode' table
    final public String sample_mode_sel;
    
    // 'sample' table
    final public String sel_last_sample_time_by_id;

    public SQL(final Dialect dialect, final String schema)
    {
	    // 'smpl_eng' table
        smpl_eng_sel_by_name = "SELECT eng_id, descr, url FROM " + schema + "smpl_eng WHERE name=?";

        // 'chan_grp' table
        chan_grp_sel_by_eng_id = "SELECT grp_id, name, enabling_chan_id FROM " + schema + "chan_grp WHERE eng_id=? ORDER BY name";

        // 'channel' table
        channel_sel_by_group_id = "SELECT channel_id, name, smpl_mode_id, smpl_val, smpl_per FROM " + schema + "channel WHERE grp_id=? ORDER BY name";
        channel_sel_by_id = "SELECT name FROM " + schema + "channel WHERE channel_id=?";
        
        // 'sample mode' table
        sample_mode_sel = "SELECT smpl_mode_id, name, descr FROM " + schema + "smpl_mode";

        // 'sample' table
		sel_last_sample_time_by_id = "SELECT MAX(smpl_time) FROM " + schema + "sample WHERE channel_id=?";
    }
}
