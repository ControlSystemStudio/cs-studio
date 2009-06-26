package org.csstudio.archive.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.trends.databrowser.Plugin;

/** ArchiveServer implementation that uses a cache whenever possible, 
 *  and forwards the rest to the 'real' server.
 *  @author Kay Kasemir
 */
public class CachingArchiveServer extends ArchiveServer
{
    /** The 'real' server. */
    final private ArchiveServer server;
    
    // The sample cache simply compares the exact request.
    // That works OK for panning back and forth, or zooming in and back out:
    // Request are then found on the cache.
    // It doesn't work when we start scrolling, and thus always have tiny
    // changes in the start/end time even when only zooming out and back in.
    //
    // Since keeping data cached consumes a lot of memory, this cache uses
    // a list of finite length. Assuming the 'undo' kind of usage, where
    // recent additions are most likely to be used again, we add entries
    // to the front, and remove the (old) entries when
    // exceeding the SAMPLE_CACHE_LENGTH.
    private static int SAMPLE_CACHE_LENGTH = 10;
    final private LinkedHashMap<SampleHashKey, ArchiveValues> sample_cache =
                            new LinkedHashMap<SampleHashKey, ArchiveValues>()
    {
        // Keep compiler happy 
        private static final long serialVersionUID = 1L;

        /** Keep cache size limited to SAMPLE_CACHE_LENGTH */
        @Override
        protected boolean removeEldestEntry(
                Entry<SampleHashKey, ArchiveValues> eldest)
        {
            return size() > SAMPLE_CACHE_LENGTH;
        }
    };

    /** Constructor.
     *  @param server The 'real' server.
     */
    CachingArchiveServer(final ArchiveServer server)
    {
        super();
        this.server = server;
    }

    /** Forward to 'real' server */
    @Override 
    public String getServerName() 
    {	return server.getServerName(); }

    /** Forward to 'real' server */
    @Override
    public String getURL()
    {   return server.getURL();  }

    /** Forward to 'real' server */
    @Override
    public String getDescription()
    {   return server.getDescription();   }

    /** Forward to 'real' server */
    @Override
    public int getVersion()
    {   return server.getVersion();  }

    /** Forward to 'real' server */
    @Override
    public String[] getRequestTypes()
    {   return server.getRequestTypes();  }

    /** Forward to 'real' server */
    @Override
    public ArchiveInfo[] getArchiveInfos()
    {   return server.getArchiveInfos(); }

    /** Forward to 'real' server */
    @Override
    public NameInfo[] getNames(int key, String pattern) throws Exception
    {   return server.getNames(key, pattern);  }

    /** Look for samples in cache, otherwise forward to real server */
    @SuppressWarnings("nls")
    @Override
    synchronized public ArchiveValues[] getSamples(
                    final int key,
                    final String[] names,
                    final ITimestamp start,
                    final ITimestamp end,
                    final String request_type,
                    final Object request_parms[]) throws Exception
    {
        if (names.length != 1)
            throw new Exception("Only supporting single-name requests.");
        // Is result for this request in cache?
        final SampleHashKey hash_key = new SampleHashKey(key, names[0],
                                       start, end, request_type, request_parms);
        ArchiveValues samples = sample_cache.get(hash_key);
        final Logger logger = Plugin.getLogger();
        if (samples != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Found data on cache ("
                        + sample_cache.size() +	" entries) : "
                        + samples.getSamples().length
                        + " samples for " + hash_key);
            return new ArchiveValues[] { samples };
        }
        
        // Fall back to server
        final ArchiveValues result[] = server.getSamples(key,
                        names, start, end, request_type, request_parms);
        // Nothing? That's OK, but don't cache, since a later request might
        // actually find data that's just been added.
        if (result == null)
            return null;
        // Expect one result for single-name request.
        if (result.length != 1)
            throw new Exception("Received " + result.length + " responses");
        samples = result[0];
        // Remember the result - if it contained data
        if (samples != null)
        {
            sample_cache.put(hash_key, samples);
            if (logger.isDebugEnabled())
                logger.debug("Got " + result[0].getSamples().length
                        + " new samples for " + hash_key);
        }
        return result;
    }
    
    /** Forward to 'real' server */
    @Override
    public void cancel()
    {
        server.cancel();
    }

    @SuppressWarnings("nls")
    synchronized public void clearCache()
    {
    	Plugin.getLogger().debug("Cleared data on cache");
    	sample_cache.clear();
    }
}
