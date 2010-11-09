package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Channel;
import org.csstudio.platform.model.IProcessVariable;

/**
 * A channel interface currently tied with the channelfinder.
 * 
 * @author shroffk
 *
 */
public interface ICSSChannel extends IProcessVariable {

	/**
	 * @return
	 */
	public Channel getChannel();	
	
}
