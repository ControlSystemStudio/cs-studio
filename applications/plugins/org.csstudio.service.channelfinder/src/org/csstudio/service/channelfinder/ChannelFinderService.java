/**
 * 
 */
package org.csstudio.service.channelfinder;

import org.diirt.service.Service;
import org.diirt.service.ServiceDescription;

/**
 * @author shroffk
 * 
 */
public class ChannelFinderService extends Service {

    /**
     * @param serviceDescription
     */
    public ChannelFinderService() {
	super(new ServiceDescription("cf", "ChannelFinder service")
	.addServiceMethod(new QueryServiceMethod()));	
    }

}
