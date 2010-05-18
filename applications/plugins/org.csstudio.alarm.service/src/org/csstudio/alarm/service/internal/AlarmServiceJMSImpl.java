/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 * 
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 * 
 * $Id$
 */
package org.csstudio.alarm.service.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;

import com.cosylab.util.CommonException;

/**
 * JMS based implementation of the AlarmService.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmServiceJMSImpl implements IAlarmService {
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    /**
     * Constructor.
     */
    public AlarmServiceJMSImpl() {
        // Nothing to do
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final IAlarmConnection newAlarmConnection() {
        return new AlarmConnectionJMSImpl();
    }
    
    @Override
    public final void retrieveInitialState(@Nonnull List<? extends IAlarmInitItem> initItems) {
        List<Element> pvsUnderWay = new ArrayList<Element>();
        for (IAlarmInitItem initItem : initItems) {
            registerPVs(pvsUnderWay, initItem);
        }
        
        waitFixedTime();
        
        for (Element pvUnderWay : pvsUnderWay) {
            deregisterPVs(pvUnderWay);
        }
    }

    private void waitFixedTime() {
        try {
            // TODO jp use constant from prefs here
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            _log.warn(this, "retrieveInitialState was interrupted ", e);
        }
    }

    private void deregisterPVs(@Nonnull Element pvUnderWay) {
        try {
            DalPlugin.getDefault().getSimpleDALBroker()
                    .deregisterListener(pvUnderWay._connectionParameters, pvUnderWay._listener);
        } catch (InstantiationException e) {
            _log.error(this, "Error in deregisterPVs", e);
        } catch (CommonException e) {
            _log.error(this, "Error in deregisterPVs", e);
        }
    }

    private void registerPVs(@Nonnull List<Element> pvsUnderWay, @Nonnull IAlarmInitItem initItem) {
        try {
            Element pvUnderWay = new Element();
            pvUnderWay._connectionParameters = newConnectionParameters(initItem.getPVName());
            pvUnderWay._listener = new ChannelListenerForInit(initItem);
            DalPlugin.getDefault().getSimpleDALBroker()
                    .registerListener(pvUnderWay._connectionParameters, pvUnderWay._listener);
            pvsUnderWay.add(pvUnderWay);
        } catch (InstantiationException e) {
            _log.error(this, "Error in registerPVs", e);
        } catch (CommonException e) {
            _log.error(this, "Error in registerPVs", e);
        }
    }
    
    @Nonnull
    private ConnectionParameters newConnectionParameters(@Nonnull String pvName) {
        // TODO jp what about Double.class?
        return new ConnectionParameters(newRemoteInfo(pvName), Double.class);
    }
    
    @Nonnull
    private RemoteInfo newRemoteInfo(@Nonnull String pvName) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, null, null);
    }
    
    /**
     * Listener for retrieval of initial state
     */
    private static class ChannelListenerForInit implements ChannelListener {
        private final IAlarmInitItem _initItem;
        
        public ChannelListenerForInit(@Nonnull IAlarmInitItem initItem) {
            _initItem = initItem;
        }
        
        @Override
        public void channelDataUpdate(@SuppressWarnings("unused") AnyDataChannel channel) {
            // Nothing to do
        }
        
        @Override
        public void channelStateUpdate(@Nonnull AnyDataChannel channel) {
            System.out.println("Alarm-Tree node initializer: channelStateUpdate "
                    + channel.getUniqueName() + "\n               " + System.currentTimeMillis()
                    + ", state " + channel.getStateInfo());
            _initItem.init(new AlarmMessageDALImpl(channel.getData()));
        }
    }
    
    // CHECKSTYLE:OFF
    private static class Element {
        ConnectionParameters _connectionParameters;
        ChannelListenerForInit _listener;
    }
    // CHECKSTYLE:ON
    
}
