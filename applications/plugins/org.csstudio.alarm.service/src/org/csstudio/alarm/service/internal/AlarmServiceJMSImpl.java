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

import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;

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

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AlarmServiceJMSImpl.class);

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
    public final void retrieveInitialState(@Nonnull final List<IAlarmInitItem> initItems) {
        LOG.debug("retrieveInitialState for " + initItems.size() + " items");

        // There may be more than a thousand pv for which the initial state is requested at once.
        // Therefore the process of registering is performed in chunks with a delay.
        // After each chunk the broker releases all pvs so all resources will be freed.

        int pvChunkSize = AlarmPreference.ALARMSERVICE_PV_CHUNK_SIZE.getValue();
        int pvChunkWaitMsec = AlarmPreference.ALARMSERVICE_PV_CHUNK_WAIT_MSEC.getValue();

        ChunkableCollection<IAlarmInitItem> pvs = new ChunkableCollection<IAlarmInitItem>(initItems, pvChunkSize);

        for (Collection<IAlarmInitItem> currentChunk : pvs) {
            SimpleDALBroker broker = SimpleDALBroker.newInstance(new CssApplicationContext("CSS"));
            registerPVs(broker, currentChunk);
            LOG.debug("retrieveInitialState about to wait for chunk");
            waitFixedTime(pvChunkWaitMsec);
            broker.releaseAll();
        }
        LOG.debug("retrieveInitialState finished");
    }

    @Override
    @Nonnull
    public final IAlarmResource createAlarmResource(@CheckForNull final List<String> topics,
                                                 @CheckForNull final String filepath) {
        return new AlarmResource(topics, filepath);
    }

    private void waitFixedTime(final int delayInMsec) {
        try {
            Thread.sleep(delayInMsec);
        } catch (final InterruptedException e) {
            LOG.warn("retrieveInitialState was interrupted ", e);
        }
    }

    private void registerPVs(@Nonnull final SimpleDALBroker broker, @Nonnull final Collection<IAlarmInitItem> initItems) {
        for (IAlarmInitItem initItem : initItems) {
            try {
                // REVIEW (jpenning): hard coded type in connection parameter
                broker.registerListener(newConnectionParameters(initItem.getPVName()),
                                        new DynamicValueListenerForInit<String, StringProperty>(initItem));
            } catch (final InstantiationException e) {
                handleRegistrationError(e, initItem);
            } catch (final CommonException e) {
                handleRegistrationError(e, initItem);
            }
        }
    }

    private void handleRegistrationError(@Nonnull final Exception e, @Nonnull final IAlarmInitItem initItem) {
        LOG.error("Error in registerPVs. PV " + initItem.getPVName() + " was not registered", e);
    }

    @Nonnull
    private ConnectionParameters newConnectionParameters(@Nonnull final String pvName) {
        // REVIEW (jpenning): hard coded type in connection parameter
        return new ConnectionParameters(newRemoteInfo(pvName), String.class);
    }

    @Nonnull
    private RemoteInfo newRemoteInfo(@Nonnull final String pvName) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, null, null);
    }

    /**
     * Listener for retrieval of initial state
     */
    private static class DynamicValueListenerForInit<T, P extends SimpleProperty<T>> extends
            DynamicValueAdapter<T, P> {
        private static final Logger LOG_INNER =
            CentralLogger.getInstance().getLogger(AlarmServiceJMSImpl.DynamicValueListenerForInit.class);

        private final IAlarmInitItem _initItem;

        public DynamicValueListenerForInit(@Nonnull final IAlarmInitItem initItem) {
            _initItem = initItem;
        }

        @Override
        public void conditionChange(@CheckForNull final DynamicValueEvent<T, P> event) {
            // Currently we are not interested in conditionChange-Events
            //            LOG_INNER.debug("conditionChange received " + event.getCondition() + " for "
            //                    + event.getProperty().getUniqueName());
        }

        @Override
        public void valueChanged(@CheckForNull final DynamicValueEvent<T, P> event) {
            if (event != null) {
//                LOG_INNER.debug("valueChanged received " + event.getCondition() + " for "
//                        + event.getProperty().getUniqueName());
                if (AlarmMessageDALImpl.canCreateAlarmMessageFrom(event.getProperty(),
                                                                  event.getData())) {

                    _initItem.init(AlarmMessageDALImpl.newAlarmMessage(event.getProperty(),
                                                                       event.getData()));
                } else {
                    LOG_INNER.warn("Could not create alarm message for "
                            + event.getProperty().getUniqueName());
                }
            } // else ignore
        }

    }

    // CHECKSTYLE:OFF
    private static class Element {
        ConnectionParameters _connectionParameters;
        DynamicValueListener<?, ?> _listener;
    }
    // CHECKSTYLE:ON

}
