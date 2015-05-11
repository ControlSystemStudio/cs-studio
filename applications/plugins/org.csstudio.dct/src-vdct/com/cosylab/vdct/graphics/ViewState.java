package com.cosylab.vdct.graphics;

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

import com.cosylab.vdct.Settings;
import com.cosylab.vdct.graphics.objects.InLink;
import com.cosylab.vdct.graphics.objects.MultiInLink;
import com.cosylab.vdct.graphics.objects.OutLink;
import com.cosylab.vdct.graphics.objects.VisibleObject;

/**
 * Insert the type's description here.
 * Creation date: (13/7/99 10:26:13)
 * @author Matej Sekoranja
 */

public class ViewState {

    protected static ViewState instance = null;

    protected static int x0 = 0;                // origin
    protected static int y0 = 0;

    protected double drx = 0.0;                // precise translation (from origin)
    protected double dry = 0.0;

    protected int rx = 0;                // translation (from origin)
    protected int ry = 0;

    protected double scale = 1.0;        // scale

     // viewport size
     protected static int viewWidth = com.cosylab.vdct.Constants.VDCT_WIDTH;
    protected static int viewHeight = com.cosylab.vdct.Constants.VDCT_HEIGHT;

    protected static boolean flat = false;

    protected VisibleObject hilitedObject = null;
    protected LinkedHashSet hilitedObjects = new LinkedHashSet();
    protected Vector selectedObjects = null;

    protected Vector blinkingObjects = null;
    protected boolean blinkState = false;

    private boolean zoomOnHilited = false;

    private int gridSize = (int)(com.cosylab.vdct.Constants.GRID_SIZE*scale);
    private int dotSize = (int)(com.cosylab.vdct.Constants.DOT_SIZE*scale);

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:00:31)
 */
public ViewState() {
    selectedObjects = new Vector();
    blinkingObjects = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 13:31:07)
 * @param original com.cosylab.vdct.graphics.ViewState
 */
public ViewState(ViewState original) {
    this();

    //this.x0 = original.x0;
    //this.y0 = original.y0;

    this.rx = original.rx;
    this.ry = original.ry;

    this.scale = original.scale;
    //this.width = original.width;
    //this.height = original.height;

    this.gridSize = (int)(com.cosylab.vdct.Constants.GRID_SIZE*scale);
    this.dotSize = (int)(com.cosylab.vdct.Constants.DOT_SIZE*scale);

}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 15:54:30)
 */
public void deblinkAll() {
    blinkingObjects.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 15:54:02)
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void deblinkObject(VisibleObject object) {
    blinkingObjects.removeElement(object);
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 11:54:05)
 * @return boolean
 */
public boolean deselectAll() {
    int count = selectedObjects.size();
    selectedObjects.clear();
    return (count!=0);
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 11:54:42)
 */
public void deselectObject(VisibleObject object) {
    selectedObjects.remove(object);
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 15:52:19)
 * @return java.util.Vector
 */
public java.util.Vector getBlinkingObjects() {
    return blinkingObjects;
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 18:37:09)
 * @return int
 */

public int getGridSize() {
    return gridSize;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getHeight() {
    return Settings.getInstance().getCanvasHeight();
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:25:54)
 * @return com.cosylab.vdct.graphics.objects.VisibleObject
 */
public VisibleObject getHilitedObject() {
    return hilitedObject;
}

public LinkedHashSet getHilitedObjects() {
    return hilitedObjects;
}

public boolean isHilitedObject(VisibleObject object) {
    return hilitedObjects.contains(object);
}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:02:40)
 * @return com.cosylab.vdct.graphics.ViewState
 */
public static ViewState getInstance() {
    if (instance==null) instance = new ViewState();
    return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getRx() {
    return rx;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getRy() {
    return ry;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return double
 */
public double getScale() {
    return scale;
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 12:00:31)
 * @return java.util.Vector
 */
public Vector getSelectedObjects() {
    return selectedObjects;
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 14:27:43)
 * @return int
 */
public int getViewHeight() {
    return viewHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 14:27:43)
 * @return int
 */
public int getViewWidth() {
    return viewWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getWidth() {
    return Settings.getInstance().getCanvasWidth();
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getX0() {
    return x0;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @return int
 */
public int getY0() {
    return y0;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 15:56:04)
 * @return boolean
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public boolean isBlinking(VisibleObject object) {
    return blinkState && blinkingObjects.contains(object);
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 16:12:11)
 * @return boolean
 */
public boolean isBlinkState() {
    return blinkState;
}
/**
 * Insert the method's description here.
 * Creation date: (1.5.2001 17:53:25)
 * @return boolean
 */
public boolean isFlat() {
    return flat;
}
/**
 * Insert the method's description here.
 * Creation date: (26.12.2000 21:35:20)
 * @return boolean
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public boolean isPicked(VisibleObject object) {
    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (26.12.2000 21:35:20)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean isSelected(Object object) {
    return selectedObjects.contains(object);
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 14:17:52)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean moveOrigin(int dx, int dy) {
    int nrx = rx+dx;
    int nry = ry+dy;

    if (nrx<0) nrx=0;
    else nrx=Math.min(nrx, (int)(getWidth()*scale-viewWidth));
    if (nry<0) nry=0;
    else nry=Math.min(nry, (int)(getHeight()*scale-viewHeight));

    boolean change = (nrx!=rx) || (nry!=ry);
    rx=nrx; ry=nry;

    drx = rx;
    dry = ry;

    return change;
}
/**
 * Insert the method's description here.
 * Creation date: (28.1.2001 18:38:39)
 */
public void reset() {
    hilitedObject = null;
    zoomOnHilited = false;
    selectedObjects.removeAllElements();
    blinkingObjects.removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 13:35:59)
 */
public void set(com.cosylab.vdct.graphics.objects.Group group) {
    if (group.getLocalView()!=null)
        setInstance(group.getLocalView());
    else
    {
        ViewState copy = new ViewState(this);
        group.setLocalView(copy);
        setInstance(copy);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 15:52:58)
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public void setAsBlinking(VisibleObject object) {
    if (!blinkingObjects.contains(object))
        blinkingObjects.addElement(object);
}

public boolean isZoomOnHilited() {
    return this.zoomOnHilited;
}

public boolean setAsHilited(VisibleObject object) {
    return setAsHilited(object, false);
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:24:23)
 * @return boolean
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 * @param zoomOnHilited sets the option whether the hilited object should be zoomed in
 */
public boolean setAsHilited(VisibleObject object, boolean zoomOnHilited) {
    this.zoomOnHilited = zoomOnHilited;

    if(zoomOnHilited) {
        DrawingSurface.getInstance().repaint();
        hilitedObjects.clear();
        if (hilitedObject != null) {
            hilitedObjects.add(hilitedObject);
        }
    }

    if (object == null) {
        hilitedObjects.clear();
        hilitedObject = null;
        return false;
    }

    if (object!=hilitedObject || hilitedObjects.size() == 1) {

        hilitedObject=object;

        //initialization
        hilitedObjects.clear();
        hilitedObjects.add(hilitedObject);
        Object obj = hilitedObject;

        // inlinks
        Vector outlinks = null;
        if (obj instanceof MultiInLink) {
             outlinks = ((MultiInLink)obj).getOutlinks();
        } else if (obj instanceof InLink){
            outlinks = new Vector();
            outlinks.add(((InLink)obj).getOutput());
        }

        if (!zoomOnHilited) {
            if (outlinks != null)
                for (int i=0; i<outlinks.size(); i++) {
                    obj = outlinks.elementAt(i);
                    hilitedObjects.add(obj);
                    while (obj instanceof InLink) {
                        obj = ((InLink)obj).getOutput();
                        hilitedObjects.add(obj);
                    }
                }

            // outLinks
            obj = hilitedObject;
            while (obj instanceof OutLink) {
                obj = ((OutLink)obj).getInput();
                hilitedObjects.add(obj);
            }
        }
        return true;
    }
    else
        return false;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 22:24:23)
 * @return boolean
 * @param object com.cosylab.vdct.graphics.objects.VisibleObject
 */
public boolean setAsSelected(VisibleObject object) {
    if (!selectedObjects.contains(object)) {
        selectedObjects.addElement(object);
        return true;
    }
    else
        return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 16:12:11)
 * @param newBlinkState boolean
 */
public void setBlinkState(boolean newBlinkState) {
    blinkState = newBlinkState;
}
/**
 * Insert the method's description here.
 * Creation date: (1.5.2001 17:53:25)
 * @param newFlat boolean
 */
public void setFlat(boolean newFlat) {
    flat = newFlat;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:02:40)
 * @param newInstance com.cosylab.vdct.graphics.ViewState
 */
public static void setInstance(ViewState newInstance) {
    instance = newInstance;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @param newRx int
 */
public void setRx(int newRx) {
    rx = newRx;
    drx = newRx;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @param newRy int
 */
public void setRy(int newRy) {
    ry = newRy;
    dry = newRy;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @param newScale double
 */
public void setScale(double newScale) {
    scale = newScale;
    gridSize = Math.max(1, (int)(com.cosylab.vdct.Constants.GRID_SIZE*scale));
    dotSize = (int)(com.cosylab.vdct.Constants.DOT_SIZE*scale);
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 14:27:43)
 * @param newViewHeight int
 */
public void setViewHeight(int newViewHeight) {
    viewHeight = newViewHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 14:27:43)
 * @param newViewWidth int
 */
public void setViewWidth(int newViewWidth) {
    viewWidth = newViewWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @param newX0 int
 */
public void setX0(int newX0) {
    x0 = newX0;
}
/**
 * Insert the method's description here.
 * Creation date: (11.12.2000 17:56:18)
 * @param newY0 int
 */
public void setY0(int newY0) {
    y0 = newY0;
}
    /**
     * Returns the drx.
     * @return double
     */
    public double getDrx()
    {
        return drx;
    }

    /**
     * Returns the dry.
     * @return double
     */
    public double getDry()
    {
        return dry;
    }

    /**
     * Sets the drx.
     * @param drx The drx to set
     */
    public void setDrx(double drx)
    {
        this.drx = drx;
        this.rx = (int)drx;
    }

    /**
     * Sets the dry.
     * @param dry The dry to set
     */
    public void setDry(double dry)
    {
        this.dry = dry;
        this.ry = (int)dry;
    }

    /**
     * @return Returns the dotSize.
     */
    public int getDotSize() {
        return dotSize;
    }
}
