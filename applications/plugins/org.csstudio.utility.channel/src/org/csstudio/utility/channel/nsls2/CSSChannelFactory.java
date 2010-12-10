package org.csstudio.utility.channel.nsls2;

import org.csstudio.utility.channel.ICSSChannel;
import org.csstudio.utility.channel.ICSSChannelFactory;

import gov.bnl.channelfinder.api.Channel;

public class CSSChannelFactory implements ICSSChannelFactory{

	private static final CSSChannelFactory instance = new CSSChannelFactory();
	
	private CSSChannelFactory(){
		
	}
	
	public static CSSChannelFactory getInstance(){
		return instance;
	}
	
	public ICSSChannel getCSSChannel(Channel channel){
		return new AbstractCSSChannel(channel);		
	}
	
	
}
