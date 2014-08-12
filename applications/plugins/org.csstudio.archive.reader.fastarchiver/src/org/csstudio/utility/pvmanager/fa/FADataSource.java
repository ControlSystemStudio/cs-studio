package org.csstudio.utility.pvmanager.fa;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;

public class FADataSource extends DataSource{

	
	public FADataSource(boolean writeable) {
		super(writeable);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ChannelHandler createChannel(String channelName) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
