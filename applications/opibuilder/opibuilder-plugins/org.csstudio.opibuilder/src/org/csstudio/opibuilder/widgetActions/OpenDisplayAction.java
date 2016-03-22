/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.jdom.Element;

/** Action for executing a display.
 *
 *  <p>Properties, configured when the action is added to a widget,
 *  suggest how the display should be opened:
 *  Replace existing display, or open new standalone shell, or ..
 *
 *  <p>User can override via key modifiers:
 *  <ul>
 *  <li>Ctrl - Opens a new view in existing workbench window
 *  <li>Shift - Open a new view in new workbench window
 *  <li>Ctrl + Shift - Open a new standalone shell
 *  </ul>
 *
 *  These key modifiers need to be passed by the context menu or
 *  whatever usually invokes the 'run()' method by calling the specialized
 *  runWithModifiers()
 *
 *  @author Xihui Chen - Original author
 *  @author Will Rogers - Shell support
 *  @author Kay Kasemir
 */
public class OpenDisplayAction extends AbstractWidgetAction
{
    public static final String PROP_PATH = "path";
    public static final String PROP_MACROS = "macros";
    public static final String PROP_MODE = "mode";

    @Override
    protected void configureProperties()
    {
        addProperty(new FilePathProperty(PROP_PATH, "File Path",
                    WidgetPropertyCategory.Basic, new Path(""),
                    new String[] { "opi" }, false)
        {
            @Override
            public Object readValueFromXML(Element propElement)
            {
                handleLegacySettings(propElement);
                return super.readValueFromXML(propElement);
            }
        });
        addProperty(new MacrosProperty(PROP_MACROS, "Macros",
                    WidgetPropertyCategory.Basic, new MacrosInput(
                    new LinkedHashMap<String, String>(), true)));
        addProperty(new ComboProperty(PROP_MODE, "Mode",
                    WidgetPropertyCategory.Basic, DisplayMode.stringValues(),
                    DisplayMode.REPLACE.ordinal()));
    }

    protected void handleLegacySettings(final Element path_element)
    {
        // Original OpenDisplayAction had property "replace".
        // True  - Replace existing display
        // False - Open new display

        // Later OpenDisplayAction had property "replace" with options
        // 0 - NEW_TAB,
        // 1 - REPLACE,
        // 2 - NEW_WINDOW.
        // On branch, this was added
        // (and 0/1 were swapped, but we ignore that
        //  because it's incompatible with older displays):
        // 3 - NEW_SHELL

        // Original OpenOPIInViewAction had property "Position"
        // 0 - LEFT,
        // 1 - RIGHT,
        // 2 - TOP,
        // 3 - BOTTOM,
        // 4 - DETACHED,
        // 5 - DEFAULT_VIEW

        // This OpenDisplayAction has a property "mode" that combines all of the above.
        // For legacy displays, hook into loading the "path" property which was found
        // in all versions and navigate the XML for older properties.
        final Element action_element = path_element.getParentElement();
        // action_element.getName() should be "action"

        Element legacy = action_element.getChild("Position");
        if (legacy != null)
        {
            switch (Integer.parseInt(legacy.getValue()))
            {
            case 0:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB_LEFT.ordinal());
                break;
            case 1:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB_RIGHT.ordinal());
                break;
            case 2:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB_TOP.ordinal());
                break;
            case 3:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB_BOTTOM.ordinal());
                break;
            case 4:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB_DETACHED.ordinal());
                break;
            case 5:
                setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB.ordinal());
                break;
            default:
            }
        }

        legacy = action_element.getChild("replace");
        if (legacy != null)
        {
            try
            {
                switch (Integer.parseInt(legacy.getValue()))
                {
                case 0:
                    setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB.ordinal());
                    break;
                case 2:
                    setPropertyValue(PROP_MODE, DisplayMode.NEW_WINDOW.ordinal());
                    break;
                case 3:
                    setPropertyValue(PROP_MODE, DisplayMode.NEW_SHELL.ordinal());
                    break;
                default:
                    setPropertyValue(PROP_MODE, DisplayMode.REPLACE.ordinal());
                }
            }
            catch (NumberFormatException e)
            {   // Fall back for older files that stored True/false
                if (Boolean.parseBoolean(legacy.getValue()))
                    setPropertyValue(PROP_MODE, DisplayMode.NEW_TAB.ordinal());
            }
        }
    }

    private void openOPI(final IPath absolutePath, final boolean ctrlPressed, final boolean shiftPressed)
    {
        DisplayMode mode = getDisplayMode();

        if (ctrlPressed && !shiftPressed)
            mode = DisplayMode.NEW_TAB;
        else if (!ctrlPressed && shiftPressed)
            mode = DisplayMode.NEW_WINDOW;
        else if (ctrlPressed && shiftPressed)
            mode = DisplayMode.NEW_SHELL;

        final IOPIRuntime runtime =
                getWidgetModel().getRootDisplayModel().getOpiRuntime();
        RunModeService.openDisplay(absolutePath, Optional.ofNullable(getMacrosInput()), mode, Optional.ofNullable(runtime));
    }

    /** Run the action, i.e. open display, with optional modifiers
     *
     *  @param ctrlPressed True if Ctrl was pressed while invoking the action
     *  @param shiftPressed True if Shift was held while invoking the action
     */
    public void runWithModifiers(final boolean ctrlPressed, final boolean shiftPressed)
    {
        // Determine absolute path
        // TODO Do this in RuntimeDelegate, after settling View-or-Editor
        IPath absolutePath = getPath();
        if (!absolutePath.isAbsolute())
        {
            absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(),
                    getPath());
            if (!ResourceUtil.isExsitingFile(absolutePath, true))
            {
                //search from OPI search path
                absolutePath = ResourceUtil.getFileOnSearchPath(getPath(), true);
            }
        }
        if (absolutePath != null  &&  ResourceUtil.isExsitingFile(absolutePath, true))
            openOPI(absolutePath, ctrlPressed, shiftPressed);
        else
        {
            final String error = NLS.bind("The file {0} does not exist.", getPath().toString());
            ConsoleService.getInstance().writeError(error);
            MessageDialog.openError(Display.getDefault().getActiveShell(),
                    "File Open Error", error);
        }
    }

    @Override
    public void run()
    {
        runWithModifiers(false, false);
    }

    protected IPath getPath()
    {
        return (IPath) getPropertyValue(PROP_PATH);
    }

    protected MacrosInput getMacrosInput()
    {
        MacrosInput result = new MacrosInput(
                new LinkedHashMap<String, String>(), true);

        MacrosInput macrosInput = ((MacrosInput) getPropertyValue(PROP_MACROS))
                .getCopy();

        if (macrosInput.isInclude_parent_macros()) {
            Map<String, String> macrosMap = getWidgetModel() instanceof AbstractContainerModel ?
                            ((AbstractContainerModel) getWidgetModel()).getParentMacroMap() :
                            getWidgetModel().getParent().getMacroMap();
            result.getMacrosMap().putAll(macrosMap);
        }
        result.getMacrosMap().putAll(macrosInput.getMacrosMap());
        return result;
    }

    protected DisplayMode getDisplayMode()
    {
        return DisplayMode.values()[(Integer)getPropertyValue(PROP_MODE)];
    }

    @Override
    public ActionType getActionType()
    {
        return ActionType.OPEN_DISPLAY;
    }

    @Override
    public String getDefaultDescription()
    {
        return "Open " + getPath();
    }
}
