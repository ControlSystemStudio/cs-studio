/**
 *
 */
package org.csstudio.service.channelfinder;

import org.diirt.service.Service;
import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;
import org.diirt.vtype.VTable;

/**
 * @author shroffk
 *
 */
public class ChannelFinderService extends Service {

    /**
     * @param serviceDescription
     */
    public ChannelFinderService() {
        super(new ServiceDescription("cf", "ChannelFinder service").addServiceMethod(findMethod()));
    }

    public static ServiceMethodDescription findMethod() {
        return new ServiceMethodDescription("find", "Find Channels") {

            @Override
            public ServiceMethod createServiceMethod(ServiceDescription serviceDescription) {
                return new QueryServiceMethod(this, serviceDescription);
            }
        }.addArgument("query", "Query String", VString.class).addResult("result", "Query Result", VTable.class)
                .addResult("result_size", "Query Result size", VNumber.class);
    }

}
