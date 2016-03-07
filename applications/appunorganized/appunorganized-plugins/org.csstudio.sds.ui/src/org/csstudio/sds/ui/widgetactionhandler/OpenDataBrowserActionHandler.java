package org.csstudio.sds.ui.widgetactionhandler;

import org.csstudio.desy.startuphelper.IStripTool;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenDataBrowserActionHandler implements IWidgetActionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenDataBrowserActionHandler.class);

    private static final String STRIP_TOOL_ID = "org.csstudio.platform.ui.striptool";

    @Override
    public void executeAction(AbstractWidgetModel widget, AbstractWidgetActionModel action) {
        assert action instanceof OpenDataBrowserActionModel : "action instanceof OpenDataBrowserActionModel";

    OpenDataBrowserActionModel dataBrowserModel = (OpenDataBrowserActionModel) action;
    IPath path = dataBrowserModel.getResource();

    LOG.debug("OpenDataBrowserActionHandler.executeAction()");
    LOG.debug("Open " + path.lastSegment() + " in Data Browser");

    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(STRIP_TOOL_ID);
        // Allow one and only implementation
        if (configs.length < 1) {
            MessageDialog.openWarning(null, "No Strip Tool Available", "No Strip Tool Available!");
            LOG.warn("No Strip Tool Class was found!");
            return;
        }
        for (IConfigurationElement config : configs) {
            IStripTool stripTool;
            try {
                stripTool = (IStripTool) config.createExecutableExtension("class");
                stripTool.openView(file);
            } catch (CoreException e) {
                MessageDialog.openWarning(null, "No Strip Tool Available", "No Strip Tool Class was found!");
                LOG.warn("No Strip Tool Class was found!",e);
            }
        }

    }

}
