package org.csstudio.utility.channel.nsls2;

import org.csstudio.utility.channel.ICSSChannel;

import gov.bnl.channelfinder.api.Channel;

class AbstractCSSChannel implements ICSSChannel{

	private Channel channel;
	
	AbstractCSSChannel(Channel channel){
		this.channel = channel;
	}
	
	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public String getName() {
		return channel.getName();
	}

	@Override
	public String getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}