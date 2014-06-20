/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.image;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.utils.SVGUtils;
import org.apache.batik.utils.SimpleImageTranscoder;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.Preferences;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

/**
 * Main class for Symbol Image display.
 * @author Fred Arnaud (Sopra Group)
 */
public abstract class AbstractSymbolImage extends Figure {
	
	private IPath imagePath;
	
	private Image image;
	private ImageData imageData;
	private ImageData originalImageData;
	
	private Color currentColor;
	private Color colorToChange;

	private Dimension imgDimension;
	
	private int topCrop = 0;
	private int bottomCrop = 0;
	private int leftCrop = 0;
	private int rightCrop = 0;
	private boolean stretch = false;
	private double scale = 1.0;
	
	private int degree = 0;
	private boolean flipV = false;
	private boolean flipH = false;
	
	private Rectangle srcArea = null;
	private Rectangle destArea = null;

	protected boolean imageDisposed = false;
	private ExecutionMode executionMode;
	
	// SVG attributes
	protected boolean workingWithSVG = false;
	private boolean failedToLoadDocument;
	private SimpleImageTranscoder transcoder;
	private Document svgDocument;

	private PermutationMatrix oldPermutationMatrix = null;
	private PermutationMatrix permutationMatrix = PermutationMatrix
			.generateIdentityMatrix();
	
	
	public AbstractSymbolImage(boolean runMode) {
		this.executionMode = runMode ? ExecutionMode.RUN_MODE
				: ExecutionMode.EDIT_MODE;
		imgDimension = new Dimension(0, 0);
		colorToChange = new Color(Display.getCurrent(),
				Preferences.getColorToChange());
	}

	public void setImagePath(IPath imagePath) {
		this.imagePath = imagePath;
		if ("svg".compareToIgnoreCase(imagePath.getFileExtension()) == 0) {
			workingWithSVG = true;
			transcoder = null;
			failedToLoadDocument = false;
			loadDocument();
		}
	}
	
	public IPath getImagePath() {
		return imagePath;
	}

	public void setOriginalImageData(ImageData originalImageData) {
		BufferedImage awtImage = ImageUtils.convertToAWT(originalImageData);
		int w = awtImage.getWidth();
		int h = awtImage.getHeight();
		BufferedImage grayImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		grayImage.getGraphics().drawImage(awtImage, 0, 0, w, h, null);
		ImageData tmpData = SVGUtils.toSWT(Display.getCurrent(), awtImage);
		if (originalImageData.alphaData != null) {
			tmpData.alphaData = new byte[originalImageData.alphaData.length];
			System.arraycopy(originalImageData.alphaData, 0, tmpData.alphaData,
					0, originalImageData.alphaData.length);
		}
		tmpData.transparentPixel = originalImageData.transparentPixel;
		this.originalImageData = (ImageData) tmpData.clone();
		imgDimension = new Dimension(originalImageData.width,
				originalImageData.height);
	}
	
	public ImageData getOriginalImageData() {
		return originalImageData;
	}

	/**
	 * Dispose the resource used by this figure
	 */
	public synchronized void dispose() {
		imageDisposed = true;
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	public boolean isDisposed() {
		return imageDisposed;
	}

	public boolean isEditMode() {
		return ExecutionMode.EDIT_MODE.equals(executionMode);
	}
	
	public boolean isEmpty() {
		return originalImageData == null;
	}
	
	@Override
	public void setBounds(Rectangle newBounds) {
		Rectangle oldBounds = getBounds().getCopy();
		if (oldBounds.width != newBounds.width
				|| oldBounds.height != newBounds.height) {
			if(workingWithSVG) imageData = null;
		}
		super.setBounds(newBounds);
	}
	
	@Override
	public void setBorder(Border newBorder) {
		Border oldBoder = getBorder();
		Insets oldInsets = (oldBoder == null) ? null : oldBoder.getInsets(this);
		Insets newInsets = (newBorder == null) ? null : newBorder.getInsets(this);
		if ((newInsets == null && oldInsets != null)
				|| (newInsets != null && !newInsets.equals(oldInsets))) {
			if(workingWithSVG) imageData = null;
		}
		super.setBorder(newBorder);
	}
	
	public void setAbsoluteScale(double newScale) {
		if (this.scale == newScale) return;
		this.scale = newScale;
		// resize SVG
		if (workingWithSVG) imageData = null;
	}
	
	// ************************************************************
	// Image color & paint
	// ************************************************************

	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color newColor) {
		if (newColor == null || (currentColor != null && currentColor.equals(newColor)))
			return;
		this.currentColor = newColor;
		imageData = null;
	}
	
	/**
	 * The main drawing routine.
	 * 
	 * @param gfx The {@link Graphics} to use
	 */
	@Override
	public synchronized void paintFigure(final Graphics gfx) {
		if (originalImageData == null)
			return;
		if (imageData == null)
			updateData();
		// Create image
		if (image == null) {
			if (stretch && !workingWithSVG) {
				image = new Image(Display.getDefault(), imageData.scaledTo(
						imgDimension.width + leftCrop + rightCrop,
						imgDimension.height + topCrop + bottomCrop));
			} else {
				image = new Image(Display.getDefault(), imageData);
			}
			imageDisposed = false; // ImageData is set
		}
		updateAreas();
		// Draw graphic image
		if (image != null) {
			gfx.drawImage(image, srcArea, destArea);
		}
	}
	
	public void updateData() {
		dispose();
		if (workingWithSVG) generateSVGData();
		else generateData();
	}
	
	private void updateAreas() {
		if (originalImageData == null)
			return;
		
		// Update dimensions
		int imgWidth = 0, imgHeight = 0;
		if (image != null) {
			imgWidth = image.getBounds().width;
			imgHeight = image.getBounds().height;
		} else if (imageData != null) {
			imgWidth = imageData.width;
			imgHeight = imageData.height;
		} else {
			imgWidth = originalImageData.width;
			imgHeight = originalImageData.height;
		}
		imgDimension = new Dimension(imgWidth, imgHeight);
		
		// Avoid negative number
		topCrop = topCrop > imgHeight ? 0 : topCrop;
		leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
		bottomCrop = (imgHeight - topCrop - bottomCrop) < 0 ? 0 : bottomCrop;
		rightCrop = (imgWidth - leftCrop - rightCrop) < 0 ? 0 : rightCrop;
		
		// Calculate areas
		if (workingWithSVG) {
			int cropedWidth = imgWidth - (int) Math.round(scale * (leftCrop + rightCrop));
			int cropedHeight = imgHeight - (int) Math.round(scale * (bottomCrop + topCrop));
			srcArea = new Rectangle(leftCrop, topCrop, cropedWidth, cropedHeight);
			destArea = new Rectangle(bounds.x, bounds.y,
					(int) Math.round(cropedWidth / scale),
					(int) Math.round(cropedHeight / scale));
		} else {
			int cropedWidth = imgWidth - leftCrop - rightCrop;
			int cropedHeight = imgHeight - bottomCrop - topCrop;
			srcArea = new Rectangle(leftCrop, topCrop, cropedWidth, cropedHeight);
			destArea = new Rectangle(bounds.x, bounds.y, cropedWidth, cropedHeight);
		}
	}
	
	private void generateData() {
		if (originalImageData == null)
			return;
		if (imageData == null) {
			imageData = (ImageData) originalImageData.clone();
		}
		// Set color
		if (isEditMode()) { // Color for edit mode is black
			currentColor = new Color(null, new RGB(0, 0, 0));
		}
		ImageUtils.changeImageColor(currentColor, imageData);
		
		// Set flip/rotate
		imageData = ImageUtils.applyMatrix(imageData, permutationMatrix);
		
		Rectangle bounds = getBounds().getCopy();
		int imgWidth = imageData.width;
		int imgHeight = imageData.height;
		if(stretch) {
			imgWidth = bounds.width;
			imgHeight = bounds.height;
		}
		imgDimension = new Dimension(imgWidth, imgHeight);
		// Avoid negative number
		topCrop = topCrop > imgHeight ? 0 : topCrop;
		leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
		bottomCrop = (imgHeight - topCrop - bottomCrop) < 0 ? 0 : bottomCrop;
		rightCrop = (imgWidth - leftCrop - rightCrop) < 0 ? 0 : rightCrop;
	}
	
	private void generateSVGData() {
		// Load document if do not exist
		Document document = getDocument();
		if (document == null) {
			return;
		}
		transcoder.setColorToChange(colorToChange);
		if (!isEditMode())
			transcoder.setColor(currentColor);
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
		// Avoid negative number
		topCrop = topCrop > imgHeight ? 0 : topCrop;
		leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
		bottomCrop = (imgHeight - topCrop - bottomCrop) < 0 ? 0 : bottomCrop;
		rightCrop = (imgWidth - leftCrop - rightCrop) < 0 ? 0 : rightCrop;
		imgWidth = (int) Math.round(scale * (imgWidth + leftCrop + rightCrop));
		imgHeight = (int) Math.round(scale * (imgHeight + bottomCrop + topCrop));
		transcoder.setCanvasSize(imgWidth, imgHeight);
		
		BufferedImage awtImage = transcoder.getBufferedImage();
		if (awtImage != null) {
			imageData = SVGUtils.toSWT(Display.getCurrent(), awtImage);
		}
		// Calculate areas
		imgWidth = imageData.width;
		imgHeight = imageData.height;
		imgDimension = new Dimension(imgWidth, imgHeight);
	}

	// ************************************************************
	// Image size calculation
	// ************************************************************
	
	/**
	 * Resizes the image.
	 */
	public synchronized void resizeImage() {
		// TODO: too much call to this method always reset data
//		dispose();
	}

	/**
	 * Automatically adjust the widget bounds to fit the size of the static
	 * image
	 * 
	 * @param autoSize
	 */
	public synchronized void setAutoSize(final boolean autoSize) {
		if (!stretch && autoSize) {
			imageData = null;
		}
	}
	
	/**
	 * Get the auto sized widget dimension according to the static image size.
	 * 
	 * @return The auto sized widget dimension.
	 */
	public synchronized Dimension getAutoSizedDimension() {
		updateAreas();
		if (destArea != null) {
			return new Dimension(destArea.width + getInsets().getWidth(),
					destArea.height + getInsets().getHeight());
		} else {
			return null;
		}
	}
	
	// ************************************************************
	// Image crop calculation
	// ************************************************************
	
	/**
	 * Sets the amount of pixels, which are cropped from the left.
	 */
	public synchronized void setLeftCrop(final int newval) {
		if (leftCrop == newval || newval < 0) {
			return;
		}
		leftCrop = newval;
		imageData = null;
	}

	/**
	 * Sets the amount of pixels, which are cropped from the right.
	 */
	public synchronized void setRightCrop(final int newval) {
		if (rightCrop == newval || newval < 0) {
			return;
		}
		rightCrop = newval;
		imageData = null;
	}

	/**
	 * Sets the amount of pixels, which are cropped from the bottom.
	 */
	public synchronized void setBottomCrop(final int newval) {
		if (bottomCrop == newval || newval < 0) {
			return;
		}
		bottomCrop = newval;
		imageData = null;
	}

	/**
	 * Sets the amount of pixels, which are cropped from the top.
	 */
	public synchronized void setTopCrop(final int newval) {
		if (topCrop == newval || newval < 0) {
			return;
		}
		topCrop = newval;
		imageData = null;
	}
	
	public int getLeftCrop() {
		return leftCrop;
	}
	public int getRightCrop() {
		return rightCrop;
	}
	public int getBottomCrop() {
		return bottomCrop;
	}
	public int getTopCrop() {
		return topCrop;
	}
	
	// ************************************************************
	// Image stretch & rotation calculation
	// ************************************************************
	
	/**
	 * Set the stretch state for the image.
	 * 
	 * @param newval true, if it should be stretched, 
	 * false otherwise)
	 */
	public synchronized void setStretch(final boolean newval) {
		if (stretch == newval) {
			return;
		}
		stretch = newval;
		if(workingWithSVG) imageData = null;
	}
	
	public void setPermutationMatrix(final PermutationMatrix permutationMatrix) {
		this.oldPermutationMatrix = this.permutationMatrix;
		this.permutationMatrix = permutationMatrix;
		if ((oldPermutationMatrix != null && oldPermutationMatrix.equals(permutationMatrix)) 
				|| permutationMatrix == null)
			return;
		imageData = null;
	}
	
	public synchronized void setFlipV(boolean flipV) {
		this.flipV = flipV;
//		imageData = null;
	}

	public synchronized void setFlipH(boolean flipH) {
		this.flipH = flipH;
//		imageData = null;
	}
	
	public synchronized void setDegree(Integer degree) {
		this.degree = degree;
//		imageData = null;
	}
	
	public PermutationMatrix getPermutationMatrix() {
		return permutationMatrix;
	}

	public boolean isStretch() {
		return stretch;
	}

	public synchronized boolean isFlipV() {
		return flipV;
	}

	public synchronized boolean isFlipH() {
		return flipH;
	}

	public synchronized Integer getDegree() {
		return degree;
	}
	
	// ************************************************************
	// SVG specific methods
	// ************************************************************
	
	private void loadDocument() {
		transcoder = null;
		failedToLoadDocument = true;
		if (imagePath == null || imagePath.isEmpty()) {
			return;
		}
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		try {
			IPath workSpacePath=ResourceUtil.workspacePathToSysPath(new Path("/")); //$NON-NLS-1$
			
			String uri = "file://"
					+  (workSpacePath == null? "" : workSpacePath.toOSString())//$NON-NLS-1$ //ResourcesPlugin.getWorkspace().getRoot().getRawLocation()
					+ imagePath.toString();
		
			final InputStream inputStream = ResourceUtil.pathToInputStream(imagePath);
			svgDocument = factory.createDocument(uri, inputStream);
			transcoder = new SimpleImageTranscoder(svgDocument);
			initRenderingHints();
			BufferedImage awtImage = transcoder.getBufferedImage();
			if (awtImage != null) {
				setOriginalImageData(SVGUtils.toSWT(Display.getCurrent(), awtImage));
			}
			failedToLoadDocument = false;
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING,
					"Error loading SVG file " + imagePath, e);
		}
	}
	
	private final Document getDocument() {
		if (failedToLoadDocument) {
			return null;
		}
		if (transcoder == null) {
			loadDocument();
		}
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
