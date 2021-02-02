/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.datadefinition;

import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.eclipse.swt.widgets.Display;

/** The element in the {@link GUIRefreshThread}'s task queue.
 *  @author Pedro Ramirez
 */
public class WidgetUITask {

    /**
     * The widget property.
     */
    final protected Object identifyObject;

    /**
     * The task to be executed.
     */
    final protected Runnable runnableTask;

    final protected Display display;

    /** Constructor.
     * @param identifyObject the object that identifies this task. If the task associated
     * with the same identifyObject has not been executed, it will be ignored.
     * @param runnableTask the task to be executed.
     * @param display Associated Display.
     */
    public WidgetUITask(final Object identifyObject,
            final Runnable runnableTask, final Display display){
        this.identifyObject = identifyObject;
        this.runnableTask = runnableTask;
        this.display = display;
    }

    public Display getDisplay(){
        return this.display;
    }

    /**
     * @return the identify object
     */
    public Object getIdentifyObject(){
        return this.identifyObject;
    }

    /**
     * @return the runnableTask
     */
    public Runnable getRunnableTask(){
        return this.runnableTask;
    }

}
