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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Font;

/**
 * A text figure which is able to wrap text automatically.  *
 *
 * @author Xihui Chen
 *
 */
public class WrappableTextFigure extends TextFigure{
	
	/** The inner TextFlow **/
	private TextFlow textFlow;
	private FlowPage flowPage;	

	public WrappableTextFigure() {
		this(false);
	}
	
	/** 
	 * Creates a new StickyNoteFigure with a MarginBorder that is the given size and a
	 * FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize the size of the MarginBorder
	 */
	public WrappableTextFigure(boolean runMode) {
		super(runMode);
		//setLayoutManager(new StackLayout());
		//add(scrollPane);
		flowPage = new FlowPage();
		textFlow = new TextFlow(""){
			@Override
			public void setFont(Font f) {
				super.setFont(f);
				revalidateBidi(this);
				repaint();
			}
		};
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
		add(flowPage);
	}
	
	@Override
	public boolean containsPoint(int x, int y) {
		if(runMode && !selectable)
			return false;
		else
			return super.containsPoint(x, y);
	}

	public Dimension getAutoSizeDimension(){
		return flowPage.getPreferredSize().getCopy().expand(
				getInsets().getWidth(), getInsets().getHeight());
	}



	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}
	

	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea();
		Dimension preferedSize = flowPage.getPreferredSize();
			int x=clientArea.x;
			if(clientArea.width > preferedSize.width){
				
				switch (horizontalAlignment) {
				case CENTER:
					x = clientArea.x + (clientArea.width - preferedSize.width)/2;
					break;
				case RIGHT:
					x = clientArea.x + clientArea.width - preferedSize.width;
					break;
				case LEFT:
				default:
					x=clientArea.x;
					break;
				}
			}else{
				preferedSize = flowPage.getPreferredSize(clientArea.width, -1);
			}
			int y=clientArea.y;
			if(clientArea.height > preferedSize.height){
				switch (verticalAlignment) {
				case MIDDLE:
					y = clientArea.y + (clientArea.height - preferedSize.height)/2;
					break;
				case BOTTOM:
					y = clientArea.y + clientArea.height - preferedSize.height;
					break;
				case TOP:
				default:
					y=clientArea.y;
					break;
				}
			}
			
			flowPage.setBounds(new Rectangle(x, y, 
					clientArea.width - (x - clientArea.x), clientArea.height - (y - clientArea.y)));
	}
	
	
	@Override
	public void setFont(Font f) {
		flowPage.setFont(f);
		textFlow.setFont(f);
		super.setFont(f);
		revalidate();			
	}
	
	
	@Override
	public void setOpaque(boolean opaque) {		
		textFlow.setOpaque(opaque);
		super.setOpaque(opaque);
	}
	
	
	
		

	
	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText the new text value.
	 */
	public void setText(String newText) {		
		textFlow.setText(newText);
		revalidate();

	}


	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

	

}
