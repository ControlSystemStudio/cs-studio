/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.ui.util.CustomMediaFactory;
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
public class ComboFigure extends AbstractSWTWidgetFigure<Combo> {

	
	Triangle selector;
	private final static Color GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(240,240,240);
	private final static Color DARK_GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_DARK_GRAY);

	private static final int SELECTOR_WIDTH = 8;

	private Combo combo;
	
	public ComboFigure(AbstractBaseEditPart editPart) {
		super(editPart);		
		if(!runmode){
			selector = new Triangle();	
			selector.setBackgroundColor(DARK_GRAY_COLOR);
			selector.setDirection(PositionConstants.SOUTH);
			selector.setFill(true);
			add(selector);
		}				
		
	}
	
	@Override
	protected Combo createSWTWidget(Composite parent, int style) {
		combo= new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		return combo;
	}
	
	@Override
	protected void layout() {
		super.layout();
		if(!runmode){
			Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
			selector.setBounds(new Rectangle(clientArea.x + clientArea.width - SELECTOR_WIDTH -2,
					clientArea.y, SELECTOR_WIDTH, clientArea.height));
		}
	}
	
	@Override
	protected void paintOutlineFigure(Graphics graphics) {
		// draw this so that it can be seen in the outline view
		if (!runmode) {
			Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(clientArea);
			graphics.setForegroundColor(DARK_GRAY_COLOR);
			graphics.drawRectangle(new Rectangle(clientArea.getLocation(),
					clientArea.getSize().shrink(1, 1)));
		}
	}
	
	public void setText(String text) {
		combo.setText(text);
	}
	
	public Dimension getAutoSizeDimension(){
		return new Dimension(getBounds().width, 
				combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + getInsets().getHeight());
	}	
	
}
