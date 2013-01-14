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
package org.csstudio.persister.internal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.csstudio.persister.declaration.IPersistableService;
import org.csstudio.persister.declaration.IPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to persist the memento of the persistable service
 * 
 * This is a stateful service maintaining the memento of the given service.
 * The current implementation supports only one instance, therefore init may only be called once.
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public class PersistenceService implements IPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceService.class);
    private static final long MAX_DURATION_MSECS = 3000;
    private IPersistableService _persistableService;
    private String _filename;
    private ScheduledThreadPoolExecutor _executor;
    
    @Override
    @Nonnull
    public IPersistenceService init(@Nonnull final IPersistableService persistableService, @Nonnull final String filename) {
        _filename = filename;
        // guard: persistableService cannot be null
        if (persistableService == null) {
            throw new IllegalArgumentException("persistableService must be given");
        }
        // guard: init may only be called once
        if (_persistableService != null) {
            throw new IllegalStateException("Cannot call init more than once");
        }

        LOG.debug("persistence service init for service {}, storing in file {}", persistableService.getClass().getName(), filename);
        _persistableService = persistableService;
        return this;
    }

    @Override
    public void runPersister(final int delayInSeconds) {
        LOG.debug("runPersister with {} seconds delay", delayInSeconds);
        _executor = new ScheduledThreadPoolExecutor(1);
        Runnable runnable = newPersister();
        _executor.scheduleWithFixedDelay(runnable, delayInSeconds, delayInSeconds, TimeUnit.SECONDS);
    }
    
    @Override
    public void stopPersister() {
        LOG.debug("stopPersister");
        _executor.shutdown();
        try {
            saveMemento();
        } catch (IOException e) {
            LOG.error("saveMemento failed after shutdown of persistence service");
        }
    }

    // package-scoped for tests
    void saveMemento() throws IOException {
        LOG.debug("saveMemento");
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(_filename));
            Object memento = _persistableService.getMemento();
            //        o.writeObject(new Date());
            outputStream.writeObject(memento);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @Override
    public void restoreMemento() throws IOException, ClassNotFoundException {
        LOG.debug("restoreMemento");
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(_filename));
            //        Date date = (Date) o.readObject();
            Object memento = inputStream.readObject();
            inputStream.close();
            _persistableService.restoreMemento(memento);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Nonnull
    private Runnable newPersister() {
        return new Runnable() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    saveMemento();
                    long duration = System.currentTimeMillis() - start;
                    LOG.info("Memento was saved in {} msec", duration);
                    if (duration > MAX_DURATION_MSECS) {
                        LOG.warn("Saving of memento needed too long: {} msec", duration);
                    }
                } catch (IOException e) {
                    LOG.error("Cannot save memento", e);
                }
            }
        };
    }
}
