package org.csstudio.platform.ui.internal.data.exchange;

/** Minimal implementation of IArchiveDataSource.
 *  <p>
 *  The drag-and-drop transfer uses it internally.<br>
 *  Applications which need to provide IArchiveDataSource
 *  can use this, but can also implement the interface themselves.
 *  @author Kay Kasemir
 */
public class ArchiveDataSource implements IArchiveDataSource
{
    /** The URL of the archive data server. */
    private String url;
    
    /** The key of the archive under the url. */
    private int key;
    
    /** The name of the archive. */
    private String name;
    
    /** Constructor.
     *  @param url Data server URL.
     *  @param key Archive key.
     *  @param name Archive name, derived from key.
     */
    public ArchiveDataSource(String url, int key, String name)
    {
        this.url = url;
        this.key = key;
        this.name = name;
    }

    /* @see org.csstudio.data.exchange.IArchiveDataSource#getUrl() */
    public String getUrl()
    {
        return url;
    }

    
    /* @see org.csstudio.data.exchange.IArchiveDataSource#getKey() */
    public int getKey()
    {
        return key;
    }

    /* @see org.csstudio.data.exchange.IArchiveDataSource#getName() */
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "Archive '" + url + "' (" + key + ", '" + name + "')";
    }
}
