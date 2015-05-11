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

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.vdb.GUIHeader;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.NameValueInfoProperty;
import com.cosylab.vdct.vdb.ROProperty;
import com.cosylab.vdct.vdb.VDBFieldData;
import com.cosylab.vdct.vdb.VDBTemplatePort;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:27:30)
 * @author Matej Sekoranja
 */
public class TemplateEPICSPort extends EPICSVarLink implements TemplateEPICSLink, Movable {

     private String lastUpdatedFullName = null;
    private static GUISeparator portSeparator = null;
    private static javax.swing.ImageIcon icon = null;
    private static final String hiddenString = "<hidden>";
/**
 * EPICSVarLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public TemplateEPICSPort(ContainerObject parent, VDBFieldData fieldData) {
    super(parent, fieldData);
    setWidth(Constants.TEMPLATE_WIDTH/2);

    updateTemplateLink();

    // set default side (left)
    int mode = OutLink.INPUT_PORT_MODE;
    Port visiblePort = ((VDBTemplatePort)fieldData).getPort().getVisibleObject();
    if (visiblePort!=null)
        mode = visiblePort.getMode();

    super.setRight(mode==OutLink.OUTPUT_PORT_MODE);

    drawOnlyOneSided = true;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public void updateTemplateLink()
{
    if (lastUpdatedFullName!=null && getFieldData().getFullName().equals(lastUpdatedFullName))
        return;

    // remove old one
    if (lastUpdatedFullName!=null)
        Group.getRoot().getLookupTable().remove(lastUpdatedFullName);

    // ups, we already got this registered
    if (Group.getRoot().getLookupTable().containsKey(getFieldData().getFullName()))
    {
        lastUpdatedFullName = null;
        ((LinkManagerObject)getParent()).addInvalidLink(this);
    }
    // everything is OK
    else
    {
        lastUpdatedFullName = getFieldData().getFullName();
        Group.getRoot().getLookupTable().put(lastUpdatedFullName, this);
        LinkManagerObject.fixLink(this);
        ((LinkManagerObject)getParent()).removeInvalidLink(this);
    }
}

/**
 * e.g. for rename
 * updates lookup table and fixes source
 */
public void fixTemplateLink()
{
    updateTemplateLink();
    LinkManagerObject.fixLink(this);
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return String
 */
public String getLabel() {
    return getFieldData().getName();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight()
{
    // super.super.isRigth() is the right solution, but ...
    return isStaticRight();
}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:10:37)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {

    // can happen, hiliting invisible link of the source
    if (!isVisible())
        return;

    com.cosylab.vdct.graphics.ViewState view = com.cosylab.vdct.graphics.ViewState.getInstance();

    double Rscale = view.getScale();
    boolean zoom = (Rscale < 1.0) && view.isZoomOnHilited() && view.isHilitedObject(this);

    if (zoom) {
        zoomImage = ZoomPane.getInstance().startZooming(this,!isZoomRepaint());
    }

    boolean isRightSide = isRight();

    int rrx;            // rrx, rry is center
    if (isRightSide)
        rrx = getRx()+getRwidth()-view.getRx();
    else
        rrx = getRx()-view.getRx();

    int rry = (int)(getRscale()*getInY()- view.getRy());

    ZoomPane pane = ZoomPane.getInstance();
    if (getParent().isZoomRepaint()) {
        if (isRightSide) {
            rrx = getX() - getParent().getX() + getWidth() + pane.getLeftOffset();
        } else {
            rrx = getX() - getParent().getX() + pane.getLeftOffset();
        }
        rry = getY() - getParent().getY() + pane.getTopOffset() + getHeight()/2;
    }
    else if (isZoomRepaint()) {
        if (isRightSide) {
            rrx = pane.getLeftOffset() + getWidth();
        } else {
            rrx = pane.getLeftOffset();
        }
        rry = pane.getTopOffset() + getHeight()/2;
    }

    if (!hilited) g.setColor(Constants.FRAME_COLOR);
    else g.setColor((view.isHilitedObject(this)) ?
                    Constants.HILITE_COLOR : Constants.FRAME_COLOR);


    int mode = OutLink.CONSTANT_PORT_MODE;
    Port visiblePort = ((VDBTemplatePort)getFieldData()).getPort().getVisibleObject();
    if (visiblePort!=null)
        mode = visiblePort.getMode();

    if (mode == OutLink.INPUT_PORT_MODE)
    {
        // input link
        int arrowLength = 2*r;

        if (!isRightSide)
            rrx -= arrowLength;

        // draw arrow
        g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
        g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);

        int dr=-r;
        if (isRightSide) {
            dr=-dr;
            rrx+=arrowLength;
        }
        g.drawLine(rrx, rry-r, rrx+dr, rry);
        g.drawLine(rrx, rry+r, rrx+dr, rry);
    }
    else if (mode == OutLink.OUTPUT_PORT_MODE)
    {
        // output link
        int arrowLength = 3*r;

        if (!isRightSide)
            rrx -= arrowLength;

        // draw arrow
        g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
        g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);

        int dr=r;
        if (isRightSide) {
            dr=-dr;
            rrx+=arrowLength;
        }
        g.drawLine(rrx, rry-r, rrx+dr, rry);
        g.drawLine(rrx, rry+r, rrx+dr, rry);
    }
    //else
        // constant (none)


    // invalid
    if (lastUpdatedFullName==null)
    {
        rrx = getRx()-view.getRx();
        rry = getRy()-view.getRy();
        int rwidth = getRwidth();
        int rheight = getRheight();

        g.setColor(Color.red);

        g.drawLine(rrx, rry, rrx+rwidth, rry+rheight);
        g.drawLine(rrx+rwidth, rry, rrx, rry+rheight);
    }
    super.draw(g, hilited);

//    if (zoom) {
//        int rwidth = getRwidth();
//        int rheight = getRheight();
//        rrx -= (rwidth/Rscale - rwidth)/2;
//        rry -= (rheight/Rscale - rheight)/2;
//        if (view.getRx() < 0)
//            rrx = rrx < 0 ? 2 : rrx;
//        if (view.getRy() < 0)
//            rry = rry <= 0 ? 2 : rry;
//        Rscale = 1.0;
//        r = (int)(Constants.LINK_RADIOUS);
//        rtailLen = (int)(Constants.TAIL_LENGTH);
//    }

}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroyAndRemove() {
    super.destroy();

    if (lastUpdatedFullName!=null)
        Group.getRoot().getLookupTable().remove(getFieldData().getFullName());
    else
        ((LinkManagerObject)getParent()).removeInvalidLink(this);

    lastUpdatedFullName = null;
}

public void destroy()
{
    if (!isDestroyed())
        destroyAndRemove();
}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 */
public void disconnect(Linkable disconnector) {
    if (!disconnected && outlinks.contains(disconnector)) {
        outlinks.removeElement(disconnector);
        /*if (outlinks.size()==0) {
            // do not destory port
            //destroy();
        }
        else */if (outlinks.size()==1)
            if (outlinks.firstElement() instanceof VisibleObject)
                setColor(((VisibleObject)outlinks.firstElement()).getColor());
    }
}

/**
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#setDestroyed(boolean)
 */
public void setDestroyed(boolean newDestroyed) {
    super.setDestroyed(newDestroyed);
    if (!newDestroyed)
        updateTemplateLink();
}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getPortSeparator() {
    if (portSeparator==null) portSeparator = new GUISeparator("Port");
    return portSeparator;
}

/**
 * Return properties to be inspected
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {

    OutLink out;
    Vector starts = new Vector();
    Enumeration e = outlinks.elements();
    while (e.hasMoreElements()) {
        out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
        if (out instanceof EPICSLinkOut) starts.addElement(out);
    }

    InspectableProperty[] properties = new InspectableProperty[1+3+2*starts.size()];

    properties[0]=GUIHeader.getDefaultHeader();
    properties[1]=getPortSeparator();
    //properties[2]=new NameValueInfoProperty("Value", getFieldData().getValue());
    properties[2]=new ROProperty(getFieldData(), true);
    properties[3]=new NameValueInfoProperty("Description", getFieldData().getHelp());

    int i = 4;
    VDBFieldData fieldData;
    e = starts.elements();
    while (e.hasMoreElements())
    {
        fieldData = ((EPICSLinkOut)e.nextElement()).getFieldData();
        properties[i++]=new GUISeparator(fieldData.getFullName());
        properties[i++]=fieldData;
    }
    return properties;
}

/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 9:20:14)
 * @return java.lang.String
 */
public String toString() {
    return "Port: "+getName();
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return javax.swing.Icon
 */
public javax.swing.Icon getIcon() {
    if (icon==null)
        icon = new javax.swing.ImageIcon(getClass().getResource("/images/link.gif"));
    return icon;
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:07:15)
 * @return java.lang.String
 */
public String getDescription() {
    return ((VDBTemplatePort)fieldData).getDescription();
}

/**
 * @param visibile
 */
public void visilibityChanged(boolean visible)
{
    // superb solution using getLayerID()
}

/**
 * @see com.cosylab.vdct.graphics.objects.TemplateEPICSLink#isVisible()
 */
public boolean isVisible() {
    return (fieldData.getVisibility() == InspectableProperty.ALWAYS_VISIBLE ||
            (fieldData.getVisibility() == InspectableProperty.NON_DEFAULT_VISIBLE && !fieldData.hasDefaultValue()));
}

/**
 * @see com.cosylab.vdct.graphics.objects.Rotatable#setRight(boolean)
 */
public void setRight(boolean isRight)
{
    boolean oldValue = isRight();
    if (oldValue != isRight)
    {
        super.setRight(isRight);
        ((Template)getParent()).fieldSideChange(this, isRight);
    }
}

/**
 * @see com.cosylab.vdct.graphics.objects.Movable#checkMove(int, int)
 */
public boolean checkMove(int dx, int dy) {
    // this method is called only on selection move
    // and this object is not selectable
    return false;
}

/**
 * @see com.cosylab.vdct.graphics.objects.Movable#move(int, int)
 */
public boolean move(int dx, int dy) {

    boolean moved = false;

    ViewState view = ViewState.getInstance();
    dx = (int)(dx*view.getScale());
    dy = (int)(dy*view.getScale());
    int x = DrawingSurface.getInstance().getPressedX() + view.getRx();
    int y = DrawingSurface.getInstance().getPressedY() + view.getRy();

    if (dx > 0 && !isRight())
    {
        if ((x+dx) > (getRx()+getRwidth()))
        {
            rotate();
            moved = true;
        }
    }
    else if (dx < 0 && isRight())
    {
        if ((x+dx) < getRx())
        {
            rotate();
            moved = true;
        }
    }

    LinkManagerObject lmo = (LinkManagerObject)getParent();
    if (dy > 0 && !lmo.isLastField(this))
    {
        if ((y+dy) > (getRy()+getRheight()))
        {
            lmo.moveFieldDown(this);
            moved = true;
        }
    }
    else if (dy < 0 && !lmo.isFirstField(this))
    {
        if ((y+dy) < getRy())
        {
            lmo.moveFieldUp(this);
            moved = true;
        }
    }

    // should be done for discrete move
    if (moved)
        DrawingSurface.getInstance().resetDraggedPosition();

    return moved;

}

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.Linkable#getLayerID()
     */
    public String getLayerID()
    {
        if (!isVisible())
            return hiddenString;
        else
            return super.getLayerID();
    }

}
