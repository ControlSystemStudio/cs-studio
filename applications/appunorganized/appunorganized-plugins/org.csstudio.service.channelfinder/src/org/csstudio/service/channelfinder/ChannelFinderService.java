/**
 *
 */
package org.csstudio.service.channelfinder;

import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

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
