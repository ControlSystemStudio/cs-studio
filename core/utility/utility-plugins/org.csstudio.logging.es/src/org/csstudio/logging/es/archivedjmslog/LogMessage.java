package org.csstudio.logging.es.archivedjmslog;

import org.eclipse.ui.views.properties.IPropertySource;

abstract public class LogMessage
        implements IPropertySource, Comparable<LogMessage>
{
    @Override
    /**
     * Very simplistic default implementation. This function almost certainly
     * needs to be overridden.
     */
    public int compareTo(final LogMessage other)
    {
        return getTime().compareTo(other.getTime());
    }

    @Override
    public Object getEditableValue()
    {
        return this;
    }

    /** Get time in millis since the epoch. */
    abstract public Long getTime();

    /** @see IPropertySource */
    @Override
    public boolean isPropertySet(final Object id)
    {
        return getPropertyValue(id) != null;
    }

    /** @see IPropertySource */
    @Override
    public void resetPropertyValue(final Object id)
    {
        // NOP, properties are read-only
    }

    @Override
    public void setPropertyValue(Object id, Object value)
    {
        // NOP, properties are read-only
    }

}
