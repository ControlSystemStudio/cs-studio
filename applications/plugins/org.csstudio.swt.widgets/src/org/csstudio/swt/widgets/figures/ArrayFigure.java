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

import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.figures.TextFigure.H_ALIGN;
import org.csstudio.swt.widgets.figures.TextFigure.V_ALIGN;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The figure for array widget.
 * @author Xihui Chen
 *
 */
public class ArrayFigure extends Figure implements Introspectable {

	
	private static final int SCROLLBAR_WIDTH = 16;

	private static final int SPINNER_HEIGHT = 25;

	private boolean showIndexSpinner;
	
	private boolean showScrollbar;
	
	private boolean horizontal;
	
	private ScrollbarFigure scrollbar;
	
	private SpinnerFigure spinner;
	
	private Figure pane;
	
	private int index;
	
	private int arrayLength;
	
	private int visibleElementsCount;
	
	private int spinnerWidth=50;
		

	
	public ArrayFigure() {
		pane = new Figure();
		pane.setOpaque(false);
		spinner = new SpinnerFigure();
		spinner.setMin(0);
		spinner.setArrowButtonsOnLeft(true);
		spinner.setStepIncrement(1);
		spinner.setBackgroundColor(ColorConstants.white);
		spinner.getLabelFigure().setOpaque(true);
		spinner.getLabelFigure().setHorizontalAlignment(H_ALIGN.CENTER);
		spinner.getLabelFigure().setVerticalAlignment(V_ALIGN.MIDDLE);
		scrollbar = new ScrollbarFigure();
		scrollbar.setMinimum(0);
		scrollbar.setStepIncrement(1);
		scrollbar.setFormatPattern("####");//$NON-NLS-1$
		Border loweredBorder = new SchemeBorder(SchemeBorder.SCHEMES.LOWERED);
		pane.setBorder(loweredBorder);
		spinner.setBorder(loweredBorder);
		add(spinner);
		add(pane);
		add(scrollbar);
		
		setArrayLength(100);
		setVisibleElementsCount(10);
		setHorizontal(false);
		
	}
	/**
	 * Add an index change listener.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addIndexChangeListener(final IManualValueChangeListener listener) {
		spinner.addManualValueChangeListener(listener);
		scrollbar.addManualValueChangeListener(listener);
	}
	
	
	
	/**
	 * @return the arrayLength
	 */
	public int getArrayLength() {
		return arrayLength;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.csstudio.swt.widgets.introspection.Introspectable#getBeanInfo()
	 */
	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}
	/**
	 * @return the content pane to hold array element widgets.
	 */
	public IFigure getContentPane(){
		return pane;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @return the spinnerWidth
	 */
	public int getSpinnerWidth() {
		return spinnerWidth;
	}
	/**
	 * @return the visibleElementsCount
	 */
	public int getVisibleElementsCount() {
		return visibleElementsCount;
	}
	/**
	 * @return the horizontal
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
	/**
	 * @return the showIndexSpinner
	 */
	public boolean isShowIndexSpinner() {
		return showIndexSpinner;
	}
	/**
	 * @return the showScrollbar
	 */
	public boolean isShowScrollbar() {
		return showScrollbar;
	}
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		if(spinner.isVisible()){
			spinner.setBounds(new Rectangle(clientArea.x, clientArea.y, spinnerWidth, SPINNER_HEIGHT));
			clientArea.x +=spinnerWidth+1;
			clientArea.width-=spinnerWidth+1;			
		}
		if(horizontal){
			if(scrollbar.isVisible()){
				scrollbar.setBounds(new Rectangle(clientArea.x, 
						clientArea.y+clientArea.height-SCROLLBAR_WIDTH, clientArea.width, SCROLLBAR_WIDTH));
				clientArea.height-=SCROLLBAR_WIDTH;
			}			
		}else{
			if(scrollbar.isVisible()){
				scrollbar.setBounds(new Rectangle(clientArea.x+clientArea.width-SCROLLBAR_WIDTH, 
						clientArea.y, SCROLLBAR_WIDTH, clientArea.height));
				clientArea.width-=SCROLLBAR_WIDTH;
			}
		}
		pane.setBounds(clientArea);
	}
	/**
	 * @param arrayLength the arrayLength to set
	 */
	public void setArrayLength(int arrayLength) {
		this.arrayLength = arrayLength;
		scrollbar.setMaximum(arrayLength);
		scrollbar.setPageIncrement(arrayLength > 10? arrayLength/10 : 1);
		spinner.setMax(arrayLength);
	}
	/**
	 * @param horizontal the horizontal to set
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		scrollbar.setHorizontal(horizontal);
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
		spinner.setValue(index);
		scrollbar.setValue(index);
	}
	/**
	 * @param showIndexSpinner the showIndexSpinner to set
	 */
	public void setShowIndexSpinner(boolean showIndexSpinner) {
		this.showIndexSpinner = showIndexSpinner;
		spinner.setVisible(showIndexSpinner);
	}
	/**
	 * @param showScrollbar the showScrollbar to set
	 */
	public void setShowScrollbar(boolean showScrollbar) {
		this.showScrollbar = showScrollbar;
		scrollbar.setVisible(showScrollbar);
	}
	/**
	 * @param spinnerWidth the spinnerWidth to set
	 */
	public void setSpinnerWidth(int spinnerWidth) {
		this.spinnerWidth = spinnerWidth;
		revalidate();
	}
	
	
	/**
	 * @param visibleElementsCount the visibleElementsCount to set
	 */
	public void setVisibleElementsCount(int visibleElementsCount) {
		this.visibleElementsCount = visibleElementsCount;
		scrollbar.setExtent(visibleElementsCount);
	}

}
