package org.csstudio.dal.simple;

import java.util.EventListener;

/** 
 * A listener for AnyDataChannel updates. 
 * 
 *  @author tkusterle
 */
public interface ChannelListener extends EventListener
{
    /** 
     * Notification of a new data available at channel.
     *  
     * @param channel The channel that has a new data
     */
    public void channelDataUpdate(AnyDataChannel channel);
    
    /** 
     * Notification of a channel state change, e.g. channel is 
     * connected or disconnected.
     *  
     *  @param channel The channel with updated state
     */
    public void channelStateUpdate(AnyDataChannel channel);
}
