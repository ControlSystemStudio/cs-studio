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
 */
package org.csstudio.archive.common.engine.pvmanager;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.jca.JCAChannelHandler;

import com.google.common.base.Predicate;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 30.08.2011
 */
public class DesyJCAChannelHandler extends JCAChannelHandler {

    private final Predicate<DBR> _validator;
    private EpicsMetaData _desyMeta;

    // TODO (bknerr) : make this one protected in super type
    private final int _monitorMask;

    /**
     * Constructor.
     */
    public DesyJCAChannelHandler(@Nonnull final String channelName,
                                 @Nullable final Context context,
                                 final int monitorMask) {
        super(channelName, context, monitorMask);
        _validator = new DesyDbrTimeValidator();
        // TODO (bknerr) : make this one protected in super type
        _monitorMask = monitorMask;
    }

    @Override
    protected synchronized void setup(@Nonnull final Channel channel) throws CAException {
        vTypeFactory = DesyTypeFactoryProvider.matchFor(channel);

        // If metadata is needed, get it
        final DBRType epicsMetaType = vTypeFactory.getEpicsMetaType();
        if (epicsMetaType != null) {
            // Need to use callback for the listener instead of doing a synchronous get
            // (which seemed to perform better) because JCA (JNI implementation)
            // would return an empty list of labels for the Enum metadata
            channel.get(epicsMetaType, 1, new GetListener() {
                @Override
                public void getCompleted(@Nonnull final GetEvent ev) {
                    synchronized(DesyJCAChannelHandler.this) {
                        metadata = ev.getDBR();
                        // In case the metadata arrives after the monitor
                        //dispatchValue();
                    }
                }
            });
        }

        channel.addMonitor(vTypeFactory.getEpicsValueType(), channel.getElementCount(), _monitorMask, monitorListener);
        // Flush the entire context (it's the best we can do)
        channel.getContext().flushIO();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean updateCache(@Nonnull final MonitorEvent event,
                               @Nonnull final ValueCache<?> cache) {
        final DBR rawDBR = event.getDBR();

        if (_desyMeta == null && metadata != null) {
            _desyMeta = ((DesyTypeFactory) vTypeFactory).createMetaData((STS) metadata);
        }

        if (!_validator.apply(rawDBR)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        final EpicsSystemVariable newValue = ((DesyTypeFactory) vTypeFactory).createValue(getChannelName(),
                                                                                          rawDBR,
                                                                                          metadata,
                                                                                          _desyMeta);
        cache.setValue(newValue);
        return true;
    }
}
