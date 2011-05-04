
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
 *
 */

package org.csstudio.websuite.dao;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.websuite.dataModel.BasicMessage;

/**
 * 
 * 
 * @author Markus Moeller
 */
public class RecordDataReceiver
{
    /**  */
    private static RecordDataReceiver instance;
    
    /**  */
    private IProcessVariableConnectionService service;
    
    /**  */
    private ProcessVariableAdressFactory pvFactory;

    /**
     * 
     */
    private RecordDataReceiver()
    {
        // get a service instance (all applications using the same shared instance will share channels, too)
        service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
        
        // get a factory for process variable addresses 
        pvFactory = ProcessVariableAdressFactory.getInstance();
    }
    
    /**
     * 
     * @return
     */
    public synchronized static RecordDataReceiver getInstance()
    {
        if(instance == null)
        {
            instance = new RecordDataReceiver();
        }
        
        return instance;
    }
    
    /**
     * 
     * @param recordName
     * @return
     */
    public BasicMessage getRecordData(String recordName)
    {
        BasicMessage result = new BasicMessage();
        IProcessVariableAddress pv = null;
        String value = null;
        
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + recordName);

        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                result.getHashMap().put("VALUE", "null");
            }
            else
            {
                result.getHashMap().put("VALUE", value);
            }
        }
        catch(ConnectionException ce)
        {
            result.getHashMap().put("VALUE", "ERROR");
        }

        pv = null;
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + recordName + ".EGU");

        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                result.getHashMap().put("EGU", "null");
            }
            else
            {
                result.getHashMap().put("EGU", value);
            }
        }
        catch(ConnectionException ce)
        {
            result.getHashMap().put("VALUE", "ERROR");
        }

        pv = null;
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + recordName + ".DESC");

        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                result.getHashMap().put("DESCRIPTION", "null");
            }
            else
            {
                result.getHashMap().put("DESCRIPTION", value);
            }
        }
        catch(ConnectionException ce)
        {
            result.getHashMap().put("DESCRIPTION", "ERROR");
        }

        pv = null;
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + recordName + ".STAT");
        
        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                result.getHashMap().put("STATUS", "null");
            }
            else
            {
                result.getHashMap().put("STATUS", value);
            }
        }
        catch(ConnectionException ce)
        {
            result.getHashMap().put("STATUS", "ERROR");
        }

        pv = null;
        pv = pvFactory.createProcessVariableAdress(ControlSystemEnum.EPICS.getPrefix() + "://" + recordName + ".SEVR");

        try
        {
            value = service.readValueSynchronously(pv, ValueType.STRING);
            if(value == null)
            {
                result.getHashMap().put("SEVERITY", "null");
            }
            else
            {
                result.getHashMap().put("SEVERITY", value);
            }
        }
        catch(ConnectionException ce)
        {
            result.getHashMap().put("SEVERITY", "ERROR");
        }

        pv = null;

        return result;
    }
}
