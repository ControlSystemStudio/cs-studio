
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
 *
 */

package org.csstudio.utility.screenshot.util;

import java.awt.geom.AffineTransform;
import org.csstudio.utility.screenshot.ImageBundle;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ScrollBar;

/**
 *  Some code is borrowed from the basic image viewer.
 *  http://www.eclipse.org/articles/Article-Image-Viewer/Image_viewer.html
 *  @author Markus Moeller
 *  @author Chengdong Li (some borrowed parts)
 */
public class PaintSurface
{
    /* zooming rates in x and y direction are equal.*/
    final float ZOOMIN_RATE = 1.1f; /* zoomin rate */
    final float ZOOMOUT_RATE = 0.9f; /* zoomout rate */

    private Canvas paintCanvas = null;
    private ImageBundle imageBundle = null;
    private AffineTransform transform = new AffineTransform();
    private Image screenImage = null;

    public PaintSurface(Canvas paintCanvas, ImageBundle bundle)
    {
        this.paintCanvas = paintCanvas;
        this.imageBundle = bundle;

        if(imageBundle != null)
        {
            if(imageBundle.getSectionImage() != null)
            {
                imageBundle.setDisplayedImage(imageBundle.getSectionImage());
            }
            else if(imageBundle.getWindowImage() != null)
            {
                imageBundle.setDisplayedImage(imageBundle.getWindowImage());
            }
            else if(imageBundle.getScreenImage() != null)
            {
                imageBundle.setDisplayedImage(imageBundle.getScreenImage());
            }
        }

        paintCanvas.addPaintListener(new PaintListener()
        {
            @Override
            public void paintControl(PaintEvent event)
            {
                Rectangle clientRect = null;

                if(imageBundle.getDisplayedImage() != null)
                {
                    Canvas widget = (Canvas)event.widget;
                    clientRect = widget.getClientArea();

                    Rectangle imageRect=SWT2Dutil.inverseTransformRect(transform, clientRect);
                    int gap = 2; /* find a better start point to render. */
                    imageRect.x -= gap; imageRect.y -= gap;
                    imageRect.width += 2 * gap; imageRect.height += 2 * gap;

                    Rectangle imageBound = imageBundle.getDisplayedImage().getBounds();
                    imageRect = imageRect.intersection(imageBound);
                    Rectangle destRect = SWT2Dutil.transformRect(transform, imageRect);

                    if(screenImage != null){screenImage.dispose();}
                    screenImage = new Image(widget.getDisplay(), clientRect.width, clientRect.height);
                    GC newGC = new GC(screenImage);
                    newGC.setClipping(clientRect);
                    newGC.drawImage( imageBundle.getDisplayedImage(),
                            imageRect.x,
                            imageRect.y,
                            imageRect.width,
                            imageRect.height,
                            destRect.x,
                            destRect.y,
                            destRect.width,
                            destRect.height);
                    newGC.dispose();

                    event.gc.drawImage(screenImage, 0, 0);
                }
                else
                {
                    event.gc.setClipping(clientRect);
                    event.gc.fillRectangle(clientRect);
                    initScrollBars();
                }
            }
        });

        paintCanvas.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent event)
            {
                syncScrollBars();
            }
        });

        initScrollBars();
    }

    /* Initalize the scrollbar and register listeners. */
    private void initScrollBars() {
        ScrollBar horizontal = paintCanvas.getHorizontalBar();
        horizontal.setEnabled(false);
        horizontal.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                scrollHorizontally((ScrollBar) event.widget);
            }
        });

        ScrollBar vertical = paintCanvas.getVerticalBar();
        vertical.setEnabled(false);
        vertical.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                scrollVertically((ScrollBar) event.widget);
            }
        });
    }

    /**
     * Synchronize the scrollbar with the image. If the transform is out
     * of range, it will correct it. This function considers only following
     * factors :<b> transform, image size, client area</b>.
     */
    public void syncScrollBars()
    {
        if(imageBundle.getDisplayedImage() == null)
        {
            redraw();
            return;
        }

        AffineTransform af = transform;
        double sx = af.getScaleX(), sy = af.getScaleY();
        double tx = af.getTranslateX(), ty = af.getTranslateY();
        if (tx > 0) tx = 0;
        if (ty > 0) ty = 0;

        ScrollBar horizontal = paintCanvas.getHorizontalBar();
        horizontal.setIncrement((int) (paintCanvas.getClientArea().width / 100));
        horizontal.setPageIncrement(paintCanvas.getClientArea().width);
        Rectangle imageBound = imageBundle.getDisplayedImage().getBounds();
        int cw = paintCanvas.getClientArea().width, ch = paintCanvas.getClientArea().height;
        if (imageBound.width * sx > cw) { /* image is wider than client area */
            horizontal.setMaximum((int) (imageBound.width * sx));
            horizontal.setEnabled(true);
            if (((int) - tx) > horizontal.getMaximum() - cw)
                tx = -horizontal.getMaximum() + cw;
        } else { /* image is narrower than client area */
            horizontal.setEnabled(false);
            tx = (cw - imageBound.width * sx) / 2; //center if too small.
        }
        horizontal.setSelection((int) (-tx));
        horizontal.setThumb((int) (paintCanvas.getClientArea().width));

        ScrollBar vertical = paintCanvas.getVerticalBar();
        vertical.setIncrement((int) (paintCanvas.getClientArea().height / 100));
        vertical.setPageIncrement((int) (paintCanvas.getClientArea().height));
        if (imageBound.height * sy > ch) { /* image is higher than client area */
            vertical.setMaximum((int) (imageBound.height * sy));
            vertical.setEnabled(true);
            if (((int) - ty) > vertical.getMaximum() - ch)
                ty = -vertical.getMaximum() + ch;
        } else { /* image is less higher than client area */
            vertical.setEnabled(false);
            ty = (ch - imageBound.height * sy) / 2; //center if too small.
        }
        vertical.setSelection((int) (-ty));
        vertical.setThumb((int) (paintCanvas.getClientArea().height));

        /* update transform. */
        af = AffineTransform.getScaleInstance(sx, sy);
        af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
        transform = af;

        paintCanvas.redraw();
    }

    /* Scroll horizontally */
    private void scrollHorizontally(ScrollBar scrollBar) {
        if (imageBundle.getDisplayedImage() == null)
            return;

        AffineTransform af = transform;
        double tx = af.getTranslateX();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
        transform = af;
        syncScrollBars();
    }

    /* Scroll vertically */
    private void scrollVertically(ScrollBar scrollBar) {
        if (imageBundle.getDisplayedImage() == null)
            return;

        AffineTransform af = transform;
        double ty = af.getTranslateY();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
        transform = af;
        syncScrollBars();
    }

    public void redraw()
    {
        paintCanvas.redraw();
    }

    public void dispose()
    {
        paintCanvas = null;
        imageBundle = null;

        if(screenImage != null)
        {
            if(!screenImage.isDisposed())
            {
                screenImage.dispose();
            }

            screenImage = null;
        }
    }

    /**
     * Perform a zooming operation centered on the given point
     * (dx, dy) and using the given scale factor.
     * The given AffineTransform instance is preconcatenated.
     * @param dx center x
     * @param dy center y
     * @param scale zoom rate
     * @param af original affinetransform
     */
    public void centerZoom(
        double dx,
        double dy,
        double scale,
        AffineTransform af) {
        af.preConcatenate(AffineTransform.getTranslateInstance(-dx, -dy));
        af.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
        af.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        transform = af;
        syncScrollBars();
    }

    /**
     * Fit the image onto the canvas
     */
    public void fitCanvas() {
        if (imageBundle.getDisplayedImage() == null)
            return;
        Rectangle imageBound = imageBundle.getDisplayedImage().getBounds();
        Rectangle destRect = paintCanvas.getClientArea();
        double sx = (double) destRect.width / (double) imageBound.width;
        double sy = (double) destRect.height / (double) imageBound.height;
        double s = Math.min(sx, sy);
        double dx = 0.5 * destRect.width;
        double dy = 0.5 * destRect.height;
        centerZoom(dx, dy, s, new AffineTransform());
    }

    /**
     * Zoom in around the center of client Area.
     */
    public void zoomIn() {
        if (imageBundle.getDisplayedImage() == null)
            return;
        Rectangle rect = paintCanvas.getClientArea();
        int w = rect.width, h = rect.height;
        double dx = ((double) w) / 2;
        double dy = ((double) h) / 2;
        centerZoom(dx, dy, ZOOMIN_RATE, transform);
    }

    /**
     * Zoom out around the center of client Area.
     */
    public void zoomOut() {
        if (imageBundle.getDisplayedImage() == null)
            return;
        Rectangle rect = paintCanvas.getClientArea();
        int w = rect.width, h = rect.height;
        double dx = ((double) w) / 2;
        double dy = ((double) h) / 2;
        centerZoom(dx, dy, ZOOMOUT_RATE, transform);
    }

    /**
     * Show the image with the original size
     */
    public void showOriginal() {
        if (imageBundle.getDisplayedImage() == null)
            return;
        transform = new AffineTransform();
        syncScrollBars();
    }

    public Image getImage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Image getCapturedImage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setCapturedImage(Image i)
    {
        // TODO Auto-generated method stub

    }

    public ImageBundle getAllImages()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
