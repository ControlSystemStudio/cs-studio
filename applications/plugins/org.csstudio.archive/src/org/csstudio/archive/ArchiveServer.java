package org.csstudio.archive;

import org.csstudio.data.values.ITimestamp;

/** Main interface to an archive.
 *  <p>
 *  To get an actual implementation, use the ArchiveImplementationRegistry.
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
 *  @see ArchiveImplementationRegistry
 *
 *  @author Kay Kasemir
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Blaz Lipuscek
 *  @author Craig McChesney
 *  @author Sergei Chevtsov
 *  @author Peregrine McGehee
 */
@SuppressWarnings("nls")
public abstract class ArchiveServer
{
    /** Server name information.
     *  <p>
     *  Unique name of this ArchiveServer implementation.
     *  <p>
     *  The Description, Version and URL give more detail.
     *  This string is meant for tools that need a short
     *  description of the data source, for example
     *  "Channel Archiver" or "Archive Record" etc.
     *  @return Name of the server.
     */
    abstract public String getServerName();

    /** URL for this ArchiveServer.
     *  @return URL as a string.
     */
    abstract public String getURL();

    /** Arbitrary description string, may span multiple lines,
     *  with details left to the implementation.
     *  @return Description string.
     */
    abstract public String getDescription();

    /** Version information.
     *  <p>
     *  The meaning of this version number is up to the implementation.
     *  @return A version number.
     */
    abstract public int getVersion();

    /** Request type for getting raw samples.
     *  <p>
     *  Every archive server should support this request type.
     *  <p>
     *  Additional <code>request_parms</code>:
     *  <ol>
     *  <li><code>Integer count</code>: Maximum number of samples<br>
     *      The client can restrict the number of samples returned
     *      by this call to prevent unnecessary memory and network load.
     *      The server might add additional contraints,
     *      so the result is not guaranteed to reach the requested 'end'
     *      time; follow-up requests might be required.
     *  </ol>
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, String, Object[])
     */
    public static final String GET_RAW = "raw";

    /** Request type for getting averaged samples with minimum and maximum
     *  detail.
     *  <p>
     *  Every archive server should support this request type
     *  as best as it can, meaning: It should provide samples.
     *  If a server cannot perform true averaging, it may internally
     *  revert to the <code>GET_RAW</code> request.
     *  <p>
     *  Additional <code>request_parms</code>:
     *  <ol>
     *  <li><code>Double seconds</code>: "Delta",
     *      the distance in seconds between samples<br>
     *      The server computes the average over samples within the given
     *      time period in seconds. The resulting sample count should be
     *      close to <code>(end - start) / delta</code>.
     *
     *      The server might add additional contraints, especially when it
     *      comes to non-numeric values.
     *      It might return samples that indicate a network disconnect as such,
     *      suspending the averaging.
     *      It might also return Strings or waveform samples as such.
     *      It might further limit the total sample count to preserve resources,
     *      so the result is not guaranteed to reach the requested 'end'
     *      time; follow-up requests might be required.
     *  </ol>
     *  @see #getRequestTypes()
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, String, Object[])
     */
    public static final String GET_AVERAGE = "average";

    /** Request type for getting linearly interpolated samples.
     *  <p>
     *  If supported, this request type uses linear interpolation onto
     *  the transitions between segments.
     *  <p>
     *  Requires one 'Double' parameter that specifies the seconds
     *  between interpolated samples.
     *
     *  @see #getRequestTypes()
     *  @see #getSamples()
     */
    public static final String GET_LINEAR = "linear";

    /** Get a list of the supported request types as strings.
     *  <p>
     *  The returned list should at least include GET_RAW
     *  and GET_AVERAGE, so that clients
     *  can deal with known request methods.
     *  <p>
     *  The implementation is free to support additional request
     *  types and return their names in here, but of course only
     *  certain clients will know how to handle those.
     *  @return List of supported request types.
     *  @see #getSamples(int, String[], ITimestamp, ITimestamp, String, Object[])
     */
    abstract public String [] getRequestTypes();

    /** Obtain a list of archives handled by this server.
     *  @return The available archives.
     */
    abstract public ArchiveInfo[] getArchiveInfos();

    /** Helper for searching the ArchiveInfos for a given archive.
     *  <p>
     *  Implementations of the <code>ArchiveServer</code> are welcome
     *  to provide a more efficient version, since this is a simple
     *  linear search.
     *
     *  @param name The archive to locate.
     *  @return The key for that archive.
     *  @throws ArchiveAccessException on error (archive not found).
     */
    public int getArchiveKey(final String name) throws ArchiveAccessException
    {
        final ArchiveInfo[] archives = getArchiveInfos();
        for (final ArchiveInfo archive : archives) {
            if (archive.getName().equals(name)) {
                return archive.getKey();
            }
        }
        throw new ArchiveAccessException("Unknown archive '" + name + "'");
    }

    /** Helper for searching the ArchiveInfos for a given archive.
     *  <p>
     *  Implementations of the <code>ArchiveServer</code> are welcome
     *  to provide a more efficient version, since this is a simple
     *  linear search.
     *
     *  @param key The archive keyto locate.
     *  @return The archive name for given key.
     *  @throws ArchiveAccessException on error (invalid key).
     */
    public String getArchiveName(final int key) throws ArchiveAccessException
    {
        final ArchiveInfo[] archives = getArchiveInfos();
        for (final ArchiveInfo archive : archives) {
            if (archive.getKey() == key) {
                return archive.getName();
            }
        }
        throw new ArchiveAccessException("Unknown archive key " + key);
    }

    /** Find channel in given sub-archive.
     *  <p>
     *  <i>This method might not return immediately.</i>
     *
	 * @param key Key of archive to search.
	 * @param pattern Regular Expression for channel name.
	 * @return One <code>ChannelInfo</code> for each matching channel.
	 *         Might be empty.
	 * @throws ArchiveAccessException on wrong key or internal error.
	 */
    abstract public NameInfo[] getNames(int key, String pattern)
        throws ArchiveAccessException;

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
	 *  @throws ArchiveAccessException on error
     *  @see #getRequestTypes()
	 */
    abstract public ArchiveValues[] getSamples(int key,
                                               String names[],
                                               ITimestamp start,
                                               ITimestamp end,
                                               String request_type,
                                               Object request_parms[])
        throws ArchiveAccessException;

    /** Cancel an ongoing archive query.
     *  It's up to the implementation to support this for all queries,
     *  or only 'getSamples', or not at all.
     */
    public void cancel()
    {
        // System.out.println("Cancel requested for " + toString());
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return getServerName() + " " + getURL();
    }
}
