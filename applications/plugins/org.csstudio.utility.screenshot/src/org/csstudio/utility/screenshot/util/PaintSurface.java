
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

import org.csstudio.utility.screenshot.ImageBundle;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

/**
 *  @author Markus Moeller
 *
 */
public class PaintSurface
{
    private Canvas paintCanvas = null;
    private ImageBundle imageBundle = null;

    public PaintSurface(Canvas paintCanvas, ImageBundle bundle)
    {
        this.paintCanvas = paintCanvas;
        this.imageBundle = bundle;

        if(imageBundle != null)
        {       
            if(imageBundle.getSectionImage() != null)
            {
                // setCapturedImage(imageBundle.getSectionImage());
                imageBundle.setDisplayedImage(imageBundle.getSectionImage());
            }
            else if(imageBundle.getWindowImage() != null)
            {
                // setCapturedImage(imageBundle.getWindowImage());
                imageBundle.setDisplayedImage(imageBundle.getWindowImage());
            }
            else if(imageBundle.getScreenImage() != null)
            {
                // setCapturedImage(imageBundle.getScreenImage());
                imageBundle.setDisplayedImage(imageBundle.getScreenImage());
            }
        }
        
        paintCanvas.addPaintListener(new PaintListener()
        {
            public void paintControl(PaintEvent event)
            {
                Canvas widget = (Canvas)event.widget;
                Rectangle r = widget.getClientArea();
                
                // float xScale = (float)(imageBundle.getImageWidth() / r.width);
                // float yScale = (float)(imageBundle.getImageHeight() / r.height);

                event.gc.drawImage(imageBundle.getDisplayedImage(), 0, 0, imageBundle.getImageWidth(),
                        imageBundle.getImageHeight(), 0, 0, r.width, r.height);
                
            }            
        });
        
        paintCanvas.addControlListener(new ControlAdapter()
        {
            public void controlResized(ControlEvent event)
            {
                handleResize();
            }           
        });
//        
//        /* Set up the paint canvas scroll bars */
//        ScrollBar horizontal = paintCanvas.getHorizontalBar();
//        horizontal.setVisible(true);
//        horizontal.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                scrollHorizontally((ScrollBar)event.widget);
//            }
//        });
//        ScrollBar vertical = paintCanvas.getVerticalBar();
//        vertical.setVisible(true);
//        vertical.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent event) {
//                scrollVertically((ScrollBar)event.widget);
//            }
//        });
    }

    /**
     * Handles a horizontal scroll event
     * 
     * @param scrollbar the horizontal scroll bar that posted this event
     */
//    public void scrollHorizontally(ScrollBar scrollBar)
//    {
//        if(imageBundle.getDisplayedImage() == null) return;
//        if(imageWidth > visibleWidth)
//        {
//            final int oldOffset = displayFDC.xOffset;
//            final int newOffset = Math.min(scrollBar.getSelection(), imageWidth - visibleWidth);
//            
//            if(oldOffset != newOffset)
//            {
//                paintCanvas.update();
//                displayFDC.xOffset = newOffset;
//                paintCanvas.scroll(Math.max(oldOffset - newOffset, 0), 0, Math.max(newOffset - oldOffset, 0), 0,
//                    visibleWidth, visibleHeight, false);
//            }
//        }
//    }

    /**
     * Handles a vertical scroll event
     * 
     * @param scrollbar the vertical scroll bar that posted this event
     */
//    public void scrollVertically(ScrollBar scrollBar)
//    {
//        if(imageBundle.getDisplayedImage() == null) return;
//        if (imageHeight > visibleHeight)
//        {
//            final int oldOffset = displayFDC.yOffset;
//            final int newOffset = Math.min(scrollBar.getSelection(), imageHeight - visibleHeight);
//            
//            if (oldOffset != newOffset)
//            {
//                paintCanvas.update();
//                displayFDC.yOffset = newOffset;
//                paintCanvas.scroll(0, Math.max(oldOffset - newOffset, 0), 0, Math.max(newOffset - oldOffset, 0),
//                    visibleWidth, visibleHeight, false);
//            }
//        }
//    }

    /**
     * Handles resize events
     */
    private void handleResize()
    {
        paintCanvas.redraw();
//        
//        Rectangle visibleRect = paintCanvas.getClientArea();
//        
//        visibleWidth = visibleRect.width;
//        visibleHeight = visibleRect.height;
//
//        ScrollBar horizontal = paintCanvas.getHorizontalBar();
//        if (horizontal != null)
//        {
//            displayFDC.xOffset = Math.min(horizontal.getSelection(), imageWidth - visibleWidth);
//            
//            if (imageWidth <= visibleWidth)
//            {
//                horizontal.setEnabled(false);
//                horizontal.setSelection(0);
//            }
//            else
//            {
//                horizontal.setEnabled(true);
//                horizontal.setValues(displayFDC.xOffset, 0, imageWidth, visibleWidth,
//                    8, visibleWidth);
//            }
//        }
//
//        ScrollBar vertical = paintCanvas.getVerticalBar();
//        if (vertical != null)
//        {
//            displayFDC.yOffset = Math.min(vertical.getSelection(), imageHeight - visibleHeight);
//            
//            if (imageHeight <= visibleHeight)
//            {
//                vertical.setEnabled(false);
//                vertical.setSelection(0);
//            }
//            else
//            {
//                vertical.setEnabled(true);
//                vertical.setValues(displayFDC.yOffset, 0, imageHeight, visibleHeight,
//                    8, visibleHeight);
//            }
//        }
    }

    public void redraw()
    {
        paintCanvas.redraw();
    }
    
    public void dispose()
    {
        paintCanvas = null;
        imageBundle = null;
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
