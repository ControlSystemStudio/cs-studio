package org.csstudio.utility.esclient;

/**
 * Scroll-related settings for {@link ElasticsearchClient}.
 * 
 * @author Michael Ritzert
 */
public class ScrollSettings
{
    private int pageSize;
    private String timeout;

    /**
     * Initialize the settings.
     * 
     * @param timeout
     *            Timeout for the scroll context. E.g. "1m". @see <a href=
     *            "https://www.elastic.co/guide/en/elasticsearch/reference/current/common-options.html#time-units">Time
     *            units</a>.
     * @param pageSize
     *            Maximum number of results returned per page.
     */
    public ScrollSettings(String timeout, int pageSize)
    {
        setTimeout(timeout);
        setPageSize(pageSize);
    }

    public int getPageSize()
    {
        return this.pageSize;
    }

    public String getTimeout()
    {
        return this.timeout;
    }

    public void setPageSize(int pageSize)
    {
        if (0 >= pageSize)
        {
            throw new IllegalArgumentException("pageSize must be > 0."); //$NON-NLS-1$
        }
        this.pageSize = pageSize;
    }

    public void setTimeout(String timeout)
    {
        if ((null == timeout) || timeout.isEmpty())
        {
            throw new IllegalArgumentException("timeout is required."); //$NON-NLS-1$
        }
        this.timeout = timeout;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "ScrollSettings: " + this.timeout + ", " + this.pageSize;
    }
}