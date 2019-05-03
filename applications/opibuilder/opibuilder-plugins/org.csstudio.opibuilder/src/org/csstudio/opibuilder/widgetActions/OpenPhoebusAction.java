/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;


/**
 * Open the given resource (.bob or .plt files) using their default applications in phoebus
 * @author Kunal Shroff
 *
 */
public class OpenPhoebusAction extends AbstractWidgetAction
{
    public static final String PROP_PATH = "path";
    public static final String PROP_MACROS = "macros";

    @Override
    protected void configureProperties()
    {
        addProperty(new FilePathProperty(PROP_PATH,
                    "File Path",
                    WidgetPropertyCategory.Basic,
                    new Path(""),
                    PreferencesHelper.supportedResourcesPhoebus(),
                    false));
        addProperty(new MacrosProperty(PROP_MACROS,
                    "Macros",
                    WidgetPropertyCategory.Basic,
                    new MacrosInput(new LinkedHashMap<String, String>(), true)));
    }

    public void runWithMacros()
    {
        // Determine absolute path
        // TODO Do this in RuntimeDelegate, after settling View-or-Editor
        IPath absolutePath = getPath();
        if (!absolutePath.isAbsolute())
        {
            absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(), getPath());
            if (!ResourceUtil.isExsitingFile(absolutePath, true))
            {
                //search from OPI search path
                absolutePath = ResourceUtil.getFileOnSearchPath(getPath(), true);
            }
        }
        if (absolutePath != null  &&  ResourceUtil.isExsitingFile(absolutePath, true))
        {
            try {
                File file = ResourceUtil.getFile(absolutePath);
                URI uri = file.toURI();
                LinkedHashMap<String, String> macros = getMacrosInput().getMacrosMap();
                String query = macros.entrySet().stream().map((e) -> {
                    return e.getKey().strip() + "=" + e.getValue().strip();
                }).collect(Collectors.joining("&"));
                URI uri_with_macros = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
                PhoebusLauncherService.launchResource(uri_with_macros.toString());
            } catch (Exception e) {
                final String error = NLS.bind("Error {0} opening file {1} does not exist.",
                        e.getLocalizedMessage(),
                        getPath().toString());
                ConsoleService.getInstance().writeError(error);
                MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error", error);
            
            }
        }
        else
        {
            final String error = NLS.bind("The file {0} does not exist.", getPath().toString());
            ConsoleService.getInstance().writeError(error);
            MessageDialog.openError(Display.getDefault().getActiveShell(), "File Open Error", error);
        }
    }

    @Override
    public void run()
    {
        runWithMacros();
    }

    protected IPath getPath()
    {
        return (IPath) getPropertyValue(PROP_PATH);
    }

    protected MacrosInput getMacrosInput()
    {
        MacrosInput result = new MacrosInput(new LinkedHashMap<String, String>(), true);

        MacrosInput macrosInput = ((MacrosInput) getPropertyValue(PROP_MACROS))
                .getCopy();

        if (macrosInput.isInclude_parent_macros()) {
            Map<String, String> macrosMap = getWidgetModel() instanceof AbstractContainerModel ? ((AbstractContainerModel) getWidgetModel())
                    .getParentMacroMap() : getWidgetModel().getParent()
                    .getMacroMap();
            result.getMacrosMap().putAll(macrosMap);
        }
        result.getMacrosMap().putAll(macrosInput.getMacrosMap());
        return result;
    }

    @Override
    public ActionType getActionType()
    {
        return ActionType.OPEN_PHOEBUS;
    }

    @Override
    public String getDefaultDescription()
    {
        return "Open Phoebus for " + getPath().toOSString();
    }
}
