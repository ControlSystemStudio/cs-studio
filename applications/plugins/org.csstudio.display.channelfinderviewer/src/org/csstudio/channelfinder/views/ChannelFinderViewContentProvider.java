package org.csstudio.channelfinder.views;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ChannelFinderViewContentProvider implements
		IStructuredContentProvider {
	private Collection<Channel> channelsList;
	 
	public ChannelFinderViewContentProvider(Collection<Channel> channelsList) {
		// TODO Auto-generated constructor stub
		this.channelsList = channelsList;
	}

	@Override
	public Object[] getElements(Object arg0) {
		try{
			return channelsList.toArray();
//			return channelslist.getChannels().getChannels().toArray();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
//		return client.getInstance().getChannels().getChannels().toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
	}

}
