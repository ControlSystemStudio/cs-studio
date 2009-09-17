package org.csstudio.opibuilder.widgetActions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.ActionsProperty;

/**The value type definition for {@link ActionsProperty}, which describes the input
 * for an actions Property.
 * @author Xihui Chen
 *
 */
public class ActionsInput {

	private List<AbstractWidgetAction> actionsList;
	
	private boolean hookUpToWidget = false;
	
	public ActionsInput(List<AbstractWidgetAction> actionsList) {
		this.actionsList = actionsList;
	}
	
	public ActionsInput() {
		actionsList = new ArrayList<AbstractWidgetAction>();
	}

	/**
	 * @return the scriptList
	 */
	public List<AbstractWidgetAction> getActionsList() {
		return actionsList;
	}
	
	/**
	 * @return a total contents copy of this ScriptsInput.
	 */
	public ActionsInput getCopy(){
		ActionsInput copy = new ActionsInput();
		for(AbstractWidgetAction data : actionsList){
			copy.getActionsList().add(data.getCopy());
		}
		return copy;
	}

	/**
	 * @param hookWithWidget the hookWithWidget to set
	 */
	public void setHookUpToWidget(boolean hookWithWidget) {
		this.hookUpToWidget = hookWithWidget;
	}

	/**
	 * @return the hookWithWidget true if the first action is hooked with the widget's click,
	 * which means click on the widget will activate the first action in the list.
	 */
	public boolean isHookedUpToWidget() {
		return hookUpToWidget;
	}
	
	
}
