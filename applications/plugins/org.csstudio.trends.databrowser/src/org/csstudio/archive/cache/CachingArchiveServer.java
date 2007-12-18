package org.csstudio.archive.cache;

import java.util.LinkedList;

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
    // to the front, and remove the (old) entries from the tail when
    // exceeding the SAMPLE_CACHE_LENGTH.
    private static int SAMPLE_CACHE_LENGTH = 40;
    final private LinkedList<SampleCacheEntry> sample_cache =
                                new LinkedList<SampleCacheEntry>();

    /** Constructor.
     *  @param server The 'real' server.
     */
    CachingArchiveServer(final ArchiveServer server)
    {
        super();
        this.server = server;
    }

    @Override
    public ArchiveInfo[] getArchiveInfos()
    {   return server.getArchiveInfos(); }

    @Override
    public String getDescription()
    {   return server.getDescription();   }

    @Override
    public NameInfo[] getNames(int key, String pattern) throws Exception
    {   return server.getNames(key, pattern);  }

    @Override
    public String[] getRequestTypes()
    {   return server.getRequestTypes();  }
    
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
        // See if we find the result for this request in the cache:
        SampleHashKey hash_key = new SampleHashKey(key, names[0],
                       start, end, request_type, request_parms);
        Plugin.getLogger().debug("CachingArchiveServer: " + hash_key);
        for (SampleCacheEntry entry : sample_cache)
            if (entry.getKey().equals(hash_key))
            {
                ArchiveValues result[] = new ArchiveValues[]
                { entry.getData() };
                Plugin.getLogger().debug("Found data on cache: " +
                                result[0].getSamples().length + " samples");
                return result;
            }
        
        // Fall back to server
        ArchiveValues result[] = server.getSamples(key,
                        names, start, end, request_type, request_parms);
        // Nothing? That's OK, but don't cache, since a later request might
        // actually find data that's just been added.
        if (result == null)
            return null;
        // Expect one result for single-name request.
        if (result.length != 1)
            throw new Exception("Received " + result.length + " responses");
        final ArchiveValues samples = result[0];
        // Remember the result
        if (samples != null)
        {
            if (sample_cache.size() >= SAMPLE_CACHE_LENGTH-1)
                sample_cache.removeLast().getKey();
            sample_cache.addFirst(new SampleCacheEntry(hash_key, samples));
            Plugin.getLogger().debug("Got " +
                            result[0].getSamples().length + " new samples");
        }
        return result;
    }

    @Override
    public String getURL()
    {   return server.getURL();  }

    @Override
    public int getVersion()
    {   return server.getVersion();  }
    
    @Override 
    public String getServerName() 
    {	return server.getServerName(); }
        
    @SuppressWarnings("nls")
    synchronized public void clearCache()
    {
    	Plugin.getLogger().debug("Cleared data on cache");
    	sample_cache.clear();
    }
}
