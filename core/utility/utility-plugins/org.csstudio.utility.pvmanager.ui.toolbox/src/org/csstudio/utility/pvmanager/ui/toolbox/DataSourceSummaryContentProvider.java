package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;

public class DataSourceSummaryContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing to do
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement == DataSourceContentProvider.ALL) {
            CompositeDataSource composite = (CompositeDataSource) PVManager.getDefaultDataSource();
            List<String> dataSourceNames = new ArrayList<String>(composite.getDataSources().keySet());
            Collections.sort(dataSourceNames);
            Object[] dataSources = new Object[dataSourceNames.size()];
            for (int i = 0; i < dataSources.length; i++) {
                dataSources[i] = composite.getDataSources().get(dataSourceNames.get(i));
            }
            return dataSources;
        } else {
            String dataSourceName = (String) inputElement;
            DataSource dataSource = ((CompositeDataSource) PVManager.getDefaultDataSource()).getDataSources().get(dataSourceName);
            return new Object[] {dataSource};
        }
    }

    private void addChannels(List<DataSourceChannel> channels, String dataSourceName, DataSource dataSource) {
        for (ChannelHandler channelHandler : dataSource.getChannels().values()) {
            channels.add(new DataSourceChannel(dataSourceName, channelHandler));
        }
    }

}
