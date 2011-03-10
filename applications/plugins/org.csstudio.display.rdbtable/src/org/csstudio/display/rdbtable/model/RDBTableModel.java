/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Model of RDB Table
 * 
 *  Reads configuration file that defines table columns and SQL,
 *  reads table from RDB, allows editing, can write changes back to RDB.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBTableModel
{
    final ArrayList<RDBTableModelListener> listeners = new ArrayList<RDBTableModelListener>();
    
    /** Document title */
    private String title;

    /** RDB URL, user, password */
    private String url, user, password;

    /** Column info
     *  Array length also defines the number of columns
     */
    private ColumnInfo[] columns;
    
    /** SQL statements */
    private String sql_select, sql_insert, sql_update, sql_delete;

    /** RDB connection */
    private RDBUtil rdb;

    /** Has the model data been modified? */
    private boolean was_modified = false;
    
    /** The rows in the table */
    final private ArrayList<RDBTableRow> rows = new ArrayList<RDBTableRow>();

    
    /** Initialize RDBTable from file
     *  @param filename Name of XML file
     *  @throws Exception on error in XML file or RDB readout
     */
    public RDBTableModel(final String filename) throws Exception
    {
        this(new FileInputStream(filename));
    }

    /** Initialize RDBTable from InputStream
     *  @param stream Stream for XML file
     *  @throws Exception on error in XML file or RDB readout
     */
    public RDBTableModel(final InputStream stream) throws Exception
    {
        readConfiguration(stream);
    }

    /** Read configuration
     *  @param stream Stream for XML file
     *  @throws Exception on error in XML file
     */
    private void readConfiguration(final InputStream stream)
            throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);
    
        // Check root element
        final Element root_node = doc.getDocumentElement();
        root_node.normalize();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals("rdbtable"))
            throw new Exception("Got " + root_name + " instead of 'rdbtable'");
        
        // Read basic info
        title = DOMHelper.getSubelementString(root_node, "title");
        url = DOMHelper.getSubelementString(root_node, "url");
        user = DOMHelper.getSubelementString(root_node, "user");
        password = DOMHelper.getSubelementString(root_node, "password");
        
        // Read column definitions: Locate list of columns
        final Element cols_node =
            DOMHelper.findFirstElementNode(root_node.getFirstChild(), "columns");
        if (cols_node == null)
            throw new Exception("No 'columns' definition");
        
        final ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
        Element col_node = DOMHelper.findFirstElementNode(cols_node.getFirstChild(), "column");
        while (col_node != null)
        {
            final String header = col_node.getFirstChild().getNodeValue();
            final int width = getWidth(col_node);
            columns.add(new ColumnInfo(header, width));
            col_node = DOMHelper.findNextElementNode(col_node, "column");
        }
        this.columns = (ColumnInfo[]) columns.toArray(new ColumnInfo[columns.size()]);
    
        // Read SQL
        final Element sql_node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), "sql");
        if (sql_node == null)
            throw new Exception("No 'sql' definition");
        sql_select = DOMHelper.getSubelementString(sql_node, "select");
        sql_insert = DOMHelper.getSubelementString(sql_node, "insert");
        sql_update = DOMHelper.getSubelementString(sql_node, "update");
        sql_delete = DOMHelper.getSubelementString(sql_node, "delete");
    }

    /** Get "widths" from column node
     *  @param col_node DOM element for the "column"
     *  @return widths extracted from the node or 100 for default
     * @throws Exception on error in the 'width'
     */
    private int getWidth(final Element col_node) throws Exception
    {
        String text = col_node.getAttribute("width");
        if (text.length() <= 0)
            return 100;
        if (text.endsWith("%"))
            text = text.substring(0, text.length()-1);
        try
        {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException ex)
        {
            throw new Exception("Cannot parse 'widths' from " + text);
        }
    }

    /** @return <code>true</code> if no password was provided in the configuration */
    public boolean needPassword()
    {
        return password.length() <= 0;
    }
    
    /** @return User name provided in configuration file. May be "" */
    public String getUser()
    {
        return user;
    }

    /** Read table data from RDB with user/password, replacing what
     *  was found in the configuration file
     *  @param user RDB user to use
     *  @param password RDB password to use
     *  @throws Exception on error
     */
    public void read(final String user, final String password) throws Exception
    {
        this.user = user;
        this.password = password;
        read();
    }
    
    /** Read table data from RDB with user, password from configuration file
     *  or last read(user, password) call.
     *  @throws Exception on error
     */
    public void read() throws Exception
    {
        rows.clear();
        rdb = RDBUtil.connect(url, user, password, true);
        final PreparedStatement statement =
            rdb.getConnection().prepareStatement(sql_select);
        try
        {
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                final String column_data[] = new String[getColumnCount()];
                for (int c=0; c<column_data.length; ++c)
                    column_data[c] = result.getString(c+1);
                rows.add(new RDBTableRow(this, column_data, true));
            }
            was_modified = false;
        }
        finally
        {
            statement.close();
        }
    }

    /** @param listener Listener to add */
    public void addListener(final RDBTableModelListener listener)
    {
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final RDBTableModelListener listener)
    {
        listeners.remove(listener);
    }
    
    /** @return Document title */
    public String getTitle()
    {
        return title;
    }

    /** @return Number of table columns */
    public int getColumnCount()
    {
        return columns.length;
    }

    /** @param column Column index
     *  @return Name of header
     */
    public String getHeader(int column)
    {
        return columns[column].getHeader();
    }

    /** @param column Column index
     *  @return Widths of column in percent
     */
    public int getWidth(int column)
    {
        return columns[column].getWidth();
    }

    /** @return <code>true</code> when table was edited after
     *          reading original RDB data
     */
    public boolean wasModified()
    {
        return was_modified;
    }

    /** @return Number of table rows */
    public int getRowCount()
    {
        return rows.size();
    }

    /** Add a row to the table
     *  @param column_data Data for the columns
     */
    public void addRow(final String column_data[])
    {
        final RDBTableRow row = new RDBTableRow(this, column_data, false);
        rows.add(row);
        fireNewRow(row);
    }

    /** @param row Index of row to delete */
    public void deleteRow(final int row)
    {
        final RDBTableRow rdb_row = rows.get(row);
        rdb_row.delete();
        fireRowChanged(rdb_row);
    }

    /** @param row Index 0, ... of row in table
     *  @return Info about that row
     */
    public RDBTableRow getRow(final int row)
    {
        return rows.get(row);
    }

    /** Write table changes/additions out to RDB */
    public void write() throws Exception
    {
        final PreparedStatement insert = rdb.getConnection().prepareStatement(sql_insert);
        final PreparedStatement update = rdb.getConnection().prepareStatement(sql_update);
        final PreparedStatement delete = rdb.getConnection().prepareStatement(sql_delete);
        try
        {
            for (RDBTableRow row : rows)
            {
                if (row.wasDeleted())
                {   // Was it ever in the RDB?
                    if (!row.wasReadFromRDB())
                        continue;
                    delete.setString(1, row.getColumn(0));
                    final int affected = delete.executeUpdate();
                    if (affected != 1)
                        throw new Exception("Delete of " + row + " affected " +
                                affected + " instead of 1 rows");
                }
                else
                {   // Not a deletion. Modification?
                    if (! row.wasModified())
                        continue;
                    if (row.wasReadFromRDB())
                    {   // Update existing RDB row
                        for (int c=1; c<getColumnCount(); ++c)
                            update.setString(c, row.getColumn(c));
                        update.setString(getColumnCount(), row.getColumn(0));
                        final int affected = update.executeUpdate();
                        if (affected != 1)
                            throw new Exception("Update of " + row + " affected " +
                                    affected + " instead of 1 rows");
                    }
                    else
                    {   // Insert new RDB row
                        for (int c=0; c<getColumnCount(); ++c)
                            insert.setString(c+1, row.getColumn(c));
                        final int affected = insert.executeUpdate();
                        if (affected != 1)
                            throw new Exception("Insert of " + row + " affected " +
                                    affected + " instead of 1 rows");
                    }
                }
            }
        }
        finally
        {
            insert.close();
            update.close();
            delete.close();
        }
        rdb.getConnection().commit();
        read();
    }

    /** @param changed_row Inform listeners that this row changed */
    protected void fireRowChanged(final RDBTableRow changed_row)
    {
        was_modified = true;
        for (RDBTableModelListener listener : listeners)
            listener.rowChanged(changed_row);
    }

    /** @param new_row Inform listeners that this row was added */
    protected void fireNewRow(final RDBTableRow new_row)
    {
        was_modified = true;
        for (RDBTableModelListener listener : listeners)
            listener.newRow(new_row);
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        // Title
        final StringBuilder result = new StringBuilder(title);
        // Column headers
        for (int c=0;  c<getColumnCount();  ++c)
            result.append("|" + getHeader(c));
        result.append("|\n");
        // Rows
        for (RDBTableRow row : rows)
        {
            for (int c=0;  c<getColumnCount();  ++c)
                result.append("|" + row.getColumn(c));
            result.append("|\n");
        }
        return result.toString();
    }
}
