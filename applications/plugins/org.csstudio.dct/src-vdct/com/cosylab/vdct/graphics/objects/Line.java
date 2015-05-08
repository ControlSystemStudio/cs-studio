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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.cosylab.vdct.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.graphics.popup.*;
import com.cosylab.vdct.events.*;
import com.cosylab.vdct.util.*;

/**
 * @author ssah
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Line extends VisibleObject implements BorderObject, Flexible, Movable, Popupable, Selectable, Clipboardable
{

class PopupMenuHandler implements ActionListener
{

public void actionPerformed(ActionEvent event)
{
    String action = event.getActionCommand();
    if(action.equals(colorString))
    {
        Color newColor = ColorChooser.getColor(colorTitleString, getColor());

        if(newColor != null)
        {
            setColor(newColor);
            currentColor = newColor;
        }

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
    else if(action.equals(dashedString))
    {
        dashed = !dashed;
        currentDashed = dashed;

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
    else if(action.equals(startArrowString))
    {
        startArrow = !startArrow;
        currentStartArrow = startArrow;

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
    else if(action.equals(endArrowString))
    {
        endArrow = !endArrow;
        currentEndArrow = endArrow;

        CommandManager.getInstance().execute("RepaintWorkspace");
    }
}

}

private String hashId;
private String name;
private Vertex startVertex;
private Vertex endVertex;
private boolean dashed;
private boolean startArrow;
private boolean endArrow;

private static final String nullString = "";
private static final String colorString = "Color...";
private static final String colorTitleString = "Line color";
private static final String dashedString = "Dashed";
private static final String startArrowString = "Start Arrow";
private static final String endArrowString = "End Arrow";

private static Color currentColor = Constants.LINE_COLOR;
private static boolean currentDashed = false;
private static boolean currentStartArrow = false;
private static boolean currentEndArrow = false;

private static final String hashIdPrefix = "Line";

public Line(String parName, Group parentGroup, int posX, int posY, int posX2, int posY2)
{
    super(parentGroup);

    startVertex = new Vertex(this, posX, posY);
    endVertex = new Vertex(this, posX2, posY2);

    revalidatePosition();

    // for move rectangle
    setWidth(Constants.CONNECTOR_WIDTH);
    setHeight(Constants.CONNECTOR_HEIGHT);

    setColor(currentColor);
    dashed = currentDashed;
    startArrow = currentStartArrow;
    endArrow = currentEndArrow;

    if(parName == null)
    {
        hashId = getAvailableHashId();

        if(parentGroup.getAbsoluteName().length() > 0)
            name = parentGroup.getAbsoluteName() + Constants.GROUP_SEPARATOR + hashId;
        else
            name = hashId;
    }
    else
        name = parName;
}

public void accept(Visitor visitor)
{
    visitor.visitGroup();
}

public boolean checkMove(int dx, int dy)
{
    //ViewState view = ViewState.getInstance();

    if(startVertex.checkMove(dx, dy) && endVertex.checkMove(dx, dy))
        return true;

    return false;
}

public Flexible copyToGroup(String group)
{
    String newName;
    if(group.equals(nullString))
        newName = Group.substractObjectName(getName());
    else
        newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

// object with new name already exists, add suffix ///!!!
    while(Group.getRoot().findObject(newName, true) != null)
        newName = StringUtils.incrementName(newName, Constants.COPY_SUFFIX);

    Line grLine = new Line(newName, null,
                            startVertex.getX(), startVertex.getY(),
                            endVertex.getX(), endVertex.getY());
    Group.getRoot().addSubObject(newName, grLine, true);
    grLine.setStartArrow(startArrow);
    grLine.setEndArrow(endArrow);
    grLine.setColor(getColor());

    //ViewState view = ViewState.getInstance();
    //grLine.move(20 - view.getRx(), 20 - view.getRy());

    unconditionalValidation();
    return grLine;
}

public void destroy()
{
    super.destroy();
    if(getParent() != null)
        getParent().removeObject(Group.substractObjectName(name));

    if(!startVertex.isDestroyed())
        startVertex.destroy();

    if(!endVertex.isDestroyed())
        endVertex.destroy();
}

protected void draw(Graphics g, boolean hilited)
{
    ViewState view = ViewState.getInstance();

    int offsetX = view.getRx();
    int offsetY = view.getRy();

    int posX = getRx() - offsetX;
    int posY = getRy() - offsetY;
    int rwidth = getRwidth();
    int rheight = getRheight();

    double Rscale = view.getScale();
    boolean zoom = (Rscale < 1.0) && view.isZoomOnHilited() && view.isHilitedObject(this);
    if (zoom) {
        rwidth /= Rscale;
        rheight /= Rscale;
        posX -= (rwidth - getRwidth())/2;
        posY -= (rheight - getRheight())/2;
        if (view.getRx() < 0)
            posX = posX < 0 ? 2 : posX;
        if (view.getRy() < 0)
            posY = posY <= 0 ? 2 : posY;
        Rscale = 1.0;
    }

    if (hilited)
        g.setColor(Constants.HILITE_COLOR);
    else
        g.setColor(getVisibleColor());

    //double scale = view.getScale();

    posX = startVertex.getRx() - offsetX;
    posY = startVertex.getRy() - offsetY;

    int posX2 = endVertex.getRx() - offsetX;
    int posY2 = endVertex.getRy() - offsetY;

    int dirX = (posX < posX2) ? (1) : (-1);
    int dirY = (posY < posY2) ? (1) : (-1);

    if((dashed) && ((posX != posX2) || (posY != posY2)))
    {
        double angle = Math.atan(((double)(Math.abs(posY2 - posY))) / Math.abs(posX2 - posX));
        double cos = dirX * Math.cos(angle) * Constants.DASHED_LINE_DENSITY;
        double sin = dirY * Math.sin(angle) * Constants.DASHED_LINE_DENSITY;

        double curX = posX;
        double curY = posY;

        int step = 0;

        while(((curX * dirX) <= (posX2 * dirX)) && ((curY * dirY) <= (posY2 * dirY)))
        {
            step++;

            double curX2 = posX + cos * step;
            double curY2 = posY + sin * step;

            if(((curX2 * dirX) > (posX2 * dirX)) || ((curY2 * dirY) > (posY2 * dirY)))
            {
                curX2 = posX2;
                curY2 = posY2;
            }

            g.drawLine((int)curX, (int)curY, (int)curX2, (int)curY2);
            step++;

            curX = posX + cos * step;
            curY = posY + sin * step;
        }
    }
    else
        g.drawLine(posX, posY, posX2, posY2);

    if(startArrow || endArrow)
    {
        double angle = Math.atan(((double)(Math.abs(posY2 - posY))) / Math.abs(posX2 - posX));

        double arrowSize = Constants.ARROW_SIZE * getRscale();
        double lineLength = Math.sqrt((posX2 - posX) * (posX2 - posX) + (posY2 - posY) * (posY2 - posY));

        if(arrowSize > lineLength / 2)
            arrowSize = lineLength / 2;

        int[] vertexX = new int[3];
        int[] vertexY = new int[3];

        if(startArrow)
        {
            vertexX[0] = posX;
            vertexY[0] = posY;

            vertexX[1] = (int)(posX + dirX * Math.cos(angle + Constants.ARROW_SHARPNESS) * arrowSize);
            vertexY[1] = (int)(posY + dirY * Math.sin(angle + Constants.ARROW_SHARPNESS) * arrowSize);

            vertexX[2] = (int)(posX + dirX * Math.cos(angle - Constants.ARROW_SHARPNESS) * arrowSize);
            vertexY[2] = (int)(posY + dirY * Math.sin(angle - Constants.ARROW_SHARPNESS) * arrowSize);

            g.fillPolygon(vertexX, vertexY, 3);
        }

        if(endArrow)
        {
            vertexX[0] = posX2;
            vertexY[0] = posY2;

            vertexX[1] = (int)(posX2 - dirX * Math.cos(angle + Constants.ARROW_SHARPNESS) * arrowSize);
            vertexY[1] = (int)(posY2 - dirY * Math.sin(angle + Constants.ARROW_SHARPNESS) * arrowSize);

            vertexX[2] = (int)(posX2 - dirX * Math.cos(angle - Constants.ARROW_SHARPNESS) * arrowSize);
            vertexY[2] = (int)(posY2 - dirY * Math.sin(angle - Constants.ARROW_SHARPNESS) * arrowSize);

            g.fillPolygon(vertexX, vertexY, 3);
        }

    }
}

private String getAvailableHashId()
{
    int number = 0;
    String testHashId = hashIdPrefix + String.valueOf(number);

    while(getParent().containsObject(testHashId))
    {
        number++;
        testHashId = hashIdPrefix + String.valueOf(number);
    }

    return testHashId;
}

public static boolean getCurrentStartArrow()
{
    return currentStartArrow;
}

public static boolean getCurrentEndArrow()
{
    return currentEndArrow;
}

public boolean getEndArrow()
{
    return endArrow;
}

public Vertex getEndVertex()
{
    return endVertex;
}

public String getFlexibleName()
{
    return name;
}

public String getHashID()
{
    return hashId;
}

public boolean getDashed()
{
    return dashed;
}

public Vector getItems()
{
    Vector items = new Vector();

    ActionListener al = new PopupMenuHandler();

    JMenuItem colorItem = new JMenuItem(colorString);
    colorItem.addActionListener(al);
    items.addElement(colorItem);

    JCheckBoxMenuItem isDashedItem = new JCheckBoxMenuItem(dashedString);
    isDashedItem.setSelected(dashed);
    isDashedItem.addActionListener(al);
    items.addElement(isDashedItem);

    if(startVertex.isHilited())
    {
        JCheckBoxMenuItem startArrowItem = new JCheckBoxMenuItem(startArrowString);
        startArrowItem.setSelected(startArrow);
        startArrowItem.addActionListener(al);
        items.addElement(startArrowItem);
    }
    else if(endVertex.isHilited())
    {
        JCheckBoxMenuItem endArrowItem = new JCheckBoxMenuItem(endArrowString);
        endArrowItem.setSelected(endArrow);
        endArrowItem.addActionListener(al);
        items.addElement(endArrowItem);
    }

    return items;
}

public String getName()
{
    return name;
}

public boolean getStartArrow()
{
    return startArrow;
}

public Vertex getStartVertex()
{
    return startVertex;
}

public boolean move(int dx, int dy)
{
    if(checkMove(dx, dy))
    {
        startVertex.move(dx, dy);
        endVertex.move(dx, dy);

        revalidatePosition();

        return true;
    }

    return false;
}

public boolean moveToGroup(String group)
{
    String currentParent = Group.substractParentName(getName());
    if(group.equals(currentParent))
        return false;

    //String oldName = getName();
    String newName;
    if (group.equals(nullString))
        newName = Group.substractObjectName(getName());
    else
        newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

    // object with new name already exists, add suffix // !!!
    Object obj;
    boolean renameNeeded = false;
    while ((obj=Group.getRoot().findObject(newName, true))!=null)
    {
        if (obj==this)    // it's me :) already moved, fix data
        {
            name = newName;
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

    name = newName;
    unconditionalValidation();

    return true;
}

public boolean rename(String newName)
{
    String newObjName = Group.substractObjectName(newName);
    String oldObjName = Group.substractObjectName(getName());

    if(!oldObjName.equals(newObjName))
    {
        getParent().removeObject(oldObjName);
        String fullName = StringUtils.replaceEnding(getName(), oldObjName, newObjName);
        name = fullName;
        getParent().addSubObject(newObjName, this);
    }

    // move if needed
    moveToGroup(Group.substractParentName(newName));

    return true;
}

public void revalidatePosition()
{
    startVertex.revalidatePosition();
    endVertex.revalidatePosition();

    double rscale = getRscale();

    setRx((int)(getX() * rscale));
    setRy((int)(getY() * rscale));
}

public void setEndArrow(boolean parEndArrow)
{
    endArrow = parEndArrow;
}

public void setStartArrow(boolean parStartArrow)
{
    startArrow = parStartArrow;
}

public void setDashed(boolean parDashed)
{
    dashed = parDashed;
}

protected void validate()
{
    startVertex.validate();
    endVertex.validate();

    revalidatePosition();

    double rscale = getRscale();

    setRwidth((int)(getWidth() * rscale));
    setRheight((int)(getHeight() * rscale));
}


/**
 * Returned value inicates change
 * Creation date: (21.12.2000 22:21:12)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param x int
 * @param y int
 */
public VisibleObject hiliteComponentsCheck(int x, int y) {

    if (startVertex.intersects(x, y)!=null)
        return startVertex;
    if (endVertex.intersects(x, y)!=null)
        return endVertex;
    return null;
}


/**
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {

    // first check on small sub-objects like connectors
    VisibleObject spotted = hiliteComponentsCheck(px, py);
      if (spotted == null) {

          final int DIST = 10;

          int rx = getRx();
          int ry = getRy();
          int rw = getRwidth();
          int rh = getRheight();
          boolean insideOuter = ((rx-DIST)<=px) &&
                              ((ry-DIST)<=py) &&
                              ((rx+rw+DIST)>=px) &&
                              ((ry+rh+DIST)>=py);
          if (insideOuter)
          {
             // square check passed... now limit only to arrow

              // vertical check
            int px1 = startVertex.getRx();
            int px2 = endVertex.getRx();
             if (px1 == px2)
                 spotted = this;
             else
             {
                 int py1 = startVertex.getRy();
                 int py2 = endVertex.getRy();

                 double tan = (py2-py1)/(double)(px2-px1);
                 if (Math.abs(tan) > DIST)
                     spotted = this;
                 else
                 {
                     double expectedY = (px-px1)*tan + py1;
                     if (Math.abs(py-expectedY) <= DIST)
                             spotted = this;
                 }
             }
          }

      }

      return spotted;
}

/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getX()
 */
public int getX() {
    return Math.min(startVertex.getX(), endVertex.getX());
}
/* (non-Javadoc)
 * @see com.cosylab.vdct.graphics.objects.VisibleObject#getY()
 */
public int getY() {
    return Math.min(startVertex.getY(), endVertex.getY());
}

public int getWidth() {
    return Math.abs(startVertex.getX() - endVertex.getX());
}

public int getHeight() {
    return Math.abs(startVertex.getY() - endVertex.getY());
}


public void snapToGrid() {
    startVertex.snapToGrid();
    endVertex.snapToGrid();
}

}
