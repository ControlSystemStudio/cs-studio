/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.image.AbstractSymbolImage;
import org.csstudio.opibuilder.widgets.symbol.image.ControlSymbolImage;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolImageProperties;
import org.csstudio.swt.widgets.figures.AbstractBoolControlFigure;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.csstudio.swt.widgets.util.TextPainter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Control figure for Boolean Symbol Image widget based on
 * {@link ControlBoolSymbolModel}
 * 
 * @author SOPRA Group
 * 
 */
public class ControlBoolSymbolFigure extends AbstractBoolControlFigure {

	/**
	 * The {@link IPath} to the on and off images.
	 */
	private IPath onImagePath = new Path("");
	private IPath offImagePath = new Path("");
	
	/**
	 * The on and off images themselves.
	 */
	private AbstractSymbolImage onImage;
	private AbstractSymbolImage offImage;
	protected SymbolImageProperties symbolProperties;
	protected boolean workingWithSVG = false;

	private boolean loadingError = false;
	private volatile boolean loadingImage;

	private ExecutionMode executionMode;
	private Cursor cursor;

	/* ************************* */
	/* Specific code for control */
	/* ************************* */

	public ControlBoolSymbolFigure() {
		cursor = Cursors.HAND;
		addMouseListener(buttonPresser);
		add(boolLabel);
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if (runMode) {
			if (value) {
				if (cursor == null || cursor.isDisposed()) {
					cursor = Cursors.HAND;
				}
			} else {
				cursor = null;
			}
		}
		setCursor(runMode ? cursor : null);
	}

	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);
		setCursor(runMode ? cursor : null);
	}

	/* ************************* */
	/* Common code with monitor */
	/* ************************* */
	
	private AbstractSymbolImage createSymbolImage(boolean runMode) {
		ControlSymbolImage csi = new ControlSymbolImage(runMode);
		if (symbolProperties != null) {
			symbolProperties.fillSymbolImage(csi);
		}
		return csi;
	}

	/**
	 * Return the ON image. If null, returns an empty image.
	 */
	public AbstractSymbolImage getOnImage() {
		if (onImage == null) {
			onImage = createSymbolImage(!isEditMode());
		}
		return onImage;
	}
	
	/**
	 * Return the OFF image. If null, returns an empty image.
	 */
	public AbstractSymbolImage getOffImage() {
		if (offImage == null) {
			offImage = createSymbolImage(!isEditMode());
		}
		return offImage;
	}
	
	/**
	 * Return the current displayed image. If null, returns an empty image.
	 */
	public AbstractSymbolImage getCurrentImage() {
		AbstractSymbolImage image = null;
		if (isEditMode()) {
			image = offImage;
		} else {
			image = booleanValue ? onImage : offImage;
		}
		if (image == null) {
			image = createSymbolImage(!isEditMode());
		}
		return image;
	}
	
	/**
	 * Dispose the image resources used by this figure.
	 */
	public synchronized void dispose() {
		if (onImage != null && !onImage.isDisposed()) {
			onImage.dispose();
			onImage = null;
		}
		if (offImage != null && !offImage.isDisposed()) {
			offImage.dispose();
			offImage = null;
		}
	}

	/**
	 * Get the path for ON image.
	 * 
	 * @return the onImagePath
	 */
	public synchronized IPath getOnImagePath() {
		return getOnImage().getImagePath();
	}

	/**
	 * Get the path for OFF image.
	 * 
	 * @return the offImagePath
	 */
	public synchronized IPath getOffImagePath() {
		return getOffImage().getImagePath();
	}
	
	protected boolean isEditMode() {
		return ExecutionMode.EDIT_MODE.equals(executionMode);
	}
	
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
	
	public void setSymbolProperties(SymbolImageProperties symbolProperties) {
		this.symbolProperties = symbolProperties;
	}
	
	// ************************************************************
	// Image loading
	// ************************************************************
	
	/**
	 * Set the boolean symbol on and off image path. If the path is relative,
	 * then build absolute path.
	 * <p>
	 * <b>Rules:</b> <br>
	 * If the image selected is on, then search off image. <br>
	 * If the image selected is off, then search on image. <br>
	 * If one or both is not found, the image path selected is the same for
	 * both.
	 * 
	 * @param model
	 * @param imagePath
	 *            The path to the selected image (on or off or other)
	 */
	public synchronized void setSymbolImagePath(ControlBoolSymbolModel model,
			IPath imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			return;
		}
		if (!imagePath.isAbsolute()) {
			imagePath = org.csstudio.opibuilder.util.ResourceUtil
					.buildAbsolutePath(model, imagePath);
		}
		// Default
		this.onImagePath = imagePath;
		this.offImagePath = imagePath;

		// Search on or off equivalent
		if (ImageUtils.isOnImage(imagePath)) {
			IPath offImagePath = ImageUtils.searchOffImage(imagePath);
			if (offImagePath != null) {
				this.offImagePath = offImagePath;
			}
		} else if (ImageUtils.isOffImage(imagePath)) {
			IPath onImagePath = ImageUtils.searchOnImage(imagePath);
			if (onImagePath != null) {
				this.onImagePath = onImagePath;
			}
		}
		if (imagePath.getFileExtension() != null
				&& "svg".compareToIgnoreCase(imagePath.getFileExtension()) == 0) {
			workingWithSVG = true;
		} else {
			workingWithSVG = false;
		}
		loadOffImage();
		loadOnImage();
	}

	private synchronized void resetOnImage() {
		if (onImage != null && !onImage.isDisposed()) {
			onImage.dispose();
		}
		onImage = null;
	}

	private synchronized void resetOffImage() {
		if (offImage != null && !offImage.isDisposed()) {
			offImage.dispose();
		}
		offImage = null;
	}

	private synchronized void loadOffImage() {
		loadingError = false;
		resetOffImage();
		loadBoolImageFromFile(false);
	}

	private synchronized void loadOnImage() {
		loadingError = false;
		resetOnImage();
		loadBoolImageFromFile(true);
	}

	private synchronized void loadBoolImageFromFile(boolean boolOnImage) {
		final IPath imagePath = boolOnImage ? onImagePath : offImagePath;
		if (imagePath != null && !imagePath.isEmpty()) {
			loadingImage = true;
			loadBoolImage(boolOnImage, new IJobErrorHandler() {
				public void handleError(Exception exception) {
					loadingError = true;
					loadingImage = false;
					Activator.getLogger().log(Level.WARNING,
							"ERROR in loading image " + imagePath, exception);
				}
			});
		}
	}

	private synchronized void loadBoolImage(final boolean boolOnImage,
			IJobErrorHandler errorHandler) {
		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
			@Override
			public void runWithInputStream(InputStream stream) {
				synchronized (ControlBoolSymbolFigure.this) {
					Image tempImage = null;
					try {
						if (boolOnImage) {
							onImage = createSymbolImage(!isEditMode());
							onImage.setImagePath(onImagePath);
							if (!workingWithSVG) {
								tempImage = new Image(Display.getDefault(), stream);
								ImageData imgData = tempImage.getImageData();
								onImage.setOriginalImageData(imgData);
							}
						} else {
							offImage = createSymbolImage(!isEditMode());
							offImage.setImagePath(offImagePath);
							if (!workingWithSVG) {
								tempImage = new Image(Display.getDefault(), stream);
								ImageData imgData = tempImage.getImageData();
								offImage.setOriginalImageData(imgData);
							}
						}
					} finally {
						try {
							stream.close();
							if (tempImage != null && !tempImage.isDisposed()) {
								tempImage.dispose();
							}
						} catch (IOException exception) {
							Activator.getLogger()
									.log(Level.WARNING,
											"ERROR in closing image stream ",
											exception);
						}
					}
					loadingImage = false;
					repaint();
				}
			}
		};
		IPath imagePath = boolOnImage ? onImagePath : offImagePath;
		ResourceUtil.pathToInputStreamInJob(imagePath, uiTask,
				"Loading Image...", errorHandler);
	}	
	
	public boolean isLoadingImage() {
		return loadingImage;
	}
	
	// ************************************************************
	// Image size calculation delegation
	// ************************************************************
	
	public synchronized void resizeImage() {
		getOnImage().resizeImage();
		getOffImage().resizeImage();
		repaint();
	}

	public synchronized void setAutoSize(final boolean autoSize) {
		if (symbolProperties != null) {
			symbolProperties.setAutoSize(autoSize);
		}
		getOnImage().setAutoSize(autoSize);
		getOffImage().setAutoSize(autoSize);
		repaint();
	}
	
	/**
	 * Get the auto sized widget dimension according to the static image size.
	 * 
	 * @return The auto sized widget dimension.
	 */
	public synchronized Dimension getAutoSizedDimension() {
		return getCurrentImage().getAutoSizedDimension();
	}

	// ************************************************************
	// Image crop calculation delegation
	// ************************************************************
	
	/**
	 * Sets the amount of pixels, which are cropped from the left.
	 * 
	 * @param newval The amount of pixels
	 */
	public synchronized void setLeftCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setLeftCrop(newval);
		}
		getOnImage().setLeftCrop(newval);
		getOffImage().setLeftCrop(newval);
		repaint();
	}
	/**
	 * Sets the amount of pixels, which are cropped from the right.
	 * 
	 * @param newval The amount of pixels
	 */
	public synchronized void setRightCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setRightCrop(newval);
		}
		getOnImage().setRightCrop(newval);
		getOffImage().setRightCrop(newval);
		repaint();
	}
	/**
	 * Sets the amount of pixels, which are cropped from the bottom.
	 * 
	 * @param newval The amount of pixels
	 */
	public synchronized void setBottomCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setBottomCrop(newval);
		}
		getOnImage().setBottomCrop(newval);
		getOffImage().setBottomCrop(newval);
		repaint();
	}
	/**
	 * Sets the amount of pixels, which are cropped from the top.
	 * 
	 * @param newval The amount of pixels
	 */
	public synchronized void setTopCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setTopCrop(newval);
		}
		getOnImage().setTopCrop(newval);
		getOffImage().setTopCrop(newval);
		repaint();
	}
	public int getLeftCrop() {
		return getCurrentImage().getLeftCrop();
	}
	public int getRightCrop() {
		return getCurrentImage().getRightCrop();
	}
	public int getBottomCrop() {
		return getCurrentImage().getBottomCrop();
	}
	public int getTopCrop() {
		return getCurrentImage().getTopCrop();
	}
	
	// ************************************************************
	// Image flip & degree & stretch calculation delegation
	// ************************************************************
	
	/**
	 * Set the stretch state for the image.
	 * 
	 * @param newval
	 *            The new state (true, if it should be stretched, false
	 *            otherwise)
	 */
	public synchronized void setStretch(final boolean newval) {
		if (symbolProperties != null) {
			symbolProperties.setStretch(newval);
		}
		getOnImage().setStretch(newval);
		getOffImage().setStretch(newval);
		repaint();
	}
	public synchronized void setFlipV(boolean flipV) {
		if (symbolProperties != null) {
			symbolProperties.setFlipV(flipV);
		}
		getOnImage().setFlipV(flipV);
		getOffImage().setFlipV(flipV);
		repaint();
	}
	public synchronized void setFlipH(boolean flipH) {
		if (symbolProperties != null) {
			symbolProperties.setFlipH(flipH);
		}
		getOnImage().setFlipH(flipH);
		getOffImage().setFlipH(flipH);
		repaint();
	}
	public synchronized void setDegree(Integer degree) {
		if (symbolProperties != null) {
			symbolProperties.setDegree(degree);
		}
		getOnImage().setDegree(degree);
		getOffImage().setDegree(degree);
		repaint();
	}
	public void setPermutationMatrix(PermutationMatrix permutationMatrix) {
		if (symbolProperties != null) {
			symbolProperties.setMatrix(permutationMatrix);
		}
		getOnImage().setPermutationMatrix(permutationMatrix);
		getOffImage().setPermutationMatrix(permutationMatrix);
		repaint();
	}
	public PermutationMatrix getPermutationMatrix() {
		return getCurrentImage().getPermutationMatrix();
	}
	public boolean isStretch() {
		return getCurrentImage().isStretch();
	}
	public synchronized boolean isFlipV() {
		return getCurrentImage().isFlipV();
	}
	public synchronized boolean isFlipH() {
		return getCurrentImage().isFlipH();
	}
	public synchronized Integer getDegree() {
		return getCurrentImage().getDegree();
	}

	// ************************************************************
	// Image color & paint
	// ************************************************************

	/**
	 * The main drawing routine.
	 * 
	 * @param gfx The {@link Graphics} to use
	 */
	@Override
	public synchronized void paintFigure(final Graphics gfx) {
		if (isLoadingImage())
			return;
		Rectangle bounds = getBounds().getCopy();
		ImageUtils.crop(bounds, this.getInsets());
		IPath imagePath = booleanValue ? onImagePath : offImagePath;
		if (loadingError) {
			if (!imagePath.isEmpty()) {
				gfx.setBackgroundColor(getBackgroundColor());
				gfx.setForegroundColor(getForegroundColor());
				gfx.fillRectangle(bounds);
				gfx.translate(bounds.getLocation());
				TextPainter.drawText(gfx, "ERROR in loading image\n"
						+ imagePath, bounds.width / 2, bounds.height / 2,
						TextPainter.CENTER);
			}
			return;
		}
		getOnImage().setCurrentColor(onColor);
		getOffImage().setCurrentColor(offColor);
		getCurrentImage().setBounds(bounds);
		getCurrentImage().setBorder(getBorder());
		getCurrentImage().setAbsoluteScale(gfx.getAbsoluteScale());
		getCurrentImage().paintFigure(gfx);
		if (!isEnabled()) {
			gfx.setAlpha(DISABLED_ALPHA);
			gfx.setBackgroundColor(DISABLE_COLOR);
			gfx.fillRectangle(bounds);
		}
	}

	/**
	 * @param onColor the onColor to set
	 */
	public synchronized void setOnColor(Color onColor) {
		if (this.onColor != null && this.onColor.equals(onColor)) {
			return;
		}
		if ((onColor.getRed() << 16 | onColor.getGreen() << 8 | onColor
				.getBlue()) == 0xFFFFFF) {
			this.onColor = CustomMediaFactory.getInstance().getColor(
					new RGB(255, 255, 254));
		} else {
			this.onColor = onColor;
		}
		repaint();
	}

	/**
	 * @param offColor  the offColor to set
	 */
	public synchronized void setOffColor(Color offColor) {
		if (this.offColor != null && this.offColor.equals(offColor)) {
			return;
		}
		if ((offColor.getRed() << 16 | offColor.getGreen() << 8 | offColor
				.getBlue()) == 0xFFFFFF) {
			this.offColor = CustomMediaFactory.getInstance().getColor(
					new RGB(255, 255, 254));
		} else {
			this.offColor = offColor;
		}
		repaint();
	}

	// ************************************************************
	// Override Figure class methods 
	// ************************************************************

	/**
	 * We want to have local coordinates here.
	 * 
	 * @return True if here should used local coordinates
	 */
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		if (boolLabel.isVisible()) {
			Dimension labelSize = boolLabel.getPreferredSize();
			boolLabel.setBounds(new Rectangle(getLabelLocation(clientArea.x
					+ clientArea.width / 2 - labelSize.width / 2, clientArea.y
					+ clientArea.height / 2 - labelSize.height / 2),
					new Dimension(labelSize.width, labelSize.height)));
		}
		super.layout();
	}

}
