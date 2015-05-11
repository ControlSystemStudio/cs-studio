package com.cosylab.vdct.graphics.objects;

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

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:27:25)
 * @author Matej Sekoranja
 */
public abstract class ContainerObject extends VisibleObject {
    protected boolean useHashtable = true;
    protected Hashtable subObjects = null;
    protected Vector subObjectsV = null;
//    private boolean isZoomRepaint;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:40:08)
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public ContainerObject(ContainerObject parent) {
    super(parent);
    subObjects = new Hashtable();            // key in not cass senstive
    subObjectsV = new Vector();                // to keep order
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:45:17)
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param useHashtable boolean
 */
public ContainerObject(ContainerObject parent, boolean useHashtable) {
    super(parent);
    subObjectsV = new Vector();                // to keep order
    this.useHashtable=useHashtable;
    if (useHashtable)
        subObjects = new Hashtable();            // key in not cass senstive
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:30:04)
 * @param id java.lang.String
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void addSubObject(String id, VisibleObject object) {
    addSubObject(id, object, subObjectsV.size());

    /*
    if (this instanceof Group)
        System.out.println("Added to group "+((Group)this).getAbsoluteName()+" object with id: "+id);
    */

}

public void addSubObject(String id, VisibleObject object, int position) {
    if (useHashtable)
    {
        if (subObjects.containsKey(id))
        {
            com.cosylab.vdct.Console.getInstance().println("Object with name "+id+" already in this group -> will not be added.");
            return;
        }
        subObjects.put(id, object);
    }
    try {
        subObjectsV.add(position, object);
    } catch (ArrayIndexOutOfBoundsException e) {
        subObjectsV.addElement(object);
    }


    if (object.getParent()==null) object.setParent(this);
}

/**
 * "Helper" method (e.g. for Group).
 * @param id
 * @param object
 * @param create
 */
public void addSubObject(String id, VisibleObject object, boolean create) {
    addSubObject(id, object);
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 12:04:45)
 */
public void clear() {
    if (useHashtable) subObjects.clear();
    subObjectsV.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:34:57)
 * @param id java.lang.String
 * @return boolean
 */
public boolean containsObject(String id) {
    if (useHashtable)
        return subObjects.containsKey(id);
    else
        return false;
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 16:54:17)
 * @return java.lang.Object
 * @param id java.lang.String
 */
public Object getSubObject(String id) {
    if (useHashtable)
        return subObjects.get(id);
    else
        return null;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:35:48)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getSubObjects() {
    return subObjects;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:35:48)
 * @return java.util.Vector
 */
public java.util.Vector getSubObjectsV() {
    return subObjectsV;
}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:32:49)
 * @param id java.lang.String
 * @return java.lang.Object
 */
public Object removeObject(String id) {
    if (useHashtable) {
        Object object = subObjects.remove(id);
        if (object!=null) subObjectsV.removeElement(object);

        /*
        if (object!=null)
            System.out.println("Removed: "+id+"["+object.toString()+"]");
        else
            System.out.println("Failed to remove: "+id);
        */

        return object;
    }
    else
        return null;
}

public void updateFields() {
    Enumeration e = getSubObjectsV().elements();
    while (e.hasMoreElements()) {
        Object o = e.nextElement();
        if (o instanceof ContainerObject)
            ((ContainerObject)o).updateFields();
    }
}

//public void setZoomRepaint(boolean zoomRepaint) {
//    isZoomRepaint = zoomRepaint;
//}
//
//public boolean isZoomRepaint() {
//    return isZoomRepaint;
//}
}
