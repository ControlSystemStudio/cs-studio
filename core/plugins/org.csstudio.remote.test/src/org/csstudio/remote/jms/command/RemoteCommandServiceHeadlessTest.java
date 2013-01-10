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
 */
package org.csstudio.remote.jms.command;

import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Plugin-Test for the jms-based implementation of the remote command service.
 * Must be runs with a specific set of preferences using the test jms installation. To ensure this, the tests are
 * by default set to 'ignore'. See the preference initializer for details. 
 * 
 * @author jpenning
 * @since 18.01.2012
 */
public class RemoteCommandServiceHeadlessTest {
    
    @Test
    @Ignore
    public void testSendMultiple() throws RemoteCommandException {
        IRemoteCommandService service = new JmsRemoteCommandService();
        IRemoteCommandService.IListener listener = mock(IRemoteCommandService.IListener.class);
        service.register(ClientGroup.DESY_MKK, listener);
        
        service.sendCommand(ClientGroup.DESY_MKK, "reloadFromLdap");
        verify(listener).receiveCommand("reloadFromLdap");
        
        service.sendCommand(ClientGroup.DESY_MKK, "reloadFromLdap");
        verify(listener, times(2)).receiveCommand("reloadFromLdap");
    }
    
    @Test
    @Ignore
    public void testDeregisterListener() throws RemoteCommandException {
        IRemoteCommandService service = new JmsRemoteCommandService();
        IRemoteCommandService.IListener listener0 = mock(IRemoteCommandService.IListener.class);
        IRemoteCommandService.IListener listener1 = mock(IRemoteCommandService.IListener.class);
        service.register(ClientGroup.DESY_MKK, listener0);
        service.register(ClientGroup.DESY_MKK, listener1);
        service.deregister(listener0);
        
        service.sendCommand(ClientGroup.DESY_MKK, "reloadFromLdap");
        verify(listener1).receiveCommand("reloadFromLdap");
        verifyZeroInteractions(listener0);
    }

    @Test
    @Ignore
    public void testSendToClientGroup() throws RemoteCommandException {
        IRemoteCommandService service = new JmsRemoteCommandService();
        IRemoteCommandService.IListener listener = mock(IRemoteCommandService.IListener.class);
        IRemoteCommandService.IListener listenerDead = mock(IRemoteCommandService.IListener.class);
        service.register(ClientGroup.DESY_MKK, listener);
        service.register(ClientGroup.DESY_AMS, listenerDead);

        service.sendCommand(ClientGroup.DESY_MKK, "reloadFromLdap");
        verify(listener).receiveCommand("reloadFromLdap");
        verifyZeroInteractions(listenerDead);
    }
    
    @Test
    @Ignore
    public void testSendToMultipleClients() throws RemoteCommandException {
        IRemoteCommandService service = new JmsRemoteCommandService();
        IRemoteCommandService.IListener listener0 = mock(IRemoteCommandService.IListener.class);
        IRemoteCommandService.IListener listener1 = mock(IRemoteCommandService.IListener.class);
        service.register(ClientGroup.DESY_MKK, listener0);
        service.register(ClientGroup.DESY_MKK, listener1);

        service.sendCommand(ClientGroup.DESY_MKK, "reloadFromLdap");
        verify(listener0).receiveCommand("reloadFromLdap");
        verify(listener1).receiveCommand("reloadFromLdap");
    }
}
