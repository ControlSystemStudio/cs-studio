package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;

/** A Channel with thread-safe reference count.
 *  @author Kay Kasemir
 */
class RefCountedChannel
{
    private Channel channel;

    private int refs;

    public RefCountedChannel(Channel channel)
    {
        this.channel = channel;
        refs = 1;
    }

    synchronized public void incRefs()
    {   ++refs;  }

    synchronized public int decRefs()
    {
        --refs;
        return refs;
    }

    public Channel getChannel()
    {   return channel;   }

    public void dispose()
    {
        try
        {
            channel.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        channel = null;
    }
}