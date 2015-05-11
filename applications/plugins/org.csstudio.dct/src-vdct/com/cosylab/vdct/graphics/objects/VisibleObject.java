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

import java.awt.*;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.*;

/**
 * Insert the type's description here.
 * Creation date: (19.12.2000 19:56:38)
 * @author Matej Sekoranja
 */
public abstract class VisibleObject implements Visitable {
    // parent (owner)
    private ContainerObject parent;

    // position & size
    protected int x;
    protected int y;
    private int width;
    private int height;

    // to support option to mark (remember) some position
    private int markedX;
    private int markedY;

    // scale
    private double scale = 1;

    // scaled position & size
    private int rx;
    private int ry;
    private int rwidth;
    private int rheight;

    // scaled scale
    private double rscale = -1;

    // label support
    private int rlabelX;
    private int rlabelY;

    private String label;
    private Font font = null;

    // color
    private Color color = null;

    private boolean destroyed = false;

    //used for blowing up the object on small zoom
    private boolean isZoomRepaint = false;
    protected Image zoomImage;

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:40:53)
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public VisibleObject(ContainerObject parent) {
    this.parent=parent;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:58:53)
 */
public void destroy() {
    destroyed=true;
    ViewState view = ViewState.getInstance();
    if (view.getHilitedObject() == this)
        view.setAsHilited(null);
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 21:30:46)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected abstract void draw(Graphics g, boolean hilited);
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 21:30:46)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void postDraw(Graphics g, boolean hilited) {}

/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:56:23)
 */
public void forceValidation() {

    ViewState view = ViewState.getInstance();
    scale=view.getScale();

    if (scale!=rscale)
        internalRevalidate();
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return java.awt.Color
 */
public java.awt.Color getColor() {
    return color;
}

public Color getVisibleColor() {
    Color c = getColor();
    if (c.equals(Constants.BACKGROUND_COLOR))
        if (c.equals(Color.black))
            c=Color.white;
        else
            c=Color.black;
    return c;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return java.awt.Font
 */
public java.awt.Font getFont() {
    return font;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:40:00)
 * @return java.lang.String
 */
public abstract String getHashID();
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return int
 */
public int getHeight() {
    return height;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return java.lang.String
 */
public java.lang.String getLabel() {
    return label;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:41:46)
 * @return com.cosylab.vdct.graphics.objects.ContainerObject
 */
public ContainerObject getParent() {
    return parent;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRheight() {
    forceValidation();
    return rheight;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRlabelX() {
    forceValidation();
    return rlabelX;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRlabelY() {
    forceValidation();
    return rlabelY;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return double
 */
public double getRscale() {
    forceValidation();
    return rscale;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRwidth() {
    forceValidation();
    return rwidth;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRx() {
    forceValidation();
    return rx;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @return int
 */
public int getRy() {
    forceValidation();
    return ry;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return double
 */
public double getScale() {
    return scale;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return int
 */
public int getWidth() {
    return width;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return int
 */
public int getX() {
    return x;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @return int
 */
public int getY() {
    return y;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 18:32:14)
 */
private void internalRevalidate() {
    rscale=scale;
    validate();
}
/**
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {
    if ((rx<=px) && (ry<=py) &&
            ((rx+rwidth)>=px) &&
            ((ry+rheight)>=py)) return this;
    else return null;
}
/**
 * Default impmlementation for square (must be rescaled)
 * p1 is upper-left point
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param p1x int
 * @param p1y int
 * @param p2x int
 * @param p2y int
 */

public VisibleObject intersects(int p1x, int p1y, int p2x, int p2y) {
    if ((rx>=p1x) && (ry>=p1y) &&
            ((rx+rwidth)<=p2x) &&
            ((ry+rheight)<=p2y)) return this;
    else return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 18:02:09)
 * @return boolean
 */
public boolean isDestroyed() {
    return destroyed;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 21:33:47)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
public void paint(Graphics g, boolean hilited) {
    forceValidation();
    draw(g, hilited);
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 21:33:47)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
public void postPaint(Graphics g, boolean hilited) {
    forceValidation();
    postDraw(g, hilited);
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:21:59)
 */
public abstract void revalidatePosition();
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newColor java.awt.Color
 */
public void setColor(java.awt.Color newColor) {
    color = newColor;
}
/**
 * Insert the method's description here.
 * Creation date: (5.5.2001 18:02:09)
 * @param newDestroyed boolean
 */
public void setDestroyed(boolean newDestroyed) {
    destroyed = newDestroyed;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newFont java.awt.Font
 */
public void setFont(java.awt.Font newFont) {
    font = newFont;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newHeight int
 */
public void setHeight(int newHeight) {
    height = newHeight;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newLabel java.lang.String
 */
public void setLabel(java.lang.String newLabel) {
    label = newLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:41:46)
 * @param newParent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public void setParent(ContainerObject newParent) {
    parent = newParent;
    if (parent!=null || destroyed) destroyed=false;    // revived
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRheight int
 */
public void setRheight(int newRheight) {
    rheight = newRheight;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRlabelX int
 */
public void setRlabelX(int newRlabelX) {
    rlabelX = newRlabelX;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRlabelY int
 */
public void setRlabelY(int newRlabelY) {
    rlabelY = newRlabelY;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRwidth int
 */
public void setRwidth(int newRwidth) {
    rwidth = newRwidth;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRx int
 */
public void setRx(int newRx) {
    rx = newRx;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:13:50)
 * @param newRy int
 */
public void setRy(int newRy) {
    ry = newRy;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newScale double
 */
public void setScale(double newScale) {
    scale = newScale;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newWidth int
 */
public void setWidth(int newWidth) {
    width = newWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newX int
 */
public void setX(int newX) {
    x = newX;
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 20:10:52)
 * @param newY int
 */
public void setY(int newY) {
    y = newY;
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:56:23)
 */
public void unconditionalValidation() {
    ViewState view = ViewState.getInstance();
    rscale=scale=view.getScale();
    internalRevalidate();
}
/**
 * Insert the method's description here.
 * Creation date: (19.12.2000 21:31:15)
 */
protected abstract void validate();

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isVisible() { return true; }

/**
 * Snap to grid. Nearest point is taken.
 */
public void snapToGrid()
{
    int mx = x % Constants.GRID_SIZE;
    int my = y % Constants.GRID_SIZE;

    final int halfGrid = Constants.GRID_SIZE / 2;
    if (mx > halfGrid)
        mx -= Constants.GRID_SIZE;
    if (my > halfGrid)
        my -= Constants.GRID_SIZE;

    x -= mx;
    y -= my;
}


/**
 * Mark (remeber) current position.
 */
public void markPosition()
{
    // some object override x, y
    markedX = getX();
    markedY = getY();
}

/**
 * @return Returns the markedX.
 */
public int getMarkedX() {
    return markedX;
}
/**
 * @return Returns the markedY.
 */
public int getMarkedY() {
    return markedY;
}


public void setZoomRepaint(boolean zoomRepaint) {
    isZoomRepaint = zoomRepaint;
}

/**
 *
 * Returns true if the object is being repainted as blown up on small zoom.
 * @return
 */
public boolean isZoomRepaint() {
    return isZoomRepaint;
}

/**
 *
 * Returns the offset in pixels(the length of additional objects (arrows, strings) drawn on
 * the left side of this object.
 * @return
 */
public int getLeftOffset() {
    return 0;
}

/**
 *
 * Returns the offset in pixels(the length of additional objects (arrows, strings) drawn on
 * the right side of this object.
 * @return
 */
public int getRightOffset() {
    return 0;
}

/**
 *
 * Returns the offset in pixels(the length of additional objects (arrows, strings) drawn above
 * this object.
 * @return
 */
public int getTopOffset() {
    return 0;
}

}
