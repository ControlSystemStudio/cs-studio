/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb.rdb;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** SQL Statements
 *  @author Kay Kasemir
 *  @author Lana Abadie - PostgreSQL additions
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class SQL
{
    final public String select_property_id_by_name;

    final public String select_next_property_id;

    final public String insert_property_id;

    final public String select_next_message_id;

    final public String insert_message_id_datum_type_name_severity;

    final public String insert_message_property_value;

    /** Construct SQL Statements for RDB
     *  @param rdb_util RDB/dialect to use
     *  @param schema Schema name or ""
     */
    public SQL(final RDBUtil rdb_util, final String schema)
    {
        final String prefix = (schema != null  &&  schema.length() > 0) ? schema + "."  :  "";

		select_property_id_by_name =
            "SELECT id FROM " + prefix + "msg_property_type WHERE name=?";

        select_next_property_id =
            "SELECT MAX(id)+1 FROM " + prefix + "msg_property_type";

        insert_property_id = "INSERT INTO " + prefix + "msg_property_type " +
        		"(id, name) VALUES (?,?)";

        if (rdb_util.getDialect() == Dialect.Oracle)
        {   // Oracle uses sequence to get message.id.
            select_next_message_id = "SELECT " + prefix + "message_id_seq.NEXTVAL FROM DUAL";
            insert_message_id_datum_type_name_severity =
                "INSERT INTO " + prefix + "message (datum, type, name, severity, id) VALUES (?,?,?,?,?)";
        }
        else if (rdb_util.getDialect() == Dialect.PostgreSQL)
    	{	// PostgreSQL 'returns' the auto-generated ID
    	    select_next_message_id = null;
    	    insert_message_id_datum_type_name_severity =
    	        "INSERT INTO " + prefix + "message (datum, type, name, severity) VALUES (?,?,?,?) returning id";
    	}
    	else
    	{   // Other dialects (MySQL) use auto-increment ID column.
    		select_next_message_id = null;
    		insert_message_id_datum_type_name_severity =
    			"INSERT INTO " + prefix + "message (datum, type, name, severity) VALUES (?,?,?,?)";
    	}

        insert_message_property_value =
            "INSERT INTO " + prefix + "message_content" +
            " (message_id, msg_property_type_id, value) VALUES(?,?,?)";
    }
}
