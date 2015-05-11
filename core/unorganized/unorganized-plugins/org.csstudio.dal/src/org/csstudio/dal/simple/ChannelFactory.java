package org.csstudio.dal.simple;


/**
 * Factory interface for creating AnyDataChannnel instances.
 *
 */
public interface ChannelFactory
{
    /**
     * Returns supported connection types.
     *
     * @see RemoteInfo#getConnectionType()
     *
     * @return supported connection types.
     */
    public String[] getSupportedConnectionTypes() throws Exception;

    /** Create a PV for the given channel name, using the PV factory
     *  selected via the prefix of the channel name, or the default
     *  PV factory if no prefix is included in the channel name.
     *
     *  @param name Channel name, format "prefix://name" or just "name"
     *  @return PV
     *  @exception Exception on error
     */
    public AnyDataChannel createChannel(final String name) throws Exception;

    /** Create a PV for the given <code>RemoteInfo</code>, using the PV factory.
     *
     *  @param name <code>RemoteInfo</code> object
     *  @return PV
     *  @exception Exception on error
     */
    public AnyDataChannel createChannel(final RemoteInfo remoteInfo) throws Exception;

}
