package org.csstudio.channelfinder.views;

import java.util.Collection;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ChannelFinderViewContentProvider implements
		IStructuredContentProvider {
	
	private Collection<ICSSChannel> channelsList;
	 
	public ChannelFinderViewContentProvider(Collection<ICSSChannel> channelsList) {
		this.channelsList = channelsList;
	}
	
	public ChannelFinderViewContentProvider(){
		
	}

	@Override
	public Object[] getElements(Object arg0) {
//		try{
//			return ((Collection<ICSSChannel>)arg0).toArray();
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
		return channelsList.toArray();
		
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
