package org.csstudio.archive;

import org.csstudio.platform.util.ITimestamp;

/** Main interface to an archive.
 *  <p>
 *  The implementation of this for a data source of type 'xyz' should
 *  be in org.csstudio.archive.xyz.Archive, and it should include a
 *  factory method
 *  <pre>
 *     public static Archive getInstance(String url) throws Exception
 *  </pre>
 *  so that we can locate and construct available implementations
 *  via Java introspection.
 *  <p>
 *  Some of the methods in this API naturally require a network request
 *  or a lenghty search for data, and they will be marked as possibly
 *  taking some time to complete, so that the caller can wrap them in
 *  a background task.
 *  Methods which are not specifically marked are supposed to return
 *  "immediately".
 *  <p>
 *  Historic remark:
 *  Much of this code is based on the java archive viewer's
 *  data library, primarily written by Craig McChesney and Sergei Chevtsov,
 *  with contributions by Peregrine McGehee, all at LANL at the time.
 *  
 *  @author Kay Kasemir
 *  @author Craig McChesney
 *  @author Sergei Chevtsov
 *  @author Peregrine McGehee
 */
@SuppressWarnings("nls")
public abstract class ArchiveServer
{     	
    /** URL for this ArchiveServer.
     *  @return URL as a string. */
    abstract public String getURL();
    
    /** Arbitrary description string, may span multiple lines,
     *  with details of the content left to the implementation.
     *  @return Description string. */
    abstract public String getDescription();

    /** Version information.
     *  <p>
     *  The meaning of this version number is up to the implementation.
     *  @return A version number.
     */
    abstract public int getVersion();
    
    /** Server name information.
     * <p>
     * The unique name of this implementation of ArchiveServer
     * @return A name of the server.
     */
    abstract public String getServerName();
    
    /** Request type for getting raw samples.
     *  <p>
     *  Additional <code>request_parms</code>:
     *  <ol>
     *  <li><code>Integer count</code>: Maximum number of samples<br>
     *      The client can restrict the number of samples returned
     *      by this call to prevent unnecessary memory and network load.
     *      The server might add addtional contraints,
     *      so the result is not guaranteed to reach the requested 'end'
     *      time; follow-up requests might be required.
     *  </ol>
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    public static final String GET_RAW = "raw";

    /** Request type for getting averaged samples.
     *  <p>
     *  Additional <code>request_parms</code>:
     *  <ol>
     *  <li><code>Double seconds</code>: "Delta",
     *      the distance in seconds between samples<br>
     *      The server computes the average over samples within the given
     *      time period in seconds. The resulting sample count should be
     *      close to <code>(end - start) / delta</code>.
     *      
     *      The server might add addtional contraints, especially when it
     *      comes to non-numeric values.
     *      It might return samples that indicate a network disconnect as such,
     *      suspending the averaging.
     *      It might also return Strings or waveform samples as such.
     *      It might further limit the total sample count to preserve resources,
     *      so the result is not guaranteed to reach the requested 'end'
     *      time; follow-up requests might be required.
     *  </ol>
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    public static final String GET_AVERAGE = "average";

    /** Request type for getting linearly interpolated samples.
     *  <p>
     *  Similar to GET_AVERAGE, but using linear interpolation onto
     *  the transitions between segments.
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    public static final String GET_LINEAR = "linear";

    /** Request type for getting data that is optimized for plotting.
     *  Additional <code>request_parms</code>:
     *  <ol>
     *  <li><code>Integer count</code>: Number of 'bins'<br>
     *      The server splits the <code>start...end</code> time range
     *      into the given number of 'bins', and computes the initial,
     *      minimum, maximum and final value within each bin.
     *      If there are less than 4 samples within a bin,
     *      those original samples might be returned.
     *      The resulting sample count should typically be
     *      close to <code>4 * (end - start) / bins</code>.
     *      <p>
     *      The idea is to request about one 'bin' per pixel row on the screen,
     *      so that the 4 bin values all fall into the same pixel row,
     *      resulting in a 'thick' line which visually provides the same
     *      information as having received the full raw data.
     *      The server might also return the minimum, maximum and average value
     *      for each bin, since that provides basically the same visual
     *      result.
     *      <p>
     *      The server might add addtional contraints, especially when it
     *      comes to non-numeric values.
     *      It might return samples that indicate a network disconnect as such,
     *      suspending the averaging.
     *      It might also return Strings or waveform samples as such.
     *  </ol>
     *  TODO replace with MIN_MAX_AVERAGE
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    public static final String GET_PLOTBINNED = "plot-binning";

    /** Request type for getting data with staircase interpolation.
     *  TODO describe
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    public static final String GET_SPREADSHEET = "spreadsheet";

    /** Get a list of the supported request types as strings.
     *  <p>
     *  The returned list should at least include GET_RAW.
     *  If the archive supports for example an averaged request,
     *  the list should contain GET_AVERAGE, so that clients
     *  can deal with known request methods. 
     *  <p>
     *  The implementation is free to support additional request
     *  types and return their names in here, but of course only
     *  certain clients will know how to handle those. 
     *  @return List of supported request types.
     *  @see #getRequestType(String)
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, int, Object[])
     */
    abstract public String [] getRequestTypes();
    
    /** Helper for locating a request code by name.
     *  <p> 
     * @param request_name For example: GET_RAW.
     * @return The 'request_type' ID for a given request type string.
     * @throws Exception when asking for unsupported request type.
     * @see #getRequestTypes()
     */
    public int getRequestType(String request_name) throws Exception
    {
        final String request_types[] = getRequestTypes();
        for (int i=0; i<request_types.length; ++i)
            if (request_types[i].equalsIgnoreCase(request_name)) // add  IgnoreCase Albert
                return i;
        throw new Exception("Unsupported request type '" + request_name + "'");
    }

    /** Obtain a list of archives handled by this server.
     *  @return The available archives. */
    abstract public ArchiveInfo[] getArchiveInfos();

    /** Helper for searching the ArchiveInfos for a given archive.
     *  <p>
     *  Implementations of the <code>ArchiveServer</code> are welcome
     *  to provide a more efficient version, since this is a simple
     *  linear search.
     *
     *  @param name The archive to locate.
     *  @return The key for that archive.
     *  @throws Exception on error (archive not found).
     */ 
    public int getArchiveKey(String name) throws Exception
    {
        final ArchiveInfo[] archives = getArchiveInfos();
        for (int i = 0; i < archives.length; i++)
            if (archives[i].getName().equals(name))
                return archives[i].getKey();
        throw new Exception("Unknown archive '" + name + "'");
    }
    	
    /** Helper for searching the ArchiveInfos for a given archive.
     *  <p>
     *  Implementations of the <code>ArchiveServer</code> are welcome
     *  to provide a more efficient version, since this is a simple
     *  linear search.
     *
     *  @param key The archive keyto locate.
     *  @return The archive name for given key.
     *  @throws Exception on error (invalid key).
     */ 
    public String getArchiveName(int key) throws Exception
    {
        final ArchiveInfo[] archives = getArchiveInfos();
        for (int i = 0; i < archives.length; i++)
            if (archives[i].getKey() == key)
                return archives[i].getName();
        throw new Exception("Unknown archive key " + key);
    }
    
    /** Find channel in given sub-archive.
     *  <p>
     *  <i>This method might not return immediately.</i>
     *  
	 * @param key Key of archive to search.
	 * @param pattern Regular Expression for channel name.
	 * @return One <code>ChannelInfo</code> for each matching channel.
	 *         Might be empty.
	 * @throws Exception on wrong key or internal error.
	 */
    abstract public NameInfo[] getNames(int key, String pattern)
        throws Exception;

	/** Read samples from the archive.
     *  <p>
     *  <i>This method might not return immediately.</i>
     *  
	 *  @param key Key of the archive to use for retrieval.
	 *  @param channels The list of channel names.
	 *  @param start Start time.
	 *  @param end End time.
	 *  @param request_type How to retrieve
     *  @param request_parms Additional parameters,
     *                       meaning depends on request_type
	 *  @return One <code>ArchivedSamples</code> per channel as returned
	 *          by the data server. The order and count need not match
	 *          the <code>channels</code> put into the request!
	 *  @throws Exception on error
     *  @see #getRequestTypes()
	 */
    abstract public ArchiveValues[] getSamples(int key, String names[],
			ITimestamp start, ITimestamp end,
            int request_type, Object request_parms[])
        throws Exception;
    
    /** Returns an id of last error which occured on request.
     * 
     * @return 0 if there was no error, otherwise error id.
     */
    public int getLastRequestError() {
    	return 0;
    }
}
