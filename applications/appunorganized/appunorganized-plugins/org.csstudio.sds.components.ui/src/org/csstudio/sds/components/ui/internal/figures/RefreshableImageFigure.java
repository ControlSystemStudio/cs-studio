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
package org.csstudio.sds.components.ui.internal.figures;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.ExecutionService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An image figure.
 *
 * @author jbercic, Xihui Chen
 *
 */

public final class RefreshableImageFigure extends Figure implements IAdaptable {

    private static final Logger LOG = LoggerFactory.getLogger(RefreshableImageFigure.class);

    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;
    /**
     * The {@link IPath} to the image.
     */
    private IPath _path = new Path("");
    /**
     * The image itself.
     */
    private Image staticImage=null;
    /**
     * The width of the image.
     */
    private int _imgWidth=0;
    /**
     * The height of the image.
     */
    private int _imgHeight=0;

    /**
     * The amount of pixels, which are cropped from the top.
     */
    private int _topCrop=0;
    /**
     * The amount of pixels, which are cropped from the bottom.
     */
    private int _bottomCrop=0;
    /**
     * The amount of pixels, which are cropped from the left.
     */
    private int _leftCrop=0;
    /**
     * The amount of pixels, which are cropped from the right.
     */
    private int _rightCrop=0;
    /**
     * The stretch state for the image.
     */
    private boolean _stretch=true;

    /**
     * If this is an animated image
     */
    private boolean animated = false;

    private Image offScreenImage;

    private GC offScreenImageGC;
    /**
     * The imaged data array for animated image
     */
    private ImageData[] imageDataArray;

    private ImageData[] originalImageDataArray;

    /**
     * The index in image data array
     */
    private int showIndex = 0;

    /**
     * The animated image is being refreshed by editpart
     */
    private boolean refreshing = false;

    private boolean stopedAnimation = false;

    private boolean loadingError = false;

    private final boolean useGIFBackground = false;

    private final ImageLoader loader = new ImageLoader();

    private ImageData originalStaticImageData = null;



    private int repeatCount;
    private int animationIndex;
    private long lastUpdateTime;
    private long interval_ms;
    private ScheduledFuture<?> scheduledFuture;
    private CrossedOutAdapter _crossedOutAdapter;
    private RhombusAdapter _rhombusAdapter;

    public RefreshableImageFigure() {
    }

    /**
     * We want to have local coordinates here.
     * @return True if here should used local coordinates
     */
    protected boolean useLocalCoordinates() {
        return true;
    }

    /**
     * The main drawing routine.
     * @param graphics The {@link Graphics} to use
     */
    @Override
    public void paintFigure(final Graphics graphics) {
        Rectangle bound=getBounds().getCopy();
        bound.crop(this.getInsets());
        if(loadingError) {
                if (staticImage!=null) {
                    staticImage.dispose();
                }
                staticImage=null;
                if (!_path.isEmpty()) {
                    /*Font f=gfx.getFont();
                    FontData fd=f.getFontData()[0];

                    if (bound.width>=20*30) {
                        fd.setHeight(30);
                    } else {
                        if (bound.width/20+1<7) {
                            fd.setHeight(7);
                        } else {
                            fd.setHeight(bound.width/20+1);
                        }
                    }
                    f=new Font(Display.getDefault(),fd);
                    gfx.setFont(f);*/
                    graphics.setBackgroundColor(getBackgroundColor());
                    graphics.setForegroundColor(getForegroundColor());
                    graphics.fillRectangle(bound);
                    graphics.translate(bound.getLocation());
                    TextPainter.drawText(graphics,"ERROR in loading image\n"+_path,bound.width/2,bound.height/2,TextPainter.CENTER);
                    //f.dispose();
                }
                return;
        }

        //create static image
        if((staticImage == null) && (originalStaticImageData !=null)){
                if (_stretch) {
                    staticImage=new Image(Display.getDefault(),
                            originalStaticImageData.scaledTo(bound.width+_leftCrop+_rightCrop,
                                    bound.height+_topCrop+_bottomCrop));
                    if(animated) {
                        imageDataArray = new ImageData[originalImageDataArray.length];
                        double widthScaleRatio = (double)(bound.width+_leftCrop+_rightCrop) / (double)originalStaticImageData.width;
                        double heightScaleRatio = (double)(bound.height+_topCrop+_bottomCrop) / (double)originalStaticImageData.height;
                        for (int i = 0; i < originalImageDataArray.length; i++){
                            int scaleWidth = (int) (originalImageDataArray[i].width * widthScaleRatio);
                            int scaleHeight = (int) (originalImageDataArray[i].height * heightScaleRatio);
                            int x = (int) (originalImageDataArray[i].x * widthScaleRatio);
                            int y = (int) (originalImageDataArray[i].y * heightScaleRatio);

                            imageDataArray[i] = originalImageDataArray[i].scaledTo(scaleWidth, scaleHeight);
                            imageDataArray[i].x = x;
                            imageDataArray[i].y = y;
                        }

                    }
                } else {
                    staticImage=new Image(Display.getDefault(),originalStaticImageData);
                    if(animated) {
                        imageDataArray = originalImageDataArray;
                    }
                }
                _imgWidth=staticImage.getBounds().width;
                _imgHeight=staticImage.getBounds().height;

                if(animated) {
                    if ((offScreenImage != null) && !offScreenImage.isDisposed()) {
                        offScreenImage.dispose();
                    }
                    offScreenImage = new Image(Display.getDefault(), _imgWidth,
                            _imgHeight);

                    if ((offScreenImageGC != null) && !offScreenImageGC.isDisposed()) {
                        offScreenImageGC.dispose();
                    }
                    offScreenImageGC = new GC(offScreenImage);
                }
            }

        //avoid negative number
        _leftCrop = _leftCrop > _imgWidth ? 0 : _leftCrop;
        _topCrop = _topCrop > _imgWidth ? 0 : _topCrop;
        int cropedWidth = (_imgWidth-_leftCrop-_rightCrop) > 0 ?
                (_imgWidth-_leftCrop-_rightCrop) : _imgWidth;
        int cropedHeight = (_imgHeight-_topCrop-_bottomCrop) > 0 ?
                (_imgHeight-_topCrop-_bottomCrop) : _imgHeight;

        if(animated && refreshing) {   //draw refreshing image
            ImageData imageData = imageDataArray[showIndex];
            Image refresh_image = new Image(Display.getDefault(), imageData);
            switch (imageData.disposalMethod) {
            case SWT.DM_FILL_BACKGROUND:
                /* Fill with the background color before drawing. */
                Color bgColor = null;
                if (useGIFBackground  && (loader.backgroundPixel != -1)) {
                    bgColor = new Color(Display.getDefault(), imageData.palette.getRGB(loader.backgroundPixel));
                }
                offScreenImageGC.setBackground(bgColor != null ? bgColor : getBackgroundColor());
                offScreenImageGC.fillRectangle(
                        imageData.x, imageData.y, imageData.width, imageData.height);
                if (bgColor != null) {
                    bgColor.dispose();
                }
                break;
            case SWT.DM_FILL_PREVIOUS:
                /* Restore the previous image before drawing. */
                Image startImage = new Image(Display.getDefault(), imageDataArray[0]);
                offScreenImageGC.drawImage(
                    startImage,
                    0,
                    0,
                    imageData.width,
                    imageData.height,
                    imageData.x,
                    imageData.y,
                    imageData.width,
                    imageData.height);
                startImage.dispose();
                break;
            }

            offScreenImageGC.drawImage(refresh_image,
                    0,
                    0,
                    imageData.width,
                    imageData.height,
                    imageData.x,
                    imageData.y,
                    imageData.width,
                    imageData.height);

            graphics.drawImage(offScreenImage, _leftCrop,_topCrop,
                    cropedWidth,cropedHeight,
                    bound.x,bound.y,
                    cropedWidth,cropedHeight);
            refresh_image.dispose();
        } else { // draw static image
            if(animated && stopedAnimation && (offScreenImage != null) && (showIndex!=0)){
                graphics.drawImage(offScreenImage, _leftCrop,_topCrop,
                    cropedWidth,cropedHeight,
                    bound.x,bound.y,
                    cropedWidth,cropedHeight);
            } else {
                graphics.drawImage(staticImage,
                        _leftCrop,_topCrop,
                        cropedWidth,cropedHeight,
                        bound.x,bound.y,
                        cropedWidth,cropedHeight);
            }
        }
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);

    }

    /**
     * Resizes the image.
     */
    public void resizeImage() {
        if ((staticImage!=null) && !staticImage.isDisposed()) {
            staticImage.dispose();
        }
        staticImage=null;
        if(refreshing && animated){
            stopAnimation();
            startAnimation();
        }
    }

    /**
     * Sets the path to the image.
     * @param newval The path to the image
     */
    public void setFilePath(final IPath newval) {
        loadingError = false;
        _path=newval;
        if ((staticImage!=null)  && !staticImage.isDisposed()) {
            staticImage.dispose();
        }
        staticImage=null;
        try {
            if ((staticImage==null) && !_path.isEmpty()) {
//                _path
                String currentPath = _path.toOSString();
                IPath fullPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
                try {
                    loadImage(currentPath);
                } catch (Exception e) {
                    try {
                        IPath append = fullPath.append(_path);
                        IFile[] findFilesForLocation = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(append);
                        currentPath = findFilesForLocation[0].getLocation().toOSString();
                        loadImage(currentPath);
                    } catch (Exception ex) {
                        String[] segments = _path.segments();
                        String projectName = segments[0];
                        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                        int index = 1;
                        IFolder folder = null;
                        currentPath = null;
                        while (index < segments.length-1) {
                            folder = project.getFolder(segments[index]);
                            if (currentPath!=null) {
                                currentPath = currentPath+ IPath.SEPARATOR +segments[index];
                            }
                            if (folder.isLinked()) {
                                currentPath = folder.getLocation().toString();
                            }
                            index++;
                        }
                        currentPath = currentPath + IPath.SEPARATOR + segments[segments.length-1];
                        loadImage(currentPath);
                    }
                }
            }
        } catch (Exception e) {
            loadingError = true;
            LOG.error("ERROR in loading image\n"+_path, e);
        }
        if(animated){
            stopAnimation();
            startAnimation();
        }
    }

    private void loadImage(final String currentPath) {
        Image temp = null;
        try {
            temp=new Image(Display.getDefault(),currentPath);
            originalStaticImageData = temp.getImageData();
        }finally {
            if ((temp != null) && !temp.isDisposed()) {
                temp.dispose();
            }
        }
        originalImageDataArray = loader.load(currentPath);
        animated = (originalImageDataArray.length > 1);
    }

    /**
     * Returns the path to the image.
     * @return The path to the image
     */
    public IPath getFilePath() {
        return _path;
    }

    /**
     * Sets the amount of pixels, which are cropped from the top.
     * @param newval The amount of pixels
     */
    public void setTopCrop(final int newval) {
        _topCrop=newval;
        resizeImage();
    }

    /**
     * Returns the amount of pixels, which are cropped from the top.
     * @return The amount of pixels
     */
    public int getTopCrop() {
        return _topCrop;
    }

    /**
     * Sets the amount of pixels, which are cropped from the bottom.
     * @param newval The amount of pixels
     */
    public void setBottomCrop(final int newval) {
        _bottomCrop=newval;
        resizeImage();
    }

    /**
     * Returns the amount of pixels, which are cropped from the top.
     * @return The amount of pixels
     */
    public int getBottomCrop() {
        return _bottomCrop;
    }

    /**
     * Sets the amount of pixels, which are cropped from the left.
     * @param newval The amount of pixels
     */
    public void setLeftCrop(final int newval) {
        _leftCrop=newval;
        resizeImage();
    }

    /**
     * Returns the amount of pixels, which are cropped from the top.
     * @return The amount of pixels
     */
    public int getLeftCrop() {
        return _leftCrop;
    }

    /**
     * Sets the amount of pixels, which are cropped from the right.
     * @param newval The amount of pixels
     */
    public void setRightCrop(final int newval) {
        _rightCrop=newval;
        resizeImage();
    }

    /**
     * Returns the amount of pixels, which are cropped from the top.
     * @return The amount of pixels
     */
    public int getRightCrop() {
        return _rightCrop;
    }

    /**
     * Sets the stretch state for the image.
     * @param newval The new state (true, if it should be stretched, false otherwise)
     */
    public void setStretch(final boolean newval) {

        _stretch=newval;
        if ((staticImage!=null)  && !staticImage.isDisposed()) {
            staticImage.dispose();
        }
        staticImage=null;
        if(refreshing && animated){
            stopAnimation();
            startAnimation();
        }
    }
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        if(visible) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    /**
     * Returns the stretch state for the image.
     * @return True, if it should be stretched, false otherwise
     */
    public boolean getStretch() {
        return _stretch;
    }



    /**
     * @param showIndex the showIndex to set
     */
    public void setShowIndex(final int showIndex) {
        this.showIndex = showIndex;
        repaint();
    }


    /**
     * Automatically make the widget bounds be adjusted to the size of the static image
     * @param autoSize
     */
    public void setAutoSize(final boolean autoSize){
        if(!_stretch && autoSize) {
            resizeImage();
        }
    }

    /**
     * @return the auto sized widget dimension according to the static imageSize
     */
    public Dimension getAutoSizedDimension() {
        if(originalStaticImageData != null) {
            return new Dimension(originalStaticImageData.width + getInsets().getWidth() - _leftCrop - _rightCrop,
                        originalStaticImageData.height + getInsets().getHeight() - _topCrop - _bottomCrop);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if(_borderAdapter==null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }


    /**
     * start the animation if the image is an animated GIF image.
     */
    public void startAnimation(){
        if(animated && !refreshing && !stopedAnimation) {
            repeatCount = loader.repeatCount;
            //animationIndex = 0;
            lastUpdateTime=0;
            interval_ms =0;
            refreshing = true;
            Runnable animationTask = new Runnable() {
                public void run() {
                    new CheckedUiRunnable(){
                        @Override
                        protected void doRunInUi() {
                            if((loader.repeatCount ==0) || (repeatCount >0)) {
                                long currentTime = System.currentTimeMillis();
                                //use Math.abs() to ensure that the system time adjust won't cause problem
                                if(Math.abs(currentTime - lastUpdateTime) >= interval_ms) {
                                    setShowIndex(animationIndex);
                                    lastUpdateTime = currentTime;
                                    int ms = originalImageDataArray[animationIndex].delayTime * 10;
                                    animationIndex = (animationIndex + 1) % originalImageDataArray.length;
                                    if (ms < 20) {
                                        ms += 30;
                                    }
                                    if (ms < 30) {
                                        ms += 10;
                                    }
                                    interval_ms = ms;
                                    /* If we have just drawn the last image, decrement the repeat count and start again. */
                                    if(animationIndex == originalImageDataArray.length -1) {
                                        repeatCount--;
                                    }
                                }
                            }
                        }
                    };
                }
            };

            if(scheduledFuture !=null){
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
            scheduledFuture = ExecutionService.getInstance().
                                getScheduledExecutorService().scheduleAtFixedRate(
                                animationTask, 100, 10, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * stop the animation if the image is an animated GIF image.
     */
    public void stopAnimation(){

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
        refreshing = false;
    }

    public void setStopAnimation(final boolean stop){
        stopedAnimation = stop;
        if(stop){
            stopAnimation();
        }else if(animated){
            startAnimation();
        }
    }

    /**
     * dispose the resources used by this figure
     */
    public void dispose(){
        if ((offScreenImage != null) && !offScreenImage.isDisposed()) {
            offScreenImage.dispose();
            offScreenImage = null;
        }

        if ((offScreenImageGC != null) && !offScreenImageGC.isDisposed()) {
            offScreenImageGC.dispose();
            offScreenImage = null;
        }

        if ((staticImage != null) && !staticImage.isDisposed()) {
            staticImage.dispose();
            staticImage = null;
        }
    }

}
