
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.websuite.epics;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 09.08.2012
 */
public class IocRequester {
    
    /** */
    private IProcessVariableConnectionService service;
    
    /** */
    private ProcessVariableAdressFactory pvFactory;

    public IocRequester() {
        // Get a service instance
        // (all applications using the same shared instance will share channels, too)
        service = ProcessVariableConnectionServiceFactory
                           .getDefault().getProcessVariableConnectionService();
        
        // Get a factory for process variable addresses 
        pvFactory = ProcessVariableAdressFactory.getInstance();
    }
    
    public String askOnCallDuty() {
        String value = null;
        IProcessVariableAddress pv = 
                pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix()
                                                      + "://Rufbereitschaft");
        try {
            value = service.readValueSynchronously(pv, ValueType.STRING);
        } catch(ConnectionException ce) {
            value = "Not available";
        }
        return value;
    }
    
    public boolean isIocAlive(String iocName) {
        
        long value;
        
        IProcessVariableAddress pv = 
                pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix()
                                                      + "://" + iocName + ":alive");
        try {
            value = service.readValueSynchronously(pv, ValueType.LONG);
        } catch(ConnectionException ce) {
            value = 0;
        } catch(NullPointerException npe) {
            value = 0;
        }

        return (value == 6) || (value == 8);
    }
    
    public String askIocDescription(String iocName) {
        String value = null;
        IProcessVariableAddress pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + iocName + ":applDesc_si");
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
    
    public String askIocBootTime(String iocName) {
        String value = null;
        IProcessVariableAddress pv = 
                pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix()
                                                      + "://" + iocName + ":starttime_si");
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
    
    public boolean askFirstRedundantIoc(String iocName) {
        long value;
        IProcessVariableAddress pv = 
                pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix()
                                                      + "://" + iocName + ":rmtStateA");
        try {
            try {
                value = service.readValueSynchronously(pv, ValueType.LONG);
            } catch (NullPointerException e) {
                value = -1;
            }
        } catch(ConnectionException ce) {
            value = -1;
        }

        return ((value == 6) || (value == 11) || (value == 0));
    }
    
    public boolean askSecondRedundantIoc(String iocName) {
        long value;
        IProcessVariableAddress pv = 
                pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix()
                                                      + "://" + iocName + ":rmtStateB");
        try {
            try {
                value = service.readValueSynchronously(pv, ValueType.LONG);
            } catch (NullPointerException e) {
                value = -1;
            }
        } catch(ConnectionException ce) {
            value = -1;
        }

        return ((value == 6) || (value == 11) || (value == 0));
    }
}
