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
import org.eclipse.swt.widgets.Display;

/** The element in the {@link GUIRefreshThread}'s task queue.
 *  An existing task in the queue should be ignored when a new task arrives
 *  that has the same identifyObject.
 *  For example, multiple tasks calling Gauge.setValue() are ignorable
 *  since the widget only needs to display the latest value.
 *  @author Xihui Chen
 *  @author Kay Kasemir Reviewed, made immutable
 */
public class WidgetIgnorableUITask extends WidgetUITask {

    /**Constructor.
     * @param identifyObject the object that identifies this task. If the task associated
     * with the same identifyObject has not been executed, it will be ignored.
     * @param runnableTask the task to be executed.
     * @param display Associated Display.
     */
    public WidgetIgnorableUITask(final Object identifyObject, final Runnable runnableTask,
            final Display display){
        super(identifyObject, runnableTask, display);
    }


    /** @param obj Possible other {@link WidgetIgnorableUITask}
     *  @return <code>true</code> if other {@link WidgetIgnorableUITask}
     *          refers to the same {@link AbstractWidgetProperty}
     */
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof WidgetIgnorableUITask)
            return identifyObject ==((WidgetIgnorableUITask)obj).getIdentifyObject();
        else
            return false;
    }

    @Override
    public String toString() {
        return identifyObject.toString();
    }

    @Override
    public int hashCode() {
        return identifyObject.hashCode();
    }

}
