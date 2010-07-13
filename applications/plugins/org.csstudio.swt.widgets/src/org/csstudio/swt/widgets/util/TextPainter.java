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
 package org.csstudio.swt.widgets.util;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * Utility class for drawing text at different angles and alignments.
 * 
 * @author jbercic
 * 
 */
public final class TextPainter {
	/**
	 * Alignment constants.
	 */
	public static final int TOP_LEFT = 0;
	public static final int TOP_CENTER = 1;
	public static final int TOP_RIGHT = 2;
	public static final int LEFT = 3;
	public static final int CENTER = 4;
	public static final int RIGHT = 5;
	public static final int BOTTOM_LEFT = 6;
	public static final int BOTTOM_CENTER = 7;
	public static final int BOTTOM_RIGHT = 8;

	/**
	 * Calculates the offset to the top-left corner of the text, according to
	 * the given anchor point.
	 * 
	 * @param gfx
	 *            the graphics context (need this to get the font)
	 * @param text
	 *            the text to be drawn
	 * @param align
	 *            alignment
	 * @return the point at which the text is to be drawn
	 */
	public static Point getDelta(Graphics gfx, String text, int align) {
		Dimension td = FigureUtilities.getTextExtents(text, gfx.getFont());

		if (align == TOP_LEFT)   { return new Point(0          ,0); }
		if (align == TOP_CENTER) { return new Point(-td.width/2,0); }
		if (align == TOP_RIGHT)  { return new Point(-td.width  ,0); }
		
		if (align == LEFT)   { return new Point(0          ,-td.height/2); }
		if (align == CENTER) { return new Point(-td.width/2,-td.height/2); }
		if (align == RIGHT)  { return new Point(-td.width  ,-td.height/2); }

		if (align == BOTTOM_LEFT)   { return new Point(0          ,-td.height); }
		if (align == BOTTOM_CENTER) { return new Point(-td.width/2,-td.height); }
		if (align == BOTTOM_RIGHT)  { return new Point(-td.width  ,-td.height); }

		return new Point(0,0);
	}

	/**
	 * Draws the given string horizontally at the given anchor point.
	 * 
	 * @param gfx
	 *            the graphics context
	 * @param text
	 *            the text to be drawn
	 * @param x
	 *            the x coordinate of the anchor
	 * @param y
	 *            the y coordinate of the anchor
	 * @param align
	 *            where on the bounding box the anchor is
	 */
	public static void drawText(Graphics gfx, String text, int x, int y, int align) {
		Point d = getDelta(gfx, text, align);
		gfx.drawText(text, x + d.x, y + d.y);
	}

	/**
	 * Draws the given text, rotated for the specified angle
	 * (counter-clockwise).</br> The text will be rotated around the anchor.
	 * 
	 * @param gfx
	 *            the graphics context
	 * @param text
	 *            the text to be drawn
	 * @param angle
	 *            the rotation of the text, in degrees
	 * @param x
	 *            the x coordinate of the anchor
	 * @param y
	 *            the y coordinate of the anchor
	 * @param align
	 *            where on the bounding box the anchor is
	 */
	public static void drawRotatedText(final Graphics gfx, String text, double angle, int x, int y, int align) {
		Point d = getDelta(gfx, text, align);

		gfx.translate(x, y);
		try {
			gfx.rotate((float) angle);
		} catch (RuntimeException e) {
			/**
			 * some classes (e.g. {@link ScaledGraphics}) might not support
			 * rotation yet
			 */
		}

		gfx.drawText(text, d.x, d.y);

		try {
			gfx.rotate(-(float) angle);
		} catch (RuntimeException e) {
			/**
			 * some classes (e.g. {@link ScaledGraphics}) might not support
			 * rotation yet
			 */
		}
		gfx.translate(-x, -y);
	}
}
