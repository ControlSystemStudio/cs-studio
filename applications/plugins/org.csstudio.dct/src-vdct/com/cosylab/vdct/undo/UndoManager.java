package com.cosylab.vdct.undo;

import com.cosylab.vdct.graphics.DrawingSurface;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * This type was created in VisualAge.
 */
public class UndoManager {

    private static UndoManager instance = null;

    private final int lowerbound = -1;

    private int pos;
    private int first, last;
    private int bufferSize;
    private boolean bufferSizeReached = false;
    private int savedOnPos = lowerbound;
    private int actionsAfterSave = 0;
    private ActionObject[] actions;

    private boolean monitor = false;

    private ComposedActionInterface composedAction = null;
/**
 * UndoManager constructor comment.
 */
protected UndoManager(int steps2remember) {
    bufferSize = steps2remember;
    actions = new ActionObject[bufferSize];
    instance = this; // to prevent dead-loop reset-getInstance
    reset();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int actions2redo() {
    int redos = 0;
    int np = pos;
    while (np!=last) {
        redos++;
        np = increment(np);
    }
    return redos;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int actions2undo() {
    if (pos==lowerbound) return 0;

    int undos = 1;
    int np = pos;
    while (np!=first) {
        undos++;
        np = decrement(np);
    }
    return undos;
}
/**
 * This method was created in VisualAge.
 * @param action epics.undo.ActionObject
 */
public void addAction(ActionObject action) {

    if (!monitor) return;

    if (composedAction!=null)
    {
        composedAction.addAction(action);
        //System.out.println("Composing: "+action.getDescription());
        return;
    }

    if (actionsAfterSave >= 0 && actionsAfterSave <= bufferSize){
        actionsAfterSave++;
        if (actionsAfterSave >= bufferSize)
            bufferSizeReached = true;
    } else {
        bufferSizeReached = true;
    }

    //System.out.println("New action: "+action.getDescription());
    com.cosylab.vdct.graphics.DrawingSurface.getInstance().setModified(true);

    if (pos==lowerbound) pos=last=increment(pos);
    else {
        pos=last=increment(pos);
        if (last==first) first=increment(first);        // lose first (the "oldest" action)
    }
    actions[pos]=action;

    int np = increment(last);                            // clear lost actions -> finalization!
    while (np!=first) {
        actions[np]=null;
        np=increment(np);
    }

    com.cosylab.vdct.graphics.DSGUIInterface.getInstance().updateMenuItems();
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param pos int
 */
private int decrement(int pos) {
    if (pos==first) return lowerbound;
    else {
        int np = pos-1;
        if (np<0) np=bufferSize-1;
        return np;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:45:26)
 * @return com.cosylab.vdct.undo.ComposedActionInterface
 */
public ComposedActionInterface getComposedAction() {
    return composedAction;
}
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 15:56:37)
 * @return com.cosylab.vdct.undo.UndoManager
 */
public static UndoManager getInstance() {
    if (instance==null) instance = new UndoManager(com.cosylab.vdct.Constants.UNDO_STEPS_TO_REMEMBER);
    return instance;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param pos int
 */
private int increment(int pos) {
    if (pos==lowerbound) return first;
    else return ((pos+1) % bufferSize);
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:36:00)
 * @return boolean
 */
public boolean isMonitor() {
    return monitor;
}
/**
 * This method was created in VisualAge.
 */
public void redo() {
    if (pos!=last) {
        boolean m = monitor;
        monitor = false;
        pos=increment(pos);
        actions[pos].redo();
        //System.out.println("Redo: "+actions[pos].getDescription());
        com.cosylab.vdct.graphics.DrawingSurface.getInstance().setModified(true);
        com.cosylab.vdct.graphics.DSGUIInterface.getInstance().updateMenuItems();
        monitor = m;
        setModification();
        actionsAfterSave++;
    }
}
/**
 * This method was created in VisualAge.
 */
public void reset() {
    first=0;
    pos=last=lowerbound;
    for (int i=0; i < bufferSize; i++)
        actions[i]=null;
    monitor = false;
    bufferSizeReached=false;
    com.cosylab.vdct.graphics.DSGUIInterface.getInstance().updateMenuItems();
    prepareAfterSaving();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 15:36:00)
 * @param newMonitor boolean
 */
public void setMonitor(boolean newMonitor) {
    monitor = newMonitor;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:43:57)
 */
public void startMacroAction() {
    ComposedAction action = new ComposedAction();
    addAction(action);
    this.composedAction = action;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:43:57)
 * @param composedAction com.cosylab.vdct.undo.ComposedActionInterface
 */
public void startMacroAction(ComposedActionInterface composedAction) {
    this.composedAction=composedAction;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 20:44:21)
 */
public void stopMacroAction() {
    composedAction=null;
    //System.out.println("Stopped composing");
}
/**
 * This method was created in VisualAge.
 */
public void undo() {
    if (pos!=lowerbound)  {
        boolean m = monitor;
        monitor = false;
        actions[pos].undo();
        //System.out.println("Undo: "+actions[pos].getDescription());
        pos=decrement(pos);
        com.cosylab.vdct.graphics.DrawingSurface.getInstance().setModified(true);
        com.cosylab.vdct.graphics.DSGUIInterface.getInstance().updateMenuItems();
        monitor = m;
        setModification();
        actionsAfterSave--;
    }
}

/**
 *
 * Sets the modified tag according to the state of the opened template. If the template has
 * (by undoing) became the same as the one that is saved tag is turned to false or
 * else it is true.
 *
 */
private void setModification() {
    if (pos==savedOnPos && !bufferSizeReached) {
        DrawingSurface.getInstance().setModified(false);
    } else {
        DrawingSurface.getInstance().setModified(true);
    }
}

/**
 *
 * Sets all the counters after a file has been saved.
 *
 */
public void prepareAfterSaving() {
    savedOnPos = pos;
    bufferSizeReached = false;
    actionsAfterSave = 0;
}
}
