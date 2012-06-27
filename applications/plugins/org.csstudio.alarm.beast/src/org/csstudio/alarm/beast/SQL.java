/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** SQL Helper for alarm configuration and status info in RDB.
 *  @author Kay Kasemir
 *  @author Xihui Chen
 *  @author Lana Abadie (PostgreSQL)
 */
@SuppressWarnings("nls")
public class SQL
{
    /** Schema prefix. Required for SNS Oracle. Set to "" for MySQL */
    final public String schema_prefix;

    final public String sel_configurations;

	final public String sel_configuration_by_name;
	final public String sel_guidance_by_id;
	final public String sel_displays_by_id;
	final public String sel_commands_by_id;
	final public String sel_auto_actions_by_id;
    final public String sel_items_by_parent;
    final public String sel_item_by_parent_and_name;
    final public String sel_last_item_id;
    final public String insert_item;

	final public String delete_guidance_by_id;
	final public String insert_guidance;
	final public String delete_displays_by_id;
	final public String insert_display;
	final public String delete_commands_by_id;
	final public String insert_command;
	final public String delete_auto_actions_by_id;
	final public String insert_auto_action;

	final public String update_item_config_time;

    final public String delete_component_by_id;

    final public String sel_pv_by_id;
    final public String sel_global_alarm_pvs;
    final public String sel_item_by_id;
    final public String insert_pv;

    final public String update_pv_config;
    final public String update_pv_state;
    final public String update_global_state;
    final public String update_pv_enablement;
    final public String delete_pv_by_id;
    final public String rename_item;
    final public String move_item;


    final public String sel_severity;
    final public String sel_last_severity;
    final public String insert_severity;

    final public String severity_table = "SEVERITY";
    final public String severity_id_col = "SEVERITY_ID";
    final public String severity_name_col = "NAME";

    final public String message_table = "STATUS";
    final public String message_id_col = "STATUS_ID";
    final public String message_name_col = "NAME";


	/** Initialize
     *  @param rdb RDBUtil
	 *  @param schema 
     */
    public SQL(final RDBUtil rdb, final String schema) throws Exception
    {
        if (rdb.getDialect() == Dialect.MySQL || rdb.getDialect() == Dialect.PostgreSQL)
            schema_prefix = "";
        else if (rdb.getDialect() == Dialect.Oracle)
            schema_prefix = schema + ".";
        else
        	throw new Exception("This database is not supported");

        sel_configurations = "SELECT NAME FROM "+ schema_prefix + "ALARM_TREE WHERE PARENT_CMPNT_ID IS NULL";

        sel_configuration_by_name = "SELECT COMPONENT_ID FROM " + schema_prefix + "ALARM_TREE WHERE NAME=? AND PARENT_CMPNT_ID IS NULL";
        sel_guidance_by_id =
            "select TITLE, DETAIL FROM " + schema_prefix + "GUIDANCE WHERE COMPONENT_ID=? ORDER BY GUIDANCE_ORDER";
        sel_displays_by_id =
            "select TITLE, DETAIL FROM " + schema_prefix + "DISPLAY WHERE COMPONENT_ID=? ORDER BY DISPLAY_ORDER";
        sel_commands_by_id =
            "select TITLE, DETAIL FROM " + schema_prefix + "COMMAND WHERE COMPONENT_ID=? ORDER BY COMMAND_ORDER";
        sel_auto_actions_by_id =
                "select TITLE, DETAIL, DELAY FROM " + schema_prefix + "AUTOMATED_ACTION WHERE COMPONENT_ID=? ORDER BY AUTO_ACTION_ORDER";
        // Selects components or PVs by parent ID. For PVs, all the p.* columns are null.
        //
        // Components are ordered by ID, assuming they are originally
        // added in some divine order.
        //
        // Except for the additional 't.NAME', the columns must match
        // sel_item_by_parent_and_name!
        sel_items_by_parent =
            //        1               2
            "SELECT t.COMPONENT_ID, t.CONFIG_TIME," +
            //  3               4        5              6
            " p.COMPONENT_ID, p.DESCR, p.ENABLED_IND, p.ANNUNCIATE_IND," +
            //  7            8        9              10        11
            " p.LATCH_IND, p.DELAY, p.DELAY_COUNT, p.FILTER, p.CUR_SEVERITY_ID," +
            //  12               13             14           15          16
            " p.CUR_STATUS_ID, p.SEVERITY_ID, p.STATUS_ID, p.PV_VALUE, p.ALARM_TIME," +
            //  17
            " t.NAME " +
            " FROM " + schema_prefix + "ALARM_TREE t" +
            " LEFT JOIN " + schema_prefix + "PV p ON p.COMPONENT_ID = t.COMPONENT_ID" +
            " WHERE t.PARENT_CMPNT_ID=? ORDER BY t.COMPONENT_ID";

        // Selects component or PV by parent ID and name. For PV, all the p.* columns are null.
        // Columns must match sel_items_by_parent except for the t.NAME that's not in here!
        sel_item_by_parent_and_name =
            //        1               2
            "SELECT t.COMPONENT_ID, t.CONFIG_TIME," +
            //  3               4        5              6
            " p.COMPONENT_ID, p.DESCR, p.ENABLED_IND, p.ANNUNCIATE_IND," +
            //  7            8        9              10        11
            " p.LATCH_IND, p.DELAY, p.DELAY_COUNT, p.FILTER, p.CUR_SEVERITY_ID," +
            //  12               13             14           15          16
            " p.CUR_STATUS_ID, p.SEVERITY_ID, p.STATUS_ID, p.PV_VALUE, p.ALARM_TIME" +
            " FROM " + schema_prefix + "ALARM_TREE t" +
            " LEFT JOIN " + schema_prefix + "PV p ON p.COMPONENT_ID = t.COMPONENT_ID" +
            " WHERE t.PARENT_CMPNT_ID=? AND t.NAME=?";

        sel_last_item_id =
            "SELECT MAX(COMPONENT_ID) FROM " + schema_prefix + "ALARM_TREE";

        // Database might not provide a default config time, so include
        // that in the INSERT statement
        final String now = rdb.getDialect() == Dialect.Oracle
                         ? "SYSDATE" : "NOW()";
        insert_item =
            "INSERT INTO " + schema_prefix +
            "ALARM_TREE(COMPONENT_ID, PARENT_CMPNT_ID, NAME, CONFIG_TIME)" +
            " VALUES (?,?,?," + now + ")";

        delete_guidance_by_id =
            "DELETE FROM " + schema_prefix + "GUIDANCE WHERE COMPONENT_ID=?";
        insert_guidance =
            "INSERT INTO " + schema_prefix + "GUIDANCE(COMPONENT_ID, GUIDANCE_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
        delete_displays_by_id =
            "DELETE FROM " + schema_prefix + "DISPLAY WHERE COMPONENT_ID=?";
        insert_display =
            "INSERT INTO " + schema_prefix + "DISPLAY(COMPONENT_ID, DISPLAY_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
        delete_commands_by_id =
            "DELETE FROM " + schema_prefix + "COMMAND WHERE COMPONENT_ID=?";
        insert_command =
            "INSERT INTO " + schema_prefix + "COMMAND(COMPONENT_ID, COMMAND_ORDER, TITLE, DETAIL) VALUES(?,?,?,?)";
        delete_auto_actions_by_id =
                "DELETE FROM " + schema_prefix + "AUTOMATED_ACTION WHERE COMPONENT_ID=?";
        insert_auto_action =
                "INSERT INTO " + schema_prefix + "AUTOMATED_ACTION(COMPONENT_ID, AUTO_ACTION_ORDER, TITLE, DETAIL, DELAY) VALUES(?,?,?,?,?)";
        
        update_item_config_time =
            "UPDATE " + schema_prefix + "ALARM_TREE SET CONFIG_TIME=? WHERE COMPONENT_ID=?";

        delete_component_by_id = "DELETE FROM " + schema_prefix + "ALARM_TREE WHERE COMPONENT_ID = ?";

        sel_pv_by_id =
            "SELECT DESCR, ENABLED_IND, ANNUNCIATE_IND, LATCH_IND, DELAY, DELAY_COUNT, FILTER FROM " + schema_prefix + "PV WHERE COMPONENT_ID=?";

        sel_global_alarm_pvs =
            //        1                  2
            "SELECT t.PARENT_CMPNT_ID, t.NAME," +
            //       3                4          5           6
            " s.NAME SEVERITY, m.NAME STATUS, p.PV_VALUE, p.ALARM_TIME" +
            " FROM " + schema_prefix + "PV p" +
            " JOIN " + schema_prefix + "ALARM_TREE t on t.COMPONENT_ID=p.COMPONENT_ID" +
            " JOIN " + schema_prefix + "SEVERITY s on s.SEVERITY_ID=p.SEVERITY_ID" +
            " JOIN " + schema_prefix + "STATUS m on m.STATUS_ID=p.STATUS_ID" +
            " WHERE ACT_GLOBAL_ALARM_IND=?";

        sel_item_by_id = "SELECT PARENT_CMPNT_ID, NAME FROM " + schema_prefix + "ALARM_TREE WHERE COMPONENT_ID=?";

        if (rdb.getDialect() == Dialect.PostgreSQL)
        	insert_pv =
            "INSERT INTO " + schema_prefix + "PV(COMPONENT_ID, DESCR, ANNUNCIATE_IND, LATCH_IND,ENABLED_IND) VALUES (?,?,?,?,true)";
        else
        	insert_pv =
            "INSERT INTO " + schema_prefix + "PV(COMPONENT_ID, DESCR, ANNUNCIATE_IND, LATCH_IND,ENABLED_IND) VALUES (?,?,?,?,1)";

        update_pv_config =
            "UPDATE " + schema_prefix + "PV SET DESCR=?,ENABLED_IND=?,ANNUNCIATE_IND=?,LATCH_IND=?, DELAY=?,DELAY_COUNT=?,FILTER=? WHERE COMPONENT_ID=?";
        update_pv_state =
            "UPDATE " + schema_prefix + "PV SET CUR_SEVERITY_ID=?,CUR_STATUS_ID=?,SEVERITY_ID=?,STATUS_ID=?,PV_VALUE=?,ALARM_TIME=?  WHERE COMPONENT_ID=?";
        update_global_state =
            "UPDATE " + schema_prefix + "PV SET ACT_GLOBAL_ALARM_IND=? WHERE COMPONENT_ID=?";
        update_pv_enablement =
            "UPDATE " + schema_prefix + "PV SET ENABLED_IND=?  WHERE COMPONENT_ID=?";
        delete_pv_by_id = "DELETE FROM " + schema_prefix + "PV WHERE COMPONENT_ID = ?";
        rename_item = "UPDATE " + schema_prefix + "ALARM_TREE SET NAME=? WHERE COMPONENT_ID=?";
        move_item = "UPDATE " + schema_prefix + "ALARM_TREE SET PARENT_CMPNT_ID=? WHERE COMPONENT_ID=?";

        sel_severity =
            "SELECT SEVERITY_ID FROM " + schema_prefix + "SEVERITY WHERE NAME=?";
        sel_last_severity =
            "SELECT MAX(SEVERITY_ID) FROM " + schema_prefix + "SEVERITY";
        insert_severity =
            "INSERT INTO " + schema_prefix + "SEVERITY(SEVERITY_ID, NAME) VALUES (?,?)";
    }
}
