
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.application.weightrequest.server;

import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.ProcessVariableExistanceCallback;
import gov.aps.jca.cas.ProcessVariableExistanceCompletion;
import gov.aps.jca.cas.ServerContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.csstudio.application.weightrequest.Activator;
import org.csstudio.application.weightrequest.data.MetaData;
import org.csstudio.application.weightrequest.data.WeightFloatingPV;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 01.12.2011
 */
public class CaServer {
    
    private static Logger LOG = LoggerFactory.getLogger(CaServer.class);
    
    private GatewayServerImpl server;
    
    private ServerContext context = null;
    
    private String hostName;
    
    private HashMap<String, WeightFloatingPV> pvMap;

    public CaServer() {
        hostName = null;
        server = null;
        pvMap = new HashMap<String, WeightFloatingPV>();
    }

    private void initialize() throws CAException {
      
        createLocalHostName();

        JCALibrary jca = JCALibrary.getInstance();

        setServer(new GatewayServerImpl(this));

        IPreferencesService pref = Platform.getPreferencesService();

        String pvName = pref.getString(Activator.PLUGIN_ID, "pvName", "", null);
        String pvEgu = pref.getString(Activator.PLUGIN_ID, "pvEgu", "", null);
        short pvPrecision = (short)pref.getInt(Activator.PLUGIN_ID, "pvPrecision", 0, null);
        Double lowerWarningValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "lowerWarningValue", 0.0D, null));
        Double upperWarningValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "upperWarningValue", 0.0D, null));
        Double lowerAlarmValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "lowerAlarmValue", 0.0D, null));
        Double upperAlarmValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "upperAlarmValue", 0.0D, null));
        Double lowerControlValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "lowerControlValue", 0.0D, null));
        Double upperControlValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "upperControlValue", 0.0D, null));
        Double lowerDisplayValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "lowerDisplayValue", 0.0D, null));
        Double upperDisplayValue = Double.valueOf(pref.getDouble(Activator.PLUGIN_ID, "upperDisplayValue", 0.0D, null));
        long valueRefreshRate = pref.getLong(Activator.PLUGIN_ID, "valueRefreshRate", 60000L, null);

        MetaData mData = new MetaData();
        mData.setPvName(pvName);
        mData.setEgu(pvEgu);
        mData.setPrecision(pvPrecision);
        mData.setAlarmLimits(lowerWarningValue, upperWarningValue, lowerAlarmValue, upperAlarmValue);
        mData.setControlValues(lowerControlValue, upperControlValue);
        mData.setDisplayValues(lowerDisplayValue, upperDisplayValue);

        WeightFloatingPV weightPv = new WeightFloatingPV(pvName, null, mData, valueRefreshRate);
        pvMap.put(pvName, weightPv);

        server.registerProcessVaribale(weightPv);

        context = jca.createServerContext("com.cosylab.epics.caj.cas.CAJServerContext", getServer());

        LOG.info(this.context.getVersion().getVersionString());
        context.printInfo();
    }

    public void run() {
        try {
            
            initialize();
            LOG.info("Start caGateway on: {}", this.hostName);

            context.run(0);
            LOG.info("Stop caGateway on: {}", this.hostName);
        } catch (CAException e) {
            LOG.info("[*** CAException ***]: {}", e.getMessage());
        }
    }

    public final synchronized void stop() {
        
        LOG.info("stop() was called, stopping server");

        if (!pvMap.isEmpty()) {
            for (WeightFloatingPV o : pvMap.values()) {
                o.close();
            }
        }
        
        try {
            context.shutdown();
        } catch (IllegalStateException ise) {
            LOG.error("[*** IllegalStateException ***]: Context shutdown failed: {}", ise.getMessage());
        } catch (CAException cae) {
            LOG.error("[*** CAException ***]: Context shutdown failed: {}", cae.getMessage());
        }
        
        try {
            context.destroy();
        } catch (IllegalStateException ise) {
            LOG.error("[*** IllegalStateException ***]: Context shutdown failed: {}", ise.getMessage());
        } catch (CAException cae) {
            LOG.error("[*** CAException ***]: Context shutdown failed: {}", cae.getMessage());
        }
    }

    public boolean containsPV(String pv) {
        return pvMap.containsKey(pv);
    }

    public Logger getLogger() {
        return LOG;
    }

    public void setServer(GatewayServerImpl s) {
        server = s;
    }

    public GatewayServerImpl getServer() {
        return server;
    }

    private void createLocalHostName() {
        
        hostName = "localhost-NA";
        try {
            InetAddress localMachine = InetAddress.getLocalHost();
            hostName = localMachine.getHostName();
        } catch (UnknownHostException uhe) {
            LOG.error("[*** UnknownHostException ***]: " + uhe.getMessage());
        }
    }

    class GatewayServerImpl extends DefaultServerImpl {
        
        private CaServer mainServer;
        public GatewayServerImpl(CaServer s) {
            this.mainServer = s;
        }

        @Override
        public ProcessVariableExistanceCompletion
                 processVariableExistanceTest(String aliasName,
                                              InetSocketAddress clientAddress,
                                              ProcessVariableExistanceCallback asyncCompletionCallback)
                             throws CAException,
                                    IllegalArgumentException,
                                    IllegalStateException {
            
            ProcessVariableExistanceCompletion result = 
                                        ProcessVariableExistanceCompletion.DOES_NOT_EXIST_HERE;

            mainServer.getLogger()
                         .debug("processVariableExistanceTest(): "
                                 + aliasName + ", "
                                 + clientAddress.toString());

            if (mainServer.containsPV(aliasName)) {
                result = ProcessVariableExistanceCompletion.EXISTS_HERE;
                mainServer.getLogger().debug("EXISTS_HERE");
            }

            return result;
        }
    }
}
