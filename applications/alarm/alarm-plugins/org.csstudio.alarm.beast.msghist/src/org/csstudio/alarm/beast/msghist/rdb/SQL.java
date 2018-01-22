/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.rdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** SQL Statements.
 *  <p>
 *  Centralizes all SQL statements, and allows construction of
 *  statements that may be specific to an RDB Dialect.
 *
 *  @author Kay Kasemir
 *  @author Jan Hatje created the initial SQL in
 *          org.csstudio.alarm.dbaccess.SQLStatements
 *  @author Lana Abadie added Postgresql
 *  @author Borut Terpinc
 */
@SuppressWarnings("nls")
public class SQL
{
    /** RDB schema */
    final String schema;

    /** Array of property names which are handled as MESSAGE columns. */
    final private String message_properties[];

    /** Mapping of message property IDs to Names
     *  for properties that are MESSAGE_CONTENT rows.
     */
    final private HashMap<Integer, String> content_properties_by_id
        = new HashMap<Integer, String>();

    /** Mapping of message property Names to IDs
     *  for properties that are MESSAGE_CONTENT rows.
     */
    final private HashMap<String, Integer> content_properties_by_name
        = new HashMap<String, Integer>();

    /** Construct SQL Statements for RDB
     *  @param rdb_util RDB/dialect to use
     *  @param schema Database schema ending in "." or "" if not used
     */
    public SQL(final RDBUtil rdb_util, final String schema) throws Exception
    {
        this.schema = schema;
        message_properties = determineMessageProperties(rdb_util);
        readPropertyTypes(rdb_util);
    }

    /** @return Prefix to table name.
     *  @see #schema
     */
    private String getSchemaPrefix()
    {
        return (schema.length() > 0) ? schema + "." : "";
    }

    /** Determine which extra columns are found in the MESSAGE table
     *  beyond ID and DATUM.
     *  @param rdb_util RDB Utility
     *  @return Array of properties kept MESSAGE table
     */
    private String[] determineMessageProperties(final RDBUtil rdb_util)
            throws Exception
    {
        final ArrayList<String> properties = new ArrayList<String>();
        final Connection connection = rdb_util.getConnection();
        final DatabaseMetaData meta = connection.getMetaData();

        // Catalog seems to be null
        final String catalog = connection.getCatalog();
        // Oracle uses upper-case table name
        String table = "message";
        if (rdb_util.getDialect() == Dialect.Oracle)
            table = table.toUpperCase();
        ResultSet columns = meta.getColumns(catalog, schema, table, null);
        while (columns.next())
        {   // We treat all columns in upper case.
            final String column = columns.getString(4).toUpperCase();
            if ("ID".equals(column) ||
                "DATUM".equals(column))
                continue;
            properties.add(column);
        }
        final String result[] = new String[properties.size()];
        return properties.toArray(result);
    }

    /** Get all properties that are kept in MESSAGE_CONTENT
     *  @param rdb_util RDB Utility
     *  @return Map of Property ID/Name
     */
    private void readPropertyTypes(final RDBUtil rdb_util) throws Exception
    {
        final String select_property_types =
            "SELECT id, name FROM " + getSchemaPrefix() + "msg_property_type";
        final Statement statement = rdb_util.getConnection().createStatement();
        try
        {
            final ResultSet result =
                statement.executeQuery(select_property_types);
            while (result.next())
            {
                final Integer id = result.getInt(1);
                final String name = result.getString(2);
                content_properties_by_id.put(id, name);
                content_properties_by_name.put(name, id);
            }
        }
        finally
        {
            statement.close();
        }
    }

    /** Check if a property is in the MESSAGE table or MESSAGE_CONTENT.
     *  @param property Property name to check
     *  @return <code>true</code> if property is column in MESSAGE table
     */
    private boolean isMessageProperty(final String property)
    {
        for (String msg_prop : message_properties)
            if (msg_prop.equalsIgnoreCase(property))
                return true;
        return false;
    }

    /** @return Number of properties held in the MESSAGE table */
    public int messagePropertyCount()
    {
        return message_properties.length;
    }

    /** Get name of one of the properties held in the MESSAGE table
     *  @param i Index 0 ... (messagePropertyCount() - 1)
     *  @return Name of MESSAGE property
     */
    public String getMessageProperty(final int i)
    {
        return message_properties[i];
    }

    /** For properties in MESSAGE_CONTENT, obtain their name by ID
     *  @param id RDB ID for the property
     *  @return Name of the property or <code>null</code>
     */
    public String getPropertyNameById(int id)
    {
        return content_properties_by_id.get(Integer.valueOf(id));
    }

    /** For properties in MESSAGE_CONTENT, obtain their ID by name
     *  @param property Name of the property
     *  @return RDB ID for the property
     *  @throws Exception when property has no known ID.
     */
    public int getPropertyIdByName(final String property) throws Exception
    {
        final Integer id = content_properties_by_name.get(property);
        if (id == null)
            throw new Exception("Unknown message property " + property);
        return id.intValue();
    }

    /** Create "SELECT ... " which requires parameters
     *  <ol>
     *  <li>Start time
     *  <li>End time
     *  <li>Value pattern for property filter 1
     *  <li>Value pattern for property filter 2
     *  <li>...
     *  <li value=99>overall property count limit
     *  </ol>
     *  and returns the message
     *  <ol>
     *  <li>ID
     *  <li>Datum
     *  <li>First MESSAGE table property
     *  <li>Second MESSAGE table property
     *  <li>...
     *  <li value=98>MESSAGE_CONTENT property ID
     *  <li>MESSAGE_CONTENT property value
     *  </ol>
     *  @param rdb_util RDBUtil
     *  @param schema Database schema ending in "." or "" if not used
     *  @param filters Filters to use (not <code>null</code>)
     *  @return SQL string
     */
    String createSelect(final RDBUtil rdb_util,
            final MessagePropertyFilter filters[]) throws Exception
    {
        final StringBuffer sel = new StringBuffer();
        sel.append("SELECT");
        // .. all columns from MESSAGE
        sel.append(" m.id, m.datum,");
        for (String msg_prop : message_properties)
            sel.append(" m." + msg_prop + ",");
        // .. and the properties from MESSAGE_CONTENT
        sel.append(" c.msg_property_type_id p, c.value");
        sel.append(" FROM (SELECT * FROM " + getSchemaPrefix() + "message msg");
        // Set time range
        sel.append(" WHERE msg.datum BETWEEN ? AND ?");
        // Some filters may be MESSAGE columns, rest is MESSAGE_CONTENT
        for (MessagePropertyFilter filter : filters)
        {
            if (isMessageProperty(filter.getProperty()))
            {   // Filter property is actually column of MESSAGE table
                sel.append(" AND msg." + filter.getProperty() + " LIKE ?");
            }
            else
            {   // Create MESSAGE_CONTENT sub-query for this property/value
                final int id = getPropertyIdByName(filter.getProperty());
                sel.append(" AND msg.id IN (");
                sel.append(" SELECT message_id");
                sel.append(" FROM " + getSchemaPrefix() + "message_content");
                sel.append(" WHERE msg_property_type_id=" + id
                           + " AND value LIKE ?)");
            }
        }

        // Oracle limits result count via ROWNUM check within WHERE clause...
        if (rdb_util.getDialect() == Dialect.Oracle)
            sel.append(" AND ROWNUM < ?");
        sel.append(" ORDER BY msg.id DESC");
        // MySQL uses designated LIMIT statement instead.
        if (rdb_util.getDialect() == Dialect.MySQL || rdb_util.getDialect() == Dialect.PostgreSQL)
            sel.append(" LIMIT ?");
        sel.append(") m, " + getSchemaPrefix() + "message_content c");

        // Join MESSAGE and ..CONTENT
        sel.append(" WHERE m.id=c.message_id");

       return sel.toString();
    }

    /** Idea: Use the above to ONLY select from MESSAGE table, then
     *  use separate query to select remaining properties for a message
     *  SELECT msg_property_type_id, value
     *  FROM message_content
     *  WHERE message_id=?
     */
}
