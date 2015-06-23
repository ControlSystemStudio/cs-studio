/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */
package com.cosylab.vdct.graphics.objects;

import java.awt.Cursor;
import java.util.Enumeration;
import java.util.Vector;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.graphics.DrawingSurface;

/**
 * <code>LinkMoverUtilities</code> supplies the tools for moving the links.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class LinkMoverUtilities {

    /** The area around each link which is still sensitive and enables link move */
    public static final int LINK_AREA_WIDTH = 2;

    private boolean horizontalMove = false;
    private boolean verticalMove = false;
    private boolean diagonalRightMove = false;
    private static final Cursor VERTICAL_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR);
    private static final Cursor HORIZONTAL_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR);
    private static final Cursor DIAGONAL_RIGHT_CURSOR = new Cursor(Cursor.NE_RESIZE_CURSOR);
    private static final Cursor DIAGONAL_LEFT_CURSOR = new Cursor(Cursor.NW_RESIZE_CURSOR);

    private Vector returnConnectors;

    public static LinkMoverUtilities linkMoverUtilities = new LinkMoverUtilities();


    public static LinkMoverUtilities getLinkMoverUtilities() {
        return linkMoverUtilities;
    }
    private LinkMoverUtilities() {}

    /**
     *
     * Returns a vector of all connectors in the group. Method uses a recursive formula
     * to search all the ContainerObjects inside the group given as a parameter.
     *
     * @param group the group to be searched for the connectors
     * @return a Vector of all connectors
     */
    public static Vector getAllConnectors(Group group) {
        return getConnectors(new Vector(),group.getSubObjectsV().elements());
    }

    private static Vector getConnectors(Vector connectors, Enumeration elements) {
        VisibleObject o;
        while(elements.hasMoreElements()) {
            o = (VisibleObject) elements.nextElement();
            if (o instanceof Connector) {
                connectors.add(o);
            } else if (o instanceof ContainerObject) {
                getConnectors(connectors, ((ContainerObject)o).getSubObjectsV().elements());
            }
        }
        return connectors;
    }
    /**
     *
     * Returns the vector containing the connectors which are moved when the links are dragged.
     * This metod returns a Vector containing one Connector if parameters define area around
     * a line or two Connectors if they define the area around the knee.
     *
     * @param x the absolute horizontal position of the pointer
     * @param y the absolute vertical position of the pointer
     * @return a Vector of the connectors that are affected by the move started at the point (x,y)
     */
    public Vector isMousePositionLinkMovable(int x, int y) {
        Vector connectors = getAllConnectors(DrawingSurface.getInstance().getViewGroup());

        Connector c;
        returnConnectors = new Vector();
        int outx = 0;
        int inx = 0;
        int outy = 0;
        int iny = 0;
        horizontalMove = false;
        verticalMove = false;
        Connector horizontalConnector = null;
        Connector verticalConnector = null;
        OutLink out = null;
        InLink in = null;
        try {
            for (int i = 0; i < connectors.size(); i++) {
                c = (Connector) connectors.get(i);
                out = c.getOutput();
                in = c.getInput();

                if (verticalMove && horizontalMove) {
                    break;
                }

                if (!(out instanceof VisibleObject) || !(in instanceof VisibleObject)) continue;
                outx = ((VisibleObject)out).getRx();
                outy = ((VisibleObject)out).getRy();
                inx = ((VisibleObject)in).getRx();
                iny = ((VisibleObject)in).getRy();
                if (c.getQueueCount() %2 == 0 &&
                        ((outx + LINK_AREA_WIDTH >= x && inx - LINK_AREA_WIDTH <= x) ||
                        (outx - LINK_AREA_WIDTH <= x && inx + LINK_AREA_WIDTH >= x)) &&
                        Math.abs(c.getRy() - y) <= LINK_AREA_WIDTH && !verticalMove) {
                    returnConnectors.add(c);
                    verticalMove = true;
                    verticalConnector = c;
                    continue;
                }
                if (c.getQueueCount() %2 == 1 &&
                        ((outy + LINK_AREA_WIDTH >= y && iny - LINK_AREA_WIDTH<= y) ||
                        (outy - LINK_AREA_WIDTH <= y && iny + LINK_AREA_WIDTH >= y)) &&
                        Math.abs(c.getRx() - x) <= LINK_AREA_WIDTH && !horizontalMove) {
                    returnConnectors.add(c);
                    horizontalMove = true;
                    horizontalConnector = c;
                    continue;
                }
            }
        }catch (Exception e) {
            Console.getInstance().println(e);
        }
//        } catch (ClassCastException e) {
//            //shouldn't happen because all OutLinks and InLinks are VisibleObjects
//        } catch (NullPointerException e) {
//            //could happen when the connector has no input or output
//            //do nothing
//        }

        if (verticalMove && horizontalMove) {
            if (horizontalConnector.getX() <= verticalConnector.getX() &&
                    horizontalConnector.getY() <= verticalConnector.getY()) {
                diagonalRightMove = true;
            }
            else if (horizontalConnector.getX() <= verticalConnector.getX() &&
                    horizontalConnector.getY() > verticalConnector.getY()) {
                diagonalRightMove = false;
            }
            else if  (horizontalConnector.getX() > verticalConnector.getX() &&
                    horizontalConnector.getY() <= verticalConnector.getY()) {
                diagonalRightMove = false;
            }
            else if (horizontalConnector.getX() > verticalConnector.getX() &&
                    horizontalConnector.getY() > verticalConnector.getY()) {
                diagonalRightMove = true;
            }
        }
        return returnConnectors;
    }

    /**
     *
     * Returns the mouse cursor for the move that was last processed.
     * @return the appropriate cursor.
     */
    public Cursor getCursorForMove() {
        if (horizontalMove && !verticalMove)
            return HORIZONTAL_CURSOR;
        else if (!horizontalMove && verticalMove)
            return VERTICAL_CURSOR;
        else if (horizontalMove && verticalMove) {
            if (diagonalRightMove)
                return DIAGONAL_RIGHT_CURSOR;
            else
                return DIAGONAL_LEFT_CURSOR;
        }
        return Cursor.getDefaultCursor();
    }



}
