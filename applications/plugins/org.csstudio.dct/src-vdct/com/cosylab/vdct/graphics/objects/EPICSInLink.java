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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cosylab.vdct.Constants;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 21:34:08)
 * @author Matej Sekoranja
 */
public class EPICSInLink extends EPICSLinkOutIn {

    private static final String processString = "Process";
    private static final String nppString = "NPP - No Process Passive";
    private static final String ppString ="PP - Process Passive";
    private static final String caString = "CA - Channel Access";
    private static final String cpString = "CP - CA process on monitor";
    private static final String cppString = "CPP - CP if record passive";

    private static final String severityString = "Severity";
    private static final String nmsString = "NMS - No Maximize Severity";
    private static final String msString = "MS - Maximize Severity";

    class PopupMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();
            if (action.equals(nppString))
            {
                EPICSInLink.this.getLinkProperties().setProcess("NPP");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(ppString))
            {
                EPICSInLink.this.getLinkProperties().setProcess("PP");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(caString))
            {
                EPICSInLink.this.getLinkProperties().setProcess("CA");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(cpString))
            {
                EPICSInLink.this.getLinkProperties().setProcess("CP");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(cppString))
            {
                EPICSInLink.this.getLinkProperties().setProcess("CPP");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(nmsString))
            {
                EPICSInLink.this.getLinkProperties().setMaximize("NMS");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
            else if (action.equals(msString))
            {
                EPICSInLink.this.getLinkProperties().setMaximize("MS");
                EPICSInLink.this.getFieldData().setValue(EPICSInLink.this.getLinkProperties().getCompactLinkDef());
            }
        }
    }

/**
 * EPICSInLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSInLink(ContainerObject parent, com.cosylab.vdct.vdb.VDBFieldData fieldData) {
    super(parent, fieldData);
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:10:37)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {
//    super.draw(g, hilited);

    com.cosylab.vdct.graphics.ViewState view = com.cosylab.vdct.graphics.ViewState.getInstance();

    double Rscale = view.getScale();
    boolean zoom = (Rscale < 1.0) && view.isZoomOnHilited() && view.isHilitedObject(this);

    if (zoom) {
        zoomImage = ZoomPane.getInstance().startZooming(this,!isZoomRepaint());
    }

    boolean rightSide = isRight();
    int arrowLength = 2*r;

    int rrx;
    if (rightSide)
        rrx = getRx()+getRwidth()-view.getRx();
    else
        rrx = getRx()-view.getRx()-arrowLength;

//    int rry = getRy()+getRheight()/2-view.getRy();
    int rry = (int)(getRscale()*getOutY()-view.getRy());

    Color color;
    if (!hilited) color = Constants.FRAME_COLOR;
    else color = (view.isHilitedObject(this)) ?
                    Constants.HILITE_COLOR : Constants.FRAME_COLOR;

    if (getParent().isZoomRepaint()) {
        if (rightSide) {
            rrx = getX() - getParent().getX() + getWidth() + ZoomPane.getInstance().getLeftOffset();
        } else {
            rrx = getX() - getParent().getX() + ZoomPane.getInstance().getLeftOffset()-2*r;
        }
        rry = getY() - getParent().getY() + ZoomPane.VERTICAL_MARGIN + getHeight()/2;
    } else if (isZoomRepaint()) {
        if (rightSide) {
            rrx = getWidth() + ZoomPane.getInstance().getLeftOffset()+1;
        } else {
            rrx = getX() - getParent().getX() + ZoomPane.getInstance().getLeftOffset() - (2*r + Constants.ARROW_SIZE);
        }
        rry = ZoomPane.VERTICAL_MARGIN + getHeight()/2;
    }


    if (inlink!=null) {

        g.setColor(hilited && view.isHilitedObject(this) && !zoom ? Constants.HILITE_COLOR : getVisibleColor());

        LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);

        g.setColor(color);
        // draw arrow
        g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
        g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);

        int dr=r;
        if (rightSide) {
            dr=-dr;
            rrx+=arrowLength;
        }

        g.drawLine(rrx, rry-r, rrx+dr, rry);
        g.drawLine(rrx, rry+r, rrx+dr, rry);

        if (font2!=null) {
            g.setFont(font2);
            rry += realHalfHeight;
            if (rightSide)
                rrx += (labelLen-realLabelLen)/2+arrowLength/2;
            else
                rrx += arrowLength-rtailLen+labelLen-realLabelLen;

            g.drawString(label2, rrx, rry);
        }

        //if (inlink.getLayerID().equals(getLayerID()))


    } else {

         if (getLinkCount()>0) {
            // ports - draw tail line

            // draw arrow
             g.setColor(Constants.BACKGROUND_COLOR);
             g.fillRect(rrx+1, rry-r, arrowLength-1, 2*r);
             g.setColor(color);
            g.drawLine(rrx, rry-r, rrx+arrowLength, rry-r);
            g.drawLine(rrx, rry+r, rrx+arrowLength, rry+r);

            int dr=-r;
            if (rightSide) {
                dr=-dr;
                rrx+=arrowLength;
            }
            g.drawLine(rrx, rry-r, rrx+dr, rry);
            g.drawLine(rrx, rry+r, rrx+dr, rry);

            int rrx2 = (int)(getRscale()*getInX()- view.getRx());
            g.drawLine(rrx, rry, rrx2, rry);

            if (rightSide)
                rrx-=arrowLength;
        }

        if (getFieldData().getValue().length()!=0)
        {
            // draw cross
            if (!rightSide) rrx+=arrowLength;
            g.drawLine(rrx-r, rry-r, rrx+r, rry+r);
            g.drawLine(rrx+r, rry-r, rrx-r, rry+r);
        }
    }

    super.draw(g, hilited);


//    if (zoom && isZoomRepaint()) {
//        int rwidth = getRwidth();
//        int rheight = getRheight();
//        if (rightSide)
//            rrx += ((rwidth/Rscale - rwidth)/2 - ZoomPane.getInstance().getLeftOffset());
//        else
//            rrx -= ((rwidth/Rscale - rwidth)/2 + ZoomPane.getInstance().getLeftOffset());
//        if (view.getRx() < 0)
//            rrx = rrx < 0 ? 2 : rrx;
//        g.drawImage(zoomImage, rrx,rry, ZoomPane.getInstance());
//    }

}


/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.EPICSInLink.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.EPICSInLink.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}


/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
    Vector items = super.getItems();

    ActionListener al = createPopupmenuHandler();

    items.add(new JSeparator());

    JMenu processMenu = new JMenu(processString);
    items.addElement(processMenu);

    JMenuItem npp = new JMenuItem(nppString);
    npp.addActionListener(al);
    processMenu.add(npp);

    JMenuItem pp = new JMenuItem(ppString);
    pp.addActionListener(al);
    processMenu.add(pp);

    JMenuItem ca = new JMenuItem(caString);
    ca.addActionListener(al);
    processMenu.add(ca);

    JMenuItem cp = new JMenuItem(cpString);
    cp.addActionListener(al);
    processMenu.add(cp);

    JMenuItem cpp = new JMenuItem(cppString);
    cpp.addActionListener(al);
    processMenu.add(cpp);

    JMenu severityMenu = new JMenu(severityString);
    items.addElement(severityMenu);

    JMenuItem nms = new JMenuItem(nmsString);
    nms.addActionListener(al);
    severityMenu.add(nms);

    JMenuItem ms = new JMenuItem(msString);
    ms.addActionListener(al);
    severityMenu.add(ms);

    return items;
}
}
