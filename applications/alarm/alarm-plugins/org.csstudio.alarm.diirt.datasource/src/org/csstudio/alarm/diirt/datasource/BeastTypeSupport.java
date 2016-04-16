/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.List;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.DataSourceTypeSupport;
import org.diirt.datasource.ValueCache;

/**
 * @author Kunal Shroff
 *
 */
public class BeastTypeSupport extends DataSourceTypeSupport {

    private final BeastTypeAdapterSet adapters;

    public BeastTypeSupport() {
        this.adapters = new BeastTypeAdapterSet();
    }

    public BeastTypeAdapter find(ValueCache<?> cache, BeastConnectionPayload channel){
        System.out.println("FINDING adapters");
        return find(adapters.getAdapters(), cache, channel);
    }
    @Override
    protected String formatMessage(ValueCache<?> cache, Object connection,
            int match,
            List<? extends DataSourceTypeAdapter<?, ?>> matchedConverters) {
        // TODO Auto-generated method stub
        return super.formatMessage(cache, connection, match, matchedConverters);
    }
}
