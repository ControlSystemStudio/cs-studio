/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.components.ui.internal.utils;

import org.eclipse.draw2d.Graphics;

/**
 * Utility class for drawing nice 3D-looking things.
 *
 * @author jbercic
 *
 */
public final class ShadedDrawing {
    /**
     * Draws a line at the given angle, relative to the given center.
     *
     * @param gfx the graphics context
     * @param minrad the radius at which the line should begin
     * @param maxrad the radius at which the ine should end
     * @param angl the angle at which the line is to be drawn
     * @param x x coordinate of the circle's center
     * @param y y coordinate of the circle's center
     */
    public static void drawLineAtAngle(Graphics gfx, double minrad, double maxrad, double angl, int x, int y) {
        int x1,y1,x2,y2;

        x1=x+(int)(minrad*Trigonometry.cos(angl));
        y1=y-(int)(minrad*Trigonometry.sin(angl));
        x2=x+(int)(maxrad*Trigonometry.cos(angl));
        y2=y-(int)(maxrad*Trigonometry.sin(angl));
        gfx.drawLine(x1, y1, x2, y2);
    }
}
