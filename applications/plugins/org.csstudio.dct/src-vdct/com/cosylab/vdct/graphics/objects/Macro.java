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
import com.cosylab.vdct.graphics.*;

import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.util.StringUtils;
import com.cosylab.vdct.vdb.GUIHeader;
import com.cosylab.vdct.vdb.GUISeparator;
import com.cosylab.vdct.vdb.MacroDescriptionProperty;
import com.cosylab.vdct.vdb.VDBData;
import com.cosylab.vdct.vdb.VDBMacro;

import javax.swing.*;

import java.awt.event.*;

/**
 * Insert the type's description here.
 * Creation date: (29.1.2001 20:05:51)
 * @author Matej Sekoranja
 */
public class Macro extends VisibleObject implements Descriptable, Movable, InLink, Popupable, Selectable, Inspectable, MultiInLink, Flexible
{
    class PopupMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();

            /*
            if (action.equals(colorString))
            {
                Color newColor = ColorChooser.getColor(selectTitle, getColor());
                if (newColor!=null)
                    setColor(newColor);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(addConnectorString))
            {
                //addConnector();
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(removeLinkString))
            {
                removeLink();
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(removeMacroString))
            {
                destroy();
                ViewState.getInstance().deselectObject(Macro.this);
                com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.DeleteAction(Macro.this));
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else*/ if (action.equals(removeMacroDefString))
            {
                data.getTemplate().removeMacro(getName());
                ViewState.getInstance().deselectObject(Macro.this);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(inputString)) {
                setMode(InLink.INPUT_MACRO_MODE);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(outputString)) {
                setMode(InLink.OUTPUT_MACRO_MODE);
                com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
            }
            else if (action.equals(textNorth)) {
                setTextPositionNorth(true);
            }
            else if (action.equals(textSide)) {
                setTextPositionNorth(false);
            }

        }
    }

    protected boolean disconnected = true;

    //private static final String descriptionString = "Description";
    //private static final String selectTitle = "Select link color...";
    //private static final String addConnectorString = "Add connector";
    //private static final String colorString = "Color...";
    //private static final String removeLinkString = "Remove Link";
//    private static final String removeMacroString = "Hide Macro";
//    private static final String removeMacroDefString = "Remove Macro";

//    private static final String removeMacroString = "Remove Macro";
    private static final String removeMacroDefString = "Remove Macro";

    private static final String modeString = "Macro Mode";
    private static final String inputString = "INPUT";
    private static final String outputString = "OUTPUT";

    private static final String textPosition = "Text Position";
    private static final String textNorth = "TOP";
    private static final String textSide = "SIDE";

    //private static final String nullString = "";

    private int mode = InLink.INPUT_MACRO_MODE;

    protected Vector outlinks;
    private static javax.swing.ImageIcon icon = null;
    private static GUISeparator macroSeparator = null;
    protected VDBMacro data = null;

    protected int rightXtranslation = 0;
    protected int rightYtranslation = 0;
    protected int leftXtranslation = 0;
    protected int leftYtranslation = 0;
    protected Polygon leftPoly;
    protected Polygon rightPoly;

    private int r = 0;

     private String lastUpdatedFullName = null;

     /** if textPositionNorth=true text is positioned on the top of
      * the macro, if false it is on the side */
     private boolean textPositionNorth = true;

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:22:29)
 */
public Macro(VDBMacro data, ContainerObject parent, int x, int y) {
    super(parent);
    this.data = data;

    setColor(Constants.FRAME_COLOR);

    setWidth(Constants.LINK_STUB_SIZE);
    setHeight(Constants.LINK_STUB_SIZE);

    setX(x); setY(y);

    data.setVisibleObject(this);

    // initialize polygon so that it contains 5 points
    int[] pts = new int[5];
    leftPoly = new Polygon(pts, pts, 5);
    rightPoly = new Polygon(pts, pts, 5);

    outlinks = new Vector();

    updateTemplateLink();
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
private com.cosylab.vdct.graphics.objects.Macro.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:59:34)
 */
public void destroy() {
    if (!isDestroyed()) {
        super.destroy();

        if (outlinks.size()>0) {
            Object[] objs = new Object[outlinks.size()];
            outlinks.copyInto(objs);
            for(int i=0; i<objs.length; i++) {
                OutLink outlink = (OutLink)objs[i];
                OutLink start = EPICSLinkOut.getStartPoint(outlink);
                if((start instanceof EPICSLinkOut)){
                    ((EPICSLinkOut)start).sourceDestroyed();
                }
                else if ((start instanceof EPICSVarLink)) {

                    if (outlink instanceof Connector) {
                        OutLink temp;
                        while(outlink instanceof Connector) {
                            temp =((Connector)outlink).getOutput();
                            ((Connector)outlink).destroy();
                            outlink = temp;
                        }
                    }
                    start.disconnect(this);

                }
                else if (start!=null)
                    start.disconnect(this);
                else
                    outlink.disconnect(this);
            }
            outlinks.clear();
        }

        if (lastUpdatedFullName!=null)
            Group.getRoot().getLookupTable().remove(data.getFullName());
        //else
        //    ((LinkManagerObject)getParent()).removeInvalidLink(this);

        data.setVisibleObject(null);
        getParent().removeObject(getName());

        // also remove macro definition
        data.getTemplate().removeMacro(getName());



    }

}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:23:04)
 */
public void disconnect(Linkable disconnector) {
    if (!disconnected && outlinks.contains(disconnector)) {
        outlinks.removeElement(disconnector);

        if (outlinks.size()==0) {
             // cannot be destoryed by removing links
            //destroy();
            disconnected = true;
        }
        else
         if (outlinks.size()==1)
            if (outlinks.firstElement() instanceof VisibleObject)
                setColor(((VisibleObject)outlinks.firstElement()).getColor());
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

    double Rscale = getRscale();
    boolean zoom = Rscale < 1.0 && view.isZoomOnHilited() && view.isHilitedObject(this);
    if (zoom) {
        zoomImage = ZoomPane.getInstance().startZooming(this, true);
    }

    int rrx = getRx() - view.getRx();
    int rry = getRy() - view.getRy();

    int rwidth = getRwidth();
    int rheight = getRheight();

    boolean rightSide = isRight();

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

        if (rightSide)
        {
            int xx = rrx-r;
            g.drawOval(xx, rry+(rheight-r)/2, r, r);

            // draw tail if there are any links
            if (!disconnected)
            {
                int yy = rry+rheight/2;
                g.drawLine(xx, yy, xx-r, yy);
            }
        }
        else
        {
            int xx = rrx+rwidth;
            g.drawOval(xx, rry+(rheight-r)/2, r, r);

            // draw tail if there are any links
            if (!disconnected)
            {
                int yy = rry+rheight/2;
                xx += r;
                g.drawLine(xx, yy, xx+r, yy);
            }
        }

        Font font = FontMetricsBuffer.getInstance().getAppropriateFont(
                  Constants.DEFAULT_FONT, Font.PLAIN,
                  getLabel(), rwidth*4, rheight);

        if (font!=null)    {
            FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
            if (textPositionNorth) {
                setRlabelX(rwidth/2-fm.stringWidth(getLabel())/2);
                setRlabelY(-fm.getHeight()+fm.getAscent());
            } else {
                int s = (int)(getScale()*Constants.LINK_STUB_SIZE/4.0);
                setRlabelY((rheight + fm.getAscent() - fm.getDescent())/2);
                if (isRight()) {
                    setRlabelX(rwidth + s);
                } else {
                    setRlabelX(-fm.stringWidth(getLabel())-s);
                }
            }
            g.setFont(getFont());
            g.drawString(getLabel(), rrx+getRlabelX(), rry+getRlabelY());
        }

    }

    if (false)///!!!
    {

        // draw link
        g.setColor(getVisibleColor());

        //LinkDrawer.drawLink(g, this, inlink, getQueueCount(), rightSide);
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
 * Creation date: (3.2.2001 11:23:59)
 * @return java.util.Vector
 */
public java.util.Vector getItems() {
    Vector items = new Vector();

    ActionListener al = createPopupmenuHandler();
/*
    JMenuItem colorItem = new JMenuItem(colorString);
    colorItem.addActionListener(al);
    items.addElement(colorItem);
*/
    // no connectors for macros yet
    /*
    JMenuItem addItem = new JMenuItem(addConnectorString);
    addItem.setEnabled(!isDisconnected());
    addItem.addActionListener(al);
    items.addElement(addItem);

    // modes
    items.addElement(new JSeparator());
    */

    JMenu modeMenu = new JMenu(modeString);
    items.addElement(modeMenu);

    JRadioButtonMenuItem inputModeItem = new JRadioButtonMenuItem(inputString, getMode()==InLink.INPUT_MACRO_MODE);
    inputModeItem.setEnabled(getMode()!=InLink.INPUT_MACRO_MODE);
    inputModeItem.addActionListener(al);
    modeMenu.add(inputModeItem);

    JRadioButtonMenuItem outputModeItem = new JRadioButtonMenuItem(outputString, getMode()==InLink.OUTPUT_MACRO_MODE);
    outputModeItem.setEnabled(getMode()!=InLink.OUTPUT_MACRO_MODE);
    outputModeItem.addActionListener(al);
    modeMenu.add(outputModeItem);

    //TODO is this needed
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

    /*
    if (!isDisconnected())
    {
        JMenuItem removeLinkItem = new JMenuItem(removeLinkString);
        removeLinkItem.addActionListener(al);
        items.addElement(removeLinkItem);
    }
    JMenuItem removeMacroItem = new JMenuItem(removeMacroString);
    removeMacroItem.addActionListener(al);
    items.addElement(removeMacroItem);
    */

    JMenuItem removeMacroDefItem = new JMenuItem(removeMacroDefString);
    removeMacroDefItem.addActionListener(al);
    items.addElement(removeMacroDefItem);

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
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getInX() {

    boolean right = isRight();

    if (right)
        return getX()-Constants.LINK_RADIOUS*4;
    else
        return getX()+getWidth()+Constants.LINK_RADIOUS*4;
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 22:22:13)
 * @return int
 */
public int getInY() {
    return getY()+getHeight()/2;
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
 * Creation date: (29.1.2001 20:05:51)
 * @param id java.lang.String
 */
public void setLayerID(String id) {
    // not needed, dynamicaly retrieved via parent
}

private void validateFontAndPolygon(double Rscale, int rwidth, int rheight) {

    leftXtranslation = 0; leftYtranslation = 0;
    rightXtranslation = 0; rightYtranslation = 0;

    if (getMode() == InLink.OUTPUT_MACRO_MODE)
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
    else if (getMode() == InLink.INPUT_MACRO_MODE)
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

    setLabel(getName());

      ///!!! optimize static

    Font font = FontMetricsBuffer.getInstance().getAppropriateFont(
                    Constants.DEFAULT_FONT, Font.PLAIN,
                    getLabel(), rwidth*4, rheight);

    if (font!=null)
    {
        FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(font);
        if (textPositionNorth) {
            setRlabelX(rwidth/2-fm.stringWidth(getLabel())/2);
             setRlabelY(-fm.getHeight()+fm.getAscent());
        } else {
            int s = (int)(getScale()*Constants.LINK_STUB_SIZE/4.0);
            setRlabelY((rheight + fm.getAscent() - fm.getDescent())/2);
            if (isRight()) {
                setRlabelX(rwidth + s);
            } else {
                setRlabelX(-fm.stringWidth(getLabel())-s);
            }
        }
    }

    setFont(font);

    r = (int)(Rscale*Constants.LINK_RADIOUS*2);

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
 * @see com.cosylab.vdct.graphics.objects.OutLink#getMode()
 */
public int getMode()
{
    return mode;
}

public void setTextPositionNorth(boolean isTextPositionNorth) {
    this.textPositionNorth = isTextPositionNorth;
    DrawingSurface.getInstance().repaint();
}

public boolean isTextPositionNorth() {
    return this.textPositionNorth;
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
        icon = new javax.swing.ImageIcon(getClass().getResource("/images/macro.gif"));
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
//    return super.toString();
    return getID();
}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 13:07:04)
 * @return com.cosylab.vdct.vdb.GUISeparator
 */
public static com.cosylab.vdct.vdb.GUISeparator getMacroSeparator() {
    if (macroSeparator==null) macroSeparator = new GUISeparator("Macro");
    return macroSeparator;
}

/**
 * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
 */
public InspectableProperty[] getProperties(int mode)
{
    InspectableProperty[] properties = new InspectableProperty[4];

    properties[0]=GUIHeader.getDefaultHeader();
    properties[1]=getMacroSeparator();
    properties[2]=data;
    properties[3]=new MacroDescriptionProperty(data);

    return properties;
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

        // update lookup table
        lastUpdatedFullName = null;
        updateTemplateLink();

        // repair the links
        //Group.getRoot().manageLinks(true);
    }
}

/**
 */
public void rename(String oldName, String newName)
{
    getParent().removeObject(oldName);
    getParent().addSubObject(newName, this);

    // fix lookup table
    updateTemplateLink();

    // fix source links
    LinkManagerObject.fixMacroLink(this);

    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
}

/**
 * Returns the data.
 * @return VDBMacro
 */
public VDBMacro getData()
{
    return data;
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
 * Insert the method's description here.
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public boolean isRight() {
    if (disconnected || outlinks.size()==0) {
        return true;
    } else {
        OutLink first;
        int left = 0;
        int right = 0;
        for (int i = 0; i < outlinks.size(); i++) {
            first = (OutLink)outlinks.get(i);
            if (first.getLayerID().equals(getLayerID())) {
                if (first.isRight()) {
                    if (first.getRightX() < getLeftX()){
//                    if (!( getRightX() <= first.getLeftX() ||
//                           (first.getLeftX()<getLeftX() && getLeftX()<first.getRightX() && first.getRightX()<getRightX()))) {
                        right++;
                    } else {
                        left++;
                    }
                } else {
                    if (first.getLeftX() > getRightX()) {
                        left++;
                    } else {
                        right++;
                    }
                }
                //return (first.getOutX()<(getX()+getWidth()/2));
            } else {
                right++;
            }
        }
        if (right >= left)  {
            return true;
        }
        else {
            return false;
        }
    }
}
/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:34:27)
 * @param output com.cosylab.vdct.graphics.objects.OutLink
 * @param prevOutput com.cosylab.vdct.graphics.objects.OutLink
 */
public void setOutput(OutLink output, OutLink prevOutput) {
    if (prevOutput!=null) outlinks.removeElement(prevOutput);
    if (!outlinks.contains(output)) {
        outlinks.addElement(output);
        if (outlinks.size()>0) disconnected=false;
    }

    if (outlinks.firstElement() instanceof VisibleObject)
        setColor(((VisibleObject)outlinks.firstElement()).getColor());

}

/**
 * Insert the method's description here.
 * Creation date: (29.1.2001 21:34:27)
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
 * Creation date: (30.1.2001 16:58:58)
 * @return boolean
 */
public void updateTemplateLink()
{
    if (lastUpdatedFullName!=null && data.getFullName().equals(lastUpdatedFullName))
        return;

    // remove old one
    if (lastUpdatedFullName!=null)
        Group.getRoot().getLookupTable().remove(lastUpdatedFullName);

    // ups, we already got this registered
    if (Group.getRoot().getLookupTable().containsKey(data.getFullName()))
    {
        lastUpdatedFullName = null;
        //!!! this should never happen, but...
        //((LinkManagerObject)getParent()).addInvalidLink(this);
    }
    // everything is OK
    else
    {
        lastUpdatedFullName = data.getFullName();
        Group.getRoot().getLookupTable().put(lastUpdatedFullName, this);
        //!!! this should never happen, but...
        //((LinkManagerObject)getParent()).removeInvalidLink(this);
    }
}

public int getLeftX() {
    return getX()-Constants.LINK_RADIOUS*4;
}

public int getRightX() {
    return getX()+getWidth()+Constants.LINK_RADIOUS*4;
}

public int getLinkCount() {
    return outlinks.size();
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.MultiInLink#getOutlinks()
 */
public Vector getOutlinks() {
    return outlinks;
}

public int getTopOffset() {
    if (!textPositionNorth) return 0;
    FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(getFont());
    return fm.getAscent();
}

public int getLeftOffset() {
    FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(getFont());
    int length = fm.stringWidth(getLabel());
    int templ = (length - getWidth())/2;
    if (isRight()) {
        if (isTextPositionNorth() && templ > 0) return templ;
        else return 2*r;
    }
    else {
        if (isTextPositionNorth()) {
            if (templ > 0) return templ;
            else return 0;
        }
        else {
            return length;
        }
    }
}

public int getRightOffset() {
    FontMetrics fm = FontMetricsBuffer.getInstance().getFontMetrics(getFont());
    int length = fm.stringWidth(getLabel());
    int templ = (length - getWidth())/2;
    if (!isRight()) {
        if (isTextPositionNorth() && templ > 0) return templ;
        else return 2*r;
    }
    else {
        if (isTextPositionNorth()) {
            if (templ > 0) return templ;
            else return 0;
        }
        else {
            return length;
        }
    }
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

    VDBMacro theDataCopy = VDBData.copyVDBMacro(data);
    theDataCopy.setName(newName);

    Macro theMacroCopy = new Macro(theDataCopy, null, getX(), getY());
    ((Group)Group.getRoot().getSubObject(group)).addSubObject(newName, theMacroCopy, true);

    Group.getRoot().manageLinks(true);
    unconditionalValidation();

    return theMacroCopy;
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

    Group.getEditingTemplateData().removeMacro(data);
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

    Group.getEditingTemplateData().addMacro(data);
    unconditionalValidation();

    return true;
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.Flexible#rename(java.lang.String)
 */
public boolean rename(String newName) {
    if (Group.getEditingTemplateData()==null)
        return false;

    Group.getEditingTemplateData().renameMacro(data, newName);
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
