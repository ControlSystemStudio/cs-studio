package com.cosylab.vdct.inspector;

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

import java.awt.Frame;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (8.1.2001 21:35:03)
 * @author Matej Sekoranja
 * !!! inspectors are not disposed !!!! (move ins.listeners in inspectors, when this is implemented);
 */
public class InspectorManager {

    private static InspectorManager instance = null;
    private static Frame parent = null;

    private Vector inspectors;
/**
 * InspectorManager constructor comment.
 */
protected InspectorManager() {
    inspectors = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:52:28)
 * @return com.cosylab.vdct.inspector.InspectorInterface
 */
private InspectorInterface createInspector() {
    Inspector inspector = new Inspector(parent);
    com.cosylab.vdct.DataProvider.getInstance().addInspectableListener(inspector);
    return inspector;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:39:00)
 */
public void disposeAllInspectors() {
    Object obj;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        ((InspectorInterface)obj).dispose();
        com.cosylab.vdct.DataProvider.getInstance().removeInspectableListener((InspectableObjectsListener)obj);
    }
    inspectors.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 14:27:46)
 * @param inspector com.cosylab.vdct.inspector.InspectorInterface
 */
public void fucusGained(InspectorInterface inspector) {
    if (inspectors.contains(inspector) &&
        (inspectors.firstElement()!=inspector)) {
        inspectors.removeElement(inspector);
        inspectors.insertElementAt(inspector, 0);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:24:34)
 * @return com.cosylab.vdct.inspector.InspectorInterface
 */
public InspectorInterface getActiveInspector() {
    return (InspectorInterface)inspectors.firstElement();
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:36:49)
 * @return com.cosylab.vdct.inspector.InspectorManager
 */
public static InspectorManager getInstance() {
    if (instance==null) instance = new InspectorManager();
    return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 16:00:49)
 * @return java.awt.Frame
 */
public static java.awt.Frame getParent() {
    return parent;
}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 14:20:50)
 * @return boolean
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public boolean isInspected(Inspectable object) {

    InspectorInterface inspector = null;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (inspector.getInspectedObject()==object)
            return true;
    }

    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:37:54)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void requestInspectorFor(Inspectable object) {

    // search for it
    InspectorInterface inspector = null;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (inspector.getInspectedObject()==object) {
            inspector.setVisible(true);
            return;
        }
    }

    // search for first unfrozen
    e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (!inspector.isFrozen()) {
            inspector.inspectObject(object);
            return;
        }
    }

    // otherwise create a new instance
    inspector = createInspector();
    inspectors.addElement(inspector);
    inspector.inspectObject(object);
    inspector.setVisible(true);            // bug fix
}

/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:37:54)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void updateObject(Inspectable object) {

    // search for it
    InspectorInterface inspector = null;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (inspector.getInspectedObject()==object) {
            inspector.updateObject();
            return;
        }
    }
}

/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 16:00:49)
 * @param newParent java.awt.Frame
 */
public static void setParent(java.awt.Frame newParent) {
    parent = newParent;
}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 14:20:50)
 * @return com.cosylab.vdct.inspector.InspectorInterface
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void updateCommentProperty(Inspectable object) {

    InspectorInterface inspector = null;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (inspector.getInspectedObject()==object)
            inspector.updateComment();
    }

}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 15:46:11)
 */
public void updateObjectLists() {
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements())
        ((InspectorInterface)e.nextElement()).updateObjectList();
}
/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 14:20:50)
 * @return com.cosylab.vdct.inspector.InspectorInterface
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void updateProperty(Inspectable object, InspectableProperty property) {

    InspectorInterface inspector = null;
    Enumeration e = inspectors.elements();
    while (e.hasMoreElements()) {
        inspector = (InspectorInterface)e.nextElement();
        if (inspector.getInspectedObject()==object)
            inspector.updateProperty(property);
    }

}
}
