
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

package org.csstudio.websuite.utils;

/**
 * TODO (mmoeller) : 
 * 
 * @author Markus Moeller
 * @version 
 * @since 25.06.2010
 */
public enum Severity {
    
    NO_ALARM(0, "NO_ALARM", "noalarm"),
    MINOR(1, "MINOR", "minor"),
    MAJOR(2, "MAJOR", "major"),
    INVALID(3, "INVALID", "invalid");
    
    /** The name of the severity */
    private String name;

    /** Name of the CSS class */
    private String className;
    
    /** The number of the severity */
    private int severityNumber;
    
    
    /**
     * 
     * @param n
     * @param name
     */
    private Severity(int n, String name, String className) {
        this.severityNumber = n;
        this.className = className;
        this.name = name;
    }
    
    /**
     * 
     * @param n
     * @return
     */
    public static Severity getByNumber(int n) {
        
        Severity result = null;
        
        for(Severity e : Severity.values()) {
            
            if(e.getSeverityNumber() == n) {
                result = e;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param n
     * @return
     */
    public static Severity getByName(String name) {
        
        Severity result = null;
        
        if(name == null) {
            return result;
        }
        
        for(Severity e : Severity.values()) {
            
            if(e.getName().compareToIgnoreCase(name) == 0) {
                result = e;
                break;
            }
        }
        
        return result;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return
     */
    public String getClassName() {
        return className;
    }

    /**
     * 
     * @return
     */
    public int getSeverityNumber() {
        return severityNumber;
    }
}
