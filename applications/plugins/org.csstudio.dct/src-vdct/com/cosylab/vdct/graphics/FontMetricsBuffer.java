package com.cosylab.vdct.graphics;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.util.*;

/**
 * Font metrics buffer (using Flyweight/Singleton pattern)
 * Creation date: (25.12.2000 11:51:01)
 * @author Matej Sekoranja
 */

public class FontMetricsBuffer {

	private class FontData {
		private Font font;
		private FontMetrics fontMetrics;
		
		public FontData(Font font, FontMetrics fontMetrics) {
			this.font=font;
			this.fontMetrics=fontMetrics;
		}
		
		public Font getFont() { return font; }
		public FontMetrics getFontMetrics() { return fontMetrics; }
		
	}
	private final static int MIN_SIZE = 1;
	private final static int MAX_SIZE = 72;
	private Hashtable fonts;
	private Graphics graphics;
	private static FontMetricsBuffer instance = null;
/**
 * FontMetricsBuffer constructor comment.
 * @param g java.awt.Graphics
 */
protected FontMetricsBuffer(Graphics g) {
	this.graphics = g;
	fonts = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:28:19)
 * @param g java.awt.Graphics
 */
public static void createInstance(Graphics g) {
	if (g!=null) 
		if (instance==null) instance = new FontMetricsBuffer(g);
		else instance.setGraphics(g);
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:47:03)
 * @return java.awt.Font
 * @param fontName java.lang.String
 * @param style int
 * @param str java.lang.String
 * @param maxWidth int
 * @param maxHeight int
 */
public Font getAppropriateFont(String fontName, int style, String str, int maxWidth, int maxHeight) {
	return getAppropriateFont(fontName, style, str, maxWidth, maxHeight, MAX_SIZE);
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:47:03)
 * @return java.awt.Font
 * @param fontName java.lang.String
 * @param style int
 * @param str java.lang.String
 * @param maxWidth int
 * @param maxHeight int
 */
public Font getAppropriateFont(String fontName, int style, String str, int maxWidth, int maxHeight, int maxSize) {
  if (graphics==null) return null;
  int size = MIN_SIZE;				// find better starting point !!!
  FontData fl = null;
  FontData fd = getFontData(fontName, size, style);
  maxSize = Math.min(MAX_SIZE, maxSize);
  while ((size<=maxSize) &&
	  	 (fd.getFontMetrics().getHeight() < maxHeight) &&
	  	 (fd.getFontMetrics().stringWidth(str) < maxWidth)) {
 	size++;
 	fl = fd;
	fd = getFontData(fontName, size, style);
  }
  if (fl==null) return null;
  else return fl.getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:34:08)
 * @return java.awt.Font
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
public Font getFont(String name, int size, int style) {
	String id = getID(name, size, style);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
		Font font = new Font(name, style, size);
		if (font==null) return null;
 		fd = new FontData(font, graphics==null?null:graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd.getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:34:08)
 * @return com.cosylab.vdct.graphics.FontData
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
private FontData getFontData(String name, int size, int style) {
	if (graphics==null) return null;
	String id = getID(name, size, style);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
		Font font = new Font(name, style, size);
		if (font==null) return null;
 		fd = new FontData(font, graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd;
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:32:57)
 * @return java.awt.FontMetrics
 */
public FontMetrics getFontMetrics(Font font) {
	if (graphics==null || font==null) return null;
	String id = getID(font);
	FontData fd = (FontData)(fonts.get(id));
	if (fd==null) {
 		fd = new FontData(font, graphics.getFontMetrics(font));
		fonts.put(id, fd);
	}
	return fd.getFontMetrics();
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:35:28)
 * @return java.lang.String
 * @param font java.awt.Font
 */
private String getID(Font font) {
	return getID(font.getName(), font.getSize(), font.getStyle());
}
/**
 * Insert the method's description here.
 * Creation date: (25.12.2000 12:36:00)
 * @return java.lang.String
 * @param name java.lang.String
 * @param size int
 * @param style int
 */
private String getID(String name, int size, int style) {
	return name+"_"+size+"_"+style;		//!!!
}
/**
 * Must be initialized with createInstance(java.awt.Graphics)
 * Creation date: (25.12.2000 11:54:36)
 * @return com.cosylab.vdct.graphics.FontMetricsBuffer
 */
public static FontMetricsBuffer getInstance() {
	if (instance==null) instance = new FontMetricsBuffer(null);
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (26.12.2000 22:31:20)
 * @param g java.awt.Graphics
 */
private void setGraphics(Graphics g) {
	graphics = g;
}

public static void setInstance(FontMetricsBuffer fmb) {
	instance = fmb;
}

}
