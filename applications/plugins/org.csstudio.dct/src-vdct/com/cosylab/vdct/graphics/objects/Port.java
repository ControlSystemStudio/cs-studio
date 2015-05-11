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

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.LinkCommand;
import com.cosylab.vdct.graphics.*;

import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.undo.CreateConnectorAction;
import com.cosylab.vdct.undo.UndoManager;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.GUIHeader;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.LinkProperties;
import com.cosylab.vdct.vdb.PortDescriptionProperty;
import com.cosylab.vdct.vdb.VDBData;
import com.cosylab.vdct.vdb.VDBPort;

import javax.swing.*;

import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class Port extends VisibleObject implements Descriptable, Movable, OutLink, Popupable, Selectable, Inspectable, Flexible
{
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
            else if (action.equals(removeLinkString))
            {
                removeLink();
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(removePortString))
            {
                destroy();
                ViewState.getInstance().deselectObject(Port.this);
                com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.DeleteAction(Port.this));
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(removePortDefString))
            {
                data.getTemplate().removePort(getName());
                ViewState.getInstance().deselectObject(Port.this);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(constantString)) {
                setMode(OutLink.CONSTANT_PORT_MODE);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(inputString)) {
                setMode(OutLink.INPUT_PORT_MODE);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(outputString)) {
                setMode(OutLink.OUTPUT_PORT_MODE);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(startLinkingString))
            {
                LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
                cmd.setData(Port.this, data);
                 cmd.execute();
            }
            else if (action.equals(textNorth)) {
                setTextPositionNorth(true);
            }
            else if (action.equals(textSide)) {
                setTextPositionNorth(false);
            }

        }
    }

    protected InLink inlink = null;
    protected boolean disconnected = true;
    protected LinkProperties properties = null;
    private boolean hasEndpoint = false;

    //private static final String descriptionString = "Description";
    private static final String selectTitle = "Select link color...";
    private static final String addConnectorString = "Add connector";
    private static final String colorString = "Color...";
    private static final String removeLinkString = "Remove Link";
    private static final String startLinkingString = "Start linking...";
    private static final String removePortString = "Hide Port";
    private static final String removePortDefString = "Remove Port";

    private static final String modeString = "Port Mode";
    private static final String constantString = "CONSTANT";
    private static final String inputString = "INPUT";
    private static final String outputString = "OUTPUT";

    private static final String CONNECTOR_ID_START = "PORT/";
    private static final String textPosition = "Text Position";
    private static final String textNorth = "TOP";
    private static final String textSide = "SIDE";

    private static final String nullString = "";

    private int mode = OutLink.CONSTANT_PORT_MODE;

    private static javax.swing.ImageIcon icon = null;
    private static GUISeparator portSeparator = null;
    protected VDBPort data = null;

    protected int rightXtranslation = 0;
    protected int rightYtranslation = 0;
    protected int leftXtranslation = 0;
    protected int leftYtranslation = 0;
    protected Polygon leftPoly;
    protected Polygon rightPoly;

    /** if textPositionNorth=true text is positioned on the top of
      * the macro, if false it is on the side */
     private boolean textPositionNorth = true;
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:22:29)
 */
public Port(VDBPort data, ContainerObject parent, int x, int y) {
    super(parent);
    this.data = data;

    setColor(Constants.FRAME_COLOR);

    setWidth(Constants.LINK_STUB_SIZE);
    setHeight(Constants.LINK_STUB_SIZE);

    setX(x); setY(y);

    properties = new LinkProperties(data);

    data.setVisibleObject(this);

    // initialize polygon so that it contains 5 points
    int[] pts = new int[5];
    leftPoly = new Polygon(pts, pts, 5);
    rightPoly = new Polygon(pts, pts, 5);

    valueChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param visitor com.cosylab.vdct.graphics.objects.Visitor
 */
public void accept(Visitor visitor) {}

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
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.Connector.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.Port.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 16:40:51)
 */
public void destroy() {
    if (!isDestroyed()) {
        // keep the value
        String currentValue = data.getValue();
        removeLink();
        super.destroy();
        data.setVisibleObject(null);
        getParent().removeObject(getName());
        data.setValueSilently(currentValue);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 16:40:51)
 */
public void removeLink() {
    if (!isDestroyed()) {
        disconnected = true;
        EPICSLinkOut.destroyChain(inlink, this);
        setInput(null);
        data.setValue(nullString);
        properties = new LinkProperties(data);
    }
}


/**
 * ...
 * Creation date: (29.1.2001 20:05:51)
 */
public void disconnect(Linkable disconnector) {
    if (!disconnected && (inlink==disconnector) ) {
        disconnected = true;
        data.setValue(nullString);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
protected void draw(java.awt.Graphics g, boolean hilited) {

    ViewState view = ViewState.getInstance();

    double Rscale = view.getScale();
    boolean zoom = (Rscale < 1.0) && view.isZoomOnHilited() && view.isHilitedObject(this);

    if (zoom) {
        zoomImage = ZoomPane.getInstance().startZooming(this,!isZoomRepaint());
    }

    int rrx = getRx() - view.getRx();
    int rry = getRy() - view.getRy();

    int rwidth = getRwidth();
    int rheight = getRheight();

    boolean rightSide = isRight();

    if (inlink!=null)
    {
        // draw link
        g.setColor(hilited && view.isHilitedObject(this) && !zoom ? Constants.HILITE_COLOR : getVisibleColor());
        LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
    }

    // clipping
    if (!((rrx > view.getViewWidth())
        || (rry > view.getViewHeight())
        || ((rrx + rwidth) < 0)
        || ((rry + rheight) < 0)) || isZoomRepaint()) {

        if (isZoomRepaint()) {
            rrx = ZoomPane.getInstance().getLeftOffset();
            rry = ZoomPane.getInstance().getTopOffset();
        }

        Polygon poly = null;
        if (rightSide)
        {
            poly = rightPoly;
            poly.translate(rrx-rightXtranslation, rry-rightYtranslation);
            rightXtranslation = rrx; rightYtranslation = rry;
        }
        else
        {
            poly = leftPoly;
            poly.translate(rrx-leftXtranslation, rry-leftYtranslation);
            leftXtranslation = rrx; leftYtranslation = rry;
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

        g.fillPolygon(poly);

        Color drawColor;
        if (!hilited)
            drawColor = Constants.FRAME_COLOR;
        else
            drawColor =
                (view.isHilitedObject(this))
                    ? Constants.HILITE_COLOR
                    : Constants.FRAME_COLOR;

        g.setColor(drawColor);
        g.drawPolygon(poly);

        if (getFont()!=null)    {
            FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(getFont());
            if (textPositionNorth) {
                setRlabelX(rwidth/2-fm.stringWidth(getLabel())/2);
                setRlabelY(-fm.getHeight()+fm.getAscent());
            } else {
                int s = (int)(getScale()*Constants.LINK_STUB_SIZE/4.0 + 2);
                setRlabelY((rheight + fm.getAscent() - fm.getDescent())/2);
                if (!isRight()) {
                    setRlabelX(rwidth + s);
                } else {
                    setRlabelX(-fm.stringWidth(getLabel())- s);
                }
            }
            g.setFont(getFont());
            g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
        }







    }

    if (zoom) {
        rwidth /= Rscale;
        rheight /= Rscale;
        rrx -= ((rwidth - getRwidth())/2 + ZoomPane.getInstance().getLeftOffset());
        rry -= ((rheight - getRheight())/2 + ZoomPane.getInstance().getTopOffset());
        if (view.getRx() < 0)
            rrx = rrx < 0 ? 2 : rrx;
        if (view.getRy() < 0)
            rry = rry <= 0 ? 2 : rry;
        g.drawImage(zoomImage, rrx,rry, ZoomPane.getInstance());
    }

}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:04:05)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
    return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 16:43:08)
 * @return java.lang.String
 */
public java.lang.String getHashID() {
    return getID();
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:31:26)
 * @return java.lang.String
 */
public java.lang.String getID() {
    return data.getName();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
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

    // no connectors for ports yet

    JMenuItem addItem = new JMenuItem(addConnectorString);
    addItem.setEnabled(!isDisconnected());
    addItem.addActionListener(al);
    items.addElement(addItem);


    // modes
    items.addElement(new JSeparator());

    JMenu modeMenu = new JMenu(modeString);
    items.addElement(modeMenu);

    JRadioButtonMenuItem constantModeItem = new JRadioButtonMenuItem(constantString, getMode()==OutLink.CONSTANT_PORT_MODE);
    constantModeItem.setEnabled(getMode()!=OutLink.CONSTANT_PORT_MODE);
    constantModeItem.addActionListener(al);
    modeMenu.add(constantModeItem);

    JRadioButtonMenuItem inputModeItem = new JRadioButtonMenuItem(inputString, getMode()==OutLink.INPUT_PORT_MODE);
    inputModeItem.setEnabled(getMode()!=OutLink.INPUT_PORT_MODE);
    inputModeItem.addActionListener(al);
    modeMenu.add(inputModeItem);

    JRadioButtonMenuItem outputModeItem = new JRadioButtonMenuItem(outputString, getMode()==OutLink.OUTPUT_PORT_MODE);
    outputModeItem.setEnabled(getMode()!=OutLink.OUTPUT_PORT_MODE);
    outputModeItem.addActionListener(al);
    modeMenu.add(outputModeItem);


    JMenu textMenu = new JMenu(textPosition);
    items.addElement(textMenu);

    JRadioButtonMenuItem textNorthItem = new JRadioButtonMenuItem(textNorth, textPositionNorth);
    textNorthItem.setEnabled(!textPositionNorth);
    textNorthItem.addActionListener(al);
    textMenu.add(textNorthItem);

    JRadioButtonMenuItem textSideItem = new JRadioButtonMenuItem(textSide, !textPositionNorth);
    textSideItem.setEnabled(textPositionNorth);
    textSideItem.addActionListener(al);
    textMenu.add(textSideItem);

    /*
    items.add(new JSeparator());

    JMenuItem descItem = new JMenuItem(descriptionString);
    descItem.setEnabled(false);
    descItem.addActionListener(al);
    items.addElement(descItem);
    */

    items.add(new JSeparator());

    if (isDisconnected())
    {
        JMenuItem linkItem = new JMenuItem(startLinkingString);
        linkItem.addActionListener(al);
        items.addElement(linkItem);
    }
    else
    {
        JMenuItem removeLinkItem = new JMenuItem(removeLinkString);
        removeLinkItem.addActionListener(al);
        items.addElement(removeLinkItem);
    }

    JMenuItem removePortItem = new JMenuItem(removePortString);
    removePortItem.addActionListener(al);
    items.addElement(removePortItem);

    JMenuItem removePortDefItem = new JMenuItem(removePortDefString);
    removePortDefItem.addActionListener(al);
    items.addElement(removePortDefItem);

    return items;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return java.lang.String
 */
public String getLayerID() {
    return getParent().toString();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
    if (disconnected || inlink==null ||
        !inlink.getLayerID().equals(getLayerID()))
        return true;
    else {
        return getRightX()<=inlink.getLeftX() ||
            (getLeftX()<inlink.getLeftX() && inlink.getLeftX()<getRightX() && getRightX()<inlink.getRightX());
        /*if (inlink instanceof Connector) {
            return (inlink.getInX()>(getX()+getWidth()/2));
        }
        else if (inlink instanceof VisibleObject) {            // do not cycle !!!
            VisibleObject obj = (VisibleObject)inlink;
            return ((obj.getX()+obj.getWidth()/2)>(getX()+getWidth()/2));
        }
        else
            return true;*/
    }
}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getOutX() {
    // ??? what is nicer
    //boolean right = !isRight();

    boolean right = isRight();
    if (getMode()==OutLink.CONSTANT_PORT_MODE) right=!right;

    if (right)
        return getX();
    else
        return getX()+getWidth();
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
 * Insert the method's description here.
 * Creation date: (30.1.2001 14:50:26)
 * @return int
 */
public int getQueueCount() {
        return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 * @return boolean
 */
public boolean isConnectable() {
    return !disconnected;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @return boolean
 */
public boolean isDisconnected() {
    return disconnected;
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
        y+=dy;
        revalidatePosition();
        return true;
    }
    else
        return false;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
public void revalidatePosition() {
  double Rscale = getRscale();
  setRx((int)(getX()*Rscale));
  setRy((int)(getY()*Rscale));
}
/**
 * Insert the method's description here.
 * Creation date: (24.4.2001 18:04:05)
 * @param description java.lang.String
 */
public void setDescription(java.lang.String description) {}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
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
 * Creation date: (29.1.2001 20:05:51)
 * @param id java.lang.String
 */
public void setLayerID(String id) {
    // not needed, dynamicaly retrieved via parent
}

private void validateFontAndPolygon(double Rscale, int rwidth, int rheight) {
    leftXtranslation = 0; leftYtranslation = 0;
    rightXtranslation = 0; rightYtranslation = 0;

    if (getMode() == OutLink.OUTPUT_PORT_MODE)
    {
      setWidth(Constants.GRID_SIZE);
        setHeight(Constants.GRID_SIZE);

        // left poly
        leftPoly.xpoints[0]=0;
        leftPoly.xpoints[1]=rwidth/2;
        leftPoly.xpoints[2]=rwidth;
        leftPoly.xpoints[3]=leftPoly.xpoints[2];
        leftPoly.xpoints[4]=leftPoly.xpoints[1];

        leftPoly.ypoints[0]=rheight/2;
        leftPoly.ypoints[1]=rheight;
        leftPoly.ypoints[2]=leftPoly.ypoints[1];
        leftPoly.ypoints[3]=0;
        leftPoly.ypoints[4]=leftPoly.ypoints[3];


        // right poly
        rightPoly.xpoints[0]=0;
        rightPoly.xpoints[1]=rightPoly.xpoints[0];
        rightPoly.xpoints[2]=rwidth/2;
        rightPoly.xpoints[3]=rwidth;
        rightPoly.xpoints[4]=rightPoly.xpoints[2];

        rightPoly.ypoints[0]=0;
        rightPoly.ypoints[1]=rheight;
        rightPoly.ypoints[2]=rightPoly.ypoints[1];
        rightPoly.ypoints[3]=rheight/2;
        rightPoly.ypoints[4]=rightPoly.ypoints[0];
    }
    else if (getMode() == OutLink.INPUT_PORT_MODE)
    {
        setWidth(Constants.GRID_SIZE);
        setHeight(Constants.GRID_SIZE);

        // left poly
        leftPoly.xpoints[0]=rwidth/2;
        leftPoly.xpoints[1]=0;
        leftPoly.xpoints[2]=rwidth;
        leftPoly.xpoints[3]=leftPoly.xpoints[2];
        leftPoly.xpoints[4]=leftPoly.xpoints[1];

        leftPoly.ypoints[0]=rheight/2;
        leftPoly.ypoints[1]=rheight;
        leftPoly.ypoints[2]=leftPoly.ypoints[1];
        leftPoly.ypoints[3]=0;
        leftPoly.ypoints[4]=leftPoly.ypoints[3];


        // right poly
        rightPoly.xpoints[0]=0;
        rightPoly.xpoints[1]=rightPoly.xpoints[0];
        rightPoly.xpoints[2]=rwidth;
        rightPoly.xpoints[3]=rwidth/2;
        rightPoly.xpoints[4]=rightPoly.xpoints[2];

        rightPoly.ypoints[0]=0;
        rightPoly.ypoints[1]=rheight;
        rightPoly.ypoints[2]=rightPoly.ypoints[1];
        rightPoly.ypoints[3]=rheight/2;
        rightPoly.ypoints[4]=rightPoly.ypoints[0];
    }
    else if (getMode() == OutLink.CONSTANT_PORT_MODE)
    {
      int factor = 4;

        setWidth(Constants.GRID_SIZE*factor);
        setHeight(Constants.GRID_SIZE);

        rwidth /= factor;

        // left poly
        leftPoly.xpoints[0]=0;
        leftPoly.xpoints[1]=rwidth/2;
        leftPoly.xpoints[2]=rwidth*factor;
        leftPoly.xpoints[3]=leftPoly.xpoints[2];
        leftPoly.xpoints[4]=leftPoly.xpoints[1];

        leftPoly.ypoints[0]=rheight/2;
        leftPoly.ypoints[1]=rheight;
        leftPoly.ypoints[2]=leftPoly.ypoints[1];
        leftPoly.ypoints[3]=0;
        leftPoly.ypoints[4]=leftPoly.ypoints[3];


        // right poly
        rightPoly.xpoints[0]=0;
        rightPoly.xpoints[1]=rightPoly.xpoints[0];
        rightPoly.xpoints[2]=factor*rwidth-rwidth/2;
        rightPoly.xpoints[3]=rwidth*factor;
        rightPoly.xpoints[4]=rightPoly.xpoints[2];

        rightPoly.ypoints[0]=0;
        rightPoly.ypoints[1]=rheight;
        rightPoly.ypoints[2]=rightPoly.ypoints[1];
        rightPoly.ypoints[3]=rheight/2;
        rightPoly.ypoints[4]=rightPoly.ypoints[0];

        rwidth *= 4;
    }

    setLabel(getName());

      ///!!! optimize static

    Font font = FontMetricsBuffer.getInstance().getAppropriateFont(
                    Constants.DEFAULT_FONT, Font.PLAIN,
                    getLabel(), rwidth*4, rheight);

    if (font!=null)
    {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
        setRlabelX(rwidth/2-fm.stringWidth(getLabel())/2);
         setRlabelY(-fm.getHeight()+fm.getAscent());
    }

    setFont(font);

}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 20:05:52)
 */
protected void validate() {
  revalidatePosition();

  double Rscale = getRscale();

  // to make it nice, do /2)*2
  int rwidth = (int)(getWidth()*Rscale/2)*2;
  int rheight = (int)(getHeight()*Rscale/2)*2;

  setRwidth(rwidth);
  setRheight(rheight);

  validateFontAndPolygon(Rscale, rwidth, rheight);

}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 12:46:30)
 * @param newColor java.awt.Color
 */
public void setColor(Color newColor) {
        super.setColor(newColor);
        Linkable link = this;
        while ((link instanceof OutLink) && !(link instanceof EPICSLinkOut) && !(link instanceof EPICSVarOutLink)) {
            link = ((OutLink)link).getInput();
            if (link instanceof VisibleObject && !(link instanceof EPICSLinkOut))
                ((VisibleObject)link).setColor(newColor);
        }
        if (link instanceof VisibleObject && !(link instanceof EPICSLinkOut))
            ((VisibleObject)link).setColor(newColor);
}

/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
 */
public int getMode()
{
    return mode;
}

/**
 */
public void setMode(int mode)
{
    this.mode = mode;

    // reload symbol
    forceValidation();
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getCommentProperty()
 */
public InspectableProperty getCommentProperty()
{
    return null;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getIcon()
 */
public Icon getIcon()
{
    if (icon==null)
        icon = new javax.swing.ImageIcon(getClass().getResource("/images/port.gif"));
    return icon;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
 */
public ArrayList getModeNames()
{
    return null;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getName()
 */
public String getName()
{
    return getID();
}

/**
 * @see java.lang.String#toString()
 */
public String toString()
{
    return getID();
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
 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
 */
public InspectableProperty[] getProperties(int mode)
{
    InspectableProperty[] properties = new InspectableProperty[4];

    properties[0]=GUIHeader.getDefaultHeader();
    properties[1]=getPortSeparator();
    properties[2]=data;
    properties[3]=new PortDescriptionProperty(data);

    return properties;
}



/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:25:44)
 */
private void updateLink() {
    LinkProperties newProperties = new LinkProperties(data);

    if (newProperties.getRecord()==null) {            // empty field
        removeLink();
        return;
    }
    else if (!newProperties.getRecord().equals(properties.getRecord()) ||
             !newProperties.getVarName().equals(properties.getVarName()) ||
             !hasEndpoint) {
        // find endpoint
        Linkable preendpoint = this;
        Linkable endpoint = getInput();
        while (!(endpoint instanceof EPICSLinkOutIn) && !(endpoint instanceof EPICSVarOutLink) && (endpoint instanceof InLink) && (endpoint instanceof OutLink) && !(endpoint instanceof EPICSVarOutLink)) {
            preendpoint = endpoint;
            endpoint = ((OutLink)endpoint).getInput();
        }
        if ((endpoint!=null) && hasEndpoint) ((InLink)endpoint).disconnect(preendpoint);
        //OutLink lol = getTarget(properties).getOutput();
        InLink il = EPICSLinkOut.getTarget(newProperties, true);
        OutLink ol = (OutLink)preendpoint;
        ol.setInput(il);
        if (il!=null) {
            il.setOutput(ol, null);
            hasEndpoint = true;
        }
        else hasEndpoint = false;
    }

    properties = newProperties;

}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 12:25:44)
 */
public void valueChanged()
{
    updateLink();

    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#setDestroyed(boolean)
 */
public void setDestroyed(boolean newDestroyed)
{
    super.setDestroyed(newDestroyed);

    if (!newDestroyed)
    {
        // set data appropriate visibleObject
        data.setVisibleObject(this);

        // revalidate link
        valueChanged();
    }
}

/**
 */
public void rename(String oldName, String newName)
{
    getParent().removeObject(oldName);
    getParent().addSubObject(newName, this);

    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * @see com.cosylab.vdct.graphics.objects.EPICSLink#fixLinkProperties()
 */
public void fixLinkProperties()
{
    LinkProperties newProperties = new LinkProperties(data);
    properties = newProperties;
}

    /**
     * Returns the data.
     * @return VDBPort
     */
    public VDBPort getData()
    {
        return data;
    }

/**
 * @see com.cosylab.vdct.graphics.objects.OutLink#validateLink()
 */
public void validateLink()
{
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMinX()
 */
public int getLeftX() {
    return getX();
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMaxX()
 */
public int getRightX() {
    return getX()+getWidth();
}

public int getTopOffset() {
    FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(getFont());
    return fm.getAscent();
}

public Connector addConnector() {
    if (inlink instanceof Connector)
    {
        return ((Connector)inlink).addConnector();
    }
    else
    {
        ContainerObject inlinkParent = null;
        String id = CONNECTOR_ID_START + this.getID();
        if (inlink instanceof EPICSLink) {
            inlinkParent = ((EPICSLink)inlink).getParent();
        } else {
            return null;
        }
        String inlinkStr = "";
        String outlinkStr = getID();
        if (inlink!=null) inlinkStr = inlink.getID();

        Connector connector = new Connector(id, (LinkManagerObject)((EPICSLink)inlink).getParent(), this, getInput());
        inlinkParent.addSubObject(id, connector);
        UndoManager.getInstance().addAction(new CreateConnectorAction(connector, inlinkStr, outlinkStr));
        return connector;
    }
}

public void setTextPositionNorth(boolean isTextPositionNorth) {
    this.textPositionNorth = isTextPositionNorth;
    DrawingSurface.getInstance().repaint();
}

public boolean isTextPositionNorth() {
    return this.textPositionNorth;
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Flexible#copyToGroup(java.lang.String)
 */
public Flexible copyToGroup(String group) {
    String newName;
    if (group.equals(Record.nullString))
        newName = Group.substractObjectName(data.getName());
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(data.getName());

    // object with new name already exists, add suffix ///!!!
    while (Group.getRoot().findObject(newName, true)!=null){
            newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);
    }

    VDBPort theDataCopy = VDBData.copyVDBPort(data);
    theDataCopy.setName(newName);

    Port thePortCopy = new Port(theDataCopy, null, getX(), getY());
    ((Group)Group.getRoot().getSubObject(group)).addSubObject(newName, thePortCopy, true);

    Group.getRoot().manageLinks(true);
    unconditionalValidation();

    return thePortCopy;
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Flexible#getFlexibleName()
 */
public String getFlexibleName() {
    return data.getName();
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Flexible#moveToGroup(java.lang.String)
 */
public boolean moveToGroup(String group) {

    if (Group.getEditingTemplateData()==null)
        return false;

    Group.getEditingTemplateData().removePort(data);
    //String oldName = getName();
    String newName;
    if (group.equals(Record.nullString)){
        newName = Group.substractObjectName(data.getName());
    }
    else
        newName = group+Constants.GROUP_SEPARATOR+
                  Group.substractObjectName(data.getName());;

    // object with new name already exists, add suffix // !!!
    Object obj;
    boolean renameNeeded = false;

    while ((obj=Group.getRoot().findObject(newName, true))!=null)
    {
        if (obj==this)
        {
               data.setName(newName);
            return true;
        }
        else
        {
            renameNeeded = true;
            newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
        }
    }

    if (renameNeeded){
        rename(newName);
    }

    getParent().removeObject(Group.substractObjectName(newName));
    setParent(null);
    ((Group)Group.getRoot().getSubObject(group)).addSubObject(newName, this, true);
    data.setName(newName);

    Group.getEditingTemplateData().addPort(data);
    unconditionalValidation();

    return true;
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Flexible#rename(java.lang.String)
 */
public boolean rename(String newName) {
    if (Group.getEditingTemplateData()==null)
        return false;

    Group.getEditingTemplateData().renamePort(data, newName);
    String newObjName = Group.substractObjectName(newName);
    String oldObjName = Group.substractObjectName(getName());

    if (!oldObjName.equals(newObjName))
    {

        String fullName = com.cosylab.vdct.util.StringUtils.replaceEnding(getName(), oldObjName, newObjName);
        data.setName(fullName);
        getParent().addSubObject(newObjName, this);

        // fix connectors IDs
        Enumeration e = getParent().getSubObjectsV().elements();
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

    return true;
}

}
