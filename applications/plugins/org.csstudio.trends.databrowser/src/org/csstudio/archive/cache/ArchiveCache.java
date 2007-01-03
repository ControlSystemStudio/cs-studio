package org.csstudio.archive.cache;

import java.util.LinkedList;

import org.csstudio.archive.ArchiveImplementationRegistry;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.trends.databrowser.Plugin;

/** Cache access to archive servers.
 *  <p>
 *  While the first call of most chache methods will go directly
 *  to the underlying archive API and thus take however long it takes,
 *  subsequent calls for the same information might return immediately,
 *  using cached data.
 *  
 *  @author Kay Kasemir
 */
public class ArchiveCache
{
    private static ArchiveCache singleton = null;
    private LinkedList<CachingArchiveServer> server_cache =
        new LinkedList<CachingArchiveServer>();
    
    /** Hidden contructor.
     *  @see #getInstance()
     */
    private ArchiveCache()
    {}
    
    /** @return The one and only instance of this cache. */
    synchronized public static ArchiveCache getInstance()
    {
        if (singleton == null)
            singleton = new ArchiveCache();           
        return singleton;
    }
    
    /** Get an archive server.
     *  <p>
     *  Further calls to the returned server obviously go directly
     *  to that server.
     *  Alternatively, one can try to use the returned server
     *  in subsequent calls to the cache.
     *  @return ArchiveServer for given URL.
     */
    synchronized public ArchiveServer getServer(String url) throws Exception
    { 
        // Is that URL a cached server?
        for (ArchiveServer server : server_cache)
            if (server.getURL().equals(url))
                return server;
        Plugin.logInfo("ArchiveCache connects to " + url); //$NON-NLS-1$
        // Not cached, create new connection.
        // Since we're 'synchronized', this blocks all other calls.
        // But the alternative would be to risk multiple concurrent
        // connection attempts, which is overall worse.
        ArchiveServer real_server = 
            ArchiveImplementationRegistry.getInstance().getServer(url);
        CachingArchiveServer server = new CachingArchiveServer(real_server);
        server_cache.add(server);
        return server;
    }
}