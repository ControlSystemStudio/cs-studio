
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

import SDDS.java.SDDS.SDDSFile;

/**
 * @author Markus Moeller
 *
 */
public class SddsUtil
{
    /** No error */
    public static final int SDDS_CHECK_OKAY = 0;
    
    /** No error */
    public static final int SDDS_CHECK_OK = SDDS_CHECK_OKAY;
    
    /** Column, parameter, etc. does not exist */
    public static final int SDDS_CHECK_NONEXISTENT = 1;
    
    /** Column, parameter, etc. has got wrong type */
    public static final int SDDS_CHECK_WRONGTYPE = 2;
    
    /** The unit is not correct */
    public static final int SDDS_CHECK_WRONGUNITS = 3;
    
    /**
     * 
     * @param sddsFile
     * @param columnNames
     * @param columnUnits
     * @param type
     * @return
     */
    public static int checkColumn(SDDSFile sddsFile, String[] columnNames, String[] columnUnits, SddsDataType type)
    {
        int result = SDDS_CHECK_OK;
        
        return result;
    }
}
