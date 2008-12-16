package org.csstudio.platform.utility.jms;

/** JMS Connection Listener Interface
 *  <p>
 *  Some JMS implementations like Apache ActiveMQ
 *  might offer a connection listener, but only when
 *  directly using the ActiveMQ API.
 *  This interface removes the direct dependnecy from client code.
 *  
 *  @see JMSConnectionFactory#addListener
 *  @author Kay Kasemir
 */
public interface JMSConnectionListener
{
    /** Invoked when the connection to the JMS server is interrupted */
    public void linkDown();

    /** Invoked when the connection to the JMS server is re-connected (resumed) */
    public void linkUp();
}
