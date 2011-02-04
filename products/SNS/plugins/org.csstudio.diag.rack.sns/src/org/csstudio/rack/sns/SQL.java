/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.rack.sns;


public class SQL {
	final public String readRacksFromRDB_Select;
	final public String readParentFromRDB_Select;
	final public String readRackDvcListFromRDB_Select;

	public SQL()
	{
			readRacksFromRDB_Select = "select distinct a.dvc_id, b.sys_seq_nbr from epics.dvc a, epics.syst b "
			 + "where a.sys_id = b.sys_id and a.dvc_type_id = 'Cab' "
			 + "and a.dvc_id like ? "
			 + "order by b.sys_seq_nbr, a.dvc_id";
			
			readParentFromRDB_Select = "select distinct parent_dvc_id from epics.dvc "
			+ "where dvc_id = ? ";
			
			readRackDvcListFromRDB_Select = "SELECT DISTINCT a.dvc_id, a.dsgn_cab_slot_bgn bgn, a.dsgn_cab_slot_end end, a. dvc_type_id, b.bl_dvc_ind "
	        + "FROM epics.rack_dvc_v a, epics.dvc b "
	       	+ "WHERE 1=1 "
	       	+ "AND a.dvc_id = b.dvc_id(+) "
	       	+ "AND a.parent_dvc_id = ? "
	       	+ "AND a.dsgn_cab_slot_pos_ind = ? "
			+ "AND a.dsgn_cab_slot_bgn is not null AND a.dsgn_cab_slot_end is not null "
			+ "GROUP BY a.dvc_id, a.dsgn_cab_slot_bgn, a.dsgn_cab_slot_end, a. dvc_type_id, b.bl_dvc_ind "
			+ "ORDER BY a.dsgn_cab_slot_bgn desc ";
	}
	
}
