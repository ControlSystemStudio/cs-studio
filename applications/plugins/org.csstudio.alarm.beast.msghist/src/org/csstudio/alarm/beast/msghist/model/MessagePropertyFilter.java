package org.csstudio.alarm.beast.msghist.model;

/** Filter for a message property.
 *  @author Kay Kasemir
 */
public class MessagePropertyFilter
{
    final private String property;
    final private String pattern;
    
    /** Constructor
     *  @param property Property name
     *  @param pattern  Pattern for the property's value
     */
    public MessagePropertyFilter(final String property,
            final String pattern)
    {
        this.property = property;
        this.pattern = pattern;
    }

    /** @return Property name */
    public String getProperty()
    {
        return property;
    }

    /** @return Pattern for the property's value */
    public String getPattern()
    {
        return pattern;
    }
}
