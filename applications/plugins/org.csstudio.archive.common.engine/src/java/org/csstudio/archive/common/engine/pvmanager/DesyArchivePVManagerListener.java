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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.engine.model.ArchiveEngineSampleRescuer;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.utility.pv.PV;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 30.08.2011
 * @param <V> the basic element type of the variable's value collection
 * @param <T> the generic system variable type
 */
public abstract class DesyArchivePVManagerListener<V extends Serializable,
                                            T extends ISystemVariable<V>> implements PVReaderListener {

    private static final Logger LOG = LoggerFactory.getLogger(DesyArchivePVManagerListener.class);
    private static final Logger STRANGE_LOG = LoggerFactory.getLogger("StrangeThingsLogger");

    private IServiceProvider _provider;
    private final String _channelName;
    private final ArchiveChannelId _channelId;
    private final Class<V> _elemClass;
    private final Class<Collection<V>> _collClass;
    private volatile boolean _connected;

    private EpicsMetaData _metaData;

    private String _startInfo;

    private String _stopInfo;

    private final PVReader<?> _reader;
    /**
     * Constructor.
     */
    public DesyArchivePVManagerListener(@Nonnull final PVReader<?> reader,
                                        @Nonnull final IServiceProvider provider,
                                        @Nonnull final String name,
                                        @Nonnull final ArchiveChannelId id,
                                        @Nullable final Class<Collection<V>> collClazz,
                                        @Nonnull final Class<V> elemClazz) {
        _reader = reader;
        _provider = provider;
        _channelName = name;
        _channelId = id;
        _collClass = collClazz;
        _elemClass = elemClazz;
        _connected = false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void pvChanged() {
        try {
            final EpicsSystemVariable sysVar = (EpicsSystemVariable) _reader.getValue();
            if (!_connected) {
                final Exception lastException = _reader.lastException();
                _metaData = handleOnConnectionInformation(sysVar,
                                                          _channelId,
                                                          _elemClass,
                                                          lastException == null ? "Started" : lastException.getMessage());
                _connected = true;
            }
            handleValueUpdateInformation(sysVar);

        } catch (final TypeSupportException e) {
            LOG.error("Handling of newly received IValue failed. Could not be converted to ISystemVariable", e);
            return;
        } catch (final Throwable t) {
            LOG.error("Unexpected exception in PVListener for: {}:\n{}", _channelName, t.getMessage());
        }
        // TODO Auto-generated method stub

    }

    public void pvDisconnected(@CheckForNull final PV pv) {
        if (_connected && pv != null) {
            try {
                persistChannelStatusInfo(_channelId,
                                         false,
                                         _stopInfo == null ? pv.getStateInfo() : _stopInfo);
            } catch (final EngineModelException e) {
                LOG.error("Writing of disconnection for channel " + _channelName + " info failed.", e);
            }
            _metaData = null;
            _connected = false;
        }
    }


    /**
     * Interface to capture the type of a Comparable & Serializable object.
     * Damn Java.
     *
     * @author bknerr
     * @since 04.08.2011
     */
    private interface ICompSer extends Serializable, Comparable<Object> {
        // EMPTY
    }

    @SuppressWarnings("rawtypes")
    @CheckForNull
    private EpicsMetaData handleOnConnectionInformation(@Nonnull final EpicsSystemVariable pv,
                                                        @Nonnull final ArchiveChannelId id,
                                                        @Nonnull final Class<V> elemClass,
                                                        @Nonnull final String info)
                                                        throws EngineModelException,
                                                               TypeSupportException {

        //final boolean connected = pv.isConnected();
        final boolean connected =  _reader.lastException() == null;

        persistChannelStatusInfo(id, connected, info);

        final EpicsMetaData metaData = pv.getMetaData();

        if (metaData != null) {
            return this.<ICompSer>handleMetaDataInfo(metaData, id);
        }
        return null;
    }

    protected void persistChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                           final boolean connected,
                                           @Nonnull final String info)
                                           throws EngineModelException {
        try {
            final IArchiveEngineFacade service = _provider.getEngineFacade();
            service.writeChannelStatusInfo(id, connected, info, TimeInstantBuilder.fromNow());
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable to handle channel connection info.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Internal service error on handling channel connection info.", e);
        }
    }


    @SuppressWarnings("unchecked")
    @CheckForNull
    private <W extends Comparable<? super W> & Serializable>
    EpicsMetaData handleMetaDataInfo(@Nonnull final EpicsMetaData data,
                                     @Nonnull final ArchiveChannelId id)
                                     throws TypeSupportException, EngineModelException {

       final EpicsGraphicsData<W> grData = (EpicsGraphicsData<W>) data.getGrData();
       if (grData != null) {
            try {
                final IArchiveEngineFacade service = _provider.getEngineFacade();
                if (grData != null) {
                    service.writeChannelDisplayRangeInfo(id,
                                                         grData.getLowOperatingRange(),
                                                         grData.getHighOperatingRange());
                }
            } catch (final OsgiServiceUnavailableException e) {
                throw new EngineModelException("Service unavailable on updating display range info.", e);
            } catch (final ArchiveServiceException e) {
                throw new EngineModelException("Internal service error on updating display range info.", e);
            }
        }
       return data;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void handleValueUpdateInformation(@Nonnull final EpicsSystemVariable pv) throws TypeSupportException {
        final ArchiveSample<V, T> sample = createSampleFromValue(pv,
                                                                 _channelName,
                                                                 _channelId,
                                                                 _metaData,
                                                                 _collClass,
                                                                 _elemClass);

        if (sample == null || sample.getValue() == null) {
            return;
        }
        if (!addSampleToBuffer(sample)) {
            ArchiveEngineSampleRescuer.with((Collection) Collections.singleton(sample)).rescue();
        }
    }

    protected abstract boolean addSampleToBuffer(@Nonnull final IArchiveSample<V, T> sample);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @CheckForNull
    private ArchiveSample<V, T> createSampleFromValue(@Nonnull final EpicsSystemVariable sv,
                                                      @Nonnull final String name,
                                                      @Nonnull final ArchiveChannelId id,
                                                      @Nullable final EpicsMetaData metaData,
                                                      @CheckForNull final Class<Collection<V>> collClass,
                                                      @Nonnull final Class<V> elemClass) throws TypeSupportException {

        ArchiveSample<V, T> sample;
        if (Collection.class.isAssignableFrom(sv.getData().getClass())) {
            sample = new ArchiveMultiScalarSample(id, sv, sv.getAlarm());
        } else {
            sample = new ArchiveSample(id, sv, sv.getAlarm());
        }

        return sample;
    }


    public void setStartInfo(@Nonnull final String info) {
        _startInfo = info;

    }

    public void setStopInfo(@Nonnull final String info) {
        _stopInfo = info;
    }

    public void setProvider(@Nonnull final IServiceProvider provider) {
        _provider = provider;
    }
}
