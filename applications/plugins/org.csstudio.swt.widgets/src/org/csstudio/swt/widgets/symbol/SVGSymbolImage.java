/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.utils.SVGUtils;
import org.apache.batik.utils.SimpleImageTranscoder;
import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

public class SVGSymbolImage extends AbstractSymbolImage {

	private Dimension imgDimension = null;

	private boolean loadingImage = false;
	private boolean failedToLoadDocument = false;
	private SimpleImageTranscoder transcoder;
	private Document svgDocument;

	public SVGSymbolImage(SymbolImageProperties sip, boolean runMode) {
		super(sip, runMode);
	}

	// ************************************************************
	// Image color & paint
	// ************************************************************

	public synchronized void paintFigure(final Graphics gfx) {
		if (loadingImage || originalImageData == null)
			return;
		// Generate Data
		if (imageData == null) {
			dispose();
			generateSVGData();
		}
		// Create image
		if (image == null) {
			if (imageData == null)
				return;
			image = new Image(Display.getDefault(), imageData);
		}
		// Calculate areas
		if (bounds == null)
			return;
		int cropedWidth = imageData.width - (int) Math.round(scale * (leftCrop + rightCrop));
		int cropedHeight = imageData.height - (int) Math.round(scale * (bottomCrop + topCrop));
		Rectangle srcArea = new Rectangle(leftCrop, topCrop, cropedWidth, cropedHeight);
		Rectangle destArea = new Rectangle(bounds.x, bounds.y, imgDimension.width, imgDimension.height);
		if (backgroundColor != null) {
			gfx.setBackgroundColor(backgroundColor);
			gfx.fillRectangle(destArea);
		}
		// Draw graphic image
		if (image != null) {
			gfx.drawImage(image, srcArea, destArea);
		}
	}

	@Override
	public void resetData() {
		imageData = null;
	}

	private void generateSVGData() {
		// Load document if do not exist
		Document document = getDocument();
		if (document == null) {
			return;
		}
		transcoder.setColorToChange(colorToChange);
		if (!isEditMode() && !colorToChange.equals(currentColor))
			transcoder.setColor(currentColor);
		if (permutationMatrix != null)
			transcoder.setTransformMatrix(permutationMatrix.getMatrix());

		// Scale image
		java.awt.Dimension dims = transcoder.getDocumentSize();
		int imgWidth = dims.width;
		int imgHeight = dims.height;
		if (stretch) {
			if (bounds != null && !bounds.equals(0, 0, 0, 0)) {
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
		int cropedWidth = imgWidth - (int) Math.round(scale * (leftCrop + rightCrop));
		int cropedHeight = imgHeight - (int) Math.round(scale * (bottomCrop + topCrop));

		Dimension newImgDimension = new Dimension((int) Math.round(cropedWidth / scale), 
				(int) Math.round(cropedHeight / scale));
		if (imgDimension == null || newImgDimension.width != imgDimension.width
				|| newImgDimension.height != imgDimension.height)
			fireSizeChanged();
		imgDimension = newImgDimension;
	}

	// ************************************************************
	// Image size calculation
	// ************************************************************

	@Override
	public void setAbsoluteScale(double newScale) {
		super.setAbsoluteScale(newScale);
		resizeImage();
	}

	public synchronized Dimension getAutoSizedDimension() {
		// if (imgDimension == null)
		// generateSVGData();
		return imgDimension;
	}

	// ************************************************************
	// Image loading
	// ************************************************************

	public void syncLoadImage() {
		transcoder = null;
		failedToLoadDocument = false;
		loadDocument();
	}

	public void asyncLoadImage() {
		loadingImage = true;
		Display.getDefault().asyncExec(new Runnable() {
			private int maxAttempts = 6;

			public void run() {
				if (maxAttempts-- > 0) {
					try {
						Thread.sleep(100);
						syncLoadImage();
						loadingImage = false;
						fireSymbolImageLoaded();
						return;
					} catch (InterruptedException e) {
					}
				}
				loadingImage = false;
				// fireSymbolImageLoaded();
				Activator.getLogger().log(Level.WARNING,
						"ERROR in loading SVG image " + imagePath);
			}
		});
	}

	private void loadDocument() {
		transcoder = null;
		failedToLoadDocument = true;
		if (imagePath == null || imagePath.isEmpty()) {
			return;
		}
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		try {
			final InputStream inputStream = ResourceUtil.pathToInputStream(imagePath);
			svgDocument = factory.createDocument(imagePath.toOSString(), inputStream);
			transcoder = new SimpleImageTranscoder(svgDocument);
			initRenderingHints();
			BufferedImage awtImage = transcoder.getBufferedImage();
			if (awtImage != null) {
				this.originalImageData = SVGUtils.toSWT(Display.getCurrent(), awtImage);
				resetData();
			}
			failedToLoadDocument = false;
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING,
					"Error loading SVG image " + imagePath, e);
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
		transcoder.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		transcoder.getRenderingHints().put(
				RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
	}

}
