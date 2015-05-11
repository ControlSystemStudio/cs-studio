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
import com.cosylab.vdct.*;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.graphics.popup.*;

/**
 * @author ssah
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Vertex extends VisibleObject implements Movable, Popupable
{

    private VisibleObject owner;
    //private static final String nullString = "";
    private boolean hilited = false;

public Vertex(VisibleObject owner, int parX, int parY)
{
    //super(parentGroup);
    super(null);

    //ViewState view = ViewState.getInstance();

    setX(parX);
    setY(parY);
    setWidth(Constants.CONNECTOR_WIDTH);
    setHeight(Constants.CONNECTOR_HEIGHT);

    this.owner = owner;

}

public void accept(Visitor visitor)
{
    visitor.visitGroup();
}

public boolean checkMove(int dx, int dy)
{
    ViewState view = ViewState.getInstance();

    if((getX() < - dx) || (getY() < - dy)
        || (getX() > (view.getWidth() - getWidth() - dx))
        || (getY() > (view.getHeight() - getHeight() - dy)))
    {
        return false;
    }

    return true;
}

protected void draw(Graphics g, boolean hilited)
{
    ViewState view = ViewState.getInstance();

    int offsetX = view.getRx();
    int offsetY = view.getRy();

    int rwidth = getRwidth();
    int rheight = getRheight();
    int posX = getRx() - offsetX - rwidth/2;
    int posY = getRy() - offsetY - rheight/2;


    double Rscale = getRscale();
    boolean zoom = Rscale < 1.0 && view.isZoomOnHilited() && view.isHilitedObject(this);
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

    if((hilited) && (!((posX > view.getViewWidth()) || (posY > view.getViewHeight())
        || ((posX + rwidth) < 0) || ((posY + rheight) < 0))))
    {
        g.setColor(Constants.HILITE_COLOR);
        g.drawRect(posX, posY, rwidth, rheight);

        if(owner instanceof TextBox)
            ((TextBox)owner).drawDashedBorder(g, hilited);
    }
}

public String getHashID()
{
    return null;
}

public Vector getItems()
{
    try
    {
        hilited = true;
        if(owner instanceof Popupable)
            return ((Popupable)owner).getItems();
    }
    finally
    {
        hilited = false;
    }
    return null;
}

public boolean move(int dx, int dy)
{
    if(checkMove(dx, dy))
    {
        x+=dx;
        y+=dy;

        revalidatePosition();

        return true;
    }
    return false;
}

public void revalidatePosition()
{
    double rscale = getRscale();

    setRx((int)(getX() * rscale));
    setRy((int)(getY() * rscale));

}

public void setX(int parX)
{
    super.setX(parX);

    if(owner != null)
        owner.revalidatePosition();
}

public void setY(int parY)
{
    super.setY(parY);

    if(owner != null)
        owner.revalidatePosition();
}

protected void validate()
{
    revalidatePosition();

    double rscale = getRscale();

    setRwidth((int)(getWidth() * rscale));
    setRheight((int)(getHeight() * rscale));
}

/**
 * Returns the hilited.
 * @return boolean
 */
public boolean isHilited()
{
    return hilited;
}

/**
 * Default impmlementation for square (must be rescaled)
 * Creation date: (19.12.2000 20:20:20)
 * @return com.cosylab.visible.objects.VisibleObject
 * @param px int
 * @param py int
 */
public VisibleObject intersects(int px, int py) {
    int rwidth = getRwidth();
    int rheight = getRheight();
    int rx = getRx()-rwidth/2;    // position is center
    int ry = getRy()-rheight/2;
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
    int rwidth = getRwidth();
    int rheight = getRheight();
    int rx = getRx()-rwidth/2;    // position is center
    int ry = getRy()-rheight/2;
    if ((rx>=p1x) && (ry>=p1y) &&
            ((rx+rwidth)<=p2x) &&
            ((ry+rheight)<=p2y)) return this;
    else return null;
}

}
