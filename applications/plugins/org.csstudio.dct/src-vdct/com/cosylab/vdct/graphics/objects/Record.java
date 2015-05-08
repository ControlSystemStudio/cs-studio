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
import java.util.*;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.DataProvider;
import com.cosylab.vdct.Settings;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.plugin.debug.PluginDebugManager;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.dbd.DBDConstants;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDRecordData;

import com.cosylab.vdct.inspector.*;

import com.cosylab.vdct.graphics.popup.*;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @author Matej Sekoranja
 */
public class Record
    extends LinkManagerObject
    implements Clipboardable, Descriptable, Flexible, Hub, Morphable, Movable,
                MultiInLink, Rotatable, Selectable, Popupable, Inspectable, SaveObject, SelectableComponents
{

    //private final static String nullString = "";
    private final static String fieldMaxStr = "01234567890123456789012345";
    private final static int tailSizeOfR = 4;
    private static javax.swing.ImageIcon icon = null;
    protected VDBRecordData recordData = null;
    private CommentProperty commentProperty = null;
    // type label
    protected int rtypeLabelX;
    protected int rtypeLabelY;
    protected String label2;
    protected Font typeFont = null;
    // changed fields label
    protected int rfieldLabelX;
    protected int rfieldLabelY;
    protected double rfieldRowHeight;
    protected Font fieldFont = null;
    protected Vector changedFields;
    protected Vector outlinks;
    protected boolean disconnected = false;
    private boolean right = true;

    private boolean inDebugMode = false;
    private boolean debugConnected = false;
    private int debugTimeoutHour = -1;
    private int debugTimeoutMinute = -1;
    protected Color debugValueColor = null;

    // timestamp label
    protected int timestampX;
    protected int timestampY;
    protected String timestamp;
    protected Font timestampFont = null;

    // value label
    protected int valueX;
    protected int valueY;
    protected String value;
    protected Font valueFont = null;

    private static final String VAL_FIELD = "VAL";

    private static GUISeparator alphaSeparator = null;
    private static GUISeparator dbdSeparator = null;

    private static ArrayList modes = null;

    public final static int GUI_GROUP_ORDER = 0;
    public final static int SORT_ORDER = 1;
    public final static int DBD_ORDER = 2;

    private int oldNumOfFields = 0;


/**
 * Group constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public Record(ContainerObject parent, VDBRecordData recordData, int x, int y) {
    super(parent);
    this.recordData=recordData;
    setColor(Color.black);
    setWidth(Constants.RECORD_WIDTH);
    setHeight(Constants.RECORD_HEIGHT);
    setX(x); setY(y);

    changedFields = new Vector();
    outlinks = new Vector();

    VDBFieldData field;
    Enumeration e = recordData.getFieldsV().elements();
    while (e.hasMoreElements()) {
        field = (VDBFieldData)e.nextElement();
        // we use old visibility criterion, because of records' repositioning
        // when the fields are hidden the records are moved down
        if (isOldVisible(field))
            changedFields.addElement(field);
    }
    oldNumOfFields = changedFields.size();

    forceValidation();

}
/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 13:36:25)
 * @param oldRecordName java.lang.String
 * @param newRecordName java.lang.String
 */
public void _fixEPICSInLinks(String oldRecordName, String newRecordName) {
    if (oldRecordName.equals(newRecordName)) return;

    Object obj; String old;
    EPICSLinkOut outlink;
    Enumeration fields = getSubObjectsV().elements();
    Enumeration outs;
    while (fields.hasMoreElements()) {
        obj = fields.nextElement();
        if (obj instanceof EPICSVarLink) {
            outs = ((EPICSVarLink)obj).getStartPoints().elements();
            while (outs.hasMoreElements()) {
                obj = outs.nextElement();
                if (obj instanceof EPICSLinkOut) {
                    outlink = (EPICSLinkOut)obj;
                    old = outlink.getFieldData().getValue();
                    if (old.startsWith(oldRecordName))
                        outlink.getFieldData().setValue(newRecordName+old.substring(oldRecordName.length()));

                }
            }
        }
    }

    // fix record inlink
    outs = getStartPoints().elements();
    while (outs.hasMoreElements()) {
        obj = outs.nextElement();
        if (obj instanceof EPICSLinkOut) {
            outlink = (EPICSLinkOut)obj;
            old = outlink.getFieldData().getValue();
            if (old.startsWith(oldRecordName))
                outlink.getFieldData().setValue(newRecordName+old.substring(oldRecordName.length()));

        }
    }

}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
public void accept(Visitor visitor) {
    visitor.visitGroup();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:40:48)
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void addLink(Linkable link) {
    if (!getSubObjectsV().contains(link)) {
        Field field = (Field)link;
        if (field.getFieldData().getPositionIndex() >= 0)
            addSubObject(field.getFieldData().getName(), field, field.getFieldData().getPositionIndex());
        else {
            addSubObject(field.getFieldData().getName(), field);
            field.getFieldData().setPositionIndex(subObjectsV.indexOf(field));
        }
        validateFields();
        revalidateFieldsPosition();
    }
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 14:14:35)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean checkMove(int dx, int dy) {
    ViewState view = ViewState.getInstance();

    if ((getX()<-dx) || (getY()<-dy) ||
        (getX()>(view.getWidth()-getWidth()-dx)) || (getY()>(view.getHeight()-getHeight()-dy)))
        return false;
    else
        return true;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 22:02:29)
 * @param group java.lang.String
 */
public Flexible copyToGroup(java.lang.String group) {

    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(recordData.getName());
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(recordData.getName());

    // object with new name already exists, add suffix ///!!!
    //Object obj;
    while (Group.getRoot().findObject(newName, true)!=null)
//        newName += Constants.COPY_SUFFIX;
            newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);

    //ViewState view = ViewState.getInstance();


    VDBRecordData theDataCopy = VDBData.copyVDBRecordData(recordData);
    theDataCopy.setName(newName);
    Record theRecordCopy = new Record(null, theDataCopy, getX(), getY());
    //theRecordCopy.move(20-view.getRx(), 20-view.getRy());
    Group.getRoot().addSubObject(theDataCopy.getName(), theRecordCopy, true);

    // fix only valid links where target is also selected
    theRecordCopy.fixEPICSOutLinksOnCopy(Group.substractParentName(recordData.getName()), group);
    // links have to be fixed here... so <group>.manageLinks() should be called
    // for clipboard copy this is done later...

    theRecordCopy.manageLinks();
    theRecordCopy.updateFields();
    unconditionalValidation();

    return theRecordCopy;
}


/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param e java.util.Enumeration list of VDBFieldData fields
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixEPICSOutLinksOnCopy(String prevGroup, String group) {
    if (prevGroup.equals(group)) return;

    String prefix;
    if (group.equals(nullString)) prefix=nullString;
    else prefix=group+Constants.GROUP_SEPARATOR;

    Enumeration e = recordData.getFieldsV().elements();
    while (e.hasMoreElements()) {
        VDBFieldData field = (VDBFieldData)e.nextElement();
        //int type = LinkProperties.getType(field);
        //if (type != LinkProperties.VARIABLE_FIELD) {
            String old = field.getValue();
            if (!old.equals(nullString) && !old.startsWith(Constants.HARDWARE_LINK) &&
                old.startsWith(prevGroup)) {

                LinkProperties lp = new LinkProperties(field);
                InLink target = EPICSLinkOut.getTarget(lp, true);
                if (target == null)
                    continue;

                // only parent can be selected
                Object selectableObject;
                if (target instanceof Field)
                    selectableObject = ((Field)target).getParent();
                else
                    selectableObject = target;


                // fix only selected
                if (!ViewState.getInstance().isSelected(selectableObject))
                    continue;

                // fix ports...
                if (selectableObject instanceof Template) {
                    Template t = (Template)selectableObject;
                    field.setValue("$(" + prefix + t.getName() + Constants.FIELD_SEPARATOR + ((TemplateEPICSPort)target).getFieldData().getName()+")");
                }
                // normal record fields
                else if (prevGroup.equals(nullString))
                    field.setValue(prefix+old);
                else
                    field.setValue(prefix+old.substring(prevGroup.length()+1));
            }
        //}
    }

}



/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:21)
 */
public void destroy() {
    if (!isDestroyed()) {
        super.destroy();
        destroyFields();
//        disconnected=true;

        if (outlinks.size()>0) {
            Object[] objs = new Object[outlinks.size()];
            outlinks.copyInto(objs);
            for(int i=0; i<objs.length; i++) {
                OutLink outlink = (OutLink)objs[i];
                OutLink start = EPICSLinkOut.getStartPoint(outlink);
                if(start instanceof EPICSLinkOut)
                    ((EPICSLinkOut)start).destroy();
                else if (start!=null)
                    start.disconnect(this);
                else
                    outlink.disconnect(this);
            }
            outlinks.clear();
        }

        clear();
        getParent().removeObject(Group.substractObjectName(getName()));
    }
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 */
public void disconnect(Linkable disconnector) {
    if (!disconnected && outlinks.contains(disconnector)) {
        outlinks.removeElement(disconnector);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(Graphics g, boolean hilited) {

    ViewState view = ViewState.getInstance();

    double Rscale = getRscale();
    boolean zoom = Rscale < 1.0 && view.isZoomOnHilited() && view.isHilitedObject(this);

    if (zoom) {
        zoomImage = ZoomPane.getInstance().startZooming(this, true);
    }

    int rrx = getRx() - view.getRx();
    int rry = getRy() - view.getRy();

    int rwidth = getRwidth();
    int rheight = getRheight();

    // clipping
    if (!((rrx > view.getViewWidth())
        || (rry > view.getViewHeight())
        || ((rrx + rwidth) < 0)
        || ((rry + rheight) < 0)) || isZoomRepaint()) {

        if (isZoomRepaint()) {
            rrx = ZoomPane.getInstance().getLeftOffset();
            rry = ZoomPane.VERTICAL_MARGIN;
        }


        if (!hilited)
            g.setColor(Constants.RECORD_COLOR);
        else
            if (view.isPicked(this))
                g.setColor(Constants.PICK_COLOR);
            else
                if (view.isSelected(this) || view.isBlinking(this))
                    g.setColor(Constants.SELECTION_COLOR);
                else
                    g.setColor(Constants.RECORD_COLOR);

        g.fillRect(rrx, rry, rwidth, rheight);

        if (!hilited)
            g.setColor(Constants.FRAME_COLOR);
        else
            g.setColor(
                (view.isHilitedObject(this))
                    ? Constants.HILITE_COLOR
                    : Constants.FRAME_COLOR);

        g.drawRect(rrx, rry, rwidth, rheight);

        int recordSize = (int)(Constants.RECORD_HEIGHT * Rscale);

        // middle line
        int ox = (int) (10 * Rscale);
        int ly = (int) (rry + recordSize);
        g.drawLine(rrx + ox, ly, rrx + rwidth - ox, ly);


        // show VAL value and timestamp
        if (inDebugMode)
        {
            // additional middle line has to be drawn
            ly += recordSize;
            g.drawLine(rrx + ox, ly, rrx + rwidth - ox, ly);

            Color col = g.getColor();
            g.setColor(debugValueColor);

            if (valueFont != null) {
                g.setFont(valueFont);
                g.drawString(value, rrx + valueX, rry + valueY + recordSize);
            }

            if (timestampFont != null) {
                g.setFont(timestampFont);
                g.drawString(timestamp, rrx + timestampX, rry + timestampY + recordSize);
            }

            // draw a nice clock
            if (!debugConnected)
                drawDebugTimeout(g, debugTimeoutHour, debugTimeoutMinute, rrx, rry, rwidth, rheight);

            g.setColor(col);

        }

        if (getFont() != null) {
            g.setFont(getFont());
            g.drawString(getLabel(), rrx + getRlabelX(), rry + getRlabelY());
        }

        if (typeFont != null) {
            g.setFont(typeFont);
            g.drawString(label2, rrx + rtypeLabelX, rry + rtypeLabelY);
        }

        if (fieldFont != null) {

            g.setFont(fieldFont);
            FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
            String val;
            VDBFieldData fd;
            int px = rrx + rfieldLabelX;
            int py0 = rry + rfieldLabelY;
            int py = py0; int n = 0;
            Enumeration e = changedFields.elements();
            while (e.hasMoreElements()) {
                fd = (VDBFieldData) (e.nextElement());
                val = fd.getName() + "=" + fd.getValue();
                while (val.length() > 1 && (fm.stringWidth(val) + ox) > rwidth)
                    val = val.substring(0, val.length() - 2); // !!! TODO !!!

                // make monitored fields visible
                if (inDebugMode && fd.getVisibility() == InspectableProperty.ALWAYS_VISIBLE)
                {
                    Color col = g.getColor();
                    g.setColor(Color.YELLOW);
                    g.drawString(val, px, py);
                    g.setColor(col);
                }
                else
                    g.drawString(val, px, py);

                py = py0 + (int)((++n)*rfieldRowHeight);
            }
        }

        // fwdlink support
        if (!disconnected && (outlinks.size() > 0)) {

            Color recordColor = g.getColor();
            Color linkColor = recordColor;
            if (!(hilited && view.isHilitedObject(this)))
                if (outlinks.firstElement() instanceof VisibleObject)
                    linkColor = ((VisibleObject) outlinks.firstElement()).getVisibleColor();


//            if (!zoom) {
                // draw link and its tail
                boolean isRightSide = isRight();
                int r = (int)(Constants.LINK_RADIOUS * Rscale);
                int cy = (int)(Rscale*getInY()- view.getRy());
                int ccx = (int)(Rscale*getInX()- view.getRx());
                int cx;

                if (isZoomRepaint() || getParent().isZoomRepaint()) {
                    cy = getHeight()/2 + ZoomPane.VERTICAL_MARGIN;
                    ccx = isRightSide ? ZoomPane.getInstance().getWidth() - 1 : 1;

                }

                if (isRightSide) {
                    cx = rrx + rwidth + r;
                    g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
                    g.setColor(linkColor);
                    g.drawLine(cx + 2 * r, cy, ccx, cy);
                } else {
                    cx = rrx - r;
                    g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
                    g.setColor(linkColor);
                    g.drawLine(ccx, cy, cx - 2 * r, cy);
                }
//                 !!! more intergroup inlinks?!
                LinkDrawer.drawInIntergroupLink(
                    g,
                    (OutLink) outlinks.firstElement(),
                    this,
                    isRightSide);
//            }

        }

    }

    if (!hilited ) {
        paintSubObjects(g, hilited);
    }

    if (zoom && !isZoomRepaint()) {
        rwidth /= Rscale;
        rheight /= Rscale;
        rrx -= ((rwidth - getRwidth())/2 + ZoomPane.getInstance().getLeftOffset());
        rry -= ((rheight - getRheight())/2 + ZoomPane.VERTICAL_MARGIN);
        if (view.getRx() < 0)
            rrx = rrx < 0 ? 2 : rrx;
        if (view.getRy() < 0)
            rry = rry <= 0 ? 2 : rry;
        g.drawImage(zoomImage, rrx,rry, ZoomPane.getInstance());
    }




}

/**
 * Draws a timeout clock.
 * @param g
 * @param hour
 * @param minute
 * @param x0
 * @param y0
 * @param w
 * @param h
 */
protected static void drawDebugTimeout(Graphics g, int hour, int minute, int x0, int y0, int w, int h)
{
    g.setColor(Color.MAGENTA);

    final int thickness = 3;
    if (w < 5*thickness || h < 5*thickness) return;

    // calculate angles
    double phiHour = (2*Math.PI/12)*hour-Math.PI/2;
    double phiMinute = (2*Math.PI/60)*minute-Math.PI/2;

    int size = (int)Math.min(0.75 * h, 0.75 * w);

    // optimize
    w = w/2;
    h = h/2;
    size = size/2;
    double cosHour = Math.cos(phiHour);
    double sinHour = Math.sin(phiHour);
    double cosMin = Math.cos(phiMinute);
    double sinMin = Math.sin(phiMinute);

    g.drawOval(x0 + w - size + thickness, y0 + h - size + thickness, 2*size - 2*thickness, 2*size - 2*thickness);
    g.drawOval(x0 + w - size, y0 + h - size, size*2, size*2);

    int pointerSize = (3 * size) / 4;
    g.drawLine(x0 + w,     y0 + h, x0 + (int)(w + pointerSize * cosMin),      y0 + (int)(h + pointerSize * sinMin));
    g.drawLine(x0 + w + 1, y0 + h, x0 + (int)(w + pointerSize * cosMin) + 1,  y0 + (int)(h + pointerSize * sinMin));

    pointerSize = size / 2;
    g.drawLine(x0 + w,     y0 + h, x0 + (int)(w + pointerSize * cosHour),      y0 + (int)(h + pointerSize * sinHour));
    g.drawLine(x0 + w + 1, y0 + h, x0 + (int)(w + pointerSize * cosHour) + 1,  y0 + (int)(h + pointerSize * sinHour));

    int[] xs2 = { x0 + w - 5, x0 + w - 5, x0 + w + 5, x0 + w + 5};
    int[] ys2 = { y0 + h - size, y0 + h - size - 5, y0 + h - size - 5, y0 + h - size};
    g.drawPolyline(xs2, ys2, xs2.length);

    for (int i = 0; i < 12; i++)
    {
        int length = 8 + (i%3==0?4:0);
        int x1 = (int)(Math.cos(i*360/12*Math.PI/180)*(size - length));
        int y1 = (int)(Math.sin(i*360/12*Math.PI/180)*(size - length));
        int x2 = (int)(Math.cos(i*360/12*Math.PI/180)*(size - thickness));
        int y2 = (int)(Math.sin(i*360/12*Math.PI/180)*(size - thickness));
        g.drawLine(x0 + w + x1, y0 + h + y1, x0 + w + x2, y0 + h + y2);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public void fieldChanged(VDBFieldData field) {

    boolean repaint = false;

    if (inDebugMode && field.getName().equals(VAL_FIELD))
        // always repaint, VAL field linking is irrelevant in debug mode
        repaint = true;
    else
        if (manageLink(field)) repaint=true;

    if (isVisible(field)) {
        if (!changedFields.contains(field))
            changedFields.addElement(field);
        repaint=true;
    }
    else {
        if (changedFields.contains(field)) {
            changedFields.removeElement(field);
            repaint = true;
        }
    }

    if (repaint) {
        // do not repaint non-viewing group
        // might happen in debug mode and any other
        if (DrawingSurface.getInstance().getViewGroup() == getParent())
        {
            unconditionalValidation();
            com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
        }
    }
}

public boolean isVisible(VDBFieldData field) {
    int visibility = field.getVisibility();
    /*
    boolean link = field.getDbdData().getGUI_type()==DBDConstants.GUI_LINKS ||
        field.getDbdData().getGUI_type()==DBDConstants.GUI_OUTPUT ||
        field.getDbdData().getGUI_type()==DBDConstants.GUI_INPUTS;
    */

    boolean validLink = false;
    Object obj = getSubObject(field.getName());
    if (obj instanceof Linkable && EPICSOutLink.getEndPoint((Linkable)obj) != null) validLink = true;

    if (visibility == VDBFieldData.NEVER_VISIBLE ||
        (visibility == VDBFieldData.NON_DEFAULT_VISIBLE && (field.hasDefaultValue() || !Settings.getInstance().isDefaultVisibility())) ||
        (validLink && Settings.getInstance().isHideLinks())) return false;

    return true;
}

public boolean isOldVisible(VDBFieldData field) {
    int visibility = field.getVisibility();

    if (visibility == VDBFieldData.NEVER_VISIBLE ||
        (visibility == VDBFieldData.NON_DEFAULT_VISIBLE && field.hasDefaultValue())) return false;

    return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixEPICSOutLinks(String prevGroup, String group) {
    super.fixEPICSOutLinks(recordData.getFieldsV().elements(), prevGroup, group);
}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 8:37:37)
 */
private void fixForwardLinks() {

    String targetName = getRecordData().getName();
    EPICSLinkOut source;
    Object unknownLink;
    Enumeration e = this.getStartPoints().elements();
    while (e.hasMoreElements())
    {
        unknownLink = e.nextElement();
        if (unknownLink instanceof EPICSLinkOut)
                source = (EPICSLinkOut)unknownLink;
            else
                continue;    // nothing to fix

        // !!!!

        // now I got source and target, compare values
        String oldTarget = LinkProperties.getTarget(source.getFieldData());
        if (!oldTarget.equals(targetName))
        {
            // not the same, fix it gently as a doctor :)
            String value = source.getFieldData().getValue();
            value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
            source.getFieldData().setValueSilently(value);
            source.fixLinkProperties();
        }

    }

}
/**
 * Goes through link fields (in, out, var, fwd) and cheks
 * if ther are OK, if not it fixes it
 * When record is moved, renames, etc. value of in, out, fwd
 * should be changed, but visual link is still preserved :)
 * (linked list). It compares start point end end point and ...
 * Creation date: (2.5.2001 19:37:46)
 */
public void fixLinks() {

    // links to this record
    fixForwardLinks();

    super.fixLinks();
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 15:00:15)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 */
public com.cosylab.vdct.inspector.InspectableProperty getCommentProperty() {
    if (commentProperty==null)
        commentProperty = new CommentProperty(recordData);
    return commentProperty;
}
/**
 * Insert the method's description here.
 * Creation date: (9.4.2001 13:12:33)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
    return getName();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:16:55)
 * @return java.lang.String
 */
public java.lang.String getFlexibleName() {
    return recordData.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:41:13)
 * @return java.lang.String
 */
public java.lang.String getHashID() {
    return Group.substractObjectName(getName());
}
/**
 * Insert the method's description here.
 * Creation date: (25.4.2001 17:58:03)
 * @return int
 */
public int getHeight() {
    forceValidation();
    return super.getHeight();
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 15:15:51)
 * @return javax.swing.Icon
 */
public javax.swing.Icon getIcon() {
    if (icon==null)
        icon = new javax.swing.ImageIcon(getClass().getResource("/images/record.gif"));
    return icon;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 20:37:11)
 * @return java.lang.String
 */
public String getID() {
    return getName();
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @return int
 */
public int getInX() {
    if (isRight())
        return getX()+getWidth()+(tailSizeOfR+3)*Constants.LINK_RADIOUS;
    else
        return getX()-(tailSizeOfR+3)*Constants.LINK_RADIOUS;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @return int
 */
public int getInY() {
    int mid = getY() + getHeight()/2;
    boolean equal = false;
    int temp = mid;
    Enumeration en = outlinks.elements();
    while(en.hasMoreElements() && !equal) {
        temp = ((OutLink)en.nextElement()).getOutY();
        equal = Math.abs(temp - mid) < Constants.GRID_SIZE/2;
    }
    if (equal) return temp;
    else return mid;
//    return getY()+getHeight()/2;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:31:29)
 * @return java.util.Vector
 */
public Vector getItems() {
    return getLinkMenus(recordData.getFieldsV().elements());
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return java.lang.String
 */
public java.lang.String getLayerID() {
    return getParent().toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4.5.2001 9:54:07)
 * @return java.util.Vector
 */
public int getLinkCount() {
    return outlinks.size();
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 21:40:05)
 * @return java.lang.String
 */
public java.lang.String getName() {
    return recordData.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return com.cosylab.vdct.graphics.objects.OutLink
 */
public OutLink getOutput() {
    if (outlinks.size()==1)
        return (OutLink)outlinks.firstElement();
    else
        return null;
}
    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getAlphaSeparator() {
        if (alphaSeparator==null) alphaSeparator = new GUISeparator("Alphabetical");
        return alphaSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getDBDSeparator() {
        if (dbdSeparator==null) dbdSeparator = new GUISeparator("DBD Order");
        return dbdSeparator;
    }

/**
 * Return properties to be inspected
 * Creation date: (11.1.2001 21:43:31)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {

    // to fix DBD which do not have prompt group set for VAL field
    final String VAL_NAME = "VAL";

    if (mode == GUI_GROUP_ORDER)
    {
        int size = 0;
        VDBFieldData field;    Integer key;
        Hashtable groups = new Hashtable();
        Enumeration e = recordData.getFieldsV().elements();
        while (e.hasMoreElements()) {
            field = (VDBFieldData)e.nextElement();
            /*if (field.getDbdData().getField_type() !=
                com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS)*/ {

                key = new Integer(field.getGUI_type());
                if (groups.containsKey(key) /*VAL DBD fix -> */ && key.intValue()!=DBDConstants.GUI_UNDEFINED /* <- VAL DBD fix*/) {
                    ((Vector)(groups.get(key))).addElement(field);
                    size++;
                }
                // do not add fields with undefined GUI type
                else if (key.intValue()!=DBDConstants.GUI_UNDEFINED /*VAL DBD fix -> */ || field.getName().equals(VAL_NAME) /* <- VAL DBD fix*/) {
                    Vector v = new Vector();
                    v.addElement(field);
                    groups.put(key, v);
                    size+=2;    // separator + property
                }

            }
        }

        Object[] grps;
        grps = new com.cosylab.vdct.util.IntegerQuickSort().sortEnumeration(groups.keys());

        Vector all = new Vector();
        all.addElement(GUIHeader.getDefaultHeader());

        Vector items; int grp;
        for (int gn=0; gn < grps.length; gn++) {
            items = (Vector)groups.get(grps[gn]);
            grp = ((VDBFieldData)(items.firstElement())).getGUI_type();
            all.addElement(new GUISeparator(com.cosylab.vdct.dbd.DBDResolver.getGUIString(grp)));
            all.addAll(items);
        }

        InspectableProperty[] properties = new InspectableProperty[all.size()];
        all.copyInto(properties);
        return properties;
    }
    else if ((mode == SORT_ORDER) ||
               (mode == DBD_ORDER)) {

        VDBFieldData field;
        Vector all = new Vector();

        all.addElement(GUIHeader.getDefaultHeader());

        if (mode == SORT_ORDER)
            all.addElement(getAlphaSeparator());
        else
            all.addElement(getDBDSeparator());

        if (mode==DBD_ORDER)
        {
            DBDFieldData dbdField;
             Enumeration e = ((DBDRecordData)DataProvider.getInstance().getDbdDB().getDBDRecordData(recordData.getType())).getFieldsV().elements();
            while (e.hasMoreElements()) {
                dbdField = (DBDFieldData)e.nextElement();
                field = (VDBFieldData)recordData.getField(dbdField.getName());
                if (/*(field.getDbdData().getField_type() != com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS) &&*/
                    (field.getGUI_type()!=DBDConstants.GUI_UNDEFINED) || field.getName().equals(VAL_NAME))
                        all.addElement(field);
            }
        }
        else
        {
            Enumeration e = recordData.getFieldsV().elements();
            while (e.hasMoreElements()) {
                field = (VDBFieldData)e.nextElement();
                if (/*(field.getDbdData().getField_type() != com.cosylab.vdct.dbd.DBDConstants.DBF_NOACCESS) &&*/
                    (field.getGUI_type()!=DBDConstants.GUI_UNDEFINED) || field.getName().equals(VAL_NAME))
                        all.addElement(field);
                }
        }
        InspectableProperty[] properties = new InspectableProperty[all.size()];
        all.copyInto(properties);

        if (mode == SORT_ORDER)
            if (properties.length>2)
                new com.cosylab.vdct.util.StringQuickSort().sort(properties, 2, properties.length-1);

        return properties;
    }
    else
        return null;
}

/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 21:18:50)
 * @return com.cosylab.vdct.vdb.VDBRecordData
 */
public com.cosylab.vdct.vdb.VDBRecordData getRecordData() {
    return recordData;
}
/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 12:10:18)
 * @return java.util.Vector
 */
public Vector getStartPoints() {
    OutLink out;
    Vector starts = new Vector();
    Enumeration e = outlinks.elements();
    while (e.hasMoreElements()) {
        out = EPICSLinkOut.getStartPoint((Linkable)e.nextElement());
        if (out!=null) starts.addElement(out);
    }
    return starts;
}
/**
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param x int
 * @param y int
 */
public VisibleObject hiliteComponentsCheck(int x, int y) {

    ViewState view = ViewState.getInstance();
    VisibleObject spotted = null;

    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        vo = vo.intersects(x, y);
        if (vo!=null) {
            spotted=vo;
            if (view.getHilitedObject()!=vo) return vo;
        }
    }

    return spotted;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public EPICSLink initializeLinkField(VDBFieldData field) {

    if (!this.containsObject(field.getName()))
    {
        EPICSLink link = null;
        int type = LinkProperties.getType(field);
        switch (type) {
            case LinkProperties.INLINK_FIELD:
                link = new EPICSInLink(this, field);
                break;
            case  LinkProperties.OUTLINK_FIELD:
                link = new EPICSOutLink(this, field);
                break;
            case LinkProperties.FWDLINK_FIELD:
                link = new EPICSFwdLink(this, field);
                break;
            case LinkProperties.VARIABLE_FIELD:
                //link = new EPICSVarLink(this, field);
                link = new EPICSVarOutLink(this, field);
                break;
        }

        if (link!=null)
            addLink(link);
        return link;
    }
    else
        return null;
}
/**
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {
/*
      if ((getRx()<=px) && (getRy()<=py) &&
        ((getRx()+getRwidth())>=px) &&
        ((getRy()+getRheight())>=py))
        return this;
    else
        return hiliteComponentsCheck(px, py);
*/
    // first check on small sub-objects like connectors
    VisibleObject spotted = hiliteComponentsCheck(px, py);
      if ((spotted==null) &&
          (getRx()<=px) && (getRy()<=py) &&
        ((getRx()+getRwidth())>=px) &&
        ((getRy()+getRheight())>=py))
        spotted = this;
    return spotted;
}

/**
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return boolean anyNew
 * @param x1 int
 * @param y1 int
 * @param x2 int
 * @param y2 int
 */
public boolean selectComponentsCheck(int x1, int y1, int x2, int y2) {

    int t;
    if (x1>x2)
        { t=x1; x1=x2; x2=t; }
    if (y1>y2)
        { t=y1; y1=y2; y2=t; }

    ViewState view = ViewState.getInstance();
    boolean anyNew = false;

    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        if ((vo instanceof Selectable) &&
             (vo.intersects(x1, y1, x2, y2)!=null)) {
                if (view.setAsSelected(vo)) anyNew = true;
        }

        //if (vo instanceof SelectableComponents) {
        //    if (((SelectableComponents)vo).selectComponentsCheck(x1, y1, x2, y2)) anyNew = true;
        //}
    }

    return anyNew;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return boolean
 */
public boolean isConnectable() {
    return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @return boolean
 */
public boolean isDisconnected() {
    return disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
    if (disconnected || outlinks.size()!=1)
        return right;
    else {
        OutLink first = (OutLink)outlinks.firstElement();
        if (first.getLayerID().equals(getLayerID()))
            return getRightX()<first.getLeftX()
              || (first.getLeftX()<getLeftX() && getLeftX() < first.getRightX() && first.getRightX() < getRightX());
            //return (first.getOutX()>(getX()+getWidth()/2));
        else
            return right;
    }
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:35:39)
 */
public void manageLinks() {
    VDBFieldData field;
    Enumeration e = recordData.getFieldsV().elements();
    while (e.hasMoreElements()) {
        field = (VDBFieldData)e.nextElement();
        manageLink(field);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 21:58:46)
 * @param newType java.lang.String
 */
public boolean morph(java.lang.String newType) {

    //    copies VDBData
    VDBRecordData recordData = VDBData.morphVDBRecordData(
                DataProvider.getInstance().getDbdDB(), getRecordData(), newType, getName());

    if (recordData==null) {
        Console.getInstance().println("o) Interal error: failed to morph record "+getName()+" ("+getType()+")!");
        return false;
    }

    setRecordData(recordData);

    return true;
}

public void setRecordData(VDBRecordData recordData) {
        this.recordData = recordData;

        //    fix object links
        Object[] objs = new Object[subObjectsV.size()];
        subObjectsV.copyInto(objs);
        for (int i=0; i < objs.length; i++) {
            if (objs[i] instanceof Field)    {
                Field field = (Field)objs[i];
                VDBFieldData fieldData = field.getFieldData();
                VDBFieldData newFieldData = recordData.getField(fieldData.getName());
                if (newFieldData!=null) {
                    field.fieldData=newFieldData;
                } else {
                    removeLink((Linkable)field);
                    field.destroy();    // this also adds an undo action (FieldValueChanged)
                }
            }
        }

        changedFields.clear();
        Enumeration e = recordData.getFieldsV().elements();
        while (e.hasMoreElements()) {
            VDBFieldData fieldData = (VDBFieldData)e.nextElement();
            fieldChanged(fieldData);
        }

        // update inspector
        InspectorManager.getInstance().updateObject(this);
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Morphable#getTargets()
 */
public Object[] getTargets() {
    return DataProvider.getInstance().getRecordTypes();
}

/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 14:14:35)
 * @return boolean
 * @param dx int
 * @param dy int
 */
public boolean move(int dx, int dy) {
    if (checkMove(dx, dy)) {
        x+=dx;
//        y+=dy;
        setY(getY() +dy);
        revalidatePosition();
        moveConnectors(dx, dy);
        return true;
    }
    else
        return false;
}
/**
 * NOTE: only dy &lt; 0 is checked
 */
public boolean moveAsMuchAsPossibleTopUp(int dx, int dy) {

    boolean validMove = checkMove(dx, dy);
    if (!validMove && (getY() < -dy))
        dy = -getY();

    return move(dx, dy);
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 22:02:29)
 * @param group java.lang.String
 */
public boolean moveToGroup(java.lang.String group) {
    String currentParent = Group.substractParentName(recordData.getName());
    if (group.equals(currentParent)) return false;

    //String oldName = getName();
    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(recordData.getName());
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(recordData.getName());;

    // object with new name already exists, add suffix // !!!
    Object obj;
    boolean renameNeeded = false;
    while ((obj=Group.getRoot().findObject(newName, true))!=null)
    {
        if (obj==this)    // it's me :) already moved, fix data
        {
            recordData.setName(newName);
            fixLinks();
            return true;
        }
        else
        {
            renameNeeded = true;
            newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
        }
    }

    if (renameNeeded)
        return rename(newName);

    getParent().removeObject(Group.substractObjectName(getName()));
    setParent(null);
    Group.getRoot().addSubObject(newName, this, true);

    //String oldGroup = Group.substractParentName(recordData.getName());
    recordData.setName(newName);
    /*//fixEPICSInLinks(recordData.getName(), newName);
    //fixEPICSOutLinks(oldGroup, group);            // only if target is moving !!!*/
    fixLinks();
    unconditionalValidation();

    return true;
}
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:58:56)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
private void paintSubObjects(Graphics g, boolean hilited) {
    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
            vo.paint(g, hilited);
    }

}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:40:48)
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public void removeLink(Linkable link) {
    if (getSubObjectsV().contains(link)) {
        Field field = (Field)link;
        field.getFieldData().setPositionIndex(getSubObjectsV().indexOf(link));
        removeObject(field.getFieldData().getName());
    }
}
/**
 * Insert the method's description here.
 * Creation date: (2.5.2001 23:23:32)
 * @param newName java.lang.String
 */
public boolean rename(java.lang.String newName) {

    // name has to be valid

    String newObjName = Group.substractObjectName(newName);
    String oldObjName = Group.substractObjectName(getName());


    if (!oldObjName.equals(newObjName))
    {
        getParent().removeObject(oldObjName);
        String fullName = com.cosylab.vdct.util.StringUtils.replaceEnding(getName(), oldObjName, newObjName);
        getRecordData().setName(fullName);
        getParent().addSubObject(newObjName, this);

        // fix connectors IDs
        Enumeration e = subObjectsV.elements();
        Object obj; Connector connector;
        while (e.hasMoreElements()) {
            obj = e.nextElement();
            if (obj instanceof Connector)
            {
                connector = (Connector)obj;
                String id = connector.getID();
                id = com.cosylab.vdct.util.StringUtils.replaceEnding(id, oldObjName, newObjName);
                connector.setID(id);
            }
        }
    }

    // move if needed
    if (!moveToGroup(Group.substractParentName(newName)))
        fixLinks();            // fix needed

    return true;

}
/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:18:51)
 */
public void revalidateFieldsPosition() {

  int nx, ny, n=0;
  ny = getY()+getHeight();
  Enumeration e = subObjectsV.elements();
  Field field; Object obj;
  while (e.hasMoreElements()) {
    obj = e.nextElement();
    if (obj instanceof Field) {
        field = (Field)obj;
        nx = getX()+(getWidth()-field.getWidth())/2;
        field.revalidatePosition(nx, ny, n);
        ny+=field.getHeight();
        n++;
    }
  }

}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:22:45)
 */
public void revalidatePosition() {
  double Rscale = getRscale();
  setRx((int)(getX()*Rscale));
  setRy((int)(getY()*Rscale));


  // sub-components
  revalidateFieldsPosition();
  revalidateOutlinkConnectors();
}

public void revalidateOutlinkConnectors() {
    Enumeration e = outlinks.elements();
    while (e.hasMoreElements()) {
        Object obj = e.nextElement();
        if (obj instanceof Connector) {
            Connector con = (Connector)obj;
            con.revalidatePosition();
        }
    }
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 */
public void rotate() { right=!right; }
/**
 * Insert the method's description here.
 * Creation date: (27.12.2000 12:45:23)
 * @return boolean
 */
public boolean selectAllComponents() {

    ViewState view = ViewState.getInstance();
    boolean anyNew = false;

    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        if (vo instanceof Selectable)
            if (view.setAsSelected(vo)) anyNew = true;
    }

    return anyNew;
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 17:40:55)
 * @param description java.lang.String
 */
public void setDescription(java.lang.String description) {}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:53)
 * @param id java.lang.String
 */
public void setLayerID(java.lang.String id) {
    // not needed, id is retrieved dynamicaly via parent
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:47:54)
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
public void setOutput(OutLink output, OutLink prevOutput) {
    if (prevOutput!=null) outlinks.removeElement(prevOutput);
    if (!outlinks.contains(output)) {
        outlinks.addElement(output);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @param state boolean
 */
public void setRight(boolean state) { right=state; }
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 14:49:50)
 * @return java.lang.String
 */
public String toString() {
    return recordData.toString();
    // recordData.getName()+" ("+recordData.getType()+")"
}

/**
 *
 * Validates the font size and position.
 * @see validate
 * @param scale
 * @param rwidth
 * @param rheight
 * @return the new rheight for the record
 */
private int validateFont(double scale, int rwidth, int rheight) {

    //  set appropriate font size
    final int x0 = (int)(8*scale);        // insets
    final int y0 = (int)(4*scale);
    final int fieldRowHeight = (int)((Constants.RECORD_HEIGHT-2*4)*0.375);

    Font font;

    // translate name
    if (PluginDebugManager.isDebugState())
    {
      Map macroSet = PluginDebugManager.getDebugPlugin().getMacroSet();
      if (macroSet != null)
          setLabel(VDBTemplateInstance.applyProperties(recordData.getName(), macroSet));
      else
          setLabel(recordData.getName());
    }
    else
        setLabel(recordData.getName());

    if (rwidth<(2*x0)) font = null;
    else
        font = FontMetricsBuffer.getInstance().getAppropriateFont(
                        Constants.DEFAULT_FONT, Font.PLAIN,
                        getLabel(), rwidth-x0, (rheight-y0)/2);

    if (font!=null) {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
        setRlabelX((rwidth-fm.stringWidth(getLabel()))/2);
         setRlabelY(rheight/2+(rheight/2-fm.getHeight())/2+fm.getAscent());
    }
    setFont(font);

    label2 = recordData.getType();
    if (rwidth<(2*x0)) typeFont = null;
    else
        typeFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                         Constants.DEFAULT_FONT, Font.PLAIN,
                         label2, rwidth-x0, (rheight-y0)/2);

    if (typeFont!=null) {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(typeFont);
        rtypeLabelX = (rwidth-fm.stringWidth(label2))/2;
         rtypeLabelY = (rheight/2-fm.getHeight())/2+fm.getAscent();
    }

   // !!! optimize - static

    rfieldRowHeight = (rheight-2*y0)*0.375;

    // code moves record up, when fields are added and down when deleted
    if (oldNumOfFields != changedFields.size()) {
        int difference = oldNumOfFields - changedFields.size();
      oldNumOfFields = changedFields.size();
        //move(0, fieldRowHeight*difference);
        moveAsMuchAsPossibleTopUp(0, fieldRowHeight*difference);
    }

    // increase record size for VAL value and timestamp
    if (PluginDebugManager.isDebugState())
    {
      VDBFieldData fld = getField(VAL_FIELD);
      if (fld != null)
      {
          validateDebug(fld);

          inDebugMode = true;
          rheight *= 2;
      }
      else
          inDebugMode = false;
    }
    else
      inDebugMode = false;

    if (rwidth<(2*x0)) fieldFont = null;
    else
        fieldFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                         Constants.DEFAULT_FONT, Font.PLAIN,
                         fieldMaxStr, rwidth-x0, (int)rfieldRowHeight);

    int ascent = 0;
    //rfieldRowHeight = 0;
    if (fieldFont!=null) {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(fieldFont);
        rfieldLabelX = x0;
         rfieldLabelY = rheight+2*fm.getAscent();
        //rfieldRowHeight = fm.getHeight();
        ascent = fm.getAscent();
    }
    rheight += y0+rfieldRowHeight*changedFields.size()+ascent;
//    firstValidation = false;
    if (validationsCounter <=4)
        validationsCounter++;
    return rheight;
}

public void resetValidationsCounter() {
//    firstValidation = true;
    validationsCounter = 0;
}
//private boolean firstValidation = true;
private int validationsCounter = 0;
/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 20:46:35)
 */
protected void validate() {

//  boolean use = super.getHeight() != 0 && !firstValidation;
    boolean use = super.getHeight() != 0 && validationsCounter > 3;
  int bottomy = getY() + getHeight();
  double scale = getRscale();

  int rwidth = (int)(getWidth()*scale);
  int rheight = (int)(Constants.RECORD_HEIGHT*scale);

  setRheight(rheight);
  setRwidth(rwidth);

  rheight = validateFont(scale, rwidth, rheight);
  // not navigator redraw (it should not fix height!!!)
  // TODO hardcoded minumum scale!!!
  if (scale >= 0.2)
  {
      int height = (int) (rheight/scale);
      if (height != 0) {
          setHeight(height);
          //this prevents records from moving up and down when zooming
          if (use){
              int i = bottomy % Constants.GRID_SIZE;
              if (i!=0 && Settings.getInstance().getSnapToGrid()) {
                  if (i > Constants.GRID_SIZE/2.0)
                      setY(bottomy-height + Constants.GRID_SIZE -i);
                  else
                      setY(bottomy-height -i);
              } else
                  setY(bottomy-height);
          }
      }
  }

  // round fix
  rheight = (int)((getY()+super.getHeight())*scale)-(int)(getY()*scale);
  setRheight(rheight);

  // sub-components
  revalidatePosition();        // rec's height can be different
  validateFields();

}


private void validateDebug(VDBFieldData valField)
{
    double scale = getRscale();
    int rwidth = (int)(getWidth()*scale);
    int rheight = (int)(Constants.RECORD_HEIGHT*scale);

    // set appropriate font size
    int x0 = (int)(8*scale);        // insets
    int y0 = (int)(4*scale);

    debugConnected = valField.isConnected();
    if (!debugConnected && debugTimeoutHour < 0)
    {
        Calendar c = Calendar.getInstance();
        debugTimeoutHour = c.get(Calendar.HOUR_OF_DAY) % 12;
        debugTimeoutMinute = c.get(Calendar.MINUTE);
    }
    else if (debugConnected)
        debugTimeoutHour = -1;

    // select debug color (follows MEDM standard)
    switch (valField.getSeverity())
    {
        // 0 = no alarm
        case 0 :
            debugValueColor = Constants.LINE_COLOR;
            break;
        // 1 = minor
        case 1 :
            debugValueColor = Color.YELLOW;
            break;
        // 2 = major
        case 2 :
            debugValueColor = Color.RED;
            break;
        // 3 = invalid
        default :
            debugValueColor = Constants.LINE_COLOR;
    }

    // TODO for timestamp this could be optimized
    timestamp = valField.getDebugValueTimeStamp();
    timestampFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                    Constants.DEFAULT_FONT, Font.PLAIN,
                    timestamp, rwidth-x0, (rheight-y0)/2-y0);
    if (timestampFont!=null) {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(timestampFont);
        timestampX = (rwidth-fm.stringWidth(timestamp))/2;
        timestampY = rheight/2+(rheight/2-fm.getHeight())/2+fm.getAscent();
    }

    value = valField.getValue();
    valueFont = FontMetricsBuffer.getInstance().getAppropriateFont(
                    Constants.DEFAULT_FONT, Font.PLAIN,
                    value, rwidth-x0, rheight/2-y0);

    if (valueFont!=null) {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(valueFont);
        valueX = (rwidth-fm.stringWidth(value))/2;
        valueY = (rheight/2-fm.getHeight())/2+fm.getAscent();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (26.1.2001 17:19:47)
 */
private void validateFields() {

    Enumeration e = subObjectsV.elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof Field ||
            obj instanceof Connector)
            ((VisibleObject)obj).validate();
    }

}

/**
 */
public VDBFieldData getField(String name) {
    return recordData.getField(name);
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
    return getModes();
}

private static ArrayList getModes()
{
    if (modes==null)
    {
        modes = new ArrayList();
        modes.add("Group");
        modes.add("Alphabetical");
        modes.add("DBD Order");
    }
    return modes;
}

/**
 * @param linkableMacros
 * @param macros
 * @param deep
 */
public void generateMacros(HashMap macros) {
    Enumeration e = recordData.getFieldsV().elements();
    while (e.hasMoreElements())
        LinkManagerObject.checkIfMacroCandidate((VDBFieldData)e.nextElement(), macros);
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.InLink#getMinX()
 */
public int getLeftX() {
    return getX()-(tailSizeOfR+3)*Constants.LINK_RADIOUS;
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.InLink#getMaxX()
 */
public int getRightX() {
    return getX()+getWidth()+(tailSizeOfR+3)*Constants.LINK_RADIOUS;
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Morphable#getType()
 */
public String getType() {
    return getRecordData().getType();
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.MultiInLink#getOutlinks()
 */
public Vector getOutlinks() {
    return outlinks;
}

public void updateFields() {
    Enumeration e = recordData.getFieldsV().elements();
    while (e.hasMoreElements()) {
        VDBFieldData field = (VDBFieldData)e.nextElement();
        if (isVisible(field)) {
            if (!changedFields.contains(field))
                changedFields.addElement(field);
        }
        else {
            if (changedFields.contains(field))
                changedFields.removeElement(field);
        }

    }
    validate();
}

/**
 * Snap to grid. Nearest point is taken.
 */
public void snapToGrid()
{
    int mx = x % Constants.GRID_SIZE;
    // TODO za en piksel strize!!!
//    int my = (y+getHeight()) % Constants.GRID_SIZE;
    int my = (getY()+getHeight()) % Constants.GRID_SIZE;

    final int halfGrid = Constants.GRID_SIZE / 2;
    if (mx > halfGrid)
        mx -= Constants.GRID_SIZE;
    if (my > halfGrid)
        my -= Constants.GRID_SIZE;

    x -= mx;
//    y -= my;
    setY(getY() -my);
}


}
