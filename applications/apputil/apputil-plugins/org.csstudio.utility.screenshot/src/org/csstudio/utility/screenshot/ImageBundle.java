
/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.screenshot;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

/**
 * The class <code>ImageBundle</code> contains the captured images of the Eclipse window.
 *
 * @author Markus Möller
 *
 */

public class ImageBundle {

    /** The screen image */
    private Image screen = null;

    /** The window image */
    private Image window = null;

    /** The image of the selected section/view */
    private Image section = null;

    /** The image that should be displayed in the screenshot view */
    private Image displayedImage = null;

    /** Width of the displayed image */
    private int imgWidth;

    /** Height of the displayed image */
    private int imgHeight;

    /** Graphics context of the displayed image */
    public GC gc = null;

    /**
     * Standard constructor
     */
    public ImageBundle() {
        imgWidth = 0;
        imgHeight = 0;
    }

    /**
     *
     * @return The screen image
     */

    public Image getScreenImage() {
        return screen;
    }

    /**
     * Sets the screen image.
     *
     * @param i - screen image object
     */

    public void setScreenImage(Image i) {

        if(screen != null) {

            if(!screen.isDisposed()) {

                screen.dispose();
                screen = null;
            }
        }

        screen = new Image(null, i.getImageData());
    }

    /**
     *
     * @return The window image
     */

    public Image getWindowImage() {
        return window;
    }

    /**
     * Sets the window image.
     *
     * @param i - window image object
     */

    public void setWindowImage(Image i) {

        if(window != null) {

            if(!window.isDisposed()) {

                window.dispose();
                window = null;
            }
        }

        window = new Image(null, i.getImageData());
    }

    /**
     *
     * @return The section image
     */

    public Image getSectionImage() {
        return section;
    }

    /**
     * Sets the section image.
     *
     * @param i - section image object
     */

    public void setSectionImage(Image i) {

        if(section != null) {

            if(!section.isDisposed()) {
                section.dispose();

                section = null;
            }
        }

        section = new Image(null, i.getImageData());
    }

    public void setDisplayedImage(Image i) {

        if(displayedImage != null) {

            if(!displayedImage.isDisposed()) {
                displayedImage.dispose();
                displayedImage = null;
                imgWidth = 0;
                imgHeight = 0;
            }
        }

        if(i != null) {
            displayedImage = new Image(null, i.getImageData());
            imgWidth = displayedImage.getBounds().width;
            imgHeight = displayedImage.getBounds().height;
        } else {
            displayedImage = new Image(null, 2, 2);
            imgWidth = displayedImage.getBounds().width;
            imgHeight = displayedImage.getBounds().height;
        }

        if(gc != null) {

            if(!gc.isDisposed()) {
                gc.dispose();
                gc = null;
            }
        }

        gc = new GC(displayedImage);

        if(i == null) {
            gc.drawRectangle(0, 0, 2, 2);
        }
    }

    public int getImageWidth() {
        return imgWidth;
    }

    public int getImageHeight() {
        return imgHeight;
    }

    public Image getDisplayedImage() {
        return displayedImage;
    }

    /**
     * Disposes the images of this bundle.
     */

    public void dispose() {

        if(gc != null) {

            if(!gc.isDisposed()) {
                gc.dispose();
                gc = null;
            }
        }

        if(displayedImage != null) {

            if(!displayedImage.isDisposed()) {
                displayedImage.dispose();
                displayedImage = null;
                imgWidth = 0;
                imgHeight = 0;
            }
        }

        if(screen != null) {

            if(!screen.isDisposed()) {
                screen.dispose();
                screen = null;
            }
        }

        if(window != null) {

            if(!window.isDisposed()) {
                window.dispose();
                window = null;
            }
        }

        if(section != null) {

            if(!section.isDisposed()) {
                section.dispose();
                section = null;
            }
        }
    }
}
