/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.platform.internal.simpledal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard implementation of {@link IProcessVariableConnectionService}.
 *
 * @author Sven Wende
 */
public class ProcessVariableConnectionService implements IProcessVariableConnectionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessVariableConnectionService.class);

    private Map<ConnectorIdentification, AbstractConnector> _connectors;

    private IConnectorFactory _connectorFactory;

    /**
     * A cleanup thread which disposes unnecessary connections.
     */
    private Thread _cleanupThread;

    /**
     * The singleton instance.
     */
    private static IProcessVariableConnectionService _instance;

    /**
     * Constructor.
     */
    public ProcessVariableConnectionService(IConnectorFactory connectorFactory) {
        assert connectorFactory != null;
        _connectors = new HashMap<ConnectorIdentification, AbstractConnector>();
        _connectorFactory = connectorFactory;
        _cleanupThread = new CleanupThread();
        _cleanupThread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IConnector> getConnectors() {
        List<IConnector> result = new ArrayList<IConnector>();

        result.addAll(_connectors.values());
        return result;

    }

    @Override
    public int getNumberOfActiveConnectors() {
        int result = 0;
        synchronized (_connectors) {
            for (IConnector c : _connectors.values()) {
                result += c != null ? 1 : 0;
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E readValueSynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType) throws ConnectionException {
        AbstractConnector connector = getConnector(processVariableAddress, valueType);

        E value = null;

        try {
            if (processVariableAddress.isCharacteristic()) {
                value = connector.getCharacteristicSynchronously(processVariableAddress.getCharacteristic(), valueType);
            } else {
                value = connector.getValueSynchronously();
            }
        } catch (Exception e) {
            LOG.debug(e.toString());
            throw new ConnectionException(e);
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readValueAsynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType,
            IProcessVariableValueListener listener) {

        AbstractConnector connector = getConnector(processVariableAddress, valueType);

        if (processVariableAddress.isCharacteristic()) {
            connector.getCharacteristicAsynchronously(processVariableAddress.getCharacteristic(), valueType, listener);
        } else {
            connector.getValueAsynchronously(listener);
        }
    }

    @Override
    public boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType valueType)
            throws ConnectionException {
        AbstractConnector connector = getConnector(processVariableAddress, valueType);
        boolean result = false;

        try {
            result = connector.setValueSynchronously(value);
        } catch (Exception e) {
            LOG.debug(e.toString());
            throw new ConnectionException(e);
        }

        return result;
    }

    @Override
    public void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType valueType,
            IProcessVariableWriteListener listener) {
        AbstractConnector connector = getConnector(processVariableAddress, valueType);
        connector.setValueAsynchronously(value, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void register(IProcessVariableValueListener listener, IProcessVariableAddress pv, ValueType valueType) {
        AbstractConnector connector = getConnector(pv, valueType);
        connector.addProcessVariableValueListener(pv.getCharacteristic(), listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void unregister(IProcessVariableValueListener listener) {
        // we remove the listener from all connectors
        synchronized (_connectors) {
            for (AbstractConnector c : _connectors.values()) {
                if (c.removeProcessVariableValueListener(listener)) {
                    LOG.debug("UNREGISTER '" + c.getName());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv) {
        AbstractConnector connector = getConnector(pv, pv.getValueTypeHint() != null ? pv.getValueTypeHint() : ValueType.DOUBLE);

        return connector.isSettable();
    }

    /**
     * Returns a connector for the specified process variable and type. If a
     * connector exists already it is returned, otherwise a new connector will
     * be created.
     *
     * @param pv
     *            the process variable
     * @param valueType
     *            the value type
     * @return the connector (it is ensured, that this is not null)
     */
    private AbstractConnector getConnector(IProcessVariableAddress pv, ValueType valueType) {
        AbstractConnector connector = null;

        // there is one connector for each pv-type-combination
        ConnectorIdentification key = new ConnectorIdentification(pv, valueType);

        // get a connector
        synchronized (_connectors) {

            connector = (AbstractConnector) _connectors.get(key);

            // ... reuse existing connector a create a new one
            if (connector == null) {
                connector = _connectorFactory.createConnector(pv, valueType);
                _connectors.put(key, connector);
            }

            connector.block();
        }

        assert connector != null;

        connector.init();

        return connector;
    }

    /**
     * Injects a connector factory.
     *
     * @param connectorFactory
     *            the connector factory
     */
    public void setConnectorFactory(IConnectorFactory connectorFactory) {
        _connectorFactory = connectorFactory;
    }

    /**
     * Cleanup thread, which removes connectors that are not needed anymore.
     *
     * @author swende
     *
     */
    final class CleanupThread extends Thread {

        private long _sleepTime;

        /**
         * Flag that indicates if the thread should continue its execution.
         */
        private boolean _running;

        /**
         * Standard constructor.
         */
        CleanupThread() {
            super("ProcessVariableConnectionService#CleanupThread");
            // Have to be a daemon to be automatically stopped on a system
            // shutdown.
            this.setDaemon(true);
            _running = true;
            _sleepTime = 10000;
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            while (_running) {

                try {
                    sleep(_sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                doCleanup();
                yield();
            }
        }

        /**
         * Stops the execution of this BundelingThread.
         */
        public void stopExecution() {
            _running = false;
        }

        /**
         * Performs the cleanup.
         */
        private void doCleanup() {
            List<AbstractConnector> deletedConnectors = new ArrayList<AbstractConnector>();

            synchronized (_connectors) {
                List<ConnectorIdentification> deleteCandidates = new ArrayList<ConnectorIdentification>();
                Iterator<ConnectorIdentification> it = _connectors.keySet().iterator();

                while (it.hasNext()) {
                    ConnectorIdentification key = it.next();
                    AbstractConnector connector = _connectors.get(key);

                    // perform cleanup
                    connector.cleanupWeakReferences();

                    // dispose if possible
                    if (connector.isDisposable()) {
                        deleteCandidates.add(key);
                    }
                }

                for (ConnectorIdentification key : deleteCandidates) {
                    AbstractConnector connector = _connectors.remove(key);
                    deletedConnectors.add(connector);
                }
            }

            // important: dispose the connectors outside the synchronized block
            for (AbstractConnector connector : deletedConnectors) {
                connector.dispose();
            }

            LOG.debug("Cleanup-Thread: " + deletedConnectors.size() + " connectors disposed!");
        }
    }

}
