/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility;

import org.eclipse.swt.graphics.RGB;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.09.2007
 */
public class RGBColor {
    /** The RGB red value. */
    private int _red;
    /** The RGB green value. */
    private int _green;
    /** The RGB blue value. */
    private int _blue;

    /**
     * @param color The ADL Color. It be transform to RGB.
     * @throws WrongADLFormatException was thrown is the color String invalid.
     */
    public RGBColor(final String color) throws WrongADLFormatException{
        if(color.length()==6){
            setRed(color);
            setGreen(color);
            setBlue(color);
        }else{
            throw new WrongADLFormatException("Color String a invalid format");
        }
            

    }
    /** @return the red value. */
    public final int getRed() {
        return _red; 
    }
    /** @return the green value. */
    public final int getGreen() {
        return _green;
    }
    /** @return the blue value. */
    public final int getBlue() {
        return _blue;
    }
    /** @return the blue value. */
    public final RGB getRGB() {
        return new RGB(_red,_green,_blue);
    }
    
    /**
     * @param clr The ADL Color was set and transform to RGB red. 
     */
    private void setRed(final String clr) {
        String temp = "#".concat(clr.substring(0,2));
        _red = Integer.decode(temp);
    }
    /**
     * @param clr The ADL Color was set and transform to RGB green. 
     */
    private void setGreen(final String clr) {
        String temp = "#".concat(clr.substring(2,4));
        _green = Integer.decode(temp);
    }
    /**
     * @param clr The ADL Color was set and transform to RGB blue. 
     */
    private void setBlue(final String clr) {
        String temp = "#".concat(clr.substring(4,6));
        _blue = Integer.decode(temp);
    }
    

}
