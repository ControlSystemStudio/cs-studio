/**
 * Copyright (c) 2008 Borland Software Corporation
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dmitry Stadnik - initial API and implementation
 */
package org.apache.batik.utils;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.GenericCDATASection;
import org.apache.batik.dom.GenericText;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGStylableElement;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.CSSConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleImageTranscoder extends SVGAbstractTranscoder {

	private BufferedImage bufferedImage;
	private Document document;
	private int canvasWidth = -1, canvasHeight = -1;
	private Rectangle2D canvasAOI;
	private RenderingHints renderingHints;
	private Color color;
	private Color oldColor;

	public SimpleImageTranscoder(Document document) {
		this.document = document;
		renderingHints = new RenderingHints(null);
	}

	public final Document getDocument() {
		return document;
	}

	public final RenderingHints getRenderingHints() {
		return renderingHints;
	}

	public final int getCanvasWidth() {
		return canvasWidth;
	}

	public final int getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasSize(int width, int height) {
		if (this.canvasWidth == width && this.canvasHeight == height) {
			return;
		}
		this.canvasWidth = width;
		this.canvasHeight = height;
		contentChanged();
	}

	public final Rectangle2D getCanvasAreaOfInterest() {
		if (canvasAOI == null) {
			return null;
		}
		Rectangle2D result = new Rectangle2D.Float();
		result.setRect(canvasAOI);
		return result;
	}

	public void setCanvasAreaOfInterest(Rectangle2D value) {
		if (value == null) {
			if (canvasAOI == null) {
				return;
			}
			canvasAOI = null;
			contentChanged();
			return;
		}
		if (value.equals(canvasAOI)) {
			return;
		}
		canvasAOI = new Rectangle2D.Float();
		canvasAOI.setRect(value);
		contentChanged();
	}

	/**
	 * Call before querying for CSS properties. If document has CSS engine installed returns null. Client is responsible to
	 * dispose bridge context if it was returned by this method.
	 */
	public BridgeContext initCSSEngine() {
		if (this.document == null) {
			return null;
		}
		SVGOMDocument sd = (SVGOMDocument) this.document;
		if (sd.getCSSEngine() != null) {
			return null;
		}
		class BridgeContextEx extends BridgeContext {

			public BridgeContextEx() {
				super(SimpleImageTranscoder.this.userAgent);
				BridgeContextEx.this.setDocument(SimpleImageTranscoder.this.document);
				BridgeContextEx.this.initializeDocument(SimpleImageTranscoder.this.document);
			}
		}
		return new BridgeContextEx();
	}

	public void contentChanged() {
		bufferedImage = null;
	}

	private void updateImage() {
		if (document == null) {
			return;
		}
		if (color != null) {
			if (oldColor == null) {
				// only black is changed
				oldColor = new Color(Display.getCurrent(), (int) 0, (int) 0, (int) 0);
			}
			changeColor(document, oldColor, color);
			oldColor = color;
		}
		try {
			if (canvasWidth >= 0) {
				addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(canvasWidth));
			} else {
				removeTranscodingHint(ImageTranscoder.KEY_WIDTH);
			}
			if (canvasHeight >= 0) {
				addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(canvasHeight));
			} else {
				removeTranscodingHint(ImageTranscoder.KEY_HEIGHT);
			}
			if (canvasAOI != null) {
				addTranscodingHint(ImageTranscoder.KEY_AOI, canvasAOI);
			} else {
				removeTranscodingHint(ImageTranscoder.KEY_AOI);
			}
			transcode(new TranscoderInput(document), new TranscoderOutput());
		} catch (TranscoderException e) {
		}
	}

	protected void transcode(Document document, String uri, TranscoderOutput output) throws TranscoderException {
		super.transcode(document, uri, output);
		int w = (int) (width + 0.5);
		int h = (int) (height + 0.5);
		ImageRenderer renderer = createImageRenderer();
		renderer.updateOffScreen(w, h);
		// curTxf.translate(0.5, 0.5);
		renderer.setTransform(curTxf);
		renderer.setTree(this.root);
		this.root = null; // We're done with it...
		try {
			Shape raoi = new Rectangle2D.Float(0, 0, width, height);
			// Warning: the renderer's AOI must be in user space
			renderer.repaint(curTxf.createInverse().createTransformedShape(raoi));
			bufferedImage = renderer.getOffScreen();
		} catch (Exception ex) {
			throw new TranscoderException(ex);
		}
	}

	protected ImageRenderer createImageRenderer() {
		StaticRenderer renderer = new StaticRenderer();
		renderer.getRenderingHints().add(renderingHints);
		return renderer;
	}

	public final BufferedImage getBufferedImage() {
		if (bufferedImage == null) {
			updateImage();
		}
		return bufferedImage;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (color == null || (this.color != null && color.equals(this.color)))
			return;
		this.oldColor = this.color;
		this.color = color;
		contentChanged();
	}
	
	public Document copyDocument() {
		if (document == null)
			return null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Node originalRoot = document.getDocumentElement();
			Document copiedDocument = db.newDocument();
			Node copiedRoot = copiedDocument.importNode(originalRoot, true);
			copiedDocument.appendChild(copiedRoot);
			return copiedDocument;
		} catch (ParserConfigurationException e) {
		}
		return null;
	}

	private void changeColor(Document doc, Color oldColor, Color newColor) {
		Matcher matcher = null;
		String svgOldColor = toHexString(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue());
		String svgNewColor = toHexString(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
		Pattern fillPattern = Pattern.compile(CSSConstants.CSS_FILL_PROPERTY + ":" + svgOldColor);
		Pattern strokePattern = Pattern.compile(CSSConstants.CSS_STROKE_PROPERTY + ":" + svgOldColor);
		String fillReplace = CSSConstants.CSS_FILL_PROPERTY + ":" + svgNewColor;
		String strokeReplace = CSSConstants.CSS_STROKE_PROPERTY + ":" + svgNewColor;
		
		// Search for global style element <style type="text/css"></style>
		NodeList styleList = doc.getElementsByTagName("style");
		for (int i = 0; i < styleList.getLength(); i++) {
			Element style = (Element) styleList.item(i);
			NodeList childList = style.getChildNodes();
			if (childList != null) {
				for (int j = 0; j < childList.getLength(); j++) {
					Node child = childList.item(j);
					if (child instanceof GenericText
							|| child instanceof GenericCDATASection) {
						CharacterData cdata = (CharacterData) child;
						String data = cdata.getData();
						matcher = fillPattern.matcher(data);
						data = matcher.replaceAll(fillReplace);
						matcher = strokePattern.matcher(data);
						data = matcher.replaceAll(strokeReplace);
						cdata.setData(data);
					}
				}
			}
		}
		recursiveCC(doc.getDocumentElement(), fillPattern, strokePattern,
				fillReplace, strokeReplace);
	}
	
	private void recursiveCC(Element elmt, Pattern fillPattern,
			Pattern strokePattern, String fillReplace, String strokeReplace) {
		if (elmt == null)
			return;
		Matcher matcher = null;
		NodeList styleList = elmt.getChildNodes();
		if (styleList != null) {
			for (int i = 0; i < styleList.getLength(); i++) {
				Node child = styleList.item(i);
				if (child instanceof SVGStylableElement) {
					recursiveCC((Element) child, fillPattern, strokePattern,
							fillReplace, strokeReplace);
				}
			}
		}
		if (elmt instanceof SVGStylableElement) {
			String style = elmt.getAttribute("style");
			matcher = fillPattern.matcher(style);
			style = matcher.replaceAll(fillReplace);
			matcher = strokePattern.matcher(style);
			style = matcher.replaceAll(strokeReplace);
			elmt.setAttribute("style", style);
		}
	}
	
	private String toHexString(int r, int g, int b) {
		return "#" + toSVGHexValue(r) + toSVGHexValue(g) + toSVGHexValue(b);
	}

	private String toSVGHexValue(int number) {
		StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
		while (builder.length() < 2) {
			builder.append("0");
		}
		return builder.toString().toUpperCase();
	}
}
