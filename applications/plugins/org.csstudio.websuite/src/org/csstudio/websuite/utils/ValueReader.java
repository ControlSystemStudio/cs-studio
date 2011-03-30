
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

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author Markus Moeller
 * @version 
 * @since 25.06.2010
 */
public class ValueReader {
    
    /** */
    private IProcessVariableConnectionService service;
    
    /** */
    private ProcessVariableAdressFactory pvFactory;

    public ValueReader() {
        
        // Get a service instance (all applications using the same shared instance will share channels, too)
        service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
        
        // Get a factory for process variable addresses 
        pvFactory = ProcessVariableAdressFactory.getInstance();
    }
    
    /**
     * 
     * @param pvName
     * @return
     */
    public String getValueAsString(String pvName) {
        
        IProcessVariableAddress pv = null;
        String value;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + pvName);
    
        try {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null) {
                value = "Not available";
            }
        } catch(ConnectionException ce) {
            value = "Not available";
        }
        
        return value;
    }
    
    /**
     * 
     * @param pvName
     * @return
     */
    public Severity getSeverity(String pvName) {
        
        IProcessVariableAddress pv = null;
        Severity value;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + pvName + ".SEVR");
    
        try {
            String temp = service.readValueSynchronously(pv, ValueType.STRING);
            value = Severity.getByName(temp);
            if(value == null) {
                value = null;
            }
        } catch(ConnectionException ce) {
            value = null;
        }
        
        return value;
    }
    
    /**
     * 
     * @param pvName
     * @return
     */
    public String getEgu(String pvName) {
        
        IProcessVariableAddress pv = null;
        String result = null;

        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + pvName + ".EGU");
        
        try {
            result = service.readValueSynchronously(pv, ValueType.STRING);
            if(result == null) {
                result = "N/A";
            }
        } catch(ConnectionException ce) {
            result = "N/A";
        }

        return result;
    }
}
