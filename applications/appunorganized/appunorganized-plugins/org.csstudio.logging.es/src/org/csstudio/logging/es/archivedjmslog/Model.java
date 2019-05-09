package org.csstudio.logging.es.archivedjmslog;

public class Model
{
    protected PropertyFilter[] filters = null;

    public void setFilters(PropertyFilter[] filters)
    {
        synchronized (this)
        {
            this.filters = filters;
        }
    }
}
