package org.csstudio.alarm.diirt.datasource;

import java.util.ArrayList;
import java.util.Collection;

import org.diirt.datasource.DataSourceTypeAdapterSet;

public class BeastTypeAdapterSet implements DataSourceTypeAdapterSet {

    private Collection<BeastTypeAdapter> beastTypeAdapter = new ArrayList<BeastTypeAdapter>();

    public BeastTypeAdapterSet() {
        beastTypeAdapter.add(new BeastVTableAdapter());
        beastTypeAdapter.add(new BeastRawVTableAdapter());
        beastTypeAdapter.add(new BeastActiveAdapter());
        beastTypeAdapter.add(new BeastAlarmSeverityAdapter());
        beastTypeAdapter.add(new BeastEnableAdapter());
        beastTypeAdapter.add(new BeastAcknowledgeAdapter());
    }

    @Override
    public Collection<BeastTypeAdapter> getAdapters() {
        return beastTypeAdapter;
    }

}
