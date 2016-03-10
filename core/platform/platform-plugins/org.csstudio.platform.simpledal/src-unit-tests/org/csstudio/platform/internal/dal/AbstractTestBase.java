package org.csstudio.platform.internal.dal;

import junit.framework.TestCase;

import org.csstudio.dal.DalPlugin;
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
 * Tests for the new narrow interface in DAL.
 *
 * The record under test is:
 *
 * <pre>
 * record(ai,'Chiller:Pressure:1') {
 *             field(DESC,'DEMO')
 *             field(SCAN,'.1 second')
 *             field(INP,'2.22')
 *             field(HIHI,'35.00')
 *             field(HIGH,'30.00')
 *             field(LOW,'10.00')
 *             field(LOLO,'5.00')
 *             field(HOPR,'40.00')
 *             field(LOPR,'1.11')
 *             field(HHSV,'NO_ALARM')
 *             field(LLSV,'NO_ALARM')
 *             field(HSV,'NO_ALARM')
 *             field(LSV,'NO_ALARM')
 *     }
 * </pre>
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractTestBase extends TestCase {
    protected static final String PV = "Chiller:Pressure:1";

    protected SimpleDALBroker broker;

    protected static final double RECORD_VAL = 2.22;
    protected static final double RECORD_HIHI = 35.0;
    protected static final double RECORD_HIGH = 30.0;
    protected static final double RECORD_LOLO = 5.0;
    protected static final double RECORD_LOW = 10.0;
    protected static final double RECORD_HOPR = 40.0;
    protected static final double RECORD_LOPR = 1.11;

    @Override
    protected void setUp() throws Exception {
        broker = SimpleDALBroker.newInstance(DalPlugin.getDefault().getApplicationContext());
    }

    @Override
    protected void tearDown() throws Exception {
    }

    /**
     * Tries to receive a value asynchronously via
     * {@link SimpleDALBroker#getValueAsync(ConnectionParameters, ResponseListener)}
     * and verifies that the specified expected value is delivered.
     *
     * @param ri
     *            the address
     * @throws Exception
     */
    protected Double receiveAsync(RemoteInfo ri) throws Exception {
        final Holder<Double> holder = new Holder<Double>();

        Request request = broker.getValueAsync(new ConnectionParameters(ri, Double.class), new ResponseListener<Double>() {
            public void responseError(ResponseEvent<Double> event) {
                Exception error = event.getResponse().getError();
                System.out.println("Response Error On Async Call (" + error != null ? error.getMessage() : "no message" + ")");
                fail();
            }

            public void responseReceived(ResponseEvent<Double> event) {
                Double value = event.getResponse().getValue();
                System.out.println("Response Received On Async Call (" + (value != null ? value : "no value") + ")");
                holder.setValue(value);
            }
        });

        assertNotNull(request);

        Thread.sleep(5000);

        return holder.getValue();
    }

    /**
     * Tries to receive a value via a {@link ChannelListener} that is registered
     * via
     * {@link SimpleDALBroker#registerListener(ConnectionParameters, ChannelListener)}
     * and verifies that the specified expected value is delivered.
     *
     * @param ri
     * @throws Exception
     */
    protected AnyData receiveViaListener(RemoteInfo ri) throws Exception {
        final Holder<AnyData> holder = new Holder<AnyData>();

        ChannelListener listener = new ChannelListener() {

            public void channelDataUpdate(AnyDataChannel channel) {
                AnyData data = channel.getData();
                MetaData meta = data != null ? data.getMetaData() : null;
                System.out.println("Channel Data Update Received (" + (data != null ? data : "no value") + "; "
                        + (meta != null ? meta : "no metadata") + ")");
                holder.setValue(data);
            }

            public void channelStateUpdate(AnyDataChannel channel) {
                AnyData data = channel.getData();
                MetaData meta = data != null ? data.getMetaData() : null;
                System.out.println("Channel State Update Received (" + (data != null ? data : "no value") + "; "
                        + (meta != null ? meta : "no metadata") + ")");
                holder.setValue(data);
            }

        };

        broker.registerListener(new ConnectionParameters(ri, Double.class), listener);

        Thread.sleep(15000);

        return holder.getValue();
    }
}
