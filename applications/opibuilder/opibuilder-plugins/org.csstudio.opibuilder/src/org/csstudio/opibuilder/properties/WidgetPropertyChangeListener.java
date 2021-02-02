/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.datadefinition.WidgetUITask;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Display;

/**
 * The listener on widget property change.
 *
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class WidgetPropertyChangeListener implements PropertyChangeListener {

    private AbstractBaseEditPart editpart;
    private AbstractWidgetProperty widgetProperty;
    private List<IWidgetPropertyChangeHandler> handlers;

    /**Constructor.
     * @param editpart backlint to the editpart, which uses this listener.
     */
    public WidgetPropertyChangeListener(AbstractBaseEditPart editpart,
            AbstractWidgetProperty property) {
        this.editpart = editpart;
        this.widgetProperty = property;
        handlers = new ArrayList<IWidgetPropertyChangeHandler>();
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        Runnable runnable = new Runnable() {
            public synchronized void run() {
                if(editpart == null || !editpart.isActive()){
                    return;
                }
                for(IWidgetPropertyChangeHandler h : handlers) {
                    IFigure figure = editpart.getFigure();
                    h.handleChange(
                            evt.getOldValue(), evt.getNewValue(), figure);

                }
            }
        };
        Display display = editpart.getViewer().getControl().getDisplay();
        if (editpart.isIgnorableUiTask()) {
            WidgetIgnorableUITask task = new WidgetIgnorableUITask(widgetProperty, runnable, display);
            GUIRefreshThread.getInstance(
                    editpart.getExecutionMode() == ExecutionMode.RUN_MODE)
                    .addIgnorableTask(task);
        } else {
            WidgetUITask task = new WidgetUITask(widgetProperty, runnable, display);
            GUIRefreshThread.getInstance(
                    editpart.getExecutionMode() == ExecutionMode.RUN_MODE)
                    .addTask(task);
        }
    }

    /**Add handler, which is informed when a property changed.
     * @param handler
     */
    public void addHandler(final IWidgetPropertyChangeHandler handler) {
        assert handler != null;
        handlers.add(handler);
    }

    public void removeAllHandlers(){
        handlers.clear();
    }

}
