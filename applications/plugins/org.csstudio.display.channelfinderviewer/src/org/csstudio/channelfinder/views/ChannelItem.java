package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;

import org.csstudio.platform.model.IProcessVariable;

public class ChannelItem implements IProcessVariable {

	private Channel channel;
	
	public ChannelItem(Channel channel){
		this.channel = channel;
	}
	
	public Channel getChannel(){
		return this.channel;
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

	@Override
	public String getName() {
		return channel.getName();
	}

}
