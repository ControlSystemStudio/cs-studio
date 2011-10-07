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
import java.util.Arrays;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
/**
 * Base figure for a widget based on {@link AbstractBoolWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public class AbstractBoolFigure extends Figure implements Introspectable{
	
	public enum BoolLabelPosition{
		
		DEFAULT("Default"),				
		TOP("Top"),	
		LEFT("Left"),
		CENTER("Center"),
		RIGHT("Right"),
		BOTTOM("Bottom"),
		TOP_LEFT("Top Left"),
		TOP_RIGHT("Top Right"),	
		BOTTOM_LEFT("Bottom Left"),
		BOTTOM_RIGHT("Bottom Right");
		
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i=0;
			for(BoolLabelPosition h : values()){
				result[i++] = h.toString();
			}
			return result;
		}
		String descripion;
		BoolLabelPosition(String description){
			this.descripion = description;
		}
		
		@Override
		public String toString() {
			return descripion;
		}
	}

	protected Label boolLabel;

	protected long value = 0;

	protected int bit = -1;

	protected boolean showBooleanLabel = false;

	protected boolean booleanValue = false;

	protected String onLabel = "ON";

	protected String offLabel = "OFF";
	
	protected BoolLabelPosition boolLabelPosition = BoolLabelPosition.DEFAULT;

	protected Color onColor = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GREEN);

	protected Color offColor = CustomMediaFactory.getInstance().getColor(
			new RGB(0,128,0));

	private Point labelLocation;


	protected AbstractBoolFigure() {
		boolLabel = new Label(offLabel){
			@Override
			public boolean containsPoint(int x, int y) {
				return false;
			}
		};
		boolLabel.setVisible(showBooleanLabel);
	}

	protected void calculateLabelLocation(Point defaultLocation) {
		if(boolLabelPosition == BoolLabelPosition.DEFAULT){
			labelLocation =  defaultLocation;
			return;
		}
		Rectangle textArea = getClientArea();		
		Dimension textSize = TextUtilities.INSTANCE.getTextExtents(
				boolLabel.getText(), getFont());
			int x=0;
			if(textArea.width > textSize.width){				
				switch (boolLabelPosition) {
				case CENTER:
				case TOP:
				case BOTTOM:
					x = (textArea.width - textSize.width)/2;
					break;
				case RIGHT:
				case TOP_RIGHT:
				case BOTTOM_RIGHT:
					x = textArea.width - textSize.width;
					break;
				default:					
					break;
				}
			}
			
			int y=0;
			if(textArea.height > textSize.height){
				switch (boolLabelPosition) {
				case CENTER:
				case LEFT:
				case RIGHT:
					y = (textArea.height - textSize.height)/2;
					break;
				case BOTTOM:
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT:
					y =textArea.height - textSize.height;
					break;
				default:
					break;
				}
			}
			if(useLocalCoordinates())
				labelLocation = new Point(x, y);
			else
				labelLocation = new Point(x + textArea.x, y + textArea.y);
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
	public boolean getBooleanValue() {
		return booleanValue;
	}
	
	protected Point getLabelLocation(final int x, final int y){
		return getLabelLocation(new Point(x, y));
	}
	
	/**
	 * @param defaultLocation The default location.
	 * @return the location of the boolean label
	 */
	protected Point getLabelLocation(Point defaultLocation){
		if(labelLocation == null)
			calculateLabelLocation(defaultLocation);
		return labelLocation;
	}
	
	public BoolLabelPosition getBoolLabelPosition() {
		return boolLabelPosition;
	}

	/**
	 * @return the offColor
	 */
	public Color getOffColor() {
		return offColor;
	}



	/**
	 * @return the offLabel
	 */
	public String getOffLabel() {
		return offLabel;
	}

	/**
	 * @return the onColor
	 */
	public Color getOnColor() {
		return onColor;
	}

	/**
	 * @return the onLabel
	 */
	public String getOnLabel() {
		return onLabel;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	@Override
	public void invalidate() {
		labelLocation = null;
		super.invalidate();
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @return the showBooleanLabel
	 */
	public boolean isShowBooleanLabel() {
		return showBooleanLabel;
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


	public void setBooleanValue(boolean value){
		if(this.booleanValue == value)
			return;
		this.booleanValue = value;
		updateValue();
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		repaint();
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		boolLabel.setFont(f);
		revalidate();
	}
	
	public void setBoolLabelPosition(BoolLabelPosition labelPosition) {
		this.boolLabelPosition = labelPosition;
		labelPosition = null;
		revalidate();
		repaint();
	}

	/**
	 * @param offColor the offColor to set
	 */
	public void setOffColor(Color offColor) {
		if(this.offColor != null && this.offColor.equals(offColor))
			return;
		this.offColor = offColor;
		repaint();
	}

	/**
	 * @param offLabel the offLabel to set
	 */
	public void setOffLabel(String offLabel) {
		if(this.offLabel != null && this.offLabel.equals(offLabel))
			return;
		this.offLabel = offLabel;
		if(!booleanValue)
			boolLabel.setText(offLabel);
		
	}

	/**
	 * @param onColor the onColor to set
	 */
	public void setOnColor(Color onColor) {
		if(this.onColor != null && this.onColor.equals(onColor))
			return;
		this.onColor = onColor;
		repaint();
	}

	/**
	 * @param onLabel the onLabel to set
	 */
	public void setOnLabel(String onLabel) {
		if(this.onLabel != null && this.onLabel.equals(onLabel))
			return;
		this.onLabel = onLabel;
		if(booleanValue)
			boolLabel.setText(onLabel);
	}

	/**
	 * @param showBooleanLabel the showBooleanLabel to set
	 */
	public void setShowBooleanLabel(boolean showBooleanLabel) {
		if(this.showBooleanLabel == showBooleanLabel)
			return;
		this.showBooleanLabel = showBooleanLabel;
		boolLabel.setVisible(showBooleanLabel);
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
		revalidate();
		repaint();
	}

	/**
	 * update the boolValue from value and bit.
	 * All the boolValue based behavior changes should be implemented here by inheritance.
	 */
	protected void updateBoolValue() {
		//get boolValue
		if(bit < 0)
			booleanValue = (this.value != 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(this.value).toCharArray();
			if(bit >= binArray.length)
				booleanValue = false;
			else {
				booleanValue = (binArray[binArray.length - 1 - bit] == '1');
			}
		}
		//change boolLabel text
		if(booleanValue)
			boolLabel.setText(onLabel);
		else
			boolLabel.setText(offLabel);
	}

	/**
	 * update the value from boolValue
	 */
	@SuppressWarnings("nls")
    protected void updateValue(){
		//get boolValue
		if(bit < 0)
			setValue(booleanValue ? 1 : 0);
		else if(bit >=0) {
			char[] binArray = Long.toBinaryString(value).toCharArray();
			if(bit >= 64) {
			    // Log with exception to obtain call stack
                Activator.getLogger().log(Level.WARNING, "Bit " + bit + "can not exceed 63.", new Exception());
			}
			else {
				char[] bin64Array = new char[64];
				Arrays.fill(bin64Array, '0');
				for(int i=0; i<binArray.length; i++){
					bin64Array[64-binArray.length + i] = binArray[i];
				}
				bin64Array[63-bit] = booleanValue? '1' : '0';
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
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

}
