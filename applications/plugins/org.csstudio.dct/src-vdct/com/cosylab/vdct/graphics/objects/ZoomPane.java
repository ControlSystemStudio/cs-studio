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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Vector;

import com.cosylab.vdct.Constants;
import com.cosylab.vdct.VisualDCT;
import com.cosylab.vdct.graphics.ViewState;

/**
 * <code>ZoomPane</code> enables blowing up zoomed objects. It provides
 * the image containing zoomed objects in scale 1.0;
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public final class ZoomPane implements ImageObserver {


    static final int VERTICAL_MARGIN = 10;
    static final int HORIZONTAL_MARGIN =10;
    static final Color borderColor = Color.RED;
    private Image zoomImage;
    private Graphics imgGr;
    private VisibleObject object;
    private int leftOffset = 0;
    private int rightOffset;
    private int width = 0;
    private int height = 0;
    private int topOffset;

    public static ZoomPane panel = new ZoomPane();


    public static ZoomPane getInstance() {
        return panel;
    }

    private ZoomPane() {}

    /**
     *
     * Returns the left offset - distance from the left border to the most left object.
     * @return
     */
    public int getLeftOffset() {
        return leftOffset + HORIZONTAL_MARGIN;
    }

    /**
     *
     * Returns the right offset - distance from the right border to the most right object.
     * @return
     */
    public int getRightOffset() {
        return rightOffset + HORIZONTAL_MARGIN;
    }

    /**
     *
     * Returns the top offset - distance from the top border to the object that is the closest to the top.
     * @return
     */
    public int getTopOffset() {
        return topOffset + VERTICAL_MARGIN;
    }

    /**
     *
     * Starts drawing objects and returns drawn image.
     *
     * @param obj
     * @param clearImage
     * @return
     */
    public Image startZooming(VisibleObject obj, boolean clearImage) {
        object = obj;
        if (clearImage){
            leftOffset = 0;
            rightOffset = 0;
        }
        return initialize(clearImage);
    }

    private Image initialize(boolean clearImage) {
        if (zoomImage != null) {
            zoomImage.flush();
        }

        double scale = ViewState.getInstance().getScale();
        ViewState.getInstance().setScale(1.0);
        object.setZoomRepaint(true);
        object.forceValidation();

        width = object.getWidth();
        height = object.getHeight();
        leftOffset = object.getLeftOffset();
        rightOffset = object.getRightOffset();
        topOffset = object.getTopOffset();
        if (object instanceof Record) {
            int tempWidth = 0;
            int tempLO = 0;
            int tempRO = 0;
            Vector objects = ((ContainerObject)object).getSubObjectsV();
            for (int i = 0; i < objects.size(); i++) {
                VisibleObject obj = (VisibleObject) objects.get(i);
                tempWidth = obj.getWidth();
                tempLO = obj.getLeftOffset();
                tempRO = obj.getRightOffset();
                if (tempWidth/2 + tempLO > width/2) {
                    leftOffset = Math.max(leftOffset, tempWidth/2 + tempLO - width/2);
                }
                if (tempWidth/2 + tempRO > width/2) {
                    rightOffset = Math.max(rightOffset, tempWidth/2 + tempRO - width/2);
                }

                height += obj.getHeight();
            }
        } else if (object instanceof Template) {
            Vector objects = ((ContainerObject)object).getSubObjectsV();
            for (int i = 0; i < objects.size(); i++) {
                VisibleObject obj = (VisibleObject) objects.get(i);
                leftOffset = Math.max(leftOffset, obj.getLeftOffset());
                rightOffset = Math.max(rightOffset, obj.getRightOffset());
            }
        }


        width += leftOffset + rightOffset + 2*HORIZONTAL_MARGIN + 2;
        height += topOffset + 2*VERTICAL_MARGIN;

        if (clearImage || zoomImage == null || imgGr == null) {
            zoomImage = VisualDCT.getInstance().getContentPane().createImage(width,height);
            imgGr = zoomImage.getGraphics();
            imgGr.setColor(Constants.BACKGROUND_COLOR);
            imgGr.fillRect(0,0,width-1, height-1);
            imgGr.setColor(borderColor);
            imgGr.drawRect(0,0,width-1, height-1);
        }

        object.draw(imgGr, false);
        object.setZoomRepaint(false);
        ViewState.getInstance().setScale(scale);
        object.forceValidation();
        return zoomImage;

    }

    /**
     *
     * Returns the width of the image.
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image.
     * @return
     */
    public int getHeight() {
        return height;
    }

    /* (non-Javadoc)
     * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }


}
