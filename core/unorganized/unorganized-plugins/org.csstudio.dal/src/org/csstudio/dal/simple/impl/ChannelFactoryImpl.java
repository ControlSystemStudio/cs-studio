package org.csstudio.dal.simple.impl;

import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelFactory;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.spi.DefaultPropertyFactoryBroker;
import org.csstudio.dal.spi.PropertyFactoryBroker;

/** PV Factory
 *  <p>
 *  Locates the one and only expected implementation of the IPVFactory
 *  via an extension to the pvfactory extension point
 *  and creates the PV through it.
 *
 */
public class ChannelFactoryImpl implements ChannelFactory
{

    public static void main(String[] args) {

        try {

            ChannelFactory cf= ChannelFactoryImpl.getInstance();

            AnyDataChannel ch;
            ch = cf.createChannel("sfgfs");
            AnyData data= ch.getData();

            ch.addListener(new ChannelListener() {

                public void channelStateUpdate(AnyDataChannel channel) {
                    // TODO Auto-generated method stub

                }

                public void channelDataUpdate(AnyDataChannel channel) {
                    // TODO Auto-generated method stub

                }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }
    private static ChannelFactory channelFactory;

    private final PropertyFactoryBroker propertyFactoryManager;

    /**
     * Returns a default implementation of <code>ChannelFactory</code>.
     * @return default <code>ChannelFactory</code>
     */
    public static final synchronized ChannelFactory getInstance() {
        if (channelFactory == null) {
            channelFactory = new ChannelFactoryImpl(DefaultPropertyFactoryBroker.getInstance());
        }
        return channelFactory;
    }

    private ChannelFactoryImpl(PropertyFactoryBroker commonFactory) {
        this.propertyFactoryManager = commonFactory;
    }

    /** {@inheritDoc} */
    @Override
    final public String[] getSupportedConnectionTypes() throws Exception
    {
        String[] s= propertyFactoryManager.getSupportedPlugTypes();
        String[] ss= new String[s.length];
        for (int i = 0; i < ss.length; i++) {
            ss[i]=RemoteInfo.DAL_TYPE_PREFIX+s[i];
        }
        return ss;
    }

    /** {@inheritDoc} */
    @Override
    final public AnyDataChannel createChannel(final String name) throws Exception
    {
        return propertyFactoryManager.getProperty(name);
    }

    /** {@inheritDoc} */
    @Override
    final public AnyDataChannel createChannel(final RemoteInfo remoteInfo) throws Exception {
        return propertyFactoryManager.getProperty(remoteInfo);
    }

}
