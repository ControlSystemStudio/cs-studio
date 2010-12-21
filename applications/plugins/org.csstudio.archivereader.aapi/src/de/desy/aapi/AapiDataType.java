
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package de.desy.aapi;

/**
 *  Not all of the data types are used in current version of AAPI.
 *  Only DOUBLE_DATA is implemented, but agreement
 *  about this reservation was created between DESY BESSY and LANL
 *  Should used in next AAPI-generation
 * 
 * TODO (mmoeller) : 
 * 
 * @author Markus Moeller
 * @version Archive Protocol V2.4
 * @since 06.12.2010
 */
public enum AapiDataType {
    
    SHORT_TYPE(0x01),

    FLOAT_TYPE(0x02),

    ENUM_TYPE(0x03),

    CHAR_TYPE(0x04),

    LONG_TYPE(0x05),

    DOUBLE_TYPE(0x06),

    WF_FLAG(0x80),

    NUMERIC_INFO(0x100),

    ENNUMERATED_INFO(0x101);

    /** Number of data type */
    private int typeNumber;
    
    /**
     * 
     * @param typeNumber
     */
    private AapiDataType(int typeNumber) {
        
        this.typeNumber = typeNumber;
    }
    
    /**
     * Returns the number of the data type.
     * 
     * @return The number of the data type.
     */
    public int getTypeNumber() {
        return typeNumber;
    }
}
