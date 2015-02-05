package org.csstudio.platform.internal.dal;

import junit.framework.TestCase;

import org.csstudio.dal.DalPlugin;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.MetaData;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * Test class for a connection problem with initially unavailable channels in
 * combination with untyped {@link ConnectionParameters}.
 * 
 * To reproduce the problem, start the test WITHOUT a running SOFT-IOC. The test
 * should throw an execption then!
 * 
 * @author swende
 * 
 */
public final class DataAccessLayerTest5 extends TestCase {
	private static final ConnectionParameters UNTYPED_PARAMETERS = new ConnectionParameters(
			new RemoteInfo("DAL-EPICS", "Random:111", null, null));

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

		broker.registerListener(UNTYPED_PARAMETERS, listener);

		Thread.sleep(60000);
	}
}
