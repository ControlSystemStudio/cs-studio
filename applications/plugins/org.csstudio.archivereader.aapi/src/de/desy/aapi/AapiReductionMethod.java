
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
 * TODO (mmoeller) : 
 * 
 * @author Markus Moeller
 * @version Archive Protocol V2.4
 * @since 06.12.2010
 */
public enum AapiReductionMethod {
    
	/** The method with number 0 is not defined */
    INVALID_METHOD(0),
    
    /** Average method */
    AVERAGE_METHOD(1),
    
    /** Tail/Raw method */
    TAIL_RAW_METHOD(2),
    
    /** Sharp method */
    SHARP_METHOD(3),
    
    /** Spline method */
    SPLINE_METHOD(4),
    
    /** FFT method */
    FFT_METHOD(5),
    
    /** Raw data */
    NO_FILTERING_METHOD(6),
    
    /** Min/Max method */
    MIN_MAX_AVERAGE_METHOD(7),
    
    /**  */
    LINEAR_INT_METHOD(8),
    
    /**  */
    STEP_FUNCTION_METHOD(9),
    
    /**  */
    LINEAR_AND_STEP_METHOD(10);
    
    /** Number of the method */
    private int methodNumber;
    
    /**
     * 
     * @param methodNumber
     */
    private AapiReductionMethod(int methodNumber) {
        
        this.methodNumber = methodNumber;
    }

    /**
     * 
     * @return
     */
    public int getMethodNumber() {
        return methodNumber;
    }
    
    /**
     * 
     * @return
     */
    public static int getMaxMethodNumber() {
        
        int result = 0;
        
        for(AapiReductionMethod o : AapiReductionMethod.values()) {
            
            if(o.getMethodNumber() >= result) {
                
                result = o.getMethodNumber();
            }
        }
        
        return result;
    }
}
