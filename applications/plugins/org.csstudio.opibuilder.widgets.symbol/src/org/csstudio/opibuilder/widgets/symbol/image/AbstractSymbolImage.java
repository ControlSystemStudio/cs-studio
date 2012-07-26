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
import org.csstudio.opibuilder.widgets.symbol.util.ImagePermuter;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

public abstract class AbstractSymbolImage extends Figure {
	
	private IPath imagePath;
	
	private ImageData imageData;
	private ImageData originalImageData;
	
	protected Color oldColor;
	protected Color currentColor;
	
	private int imgWidth = 0;
	private int imgHeight = 0;
	private int topCrop = 0;
	private int bottomCrop = 0;
	private int leftCrop = 0;
	private int rightCrop = 0;
	private int cropedWidth = 0;
	private int cropedHeight = 0;
	private boolean stretch = false;
	
	private int degree = 0;
	private boolean flipV = false;
	private boolean flipH = false;

	protected boolean imageDisposed = false;
	private ExecutionMode executionMode;
	
	// SVG attributes
	protected boolean workingWithSVG = false;
	private boolean failedToLoadDocument;
	private SimpleImageTranscoder transcoder;
	private Document svgDocument;
	
	private String oldImageSate = "1234";
	private String imageState;
	
	public AbstractSymbolImage(boolean runMode) {
		this.executionMode = runMode ? ExecutionMode.RUN_MODE
				: ExecutionMode.EDIT_MODE;
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
		this.imgWidth = originalImageData.width;
		this.imgHeight = originalImageData.height;
	}
	
	public ImageData getOriginalImageData() {
		return originalImageData;
	}

	/**
	 * Dispose the resource used by this figure
	 */
	public synchronized void dispose() {
		imageDisposed = true;
		// Reset parameters
		imageData = null;
		oldImageSate = "1234";
		oldColor = null;
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
	public void setBounds(Rectangle rect) {
		Rectangle bounds = getBounds().getCopy();
		if (bounds.width != rect.width || bounds.height != rect.height) {
			resizeImage();
		}
		super.setBounds(rect);
	}
	
	// ************************************************************
	// Image color & paint
	// ************************************************************

	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color stateColor) {
		this.currentColor = stateColor;
		if ((currentColor != null && oldColor == null)
				|| !currentColor.equals(oldColor)) {
			dispose();
		}
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
		// SVG image need specific operations on size
		if (workingWithSVG) {
			paintSVG(gfx);
			return;
		}
		Image image = null;
		Rectangle bounds = getBounds().getCopy();
		if (imageData == null) {
			imageData = (ImageData) originalImageData.clone();
		}

		// Set flip/rotate 
		if (oldImageSate != imageState) {
			ImagePermuter imp = new ImagePermuter();
			imageData = imp.applyPermutation(imageData, imageState);
			oldImageSate = imageState;
		}
		
		// Set color
		if (isEditMode()) { // Color for edit mode is black
			currentColor = new Color(null, new RGB(0, 0, 0));
		}
		if ((currentColor != null && oldColor == null)
				|| !currentColor.equals(oldColor)) {
			ImageUtils.changeImageColor2(currentColor, imageData);
			oldColor = currentColor;
		}
		imageDisposed = false; // ImageData is set
		
		// Create image
		if (stretch) {
			image = new Image(Display.getDefault(), imageData.scaledTo(
					bounds.width + leftCrop + rightCrop, bounds.height
							+ topCrop + bottomCrop));
		} else {
			image = new Image(Display.getDefault(), imageData);
		}
		imgWidth = image.getBounds().width;
		imgHeight = image.getBounds().height;
		
		// Avoid negative number
		updateDimensions();
		if (leftCrop + cropedWidth > imgWidth
				|| topCrop + cropedHeight > imgHeight) {
			return;
		}
		
		// Draw graphic image
		if (image != null) {
			try {
				gfx.drawImage(image, leftCrop, topCrop, cropedWidth, cropedHeight, 
						bounds.x, bounds.y, cropedWidth, cropedHeight);
			} finally {
				image.dispose();
			}
		}
	}
	
	public void paintSVG(final Graphics gfx) {
		Image image = null;
		Rectangle bounds = getBounds().getCopy();
		
		// Crop is applied on original image without flip/rotate
		// so we need to apply this permutation to crop
		int lc = leftCrop, rc = rightCrop, bc = bottomCrop, tc = topCrop;
		String imgState = imageState;
		if (imageDisposed && oldImageSate != null) {
			imgState = oldImageSate;
		}
		switch (imgState.indexOf('1')) {
		case 1:
			rc = topCrop;
			bc = rightCrop;
			lc = bottomCrop;
			tc = leftCrop;
			break;
		case 2:
			rc = leftCrop;
			bc = topCrop;
			lc = rightCrop;
			tc = bottomCrop;
			break;
		case 3:
			rc = bottomCrop;
			bc = leftCrop;
			lc = topCrop;
			tc = rightCrop;
			break;
		default:
		}
		
		// SVG image size rendering
		if (imageData == null) {
			Document document = getDocument();
			if (document == null) {
				return;
			}
			// Scale image
			if (stretch) {
				transcoder.setCanvasSize(bounds.width + lc + rc, 
						bounds.height + bc + tc);
			} else {
				transcoder.setCanvasSize(-1, -1);
			}
			// Set color
			if (isEditMode()) { // Color for edit mode is black
				currentColor = new Color(null, new RGB(0, 0, 0));
			}
			if ((currentColor != null && oldColor == null)
					|| !currentColor.equals(oldColor)) {
				transcoder.setColor(currentColor);
				oldColor = currentColor;
			}
			BufferedImage awtImage = transcoder.getBufferedImage();
			if (awtImage != null) {
				imageData = SVGUtils.toSWT(Display.getCurrent(), awtImage);
			}
		}
		
		// Set flip/rotate
		if (oldImageSate != imageState) {
			ImagePermuter imp = new ImagePermuter();
			imageData = imp.applyPermutation(imageData, imageState);
			oldImageSate = imageState;
		}
		imageDisposed = false; // ImageData is set
		
		// Create image
		image = new Image(Display.getDefault(), imageData);
		imgWidth = image.getBounds().width;
		imgHeight = image.getBounds().height;
		
		updateDimensions();
		// Avoid negative number
		if (leftCrop + cropedWidth > imgWidth
				|| topCrop + cropedHeight > imgHeight) {
			return;
		}
		
		// Draw graphic image
		if (image != null) {
			try {
				gfx.drawImage(image, leftCrop, topCrop, cropedWidth, cropedHeight, 
						bounds.x, bounds.y, cropedWidth, cropedHeight);
			} finally {
				image.dispose();
			}
		}
	}
	
	private void updateDimensions() {
		if(imgWidth == 0 || imgHeight == 0) {
			imgWidth = originalImageData.width;
			imgHeight = originalImageData.height;
		}
		leftCrop = leftCrop > imgWidth ? 0 : leftCrop;
		topCrop = topCrop > imgHeight ? 0 : topCrop;
		cropedWidth = (imgWidth - leftCrop - rightCrop) > 0 ? 
				(imgWidth - leftCrop - rightCrop) : imgWidth;
		cropedHeight = (imgHeight - topCrop - bottomCrop) > 0 ? 
				(imgHeight - topCrop - bottomCrop) : imgHeight;
	}

	// ************************************************************
	// Image size calculation
	// ************************************************************
	
	/**
	 * Resizes the image.
	 */
	public synchronized void resizeImage() {
		dispose();
	}

	/**
	 * Automatically adjust the widget bounds to fit the size of the static
	 * image
	 * 
	 * @param autoSize
	 */
	public synchronized void setAutoSize(final boolean autoSize) {
		if (!stretch && autoSize) {
			resizeImage();
		}
	}
	
	/**
	 * Get the auto sized widget dimension according to the static image size.
	 * 
	 * @return The auto sized widget dimension.
	 */
	public synchronized Dimension getAutoSizedDimension() {
		if (originalImageData != null) {
			updateDimensions();
			return new Dimension(cropedWidth + getInsets().getWidth(),
					cropedHeight + getInsets().getHeight());
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
		resizeImage();
	}

	/**
	 * Sets the amount of pixels, which are cropped from the right.
	 */
	public synchronized void setRightCrop(final int newval) {
		if (rightCrop == newval || newval < 0) {
			return;
		}
		rightCrop = newval;
		resizeImage();
	}

	/**
	 * Sets the amount of pixels, which are cropped from the bottom.
	 */
	public synchronized void setBottomCrop(final int newval) {
		if (bottomCrop == newval || newval < 0) {
			return;
		}
		bottomCrop = newval;
		resizeImage();
	}

	/**
	 * Sets the amount of pixels, which are cropped from the top.
	 */
	public synchronized void setTopCrop(final int newval) {
		if (topCrop == newval || newval < 0) {
			return;
		}
		topCrop = newval;
		resizeImage();
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
		resizeImage();
	}
	
	public synchronized void setFlipV(boolean flipV) {
		this.flipV = flipV;
	}

	public synchronized void setFlipH(boolean flipH) {
		this.flipH = flipH;
	}
	
	public synchronized void setDegree(Integer degree) {
		this.degree = degree;
	}
	
	public void setImageState(String imageState) {
		this.imageState = imageState;
		dispose();
	}

	public String getImageState() {
		return this.imageState;
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
			String uri = "file://"
					+ ResourcesPlugin.getWorkspace().getRoot().getRawLocation()
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
	
	protected final Document getDocument() {
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
