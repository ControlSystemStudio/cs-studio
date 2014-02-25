/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;

/**The action executing a system command.
 * @author Xihui Chen
 *
 */
public class ExecuteCommandAction extends AbstractWidgetAction {

	private static final String OPI_DIR = "opi.dir"; //$NON-NLS-1$
	public final static String PROP_COMMAND = "command"; //$NON-NLS-1$
	public final static String PROP_DIRECTORY = "command_directory"; //$NON-NLS-1$
	public final static String PROP_WAIT_TIME = "wait_time"; //$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(
				PROP_COMMAND, "Command", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(
				PROP_DIRECTORY, "Command Directory[path]", WidgetPropertyCategory.Basic, "$(user.home)"));
		addProperty(new IntegerProperty(
				PROP_WAIT_TIME, "Wait Time(s)", WidgetPropertyCategory.Basic, 10, 1, Integer.MAX_VALUE));
		
	}

	@Override
	public ActionType getActionType() {
		return ActionType.EXECUTE_CMD;
	}

	@Override
	public void run() {
		ConsoleService.getInstance().writeInfo("Execute Command: " + getCommand());
		new CommandExecutor(getCommand(), getDirectory(), getWaitTime());
		
	}
	
	public String getCommand(){
		return (String)getPropertyValue(PROP_COMMAND);
	}
	
	public String getDirectory(){
		String directory = (String)getPropertyValue(PROP_DIRECTORY);
		try {
			return replaceProperties(directory);
		} catch (Exception e) {
			ConsoleService.getInstance().writeError(e.getMessage());
		}
		return  directory;
	}

	public int getWaitTime(){
		return (Integer)getPropertyValue(PROP_WAIT_TIME);
	}
	

    /** @param value Value that might contain "$(prop)"
     *  @return Value where "$(prop)" is replaced by Java system property "prop"
     *  @throws Exception on error
     */
    private String replaceProperties(final String value) throws Exception
    {
        final Matcher matcher = Pattern.compile("\\$\\((.*)\\)").matcher(value);
        if (matcher.matches())
        {
            final String prop_name = matcher.group(1);
            String prop = System.getProperty(prop_name);
            if(prop==null && prop_name.equals(OPI_DIR)){
            	IPath opiFilePath = getWidgetModel().getRootDisplayModel().getOpiFilePath();
            	if(ResourceUtil.isExistingWorkspaceFile(opiFilePath))
            		opiFilePath = ResourceUtil.workspacePathToSysPath(opiFilePath);
				prop=opiFilePath.removeLastSegments(1).toOSString();
            }
            
            if (prop == null)
                throw new Exception("Property '" + prop_name + "' is not defined");
            return prop;
        }
        // Return as is
        return value;
    }
    
    @Override
    public String getDefaultDescription() {
    	return super.getDefaultDescription() + " " + getCommand(); //$NON-NLS-1$
    }
}
