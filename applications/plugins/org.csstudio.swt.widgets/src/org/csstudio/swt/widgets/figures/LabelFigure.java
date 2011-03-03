/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A text figure for single line text display.
 *
 * @author Xihui Chen
 *
 */
public class LabelFigure extends Figure implements Introspectable{	
	
	
	protected V_ALIGN verticalAlignment = V_ALIGN.TOP;
	protected H_ALIGN horizontalAlignment = H_ALIGN.LEFT;
	
	protected boolean runMode;
	
	protected boolean selectable = true;
	private String text = ""; //$NON-NLS-1$
	private Point textLocation;
	private Dimension textSize;

	public LabelFigure() {
		this(false);
	}
	
	/** 
	 * Constructor
	 * 
	 * @param runMode true if this figure is in run mode; false if in edit mode.
	 */
	public LabelFigure(boolean runMode) {
		this.runMode = runMode;		
	}

	protected void calculateTextLocation() {
		Rectangle clientArea = getClientArea();		
		Dimension textSize = getTextSize();
			int x=0;
			if(clientArea.width > textSize.width){
				
				switch (horizontalAlignment) {
				case CENTER:
					x = (clientArea.width - textSize.width)/2;
					break;
				case RIGHT:
					x = clientArea.width - textSize.width;
					break;
				case LEFT:
				default:					
					break;
				}
			}
			
			int y=0;
			if(clientArea.height > textSize.height){
				switch (verticalAlignment) {
				case MIDDLE:
					y = (clientArea.height - textSize.height)/2;
					break;
				case BOTTOM:
					y =clientArea.height - textSize.height;
					break;
				case TOP:
				default:
					break;
				}
			}
			
			textLocation = new Point(x, y);
	}

	protected Dimension calculateTextSize() {
		return TextUtilities.INSTANCE.getTextExtents(text, getFont());
	}
	
	protected void clearLocation(){
		textLocation = null;
		textSize = null;
	}

	@Override
	public boolean containsPoint(int x, int y) {
		if(runMode && !selectable)
			return false;
		else
			return super.containsPoint(x, y);
	}

	
	public Dimension getAutoSizeDimension(){
		return getPreferredSize().getCopy().expand(
				getInsets().getWidth(), getInsets().getHeight());
	}
	
	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

	/**
	 * @return the h_alignment
	 */
	public H_ALIGN getHorizontalAlignment() {
		return horizontalAlignment;
	}
	

	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		return getTextSize();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return getTextSize();

	}

	public String getText() {
		return text;
	}	

	
	protected Point getTextLocation() {
		if (textLocation != null)
			return textLocation;
		calculateTextLocation();
		return textLocation;
	}	
	
	protected Dimension getTextSize() {
		if (textSize == null)
			textSize = calculateTextSize();
		return textSize;
	}
	
	/**
	 * @return the v_alignment
	 */
	public V_ALIGN getVerticalAlignment() {
		return verticalAlignment;
	}

	@Override
	public void invalidate() {
		clearLocation();
		super.invalidate();
	}
	
	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}
	
	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}	
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if(text.length() == 0)
			return;
		Rectangle clientArea = getClientArea();
		graphics.translate(clientArea.x, clientArea.y);
		graphics.drawText(text, getTextLocation());
		graphics.translate(-clientArea.x, -clientArea.y);		
	}
	
	public void setHorizontalAlignment(H_ALIGN hAlignment) {
		if(this.horizontalAlignment == hAlignment)
			return;
		horizontalAlignment = hAlignment;
		revalidate();
		repaint();
	}
	
	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runMode = runMode;
	}
	
	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}	
	
	
	public void setText(String s) {
		// "text" will never be null.
		if (s == null)
			s = "";//$NON-NLS-1$
		if (text.equals(s))
			return;
		text = s;
		clearLocation();
		repaint();
	}

	public void setVerticalAlignment(V_ALIGN vAlignment) {
		if(this.verticalAlignment == vAlignment)
			return;
		verticalAlignment = vAlignment;
		revalidate();
		repaint();
	}

	public enum H_ALIGN{
		LEFT("Left"),
		CENTER("Center"),
		RIGHT("Right");
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(H_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		String descripion;
		H_ALIGN(String description){
			this.descripion = description;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}

	public enum V_ALIGN{
		TOP("Top"),
		MIDDLE("Middle"),
		BOTTOM("Bottom");
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(V_ALIGN h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		String descripion;
		V_ALIGN(String description){
			this.descripion = description;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}

}


