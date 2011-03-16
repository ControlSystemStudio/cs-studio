/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;

import java.util.Map;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

/**The abstract figure for all SWT widget based figure. Note that there are still some unresolved
 * issues regarding using SWT native widget in draw2D figure, for example the order of the widget 
 * is always on top and it it always floats above other figures.
 * @author Xihui Chen
 *
 */
public abstract class AbstractSWTWidgetFigure extends Figure {

	protected boolean runmode;
	private boolean updateFlag;
	private UpdateListener updateManagerListener;
	private AncestorListener ancestorListener;
	protected AbstractContainerModel parentModel;
	protected Composite composite;

	
	public AbstractSWTWidgetFigure(Composite composite, AbstractContainerModel parentModel) {
		super();
		this.composite = composite;
		this.parentModel = parentModel;
		//the widget should has the same relative position as its parent container.
		ancestorListener = new AncestorListener.Stub(){
			public void ancestorMoved(org.eclipse.draw2d.IFigure arg0) {
				relocateWidget();
				updateWidgetVisibility();
			}			
		};			
		addAncestorListener(ancestorListener);		
	}

	@Override
	protected void layout() {
		super.layout();
		relocateWidget();
	}
	
	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		relocateWidget();
	}
	
	abstract public Composite getSWTWidget();
	
	public Composite getComposite() {
		return composite;
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if(getSWTWidget() != null)
			getSWTWidget().setEnabled(runmode && value);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		updateWidgetVisibility();		
	}

	/**
	 * 
	 */
	private void updateWidgetVisibility() {
		if(getSWTWidget() != null)
			getSWTWidget().setVisible(isVisible() && isShowing());
	}

	@Override
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		if(getSWTWidget() != null)
			getSWTWidget().setForeground(fg);
	}

	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		if(getSWTWidget() != null)
			getSWTWidget().setBackground(bg);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		repaintWidget();		
		super.paintClientArea(graphics);
	}
	
	/**
	 * 
	 */
	protected void repaintWidget() {
		updateWidgetVisibility();	
		//the widget should has the same visibility as its parent container.
		//the update listener can only be added when the figure was painted, because
		//the update manager is not assigned until the figure was painted for the first time.
		if(!updateFlag){
			updateFlag = true;
			if(!(parentModel instanceof DisplayModel)){
				updateManagerListener = new UpdateListener(){				
					@SuppressWarnings("unchecked")
					public void notifyPainting(Rectangle damage, Map dirtyRegions) {
						if(getSWTWidget() != null && 
								(getSWTWidget().isVisible() != (isVisible() && isShowing())))
							getSWTWidget().setVisible(isVisible() && isShowing());				
					}
					public void notifyValidating() {}
					
				};
				getUpdateManager().addUpdateListener(updateManagerListener);
			}
		}		
	}

	/**
	 * relocate the widget so it follows the figure position.
	 */
	protected void relocateWidget() {
		if(getSWTWidget() != null){
			Rectangle rect = getClientArea().getCopy();		
			translateToAbsolute(rect);
			org.eclipse.swt.graphics.Rectangle trim = getSWTWidget().computeTrim(0, 0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
			getSWTWidget().setBounds(rect.x, rect.y, rect.width, rect.height);		
		}
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		this.runmode = runMode;
		if(getSWTWidget() != null)
			getSWTWidget().setEnabled(runMode);
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if(getSWTWidget() != null)
			getSWTWidget().setFont(f);
	}

	public void dispose() {
		if(updateFlag && updateManagerListener != null)
			getUpdateManager().removeUpdateListener(updateManagerListener);
		updateFlag =false;
		removeAncestorListener(ancestorListener);		
	}

}
