
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

package org.csstudio.archive.sdds.server.sdds;

/**
 * @author Markus Moeller
 *
 */
public enum SddsDataType
{
    SDDS_UNDEFINED,
    SDDS_DOUBLE,
    SDDS_FLOAT,
    SDDS_LONG,
    SDDS_ULONG,
    SDDS_SHORT,
    SDDS_USHORT,
    SDDS_STRING,
    SDDS_CHARACTER,
    SDDS_ANY_NUMERIC_TYPE,
    SDDS_ANY_FLOATING_TYPE,
    SDDS_ANY_INTEGER_TYPE;
    
    /**
     * 
     * @return
     */
    public static int getSddsTypeCount()
    {
        return SDDS_CHARACTER.ordinal();
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isSddsIntegerType(SddsDataType type)
    {
        return ((type == SDDS_LONG) || (type == SDDS_ULONG) || (type == SDDS_SHORT) || (type == SDDS_USHORT));
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isSddsFloatingType(SddsDataType type)
    {
        return ((type == SDDS_DOUBLE) || (type == SDDS_FLOAT));
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isSddsNumericType(SddsDataType type)
    {
        return (isSddsIntegerType(type) || isSddsFloatingType(type));
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public boolean isSddsValidType(SddsDataType type)
    {
        return ((type.ordinal() >= 1) && (type.ordinal() <= SddsDataType.getSddsTypeCount()));
    }
}
