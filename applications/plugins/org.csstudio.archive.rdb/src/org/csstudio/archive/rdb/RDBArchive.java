package org.csstudio.archive.rdb;

import java.net.URL;

import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;
import org.csstudio.platform.utility.rdb.RDBUtil;

abstract public class RDBArchive
{
    /** Connect to RDB.
     *  @param url URL, where "jdbc:oracle_stage:" handled like
     *             "jdbc:oracle:" except that it switches to the "staging"
     *             tables.
     *  @throws Exception on error
     *  @see {@link RDBUtil} for syntax of URL
     */
    public static RDBArchive connect(final String url) throws Exception
    {
        return connect(url, null, null);
    }
    
    /** Connect to RDB.
     *  @param url URL, where "jdbc:oracle_stage:" handled like
     *             "jdbc:oracle:" except that it switches to the "staging"
     *             tables.
     *  @param user RDB user (null if already in URL)
     *  @param password RDB password (null if already in URL)
     *  @throws Exception on error
     *  @see {@link RDBUtil} for syntax of URL
     */
    public static RDBArchive connect(final String url, final String user,
            final String password) throws Exception
    {
        return new RDBArchiveImpl(url, user, password);
    }

    /** Close and re-open the RDB connection.
     *  <p>
     *  Can be used in an attempt to recover from for example network errors.
     *  @throws Exception on error.
     */
    abstract public void reconnect() throws Exception;
    
    /** Close the RDB connection.
     *  Clears all caches, deletes prepared statements etc.
     */
    abstract public void close();
        
    /** @return Array of supported sample modes 
     *  @throws Exception on error
     */
    abstract public SampleMode [] getSampleModes() throws Exception;
    
    /** Get channel by name.
     *  @param name
     *  @return Channel or <code>null</code> if not found.
     */
    abstract public ChannelConfig getChannel(final String name) throws Exception;

    /** Get existing or create new channel by name.
     *  @param name
     *  @return Channel or <code>null</code> if not found.
     */
    abstract public ChannelConfig createChannel(final String name) throws Exception;

    /** Get all channels where name matches the patter.
     *  @param pattern Regular expression
     *  @return Array of Channels. May be empty, but not <code>null</code>
     *  @exception Exception on error
     */
    abstract public ChannelConfig[] findChannels(String pattern) throws Exception;
    
    /** Commit samples that might have been added to a batch.
     *  @see ChannelConfig#batchSample()
     */
    abstract public void commitBatch() throws Exception;

    /** Add a sample engine config.
     *  <p>
     *  <b>Note:</b> In case that engine already exists, its current
     *  configuration is deleted (all channels and groups removed).
     *  Existing archived samples are preserved.
     * @param name Engine name (used for lookup)
     * @param description Any one-line description
     * @param url URL (host, port) where engine is supposed to run
     * @return SampleEngineInfo
     */
    abstract public SampleEngineConfig addEngine(String name, String description,
            URL url) throws Exception;

    /** Locate a sample engine config.
     *  @param name Engine name
     *  @return SampleEngineInfo or <code>null</code> if not found
     */
    abstract public SampleEngineConfig findEngine(String name) throws Exception;

    /** Locate a certain type of retention
     *  @param description Retention name
     *  @return Retention
     */
    abstract public Retention getRetention(String description) throws Exception;
}
