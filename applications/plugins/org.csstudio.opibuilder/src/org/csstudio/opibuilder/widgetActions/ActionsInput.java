/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.LinkedList;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.ActionsProperty;

/**The value type definition for {@link ActionsProperty}, which describes the input
 * for an actions Property.
 * @author Xihui Chen
 *
 */
public class ActionsInput {

	private LinkedList<AbstractWidgetAction> actionsList;
	
	private boolean hookUpFirstActionToWidget = false;
	
	private boolean hookUpAllActionsToWidget = false;
	
	private AbstractWidgetModel widgetModel;
	
	public ActionsInput(LinkedList<AbstractWidgetAction> actionsList) {
		this.actionsList = actionsList;
	}
	
	public ActionsInput() {
		actionsList = new LinkedList<AbstractWidgetAction>();
	}

	/**
	 * @return the scriptList
	 */
	public LinkedList<AbstractWidgetAction> getActionsList() {
		return actionsList;
	}
	
	public void addAction(AbstractWidgetAction action){
		actionsList.add(action);
		action.setWidgetModel(widgetModel);
	}
	
	/**
	 * @return a total contents copy of this ScriptsInput.
	 */
	public ActionsInput getCopy(){
		ActionsInput copy = new ActionsInput();
		for(AbstractWidgetAction data : actionsList){
			copy.getActionsList().add(data.getCopy());
		}
		copy.setWidgetModel(widgetModel);
		copy.setHookUpFirstActionToWidget(hookUpFirstActionToWidget);
		copy.setHookUpAllActionsToWidget(hookUpAllActionsToWidget);
		return copy;
	}

	/**
	 * @param hookWithWidget the hookWithWidget to set
	 */
	public void setHookUpFirstActionToWidget(boolean hookWithWidget) {
		this.hookUpFirstActionToWidget = hookWithWidget;
	}

	/**
	 * @return the hookWithWidget true if the first action is hooked with the widget's click,
	 * which means click on the widget will activate the first action in the list.
	 */
	public boolean isFirstActionHookedUpToWidget() {
		return hookUpFirstActionToWidget;
	}
	
	@Override
	public String toString() {
		if(actionsList.size() ==0){
			return "no action";
		}
		if(actionsList.size() == 1){
			return actionsList.get(0).getDescription();
		}
		return actionsList.size() + " actions";
	}

	/**
	 * @param widgetModel the widgetModel to set
	 */
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		for(AbstractWidgetAction action : actionsList)
			action.setWidgetModel(widgetModel);
	}

	/**
	 * @return the widgetModel
	 */
	public AbstractWidgetModel getWidgetModel() {
		return widgetModel;
	}

	public boolean isHookUpAllActionsToWidget() {
		return hookUpAllActionsToWidget;
	}

	public void setHookUpAllActionsToWidget(boolean hookUpAllActionsToWidget) {
		this.hookUpAllActionsToWidget = hookUpAllActionsToWidget;
	}
	
	
}
