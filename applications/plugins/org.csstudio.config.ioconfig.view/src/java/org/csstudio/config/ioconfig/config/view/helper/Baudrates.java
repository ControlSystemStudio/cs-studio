/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: Baudrates.java,v 1.1 2009/08/26 07:09:20 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import javax.annotation.Nonnull;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 25.06.2007
 */
public enum Baudrates {
    
    DP_KBAUD_9_6(" 9,6  kBAUD", "#define DP_KBAUD_9_6    0x00", 0x00),
    DP_KBAUD_19_2("19,2  kBAUD", "#define DP_KBAUD_19_2   0x01", 0x01),
    DP_KBAUD_45_45("45,45 kBAUD", "#define DP_KBAUD_45_45  0x0B", 0x0B),
    DP_KBAUD_93_75("93,75 kBAUD", "#define DP_KBAUD_93_75  0x02", 0x02),
    DP_KBAUD_187_5("187,5 kBAUD", "#define DP_KBAUD_187_5  0x03", 0x03),
    DP_KBAUD_500("500   kBAUD", "#define DP_KBAUD_500    0x04", 0x04),
    DP_KBAUD_750("750   kBAUD", "#define DP_KBAUD_750    0x05", 0x05),
    DP_MBAUD_1_5("  1,5 MBAUD", "#define DP_MBAUD_1_5    0x06", 0x06),
    DP_MBAUD_3("  3   MBAUD", "#define DP_MBAUD_3      0x07", 0x07),
    DP_MBAUD_6("  6   MBAUD", "#define DP_MBAUD_6      0x08", 0x08),
    DP_MBAUD_12(" 12   MBAUD", "#define DP_MBAUD_12     0x09", 0x09);
    
    /**
     * The Key.
     */
    private String _key;
    /**
     * The Buadrate Value.
     */
    private String _stringValue;
    /**
     * The Baudrate as int.
     */
    private int _value;
    
    
    /**
     * @param key displayed Key Name for the Value
     * @param stringValue the Value to configer the Baudrate
     * @param value 
     */
    private Baudrates(@Nonnull final String key,@Nonnull  final String stringValue,@Nonnull  int value) {
        this._key = key;
        this._stringValue = stringValue;
        this._value = value;
    }
    /**
     * @return @see Object#toString()
     */
    @Override
    public final String toString(){
        return _key;
    }

    /**
     * @return the KeyValue 
     */
    @Nonnull
    public final String getKey() {
        return _key;
    }

    /**
     * 
     * @return the Baudrate Value
     */
    @Nonnull
    public final String getValue() {
        return _stringValue;
    }
    
    /**
     * 
     * @return the Baudrate Value
     */
    public final int getVal() {
        return _value;
    }
}
