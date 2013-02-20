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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.swt.widgets.introspection.LabelWidgetIntrospector;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
/**
 * Figure for a check box.
 *
 * @author Xihui Chen
 *
 */
public class CheckBoxFigure extends Toggle implements Introspectable, ITextFigure{



	private static final int BOX_SIZE = 14;

	private static final int GAP = 4;

	protected long value = 0;

	protected int bit = -1;

	protected boolean boolValue = false;


		/**
	 * Listeners that react on manual boolean value change events.
	 */
	private List<IManualValueChangeListener> boolControlListeners =
		new ArrayList<IManualValueChangeListener>();

	private boolean runMode;
	
	private String text;

	private Boolean support3d;

	private Color selectedColor=ColorConstants.darkGray;;

	public CheckBoxFigure(final boolean runMode) {
		this.runMode = runMode;
		final BoxFigure boxFigure = new BoxFigure();
		setContents(boxFigure);	
		if(!runMode)
			setEventHandler(null);
		else
			setCursor(Cursors.HAND);
		addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				if(runMode){
					fireManualValueChange(!boolValue);
				}
			}
		});
		
		if (runMode && !Activator.isRAP()){
			addMouseMotionListener(new MouseMotionListener.Stub() {

				@Override
				public void mouseEntered(MouseEvent me) {
					Color backColor = getBackgroundColor();
					RGB darkColor = GraphicsUtil.mixColors(
							backColor.getRGB(), new RGB(94, 151, 230), 0.7);
					boxFigure.setBackgroundColor(CustomMediaFactory.getInstance()
							.getColor(darkColor));
				}

				@Override
				public void mouseExited(MouseEvent me) {
					boxFigure.setBackgroundColor(getBackgroundColor());
				}
			});
		}
	}

	/**add a boolean control listener which will be executed when pressed or released
	 * @param listener the listener to add
	 */
	public void addManualValueChangeListener(final IManualValueChangeListener listener){
		boolControlListeners.add(listener);
	}

	public void removeManualValueChangeListener(final IManualValueChangeListener listener){
		if(boolControlListeners.contains(listener))
			boolControlListeners.remove(listener);
	}


	/**
	 * Inform all boolean control listeners, that the manual value has changed.
	 *
	 * @param newManualValue
	 *            the new manual value
	 */
	protected void fireManualValueChange(final boolean newManualValue) {
		boolValue = newManualValue;
		updateValue();
		for (IManualValueChangeListener l : boolControlListeners) {
			l.manualValueChanged(value);
		}

	}

	/**
	 * @return the bit
	 */
	public int getBit() {
		return bit;
	}

	/**
	 * @return the boolValue
	 */
	public boolean getBoolValue() {
		return boolValue;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}
	
	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @return the runMode
	 */
	public boolean isRunMode() {
		return runMode;
	}
	
	

	/**
	 * @param bit the bit to set
	 */
	public void setBit(int bit) {
		if(this.bit == bit)
			return;
		this.bit = bit;
		updateBoolValue();
	}

	public void setBoolValue(boolean boolValue) {
		if(this.boolValue == boolValue)
			return;
		this.boolValue = boolValue;
		updateValue();
	}
	
	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		repaint();
	}

	
	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
		repaint();
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		setValue((long)value);
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(long value) {
		if(this.value == value)
			return;
		this.value = value;
		updateBoolValue();
		repaint();
	}

	/**
	 * update the boolValue from value and bit.
	 * All the boolValue based behavior changes should be implemented here by inheritance.
	 */
	protected void updateBoolValue() {
		//get boolValue
		if(bit <0 )
			boolValue = (this.value != 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(this.value).toCharArray();
			if(bit >= binArray.length)
				boolValue = false;
			else {
				boolValue = (binArray[binArray.length - 1 - bit] == '1');
			}
		}
		repaint();
	}


	/**
	 * update the value from boolValue
	 */
	@SuppressWarnings("nls")
    private void updateValue(){
		//get boolValue
		if(bit < 0)
			setValue(boolValue ? 1 : 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(value).toCharArray();
			if(bit >= 64 ) {
			    // Log with exception to obtain call stack
			    Activator.getLogger().log(Level.WARNING, "Bit " + bit + " exceeds 63.", new Exception());
            }
			else {
				char[] bin64Array = new char[64];
				Arrays.fill(bin64Array, '0');
				for(int i=0; i<binArray.length; i++){
					bin64Array[64-binArray.length + i] = binArray[i];
				}
				bin64Array[63-bit] = boolValue? '1' : '0';
				String binString = new String(bin64Array);

				if( binString.indexOf('1') <= -1){
					binArray = new char[]{'0'};
				}else {
					binArray = new char[64 - binString.indexOf('1')];
					for(int i=0; i<binArray.length; i++){
						binArray[i] = bin64Array[i+64-binArray.length];
					}
				}

				binString = new String(binArray);
				setValue(Long.parseLong(binString, 2));
			}
		}
		repaint();
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new LabelWidgetIntrospector().getBeanInfo(this.getClass());
	}
	
	public void setText(String text) {
		this.text = text;
		repaint();
	}

	public String getText() {
		return text;
	}

	
	class BoxFigure extends Figure{
		@Override
		protected void paintClientArea(Graphics graphics) {
			if(support3d == null)
				support3d = GraphicsUtil.testPatternSupported(graphics);
			Rectangle clientArea = getClientArea();
			Rectangle square = new Rectangle(clientArea.x, clientArea.y+clientArea.height/2 - BOX_SIZE/2,
					BOX_SIZE, BOX_SIZE);
			graphics.pushState();
			if(support3d)
				graphics.setBackgroundPattern(
					GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), 
							square.x, square.y+1, 
							square.x, square.y+square.height,
							ColorConstants.white, graphics.getBackgroundColor()));
			graphics.fillRoundRectangle(square, 4, 4);
			graphics.setForegroundColor(
					CustomMediaFactory.getInstance().getColor(130, 130, 130));
			graphics.drawRoundRectangle(square, 4, 4);
			
			if(boolValue){
				graphics.translate(square.x, square.y);
				graphics.setLineWidth(3);
				graphics.setForegroundColor(selectedColor);
			
				graphics.drawPolyline(new int[]{
						3, (int) (BOX_SIZE*0.45),  (int) (BOX_SIZE*0.45), BOX_SIZE*3/4-1, BOX_SIZE-2, 3
				});
			}
			graphics.popState();
			Dimension textSize = FigureUtilities.getTextExtents(text, graphics.getFont());

			if (!isEnabled()) {
				graphics.translate(1, 1);
				graphics.setForegroundColor(ColorConstants.buttonLightest);
				graphics.drawText(text, square.getRight().getTranslated(GAP, -textSize.height/2));
				graphics.translate(-1, -1);
				graphics.setForegroundColor(ColorConstants.buttonDarker);
			}
			graphics.drawText(text, square.getRight().getTranslated(GAP, -textSize.height/2));

			super.paintClientArea(graphics);
			
		}
	}


}
