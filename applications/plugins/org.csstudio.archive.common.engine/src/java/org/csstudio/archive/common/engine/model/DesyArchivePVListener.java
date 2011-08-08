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
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PV listener to translate monitored values into system variables and samples.
 *
 * @author bknerr
 * @since Mar 23, 2011
 * @param <V> the basic type of the variable's value
 * @param <T> the generic system variable type
 */
//CHECKSTYLE OFF: AbstractClassName
abstract class DesyArchivePVListener<V extends Serializable, T extends ISystemVariable<V>> implements PVListener {
//CHECKSTYLE ON: AbstractClassName

    private static final Logger LOG = LoggerFactory
            .getLogger(DesyArchivePVListener.class);

    private IServiceProvider _provider;
    private final String _channelName;
    private final ArchiveChannelId _channelId;
    private final Class<V> _typeClass;
    private volatile boolean _connected;

    private EpicsMetaData _metaData;

    private String _startInfo;

    private String _stopInfo;


    /**
     * Constructor.
     */
    DesyArchivePVListener(@Nonnull final IServiceProvider provider,
                          @Nonnull final String name,
                          @Nonnull final ArchiveChannelId id,
                          @Nonnull final Class<V> clazz) {
        _provider = provider;
        _channelName = name;
        _channelId = id;
        _typeClass = clazz;
        _connected = false;
    }


    @Override
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

    @Override
    public void pvValueUpdate(@Nonnull final PV pv) {
        try {
            if (!_connected) {
                _metaData = handleOnConnectionInformation(pv,
                                                          _channelId,
                                                          _typeClass,
                                                          _startInfo == null ? pv.getStateInfo() : _startInfo);
                _connected = true;
            }
            final ArchiveSample<V, T> sample = createSampleFromValue(pv, _channelName, _channelId, _metaData);

            if (sample != null) {
                if (sample.getValue() == null) {
                    LOG.warn("Value is null for channel id " + _channelName + "(" + _channelId + "). No sample created.");
                } else {
                    addSampleToBuffer(sample);
                }
            }

        } catch (final TypeSupportException e) {
            LOG.error("Handling of newly received IValue failed. Could not be converted to ISystemVariable", e);
            return;
        } catch (final Throwable t) {
            LOG.error("Unexpected exception in PVListener for: " + _channelName + "\n" + t.getMessage(), t);
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

    @CheckForNull
    private EpicsMetaData handleOnConnectionInformation(@Nonnull final PV pv,
                                                        @Nonnull final ArchiveChannelId id,
                                                        @Nonnull final Class<V> typeClass,
                                                        @Nonnull final String info)
                                                        throws EngineModelException,
                                                               TypeSupportException {

        final boolean connected = pv.isConnected();

        persistChannelStatusInfo(id, connected, info);

        final IMetaData metaData = pv.getValue().getMetaData();

        if (metaData != null) {
            return this.<ICompSer>handleMetaDataInfo(metaData, id, typeClass);
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
    EpicsMetaData handleMetaDataInfo(@Nonnull final IMetaData metaData,
                                     @Nonnull final ArchiveChannelId id,
                                     @Nonnull final Class<V> typeClass)
                                     throws TypeSupportException, EngineModelException {

        // FIXME (bknerr) : get rid of this IValue IMetaData shit
       final EpicsMetaData data = EpicsIMetaDataTypeSupport.toMetaData(metaData,
                                                                       typeClass);
       if (metaData instanceof INumericMetaData) {
            try {
                final IArchiveEngineFacade service = _provider.getEngineFacade();
                final EpicsGraphicsData<W> graphicsData = (EpicsGraphicsData<W>) data.getGrData();
                if (graphicsData != null) {
                    service.writeChannelDisplayRangeInfo(id,
                                                         graphicsData.getDisplayLow(),
                                                         graphicsData.getDisplayHigh());
                }
            } catch (final OsgiServiceUnavailableException e) {
                throw new EngineModelException("Service unavailable on updating display range info.", e);
            } catch (final ArchiveServiceException e) {
                throw new EngineModelException("Internal service error on updating display range info.", e);
            }
        }
       return data;
    }

    protected abstract void addSampleToBuffer(@Nonnull final IArchiveSample<V, T> sample);


    @SuppressWarnings("unchecked")
    @CheckForNull
    private ArchiveSample<V, T> createSampleFromValue(@Nonnull final PV pv,
                                                      @Nonnull final String name,
                                                      @Nonnull final ArchiveChannelId id,
                                                      @Nullable final EpicsMetaData metaData) throws TypeSupportException {
        final IValue value = pv.getValue();
        final EpicsSystemVariable<V> sv =
            (EpicsSystemVariable<V>) EpicsIValueTypeSupport.toSystemVariable(name, value, metaData);
        final ArchiveSample<V, T> sample = new ArchiveSample<V, T>(id, (T) sv, sv.getAlarm());

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
