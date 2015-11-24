/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.AlarmSeverity;

/** ArchiveReader for RDB data
 *  @author Kay Kasemir
 *  @author Lana Abadie - PostgreSQL support
 *  @author Laurent Philippe - MySQL support
 */
@SuppressWarnings("nls")
public class RDBArchiveReader implements ArchiveReader
{
    /** Oracle error code for canceled statements */
    final private static String ORACLE_CANCELLATION = "ORA-01013"; //$NON-NLS-1$

    /** Oracle error code "error occurred at recursive SQL level ...: */
    final private static String ORACLE_RECURSIVE_ERROR = "ORA-00604"; //$NON-NLS-1$

    final private boolean use_array_blob;

    final private String url;
    final private String user;
    final private int password;
    /** Timeout [secs] used for some operations that should be 'fast' */
    final private int timeout;

    /** Name of stored procedure or "" */
    final private String stored_procedure;

    final private ConnectionCache.Entry rdb;
    final private SQL sql;
    final private boolean is_oracle;

    /** Map of status IDs to Status strings */
    final private HashMap<Integer, String> stati;

    /** Map of severity IDs to Severities */
    final private HashMap<Integer, AlarmSeverity> severities;

    /** List of statements to cancel in cancel() */
    private ArrayList<Statement> cancellable_statements =
        new ArrayList<Statement>();

	private boolean concurrency = false;

    /** Initialize
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @param schema .. schema (including ".") or ""
     *  @param stored_procedure Stored procedure or "" for client-side optimization
     *  @throws Exception on error
     */
    public RDBArchiveReader(final String url, final String user,
            final String password, final String schema,
            final String stored_procedure)
        throws Exception
    {
        this(url, user, password, schema, stored_procedure, RDBArchivePreferences.useArrayBlob());
    }

    /** Initialize
     *  @param url Database URL
     *  @param user .. user
     *  @param password .. password
     *  @param schema .. schema (including ".") or ""
     *  @param stored_procedure Stored procedure or "" for client-side optimization
     *  @param use_array_blob Use BLOB for array elements?
     *  @throws Exception on error
     */
    public RDBArchiveReader(final String url, final String user,
            final String password, final String schema,
            final String stored_procedure,
            final boolean use_array_blob)
        throws Exception
    {
        this.url = url;
        this.user = user;
        this.password = (password == null) ? 0 : password.length();
        this.use_array_blob = use_array_blob;
        timeout = RDBArchivePreferences.getSQLTimeoutSecs();
        rdb = ConnectionCache.get(url, user, password);

        // Read-only allows MySQL to use load balancing
        if (!rdb.getConnection().isReadOnly()) {
        	rdb.getConnection().setReadOnly(true);
        }

        final Dialect dialect = rdb.getDialect();
        switch (dialect)
        {
        case MySQL:
            is_oracle = false;
            this.stored_procedure = stored_procedure;
            break;
        case PostgreSQL:
            is_oracle = false;
            this.stored_procedure = stored_procedure;
            break;
        case Oracle:
            is_oracle = true;
            this.stored_procedure = stored_procedure;
            break;
        default:
            throw new Exception("Unknown database dialect " + dialect);
        }
        sql = new SQL(dialect, schema);
        stati = getStatusValues();
        severities = getSeverityValues();
    }

    /** @return <code>true</code> when using Oracle, i.e. no 'nanosec'
     *          because that is included in the 'smpl_time'
     */
    public boolean isOracle()
    {
        return is_oracle;
    }

    /** @return <code>true</code> if array samples are stored in BLOB */
    public boolean useArrayBlob()
    {
        return use_array_blob;
    }

    /** @return Map of all status ID/Text mappings
     *  @throws Exception on error
     */
    private HashMap<Integer, String> getStatusValues() throws Exception
    {
        final HashMap<Integer, String> stati = new HashMap<Integer, String>();
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
        )
        {
            if (timeout > 0)
                statement.setQueryTimeout(timeout);
            statement.setFetchSize(100);
            final ResultSet result = statement.executeQuery(sql.sel_stati);
            while (result.next())
                stati.put(result.getInt(1), result.getString(2));
            return stati;
        }
    }

    /** @return Map of all severity ID/AlarmSeverity mappings
     *  @throws Exception on error
     */
    private HashMap<Integer, AlarmSeverity> getSeverityValues() throws Exception
    {
        final HashMap<Integer, AlarmSeverity> severities = new HashMap<Integer, AlarmSeverity>();
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
        )
        {
            if (timeout > 0)
                statement.setQueryTimeout(timeout);
            statement.setFetchSize(100);
            final ResultSet result = statement.executeQuery(sql.sel_severities);
            while (result.next())
            {
                final int id = result.getInt(1);
                final String text = result.getString(2);
                AlarmSeverity severity = null;
                for (AlarmSeverity s : AlarmSeverity.values())
                {
                    if (text.startsWith(s.name()))
                    {
                        severity = s;
                        break;
                    }
                    if    ("OK".equalsIgnoreCase(text) || "".equalsIgnoreCase(text))
                    {
                        severity = AlarmSeverity.NONE;
                        break;
                    }
                }
                if (severity == null)
                {
                    Activator.getLogger().log(Level.FINE,
                        "Undefined severity level {0}", text);
                    severities.put(id, AlarmSeverity.UNDEFINED);
                }
                else
                    severities.put(id, severity);
            }
            return severities;
        }
    }

    /** @return RDB connection
     *  @throws Exception on error
     */
    Connection getConnection() throws Exception
    {
        return rdb.getConnection();
    }

    Dialect getDialect()
    {
        return rdb.getDialect();
    }

    /** @return SQL statements */
    SQL getSQL()
    {
        return sql;
    }

    /** @param status_id Numeric status ID
     *  @return Status string for ID
     */
    String getStatus(int status_id)
    {
        final String status = stati.get(status_id);
        if (status == null)
            return "<" + status_id + ">";
        return status;
    }

    /** @param severity_id Numeric severity ID
     *  @return ISeverity for ID
     */
    AlarmSeverity getSeverity(int severity_id)
    {
        final AlarmSeverity severity = severities.get(severity_id);
        if (severity != null)
            return severity;
        Activator.getLogger().log(Level.WARNING, "Undefined alarm severity ID {0}", severity_id);
        severities.put(severity_id, AlarmSeverity.UNDEFINED);
        return AlarmSeverity.UNDEFINED;
    }

    /** {@inheritDoc} */
    @Override
    public String getServerName()
    {
        return "RDB";
    }

    /** {@inheritDoc} */
    @Override
    public String getURL()
    {
        return url;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return "RDB Archive V" + getVersion() + " (" + rdb.getDialect() + ")\n" +
               "User: " + user + "\n" +
               "Password: " + password + " characters";
    }

    /** {@inheritDoc} */
    @Override
    public int getVersion()
    {
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    public ArchiveInfo[] getArchiveInfos()
    {
        return new ArchiveInfo[]
        {
            new ArchiveInfo("rdb", rdb.getDialect().toString(), 1)
        };
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNamesByPattern(final int key, final String glob_pattern) throws Exception
    {
        // Escape underscores because they are SQL patterns
        String sql_pattern = glob_pattern.replace("_", "\\_");
        // Glob '?' -> SQL '_'
        sql_pattern = sql_pattern.replace('?', '_');
        // Glob '*' -> SQL '%'
        sql_pattern = sql_pattern.replace('*', '%');
        return perform_search(sql_pattern, sql.channel_sel_by_like);
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
    {
        return perform_search(reg_exp, sql.channel_sel_by_reg_exp);
    }

    /** Perform channel search by name pattern
     *  @param pattern Pattern, either SQL or Reg. Ex.
     *  @param sql_query SQL query that can handle the pattern
     *  @return Channel names
     *  @throws Exception on error
     */
    private String[] perform_search(final String pattern, final String sql_query) throws Exception
    {
        final ArrayList<String> names = new ArrayList<String>();
        final PreparedStatement statement = rdb.getConnection().prepareStatement(sql_query);
        addForCancellation(statement);
        try
        {
            statement.setString(1, pattern);
            final ResultSet result = statement.executeQuery();
            while (result.next())
                names.add(result.getString(1));
        }
        catch (Exception ex)
        {
            if (ex.getMessage().startsWith("ORA-01013") || ex.getMessage().startsWith("ERROR: canceling statement due to user request"))
            {
                // Ignore Oracle/PostgreSQL error: user requested cancel of current operation
            }
            else
                throw ex;
        }
        finally
        {
            removeFromCancellation(statement);
            statement.close();
        }
        return names.toArray(new String[names.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public ValueIterator getRawValues(final int key, final String name,
            final Timestamp start, final Timestamp end) throws UnknownChannelException, Exception
    {
        final int channel_id = getChannelID(name);
        return getRawValues(channel_id, start, end);
    }

    /** Fetch raw samples
     *  @param channel_id Channel ID in RDB
     *  @param start Start time
     *  @param end End time
     *  @return {@link ValueIterator} for raw samples
     *  @throws Exception on error
     */
    public ValueIterator getRawValues(final int channel_id,
            final Timestamp start, final Timestamp end) throws Exception
    {
        return new RawSampleIterator(this, channel_id, start, end, concurrency);
    }

    /** {@inheritDoc} */
    @Override
    public ValueIterator getOptimizedValues(final int key, final String name,
            final Timestamp start, final Timestamp end, int count) throws UnknownChannelException, Exception
    {
        // MySQL version of the stored proc. requires count > 1
        if (count <= 1)
            throw new Exception("Count must be > 1");
        final int channel_id = getChannelID(name);

        // Use stored procedure in RDB server?
        if (stored_procedure.length() > 0)
            return new StoredProcedureValueIterator(this, stored_procedure, channel_id, start, end, count);

        // Else: Determine how many samples there are
        final int counted;
        try
        (
            final PreparedStatement count_samples = rdb.getConnection().prepareStatement(
                    sql.sample_count_by_id_start_end);
        )
        {
            count_samples.setInt(1, channel_id);
            count_samples.setTimestamp(2, TimestampHelper.toSQLTimestamp(start));
            count_samples.setTimestamp(3, TimestampHelper.toSQLTimestamp(end));
            final ResultSet result = count_samples.executeQuery();
            if (! result.next())
                throw new Exception("Cannot count samples");
            counted = result.getInt(1);
        }
        // Fetch raw data and perform averaging
        final ValueIterator raw_data = getRawValues(channel_id, start, end);

        // If there weren't that many, that's it
        if (counted < count)
            return raw_data;

        // Else: Perform averaging to reduce sample count
        final double seconds = end.durationFrom(start).toSeconds() / count;
        return new AveragedValueIterator(raw_data, seconds);
    }

    /** @param name Channel name
     *  @return Numeric channel ID
     *  @throws UnknownChannelException when channel not known
     *  @throws Exception on error
     */
    // Allow access from 'package' for tests
    int getChannelID(final String name) throws UnknownChannelException, Exception
    {
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(sql.channel_sel_by_name);
        )
        {
            if (timeout > 0)
                statement.setQueryTimeout(timeout);
            statement.setString(1, name);
            final ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new UnknownChannelException(name);
            return result.getInt(1);
        }
    }

    /** Add a statement to the list of statements-to-cancel in cancel()
     *  @param statement Statement to cancel
     *  @see #cancel()
     */
    void addForCancellation(final Statement statement)
    {
        synchronized (cancellable_statements)
        {
            cancellable_statements.add(statement);
        }
    }

    /** Remove a statement to the list of statements-to-cancel in cancel()
     *  @param statement Statement that should no longer be cancelled
     *  @see #cancel()
     */
    void removeFromCancellation(final Statement statement)
    {
        synchronized (cancellable_statements)
        {
            cancellable_statements.remove(statement);
        }
    }

    /** Check if an exception indicates Oracle operation was canceled,
     *  i.e. this program requested the operation to abort
     *  @param ex Exception (Throwable) to test
     *  @return <code>true</code> if it looks like the result of cancellation.
     */
    public static boolean isCancellation(final Throwable ex)
    {
        final String message = ex.getMessage();
        if (message == null)
            return false;
        if (message.startsWith(ORACLE_CANCELLATION))
            return true;
        if (message.startsWith(ORACLE_RECURSIVE_ERROR))
        {
            final Throwable cause = ex.getCause();
            if (cause != null)
                return isCancellation(cause);
        }
        return false;
    }

    /** Cancel an ongoing RDB query.
     *  Not supported by all queries.
     */
    @Override
    public void cancel()
    {
        synchronized (cancellable_statements)
        {
            for (Statement statement : cancellable_statements)
            {
                try
                {
                    // Note that
                    //    statement.getConnection().close()
                    // does NOT stop an ongoing Oracle query!
                    // Only this seems to do it:
                    statement.cancel();
                }
                catch (Exception ex)
                {
                    Logger.getLogger(Activator.ID).log(Level.WARNING,
                        "Attempt to cancel statement", ex); //$NON-NLS-1$
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        cancel();
        ConnectionCache.release(rdb);
    }
    
    @Override
    public void enabledConcurrency(boolean concurrency) {
    	this.concurrency  = concurrency;
    }
}
