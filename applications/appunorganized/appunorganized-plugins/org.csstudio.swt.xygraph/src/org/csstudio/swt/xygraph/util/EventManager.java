/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.util;

import java.util.ArrayList;
import java.util.List;


/**The operation manager will help to manage the undoable and redoable operations.
 * @author Xihui Chen
 *
 */
public class EventManager {

    boolean isScrollingDisabled;

    private List<IEventManagerListener> listeners;

    /**
     * Constructor.
     */
    public EventManager() {
        isScrollingDisabled = false;
        listeners = new ArrayList<IEventManagerListener>();
    }

    public void setScrollingDisabled(boolean isScrollingDisabled_){
        isScrollingDisabled = isScrollingDisabled_;
        fireDataChanged();
    }

    public void addListener(IEventManagerListener listener){
        listeners.add(listener);
    }

    public boolean removeListener(IEventManagerListener listener){
        return listeners.remove(listener);
    }

    private void fireDataChanged(){
        for(IEventManagerListener listener : listeners)
            listener.dataChanged(this);
    }

    public boolean isScrollingDisabled() {
        return isScrollingDisabled;
    }
}
