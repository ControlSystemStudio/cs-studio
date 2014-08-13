package org.csstudio.utility.pvmanager.fa;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.vtype.VType;

public class FADataSource extends DataSource{

//	/** {@inheritDoc} */
//	public FADataSource (boolean writeable){
//		super(false); // Fast Archiver is never writable
//	}
	
	//is called from extension point
	public FADataSource() {
		super(false);
	}


	/** {@inheritDoc} */
	@Override
	protected ChannelHandler createChannel(String channelName){
		
		// TODO Need to find a way to find out the url(Add to the ChannelName?)
		String url = "fads://fa-archiver";
		return new FAChannelHandler(channelName, url);
	}

	/** Returns the lookup name to use to find the channel handler in the cache.
	 * 	It removes the coordinate */
	@Override
	protected String channelHandlerLookupName(String channelName) {
		// TODO Find a way to reuse channel for both coordinates
        return channelName;
    }
	

}
