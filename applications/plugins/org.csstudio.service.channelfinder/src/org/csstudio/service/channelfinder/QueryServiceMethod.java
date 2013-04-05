/**
 * 
 */
package org.csstudio.service.channelfinder;

import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;

import java.util.HashMap;
import java.util.Map;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;

/**
 * @author shroffk
 * 
 */
public class QueryServiceMethod extends ServiceMethod {

    /**
     */
    public QueryServiceMethod() {
	super(new ServiceMethodDescription("find", "Find Channels")
		.addArgument("query", "Query String", String.class).addResult(
			"result", "Query Result", String.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.epics.pvmanager.service.ServiceMethod#executeMethod(java.util.Map,
     * org.epics.pvmanager.WriteFunction, org.epics.pvmanager.WriteFunction)
     */
    @Override
    public void executeMethod(Map<String, Object> parameters,
	    final WriteFunction<Map<String, Object>> callback,
	    final WriteFunction<Exception> errorCallback) {
	String query = (String) parameters.get("query");
	ChannelQuery channelQuery = ChannelQuery.query(query).build();
	channelQuery.addChannelQueryListener(new ChannelQueryListener() {

	    @Override
	    public void queryExecuted(final Result result) {
		if (result.exception != null) {
		    errorCallback.writeValue(result.exception);
		} else {
		    Map<String, Object> resultMap = new HashMap<>();
		    resultMap.put("result", result.channels.size()
			    + "-channels found");
		    callback.writeValue(resultMap);
		}
	    }
	});
	channelQuery.refresh();
    }

}
