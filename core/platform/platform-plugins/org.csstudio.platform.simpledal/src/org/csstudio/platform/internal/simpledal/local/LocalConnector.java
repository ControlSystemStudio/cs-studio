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
package org.csstudio.platform.internal.simpledal.local;

import org.csstudio.dal.Timestamp;
import org.csstudio.platform.ExecutionService;
import org.csstudio.platform.internal.simpledal.AbstractConnector;
import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local Connectors are connected to a simulated channels that live in the local
 * JVM.
 *
 * See {@link LocalChannelPool}.
 *
 * @author Sven Wende
 *
 */
@SuppressWarnings("unchecked")
public class LocalConnector extends AbstractConnector implements ILocalChannelListener {

    private static final Logger LOG = LoggerFactory.getLogger(LocalConnector.class);

    /**
     * Constructor.
     */
    public LocalConnector(IProcessVariableAddress pvAddress, ValueType valueType) {
        super(pvAddress, valueType);
        assert valueType != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(Object value) {
        doForwardValue(value, new Timestamp());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGetValueAsynchronously(final IProcessVariableValueListener listener) {
        Runnable r = new Runnable() {
            public void run() {
                Object value = LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).getValue();
                try {
                    listener.valueChanged(ConverterUtil.convert(value, getValueType()), new Timestamp());
                }catch(NumberFormatException nfe) {
                    LOG.warn(nfe.toString());
                }
            }
        };

        ExecutionService.getInstance().executeWithNormalPriority(r);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValueSynchronously() {
        Object value = LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).getValue();
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGetCharacteristicAsynchronously(String characteristicId, ValueType valueType, IProcessVariableValueListener listener)
            throws Exception {
        doGetValueAsynchronously(listener);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetCharacteristicSynchronously(String characteristicId, ValueType valueType) throws Exception {
        return doGetValueSynchronously();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValueAsynchronously(final Object value, final IProcessVariableWriteListener listener) {
        Runnable r = new Runnable() {
            public void run() {
                LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).setValue(value);

                if (listener != null) {
                    listener.success();
                }
            }
        };

        ExecutionService.getInstance().executeWithNormalPriority(r);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doSetValueSynchronously(Object value) {
        LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).setValue(value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInit() {
        LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).addListener(this);
        forwardConnectionState(ConnectionState.CONNECTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDispose() {
        LocalChannelPool.getInstance().getChannel(getProcessVariableAddress(), getValueType()).removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SettableState doIsSettable() {
        return SettableState.SETTABLE;
    }

}
