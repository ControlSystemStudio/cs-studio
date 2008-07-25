package org.csstudio.sns.jms2rdb.rdb;

import org.csstudio.platform.utility.rdb.RDBUtil;

/** SQL Statements
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SQL
{
    final public String select_log_message_type;

    final public String select_property_id_by_name;

    final public String select_next_property_id;

    final public String insert_property_id;
    
    final public String select_next_message_id;

    final public String insert_message_type_datum;

    final public String select_next_content_id;

    final public String insert_message_property_value;
    
    /** Construct SQL Statements for RDB
     *  @param rdb_util RDB/dialect to use
     *  @param schema Schema name or ""
     */
    public SQL(final RDBUtil rdb_util, final String schema)
    {
        select_log_message_type = "SELECT id FROM " + schema + "msg_type WHERE name='log'";
        
        select_property_id_by_name =
            "SELECT id FROM " + schema + "msg_property_type WHERE name=?";
        
        select_next_property_id =
            "SELECT MAX(id)+1 FROM " + schema + "msg_property_type";
        
        insert_property_id = "INSERT INTO " + schema + "msg_property_type " +
        		"(id, name) VALUES (?,?)";

        select_next_message_id = "SELECT MAX(id)+1 FROM " + schema + "message";
        
        insert_message_type_datum =
            "INSERT INTO " + schema + "message (id, msg_type_id, datum) VALUES (?,?,?)";

        select_next_content_id = "SELECT MAX(id)+1 FROM " + schema + "message_content";
        
        insert_message_property_value =
            "INSERT INTO " + schema + "message_content" +
            " (id, message_id, msg_property_type_id, value) VALUES(?,?,?,?)";
    }
}
