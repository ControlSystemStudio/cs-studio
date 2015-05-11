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
import java.util.Enumeration;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.Settings;
import com.cosylab.vdct.graphics.*;

/**
 * !!! support for shortened tail drawing
 * Insert the type's description here.
 * Creation date: (30.1.2001 14:35:04)
 * @author Matej Sekoranja
 */

public final class LinkDrawer {
    private final static int tailLenOfR = 6;
    private final static int maxWidth = Constants.FIELD_WIDTH*4/5;
    private final static int maxHeight = Constants.FIELD_HEIGHT;
    private final static int LARGE_RECT = Constants.LINK_RADIOUS;
    private final static int SMALL_RECT = (int)(0.7*LARGE_RECT);
    private final static String maxLenStr = "012345678901234";
    private static double lastScale = 0.0;
    private static Font font = null;
    private static FontMetrics fontMetrics = null;
    private static int dy;
/**
 * Creation date: (30.1.2001 14:43:02)
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param isRight boolean
 */
public static void drawInIntergroupLink(Graphics g, OutLink out, InLink in, boolean isRight) {
    if (!out.getLayerID().equals(in.getLayerID())) {

        ViewState view = ViewState.getInstance();
        double scale = view.getScale();
        int x1 = (int)(scale*in.getInX())-view.getRx();
        int y1 = (int)(scale*in.getInY())-view.getRy();

        if (in instanceof MultiInLink && ((MultiInLink)in).getLinkCount()>1)
            drawIntergroupLink(g, view, x1, y1, null, isRight);
        else
            drawIntergroupLink(g, view, x1, y1, out, isRight);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 12:30:16)
 * @param x1 int
 * @param y1 int
 * @param descPoint com.cosylab.vdct.graphics.objects.Linkable
 * @param isRight boolean
 */
private static void drawIntergroupLink(Graphics g, ViewState view, int x1, int y1, Linkable descPoint, boolean isRight) {

    int r = (int)(LARGE_RECT*view.getScale());

    int dx = tailLenOfR*r;
    if (!isRight) dx=-dx;
    g.drawLine(x1, y1, x1+dx, y1);
    x1+=dx;

    g.drawRect(x1-r, y1-r, 2*r, 2*r);

    String label = null;
    validateFont(view);
    if (font!=null) {
        g.setFont(font);
        Linkable target = null;
        if (descPoint==null)
        {
            label = "<more>";
        }

        // bug: what about connectors (they are both)! +
        // InLink/OutLink cannot identify the right direction
        //    there can be INP in the subgroup    or hypergroup!
        else if (descPoint instanceof InLink)
        {
            target = EPICSLinkOut.getEndPoint(descPoint);
            // any special threatment ??
            if (target instanceof Descriptable)
                label = ((Descriptable)target).getDescription();
        }
        else if (descPoint instanceof OutLink && !(descPoint instanceof EPICSVarOutLink))
        {
            target = EPICSLinkOut.getStartPoint(descPoint);
            // any special threatment ??
            if (target instanceof Descriptable)
                label = ((Descriptable)target).getDescription();
        }

        if (label!=null) {
            if (isRight) dx = 3*r;
            else dx = -(fontMetrics.stringWidth(label)+2*r);
            g.drawString(label, x1+dx, y1+dy);
        }
    }

    r=(int)(SMALL_RECT*view.getScale());
    g.drawRect(x1-r, y1-r, 2*r, 2*r);

}
/**
 * This method was created in VisualAge.
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param firstHorizontal boolean
 */
public static void drawKneeLine(Graphics g, OutLink out, InLink in, boolean firstHorizontal) {
    ViewState view = ViewState.getInstance();
    double scale = view.getScale();
    int x1 = (int)(scale*out.getOutX()-view.getRx());
    int y1 = (int)(scale*out.getOutY()-view.getRy());

    int x2 = x1;
    int y2 = y1;
    boolean isInLeft = false;
    int middleInX = 0;
    if (in!=null)
    {
        // EPICSVarLink is double sided, take the best side
        if (in instanceof EPICSVarOutLink) {
            int lx = in.getLeftX();
            int rx = in.getRightX();
            middleInX = (lx+rx)/2;
            if (out.getOutX() > middleInX)
                x2 = (int)(scale*rx-view.getRx());
            else
            {
                x2 = (int)(scale*lx-view.getRx());
                isInLeft = true;
            }
        }
        else
        {
            x2 = (int)(scale*in.getInX()-view.getRx());
            isInLeft = !in.isRight();
        }

        y2 = (int)(scale*in.getInY()-view.getRy());
    }

    //
    // !!! temporary "solution"
    //

    // inivsible mode
    if (out.getMode() == OutLink.INVISIBLE_MODE)
    {
        int s = (int)(scale*Constants.INVISIBLE_CROSS_SIZE);

        if (firstHorizontal) {
            g.drawLine(x1-s, y1, x1+s, y1);
            g.drawLine(x1, y1-s, x1, y1+s);

            g.drawLine(x2, y2-s, x2, y2+s);
            g.drawLine(x2-s, y2, x2+s, y2);
        }
        else {
            g.drawLine(x1, y1-s, x1, y1+s);
            g.drawLine(x1-s, y1, x1+s, y1);

            g.drawLine(x2-s, y2, x2+s, y2);
            g.drawLine(x2, y2-s, x2, y2+s);
        }


        Linkable descPoint = out;
        int r = (int)(LARGE_RECT*view.getScale());
        int dx = tailLenOfR*r;

        String label = null;
        validateFont(view);
        if (font!=null) {
            g.setFont(font);
            Linkable target = null;
            //else if (descPoint instanceof OutLink && !(descPoint instanceof EPICSVarOutLink))
            {
                target = EPICSLinkOut.getEndPoint(descPoint);
                // any special threatment ??
                if (target instanceof Descriptable)
                    label = ((Descriptable)target).getDescription();
            }

            if (label!=null) {
                /*if (isRight) dx = 3*r;
                else*/ dx = -(fontMetrics.stringWidth(label)+2*r);
                g.drawString(label, x1+dx, y1+dy);
            }
        }


        label = null;
        descPoint = in;
        r = (int)(LARGE_RECT*view.getScale());
        dx = tailLenOfR*r;

        if (font!=null) {
            g.setFont(font);
            Linkable target = null;

            //if (descPoint instanceof InLink)
            {
                target = EPICSLinkOut.getStartPoint(descPoint);
                // any special threatment ??
                if (target instanceof Descriptable)
                    label = ((Descriptable)target).getDescription();
            }

            if (label!=null) {
                /*if (isRight) dx = 3*r;
                else*/ dx = -(fontMetrics.stringWidth(label)+2*r);
                g.drawString(label, x2+dx, y2+dy);
            }
        }


        return;
    }

    // only test here
    else if (out.getMode() == OutLink.EXTERNAL_OUTPUT_MODE)
    {
        // horizontal

        int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);

//        if (x2<x1) s = -s;
        if (!firstHorizontal) s = -s;

        // draw tail
        g.drawLine(x1, y1, x1+s, y1);
        x1+=s;

        // drawing -->
        g.drawLine(x1, y1-s, x1, y1+s);

        g.drawLine(x1, y1-s, x1+s, y1-s);
        g.drawLine(x1, y1+s, x1+s, y1+s);

        g.drawLine(x1+s, y1-s, x1+2*s, y1);
        g.drawLine(x1+s, y1+s, x1+2*s, y1);

        Linkable descPoint = out;
        int r = (int)(LARGE_RECT*view.getScale());
        int dx = tailLenOfR*r;

        String label = null;
        validateFont(view);
        if (font!=null) {
            g.setFont(font);
            Linkable target = null;
            //else if (descPoint instanceof OutLink && !(descPoint instanceof EPICSVarOutLink))
            {
                target = EPICSLinkOut.getStartPoint(descPoint);
                // any special threatment ??
                if (target instanceof Field)
                    label = ((Field)target).getFieldData().getValue();
            }

            if (label!=null) {
                /*if (isRight) dx = 3*r;
                else*/
                if (!firstHorizontal) dx = -fontMetrics.stringWidth(label)+3*s;
                g.drawString(label, x1+dx, y1+dy);
            }
        }

        return;
    }
    else if (out.getMode() == OutLink.EXTERNAL_INPUT_MODE)
    {
        // horizontal

        int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);

//        if (x2<x1) s = -s;
        if (!firstHorizontal) s = -s;

        // draw tail
        g.drawLine(x1, y1, x1+s, y1);
        x1+=s;

        // drawing -->
        g.drawLine(x1, y1-s, x1, y1+s);

        g.drawLine(x1, y1-s, x1+2*s, y1-s);
        g.drawLine(x1, y1+s, x1+2*s, y1+s);

        g.drawLine(x1+2*s, y1-s, x1+s, y1);
        g.drawLine(x1+2*s, y1+s, x1+s, y1);

        Linkable descPoint = out;
        int r = (int)(LARGE_RECT*view.getScale());
        int dx = tailLenOfR*r;

        String label = null;
        validateFont(view);
        if (font!=null) {
            g.setFont(font);
            Linkable target = null;
            //else if (descPoint instanceof OutLink && !(descPoint instanceof EPICSVarOutLink))
            {
                target = EPICSLinkOut.getStartPoint(descPoint);
                // any special threatment ??
                if (target instanceof Field)
                    label = ((Field)target).getFieldData().getValue();
            }

            if (label!=null) {
                /*if (isRight) dx = 3*r;
                else*/
                if (!firstHorizontal) dx = -fontMetrics.stringWidth(label)+3*s;
                g.drawString(label, x1+dx, y1+dy);
            }
        }

        return;
    }
/*
    else if (out.getMode() == ?)
    {
        // horizontal

        int s = (int)(scale*Constants.LINK_STUB_SIZE/2.0);

        if (x2<x1) s = -s;

        g.drawLine(x1, y1-s, x1, y1+s);

        g.drawLine(x1, y1-s, x1+2*s, y1-s);
        g.drawLine(x1, y1+s, x1+2*s, y1+s);

        g.drawLine(x1+s, y1-s, x1+s, y1);
        g.drawLine(x1+s, y1+s, x1+s, y1);

        return;
    }
*/

    int dotSize = view.getDotSize();
    int dotSize2 = 2*dotSize;

        if (in!=null) {

            boolean doDots = false;
            boolean drawDot = in instanceof MultiInLink && ((MultiInLink)in).getLinkCount() > 1;
            // diff left from right size
            if (drawDot && in instanceof EPICSVarOutLink)
                doDots = drawDot = checkForSameSideLinks((EPICSVarOutLink)in, isInLeft, middleInX);
            else
                doDots = drawDot;

            if (doDots)
            {
                if (out instanceof Connector)
                {
                    if (!firstHorizontal)
                    {
                        int ix = isInLeft ? in.getLeftX() : in.getRightX();
                        drawDot = checkConnectorOuterMost((MultiInLink)in, out.getOutX(), ix, isInLeft, middleInX);
                    }
                    else
                        drawDot = checkHorizontalFirstDootNeeded((MultiInLink)in, out.getOutY(), in.getInY(), isInLeft, middleInX);
                }
                else
                    drawDot = checkHorizontalFirstDootNeeded((MultiInLink)in, out.getOutY(), in.getInY(), isInLeft, middleInX);
            }

            if (firstHorizontal) {

                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x2, y1, x2, y2);

                if (doDots && drawDot)
                {
                    g.fillOval(x2-dotSize, y1-dotSize, dotSize2, dotSize2);
                }
                else
                {
                    // 2 links, one above, one below case (dot needed in the middle)
                    if (doDots &&
                        checkMiddleDotNeededCase((MultiInLink)in, out.getOutY(), in.getInY(), isInLeft, middleInX))
                            g.fillOval(x2-dotSize, y2-dotSize, dotSize2, dotSize2);
                }
            }
            else {
                g.drawLine(x1, y1, x1, y2);
                g.drawLine(x1, y2, x2, y2);

                if (drawDot)
                {
                    g.fillOval(x1-dotSize, y2-dotSize, dotSize2, dotSize2);
                }
            }
        }


        if (Settings.getInstance().isWireCrossingAvoidance())
        {
                // diff left from right size
                // tails to be drawn to cover distance -?[ (where ? is)
                final double rlsw = Constants.LINK_SLOT_WIDTH*scale;
                //line for out
                if (out instanceof Field) {
                    if (out.isRight())
                        g.drawLine(x1,y1,x1-(int)(((Field)out).getVerticalPosition()*rlsw),y1);
                    else
                        g.drawLine(x1,y1,x1+(int)(((Field)out).getVerticalPosition()*rlsw),y1);
                }

        }
}

/**
 * @param in
 * @param isInLeft
 * @param middleInX
 * @param drawDot
 * @return
 */
private static boolean checkForSameSideLinks(EPICSVarOutLink evol, boolean isInLeft, int middleInX) {
    boolean drawDot = true;
    Enumeration links = evol.getOutlinks().elements();
    int count = 0;
    while (links.hasMoreElements())
    {
        OutLink l = (OutLink)links.nextElement();

        // skip links from clipboards
        if (!l.getLayerID().equals(evol.getLayerID()))
            continue;

        if (l.getOutX() > middleInX && !isInLeft)
            count++;
        else if (l.getOutX() <= middleInX && isInLeft)
            count++;
    }

    if (count <= 1)
        drawDot = false;
    return drawDot;
}

/**
 * Precondition: checkForSameSideLinks == true, i.e. at least 2 links on the same side
 * @return
 */
private static boolean checkHorizontalFirstDootNeeded(MultiInLink evol, int outY, int inY, boolean isInLeft, int middleInX) {

    // allways needed
    if (outY == inY)
        return true;

    int count = 0;
    int minY = inY;
    int maxY = inY;
    //int minCount = 0;
    //int maxCount = 0;

    Enumeration links = evol.getOutlinks().elements();
    while (links.hasMoreElements())
    {
        OutLink l = (OutLink)links.nextElement();
        // skip links from clipboards
        if (!l.getLayerID().equals(evol.getLayerID()))
            continue;

        int ox = l.getOutX();

        if ((l instanceof Connector && ((l.getQueueCount()%2)!=0)) || // X is not the same... (skip all not first horizontal connectors)
            (isInLeft && ox > middleInX) ||
            (!isInLeft && ox <= middleInX))
            continue; // do not count links on the other side!

        count++;
        int oy = l.getOutY();
        if (oy < minY)
        {
            minY = oy;
            //minCount = 0;
        }
        //else if (oy == minY)
        //    minCount++;
        else if (oy > maxY)
        {
            maxY = oy;
        //    maxCount = 0;
        }
        //else if (oy == maxY)
        //    maxCount++;
    }

    // edge
    if (count == 1 ||
        (outY == maxY /*&& maxCount == 0*/) ||
        (outY == minY /*&& minCount == 0*/))
        return false;

    return true;
}

/**
 * Precondition: checkForSameSideLinks == true, i.e. at least 2 links on the same side
 * @return
 */
private static boolean checkMiddleDotNeededCase(MultiInLink evol, int outY, int inY, boolean isInLeft, int middleInX) {

    int upcount = 0;
    int downcount = 0;

    Enumeration links = evol.getOutlinks().elements();
    while (links.hasMoreElements())
    {
        OutLink l = (OutLink)links.nextElement();
        // skip links from clipboards
        if (!l.getLayerID().equals(evol.getLayerID()))
            continue;

        int ox = l.getOutX();
        if ((isInLeft && ox > middleInX) ||
            (!isInLeft && ox <= middleInX))
            continue; // do not count links on the other side!
        // connector on the same side
        if (l instanceof Connector)
        {
            int ex;
            if (evol instanceof EPICSVarOutLink) {
                if (isInLeft)
                    ex = evol.getLeftX();
                else
                    ex = evol.getRightX();
            }
            else
                ex = evol.getInX();

            if ((isInLeft && ox <= ex) ||
                (!isInLeft && ox >= ex))
                return true;
            else
                continue;
        }

        if (l.getOutY() < inY)
            upcount++;
        else /*if (oy < inY)*/
            downcount++;
    }

    // if none on the same side no dot is needed
    if (upcount == 0 || downcount == 0)
        return false;

    // at least one on the same side
    if (upcount <= 1 || downcount <= 1)
        return true;

    return false;
}

/**
 * Precondition: checkForSameSideLinks == true, i.e. at least 2 links on the same side
 * @return
 */
private static boolean checkConnectorOuterMost(MultiInLink evol, int outX, int inX, boolean isInLeft, int middleInX) {

    int minX = inX;
    int maxX = inX;
    int minCount = 0;
    int maxCount = 0;

    Enumeration links = evol.getOutlinks().elements();
    while (links.hasMoreElements())
    {
        OutLink l = (OutLink)links.nextElement();
        // skip links from clipboards
        if (!l.getLayerID().equals(evol.getLayerID()))
            continue;

        if (!(l instanceof Connector))
            continue;
        int ox = l.getOutX();

        /*
        if ((isInLeft && ox > middleInX) ||
            (!isInLeft && ox <= middleInX))
            continue; // do not count links on the other side!
            */

        if (ox < minX)
        {
            minX = ox;
            minCount = 0;
        }
        else if (ox == minX)
            minCount++;
        else if (ox > maxX)
        {
            maxX = ox;
            maxCount = 0;
        }
        else if (ox == maxX)
            maxCount++;
    }

    if (isInLeft && minX == outX && minCount == 0)
        return false;
    else if (!isInLeft && maxX == outX && maxCount == 0)
        return false;

    return true;
}


/**
 * Creation date: (30.1.2001 14:43:02)
 * @param g java.awt.Graphics
 * @param com.cosylab.vdct.graphics.objects.OutLink out
 * @param com.cosylab.vdct.graphics.objects.InLink in
 * @param count int
 * @param isRight boolean
 */
public static void drawLink(Graphics g, OutLink out, InLink in, int count, boolean isRight) {
    if (in==null)
        drawKneeLine(g, out, null, isRight);
    else if (out.getLayerID().equals(in.getLayerID()))
        drawKneeLine(g, out, in, ((count%2)==0));
    else {

        ViewState view = ViewState.getInstance();
        double scale = view.getScale();
        int x1 = (int)(scale*out.getOutX())-view.getRx();
        int y1 = (int)(scale*out.getOutY())-view.getRy();
        //int r = (int)(LARGE_RECT*scale);

        drawIntergroupLink(g, view, x1, y1, in, isRight);

    }
}
/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 10:26:24)
 */
private static void validateFont(ViewState view) {
  if (lastScale!=view.getScale()) {
      double nscale = view.getScale();
      int w = (int)(maxWidth*nscale);
      if (w<10)
      {
        /*if (font==null) - perf. */ lastScale = nscale;
          font = null;
          return;
      }
        lastScale = nscale;
      font = FontMetricsBuffer.getInstance().getAppropriateFont(
                      Constants.DEFAULT_FONT, Font.PLAIN,
                      maxLenStr,
                      w,
                      (int)(maxHeight*lastScale));

      if (font!=null) {
          fontMetrics = FontMetricsBuffer.getInstance().getFontMetrics(font);
          dy = fontMetrics.getAscent()-fontMetrics.getHeight()/2;
      }
  }
}
}
