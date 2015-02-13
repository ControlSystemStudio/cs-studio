package org.csstudio.askap.pvmanager.ice;

import org.epics.pvmanager.ChannelHandler;

public class IceDataSource extends org.epics.pvmanager.DataSource {

	public IceDataSource() {
		super(false);
	}

	@Override
	protected ChannelHandler createChannel(String channelName) {
		return new IceChannelHandler(channelName);
	}

}
