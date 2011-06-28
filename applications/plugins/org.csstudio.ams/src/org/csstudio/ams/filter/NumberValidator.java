
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

package org.csstudio.ams.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 
 * @since 20.10.2010
 */
public class NumberValidator {
    
    /**
     * The method extracts the first valid long value from the string.
     *  
     * @param s
     * @return The first valid long value 
     */
    public static Long getCleanLong(String s) {
        
        Long result = null;
        
        Pattern p = Pattern.compile("\\d++");
        Matcher m = p.matcher(s);
        
        while(m.find()) {
            
            try {
                result = new Long(m.group());
                break;
            } catch(NumberFormatException nfe) {
                result = null;
            }
        }
        
        return result;
    }
    
    /**
     * The method extracts the first valid double value from the string.
     *  
     * @param s
     * @return The first valid double value 
     */
    public static Double getCleanDouble(String s) {
        
        Double result = null;
        
        Pattern p = Pattern.compile("\\d++\\.\\d++");
        Matcher m = p.matcher(s);
        
        while(m.find()) {
            
            try {
                result = new Double(m.group());
                break;
            } catch(NumberFormatException nfe) {
                Long l = getCleanLong(s);
                if(l != null) {
                    result = new Double(l.doubleValue());
                } else {
                    result = null;
                }
            }
        }
        
        return result;
    }
}
