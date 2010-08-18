package org.csstudio.sds.ui.widgetactionhandler;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.trends.IStripTool;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.model.properties.actions.OpenDataBrowserActionModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;

public class OpenDataBrowserActionHandler implements IWidgetActionHandler {

    private static final String STRIP_TOOL_ID = "org.csstudio.platform.ui.striptool";

    public void executeAction(AbstractWidgetModel widget, AbstractWidgetActionModel action) {
        assert action instanceof OpenDataBrowserActionModel : "action instanceof OpenDataBrowserActionModel";

    OpenDataBrowserActionModel dataBrowserModel = (OpenDataBrowserActionModel) action;
    IPath path = dataBrowserModel.getResource();
    
    CentralLogger.getInstance().debug(this, "OpenDataBrowserActionHandler.executeAction()");
    CentralLogger.getInstance().debug(this, "Open " + path.lastSegment() + " in Data Browser");
    
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(STRIP_TOOL_ID);
        // Allow one and only implementation
        if (configs.length < 1) {
            MessageDialog.openWarning(null, "No Strip Tool Available", "No Strip Tool Available!");
            CentralLogger.getInstance().warn(this, "No Strip Tool Class was found!");
            return;
        }
        for (IConfigurationElement config : configs) {
            IStripTool stripTool;
            try {
                stripTool = (IStripTool) config.createExecutableExtension("class");
                stripTool.openView(file);
            } catch (CoreException e) {
                MessageDialog.openWarning(null, "No Strip Tool Available", "No Strip Tool Class was found!");
                CentralLogger.getInstance().warn(this, "No Strip Tool Class was found!",e);
            }
        }

    }

}
