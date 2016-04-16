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
import java.util.function.Consumer;

import org.diirt.service.ServiceDescription;
import org.diirt.service.ServiceMethod;
import org.diirt.service.ServiceMethodDescription;
import org.diirt.vtype.VString;
import org.diirt.vtype.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author shroffk
 *
 */
public class QueryServiceMethod extends ServiceMethod {

    /**
     */
    public QueryServiceMethod(ServiceMethodDescription serviceMethodDescription, ServiceDescription serviceDescription) {
        super(serviceMethodDescription, serviceDescription);
    }

    @Override
    public void executeAsync(Map<String, Object> parameters,
        final Consumer<Map<String, Object>> callback,
        final Consumer<Exception> errorCallback) {
    String query = ((VString) parameters.get("query")).getValue();
    ChannelQuery channelQuery = ChannelQuery.query(query).build();
    channelQuery.addChannelQueryListener(new ChannelQueryListener() {

        @Override
        public void queryExecuted(final Result result) {
        if (result.exception != null) {
            errorCallback.accept(result.exception);
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
            callback.accept(resultMap);
        }
        }
    });
    channelQuery.refresh();
    }
}
