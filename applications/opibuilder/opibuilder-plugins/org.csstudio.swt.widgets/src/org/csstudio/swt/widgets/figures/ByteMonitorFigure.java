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
import java.util.List;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.ui.util.ColorConstants;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * @author hammonds, Xihui Chen
 * @author Takashi Nakamoto - added labels
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
    private boolean hasPackedLEDs = false;
    private int ledBorderWidth = 2;
    private Color ledBorderColor = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_DARK_GRAY);

    /** LEDs */
    private List<LEDFigure> ledFigures = new ArrayList<LEDFigure>();

    /** Labels */
    private List<TextFigure> textFigures = new ArrayList<TextFigure>();
    private List<String> labels = new ArrayList<String>();

    public ByteMonitorFigure() {
        setNumBits(16);
    }

    private LEDFigure createLED(){
        LEDFigure led = new LEDFigure();
        led.setShowBooleanLabel(false);
        led.setOnColor(getOnColor());
        led.setOffColor(getOffColor());
        led.setBulbBorderWidth(ledBorderWidth);
        led.setBulbBorderColor(ledBorderColor);
        led.setSquareLED(squareLED);
        led.setEffect3D(effect3D);
        return led;
    }

    private TextFigure createText(){
        TextFigure text = new TextFigure();
        text.setText("");
        alignText(text);
        return text;
    }

    /**
     * Color the rectangles with values appropriate for the value.  Rectangles are colored with onColor if the bit
     * is 1.  They are colored offColor if the bit is 0.
     */
    public void drawValue() {
        for (int ii=startBit; ii< startBit+numBits; ii++){
            int widgetIndex =0;
            if (reverseBits){
                widgetIndex = ii-startBit;
            }
            else{
                widgetIndex = (numBits - 1) -(ii-startBit);
            }
            LEDFigure led = ledFigures.get(widgetIndex);
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

    /**
     * LEDs are sized to 'fill' the client area.
     *
     * If 'packed' borders around LEDs are overlapped giving a higher density.
     * This gives an odd visual effect for 3d and round LEDs
     *
     * @param clientSize
     * @param borderSize
     * @return
     */
    private int calculateLedSize(int clientSize, int borderSize) {
        int size;

        if (hasPackedLEDs) {
            size = (clientSize - borderSize) / numBits + borderSize;
        }
        else {
            size = clientSize / numBits;
        }
        return size;
    }

    /**
     * LEDs spacing is the offset between the corners of successive LEDs
     *
     * If 'packed' borders around LEDs are overlapped giving a higher density.
     * This gives an odd visual effect for 3d and round LEDs
     *
     * @param ledSize
     * @param borderSize
     * @return
     */
    private int calculateLedSpacing(int ledSize, int borderSize) {
        int spacing = ledSize;

        if (hasPackedLEDs) {
            spacing -= borderSize;
        }

        return spacing;
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
                int ledWidth = calculateLedSize(clientArea.width, ledBorderWidth);
                int ledSpacing = calculateLedSpacing(ledWidth, ledBorderWidth);
                int startX = clientArea.x;

                int ledHeight = 0;
                if (ledWidth > clientArea.height || squareLED) {
                    ledHeight = clientArea.height;
                } else {
                    ledHeight = ledWidth;
                }

                for (LEDFigure led : ledFigures) {
                    led.setSize(ledWidth, ledHeight);
                    led.setLocation(new Point(startX, clientArea.y));
                    startX += ledSpacing;
                }

                startX = clientArea.x;
                for (TextFigure text : textFigures) {
                    if (squareLED) {
                        text.setBounds(new Rectangle(
                                startX, clientArea.y, ledWidth, ledHeight));
                    } else {
                        text.setBounds(new Rectangle(
                                startX, clientArea.y + ledHeight, ledWidth, clientArea.height - ledHeight));
                    }
                    startX += ledSpacing;
                }
            }
            else {
                int ledHeight = calculateLedSize(clientArea.height, ledBorderWidth);
                int ledSpacing = calculateLedSpacing(ledHeight, ledBorderWidth);
                int startY = clientArea.y;

                int ledWidth = 0;
                if (ledHeight > clientArea.width || squareLED) {
                    ledWidth = clientArea.width;
                } else {
                    ledWidth = ledHeight;
                }

                for (LEDFigure led : ledFigures) {
                    led.setSize(ledWidth, ledHeight);
                    led.setLocation(new Point(clientArea.x, startY));
                    startY += ledSpacing;
                }

                startY = clientArea.y;
                for (TextFigure text : textFigures) {
                    if (squareLED) {
                        text.setBounds(new Rectangle(
                                clientArea.x, startY, ledWidth, ledHeight));
                    } else {
                        text.setBounds(new Rectangle(
                                clientArea.x + ledWidth, startY, clientArea.width - ledWidth, ledHeight));
                    }
                    startY += ledSpacing;
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
        for (LEDFigure bulb : ledFigures) {
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

        for (TextFigure text : textFigures) {
            alignText(text);
        }

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
        ledFigures.clear();
        textFigures.clear();
        for (int ii =0; ii < numBits; ii++){
            LEDFigure led = createLED();
            add(led);
            ledFigures.add(led);

            TextFigure text = createText();
            add(text);
            textFigures.add(text);
        }
        updateLabels();
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
        for (LEDFigure led : ledFigures){
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
        for (LEDFigure led : ledFigures) {
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
        updateLabels();
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
        for (LEDFigure bulb : ledFigures) {
            bulb.setSquareLED(this.squareLED);
        }
        for (TextFigure text : textFigures) {
            alignText(text);
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
     * Set the LED border width, i.e. the spacing between
     * the LEDs in the widget
     */
    public void setLedBorderWidth(int value) {
        if (value == ledBorderWidth)
            return;

        ledBorderWidth = value;

        for (LEDFigure bulb : ledFigures) {
            bulb.setBulbBorderWidth(ledBorderWidth);
        }
        revalidate();
        repaint();
    }


    /**
     * Set the LED border color
     */
    public void setLedBorderColor(Color value) {
        if (ledBorderColor != null && value == ledBorderColor)
            return;

        ledBorderColor = value;

        for (LEDFigure bulb : ledFigures) {
            bulb.setBulbBorderColor(ledBorderColor);
        }
        revalidate();
        repaint();
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

    public void setLabels(List<String> labels) {
        this.labels = labels;
        updateLabels();
    }

    private void updateLabels() {
        for (int i = 0; i<textFigures.size(); i++) {
            TextFigure text;
            if (reverseBits) {
                text = textFigures.get(i);
            } else {
                text = textFigures.get(textFigures.size() - i - 1);
            }

            if (i < labels.size()) {
                text.setText(labels.get(i));
            } else {
                text.setText("");
            }
        }
    }

    private void alignText(TextFigure text) {
        if (isHorizontal) {
            text.setRotate(270.0);
            text.setHorizontalAlignment(TextFigure.H_ALIGN.CENTER);
            if (squareLED) {
                text.setVerticalAlignment(TextFigure.V_ALIGN.MIDDLE);
            } else {
                text.setVerticalAlignment(TextFigure.V_ALIGN.TOP);
            }
        } else {
            text.setRotate(0.0);
            if (squareLED) {
                text.setHorizontalAlignment(TextFigure.H_ALIGN.CENTER);
            } else {
                text.setHorizontalAlignment(TextFigure.H_ALIGN.LEFT);
            }
            text.setVerticalAlignment(TextFigure.V_ALIGN.MIDDLE);
        }
    }

    public void setPackedLEDs(boolean packLEDs) {
        if(this.hasPackedLEDs  == packLEDs)
            return;

        this.hasPackedLEDs = packLEDs;
        revalidate();
        repaint();
    }
}
