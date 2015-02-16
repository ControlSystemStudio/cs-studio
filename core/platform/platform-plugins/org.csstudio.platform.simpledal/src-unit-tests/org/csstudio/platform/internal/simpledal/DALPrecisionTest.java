package org.csstudio.platform.internal.simpledal;


import junit.framework.Assert;

import org.csstudio.dal.DalPlugin;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.RemoteInfo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * This test expected the following channel on an IOC:
 *  record(calc,"DALPrecisionTest1") {
 *      field(DESC,"DALPrecisionTest")
 *      field(SCAN,"1 second")
 *      field(INPA, "12.345678901")
 *      field(CALC, "A")
 *      field(LOPR,"0")
 *      field(HOPR,"20")
 *      field(PREC,"4")
 *  }
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.07.2010
 */
public class DALPrecisionTest {

    private static final Logger LOG = LoggerFactory.getLogger(DALPrecisionTest.class);

	
    /**
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 30.06.2010
     */
    private final class ChannelListenerImplementation implements ChannelListener {
        private boolean _updated;
        private AnyDataChannel _newChannel;
        private final String _name;

        /**
         * Constructor.
         * @param name
         * @param class1
         */
        public ChannelListenerImplementation(final String name) {
            _name = name;
            _updated = false;
        }

        public void channelStateUpdate(final AnyDataChannel channel) {
            LOG.info(_name+":\t********** channelStateUpdate " + channel.getUniqueName());
        }

        public void channelDataUpdate(final AnyDataChannel channel) {
            LOG.info("********** channelDataUpdate  " + channel.getUniqueName());
            LOG
                    .info("state " + channel.getStateInfo() + ", connected "
                            + channel.isConnected());
            _newChannel = channel;
            _updated=true;
        }

        public boolean hasUpdated() {
            return _updated;
        }

        /**
         * @return the newChannel
         */
        public AnyDataChannel getNewChannel() {
            return _newChannel;
        }


    }

    private static final String DAL_PRECISION_TEST_1 = "DALPrecisionTest1";
    private static final long SLEEP_TIME_MSEC = 2000;

    @Before
    public void setUp() throws Exception {
        //
    }

    // Dal return at the moment the VAL as String with a fixed precision 6. Don't use the IOC precision.
    @Ignore
    @Test(timeout=5000)
    public void testChannelPrecisionAsDouble() throws Exception {
        LOG.info("Will register  " + System.currentTimeMillis());

        ChannelListenerImplementation listener = new ChannelListenerImplementation("as Double");
        DalPlugin.getDefault().getSimpleDALBroker()
        .registerListener(newConnectionParameters(DAL_PRECISION_TEST_1, null, Double.class),
                          listener);

        // Wait
        // Wait
        while(!listener.hasUpdated()) {
            //wait for update
        }
        AnyDataChannel channel = listener.getNewChannel();

        if (channel.isConnected()) {
            showMetadata(channel.getData().getMetaData());
            checkDoubleAnydata(channel.getData());
        } else {
            Assert.fail();
        }

        LOG.info("Go to sleep at " + System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME_MSEC);
        LOG.info("Woke up at     " + System.currentTimeMillis());
    }

    @Test(timeout=5000)
    public void testChannelPrecisionAsString() throws Exception {
        LOG.info("Will register  " + System.currentTimeMillis());

        ChannelListenerImplementation listener = new ChannelListenerImplementation("as String");
        DalPlugin.getDefault().getSimpleDALBroker()
        .registerListener(newConnectionParameters(DAL_PRECISION_TEST_1, null, String.class),
                          listener);
        // Wait
        while(!listener.hasUpdated()) {
            //wait for update
        }
        AnyDataChannel channel = listener.getNewChannel();

        if (channel.isConnected()) {
            showMetadata(channel.getData().getMetaData());
            checkStringAnydata(channel.getData());
        } else {
            Assert.fail();
        }

        LOG.info("Go to sleep at " + System.currentTimeMillis());
        Thread.sleep(SLEEP_TIME_MSEC);
        LOG.info("Woke up at     " + System.currentTimeMillis());
    }

    private ConnectionParameters newConnectionParameters(final String pvName,
                                                         final String characteristic,
                                                         final Class<?> type) {
        return new ConnectionParameters(newRemoteInfo(pvName, characteristic), type);
    }

    private RemoteInfo newRemoteInfo(final String pvName, final String characteristic) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, characteristic, null);
    }

    private void checkStringAnydata(final AnyData data) {
        LOG.info("Check for String");
        if (data != null) {
            //            try {
            LOG.info("\tString:\tdoubleValue " + data.doubleValue());
            LOG.info("\tStrign:\tstringValue " + data.stringValue());
            Assert.assertEquals("12.3456", data.stringValue());
        } else {
            Assert.fail();
            LOG.info("\tString:\tdata not available");
        }
    }
    private void checkDoubleAnydata(final AnyData data) {
        LOG.info("Check for Double");
        if (data != null) {
            //            try {
            LOG.info("\tDouble:\tdoubleValue " + data.doubleValue());
            LOG.info("\tDouble:\tstringValue " + data.stringValue());
            Assert.assertEquals(12.345678901, data.doubleValue());
        } else {
            Assert.fail();
            LOG.info("\tDouble:\tdata not available");
        }
    }

    private void showMetadata(final MetaData metaData) {
        if (metaData != null) {
            LOG.info("getName " + metaData.getName());
            LOG.info("getPrecision " + metaData.getPrecision());
        } else {
            LOG.info("metadata not available");
        }
    }

}
