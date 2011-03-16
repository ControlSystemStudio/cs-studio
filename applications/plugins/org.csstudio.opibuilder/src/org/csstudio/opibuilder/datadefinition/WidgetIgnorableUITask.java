/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.util.GUIRefreshThread;

/** The element in the {@link GUIRefreshThread}'s task queue. 
 *  It corresponds to a widget property change event. 
 *  An existing task in the queue should be ignored when a new task arrives
 *  that affects the same widget property.
 *  For example, multiple tasks calling Gauge.setValue() are ignorable
 *  since the widget only needs to display the latest value.  
 *  @author Xihui Chen
 *  @author Kay Kasemir Reviewed, made immutable
 */
public class WidgetIgnorableUITask {

	/**
	 * The widget property.
	 */
	final private AbstractWidgetProperty widgetProperty;
	
	/**
	 * The task which will be executed when widget property changed.
	 */
	final private Runnable runnableTask;
	

	public WidgetIgnorableUITask(final AbstractWidgetProperty property, final Runnable runnableTask){
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
	 * @return the runnableTask
	 */
	public Runnable getRunnableTask() {
		return runnableTask;
	}
	
	/** @param obj Possible other {@link WidgetIgnorableUITask}
	 *  @return <code>true</code> if other {@link WidgetIgnorableUITask}
	 *          refers to the same {@link AbstractWidgetProperty}
	 */
	@Override
	public boolean equals(final Object obj) {
		if(obj instanceof WidgetIgnorableUITask)
			return widgetProperty ==((WidgetIgnorableUITask)obj).getWidgetProperty();
		else 
			return false;
	}
	
	@Override
	public String toString() {
		return widgetProperty.toString();
	}
	
	@Override
	public int hashCode() {
		return widgetProperty.hashCode();
	}
	
}
