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

import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The worker providing the persistence of the heartbeat for this engine.
 *
 * @author bknerr
 * @since 13.04.2011
 */
public class HeartBeatWorker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatWorker.class);

    private final IServiceProvider _provider;

    private final ArchiveEngineId _engineId;

    /**
     * Constructor.
     */
    public HeartBeatWorker(@Nonnull final ArchiveEngineId engineId,
                           @Nonnull final IServiceProvider provider) {
        _engineId = engineId;
        _provider = provider;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            updateEngineHeartBeat(_provider, _engineId);
        } catch (final ArchiveServiceException e) {
            LOG.error("Exception within service impl. Archive engine heart beat persistence failed.", e);
        } catch (final Throwable t) {
            LOG.error("Unknown throwable. Thread HeartBeatWorker is terminated");
            t.printStackTrace();
        }
    }

    private void updateEngineHeartBeat(@Nonnull final IServiceProvider provider,
                                       @Nonnull final ArchiveEngineId engineId)
                                       throws ArchiveServiceException {
        try {
            final IArchiveEngineFacade engineFacade  = provider.getEngineFacade();
            engineFacade.updateEngineIsAlive(engineId, TimeInstantBuilder.fromNow());
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("Service unavailable. Engine heartbeat for engine " + engineId + " not written.", e);
        }
    }
}
