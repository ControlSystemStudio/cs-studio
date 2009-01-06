package org.csstudio.platform.utility.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


/** Helper for <code>StringID</code> entries in RDB.
 *  <p>
 *  The find... calls keep the prepared statement open for re-use.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StringIDHelper
{
    private RDBUtil rdb;
    final private String table;
    final private String id_column;
    final private String name_column;
    private PreparedStatement sel_by_name = null;
    private PreparedStatement sel_by_id = null;
    private Connection connection = null;
    
    /** Construct helper
     *  @param rdb RDBUTil
     *  @param table Name of RDB table
     *  @param id_column Name of the ID column
     *  @param name_column Name of the Name column
     */
    public StringIDHelper(final RDBUtil rdb,
            final String table, final String id_column,
            final String name_column)
    {
        this.rdb = rdb;
        try {
			this.connection = rdb.getConnection();
		} catch (Exception e) {
			//discard the exception, simply print its stack trace
			e.printStackTrace();
		}
        this.table = table;
        this.id_column = id_column;
        this.name_column = name_column;
    }
    
    /** Must be called for cleanup when no longer needed */
    public void dispose()
    {
        if (sel_by_name != null)
        {
            try
            {
                sel_by_name.close();
            }
            catch (Exception ex) { /* NOP */ }
            sel_by_name = null;
        }
        if (sel_by_id != null)
        {
            try
            {
                sel_by_id.close();
            }
            catch (Exception ex) { /* NOP */ }
            sel_by_id = null;
        }
    }

    /** Locate StringID by name
     *  @param name Name to locate
     *  @return StringID or <code>null</code> if nothing found
     *  @throws Exception on error
     */
    public StringID find(final String name) throws Exception
    {
        Connection tempConnection = rdb.getConnection();       	
    	if (sel_by_name == null || connection != tempConnection) {
    		connection = tempConnection;
            sel_by_name = connection.prepareStatement(
                "SELECT " + id_column + " FROM " + table +
                " WHERE "+ name_column + "=?");
    	}
        sel_by_name.setString(1, name);
        final ResultSet result = sel_by_name.executeQuery();
        if (result.next())
            return new StringID(result.getInt(1), name);
        return null;
    }

    /** Locate StringID by ID
     *  @param id ID to locate
     *  @return StringID or <code>null</code> if nothing found
     *  @throws Exception on error
     */
    public StringID find(final int id) throws Exception
    {
    	Connection tempConnection = rdb.getConnection();       	
        if (sel_by_id == null || connection != tempConnection) {
        	connection = tempConnection;
            sel_by_id = connection.prepareStatement(
                    "SELECT " + name_column + " FROM " + table +
                    " WHERE "+ id_column + "=?");
        }
        sel_by_id.setInt(1, id);
        final ResultSet result = sel_by_id.executeQuery();
        if (result.next())
            return new StringID(id, result.getString(1));
        return null;
    }

    /** Add new name, unless it's already in the RDB.
     *  @param name Name to add
     *  @return StringID found or generated
     *  @throws Exception on error
     */
    public StringID add(final String name) throws Exception
    {
        StringID entry = find(name);
        if (entry != null)
            return entry;
        entry = new StringID(getNextID(), name);
        final PreparedStatement insert = rdb.getConnection().prepareStatement(
                "INSERT INTO " + table +
                "(" + id_column + "," + name_column + ") VALUES (?,?)");
        try
        {
            insert.setInt(1, entry.getId());
            insert.setString(2, entry.getName());
            final int rows = insert.executeUpdate();
            if (rows != 1)
                throw new Exception("Insert of " + entry + " changed " +
                        rows + " instead of 1 rows");
            rdb.getConnection().commit();
            return entry;
        }
        finally
        {
            insert.close();
        }
    }
    
    private int getNextID() throws Exception
    {
        final Statement statement = rdb.getConnection().createStatement();
        try
        {
            final ResultSet res = statement.executeQuery(
                    "SELECT MAX(" + id_column + ") FROM " + table);
            if (res.next())
            {
                final int id = res.getInt(1);
                if (id > 0)
                    return id + 1;
            }
            return 1;
        }
        finally
        {
            statement.close();
        }
    }
}
