package org.csstudio.archive.rdb.internal;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.rdb.Severity;
import org.csstudio.archive.rdb.Status;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupHelper;
import org.csstudio.archive.rdb.engineconfig.RetentionHelper;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineHelper;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.platform.utility.rdb.TimeWarp;
import org.csstudio.platform.utility.rdb.RDBUtil.Dialect;

/** RDB Archive access.
 *  @author Kay Kasemir
 */
public class RDBArchiveImpl extends RDBArchive
{
    /** Status string for <code>Double.NaN</code> samples */
	private static final String NOT_A_NUMBER_STATUS = "NaN"; //$NON-NLS-1$

    /** Severity string for <code>Double.NaN</code> samples */
    private static final String NOT_A_NUMBER_SEVERITY = "INVALID"; //$NON-NLS-1$

    /** Database URL/user/password */
    final private String url, user, password;
    
    /** RDB connection */
	private RDBUtil rdb;
	
	/** SQL statements */
	private SQL sql;
	
	/** Channel (ID, name) cache */
	private ChannelCache channels;
	
	/** Severity (ID, name) cache */
	private SeverityCache severities;

	/** Status (ID, name) cache */
	private StatusCache stati;

	/** Prepared statement for inserting 'double' samples */
	private PreparedStatement insert_double_sample = null;
	
    /** Prepared statement for inserting 'double' array samples */
    private PreparedStatement insert_double_array_sample = null;

    /** Prepared statement for inserting 'long' samples */
	private PreparedStatement insert_long_sample = null;
	
	/** Prepared statement for inserting 'String' samples */
	private PreparedStatement insert_txt_sample = null;
	
	/** Counter for accumulated samples in 'double' batch */
	private int batched_double_inserts = 0;
	
    /** Counter for accumulated samples in 'double array' batch */
	private int batched_double_array_inserts = 0;

	/** Counter for accumulated samples in 'long' batch */
	private int batched_long_inserts = 0;
	
	/** Counter for accumulated samples in 'String' batch */
	private int batched_txt_inserts = 0;

    private RetentionHelper retentions = null;

    private SampleMode sample_modes[] = null;
	
	/** Connect to RDB.
	 *  @param url URL
     *  @param user RDB user (null if already in URL)
     *  @param password RDB password (null if already in URL)
	 *  @throws Exception on error
	 *  @see {@link RDBUtil} for syntax of URL
	 */
    public RDBArchiveImpl(final String url, final String user,
            final String password) throws Exception
	{
	    this.url = url;
	    this.user = user;
	    this.password = password;
	    connect();
	}
	
	/** Connect to RDB */
    @SuppressWarnings("nls")
    private void connect() throws Exception
    {
        // Create new connection
        CentralLogger.getInstance().getLogger(this).debug("Connecting to '" + url + "'");
        rdb = RDBUtil.connect(url, user, password);
        sql = new SQL(rdb.getDialect());
        channels = new ChannelCache(this);
        severities = new SeverityCache(rdb, sql);
        stati = new StatusCache(rdb, sql);
        
        // TODO Remove Oracle test code
        if (false)
        {
            System.out.println("Enabling Oracle trace");
            final Statement stmt = rdb.getConnection().createStatement();
            stmt.execute("alter session set tracefile_identifier='KayTest_max'"); 
            stmt.execute("ALTER SESSION SET events " +
                         "'10046 trace name context forever, level 12'");
        }
    }

    /** {@inheritDoc} */
	@Override
    public void reconnect() throws Exception
	{
	    close();
	    connect();
	}

    /** @return RDBUtil */
	public RDBUtil getRDB()
    {
        return rdb;
    }
	
	/** Access to the SQL Statements.
	 *  <p>
	 *  Not meant to be used by clients....
	 *  @return SQL statements
	 */
	public SQL getSQL()
	{
		return sql;
	}

    /** {@inheritDoc} */
    @Override
	public SampleMode [] getSampleModes() throws Exception
	{
	    if (sample_modes == null)
	        sample_modes = SampleModeHelper.getSampleModes(rdb, sql);
	    return sample_modes;
	}
    
    /** @return Sample mode for ID */
    public SampleMode getSampleMode(final int id) throws Exception
    {
        getSampleModes();
        for (SampleMode mode : sample_modes)
            if (mode.getId() == id)
                return mode;
        // Default to the first one
        return sample_modes[0];
    }
	
    /** {@inheritDoc} */
	@Override
    @SuppressWarnings("nls")
    public void close()
	{
        CentralLogger.getInstance().getLogger(this).debug("Disconnecting from '" + url + "'");
        if (sample_modes != null)
            sample_modes = null;
        if (retentions != null)
        {
            retentions.dispose();
            retentions = null;
        }
        // Clear caches
	    if (channels != null)
        {
	        channels.dispose();
	        channels = null;
        }
	    if (severities != null)
	    {
    	    severities.dispose();
    	    severities = null;
	    }
	    if (stati != null)
	    {
    	    stati.dispose();
    	    stati = null;
	    }
        // Close prepared statements
        if (insert_double_sample != null)
        {
            try
            {
                insert_double_sample.close();
            }
            catch (Exception ex)
            {
                CentralLogger.getInstance().getLogger(this).warn(ex);
            }
            insert_double_sample = null;
        }
        if (insert_double_array_sample != null)
        {
            try
            {
                insert_double_array_sample.close();
            }
            catch (Exception ex)
            {
                CentralLogger.getInstance().getLogger(this).warn(ex);
            }
            insert_double_array_sample = null;
        }
        if (insert_long_sample != null)
        {
            try
            {
                insert_long_sample.close();
            }
            catch (Exception ex)
            {
                CentralLogger.getInstance().getLogger(this).warn(ex);
            }
            insert_long_sample = null;
        }
        if (insert_txt_sample != null)
        {
            try
            {
                insert_txt_sample.close();
            }
            catch (Exception ex)
            {
                CentralLogger.getInstance().getLogger(this).warn(ex);
            }
            insert_txt_sample = null;
        }
        // Reset counters
        batched_double_inserts = 0;
        batched_double_array_inserts = 0;
        batched_long_inserts = 0;
        batched_txt_inserts = 0;
        // Disconnect from the database
        if (rdb != null)
        {
    		rdb.close();
    		rdb = null;
        }
	}
	
	/** {@inheritDoc} */
	@Override
    public ChannelConfig getChannel(final String name) throws Exception
	{
		return channels.find(name);
	}

    /** {@inheritDoc} */
	@Override
    public ChannelConfig createChannel(final String name) throws Exception
	{
		return channels.findOrCreate(name);
	}

    /** {@inheritDoc} */
	@Override
    public ChannelConfig[] findChannels(String pattern) throws Exception
	{
        // Does the pattern need patching of '\' into '\\' for MySQL
		// because its string parser handles the pattern before the MySQL regex?
        PreparedStatement sel =
            rdb.getConnection().prepareStatement(sql.channel_sel_by_pattern);
		sel.setString(1, pattern);
		ResultSet res = sel.executeQuery();
		final ArrayList<ChannelConfigImpl> tmp_res = new ArrayList<ChannelConfigImpl>();
		while (res.next())
		{   // channel_id, name, grp_id, smpl_mode_id, smpl_per
			final int group_id = res.getInt(3);
			
            final ChannelConfigImpl channel =
			    new ChannelConfigImpl(this,
			            res.getInt(1), res.getString(2), group_id,
			            getSampleMode(res.getInt(4)), res.getDouble(5));
			channels.memorize(channel);
			tmp_res.add(channel);
		}
		res.close();
		res = null;
		sel.close();
		sel = null;
		final ChannelConfigImpl result[] = new ChannelConfigImpl[tmp_res.size()];
		return tmp_res.toArray(result);
	}
	
	/** Locate a severity via its ID.
	 *  @param id Severity ID as used in RDB
	 *  @return Severity or <code>null</code>
	 *  @throws Exception on error
	 */
	public Severity getSeverity(final int id) throws Exception
	{
		return severities.find(id);
	}


	/** Locate a status via its ID.
	 *  @param id Status ID as used in RDB
	 *  @return Status, never <code>null</code>
	 *  @throws Exception on error
	 */
	public String getStatusString(final int id) throws Exception
	{
		final Status status = stati.find(id);
		if (status != null)
			return status.getName();
		return ""; //$NON-NLS-1$
	}
	
	/** Add a sample to the archive.
	 *  <p>
	 *  For performance reasons, this call actually only adds
	 *  the sample to a 'batch'.
	 *  Need to follow up with 'commitBatch()' when done.
	 *  @param channel Channel to which this sample belongs
	 *  @param sample
	 *  @throws Exception on error
	 *  @see #commitBatch()
	 */
	public void batchSample(final ChannelConfigImpl channel,
			                final IValue sample) throws Exception
	{
	    // Need to write meta data?
	    if (channel.getMetaData() == null)
	        writeMetaData(channel, sample);
        final Timestamp stamp = TimeWarp.getSQLTimestamp(sample.getTime());
        final Severity severity =
        			severities.findOrCreate(sample.getSeverity().toString());
        final Status status = stati.findOrCreate(sample.getStatus());
        if (sample instanceof IDoubleValue)
        {
            final double dbl[] = ((IDoubleValue)sample).getValues();
            batchDoubleSamples(channel, stamp, severity, status, dbl);
        }
        else if (sample instanceof ILongValue)
        {
            final long num = ((ILongValue)sample).getValue();
            batchLongSamples(channel, stamp, severity, status, num);
        }
        else if (sample instanceof IEnumeratedValue)
        {	// Enum handled just like (long) integer
            final long num = ((IEnumeratedValue)sample).getValue();
            batchLongSamples(channel, stamp, severity, status, num);
        }
        else
        {	// Handle string and possible other types as strings
            final String txt = sample.format();
            batchTextSamples(channel, stamp, severity, status, txt);
        }
	}

	/** Write the meta data of the sample, and update the channel's info. */
    private void writeMetaData(final ChannelConfigImpl channel, final IValue sample)
        throws Exception
    {
        if (sample instanceof IEnumeratedValue)
        {
            // Clear numeric meta data, set enumerated in RDB
            NumericMetaDataHelper.set(this, channel, null);
            final IEnumeratedMetaData meta =
                (IEnumeratedMetaData)sample.getMetaData();
            EnumMetaDataHelper.set(this, channel, meta);
            channel.setMetaData(meta);
        }
        else if (sample instanceof IStringValue)
        {
            // Strings have no meta data. But we don't know at this point
            // if it's really a string channel, or of this is just a special
            // string value like "disconnected".
            // In order to not delete any existing meta data,
            // we just do nothing.
        }
        else // One of the numeric types
        {
            // Clear enumerated meta data, set numeric
            EnumMetaDataHelper.set(this, channel, null);
            final INumericMetaData meta =
                (INumericMetaData)sample.getMetaData();
            NumericMetaDataHelper.set(this, channel, meta);
            channel.setMetaData(meta);
        }
    }

    /** Helper for batchSample: Add double sample(s) to batch. */
	private void batchDoubleSamples(final ChannelConfigImpl channel,
	        final Timestamp stamp, final Severity severity,
	        final Status status, final double dbl[]) throws Exception
	{
        if (insert_double_sample == null)
            insert_double_sample =
                rdb.getConnection().prepareStatement(sql.sample_insert_double);
        // Catch not-a-number, which JDBC (at least Oracle) can't handle.
        if (Double.isNaN(dbl[0]))
        {
            insert_double_sample.setDouble(5, 0.0);
            completeAndBatchInsert(insert_double_sample,
                    channel, stamp,
                    severities.findOrCreate(NOT_A_NUMBER_SEVERITY),
                    stati.findOrCreate(NOT_A_NUMBER_STATUS));
        }
        else
        {
            insert_double_sample.setDouble(5, dbl[0]);
            completeAndBatchInsert(insert_double_sample, channel, stamp, severity, status);
        }
        ++batched_double_inserts;
        // More array elements?
        if (dbl.length > 1)
        {
            if (insert_double_array_sample == null)
                insert_double_array_sample =
                    rdb.getConnection().prepareStatement(
                            sql.sample_insert_double_array_element);
            for (int i = 1; i < dbl.length; i++)
            {
                insert_double_array_sample.setInt(1, channel.getId());
                insert_double_array_sample.setTimestamp(2, stamp);
                insert_double_array_sample.setInt(3, i);
                // Patch NaN.
                // Conundrum: Should we set the status/severity to indicate NaN?
                // Would be easy if we wrote the main sample with overall
                // stat/sevr at the end.
                // But we have to write it first to avoid index (key) errors
                // with the array sample time stamp....
                // Go back and update the main sample after the fact??
                if (Double.isNaN(dbl[i]))
                    insert_double_array_sample.setDouble(4, 0.0);
                else
                    insert_double_array_sample.setDouble(4, dbl[i]);
                // MySQL nanosecs
                if (rdb.getDialect() == Dialect.MySQL)
                    insert_double_array_sample.setInt(5, stamp.getNanos());  

                // Batch
                insert_double_array_sample.addBatch();
                ++batched_double_array_inserts;
            }
        }
	}
	
    /** Helper for batchSample: Add long sample to batch.
     *  TODO support arrays of long?
     */
	private void batchLongSamples(final ChannelConfigImpl channel,
            final Timestamp stamp, final Severity severity,
            final Status status, final long num) throws Exception
    {
       if (insert_long_sample == null)
           insert_long_sample =
               rdb.getConnection().prepareStatement(sql.sample_insert_int);
       insert_long_sample.setLong(5, num);
       completeAndBatchInsert(insert_long_sample, channel, stamp, severity, status);
       ++batched_long_inserts;
    }

    /** Helper for batchSample: Add text sample to batch. */
    private void batchTextSamples(final ChannelConfigImpl channel,
            final Timestamp stamp, final Severity severity,
            final Status status, final String txt) throws Exception
    {
        if (insert_txt_sample == null)
            insert_txt_sample =
                rdb.getConnection().prepareStatement(sql.sample_insert_string);
        insert_txt_sample.setString(5, txt);
        completeAndBatchInsert(insert_txt_sample, channel, stamp, severity, status);
        ++batched_txt_inserts;
    }

    /** Helper for batchSample:
	 *  Set the parameters common to all insert statements, add to batch.
	 */
    private void completeAndBatchInsert(
            final PreparedStatement insert_xx, final ChannelConfigImpl channel,
            final Timestamp stamp, final Severity severity,
            final Status status) throws Exception
    {
        // Set the stuff that's common to each type
        insert_xx.setInt(1, channel.getId());
        insert_xx.setTimestamp(2, stamp);
        insert_xx.setInt(3, severity.getId());
        insert_xx.setInt(4, status.getId());
        // MySQL nanosecs
        if (rdb.getDialect() == Dialect.MySQL)
            insert_xx.setInt(6, stamp.getNanos());  
        // Batch
        insert_xx.addBatch();
    }
	
    /** {@inheritDoc} */
    @Override
	public void commitBatch() throws Exception
	{
        if (batched_double_inserts > 0)
        {
            try
            {
                checkBatchExecution(insert_double_sample);
            }
            finally
            {
                batched_double_inserts = 0;
            }
        }
        if (batched_long_inserts > 0)
        {
            try
            {
                checkBatchExecution(insert_long_sample);
            }
            finally
            {
                batched_long_inserts = 0;
            }
        }
        if (batched_txt_inserts > 0)
        {
            try
            {
                checkBatchExecution(insert_txt_sample);
            }
            finally
            {
            	batched_txt_inserts = 0;
            }
        }
        if (batched_double_array_inserts > 0)
        {
            try
            {
                checkBatchExecution(insert_double_array_sample);
            }
            finally
            {
                batched_double_array_inserts = 0;
            }
        }
	}

	/** Submit and clear the batch, or roll back on error */
    private void checkBatchExecution(final PreparedStatement insert) throws Exception
    {
        try
        {   // Try to perform the inserts
            insert.executeBatch();
            rdb.getConnection().commit();
        }
        catch (final SQLException ex)
        {   // On failure, roll back.
            // With Oracle 10g, the BatchUpdateException doesn't
            // indicate which of the batched commands faulted...
            insert.clearBatch();
            // Still: Commit what's committable.
            // Unfortunately no way to know what failed,
            // and no way to re-submit the 'remaining' inserts.
            rdb.getConnection().commit();
            throw ex;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public SampleEngineConfig addEngine(String name, String description,
            URL url) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.add(name, description, url);
    }
    
    /** {@inheritDoc} */
    @Override
    public SampleEngineConfig findEngine(String name) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.find(name);
    }
    
    /** Find engine by ID
     *  @param engine_id ID of engine to locate
     *  @return SampleEngineInfo or <code>null</code> when not found
     *  @throws Exception on error
     */
    public SampleEngineConfig findEngine(final int engine_id) throws Exception
    {
        final SampleEngineHelper engines = new SampleEngineHelper(this);
        return engines.find(engine_id);
    }

    /** {@inheritDoc} */
    @Override
    public Retention getRetention(final String description) throws Exception
    {
        if (retentions == null)
            retentions = new RetentionHelper(rdb, sql);
        return retentions.getRetention(description);
    }

    /** Find group by ID
     *  @param group_id Group ID
     *  @return ChannelGroup or <code>null</code>
     *  @throws Exception on error
     */
    public ChannelGroupConfig findGroup(int group_id) throws Exception
    {
        final ChannelGroupHelper groups = new ChannelGroupHelper(this);
        return groups.find(group_id);
    }

    /** Add group
     *  @param engine SampleEngineInfo
     *  @param group_name Name that identifies the group
     *  @param retention How to retain the values over time
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig addGroup(final SampleEngineConfig engine,
            final String group_name, final Retention retention) throws Exception
    {
        final ChannelGroupHelper groups = new ChannelGroupHelper(this);
        return groups.add(group_name, engine.getId(), 0, retention.getId());
    }
}
