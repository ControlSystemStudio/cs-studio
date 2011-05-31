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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
/**
 * @author hammonds, Xihui Chen
 *
 */

public class ByteMonitorFigure extends Figure implements Introspectable{

	/** The maximum number of bits in the value */
	private int MAX_BITS = 64;
	/** The bit to start display*/
	private int startBit;
	/** The number of bits to display */
	private int numBits;
	/** Display direction.  Horizontal if true, Vertical if false */
	private boolean isHorizontal = true;
	/** Reverse the direction to display bits.  If true start bit is displayed left or top, if false start bit
	 * is right or top.*/
	private boolean reverseBits = false;

	/** The value to be displayed */
	private long value = 0;
	/** The color to be displayed if a bit is 1 */
	private Color onColor = ColorConstants.green;
	/** The color to be displayed if a bit is 0 */
	private Color offColor = ColorConstants.darkGreen;
	
	/** Give the objects representing the bits a 3dEffect */
	private boolean effect3D = true;
	private boolean squareLED = false;
	
	
	public ByteMonitorFigure() {
		setNumBits(16);
	}
	
	private LEDFigure createLED(){
		LEDFigure led = new LEDFigure();
		led.setShowBooleanLabel(false);
		led.setOnColor(getOnColor());
		led.setOffColor(getOffColor());
		led.setSquareLED(squareLED);
		led.setEffect3D(effect3D);
		return led;
	}
	
	/**
	 * Color the rectangles with values appropriate for the value.  Rectangles are colored with onColor if the bit
	 * is 1.  They are colored offColor if the bit is 0.
	 */
	public void drawValue() {
		Object[] children = getChildren().toArray();
		
		for (int ii=startBit; ii< startBit+numBits; ii++){
			int widgetIndex =0;
			if (reverseBits){
				widgetIndex = ii-startBit;
			}
			else{
				widgetIndex = (numBits - 1) -(ii-startBit);
			}
			LEDFigure led = ((LEDFigure)children[widgetIndex]);
			if (((value>>ii)&0x1) == 1){
				led.setBooleanValue(true);
			}
			else {
				
				led.setBooleanValue(false);
			}
		}

		
	}

	/**
	 * returns the maximum number of bits to be displayed
	 * @return
	 */
	public int getMAX_BITS() {
		return MAX_BITS;
	}

	/**
	 * The number of bits to display
	 * @return the numBits
	 */
	public int getNumBits() {
		return numBits;
	}

	/**
	 * Get the color to be displayed if a bit is 0.
	 * @return
	 */
	public Color getOffColor() {
		return offColor;
	}

	/**
	 * Get the color to be displayed if a bit is 1.
	 * @return
	 */
	public Color getOnColor() {
		return onColor;
	}

	/**
	 * Return the starting bit for the display
	 * @return the starting bit for the display
	 */
	public int getStartBit() {
		return startBit;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @return the effect3D
	 */
	public boolean isEffect3D() {
		return effect3D;
	}
	/**
	 * Check if shapes corresponding to bits should be vertical or horizontal.  Bits are displayed horizontally 
	 * if true and vertically if false. 
	 * @return the isHorizontal
	 */
	public boolean isHorizontal() {
		return isHorizontal;
	}

	/**
	 * Return true if the display order of the bits should be reversed.  If true the start bit is on the left or
	 *  top.  If false it is on the right or bottom.
	 * @return the reverseBits
	 */
	public boolean isReverseBits() {
		return reverseBits;
	}

	/**
	 * @return the squareLED
	 */
	public boolean isSquareLED() {
		return squareLED;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#layout()
	 */
	@Override
	protected void layout() {
		super.layout();

		if(numBits >0){
			Rectangle clientArea = getClientArea();
			if (isHorizontal){
				int avgWidth = clientArea.width/numBits;
				int startX = clientArea.x;		
				for(Object child : getChildren()){
					((Figure)child).setBounds(new Rectangle(startX,clientArea.y, avgWidth, clientArea.height));
					startX += avgWidth;
				}
			}
			else {
				int avgHeight = clientArea.height/numBits;
				int startY = clientArea.y;		
				for(Object child : getChildren()){
					((Figure)child).setBounds(new Rectangle(
							clientArea.x, startY, clientArea.width, avgHeight));
					startY += avgHeight;
				}
			}
		}
	}

	/**
	 * Set that the displayed LEDs should have a 3D effect
	 * @param newValue boolean true if 3D, false if not
	 */
	public void setEffect3D(boolean newValue) {
		if(this.effect3D == newValue)
			return;
		this.effect3D = newValue;
		for (Object child : getChildren()){
			LEDFigure bulb = (LEDFigure)child;
			bulb.setEffect3D(this.effect3D);
		}
		repaint();
	}

	/**
	 * sets the direction that shapes corresponding to bits should be displayed.  Bits are displayed horizontally 
	 * if true and vertically if false. 
	 * @param isHorizontal the isHorizontal to set
	 */
	public void setHorizontal(boolean isHorizontal) {
		if(this.isHorizontal == isHorizontal)
			return;
		this.isHorizontal = isHorizontal;
		layout();
		revalidate();
	}

	/**
	 * Set the number of bits to display
	 * @param numBits
	 */
	public void setNumBits(int numBits) {
		if(this.numBits == numBits || numBits <=0 || numBits > MAX_BITS)
			return;
		this.numBits = numBits;
		removeAll();
		for (int ii =0; ii < numBits; ii++){
			add(createLED());
		}
		revalidate();
	}


	/**
	 * Set the color to be displayed if a bit is 0.
	 * @param offColor the offColor to set
	 */
	public void setOffColor(Color rgb) {
		if(this.offColor != null && this.offColor.equals(rgb))
			return;
		this.offColor = rgb;
		for (Object child : getChildren()){
			LEDFigure led = (LEDFigure)child;
			led.setOffColor(rgb);
		}
	}
	/**
	 * Set the color to be displayed if a bit is 1.
	 * @param onColor the onColor to set
	 */
	public void setOnColor(Color rgb) {
		if(this.onColor != null && this.onColor.equals(rgb))
			return;
		this.onColor = rgb;
		for (Object child : getChildren()){
			LEDFigure led = (LEDFigure)child;
			led.setOnColor(rgb);
		}
	}

	/**
	 * Sets the order to display the bits.    If true the start bit is on the left or
	 *  top.  If false it is on the right or bottom.
	 * @param reverseBits the reverseBits to set
	 */
	public void setReverseBits(boolean reverseBits) {
		if(this.reverseBits == reverseBits)
			return;
		this.reverseBits = reverseBits;
		repaint();
	}

	/**
	 * Set if the displayed LEDs should be square or round.  
	 * @param squareLED boolean true if square, false if round
	 */
	public void setSquareLED(boolean squareLED) {
		if(this.squareLED == squareLED)
			return;
		this.squareLED = squareLED;
		for (Object child : getChildren()){
			LEDFigure bulb = (LEDFigure)child;
			bulb.setSquareLED(this.squareLED);
		}
		revalidate();
		repaint();
	}

	/**
	 * Set the starting bit for the display
	 * @param startBit 
	 */
	public void setStartBit(int startBit) {
		if(this.startBit == startBit || startBit <0 || startBit +numBits > MAX_BITS)
			return;
		this.startBit = startBit;
		repaint();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {		
		setValue((long)value);
	}
	
	public void setValue(double value){
		setValue((long)value);
	}
	
	/**
	 * Change the value to the last read value 
	 * @param value
	 */
	public void setValue(long value){
		this.value = value;
		drawValue();
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

}
