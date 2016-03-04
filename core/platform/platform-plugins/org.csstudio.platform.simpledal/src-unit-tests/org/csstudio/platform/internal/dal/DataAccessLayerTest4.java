package org.csstudio.platform.internal.dal;

import junit.framework.TestCase;

import org.csstudio.dal.DalPlugin;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseEvent;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * Test class for a connection problem with initially unavailable channels.
 *
 * To reproduce the problem, start the test WITHOUT a running SOFT-IOC and start
 * the SOFT-IOC, when the test is running. Take a look at the console then!!
 *
 * @author swende
 *
 */
public final class DataAccessLayerTest4 extends TestCase {
    private static final ConnectionParameters TYPED_PARAMETERS = new ConnectionParameters(
            new RemoteInfo("DAL-EPICS", "Random:111", null, null), Double.class);

    private SimpleDALBroker broker;

    @Override
    protected void setUp() throws Exception {
        broker = SimpleDALBroker.newInstance(DalPlugin.getDefault()
                .getApplicationContext());
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void test() throws Exception {
        ChannelListener listener = new ChannelListener() {

            public void channelDataUpdate(AnyDataChannel channel) {
                AnyData data = channel.getData();
                MetaData meta = data != null ? data.getMetaData() : null;
                System.out.println("Channel Data Update Received ("
                        + (data != null ? data : "no value") + "; "
                        + (meta != null ? meta : "no metadata") + ")");
            }

            public void channelStateUpdate(AnyDataChannel channel) {
                AnyData data = channel.getData();
                MetaData meta = data != null ? data.getMetaData() : null;
                System.out.println("Channel State Update Received ("
                        + (data != null ? data : "no value") + "; "
                        + (meta != null ? meta : "no metadata") + ")");
            }

        };

        broker.registerListener(TYPED_PARAMETERS, listener);

        Thread.sleep(2000);

        System.out
                .println("Start SOFT-IOC now!! (Do you receive any updates??)");

        Thread.sleep(60000);
    }
}
