/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import org.epics.util.time.Timestamp;

/** Interface to archive data retrieval.
 *  <p>
 *  To get an actual implementation, use the ArchiveRepository.
 *  <p>
 *  Note that most methods in this API naturally require a network request
 *  or a lengthy search for data, so they will possibly take some time to
 *  complete.
 *  <p>
 *  Historic remark:
 *  Much of this code is based on the java archive viewer's
 *  data library, primarily written by Craig McChesney and Sergei Chevtsov,
 *  with contributions by Peregrine McGehee, all at LANL at the time.
 *  The first CSS implementation was in org.csstudio.archive.
 *  <p>
 *  The difference between this archive reader API and the previous CSS
 *  org.csstudio.archive implementation:
 *  <ul>
 *  <li>Samples are fetched via iterators, similar to JDBC.
 *      The old API returned an array of samples and would either run out of
 *      memory when trying to get a lot of data, or return only a fraction of
 *      the requested data, which in turn required another layer of code to
 *      iterate over 'batches' of partial results.
 *      This iterator-based API can be used to export virtually unlimited
 *      amounts of data, sample by sample, for computations or export
 *      to other data formats. It is up to the implementation to optimize
 *      the data transfer, for example like JDBC's internal fetch size.
 *  <li>Functionality that several data providers never actually implemented
 *      has been removed (start/end time of a channel's sample range)
 *  <li>Channel search via 'glob' pattern, which can be a lot faster than
 *      regular expression search, is directly supported.
 *  </ul>
 *
 *  @see ArchiveRepository
 *
 *  @author Kay Kasemir
 *  @author Jan Hatje, Albert Kagarmanov, Blaz Lipuscek:
 *          Contributed to org.csstudio.archive
 *  @author Craig McChesney, Sergei Chevtsov, Peregrine McGehee:
 *          Created original Java archive viewer
 */
public interface ArchiveReader
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
    public String getServerName();

    /** URL for this ArchiveServer.
     *  @return URL as a string.
     */
    public String getURL();

    /** Arbitrary description string, may span multiple lines,
     *  with details left to the implementation.
     *  @return Description string.
     */
    public String getDescription();

    /** Version information.
     *  <p>
     *  The meaning of this version number is up to the implementation.
     *  @return A version number.
     */
    public int getVersion();

    /** Obtain a list of archives handled by this server.
     *  <p>
     *  A server might support access to more than one sub-archive.
     *
     *  @return The available archives, at least one.
     */
    public ArchiveInfo[] getArchiveInfos();

    /** Find channel in given sub-archive via "file glob" pattern search.
     *  <p>
     *  In principle, globs can be translated into regular expressions,
     *  then use <code>getNamesByRegExp()</code>. But file globs
     *  can be a lot faster for some implementations, and are also known
     *  by more users, so a designated call appeared appropriate.
     *  <p>
     *  The search should be case-insensitive, but details can depend
     *  on the implementation.
     *  <p>
     *  It is not fully defined how to handle an empty pattern.
     *  Ideally, the result would be empty.
     *  To locate <u>all</u> channels in the archive, the user
     *  needs to specifically search for "*".
     *  But some existing implementations return all channels
     *  for an empty pattern...
     *
     *  @param key Key of archive to search.
     *  @param glob_pattern Pattern for channel name with '*' or '?'.
     *  @return List of matching channel names. Might be empty.
     *  @throws Exception on wrong key or internal error.
     */
    public String[] getNamesByPattern(int key, String glob_pattern)
        throws Exception;

    /** Find channel in given sub-archive via regular expression search.
     *  <p>
     *  For case-sensitivity and handling of empty regular expression refer to
     *  <code>getNamesByPattern</code>
     *
     *  @param key Key of archive to search.
     *  @param reg_exp Regular Expression for channel name.
     *  @return List of matching channel names. Might be empty.
     *  @throws Exception on wrong key or internal error.
     */
    public String[] getNamesByRegExp(int key, String reg_exp)
        throws Exception;

    /** Read original, raw samples from the archive
     *  @param key Key of the archive to use for retrieval.
     *  @param name Channel name
     *  @param start Start time
     *  @param end End time
     *  @return ValueIterator for the 'raw' samples in the archive
     *  @throws UnknownChannelException when channel is not known
     *  @throws Exception on error
     */
    public ValueIterator getRawValues(int key, String name,
            Timestamp start, Timestamp end) throws UnknownChannelException, Exception;

    /** Read optimized samples from the archive.
     *  <p>
     *  The exact behavior is up to the implementation.
     *  In the simplest case, a data provider can fall back to
     *  <code>getRawValues</code>, i.e. return the raw data.
     *  Ideally, however, the result will contain about <code>count</code>
     *  values that represent the data between the <code>start</code> and
     *  <code>end</code> time, for example by segmenting the time range into
     *  'count' buckets and returning the min/max/average for each bucket.
     *  If the raw data contains less than the requested 'count',
     *  or the raw data is not numeric and thus cannot be reduced,
     *  the method can fall back to returning the original samples.
     *
     *  @param key Key of the archive to use for retrieval.
     *  @param name Channel name
     *  @param start Start time
     *  @param end End time
     *  @param count Hint for number of values
     *  @return ValueIterator for the 'raw' samples in the archive
     *  @throws UnknownChannelException when channel is not known
     *  @throws Exception on error
     */
    public ValueIterator getOptimizedValues(int key, String name,
        Timestamp start, Timestamp end, int count) throws UnknownChannelException, Exception;

    /** Cancel an ongoing archive query.
     *  It's up to the implementation to support this for all queries,
     *  or only 'getSamples', or not at all.
     */
    public void cancel();

    /** Must be called when archive is no longer used to release resources */
    public void close();
}
