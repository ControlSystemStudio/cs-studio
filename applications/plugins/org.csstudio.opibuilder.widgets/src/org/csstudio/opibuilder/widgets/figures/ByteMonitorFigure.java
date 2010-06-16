package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.widgets.figureparts.Bulb;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;
/**
 * @author hammonds
 *
 */
/**
 * @author hammonds
 *
 */
public class ByteMonitorFigure extends Figure {

	/** The maximum number of bits in the value */
	private int MAX_BITS = 64;
	/** The bit to start display*/
	private int startBit;
	/** The number of bits to display */
	private int numBits;
	/** Display direction.  Horizontal if true, Vertical if false */
	private boolean isHorizontal;
	/** Reverse the direction to display bits.  If true start bit is displayed left or top, if false start bit
	 * is right or top.*/
	private boolean reverseBits;

	/** The value to be displayed */
	private int value;
	/** The color to be displayed if a bit is 1 */
	private RGB onColor;
	/** The color to be displayed if a bit is 0 */
	private RGB offColor;
	
	/** Give the objects representing the bits a 3dEffect */
	private Boolean effect3D;
	private Boolean squareLED;
	
	
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
	 * Set the starting bit for the display
	 * @param startBit 
	 */
	public void setStartBit(int startBit) {
		this.startBit = startBit;
	}

	/**
	 * Return the starting bit for the display
	 * @return the starting bit for the display
	 */
	public int getStartBit() {
		return startBit;
	}

	/**
	 * Set the number of bits to display
	 * @param numBits
	 */
	public void setNumBits(int numBits) {
		this.numBits = numBits;
		removeAll();
		for (int ii =0; ii < numBits; ii++){
			add(new Bulb());
		}
		revalidate();
	}

	/**
	 * The number of bits to display
	 * @return the numBits
	 */
	public int getNumBits() {
		return numBits;
	}

	/**
	 * sets the direction that shapes corresponding to bits should be displayed.  Bits are displayed horizontally 
	 * if true and vertically if false. 
	 * @param isHorizontal the isHorizontal to set
	 */
	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
		layout();
		revalidate();
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
	 * Change the value to the last read value 
	 * @param value
	 */
	public void setValue(Object value){
		this.value = ((Integer)value).intValue();
		drawValue();
	}

	/**
	 * Color the rectangles with values appropriate for the value.  Rectangles are colored with onColor if the bit
	 * is 1.  They are colored offColor if the bit is 0.
	 */
	public void drawValue() {
		Object[] children = getChildren().toArray();
		RGB onColor = getOnColor();
		RGB offColor = getOffColor();
		
		for (int ii=startBit; ii< startBit+numBits; ii++){
			int widgetIndex =0;
			if (reverseBits){
				widgetIndex = ii-startBit;
			}
			else{
				widgetIndex = (numBits - 1) -(ii-startBit);
			}
			Bulb bulb = ((Bulb)children[widgetIndex]);
			if (((value>>ii)&0x1) == 1){
				bulb.setBulbColor(onColor);
				bulb.repaint();
			}
			else {
				
				bulb.setBulbColor(offColor);
				bulb.repaint();
			}
		}

		
	}
	/**
	 * Get the color to be displayed if a bit is 0.
	 * @return
	 */
	private RGB getOffColor() {
		return offColor;
	}

	/**
	 * Get the color to be displayed if a bit is 1.
	 * @return
	 */
	private RGB getOnColor() {
		return onColor;
	}

	/**
	 * Set the color to be displayed if a bit is 1.
	 * @param onColor the onColor to set
	 */
	public void setOnColor(RGB rgb) {
//		this.onColor = new Color(null, rgb);
		this.onColor = rgb;
	}

	/**
	 * Set the color to be displayed if a bit is 0.
	 * @param offColor the offColor to set
	 */
	public void setOffColor(RGB rgb) {
//		this.offColor = new Color(null, rgb);
		this.offColor = rgb;
	}

	/**
	 * returns the maximum number of bits to be displayed
	 * @return
	 */
	public int getMAX_BITS() {
		return MAX_BITS;
	}

	/**
	 * Sets the order to display the bits.    If true the start bit is on the left or
	 *  top.  If false it is on the right or bottom.
	 * @param reverseBits the reverseBits to set
	 */
	public void setReverseBits(boolean reverseBits) {
		this.reverseBits = reverseBits;
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
	 * Set that the displayed LEDs should have a 3D effect
	 * @param newValue boolean true if 3D, false if not
	 */
	public void setEffect3D(Boolean newValue) {
		this.effect3D = newValue;
		for (Object child : getChildren()){
			Bulb bulb = (Bulb)child;
			bulb.setEffect3D(this.effect3D);
		}
	}
	/**
	 * Set if the displayed LEDs should be square or round.  
	 * @param squareLED boolean true if square, false if round
	 */
	public void setSquareLED(Boolean squareLED) {
		this.squareLED = squareLED;
		for (Object child : getChildren()){
			Bulb bulb = (Bulb)child;
			bulb.setSquareLED(this.squareLED);
		}
	}

}
