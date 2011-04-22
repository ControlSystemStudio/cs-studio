
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
 * TODO (Markus Moeller) : 
 * 
 * @author Markus Moeller
 * @version 
 * @since 30.06.2010
 */
public class PageEntry {
    
    /** Name of the PV */
    private String pvName;

    /** EGU string */
    private String egu;
    
    /** The label / description for the value */
    private String label;
    
    /**
     * The standard constructor
     */
    public PageEntry() {
        
        this.pvName = "";
        this.egu = "";
        this.label = "";
    }

    /**
     * 
     * @param pvName
     * @param equ
     * @param label
     */
    public PageEntry(String pvName, String egu, String label) {
        
        this.pvName = (pvName != null) ? pvName : "";
        this.egu = (egu != null) ? egu : "";
        this.label = (label != null) ? label : "";
    }

    /**
     * 
     * @return
     */
    public boolean containsData() {
        
        boolean result = false;
        
        if((pvName.trim().length() > 0) || (label.trim().length() > 0)) {
            result = true;
        }
        
        return result;
    }
    
    /**
     * 
     */
    @Override
	public String toString() {
        
        return "PageEntry{" + pvName + ", " + egu + ", " + label + "}";
    }
    
    /**
     * 
     * @return
     */
    public String getPvName() {
        return pvName;
    }

    /**
     * 
     * @param pvName
     */
    public void setPvName(String pvName) {
        this.pvName = pvName;
    }

    /**
     * 
     * @return
     */
    public String getEgu() {
        return egu;
    }

    /**
     * 
     * @param egu
     */
    public void setEgu(String egu) {
        this.egu = egu;
    }
    
    /**
     * 
     * @return
     */
    public boolean containsEgu() {
        return (egu.length() > 0);
    }
    
    /**
     * 
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
