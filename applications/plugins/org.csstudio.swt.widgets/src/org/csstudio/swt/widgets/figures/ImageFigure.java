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
package org.csstudio.swt.widgets.figures;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.utils.SVGUtils;
import org.apache.batik.utils.SimpleImageTranscoder;
import org.csstudio.java.thread.ExecutionService;
import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IImageLoadedListener;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ImageUtils;
import org.csstudio.swt.widgets.util.PermutationMatrix;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.csstudio.swt.widgets.util.SingleSourceHelper;
import org.csstudio.swt.widgets.util.TextPainter;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

/**
 * An image figure.
 * 
 * @author jbercic, Xihui Chen
 * 
 */

public final class ImageFigure extends Figure implements Introspectable {

	/**
	 * The {@link IPath} to the image.
	 */
	private IPath filePath = new Path("");
	/**
	 * The image itself.
	 */
	private Image staticImage = null;
	/**
	 * The width of the image.
	 */
	private int imgWidth = 0;
	/**
	 * The height of the image.
	 */
	private int imgHeight = 0;

	/**
	 * The amount of pixels, which are cropped from the top.
	 */
	private int topCrop = 0;

	/**
	 * The amount of pixels, which are cropped from the bottom.
	 */
	private int bottomCrop = 0;
	/**
	 * The amount of pixels, which are cropped from the left.
	 */
	private int leftCrop = 0;
	/**
	 * The amount of pixels, which are cropped from the right.
	 */
	private int rightCrop = 0;
	/**
	 * The stretch state for the image.
	 */
	private boolean stretch = true;
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

	private boolean animationDisabled = false;

	private boolean loadingError = false;

	private ImageLoader loader = new ImageLoader();

	// private boolean useGIFBackground = false;

	private ImageData staticImageData = null;
	private ImageData originalStaticImageData = null;

	private int repeatCount;

	private int animationIndex = 0;
	private long lastUpdateTime;
	private long interval_ms;
	private ScheduledFuture<?> scheduledFuture;

	private boolean startAnimationRequested = false;
	
	private volatile boolean loadingImage;
	private IImageLoadedListener imageLoadedListener;

	private PermutationMatrix oldPermutationMatrix = null;
	private PermutationMatrix permutationMatrix = PermutationMatrix
			.generateIdentityMatrix();
	
	// SVG attributes
	private boolean workingWithSVG = false;
	private boolean failedToLoadDocument;
	private SimpleImageTranscoder transcoder;
	private Document svgDocument;

	private double scale = 1.0;

	/**
	 * dispose the resources used by this figure
	 */
	public synchronized void dispose() {
		stopAnimation();
		if (offScreenImage != null && !offScreenImage.isDisposed()) {
			offScreenImage.dispose();
			offScreenImage = null;
		}

		if (offScreenImageGC != null && !offScreenImageGC.isDisposed()) {
			offScreenImageGC.dispose();
			offScreenImage = null;
		}

		if (staticImage != null && !staticImage.isDisposed()) {
			staticImage.dispose();
			staticImage = null;
		}
	}

	/**
	 * @return the auto sized widget dimension according to the static imageSize
	 */
	public synchronized Dimension getAutoSizedDimension() {
		if (originalStaticImageData != null) {
			ImageData imageData = (staticImageData == null) ? originalStaticImageData : staticImageData;
			return new Dimension(imageData.width + getInsets().getWidth()
					- leftCrop - rightCrop, imageData.height
					+ getInsets().getHeight() - topCrop - bottomCrop);
		}
		else return null;
	}

	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * 
	 * @return The amount of pixels
	 */
	public synchronized int getBottomCrop() {
		return bottomCrop;
	}

	/**
	 * Returns the path to the image.
	 * 
	 * @return The path to the image
	 */
	public synchronized IPath getFilePath() {
		return filePath;
	}

	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * 
	 * @return The amount of pixels
	 */
	public synchronized int getLeftCrop() {
		return leftCrop;
	}

	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * 
	 * @return The amount of pixels
	 */
	public synchronized int getRightCrop() {
		return rightCrop;
	}

	/**
	 * Returns the stretch state for the image.
	 * 
	 * @return True, if it should be stretched, false otherwise
	 */
	public synchronized boolean getStretch() {
		return stretch;
	}

	/**
	 * Returns the amount of pixels, which are cropped from the top.
	 * 
	 * @return The amount of pixels
	 */
	public synchronized int getTopCrop() {
		return topCrop;
	}

	/**
	 * @return the animationDisabled
	 */
	public synchronized boolean isAnimationDisabled() {
		return animationDisabled;
	}

	public boolean isLoadingImage() {
		return loadingImage;
	}

	public void setImageLoadedListener(IImageLoadedListener listener) {
		this.imageLoadedListener = listener;
	}

	private synchronized void fireImageLoadedListeners() {
		imageLoadedListener.imageLoaded(this);
	}
	
	private synchronized void loadImage(IPath path,
			IJobErrorHandler errorHandler) {		
		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {

			@Override
			public void runWithInputStream(InputStream stream) {
				synchronized (ImageFigure.this) {
					Image temp = null;
					try {
						temp = new Image(null, stream);
						originalStaticImageData = temp.getImageData();
						imgWidth = originalStaticImageData.width;
						imgHeight = originalStaticImageData.height;						
					} finally {
						if (temp != null && !temp.isDisposed())
							temp.dispose();
						try {
							stream.close();
						} catch (IOException e) {						
						}
					}
				}
			}
		};
		ResourceUtil.pathToInputStreamInJob(filePath, uiTask, "Loading Image...",
				errorHandler);
		uiTask = new AbstractInputStreamRunnable() {

			@Override
			public void runWithInputStream(InputStream stream) {
				synchronized (ImageFigure.this) {
					originalImageDataArray = loader.load(stream);
					try {
						stream.close();
					} catch (IOException e) {
					}
					if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
						animated = false;
					else
						animated = (originalImageDataArray.length > 1);
					loadingImage = false;
					repaint();
				}
			}
		};
		ResourceUtil.pathToInputStreamInJob(filePath, uiTask, "Loading Image...",
				errorHandler);

	}

	/**
	 *
	 */
	@SuppressWarnings("nls")
	private synchronized void loadImageFromFile() {
		// load image from file
		if (staticImage == null && !filePath.isEmpty()) {
			loadingImage = true;
			showIndex =0;
			// loading by stream
			loadImage(filePath, new IJobErrorHandler() {

				public void handleError(Exception exception) {
					loadingError = true;
					loadingImage = false;
					Activator.getLogger().log(Level.WARNING,
							"ERROR in loading image " + filePath, exception);
				}
			});
		}

	}

	/**
	 * The main drawing routine.
	 * 
	 * @param gfx
	 *            The {@link Graphics} to use
	 */
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void paintFigure(final Graphics gfx) {
		if(loadingImage)
			return;
		Rectangle bound = getBounds().getCopy();
		bound.crop(this.getInsets());
		if(bound.width<=0 || bound.height<=0)
			return;
		if (loadingError || failedToLoadDocument) {
			if (staticImage != null) {
				staticImage.dispose();
			}
			staticImage = null;
			if (!filePath.isEmpty()) {
				/*
				 * Font f=gfx.getFont(); FontData fd=f.getFontData()[0];
				 * 
				 * if (bound.width>=20*30) { fd.setHeight(30); } else { if
				 * (bound.width/20+1<7) { fd.setHeight(7); } else {
				 * fd.setHeight(bound.width/20+1); } } f=new
				 * Font(Display.getDefault(),fd); gfx.setFont(f);
				 */
				gfx.setBackgroundColor(getBackgroundColor());
				gfx.setForegroundColor(getForegroundColor());
				gfx.fillRectangle(bound);
				gfx.translate(bound.getLocation());
				TextPainter.drawText(gfx,
						"ERROR in loading image\n" + filePath, bound.width / 2,
						bound.height / 2, TextPainter.CENTER);
				// f.dispose();
			}
			return;
		}

		// create static image
		if (staticImage == null && originalStaticImageData != null
				&& !workingWithSVG) {
			ImageData imageData = (staticImageData == null) ? originalStaticImageData : staticImageData;
			// Apply rotation / flip
			if (permutationMatrix != null
					&& !permutationMatrix.equals(oldPermutationMatrix)
					&& !permutationMatrix.equals(PermutationMatrix.generateIdentityMatrix()) 
					&& !animated) {
				staticImageData = ImageUtils.applyMatrix(originalStaticImageData, permutationMatrix);
				imageData = staticImageData;
			}
			if (stretch) {
				staticImage = new Image(Display.getDefault(),
						imageData.scaledTo(bound.width + leftCrop + rightCrop,
								bound.height + topCrop + bottomCrop));
				if (animated) {
					imageDataArray = new ImageData[originalImageDataArray.length];
					double widthScaleRatio = (double) (bound.width + leftCrop + rightCrop)
							/ (double) originalStaticImageData.width;
					double heightScaleRatio = (double) (bound.height + topCrop + bottomCrop)
							/ (double) originalStaticImageData.height;
					for (int i = 0; i < originalImageDataArray.length; i++) {
						int scaleWidth = (int) (originalImageDataArray[i].width * widthScaleRatio);
						int scaleHeight = (int) (originalImageDataArray[i].height * heightScaleRatio);
						int x = (int) (originalImageDataArray[i].x * widthScaleRatio);
						int y = (int) (originalImageDataArray[i].y * heightScaleRatio);

						imageDataArray[i] = originalImageDataArray[i].scaledTo(
								scaleWidth, scaleHeight);
						imageDataArray[i].x = x;
						imageDataArray[i].y = y;
					}

				}
			} else {
				staticImage = new Image(Display.getDefault(), imageData);
				if (animated)
					imageDataArray = originalImageDataArray;
			}
			imgWidth = staticImage.getBounds().width;
			imgHeight = staticImage.getBounds().height;

			if (animated) {
				if (offScreenImage != null && !offScreenImage.isDisposed())
					offScreenImage.dispose();
				offScreenImage = new Image(Display.getDefault(), imgWidth,
						imgHeight);

				if (offScreenImageGC != null && !offScreenImageGC.isDisposed())
					offScreenImageGC.dispose();
				offScreenImageGC = SingleSourceHelper.getImageGC(offScreenImage);// new GC(offScreenImage);
			}
		}

		// avoid negative number
		leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
		topCrop = topCrop > imgWidth ? 0 : topCrop;
		int cropedWidth = (imgWidth - leftCrop - rightCrop) > 0 ? (imgWidth
				- leftCrop - rightCrop) : imgWidth;
		int cropedHeight = (imgHeight - topCrop - bottomCrop) > 0 ? (imgHeight
				- topCrop - bottomCrop) : imgHeight;

		if (leftCrop + cropedWidth > imgWidth
				|| topCrop + cropedHeight > imgHeight)
			return;

		if (workingWithSVG) { // draw refreshing SVG image
			double newScale = gfx.getAbsoluteScale();
			if (newScale != scale) {
				this.scale = newScale;
				resizeImage();
			}
			if (staticImage == null) {
				// Load document if do not exist
				Document document = getDocument();
				if (document == null)
					return;
				transcoder.setTransformMatrix(permutationMatrix.getMatrix());

				// Scale image
				java.awt.Dimension dims = transcoder.getDocumentSize();
				int imgWidth = dims.width;
				int imgHeight = dims.height;
				if (stretch) {
					Rectangle bounds = getBounds().getCopy();
					if (!bounds.equals(0, 0, 0, 0)) {
						imgWidth = bounds.width;
						imgHeight = bounds.height;
					}
				}
				imgWidth = (int) Math.round(scale * (imgWidth + leftCrop + rightCrop));
				imgHeight = (int) Math.round(scale * (imgHeight + bottomCrop + topCrop));
				transcoder.setCanvasSize(imgWidth, imgHeight);

				BufferedImage awtImage = transcoder.getBufferedImage();
				if (awtImage != null)
					staticImageData = SVGUtils.toSWT(Display.getCurrent(),
							awtImage);
				if (staticImageData != null)
					staticImage = new Image(Display.getDefault(),
							staticImageData);
			}
			// Calculate areas
			imgWidth = staticImage.getBounds().width;
			imgHeight = staticImage.getBounds().height;
			cropedWidth = imgWidth - (int) Math.round(scale * (leftCrop + rightCrop));
			cropedHeight = imgHeight - (int) Math.round(scale * (bottomCrop + topCrop));
			Rectangle srcArea = new Rectangle(leftCrop, topCrop, cropedWidth, cropedHeight);
			Rectangle destArea = new Rectangle(bounds.x, bounds.y,
					(int) Math.round(cropedWidth / scale),
					(int) Math.round(cropedHeight / scale));

			// Draw graphic image
			if (staticImage != null)
				gfx.drawImage(staticImage, srcArea, destArea);
			return;
		}

		if (animated) { // draw refreshing image
			if (startAnimationRequested)
				realStartAnimation();
			ImageData imageData = imageDataArray[showIndex];
			Image refresh_image = new Image(Display.getDefault(), imageData);
			switch (imageData.disposalMethod) {
			case SWT.DM_FILL_BACKGROUND:
				/* Fill with the background color before drawing. */
				offScreenImageGC.setBackground(getBackgroundColor());
				offScreenImageGC.fillRectangle(imageData.x, imageData.y,
						imageData.width, imageData.height);
				break;
			case SWT.DM_FILL_PREVIOUS:
				/* Restore the previous image before drawing. */
				Image startImage = new Image(Display.getDefault(),
						imageDataArray[0]);
				offScreenImageGC.drawImage(startImage, 0, 0, imageData.width,
						imageData.height, imageData.x, imageData.y,
						imageData.width, imageData.height);
				startImage.dispose();
				break;
			}

			offScreenImageGC.drawImage(refresh_image, 0, 0, imageData.width,
					imageData.height, imageData.x, imageData.y,
					imageData.width, imageData.height);

			gfx.drawImage(offScreenImage, leftCrop, topCrop, cropedWidth,
					cropedHeight, bound.x, bound.y, cropedWidth, cropedHeight);
			refresh_image.dispose();
		} else { // draw static image
			if (animated && animationDisabled && offScreenImage != null
					&& showIndex != 0) {
				gfx.drawImage(offScreenImage, leftCrop, topCrop, cropedWidth,
						cropedHeight, bound.x, bound.y, cropedWidth,
						cropedHeight);
			} else
				gfx.drawImage(staticImage, leftCrop, topCrop, cropedWidth,
						cropedHeight, bound.x, bound.y, cropedWidth,
						cropedHeight);
		}
	}

	/**
	 * Resizes the image.
	 */
	public synchronized void resizeImage() {
		if (staticImage != null && !staticImage.isDisposed()) {
			staticImage.dispose();
		}
		staticImage = null;
		if (refreshing && animated) {
			stopAnimation();
			startAnimation();
		}
		repaint();
	}

	/**
	 * Automatically make the widget bounds be adjusted to the size of the
	 * static image
	 * 
	 * @param autoSize
	 */
	public synchronized void setAutoSize(final boolean autoSize) {
		if (!stretch && autoSize)
			resizeImage();
	}

	public synchronized void setAnimationDisabled(final boolean stop) {
		if (animationDisabled == stop)
			return;
		animationDisabled = stop;
		if (stop) {
			stopAnimation();
		} else if (animated) {
			startAnimation();
		}
	}

	/**
	 * Sets the amount of pixels, which are cropped from the bottom.
	 * 
	 * @param newval
	 *            The amount of pixels
	 */
	public synchronized void setBottomCrop(final int newval) {
		if (bottomCrop == newval //|| (newval + topCrop) >= imgHeight
				|| newval < 0 || (newval + topCrop) < 0)
			return;
		bottomCrop = newval;
		resizeImage();
	}

	/**
	 * Sets the path to the image.
	 * 
	 * @param newval
	 *            The path to the image
	 */
	public synchronized void setFilePath(final IPath newval) {
		if (newval == null)
			return;
		if (animated) {
			stopAnimation();
			animationIndex = 0;
		}
		loadingError = false;
		filePath = newval;
		if (staticImage != null && !staticImage.isDisposed()) {
			staticImage.dispose();
		}
		staticImage = null;
		if (filePath.getFileExtension() != null
				&& "svg".compareToIgnoreCase(filePath.getFileExtension()) == 0) {
			workingWithSVG = true;
			transcoder = null;
			failedToLoadDocument = false;
			loadDocument();
		} else {
			workingWithSVG = false;
			loadImageFromFile();
			if (animated) {
				startAnimation();
			}
		}
	}

	/**
	 * Sets the amount of pixels, which are cropped from the left.
	 * 
	 * @param newval
	 *            The amount of pixels
	 */
	public synchronized void setLeftCrop(final int newval) {
		if (leftCrop == newval || newval < 0 //|| (newval + rightCrop) > imgWidth //image may not be loaded when this is set
				|| (newval + rightCrop) < 0)
			return;
		leftCrop = newval;
		resizeImage();
	}

	/**
	 * Sets the amount of pixels, which are cropped from the right.
	 * 
	 * @param newval
	 *            The amount of pixels
	 */
	public synchronized void setRightCrop(final int newval) {
		if (rightCrop == newval || newval < 0 //|| (newval + leftCrop) > imgWidth
				|| (newval + leftCrop) < 0)
			return;
		rightCrop = newval;
		resizeImage();
	}

	/**
	 * @param showIndex
	 *            the showIndex to set
	 */
	protected synchronized void setShowIndex(int showIndex) {
		if (showIndex >= imageDataArray.length || this.showIndex == showIndex)
			return;
		this.showIndex = showIndex;
		repaint();
	}

	/**
	 * Sets the stretch state for the image.
	 * 
	 * @param newval
	 *            The new state (true, if it should be stretched, false
	 *            otherwise)
	 */
	public synchronized void setStretch(final boolean newval) {
		if (stretch == newval)
			return;
		stretch = newval;
		if (staticImage != null && !staticImage.isDisposed()) {
			staticImage.dispose();
		}
		staticImage = null;
		if (refreshing && animated) {
			stopAnimation();
			startAnimation();
		}
		repaint();
	}

	/**
	 * Sets the amount of pixels, which are cropped from the top.
	 * 
	 * @param newval
	 *            The amount of pixels
	 */
	public synchronized void setTopCrop(final int newval) {
		if (topCrop == newval || newval < 0
//				|| (newval + bottomCrop) > imgHeight
				|| (newval + bottomCrop) < 0)
			return;
		topCrop = newval;
		resizeImage();
	}

	@Override
	public void setBorder(Border border) {
		super.setBorder(border);
		resizeImage();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			startAnimation();
		else {
			stopAnimation();
		}
	}

	/**
	 * Start animation. The request will be pended until figure painted for the
	 * first time.
	 */
	public synchronized void startAnimation() {
		startAnimationRequested = true;
		repaint();
	}

	/**
	 * start the animation if the image is an animated GIF image.
	 */
	public synchronized void realStartAnimation() {
		startAnimationRequested = false;
		if (animated && !refreshing && !animationDisabled) {
			repeatCount = loader.repeatCount;
			// animationIndex = 0;
			lastUpdateTime = 0;
			interval_ms = 0;
			refreshing = true;
			Runnable animationTask = new Runnable() {
				public void run() {
					UIBundlingThread.getInstance().addRunnable(new Runnable() {

						public void run() {
							synchronized (ImageFigure.this) {
								if (refreshing
										&& (loader.repeatCount == 0 || repeatCount > 0)) {
									long currentTime = System
											.currentTimeMillis();
									// use Math.abs() to ensure that the system
									// time adjust won't cause problem
									if (Math.abs(currentTime - lastUpdateTime) >= interval_ms) {
										setShowIndex(animationIndex);
										lastUpdateTime = currentTime;
										int ms = originalImageDataArray[animationIndex].delayTime * 10;
										animationIndex = (animationIndex + 1)
												% originalImageDataArray.length;
										if (ms < 20)
											ms += 30;
										if (ms < 30)
											ms += 10;
										interval_ms = ms;
										/*
										 * If we have just drawn the last image,
										 * decrement the repeat count and start
										 * again.
										 */
										if (loader.repeatCount > 0
												&& animationIndex == originalImageDataArray.length - 1)
											repeatCount--;
									}
								} else if (loader.repeatCount > 0
										&& repeatCount <= 0) { // stop thread
																// when
																// animation
																// finished
									if (scheduledFuture != null) {
										scheduledFuture.cancel(true);
										scheduledFuture = null;
									}
								}
							}

						}
					});
				}
			};

			if (scheduledFuture != null) {
				scheduledFuture.cancel(true);
				scheduledFuture = null;
			}
			scheduledFuture = ExecutionService
					.getInstance()
					.getScheduledExecutorService()
					.scheduleAtFixedRate(animationTask, 100, 10,
							TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * stop the animation if the image is an animated GIF image.
	 */
	public synchronized void stopAnimation() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
			scheduledFuture = null;
		}
		refreshing = false;
	}

	/**
	 * We want to have local coordinates here.
	 * 
	 * @return True if here should used local coordinates
	 */
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}
	
	public void setPermutationMatrix(final PermutationMatrix permutationMatrix) {
		this.oldPermutationMatrix = this.permutationMatrix;
		this.permutationMatrix = permutationMatrix;
		staticImageData = null; // Reset data
		if ((oldPermutationMatrix != null && oldPermutationMatrix.equals(permutationMatrix)) 
				|| permutationMatrix == null || animated)
			return;
		dispose();
		repaint();
	}

	public PermutationMatrix getPermutationMatrix() {
		return permutationMatrix;
	}

	public void setAbsoluteScale(double newScale) {
		if (this.scale == newScale)
			return;
		this.scale = newScale;
		if (workingWithSVG)
			repaint();
	}

	// ************************************************************
	// SVG specific methods
	// ************************************************************

	private void loadDocument() {
		transcoder = null;
		failedToLoadDocument = true;
		if (filePath == null || filePath.isEmpty())
			return;
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		try {
			String uri = "file://" + filePath.toString();
			final InputStream inputStream = ResourceUtil
					.pathToInputStream(filePath);
			svgDocument = factory.createDocument(uri, inputStream);
			transcoder = new SimpleImageTranscoder(svgDocument);
			initRenderingHints();
			BufferedImage awtImage = transcoder.getBufferedImage();
			if (awtImage != null) {
				originalStaticImageData = SVGUtils.toSWT(Display.getCurrent(), awtImage);
				imgWidth = originalStaticImageData.width;
				imgHeight = originalStaticImageData.height;
			}
			failedToLoadDocument = false;
			resizeImage();
			fireImageLoadedListeners();
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING,
					"Error loading SVG file " + filePath, e);
		}
	}

	private final Document getDocument() {
		if (failedToLoadDocument)
			return null;
		if (transcoder == null)
			loadDocument();
		return transcoder == null ? null : transcoder.getDocument();
	}

	private void initRenderingHints() {
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
	}

}