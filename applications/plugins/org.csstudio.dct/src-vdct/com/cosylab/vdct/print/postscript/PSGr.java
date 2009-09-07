package com.cosylab.vdct.print.postscript;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.text.AttributedCharacterIterator;

/**
 * PSGr is a awt.Graphics subclass that images to PostScript.
 * (C) 1996 E.J. Friedman-Hill and Sandia National Labs
 * @version   1.0
 * @author   Ernest Friedman-Hill
 * @author  ejfried@ca.sandia.gov
 * @author  http://herzberg.ca.sandia.gov
 * IMPORTANT!
 * Changes for Java 1.1 by Morris Hirsch mhirsch@ipdinc.com
 * All thanks to Ernest Friedman-Hill.
 * Any complaints or bugs to Morris Hirsch.
 */

public class PSGr extends java.awt.Graphics {

    public final static int CLONE = 49;

/* Standard 8.5 x 11 inch page with margins -- in points */

    protected static final int

      PAGEMARGINX = 30,
      PAGEMARGINY = 30,

      PAGEHEIGHT = 792,
      PAGEWIDTH = 612;
    protected static final boolean PAGEPORTRAIT = true;

    protected int

      Page_MarginX = PAGEMARGINX,
      Page_MarginY = PAGEMARGINY,
      Page_Height = PAGEHEIGHT,
      Page_Width = PAGEWIDTH;

	protected boolean Page_Portrait = PAGEPORTRAIT;

/* Output stream where PostScript goes */

    protected PrintStream os = System.out;

/* The current color */

    protected Color clr = Color.black;

/* The background color of the current widget.
   It's up to the client software to set this correctly! */

    protected Color backClr = Color.white;

/* The current font */

    protected Font font = new Font ("Helvetica",Font.PLAIN,12);

/* Use clippingRect for Java bookkeeping as mirror of PS clip */

    protected Rectangle clippingRect = new Rectangle (0,0,Page_Width,Page_Height);

/* The "real g" from AWT */

//graphics
//    protected Graphics realg;

/* Set true when this Graphics context is disposed,
 * after which it may no longer be used. */

    protected boolean disposed = false;

/**
 * Constructs a new PSGr Object.
 * Unlike regular Graphics objects,
 * PSGr contexts can be created directly.
 * @param o Output stream for PostScript output
 * @param g The "real g" from AWT
 * @see #create
 */

    public PSGr (OutputStream o/*, Graphics g*/) {
        this (o, /*g,*/ CLONE-1);
    }

    protected PSGr (OutputStream o,/* Graphics g,*/ int what) {
        if (o instanceof PrintStream)
            os = (PrintStream)o;
        else
            os = new PrintStream (o);
//graphics
//        realg = g;

/* This must be first thing in the file --
 * not even % comments should be ahead of it.
 * The first line is the "magic number" to invoke PS. */

        if (what != CLONE)
            emitProlog ();
    }

/** Constructor for non-default page dimensions and orientation.
 * @param o Output stream for PostScript output
 * @param g The "real g" from AWT
 * @param margin Page_MarginX (left side) in points.
 * @param margin Page_MarginY (bottom side) in points.
 * @param width Page_Width in points.
 * @param height Page_Height in points.
 * @param portrait Portrait if true, otherwise landscape.
 */

//    public PSGr (OutputStream o, Graphics g, int margin, int width, int height) {

    public PSGr (OutputStream o, /*Graphics g,*/ int marginX, int marginY, int width,
    	int height, boolean portrait)
    {
        if (o instanceof PrintStream)
            os = (PrintStream)o;
        else
            os = new PrintStream (o);
//graphics
//        realg = g;

        Page_MarginX = marginX;
        Page_MarginY= marginY;
        Page_Width = width;
        Page_Height = height;
        Page_Portrait = portrait;

	clippingRect = new Rectangle (0,0,Page_Width,Page_Height);

/* This must be first thing in the file --
 * not even % comments should be ahead of it.
 * The first line is the "magic number" to invoke PS. */

        emitProlog ();
    }

/** Creates a new PSGr Object that is a copy of the original PSGr Object.  */

/** Container.paint calls create and dispose,
 * for a separate g to clip and position each child
 * and then restore larger clip and original coordinates
 * by disposing of the smaller g and using the original g.
 * We have our own Container painting code,
 * which works the same way.
 * This simulates the context stack in PostScript,
 * and works fine as long as each g is used as described,
 * that is by using only the most recently created g.
 */

    public Graphics create () {
        emitThis ("% create");
        PSGr psgr = new PSGr (os,/*realg,*/CLONE);
        psgr.font = font;
        psgr.clippingRect = clippingRect;
        psgr.clr = clr;
        psgr.backClr = backClr;
        emitThis ("% create => "+psgr);
        return (Graphics) psgr;
    }

/**
 * Creates a new PSGr Object with the specified parameters,
 * based on the original PSGr Object.
 * This method translates the origin to the specified x and y,
 * and then clips to the area with specified width and height.
 * The resulting clipping area is the intersection
 * of the current clipping area and the specified rectangle.
 *
 * We first "gsave" the PostScript context here,
 * and we "grestore" it when dispose () is called,
 * so if create () and dispose () are used in pairs,
 * the earlier and larger clip should be preserved,
 * and what the old g thinks is the clip should be so.
 * NOT TESTED YET!
 *
 *** Compiler won't let this one be PSGr only Graphics
 *** I think because it is part of the interface spec.
 *
 * @param x the x coordinate, pixels positive right from left.
 * @param y the y coordinate, pixels positive down from top.
 * @param width the width of the area
 * @param height the height of the area
 * @see #translate
 */
    public Graphics create (int x, int y, int width, int height) {
        emitThis ("% create xywh "+x+" "+y+" "+width+" "+height);
        Graphics cg = create ();

        emitThis ("gsave");

        cg.translate (x, y);
        cg.clipRect (0, 0, width, height);

        return cg;
    }

/** Printing dimensions default or set at construct time.
 * These are methods of the PSGr extension of Graphics. */

//    public int getPageMargin () { return Page_Margin; }

    public int getPageMarginX () { return Page_MarginX; }
    public int getPageMarginY () { return Page_MarginY; }

    public int getPageHeight () { return Page_Height; }

    public int getPageWidth () { return Page_Width; }


/**
 * Translates the origin of the graphics context
 * to the specified parameters.
 * All subsequent operations on this graphics context
 * will be relative to this origin.
 * @param x the x coordinate, pixels positive right from left.
 * @param y the y coordinate, pixels positive down from top.
 * Note: PostScript y coordinate is positive up from bottom.
 * Note: PostScript default units are points (1/72 inch).
 * @see #rotate
 * @see #scale
 */

    public void translate (int x, int y) {
        if ((0 == x) && (0 == y))
            return;

        emitThis (xTrans (x)+" "+yTrans (y)+" translate");
    }

/**
 * Scales the graphics context.
 * All subsequent operations on this
 * graphics context will be affected.
 * @param sx the scaled x coordinate
 * @param sy the scaled y coordinate
 * @see #translate
 * @see #rotate
 * This is NOT part of the graphics API (although translate is).
 * It is used internally to map from pixels to points,
 * such that the entire top-level object will fit the page.
 */
    protected void scale (float sx, float sy) {
        if ((1.0 == sx) && (1.0 == sy))
            return;

        emitThis (sx+" "+sy+" scale");
    }

/**
 * Gets the current color.
 * @see #setColor
 */
    public Color getColor () {
        return clr;
    }

/**
 * Gets the current color.
 * This is NOT part of the Graphics API.
 * @see #setColor
 */
    protected void setBackground (Color c) {
        backClr = c;
    }

/**
 * Sets the current color to the specified color.
 * All subsequent graphics operations
 * will use this specified color.
 * @param c the color to be set
 * @see Color
 * @see #getColor
 */

    public void setColor (Color c) {
        if (c != null)
            clr = c;
        emitThis
	    (strunc (clr.getRed()/255.0)
        +" "+strunc (clr.getGreen()/255.0)
        +" "+strunc (clr.getBlue()/255.0)
	+" setrgbcolor");
    }

/* Short reals are enough */

    protected String strunc (double d) {
	if (0 == d)
	    return "0";

	if (1 == d)
	    return "1";

	String s = Double.toString (d);
	if (s.length () > 5)
	    return s.substring (0, 5);

        return s;
    }

/**
 * Sets the default paint mode to overwrite the destination with the
 * current color. PostScript has only paint mode.
 */
    public void setPaintMode () {
    }

/**
 * Sets the paint mode to alternate between the current color
 * and the new specified color.
 * PostScript does not support XOR mode.
 * @param c1 the second color
 */
    public void setXORMode (Color c1) {
        System.err.println ("Warning: PSGr does not support XOR mode");
    }

/**
 * Gets the current font.
 * @see #setFont
 */
    public Font getFont () {
        return font;
    }

/**
 * Sets the font for all subsequent text-drawing operations.
 * @param font the specified font
 * @see Font
 * @see #getFont
 * @see #drawString
 * @see #drawBytes
 * @see #drawChars
 */
    public void setFont (Font f) {
        emitThis ("%setFont");
        if (f != null) {
            this.font = f;
            String javaName = font.getName ();
            int javaStyle = font.getStyle ();
            String psName;

            if (javaName.equals("Symbol"))
                psName = "Symbol";

/* Times says Italic... */

            else if (javaName.startsWith("Times")
              || javaName.equals ("Serif")) {
                psName = "Times-";
                switch (javaStyle) {
                case Font.PLAIN:
                    psName += "Roman";
                    break;
                case Font.BOLD:
                    psName += "Bold";
                    break;
                case Font.ITALIC:
                    psName += "Italic";
                    break;
                case (Font.ITALIC + Font.BOLD):
                    psName += "BoldItalic";
                    break;
                }
            }

/* ...but Helvetica and Courier say Oblique */

            else if (javaName.equals("Helvetica")
              || javaName.equals ("Courier")) {
                psName = javaName;
                switch (javaStyle) {
                case Font.PLAIN:
                    break;
                case Font.BOLD:
                    psName += "-Bold";
                    break;
                case Font.ITALIC:
                    psName += "-Oblique";
                    break;
                case (Font.ITALIC + Font.BOLD):
                    psName += "BoldOblique";
                    break;
                }
            }

/* Just more names for Helvetica */

            else if (javaName.equals("SansSerif")
              || javaName.equals ("Dialog")) {
                psName = "Helvetica";
                switch (javaStyle) {
                case Font.PLAIN:
                    break;
                case Font.BOLD:
                    psName += "-Bold";
                    break;
                case Font.ITALIC:
                    psName += "-Oblique";
                    break;
                case (Font.ITALIC + Font.BOLD):
                    psName += "BoldOblique";
                    break;
                }
            }

/* And for Courier */

            else if (javaName.equals("Monospaced")) {
                psName = "Courier";
                switch (javaStyle) {
                case Font.PLAIN:
                    break;
                case Font.BOLD:
                    psName += "-Bold";
                    break;
                case Font.ITALIC:
                    psName += "-Oblique";
                    break;
                case (Font.ITALIC + Font.BOLD):
                    psName += "BoldOblique";
                    break;
                }
            }

            else
                psName = "Courier";

            emitThis ("/" + psName + " findfont");
            emitThis (""+font.getSize()+" scalefont setfont");
        }
    }

/**
 * PSGr does not implement getFontMetrics(*)
 */
    public FontMetrics getFontMetrics ()
    {
        System.err.println ("Warning: PSGr does not implement getFontMetrics(*)");
        return null;
    }

/**
 * "Warning: PSGr does not implement getFontMetrics(*)
 * @see #getFont
 */
    public FontMetrics getFontMetrics(Font f)
    {
        System.err.println ("Warning: PSGr does not implement getFontMetrics(*)");
        return null;
    }
/** MGH added 1.1 form getClipBounds () */

/**
 * Returns the bounding Rectangle of the current clipping area.
 * @see #clipRect
 * @deprecated in 1.1
 * @see getClipBounds */

    public Rectangle getClipRect () {
        return getClipBounds ();
    }

    public Rectangle getClipBounds () {
        return clippingRect;
    }

/**
 * MGH added Shape getClip () per 1.1 API.
 * Rectangle implements Shape.
 */

    public Shape getClip () {
        System.err.println ("Shape getClip () is not implemented:");
        return getClipBounds ();
    }

/** MGH added setClip (Shape s) hacked over Rectangle.
 * Really nothing to do for Shape as yet... */

    public void setClip (Shape s) {
        System.err.println ("setClip (Shape) is not implemented:"+s);
        Rectangle sb = s.getBounds ();
        setClip (sb.x, sb.y, sb.width, sb.height);
    }

/** MGH added setClip (x y w h) per 1.1 API,
 * this is supposed to be **NEW** Clip,
 * rather than intersection with existing Clip,
 * but that may not be correct yet..
 * Easy to make the new clippingRect,
 * but that is only the Java book keeping version.
 * The actual PostScript clip is the intersection,
 * unless we use initclip to reset it,
 * which I could do...
 * But my PostScript book says to "almost never" do initclip.
 * Instead we should grestore but WHEN WAS THE GSAVE?
 * Use grestoreall,
 * it pops all gsaves until the one that was done to start us,
 * uses that one and leaves it on the stack,
 * so it can be done again and again.
 *
 * NOT TESTED YET!
 */

    public void setClip (int x, int y, int width, int height) {
        emitThis ("%setClip "+x+" "+y+" "+width+" "+height);

        clippingRect = new Rectangle (x,y,width,height);

        int xps = xTrans (x);
        int yps = yTrans (y);
        int wps = dTrans (width);
        int hps = dTrans (height);

        //// emitThis ("initclip");
        emitThis ("grestoreall");

        emitThis ("newpath");

        emitThis (xps + " " + yps + " moveto");
        emitThis ((xps + wps) + " " + yps  + " lineto");
        emitThis ((xps + wps) + " " + (yps - hps) + " lineto");
        emitThis (xps  + " " + (yps - hps) + " lineto");
        emitThis ("closepath clip newpath");
    }

/**
 * Clips to a rectangle.
 * The resulting clipping area is the
 * intersection of the current clipping area and the specified
 * rectangle.
 * Graphic operations have no effect outside of the
 * clipping area.

 * MGH made clippingRect = clippingRect.intersection,
 * MGH commented out // emitThis ("initclip");
 * MGH added the starting emitThis ("newpath");

 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #getClipRect
 */
    public void clipRect (int x, int y, int width, int height) {
        emitThis ("%clipRect "+x+" "+y+" "+width+" "+height);

        clippingRect = clippingRect.intersection
          (new Rectangle(x,y,width,height));
        ////// emitThis ("initclip");

        int xps = xTrans (x);
        int yps = yTrans (y);
        int wps = dTrans (width);
        int hps = dTrans (height);

        emitThis ("newpath");

        emitThis (xps + " " + yps + " moveto");
        emitThis ((xps + wps) + " " + yps  + " lineto");
        emitThis ((xps + wps) + " " + (yps - hps) + " lineto");
        emitThis (xps  + " " + (yps - hps) + " lineto");
        emitThis ("closepath clip newpath");
    }

/**
 * Copies an area of the screen.
 * @param x the x-coordinate of the source
 * @param y the y-coordinate of the source
 * @param width the width
 * @param height the height
 * @param dx the horizontal distance
 * @param dy the vertical distance
 * Note: copyArea not supported by PostScript
 */
    public void copyArea (int x, int y, int width, int height, int dx, int dy) {
        throw new RuntimeException ("copyArea not supported");
    }

/**
 * Draws a line between the coordinates (x1,y1) and (x2,y2).
 * The line is drawn below and to the left of the logical coordinates.
 * @param x1 the first point's x coordinate
 * @param y1 the first point's y coordinate
 * @param x2 the second point's x coordinate
 * @param y2 the second point's y coordinate
 */
    public void drawLine (int x1, int y1, int x2, int y2) {
        emitThis ("%drawLine "+x1+" "+y1+" "+x2+" "+y2);

        int xps1 = xTrans (x1);
        int yps1 = yTrans (y1);

        int xps2 = xTrans (x2);
        int yps2 = yTrans (y2);

        emitThis (xps1+" "+yps1+" moveto "+xps2+" "+yps2+" lineto stroke");
    }

/** Polyline
 * MGH added to satisfy 1.1 API
 */

    public void drawPolyline (int [] x, int [] y, int np) {
        for (int ii = 0; ii < np-1; ii++)
            drawLine (x[ii], y[ii], x[ii+1], y[ii+1]);
    }

    protected void doRect (int x, int y, int width, int height, boolean fill) {

        int xps = xTrans (x);
        int yps = yTrans (y);
        int wps = dTrans (width);
        int hps = dTrans (height);

        emitThis (xps+" "+yps+" "+wps+" "+hps+" "+fill+" doRect");
    }

/**
 * Fills the specified rectangle with the current color.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #drawRect
 * @see #clearRect
 */
    public void fillRect (int x, int y, int width, int height) {
        doRect (x,y,width,height,true);
    }

/**
 * Draws the outline of the specified rectangle using the current color.
 * Use drawRect (x, y, width-1, height-1) to draw the outline inside the specified
 * rectangle.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #fillRect
 * @see #clearRect
 */
    public void drawRect (int x, int y, int width, int height) {
        doRect (x,y,width,height,false);
    }

/**
 * Clears the specified rectangle by filling it with the current background color
 * of the current drawing surface.
 * Which drawing surface it selects depends on how the graphics context
 * was created.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #fillRect
 * @see #drawRect
 */
    public void clearRect (int x, int y, int width, int height) {
        emitThis ("%clearRect");
        emitThis ("gsave");
        Color c = getColor ();
        setColor (backClr);
        doRect (x,y,width,height, true);
        setColor (c);
        emitThis ("grestore");
    }

    private void doRoundRect (int x, int y, int width, int height,
      int arcWidth, int arcHeight, boolean fill) {
        int yps = yTrans (y);
        int xps = xTrans (x);
        int wps = dTrans (width);
        int hps = dTrans (height);

        emitThis (xps+" "+yps+" "+wps+" "+hps+" "+arcWidth+" "+arcHeight+" "+fill+" doRoundRect");
    }

/**
 * Draws an outlined rounded corner rectangle using the current color.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param arcWidth the diameter of the arc
 * @param arcHeight the radius of the arc
 * @see #fillRoundRect
 */
    public void drawRoundRect (int x, int y, int width, int height, int arcWidth, int arcHeight) {
        emitThis ("%drawRoundRect");
        doRoundRect (x,y,width,height,arcWidth,arcHeight, false);
    }

/**
 * Draws a rounded rectangle filled in with the current color.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param arcWidth the diameter of the arc
 * @param arcHeight the radius of the arc
 * @see #drawRoundRect
 */
    public void fillRoundRect (int x, int y, int width, int height, int arcWidth, int arcHeight) {
        emitThis ("%fillRoundRect");
        doRoundRect (x,y,width,height,arcWidth,arcHeight, true);
    }

/**
 * Draws a highlighted 3-D rectangle.
 * Two edges brighter and two darker.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param raised a boolean that states whether the rectangle is raised or not
 */
    public void draw3DRect (int x, int y, int width, int height, boolean raised) {
        emitThis ("%draw3DRect");
        Color c = getColor ();
        Color brighter = c.brighter ();
        Color darker = c.darker ();

        setColor (raised ? brighter : darker);
        drawLine (x, y, x, y + height);
        drawLine (x + 1, y, x + width - 1, y);
        setColor (raised ? darker : brighter);
        drawLine (x + 1, y + height, x + width, y + height);
        drawLine (x + width, y, x + width, y + height);
        setColor (c);
    }

/**
 * Paints a highlighted 3-D rectangle using the current color.
 * Two edges brighter and two darker.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param raised a boolean that states whether the rectangle is raised or not
 */
    public void fill3DRect (int x, int y, int width, int height, boolean raised) {
        emitThis ("%fill3DRect");
        Color c = getColor ();
        Color brighter = c.brighter ();
        Color darker = c.darker ();

        if (!raised)
            setColor (darker);

        fillRect (x+1, y+1, width-2, height-2);
        setColor (raised ? brighter : darker);
        drawLine (x, y, x, y + height - 1);
        drawLine (x + 1, y, x + width - 2, y);
        setColor (raised ? darker : brighter);
        drawLine (x + 1, y + height - 1, x + width - 1, y + height - 1);
        drawLine (x + width - 1, y, x + width - 1, y + height - 1);
        setColor (c);
    }

/**
 * Draws an oval inside the specified rectangle using the current color.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #fillOval
 */
    public void drawOval (int x, int y, int width, int height) {
        emitThis ("%drawOval");
        doArc (x,y,width,height,0,360,false);
    }

/**
 * Fills an oval inside the specified rectangle using the current color.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @see #drawOval
 */
    public void fillOval (int x, int y, int width, int height) {
        emitThis ("%fillOval");
        doArc (x,y,width,height,0,360,true);
    }

    private void doArc (int x, int y, int width, int height,
      int startAngle, int arcAngle, boolean fill) {

        int xps = xTrans (x);
        int yps = yTrans (y);
        int wps = dTrans (width);
        int hps = dTrans (height);

        emitThis (xps+" "+yps+" "+wps+" "+hps+" "+startAngle+" "+arcAngle+" "+fill+" doArc");
    }

/**
 * Draws an arc bounded by the specified rectangle from startAngle to
 * endAngle. 0 degrees is at the 3-o'clock position.Positive arc
 * angles indicate counter-clockwise rotations, negative arc angles are
 * drawn clockwise.
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param startAngle the beginning angle
 * @param arcAngle the angle of the arc (relative to startAngle).
 * @see #fillArc
 */
    public void drawArc (int x, int y, int width, int height,
      int startAngle, int arcAngle) {
        emitThis ("%drawArc");
        doArc (x,y,width,height,startAngle,arcAngle,false);
    }

/**
 * Fills an arc using the current color. This generates a pie shape.
   *
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the arc
 * @param height the height of the arc
 * @param startAngle the beginning angle
 * @param arcAngle the angle of the arc (relative to startAngle).
 * @see #drawArc
 */
    public void fillArc (int x, int y, int width, int height, int startAngle, int arcAngle) {
        emitThis ("%fillArc");
        doArc (x,y,width,height,startAngle,arcAngle,true);
    }

    private void doPoly (int xPoints[], int yPoints[], int nPoints, boolean fill) {
        if (nPoints < 2)
            return;

        int newXPoints[] = new int[nPoints];
        int newYPoints[] = new int[nPoints];

        int i;

        for (i=0; i< nPoints; i++) {
            newXPoints[i] = xTrans (xPoints[i]);
            newYPoints[i] = yTrans (yPoints[i]);
        }

        emitThis (""+xPoints[0]+" "+newYPoints[0]+" moveto");

        for (i=0; i<nPoints; i++)
            emitThis (newXPoints[i]+" "+newYPoints[i]+" lineto");

        if (fill)
            emitThis ("fill");
        else
            emitThis ("stroke");
    }

/**
 * Draws a polygon defined by an array of x points and y points.
 * @param xPoints an array of x points
 * @param yPoints an array of y points
 * @param nPoints the total number of points
 * @see #fillPolygon
 */
    public void drawPolygon (int xPoints[], int yPoints[], int nPoints) {
        emitThis ("%drawPoly");
        doPoly (xPoints, yPoints, nPoints, false);
    }

/**
 * Draws a polygon defined by the specified point.
 * @param p the specified polygon
 * @see #fillPolygon
 */
    public void drawPolygon (Polygon p) {
        emitThis ("%drawPoly");
        doPoly (p.xpoints, p.ypoints, p.npoints, false);
    }

/**
 * Fills a polygon with the current color.
 * @param xPoints an array of x points
 * @param yPoints an array of y points
 * @param nPoints the total number of points
 * @see #drawPolygon
 */
    public void fillPolygon (int xPoints[], int yPoints[], int nPoints) {
        emitThis ("%fillPoly");
        doPoly (xPoints, yPoints, nPoints, true);
    }

/**
 * Fills the specified polygon with the current color.
 * @param p the polygon
 * @see #drawPolygon
 */
    public void fillPolygon (Polygon p) {
        emitThis ("%fillPoly");
        doPoly (p.xpoints, p.ypoints, p.npoints, true);
    }

/**
 * Draws the specified String using the current font and color.
 * The x,y position is the starting point of the baseline of the String.
 * @param str the String to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @see #drawChars
 * @see #drawBytes
*** WE CAN CORRECT FOR MINOR FONT DIFFERENCES
*** USE JAVA METRICS FOR EXPECTED STRINGWIDTH
*** TELL POSTSCRIPT TO KSHOW AND EXACTLY FILL THAT WIDTH
   *
 * BUG!
 * Any backslashes (e.g. separators in file names) show on screen,
 * at least if they are not a legitimate escape sequence,
 * I HAVE NOT TRIED YET To see what they do on screen,
 * but must be doubled (\ to \\) to show in the PostScript output.
 * So first see what AWT does for a legitimate escape sequence,
 * then do what we can for the PostScript string.
 * According to the PostScript LRF "File Input and Output"
 * PostScript recognizes \ddd for octal character codes,
 * \n newline
 * \r return
 * \t tab
 * \b backspace
 * \f feed (top-of-form)
 * \ ( lpar
 * \) rpar
 * \\ backslash itself
 * \newline eats the backslash and the newline
 * \X any other character eats the backslash
 * I wonder what they do for top-of-form? */

    public void drawString (AttributedCharacterIterator iterator, int x, int y)
    {
    }

    public void drawString (String str, int x, int y) {
        emitThis ("%drawString "+str+" "+x+" "+y);
        int xps = xTrans (x);
        int yps = yTrans (y);
        emitThis (xps+" "+yps+" moveto"+" ("+str+") show stroke");
    }

/**
 * Draws the specified characters using the current font and color.
 * @param data the array of characters to be drawn
 * @param offset the start offset in the data
 * @param length the number of characters to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @see #drawString
 * @see #drawBytes
 */
    public void drawChars (char data[], int offset, int length, int x, int y) {
        emitThis ("%drawChars");
        drawString (new String(data, offset, length), x, y);
    }

/**
 * Draws the specified bytes using the current font and color.
 * @param data the data to be drawn
 * @param offset the start offset in the data
 * @param length the number of bytes that are drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @see #drawString
 * @see #drawChars
 */

    public void drawBytes (byte data[], int offset, int length, int x, int y) {
        emitThis ("%drawBytes");
        drawString (new String(data, offset, length), x, y);
    }

    private String [] hd =
    { "0", "1", "2", "3", "4", "5", "6", "7",
      "8", "9", "A", "B", "C", "D", "E", "F" };

/* Always two characters "00" through "FF"
 * Cannot use Integer.toHexString because not always two characters. */

    protected String myHexString (int n) {
	int msb = (n >> 4) & 0xf;
	int lsb = n & 0xf;
	return hd[msb] + hd[lsb];
    }

/* Support all versions of drawImage () */

    protected boolean doImage (Image img,
    int x, int y, int width, int height,
      ImageObserver observer,
      Color bgcolor) {

        String bgString = null;

        if (null != bgcolor)
	    bgString =
	     myHexString (bgcolor.getRed ())
	    +myHexString (bgcolor.getGreen ())
	    +myHexString (bgcolor.getBlue ());

        int yps = yTrans (y);
        int xps = xTrans (x);
        int wps = dTrans (width);
        int hps = dTrans (height);

/* Get image dimensions and data */

	int iw = img.getWidth (observer);
	int ih = img.getHeight (observer);

/* Punt --
 * should just wait here until we have it,
 * waiting for image before coming here is the safest way */

	if ((0 > iw) || (0 > ih))
	    return false;

        emitThis ("gsave");

     //// emitThis ("20 dict begin");

/* We need not write and read exact scanlines,
 * or even write and read the same size lines,
 * as long as we come out even by the end.
 * Doing so keeps the code simple,
 * but writing very long lines may be a problem elsewhere,
 * e.g. emailing a file may truncate long lines. */

        emitThis ("% string to hold a scanline's worth of data");
        emitThis ("/pix "+(iw*6)+" string def");

        emitThis ("% space for color conversion to gray");
        emitThis ("/grays "+(iw*2)+" string def");
        emitThis ("/npixls 0 def");
        emitThis ("/rgbindx 0 def");

/* if calling width or height is 0,
 * it means to use actual image dimensions,
 * otherwise scale to fill given rectangle. */

        if (hps == 0 || wps == 0) {
            hps = ih;
            wps = iw;
        }

/* lower left corner */

        emitThis (xps+" "+(yps-hps)+" translate");

/* scale from unit square to size to be filled */

        emitThis (wps+" "+hps+" scale");

/* width height bits */

        emitThis (iw+" "+ih+" 8");

/* Matrix [w 0 0 -h 0 h]
 * for image scanned left-to-right and top-down as Java does
 * to fill a unit square */

        emitThis ("["+iw+" 0 0 -"+ih+" 0 "+ih+"]");

/* Procedure to read following data lines --
 * discarding the EOF test because it would never happen,
 * instead would read beyond data into following code! */

        emitThis ("{currentfile pix readhexstring pop}");

/* last couple of args for the Level 2 operator,
 * single stream and 3 color channels */

        emitThis ("false 3");

/* Do it (or fake it) */

        emitThis ("colorimage");

/* Blank cosmetic line before the data lines --
 * harmless with readhexstring */

        emitThis ("");

	int nchars = 0;

/* This is simplest but BAD FOR LARGE IMAGES,
 * because it requires a lot of memory,
 * better to grab and emit by bands.
 * Although we could use the same pixels array for each band,
 * it seems we would need a new PixelGrabber each time,
 * because the constructor requires x y w h. */

	int [] pixels = new int [iw * ih];

        PixelGrabber pg = new PixelGrabber (img, 0, 0, iw, ih, pixels, 0, iw);

/* Here again simplest but BAD FOR LARGE IMAGES,
 * better use grabPixels (maxMSec)
 * note that either form throws InterruptedException.
 * Should never use wait-forever. */

	try { pg.grabPixels (); }
	catch (Exception ex) {
	    System.out.println ("Failed grabPixels");
	    return false;
	}

	for (int py = 0; py < ih; py++) {
	    for (int px = 0; px < iw; px++) {
		int apixel = pixels [py * iw + px];

		int alpha = (apixel >> 24) & 0xff;
		int red   = (apixel >> 16) & 0xff;
		int green = (apixel >>  8) & 0xff;
		int blue  = (apixel ) & 0xff;

/* Completely transparent pixels get BG if one specified.
 * We make no other (partial) use of alpha,
 * either we paint BG or stated color,
 * never leave the PostScript BG. */

		if ((0 == alpha) && (null != bgString))
		    emitThisNext (bgString);

		else
		    emitThisNext
		    (myHexString (red)
		    +myHexString (green)
		    +myHexString (blue));

/* Because readhextstring ignores whitespace,
 * we can write these safe and tidy blocks regardless,
 * and still use a read buffer sized to image width. */

		nchars +=6;
		if (60 == nchars) {
		    emitThis ("");
		    nchars = 0;
		}
	    }
	}
	if (0 < nchars)
	    emitThis ("");

/* Blank cosmetic line after all the data */

        emitThis ("");

/* Done with our dict, clean up state */

     //// emitThis ("end");
        emitThis ("grestore");

        return true;
    }

/**
 * Draws the specified image at the specified top left (x, y),
 * with width and height determined by the image.
 * If the image is incomplete return false.
 * The image observer will be notified later.
 * @param img the specified image to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @param observer notifies if the image is complete or not
 * @see Image
 * @see ImageObserver
 */

    public boolean drawImage (Image img, int x, int y,
    ImageObserver observer) {
        emitThis ("% drawImage(img, x, y, obs)");
        return doImage (img, x, y, 0, 0, observer, null);
    }

/**
 * Draws the specified image inside the specified rectangle.
 * The image is scaled if necessary.
 * If the image is incomplete return false.
 * The image observer will be notified later.
 * @param img the specified image to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param observer notifies if the image is complete or not
 * @see Image
 * @see ImageObserver
 */
    public boolean drawImage (Image img, int x, int y,
    int width, int height,
    ImageObserver observer) {
        emitThis ("% drawImage(img, x, y, w, h, obs)");
        return doImage (img, x, y, width, height, observer, null);
    }

/**
 * Draws the specified image at the specified top left (x, y),
 * with width and height determined by the image.
 * If the image is incomplete return false.
 * The image observer will be notified later.
 * @param img the specified image to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @param bgcolor the background color
 * @param observer notifies if the image is complete or not
 * @see Image
 * @see ImageObserver
 */

    public boolean drawImage (Image img, int x, int y, Color bgcolor,
    ImageObserver observer) {
        emitThis ("% drawImage(img, x, y, bg, obs)");
        return doImage (img, x, y, 0, 0, observer, bgcolor);
    }

/**
 * Draws the specified image inside the specified rectangle.
 * The image is scaled if necessary.
 * If the image is incomplete the image observer will be
 * notified later.
 * @param img the specified image to be drawn
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 * @param bgcolor the background color
 * @param observer notifies if the image is complete or not
 * @see Image
 * @see ImageObserver
 * NOTE: PSGr ignores the background color.
 */
    public boolean drawImage (Image img, int x, int y,
    int width, int height, Color bgcolor,
    ImageObserver observer) {
        emitThis ("% drawImage(img, x, y, w, h, bg, obs)");
        return doImage (img, x, y, width, height, observer, bgcolor);
    }

/** Scaling and translating versions not supported -- BUT COULD BE
 * @param img - the specified image to be drawn
 * @param dx1 - the x coordinate of the first corner of the destination rectangle.
 * @param dy1 - the y coordinate of the first corner of the destination rectangle.
 * @param dx2 - the x coordinate of the second corner of the destination rectangle.
 * @param dy2 - the y coordinate of the second corner of the destination rectangle.
 * @param sx1 - the x coordinate of the first corner of the source rectangle.
 * @param sy1 - the y coordinate of the first corner of the source rectangle.
 * @param sx2 - the x coordinate of the second corner of the source rectangle.
 * @param sy2 - the y coordinate of the second corner of the source rectangle.
 * @param bgcolor the background color
 * @param observer - object to be notified as more of the image is scaled and converted
 * NOTE: PSGr ignores the background color.
 * @see Image
 * @see ImageObserver
 */

    public boolean drawImage (Image img,
    int dx1, int dy1,
    int dx2, int dy2,
    int sx1, int sy1,
    int sx2, int sy2,
    Color bgcolor,
    ImageObserver observer) {
        System.err.println ("Warning: PSGr does not support image mapping");
        return drawImage (img,
          dx1, dy1,
          dx2-dx1, dy2-dy1,
          bgcolor,
          observer);
    }

/** Scaling and translating versions not supported -- BUT WE COULD
 * @param img - the specified image to be drawn
 * @param dx1 - the x coordinate of the first corner of the destination rectangle.
 * @param dy1 - the y coordinate of the first corner of the destination rectangle.
 * @param dx2 - the x coordinate of the second corner of the destination rectangle.
 * @param dy2 - the y coordinate of the second corner of the destination rectangle.
 * @param sx1 - the x coordinate of the first corner of the source rectangle.
 * @param sy1 - the y coordinate of the first corner of the source rectangle.
 * @param sx2 - the x coordinate of the second corner of the source rectangle.
 * @param sy2 - the y coordinate of the second corner of the source rectangle.
 * @param observer - object to be notified as more of the image is scaled and converted
 * @see Image
 * @see ImageObserver
 */

    public boolean drawImage (Image img,
    int dx1, int dy1,
    int dx2, int dy2,
    int sx1, int sy1,
    int sx2, int sy2,
    ImageObserver observer) {
        System.err.println ("Warning: PSGr does not support image mapping");
        return drawImage (img,
          dx1, dy1,
          dx2-dx1, dy2-dy1,
          observer);
    }

/**
 * Disposes of this Graphics context,
 * which cannot be used after being disposed of.
 * @see #finalize
 */
    public void dispose () {
	if (disposed)
	    return;

        emitThis ("%dispose "+this);
        emitThis ("grestore");
        os.flush ();

	disposed = true;
    }

/** Needed unless the file is intended as part of a larger one.
 * As best I understand the conventions,
 * we are expected to say showpage,
 * but any "wrapped" code may disable it,
 * and scale this as part of a larger page.
 *
 * The grestore and gsave seem to be needed,
 * otherwise all but first page came out blank,
 * I don't see why that should be. */

    public void showpage () {
        emitThis ("showpage");
        emitThis ("grestore");
        emitThis ("gsave");
        os.flush ();
    }

/**
 * Disposes of this graphics context once it is no longer referenced.
 * @see #dispose
 */
    public void finalize () {
        dispose ();
    }

/**
 * Returns a String object representing this Graphic's value.
 */
    public String toString () {
        return getClass ().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

/* Flip Y coords so Postscript looks like Java.
 * Leave X coords and distances (width and height) alone. */

    protected int yTrans (int y) {
        return -y;
    }
    protected int xTrans (int x) {
        return x;
    }
    protected int dTrans (int d) {
        return d;
    }

/** Top of every PS file needs this.
 * It must be the first thing in the file.
 * The first line is a "magic number" to invoke PostScript.
 * There are other standard %% comments that could be added. */

    protected void emitProlog () {
        emitThis ("%!PS-Adobe-2.0");
        emitThis ("%%Creator: PSGr Java PostScript Context");
        emitThis ("%%Copyright: 1996 Ernest Friedman-Hill and Sandia National Labs");
        emitThis ("%%EndComments");

        emitThis ("% PSGr is a awt.Graphics subclass that images to PostScript.");
        emitThis ("% (C) 1996 E.J. Friedman-Hill and Sandia National Labs");
        emitThis ("% @version   1.0");
        emitThis ("% author   Ernest Friedman-Hill");
        emitThis ("% ejfried@ca.sandia.gov");
        emitThis ("% http://herzberg.ca.sandia.gov");

        emitThis ("% Right to unrestricted personal and commerical use is granted");
        emitThis ("% if this acknowledgement is given on product or packing materials");

        emitThis ("% Color picture stuff, lifted from XV's PS files");
        emitThis ("% 'colortogray' and 'mergeprocs' come from xwd2ps via xgrab");

        emitThis ("% --------------");
        emitThis ("%%BeginProlog");

        emitThis ("% define a weighted RGB->I function");
        emitThis ("/colortogray {");
        emitThis ("/rgbdata exch store  % call input 'rgbdata'");
        emitThis ("rgbdata length 3 idiv");
        emitThis ("/npixls exch store");
        emitThis ("/rgbindx 0 store");
        emitThis ("0 1 npixls 1 sub {");
        emitThis ("grays exch");
        emitThis ("rgbdata rgbindx   get 20 mul  % Red");
        emitThis ("rgbdata rgbindx 1 add get 32 mul  % Green");
        emitThis ("rgbdata rgbindx 2 add get 12 mul  % Blue");
        emitThis ("add add 64 idiv  % I = .5G + .31R + .18B");
        emitThis ("put");
        emitThis ("/rgbindx rgbindx 3 add store");
        emitThis ("} for");
        emitThis ("grays 0 npixls getinterval");
        emitThis ("} bind def");

        emitThis ("% --------------");

        emitThis ("% This procedure takes two procedures off the");
        emitThis ("% stack and merges them into a single procedure.");
        emitThis ("% See an earlier version in Blue Cookbook Program 6");
        emitThis ("");
        emitThis ("/mergeprocs {");
        emitThis ("dup length");
        emitThis ("3 -1 roll");
        emitThis ("dup");
        emitThis ("length");
        emitThis ("dup");
        emitThis ("5 1 roll");
        emitThis ("3 -1 roll");
        emitThis ("add");
        emitThis ("array cvx");
        emitThis ("dup");
        emitThis ("3 -1 roll");
        emitThis ("0 exch");
        emitThis ("putinterval");
        emitThis ("dup");
        emitThis ("4 2 roll");
        emitThis ("putinterval");
        emitThis ("} bind def");

        emitThis ("% --------------");

        emitThis ("% define 'colorimage' fallback if it isn't defined");
        emitThis ("/colorimage where");
        emitThis ("% yes: pop off the 'dict' returned");
        emitThis ("{ pop }");
        emitThis ("% no:  define one");
        emitThis ("{ /colorimage {");
        emitThis ("  pop pop   % remove 'false 3' operands we do not use");
        emitThis ("  {colortogray} mergeprocs");
        emitThis ("  image");
        emitThis ("} bind def");
        emitThis ("} ifelse");

        emitThis ("% --------------");

        emitThis ("/doRect { % x y w h uf");
        emitThis ("/uf exch def /h exch def /w exch def /y exch def /x exch def");
        emitThis ("newpath");
        emitThis ("x y moveto");
        emitThis ("w 0 rlineto");
        emitThis ("0 h neg rlineto");
        emitThis ("w neg 0 rlineto");
        emitThis ("closepath");
        emitThis ("uf {fill} {stroke} ifelse } bind def");

        emitThis ("% --------------");

        emitThis ("/doRoundRect { % x y w h arcWidth arcHeight uf");
        emitThis ("/uf exch def /arcHeight exch def /arcWidth exch def /h exch def /w exch def /y exch def /x exch def");

        emitThis ("x arcHeight add y moveto");
        emitThis ("x w add y x w add y h sub arcHeight arcto pop pop pop pop");
        emitThis ("x w add y h sub x y h sub arcHeight arcto pop pop pop pop");
        emitThis ("x y h sub x y arcHeight arcto pop pop pop pop");
        emitThis ("x y x w add y arcHeight arcto pop pop pop pop");

        emitThis ("closepath");
        emitThis ("uf {fill} {stroke} ifelse } bind def");

        emitThis ("% --------------");

        emitThis ("/doArc { % x y w h startAngle arcAngle uf");

        emitThis ("gsave");

        emitThis ("/uf exch def /arcAngle exch def /startAngle exch def /h exch def /w exch def /y exch def /x exch def");

        // cx,cy is the center of the arc
        // translate the page to be centered there

          emitThis ("x w 2 div add y h 2 div sub translate");

        // scale the Y coordinate system
        // this is the only way to directly draw
        // an eliptical arc in postscript.

          emitThis ("1 h w div scale");

        emitThis ("uf {0 0 moveto} if");

        // now draw the arc.

          emitThis ("0 0 w 2 div startAngle dup arcAngle add arc");

        // undo all the scaling!

          emitThis ("uf {closepath fill} {stroke} ifelse grestore} bind def");

        emitThis ("% --------------");
        emitThis ("%%EndProlog");

/* Allow for non-printing margins all around.
 * Negative Y from top of page (less margin) for PostScript coord. */

		if(Page_Portrait)
	        emitThis(Page_MarginX + " " + (Page_Height - Page_MarginY) + " translate");
	    else
	    {
	        emitThis("90 rotate");
	        emitThis(Page_MarginX + " " + (-Page_MarginY) + " translate");
	    }
	    	
//        emitThis (angle + " rotate");


/* Save our (almost) starting state,
 * before any clips,
 * or other translates */

        emitThis ("gsave");

        setFont (font);
    }

/** Save and restore the PostScript state,
 * SHOULD ALSO any Java mirror state */

    protected void gsave () {
        emitThis ("gsave");
    }

    protected void grestore () {
        emitThis ("grestore");
    }

    public void emitThisNext (String s) {
	if (disposed)
	    throw new IllegalStateException ("Graphics has been disposed");
        os.print (s);
    }

    public void emitThis (String s) {
	if (disposed)
	    throw new IllegalStateException ("Graphics has been disposed");
        os.println (s);
    }

    protected void myBasicPaint (Component comp, boolean edge) {
        Dimension tb = comp.getSize ();

        if (null != comp.getFont ())
            setFont (comp.getFont ());

        if (null != comp.getBackground ())
            setBackground (comp.getBackground ());
        else
            setBackground (Color.white);

        clearRect (0, 0, tb.width, tb.height);

        if (null != comp.getForeground ())
            setColor (comp.getForeground ());
        else
            setColor (Color.black);

        if (edge)
	    drawRect (0, 0, tb.width-1, tb.height-1);
    }

/** Find scale factor that will fit the page,
 * use smaller of x and y for both,
 * to convert all pixels to points.
 * Only reduce scale if needed -- do not ever magnify.
 * PostScript scale operator also changes text sizes.
 * Must allow for non-printing margins all around. */

    public void scalePaint (Component top) {

        Dimension topSize = top.getSize ();

        float xScale = ((float)Page_Width-2*Page_MarginX)
            / ((float)topSize.width);
        float yScale = ((float)Page_Height-2*Page_MarginY)
            / ((float)topSize.height);

        float xyScale = yScale;

        if (xScale < yScale)
            xyScale = xScale;

        if (xyScale > 1)
            xyScale = 1;

        emitThis (xyScale+" "+xyScale+" scale");
    }
    

}
/* <IMG SRC="/cgi-bin/counter">*/
