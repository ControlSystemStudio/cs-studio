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
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.OpenRelatedDisplayAction.OpenDisplayTarget;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.OPIShell;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.jdom.Element;

/**
 * The action running another OPI file.
 * 
 * @author Xihui Chen - Original author
 * @author Will Rogers - Shell support
 * @author Kay Kasemir - Consolidated AbstractOpenOPIAction/OpenOPIInView into this
 */
public class OpenDisplayAction extends AbstractWidgetAction {
    public static final String PROP_PATH = "path";//$NON-NLS-1$
    public static final String PROP_MACROS = "macros";//$NON-NLS-1$

    // TODO Merge PROP_REPLACE and PROP_POSITION into one 'mode'
	public static final String PROP_REPLACE = "replace";//$NON-NLS-1$
    public static final String PROP_POSITION = "Position";//$NON-NLS-1$

    protected boolean ctrlPressed = false;
    protected boolean shiftPressed = false;

    /**
     * @param ctrlPressed
     *            the ctrlPressed to set
     */
    public final void setCtrlPressed(boolean ctrlPressed) {
        this.ctrlPressed = ctrlPressed;
    }

    /**
     * @param shiftPressed
     *            the shiftPressed to set
     */
    public final void setShiftPressed(boolean shiftPressed) {
        this.shiftPressed = shiftPressed;
    }

    
	@Override
	protected void configureProperties() {
        addProperty(new FilePathProperty(PROP_PATH, "File Path",
                WidgetPropertyCategory.Basic, new Path(""),
                new String[] { "opi" }, false)); //$NON-NLS-1$
        addProperty(new MacrosProperty(PROP_MACROS, "Macros",
                WidgetPropertyCategory.Basic, new MacrosInput(
                        new LinkedHashMap<String, String>(), true)));
		addProperty(new ComboProperty(PROP_REPLACE, "Target",
				WidgetPropertyCategory.Basic, OpenDisplayTarget.stringValues(), 0){
			@Override
			public Object readValueFromXML(Element propElement) {
				try {
					Integer index = Integer.parseInt(propElement.getValue());
					return index;
				} catch (NumberFormatException e) {
				    // Fall back for older files that stored True/false
					boolean b = Boolean.parseBoolean(propElement.getValue());
					return b?new Integer(1): new Integer(0);
				}
			}
		});
		
        addProperty(new ComboProperty(PROP_POSITION, "Position", WidgetPropertyCategory.Basic,
	                Position.stringValues(), Position.DEFAULT_VIEW.ordinal()));
	}

	protected void openOPI(IPath absolutePath) {
		if (!ctrlPressed && !shiftPressed && getOpenDisplayTarget() == OpenDisplayTarget.DEFAULT) {
			IOPIRuntime opiRuntime = getWidgetModel().getRootDisplayModel()
					.getOpiRuntime();
			if (opiRuntime instanceof OPIShell) {
				// Default behaviour for OPIShell is to open another OPIShell.
				OPIShell.openOPIShell(absolutePath, getMacrosInput());
			} else {
				// Default behaviour for OPIView is to replace current OPIView.
				DisplayOpenManager manager = (DisplayOpenManager) (opiRuntime
					.getAdapter(DisplayOpenManager.class));
				manager.openNewDisplay();
				try {
				    // Open View in desired position
				    if (getPosition() != Position.DEFAULT_VIEW)
				        RunModeService.runOPIInView(absolutePath, null, getMacrosInput(), getPosition());
				    else
				        RunModeService.replaceOPIRuntimeContent(opiRuntime, new RunnerInput(
								absolutePath, manager, getMacrosInput()));
				} catch (PartInitException e) {
					OPIBuilderPlugin.getLogger().log(Level.WARNING,
							"Failed to open " + absolutePath, e); //$NON-NLS-1$
					MessageDialog.openError(Display.getDefault().getActiveShell(),
							"Open file error",
							NLS.bind("Failed to open {0}", absolutePath));
				}
			}
		} else {
			TargetWindow target;
			if(!ctrlPressed && !shiftPressed){
				switch (getOpenDisplayTarget()) {
				case NEW_TAB:
					target = TargetWindow.SAME_WINDOW;
					break;
				case NEW_WINDOW:
					target = TargetWindow.NEW_WINDOW;
					break;
				default:
					target = TargetWindow.SAME_WINDOW;
					break;
				}
			}else if (shiftPressed && !ctrlPressed) {
				target = TargetWindow.NEW_WINDOW;
			} else if (ctrlPressed && !shiftPressed) {
				target = TargetWindow.SAME_WINDOW;
			} else {  // ctrl and shift pressed
				target = TargetWindow.NEW_SHELL;
			}
			RunModeService.getInstance().runOPI(absolutePath, target, null,
					getMacrosInput(), null);
			
		}
	}
	
	   @Override
	    public void run()
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
	            openOPI(absolutePath);
	        else
	        {
	            final String error = NLS.bind("The file {0} does not exist.", getPath().toString());
	            ConsoleService.getInstance().writeError(error);
	            MessageDialog.openError(Display.getDefault().getActiveShell(),
	                        "File Open Error", error);
	        }
	    }



    protected IPath getPath() {
        return (IPath) getPropertyValue(PROP_PATH);
    }

    protected MacrosInput getMacrosInput() {
        MacrosInput result = new MacrosInput(
                new LinkedHashMap<String, String>(), false);

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

	private OpenDisplayTarget getOpenDisplayTarget() {
		int index = (Integer) getPropertyValue(PROP_REPLACE);
		return OpenDisplayTarget.values()[index];
	}

    protected Position getPosition(){
        return Position.values()[(Integer)getPropertyValue(PROP_POSITION)];
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_DISPLAY;
	}

	@Override
	public String getDefaultDescription() {
		return "Open " + getPath();
	}

}
