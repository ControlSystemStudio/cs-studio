package org.csstudio.debugging.jmsmonitor;

/** One property and value of a received message
 *  @author Kay Kasemir
 */
public class MessageProperty
{
    final private String name, value;

    /** Initialize Property
     *  @param name Name of property
     *  @param value Value of property
     */
    public MessageProperty(final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }

    /** @return name */
    public String getName()
    {
        return name;
    }

    /** @return value */
    public String getValue()
    {
        return value;
    }
}
