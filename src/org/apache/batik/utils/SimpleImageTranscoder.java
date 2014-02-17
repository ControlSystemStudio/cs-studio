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

import java.awt.Dimension;
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
import org.apache.batik.dom.svg.SVGDOMImplementation;
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
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGSVGElement;

public class SimpleImageTranscoder extends SVGAbstractTranscoder {

	private BufferedImage bufferedImage;
	private Document originalDocument, document;
	private int canvasWidth = -1, canvasHeight = -1;
	private Rectangle2D canvasAOI;
	private RenderingHints renderingHints;
	private Color colorToChange, appliedColor, colorToApply;
	private double[][] matrix;

	public SimpleImageTranscoder(Document document) {
		this.document = document;
		this.originalDocument = document;
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
		if (colorToApply != null) {
			if (appliedColor == null) {
				appliedColor = colorToChange != null ? colorToChange
						: new Color(Display.getCurrent(), (int) 0, (int) 0, (int) 0);
			}
			changeColor(document, appliedColor, colorToApply);
			appliedColor = colorToApply;
		}
		try {
			if (canvasWidth > 0) {
				addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(canvasWidth));
			} else {
				removeTranscodingHint(ImageTranscoder.KEY_WIDTH);
			}
			if (canvasHeight > 0) {
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
		int w = (int) (this.width + 0.5);
		int h = (int) (this.height + 0.5);
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
		return colorToApply;
	}

	public void setColor(Color newColor) {
		if (newColor == null || (this.colorToApply != null && newColor.equals(this.colorToApply)))
			return;
		this.colorToApply = newColor;
		contentChanged();
	}
	
	public void setColorToChange(Color newColor) {
		if (newColor == null || (this.colorToChange != null && newColor.equals(this.colorToChange)))
			return;
		this.colorToChange = newColor;
	}
	
	public double[][] getTransformMatrix() {
		return matrix;
	}
	
	public void setTransformMatrix(double[][] newMatrix) {
		if (newMatrix == null)
			return;
		this.matrix = newMatrix;
		this.document = applyMatrix(matrix);
		// Transformed document is based on original => reset color
		this.appliedColor = null;
		contentChanged();
	}
	
	public Dimension getDocumentSize() {
		SVGSVGElement svgElmt = ((SVGOMDocument) document).getRootElement();
		double width = svgElmt.getWidth().getBaseVal().getValue();
		double height = svgElmt.getHeight().getBaseVal().getValue();
		return new Dimension((int) Math.round(width), (int) Math.round(height));
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
		Pattern fillPattern = Pattern.compile("(?i)" + CSSConstants.CSS_FILL_PROPERTY + ":" + svgOldColor);
		Pattern strokePattern = Pattern.compile("(?i)" + CSSConstants.CSS_STROKE_PROPERTY + ":" + svgOldColor);
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
	
	private Document applyMatrix(double[][] matrix) {
		// creation of the SVG document
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		final Document newDocument = impl.createDocument(svgNS, "svg", null);

		// get the root element (the 'svg' element).
		Element svgRoot = newDocument.getDocumentElement();
		
		// get the original document size
		SVGSVGElement svgElmt = ((SVGOMDocument) originalDocument).getRootElement();
		double width = svgElmt.getWidth().getBaseVal().getValue();
		double height = svgElmt.getHeight().getBaseVal().getValue();

		// current Transformation Matrix
		double[][] CTM = { 
				{ matrix[0][0], matrix[0][1], 0 }, 
				{ matrix[1][0], matrix[1][1], 0 }, 
				{ 0, 0, 1 } };
		
		// apply permutation to viewBox corner points
		double[] a = transformP(0.0, 0.0, 1.0, CTM);
		double[] b = transformP(width, 0.0, 1.0, CTM);
		double[] c = transformP(width, height, 1.0, CTM);
		double[] d = transformP(0.0, height, 1.0, CTM);

		// find new points
		double minX = findMin(a[0], b[0], c[0], d[0]);
		double minY = findMin(a[1], b[1], c[1], d[1]);
		double maxX = findMax(a[0], b[0], c[0], d[0]);
		double maxY = findMax(a[1], b[1], c[1], d[1]);
		double newWidth = maxX - minX;
		double newHeight = maxY - minY;

		// set the width and height attributes on the root 'svg' element.
		svgRoot.setAttributeNS(null, "width", String.valueOf(newWidth));
		svgRoot.setAttributeNS(null, "height", String.valueOf(newHeight));
		String vbs = minX + " " + minY + " " + newWidth + " " + newHeight;
		svgRoot.setAttributeNS(null, "viewBox", vbs);
		svgRoot.setAttributeNS(null, "preserveAspectRatio", "none");
		
		// Create the transform matrix
		StringBuilder sb = new StringBuilder();
		// a c e
		// b d f
		// 0 0 1
		sb.append("matrix(");
		sb.append(CTM[0][0] + ",");
		sb.append(CTM[1][0] + ",");
		sb.append(CTM[0][1] + ",");
		sb.append(CTM[1][1] + ",");
		sb.append(CTM[0][2] + ",");
		sb.append(CTM[1][2] + ")");
		Element graphic = newDocument.createElementNS(svgNS, "g");
		graphic.setAttributeNS(null, "transform", sb.toString());

		// Attach the transform to the root 'svg' element.
		Node copiedRoot = newDocument.importNode(originalDocument.getDocumentElement(), true);
		graphic.appendChild(copiedRoot);
		svgRoot.appendChild(graphic);

		// TODO: remove this part => debug
		// Write to file
//		try {
//			TransformerFactory factory = TransformerFactory.newInstance();
//			Transformer transformer = factory.newTransformer();
//			FileWriter writer = new FileWriter("/home/ITER/arnaudf/perso/testX.svg");
//			Source source = new DOMSource(newDocument);
//			Result result = new StreamResult(writer);
//			transformer.transform(source, result);
//			writer.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return newDocument;
	}
	
	// apply transformation to point { x, y, z } (affine transformation)
	private double[] transformP(double x, double y, double z, double[][] matrix) {
		double[] p = { x, y, z };
		double[] pp = new double[3];
		for (int a = 0; a < 3; a++)
			for (int b = 0; b < 3; b++)
				pp[a] += matrix[a][b] * p[b];
		return pp;
	}
	
	private double findMax(double a, double b, double c, double d) {
		double result = Math.max(a, b);
		result = Math.max(result, c);
		result = Math.max(result, d);
		return result;
	}
	
	private double findMin(double a, double b, double c, double d) {
		double result = Math.min(a, b);
		result = Math.min(result, c);
		result = Math.min(result, d);
		return result;
	}
}
