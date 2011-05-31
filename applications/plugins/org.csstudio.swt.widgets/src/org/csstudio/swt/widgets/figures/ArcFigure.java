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

import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.ShapeWidgetIntrospector;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.swt.SWT;

/**The arc figure
 * 
 * @author Xihui Chen
 *
 */
public class ArcFigure extends Shape implements Introspectable{
	
	private boolean antiAlias = true;
//	private boolean cordFill = false;
	private int startAngle = 0;	
	private int totalAngle = 90;
	private boolean fill = false;
	

	/**
	 * @return the antiAlias
	 */
	public boolean isAntiAlias() {
		return antiAlias;
	}

	/**
	 * @return the startAngle
	 */
	public int getStartAngle() {
		return startAngle;
	}

	/**
	 * @return the totalAngle
	 */
	public int getTotalAngle() {
		return totalAngle;
	}

	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.fillArc(getClientArea().getCopy().shrink(
				(int)(getLineWidth()*1.5), (int)(getLineWidth()*1.5)), startAngle, totalAngle);
		
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		graphics.setAntialias(antiAlias ? SWT.ON : SWT.OFF);
		graphics.drawArc(getClientArea().getCopy().shrink(		
				getLineWidth(), getLineWidth()), startAngle, totalAngle);		

	}
	
	public void setAntiAlias(boolean antiAlias) {
		if(this.antiAlias == antiAlias)
			return;
		this.antiAlias = antiAlias;
		repaint();
	}
	
	public void setStartAngle(int start_angle) {
		if(this.startAngle == start_angle)
			return;
		this.startAngle = start_angle;
		repaint();
	}

	
	public void setTotalAngle(int total_angle) {
		if(this.totalAngle == total_angle)
			return;
		this.totalAngle = total_angle;
		repaint();
	}
	
	public boolean isFill(){
		return fill;
	}
	
	@Override
	public void setFill(boolean b) {
		fill = b;
		super.setFill(b);
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new ShapeWidgetIntrospector().getBeanInfo(this.getClass());
	}
}
