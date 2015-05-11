/**
 *
 */
package org.csstudio.service.channelfinder;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;
import gov.bnl.channelfinder.api.ChannelQuery.Result;
import gov.bnl.channelfinder.api.ChannelQueryListener;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceMethodDescription;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 *
 */
public class QueryServiceMethod extends ServiceMethod {

    /**
     */
    public QueryServiceMethod() {
    super(new ServiceMethodDescription("find", "Find Channels")
        .addArgument("query", "Query String", VString.class)
        .addResult("result", "Query Result", VTable.class)
        .addResult("result_size", "Query Result size", VNumber.class));
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
    String query = ((VString) parameters.get("query")).getValue();
    ChannelQuery channelQuery = ChannelQuery.query(query).build();
    channelQuery.addChannelQueryListener(new ChannelQueryListener() {

        @Override
        public void queryExecuted(final Result result) {
        if (result.exception != null) {
            errorCallback.writeValue(result.exception);
        } else {

            List<Channel> channels = new ArrayList<Channel>(
                result.channels);
            Collections.sort(channels, new Comparator<Channel>() {

            @Override
            public int compare(Channel o1, Channel o2) {
                return o1.getName().compareTo(o2.getName());
            }

            });

            List<String> names = new ArrayList<>();
            List<Class<?>> types = new ArrayList<Class<?>>();
            List<Object> values = new ArrayList<Object>();

            // Add Channel Name column
            names.add("Name");
            types.add(String.class);
            values.add(Lists.transform(channels,
                new Function<Channel, String>() {
                @Override
                public String apply(Channel input) {
                    return input.getName();
                }
                }));

            // Add Property Columns
            Collection<String> propertyNames = ChannelUtil
                .getPropertyNames(channels);
            for (final String propertyName : propertyNames) {
            names.add(propertyName);
            types.add(String.class);
            values.add(Lists.transform(channels,
                new Function<Channel, String>() {
                    @Override
                    public String apply(Channel input) {
                    return input.getProperty(propertyName) != null ? input
                        .getProperty(propertyName)
                        .getValue() : "";
                    }
                }));
            }

            // Add Tag Columns
            Collection<String> tagNames = ChannelUtil
                .getAllTagNames(channels);
            for (final String tagName : tagNames) {
            names.add(tagName);
            types.add(String.class);
            values.add(Lists.transform(channels,
                new Function<Channel, String>() {
                    @Override
                    public String apply(Channel input) {
                    return input.getTag(tagName) != null ? "tagged"
                        : "";
                    }
                }));
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("result",
                ValueFactory.newVTable(types, names, values));
            resultMap.put("result_size", result.channels.size());
            callback.writeValue(resultMap);
        }
        }
    });
    channelQuery.refresh();
    }
}
