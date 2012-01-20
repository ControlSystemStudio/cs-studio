
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.delivery.sms;

import java.util.Vector;

/**
 * @author Markus Moeller
 *
 */
public class ModemInfoContainer {
    
    private Vector<ModemInfo> modemInfo;
     
    public ModemInfoContainer() {
        modemInfo = new Vector<ModemInfo>();
    }
    
    public int getModemCount() {
        return modemInfo.size();
    }
    
    public String getModemName(int index) {
        
        String result = null;
                
        if(modemInfo.isEmpty() == false) {
            if((index >= 0) && (index < modemInfo.size())) {
                result = modemInfo.elementAt(index).getModemName();
            }
        }
        
        return result;
    }
    
    public void addModemName(String name, String phoneNumber) {        
        if((name != null) && (phoneNumber != null)) {
            if((name.length() > 0) && (phoneNumber.length() > 0)) {
                modemInfo.add(new ModemInfo(name, phoneNumber));
            }
        }
    }
    
    public String[] getModemNames() {
        
        String[] result = null;
        
        if(modemInfo.size() > 0) {
            result = new String[modemInfo.size()];
            for(int i = 0;i < modemInfo.size();i++) {
                result[i] = modemInfo.get(i).getModemName();
            }
        } else {
            result = new String[1];
        }
        
        return result;
    }
    
    public String getPhoneNumber(String name) {
        
        String result = null;

        if(containsModemName(name)) {
            for(ModemInfo mi : modemInfo) {
                if(mi.getModemName().compareTo(name) == 0) {
                    result = mi.getPhoneNumber();
                    break;
                }
            }
        }
        
        return result;
    }
    
    public boolean containsModemName(String name) {
        
        boolean contains = false;
        
        for(ModemInfo mi : modemInfo) {
            if(mi.getModemName().compareTo(name) == 0) {
                contains = true;
                break;
            }
        }

        return contains;
    }
}
