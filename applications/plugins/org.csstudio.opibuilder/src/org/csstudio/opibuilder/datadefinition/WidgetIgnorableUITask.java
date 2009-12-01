package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.util.GUIRefreshThread;

/**The element in the queue of {@link GUIRefreshThread}. 
 * It corresponds to the widget property change event. 
 * The old task in the queue could be ignored if there is a new task with same 
 * widget property arrived. For example, the task calling Gauge.setValue() is ignorable
 * since the widget only need to display the latest value.  
 * @author Xihui Chen
 *
 */
public class WidgetIgnorableUITask {

	/**
	 * The widget property.
	 */
	private AbstractWidgetProperty widgetProperty;
	
	/**
	 * The task which will be executed when widget property changed.
	 */
	private Runnable runnableTask;
	

	public WidgetIgnorableUITask(AbstractWidgetProperty property, Runnable runnableTask){
		this.widgetProperty = property;
		this.runnableTask = runnableTask;
	}

	

	/**
	 * @return the widgetProperty
	 */
	public AbstractWidgetProperty getWidgetProperty() {
		return widgetProperty;
	}


	/**
	 * @param widgetProperty the widgetProperty to set
	 */
	public void setWidgetProperty(AbstractWidgetProperty widgetProperty) {
		this.widgetProperty = widgetProperty;
	}


	/**
	 * @return the runnableTask
	 */
	public Runnable getRunnableTask() {
		return runnableTask;
	}

	/**
	 * @param runnableTask the runnableTask to set
	 */
	public void setRunnableTask(Runnable runnableTask) {
		this.runnableTask = runnableTask;
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WidgetIgnorableUITask)
			return widgetProperty ==((WidgetIgnorableUITask)obj).getWidgetProperty();
		else 
			return false;
	}
	
	
	
}
