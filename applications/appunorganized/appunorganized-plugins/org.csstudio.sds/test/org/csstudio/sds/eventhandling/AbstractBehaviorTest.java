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
package org.csstudio.sds.eventhandling;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumSet;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 27.10.2011
 */
abstract public class AbstractBehaviorTest<M extends AbstractWidgetModel, B extends AbstractBehavior<M>> {

    private B _behavior;
    private M _modelMock;
    private DynamicValueProperty _dynamicValueProperty;
    private AnyDataChannel _anyDataChannel;
    private AnyData _anyData;
    private InOrder _inOrder;
    private MetaData _metaData;

    /**
     * Constructor.
     */
    public AbstractBehaviorTest() {
        super();
    }

    protected abstract M createModelMock();
    protected abstract B createBehavior();

    protected M getModelMock() {
        return _modelMock;
    }
    @Before
    public void setUp() throws Exception {
        _behavior = createBehavior();
        _modelMock = createModelMock();
        _dynamicValueProperty = mock(DynamicValueProperty.class);
        _anyDataChannel = mock(AnyDataChannel.class);
        _anyData = mock(AnyData.class);
        _metaData = mock(MetaData.class);
        setInOrder(inOrder(_modelMock));
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        when(_anyData.numberValue()).thenReturn(0);
        when(_anyData.getParentProperty()).thenReturn(_dynamicValueProperty);
        when(_anyData.getParentChannel()).thenReturn(_anyDataChannel);
        when(_anyData.getMetaData()).thenReturn(_metaData);
        when(_metaData.getPrecision()).thenReturn(4);
        setHasNoLiveData();
        _behavior.doInitialize(_modelMock);
        verifyDoInitialize();
    }

    /**
     *
     */
    private void setHasLiveData() {
        when(_dynamicValueProperty.getCondition()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.HAS_LIVE_DATA)));
    }
    private void setHasNoLiveData() {
        when(_dynamicValueProperty.getCondition()).thenReturn(new DynamicValueCondition(EnumSet.noneOf(DynamicValueState.class)));
    }

    protected void verifyDoInitialize() {

    }

    protected abstract void verifyConnectionStateConnectedWithoutData();
    protected abstract void verifyValueChangeConnectedWithoutData();

    @Test
    public void connectionStateConnectedWithoutDataTest() throws Exception {
        setHasNoLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithoutData();
    }
    protected abstract void verifyConnectionStateConnectedWithData();
    protected abstract void verifyValueChangeConnectedWithData();

    @Test
    public void connectionStateConnectedWithDataTest() throws Exception {
        setHasLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithData();
    }

    protected abstract void verifyConnectionStateConnecting();

    @Test
    public void connectionStateConnectingTest() throws Exception {
        setHasNoLiveData();
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnecting();
    }

    protected abstract void verifyConnectionStateFailed();

    @Test
    public void connectionStateConnectionFailedTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTION_FAILED);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateFailed();
    }

    protected abstract void verifyConnectionStateConnectionLost();

    @Test
    public void connectionStateConnectionLostTest() throws Exception {
        setHasLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTION_LOST);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectionLost();
    }

    protected abstract void verifyConnectionStateDestroyed();

    @Test
    public void connectionStateDestroyedTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.DESTROYED);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateDestroyed();
    }

    protected abstract void verifyConnectionStateDisconnecting();

    @Test
    public void connectionStateDisconnectingTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.DISCONNECTING);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateDisconnecting();
    }

    protected abstract void verifyConnectionStateDisconnected();

    @Test
    public void connectionStateDisconnectedTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.DISCONNECTED);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateDisconnected();
    }

    protected abstract void verifyConnectionStateInitial();

    @Test
    public void connectionStateInitialTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.INITIAL);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateInitial();
    }

    protected abstract void verifyConnectionStateOperational();
    protected abstract void verifyValueChangeOperational();

    @Test
    public void connectionStateOperationalTest() throws Exception {
        setHasLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM)));
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateOperational();
    }

    protected abstract void verifyConnectionStateReady();

    @Test
    public void connectionStateReadyTest() throws Exception {
        setHasNoLiveData();
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.READY);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateReady();
    }

    @Test
    public void failOverLiveCycleTest() throws Exception {
        // The Livecycle:
        //       1: ConnectionState.INITIAL;
        //       2: ConnectionState.CONNECTING;
        //       3: ConnectionState.READY;
        //       4: ConnectionState.CONNECTED;
        //       5: ConnectionState.OPERATIONAL;
        //       6: ConnectionState.DISCONNECTING;
        //       7: ConnectionState.DISCONNECTED;
        //       8: ConnectionState.CONNECTING;
        //       9: ConnectionState.READY;
        //      10: ConnectionState.CONNECTED;
        //      11: ConnectionState.OPERATIONAL;

        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        when(_anyDataChannel.isRunning()).thenReturn(false);
        setHasNoLiveData();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.INITIAL);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateInitial();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnecting();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.READY);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateReady();

        setHasNoLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithoutData();

        setHasLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithData();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateOperational();

        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeOperational();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.DISCONNECTING);
        when(_anyDataChannel.isRunning()).thenReturn(false);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateDisconnecting();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.DISCONNECTED);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateDisconnected();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnecting();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.READY);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateReady();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithData();

        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeConnectedWithData();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateOperational();

    }

    @Test
    public void realDalFailOverLiveCycleTest() throws Exception {
        /* The Livecycle:
         *     Update      State                               isConnnected
         *      State    1: ConnectionState.CONNECTING;         false
         *      State    2: ConnectionState.CONNECTING;         false
         *      State    3: ConnectionState.CONNECTED;          true
         *      Date     4: ConnectionState.CONNECTED;          true
         *      State    5: ConnectionState.CONNECTED;          true
         *      State    6: ConnectionState.OPERATIONAL;        true
         *      Data     7: ConnectionState.OPERATIONAL;        true
         *                     .
         *                     .
         *                     .
         *      State    8: ConnectionState.CONNECTION_LOST;    true
         *      State    9: ConnectionState.CONNECTION_LOST;    true
         *      State   10: ConnectionState.CONNECTION_LOST;    true
         *      Data    11: ConnectionState.CONNECTED;          true
         *                     .
         *                     .
         *                     .
         */
        when(_anyDataChannel.getProperty()).thenReturn(_dynamicValueProperty);
        when(_anyDataChannel.getData()).thenReturn(_anyData);
        when(_anyDataChannel.isRunning()).thenReturn(false);

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnecting();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnecting();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithoutData();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeConnectedWithoutData();

        setHasLiveData();
        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectedWithData();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateOperational();

        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeOperational();

        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeOperational();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTION_LOST);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectionLost();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTION_LOST);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectionLost();

        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTION_LOST);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessConnectionStateChange(_modelMock, _anyDataChannel);
        verifyConnectionStateConnectionLost();

        when(_anyData.getSeverity()).thenReturn(new DynamicValueCondition(EnumSet.of(DynamicValueState.WARNING)));
        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        when(_anyDataChannel.isRunning()).thenReturn(true);
        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeConnectedWithData();

        _behavior.doProcessValueChange(_modelMock, _anyData);
        verifyValueChangeConnectedWithData();
    }

    private void setInOrder(final InOrder inOrder) {
        _inOrder = inOrder;
    }

    public InOrder getInOrder() {
        return _inOrder;
    }

}