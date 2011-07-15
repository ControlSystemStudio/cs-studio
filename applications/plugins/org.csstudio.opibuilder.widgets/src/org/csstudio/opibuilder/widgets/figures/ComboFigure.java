/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**Figure for a Combo widget.
 * @author Xihui Chen
 *
 */
public class ComboFigure extends AbstractSWTWidgetFigure {

	
	Triangle selector;
	private final static Color GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_GRAY);
	private final static Color DARK_GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_DARK_GRAY);

	private static final int SELECTOR_WIDTH = 12;

	private Combo combo;
	
	public ComboFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setVisible(false);
		combo.moveAbove(null);
		selector = new Triangle();	
		selector.setDirection(PositionConstants.SOUTH);
		selector.setFill(true);
		add(selector);
		
	}
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
		selector.setBounds(new Rectangle(clientArea.x + clientArea.width - SELECTOR_WIDTH -2,
				clientArea.y, SELECTOR_WIDTH, clientArea.height));
	}

	@Override
	protected void paintClientArea(Graphics graphics) {			
		//draw this so that it can be seen in the outline view
		if(!runmode){
			Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(clientArea);
			graphics.setForegroundColor(DARK_GRAY_COLOR);
			graphics.drawRectangle(
					new Rectangle(clientArea.getLocation(), clientArea.getSize().shrink(1, 1)));
		}
		super.paintClientArea(graphics);	
	}

	@Override
	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		relocateWidget();
	}

	public void setText(String text) {
		combo.setText(text);
	}
	
	/**
	 * @return the SWT combo in the combo figure.
	 */
	public Combo getCombo() {
		return combo;
	}
	
	public Dimension getAutoSizeDimension(){
		return new Dimension(getBounds().width, 
				combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + getInsets().getHeight());
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);
		selector.setVisible(!runMode);
	}



	@Override
	public Composite getSWTWidget() {
		return combo;
	}	
	
	@Override
	public void dispose() {
		super.dispose();
		UIBundlingThread.getInstance().addRunnable(
				combo.getDisplay(), new Runnable() {
			
			public void run() {
				combo.setMenu(null);
				combo.dispose();
				combo = null;				
			}
		});
		
	}
	
}
