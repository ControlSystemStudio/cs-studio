package org.csstudio.trends.databrowser2;

import org.csstudio.rap.core.DisplayManager;
import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.CompositeDataSourceConfiguration;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class WorkbenchDataBrowserRuntimeEntryPoint implements EntryPoint {

    private static final String DEFAULT_DATASOURCE_NAME = "ca";

    public int createUI() {
        initDefaultDatasource();
        Display display = PlatformUI.createDisplay();
        DisplayManager.getInstance().registerDisplay(display, true);
        WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
        int result = PlatformUI.createAndRunWorkbench( display, advisor );
        display.dispose();
        return result;

    }

    public static void initDefaultDatasource() {
        DataSource dataSource = PVManager.getDefaultDataSource();
        if (dataSource instanceof CompositeDataSource) {
            CompositeDataSourceConfiguration conf = ((CompositeDataSource)dataSource).getConfiguration();
            conf.defaultDataSource(DEFAULT_DATASOURCE_NAME);
        }
    }
}
