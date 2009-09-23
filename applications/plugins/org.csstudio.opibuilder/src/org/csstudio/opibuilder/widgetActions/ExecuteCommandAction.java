package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;

public class ExecuteCommandAction extends AbstractWidgetAction {

	private final static String PROP_COMMAND = "command";
	private final static String PROP_DIRECTORY = "command_directory";
	private final static String PROP_WAIT_TIME = "wait_time";
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(
				PROP_COMMAND, "Command", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(
				PROP_DIRECTORY, "Command Directory[path]", WidgetPropertyCategory.Basic, ""));
		addProperty(new IntegerProperty(
				PROP_WAIT_TIME, "Wait Time(s)", WidgetPropertyCategory.Basic, 10, 1, Integer.MAX_VALUE));
		
	}

	@Override
	public ActionType getActionType() {
		return ActionType.EXECUTE_CMD;
	}

	@Override
	public void run() {
		
		
	}
	
	public String getCommand(){
		return (String)getPropertyValue(PROP_COMMAND);
	}
	
	public String getDirectory(){
		return (String)getPropertyValue(PROP_DIRECTORY);
	}

	public int getWaitTime(){
		return (Integer)getPropertyValue(PROP_WAIT_TIME);
	}
}
