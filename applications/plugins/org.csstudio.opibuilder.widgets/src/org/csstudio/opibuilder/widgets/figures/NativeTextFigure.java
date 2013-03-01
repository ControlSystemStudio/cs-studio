/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**Figure for a native text widget.
 * @author Xihui Chen
 *
 */
public class NativeTextFigure extends AbstractSWTWidgetFigure<Text> implements ITextFigure {

	private Text text;
	
	private boolean readOnly;
	
	public NativeTextFigure(AbstractBaseEditPart editPart, int style) {
		super(editPart, style);		
	}
	
	@Override
	protected Text createSWTWidget(Composite parent, int style) {
		text= new Text(parent, style);
		readOnly = (style & SWT.READ_ONLY)!=0;
		return text;
	}
	
	public Dimension getAutoSizeDimension(){
		Point preferredSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Dimension(preferredSize.x + getInsets().getWidth(), 
				preferredSize.y + getInsets().getHeight());
	}
	
	@Override
	public void setEnabled(boolean value) {		
			super.setEnabled(value);
			if(runmode && getSWTWidget() != null && !getSWTWidget().isDisposed()){
				//Its parent should be always enabled so the text can be enabled.
				text.getParent().setEnabled(true);
				text.setEnabled(true);
				if(!readOnly)
					getSWTWidget().setEditable(value);
			}
			
	}

	@Override
	public String getText() {
		return text.getText();
	}
	
}
