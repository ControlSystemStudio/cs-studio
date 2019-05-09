package org.csstudio.logging.es.model;

import java.util.Arrays;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.Helpers;
import org.csstudio.logging.es.archivedjmslog.StringPropertyMultiFilter;

public class MessageSeverityPropertyFilter extends StringPropertyMultiFilter
{
    protected final static String MSG_PROPERTY = JMSLogMessage.SEVERITY;
    protected int minLevel;

    public MessageSeverityPropertyFilter(final int minLevel)
    {
        super(MessageSeverityPropertyFilter.MSG_PROPERTY,
                Arrays.copyOfRange(Helpers.LOG_LEVELS, minLevel,
                        Helpers.LOG_LEVELS.length),
                false);
        this.minLevel = minLevel;
    }

    public int getMinLevel()
    {
        return this.minLevel;
    }

    @Override
    public String toString()
    {
        return MessageSeverityPropertyFilter.MSG_PROPERTY + " >= " //$NON-NLS-1$
                + Helpers.LOG_LEVELS[this.minLevel];
    }
}
