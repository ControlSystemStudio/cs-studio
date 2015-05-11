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
import com.cosylab.vdct.Constants;

import javax.swing.*;
import java.awt.event.*;

import com.cosylab.vdct.undo.CreateConnectorAction;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.inspector.*;
import com.cosylab.vdct.graphics.popup.*;

/**
 * Insert the type's description here.
 * Creation date: (30.1.2001 12:26:07)
 * @author Matej Sekoranja
 */
public abstract class EPICSLinkOut extends EPICSLink implements OutLink, Popupable, Inspectable {

    class PopupMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();
            if (action.equals(colorString))
            {
                Color newColor = ColorChooser.getColor(selectTitle, getColor());
                if (newColor!=null)
                    setColor(newColor);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(addConnectorString))
            {
                addConnector();
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(moveUpString))
            {
                ((Record)getParent()).moveFieldUp(EPICSLinkOut.this);
            }
            else if (action.equals(moveDownString))
            {
                ((Record)getParent()).moveFieldDown(EPICSLinkOut.this);
            }
            else if (action.equals(removeString))
            {
                destroy();
            }

        }
    }
    private static javax.swing.ImageIcon icon = null;
    public static final char LINK_SEPARATOR = '/';
    private static final String nullString = "";
    private static final String selectTitle = "Select link color...";
    private static final String addConnectorString = "Add connector";
    private static final String colorString = "Color...";
    private static final String moveUpString = "Move Up";
    private static final String moveDownString = "Move Down";
    private static final String removeString = "Remove Link";
    private static GUISeparator recordSeparator = null;
    private static GUISeparator fieldSeparator = null;
    protected InLink inlink = null;
    protected LinkProperties properties;
    private final static String maxLenStr = "NPP NMS";
    protected String label2;
    protected Font font2 = null;
    protected int realLabelLen = Constants.LINK_LABEL_LENGTH;
    protected int labelLen = Constants.LINK_LABEL_LENGTH;
    protected int realHalfHeight = Constants.FIELD_HEIGHT/2;
    protected boolean hasEndpoint = false;

/**
 * EPICSOutLink constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 * @param fieldData com.cosylab.vdct.vdb.VDBFieldData
 */
protected EPICSLinkOut(ContainerObject parent, VDBFieldData fieldData) {
    super(parent, fieldData);
    properties = new LinkProperties(fieldData);
    //updateLink(); // this causes problems in applyVisualData (connectors are not completed)
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:50:51)
 */
public Connector addConnector() {
    String id = generateConnectorID(this);
    String inlinkStr = "";
    String outlinkStr = getID();
    if (inlink!=null) inlinkStr = inlink.getID();
    Connector connector = new Connector(id, (LinkManagerObject)getParent(), this, getInput());
    getParent().addSubObject(id, connector);
    UndoManager.getInstance().addAction(new CreateConnectorAction(connector, inlinkStr, outlinkStr));
    return connector;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.EPICSLinkOut.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.EPICSLinkOut.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}

/**
 * Called when VARIABLE link (source) was destroyed
 */
public void sourceDestroyed() {
    destroy();
}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:11:34)
 */
public void destroy() {
    if (!isDestroyed()) {
        super.destroy();
        destroyChain(inlink, this);
        setInput(null);
        getFieldData().setValue(nullString);
        properties = new LinkProperties(fieldData);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 21:32:42)
 * @param link first inlink of the out
 * @param out source of the link
 */
public static void destroyChain(Linkable link, OutLink out) {
    while (link instanceof OutLink && !(link instanceof EPICSVarOutLink) && !(link instanceof EPICSLinkOut)) {
        out = (OutLink)link;
        link = out.getInput();
        if (out instanceof Connector) {
            ((Connector)out).destroy();            // connectors
        }

    }
//    if (link instanceof EPICSLink)
        //((VisibleObject)link).destroy();
    if (link!=null)
        link.disconnect(out);
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:44:32)
 */
public void disconnect(Linkable disconnector) {
    if (!disconnected && (disconnector==inlink)) {
        super.disconnect(disconnector);
        //setInput(null);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:25:44)
 */
public void fixLinkProperties() {
    LinkProperties newProperties = new LinkProperties(fieldData);
    properties = newProperties;
    setLabel(properties.getOptions());
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 13:01:01)
 * @return java.lang.String
 * @param outlink com.cosylab.vdct.graphics.objects.EPICSLinkOut
 */
public static String generateConnectorID(EPICSLink outlink) {
    //String rootName = Group.substractObjectName(outlink.getFieldData().getRecord().getName())+
    //                  LINK_SEPARATOR+outlink.getFieldData().getName();
    String rootName = Group.substractObjectName(outlink.getFieldData().getFullName());
    rootName = rootName.replace(Constants.FIELD_SEPARATOR, LINK_SEPARATOR);

    if (outlink instanceof TemplateEPICSMacro) {
        if (outlink.getParent() != null)
            rootName = outlink.getParent().getHashID() + Constants.CONNECTOR_FIELD_SEPARATOR + rootName;
    }

    if (!outlink.getParent().containsObject(rootName)){
        return rootName;}
    else {
        String name;
        int count = 0;
        do {
            count++;
            name = rootName+(new Integer(count)).toString();
        } while (outlink.getParent().containsObject(name));
        return name;
    }
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty
 */
public com.cosylab.vdct.inspector.InspectableProperty getCommentProperty() {
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 11:26:33)
 * @return com.cosylab.vdct.graphics.objects.InLink
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public static InLink getEndPoint(Linkable link) {
    //!!!
    while (link instanceof OutLink && !(link instanceof EPICSVarOutLink))
        link = ((OutLink)link).getInput();
    return (InLink)link;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getFieldSeparator() {
    if (fieldSeparator==null) fieldSeparator = new GUISeparator("Field");
    return fieldSeparator;
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
 * Creation date: (29.1.2001 22:22:14)
 * @return com.cosylab.vdct.graphics.objects.InLink
 */
public InLink getInput() {
    return inlink;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
    Vector items = new Vector();

    ActionListener al = createPopupmenuHandler();

    JMenuItem colorItem = new JMenuItem(colorString);
    colorItem.addActionListener(al);
    items.addElement(colorItem);

    JMenuItem addItem = new JMenuItem(addConnectorString);
    addItem.addActionListener(al);
    items.addElement(addItem);

    items.add(new JSeparator());

    if (getParent() instanceof Record)
    {
        Record parRec = (Record)getParent();
        boolean isFirst = parRec.isFirstField(this);
        boolean isLast = parRec.isLastField(this);


        if (!isFirst)
        {
            JMenuItem upItem = new JMenuItem(moveUpString);
            upItem.addActionListener(al);
            upItem.setIcon(new ImageIcon(getClass().getResource("/images/up.gif")));
            items.addElement(upItem);
        }

        if (!isLast)
        {
            JMenuItem downItem = new JMenuItem(moveDownString);
            downItem.addActionListener(al);
            downItem.setIcon(new ImageIcon(getClass().getResource("/images/down.gif")));
            items.addElement(downItem);
        }

        if (!(isFirst && isLast))
            items.add(new JSeparator());
    }

    JMenuItem removeItem = new JMenuItem(removeString);
    removeItem.addActionListener(al);
    items.addElement(removeItem);

    return items;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:23:39)
 * @return com.cosylab.vdct.vdb.LinkProperties
 */
public com.cosylab.vdct.vdb.LinkProperties getLinkProperties() {
    return properties;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:22:37)
 * @return java.lang.String
 */
public String getName() {
    return fieldData.getFullName();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getOutX() {
    if (isRight())
        return getRightX();
    else
        return getLeftX();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getOutY() {
    return getY()+getHeight()/2;
}
/**
 * Return properties to be inspected
 * Creation date: (1.2.2001 22:22:37)
 * @return com.cosylab.vdct.inspector.InspectableProperty[]
 */
public com.cosylab.vdct.inspector.InspectableProperty[] getProperties(int mode) {
    InspectableProperty[] properties = new InspectableProperty[1+4];
    properties[0]=GUIHeader.getDefaultHeader();
    properties[1]=getRecordSeparator();
    properties[2]=new FieldInfoProperty(fieldData.getRecord().getField("DTYP"));
    properties[3]=getFieldSeparator();
    properties[4]=fieldData;
    return properties;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:48:39)
 * @return int
 */
public int getQueueCount() {
    return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getRecordSeparator() {
    if (recordSeparator==null) recordSeparator = new GUISeparator("Record");
    return recordSeparator;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 11:25:46)
 * @return com.cosylab.vdct.graphics.objects.OutLink
 * @param link com.cosylab.vdct.graphics.objects.Linkable
 */
public static OutLink getStartPoint(Linkable link) {
    while (!(link instanceof EPICSVarOutLink) && !(link instanceof EPICSLinkOut) && link instanceof InLink)
        link = ((InLink)link).getOutput();
    return (OutLink)link;
}

public static InLink getTarget(LinkProperties link) {
    return getTarget(link, false, false);
}

public static InLink getTarget(LinkProperties link, boolean allowLinkOutAsTarget) {
    return getTarget(link, allowLinkOutAsTarget, false);
}

/**
 * get/create target link field
 * Creation date: (30.1.2001 13:40:51)
 * @return com.cosylab.vdct.graphics.objects.InLink
 * @param link com.cosylab.vdct.vdb.LinkProperties
 */
public static InLink getTarget(LinkProperties link, boolean allowLinkOutAsTarget, boolean doNotSearchRecordFields) {

    String recName = link.getRecord();
    // !!! check for getType()==LinkProperties.NOT_VALID
    if ((recName==null) || recName.equals(nullString)) return null;

    Object otherLinkObj = Group.getRoot().getLookupTable().get(link.getTarget());
    if (otherLinkObj!=null && otherLinkObj instanceof InLink)
    {
        InLink templateLink = (InLink)otherLinkObj;
        if (templateLink!=null)
        {
    //        ((TemplateEPICSVarLink/TemplateEPICSPort)templateLink).setDestroyed(false);
            ((VisibleObject)templateLink).setDestroyed(false);
            return templateLink;
        }
    }

    // else macro check (w/o .VAL ending)
    otherLinkObj = Group.getRoot().getLookupTable().get(link.getRecord());
    if (otherLinkObj!=null && otherLinkObj instanceof InLink)
        return (InLink)otherLinkObj;

    if (doNotSearchRecordFields)
        return null;

    Object obj = Group.getRoot().findObject(recName, true);
    if (obj==null || !(obj instanceof Record)) return null;
    Record record = (Record)obj;
    if (link.getType()==LinkProperties.FWDLINK_FIELD) {
        //if (!link.getVarName().equals("PROC"))        // !!! proc
        // check if variable exists
        if (record.getRecordData().getField(link.getVarName())==null)
            return null;
        else
            return record;
    }

    InLink inlink = null;
    // already created link field
    if (record.containsObject(link.getVarName()))
    {
        Object subObject = record.getSubObject(link.getVarName());
        if (subObject instanceof InLink)
            inlink = (InLink)subObject;
    }
    // not yet
    else {
        VDBFieldData target = record.getRecordData().getField(link.getVarName());
        if ((target==null) ||
            (LinkProperties.getType(target)!=LinkProperties.VARIABLE_FIELD && !allowLinkOutAsTarget)) return null;
        else {
            //inlink = new EPICSVarLink(record, target);
            //record.addLink(inlink);

            EPICSLink el = record.initializeLinkField(target);
            if (el instanceof InLink)
            {
                inlink = (InLink)el;
            }
            else
                inlink = null;
        }
    }
    return inlink;
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
    if (disconnected || inlink==null ||
        !inlink.getLayerID().equals(getLayerID()))
        return super.isRight();
    else {
        return getRightX()<=inlink.getLeftX()
          || (getLeftX()<inlink.getLeftX() && inlink.getLeftX()<getRightX() && getRightX()<inlink.getRightX());
        /*if (inlink instanceof Connector) {
            return (inlink.getInX()>(getX()+getWidth()/2));
        }
        else if (inlink instanceof EPICSLinkOut) {            // not cycling
            EPICSLinkOut obj = (EPICSLinkOut)inlink;
            return getMaxX()<obj.getMinX() ||
                (getMaxX()>obj.getMaxX() && getMinX()<obj.getMaxX());
        }
        else if (inlink instanceof VisibleObject) {            // do not cycle !!!
            VisibleObject obj = (VisibleObject)inlink;
            return ((obj.getX()+obj.getWidth()/2)>(getX()+getWidth()/2));
        }
        else
            return super.isRight();*/
    }
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:46:30)
 * @param newColor java.awt.Color
 */
public void setColor(Color newColor) {
        super.setColor(newColor);
        Linkable link = this;
        while (link instanceof OutLink && !(link instanceof EPICSVarOutLink)) {
            link = ((OutLink)link).getInput();
            if (link instanceof VisibleObject)
                ((VisibleObject)link).setColor(newColor);
        }
        if (link instanceof VisibleObject)
            ((VisibleObject)link).setColor(newColor);
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 */
public void setInput(InLink input) {
    if (inlink==input) return;
    if (inlink!=null)
        inlink.disconnect(this);
    inlink=input;
    if (inlink!=null) disconnected=false;
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 22:31:14)
 */
public String toString() {
    return getName();
}

/**
 */
public void valueWithNoRecord()
{
    destroy();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:25:44)
 */
private void updateLink() {
    LinkProperties newProperties = new LinkProperties(fieldData);

    if (newProperties.getRecord()==null) {            // empty field
        valueWithNoRecord();
        return;
    }
    else if (!newProperties.getRecord().equals(properties.getRecord()) ||
             !newProperties.getVarName().equals(properties.getVarName()) ||
             !hasEndpoint) {
        // find endpoint
        Linkable preendpoint = this;
        Linkable endpoint = getInput();
        while (((endpoint instanceof InLink) && (endpoint instanceof OutLink)) && !(endpoint instanceof EPICSVarOutLink)) {
            preendpoint = endpoint;
            endpoint = ((OutLink)endpoint).getInput();
        }
        if ((endpoint!=null) && hasEndpoint) ((InLink)endpoint).disconnect(preendpoint);
        //OutLink lol = getTarget(properties).getOutput();
        InLink il = getTarget(newProperties);
        OutLink ol = (OutLink)preendpoint;
        ol.setInput(il);
        if (il!=null) {
            il.setOutput(ol, null);
            hasEndpoint = true;
        }
        else hasEndpoint = false;
    }

    properties = newProperties;
    setLabel(properties.getOptions());
}

protected void validateFontAndDimension(double Rscale, int rwidth, int rheight) {
    label2 = properties.getOptions();
    labelLen = (int)(Constants.LINK_LABEL_LENGTH*Rscale);

    if (labelLen<15) font2 = null;
    else {
      font2 = FontMetricsBuffer.getInstance().getAppropriateFont(
                      Constants.DEFAULT_FONT, Font.PLAIN,
//                      label2, labelLen, getRheight());
                      maxLenStr, labelLen, rheight);
      if (font2!=null) {
          FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font2);
          realLabelLen = fm.stringWidth(label2);
          realHalfHeight = fm.getAscent()-fm.getHeight()/2;
      }
    }
    r = (int)(Rscale*Constants.LINK_RADIOUS);
    rtailLen = (int)(Rscale*Constants.TAIL_LENGTH);
}
/**
 * Insert the method's description here.
 * Creation date: (31.1.2001 18:27:35)
 */
public void validate() {
    super.validate();
    validateFontAndDimension(getRscale(), getRwidth(), getRheight());
}
/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:24:26)
 */
public void valueChanged() {
    updateLink();
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
    return null;
}


/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
 */
public int getMode()
{
    return OutLink.NORMAL_MODE;
}

/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#validateLink()
 */
public void validateLink()
{
}



public int getRightX() {
    if (inlink==null || !getLayerID().equals(inlink.getLayerID()))
        return getX()+getWidth()+Constants.TAIL_LENGTH;
    else
        return getX()+getWidth()+Constants.TAIL_LENGTH + getVerticalPosition()*Constants.LINK_SLOT_WIDTH;
}

public int getLeftX() {
    if (inlink==null || !getLayerID().equals(inlink.getLayerID()))
        return getX()-Constants.TAIL_LENGTH;
    else
        return getX()-Constants.TAIL_LENGTH - getVerticalPosition()*Constants.LINK_SLOT_WIDTH;
}

}
