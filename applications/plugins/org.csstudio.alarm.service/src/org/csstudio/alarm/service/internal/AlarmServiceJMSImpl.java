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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.SimpleProperty;
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
    public final void retrieveInitialState(@Nonnull final List<? extends IAlarmInitItem> initItems) {
        LOG.debug("retrieveInitialState for " + initItems.size() + " items");

        // There may be more than a thousand pv for which the initial state is requested at once.
        // Therefore the process of registering is performed in chunks with a delay of approx. 100 msec.
        // TODO (jpenning) define prefs for chunk size and delay times

        final List<Element> pvsUnderWay = new ArrayList<Element>();

        for (int i = 0; i < initItems.size(); i++) {
            registerPV(pvsUnderWay, initItems.get(i));
            if ((i % 500) == 0) {
                waitFixedTime(100);
            }
        }

        LOG.debug("retrieveInitialState about to wait");
        waitFixedTime(1000);

        // The process of deregistering is also performed in chunks with a delay
        LOG.debug("retrieveInitialState about to deregister " + pvsUnderWay.size() + " pvs");
        for (int i = 0; i < pvsUnderWay.size(); i++) {
            deregisterPV(pvsUnderWay.get(i));
            if ((i % 500) == 0) {
                waitFixedTime(100);
            }
        }

        LOG.debug("retrieveInitialState finished");
    }

    @Override
    @Nonnull
    public final IAlarmResource newAlarmResource(@CheckForNull final List<String> topics,
                                                 @CheckForNull final List<String> facilities,
                                                 @CheckForNull final String filepath) {
        return new AlarmResource(topics, facilities, filepath);
    }

    private void registerPV(@Nonnull final List<Element> pvsUnderWay,
                            @Nonnull final IAlarmInitItem initItem) {
        try {
            final Element pvUnderWay = new Element();
            pvUnderWay._connectionParameters = newConnectionParameters(initItem.getPVName());
            // TODO (jpenning) Review: hard coded Double.class in connection parameter
            pvUnderWay._listener = new DynamicValueListenerForInit<Double, DoubleProperty>(initItem);
            DalPlugin.getDefault().getSimpleDALBroker()
                    .registerListener(pvUnderWay._connectionParameters,
                                      pvUnderWay._listener);
            pvsUnderWay.add(pvUnderWay);
        } catch (final InstantiationException e) {
            LOG.error("Error in registerPVs", e);
        } catch (final CommonException e) {
            LOG.error("Error in registerPVs", e);
        }
    }

    private void waitFixedTime(final int delayInMsec) {
        try {
            Thread.sleep(delayInMsec);
        } catch (final InterruptedException e) {
            LOG.warn("retrieveInitialState was interrupted ", e);
        }
    }

    private void deregisterPV(@Nonnull final Element pvUnderWay) {
        try {
            DalPlugin.getDefault().getSimpleDALBroker()
                    .deregisterListener(pvUnderWay._connectionParameters,
                                        pvUnderWay._listener);
        } catch (final InstantiationException e) {
            LOG.error("Error in deregisterPVs", e);
        } catch (final CommonException e) {
            LOG.error("Error in deregisterPVs", e);
        }
    }

    @Nonnull
    private ConnectionParameters newConnectionParameters(@Nonnull final String pvName) {
        // TODO (jpenning) what about Double.class?
        return new ConnectionParameters(newRemoteInfo(pvName), Double.class);
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
