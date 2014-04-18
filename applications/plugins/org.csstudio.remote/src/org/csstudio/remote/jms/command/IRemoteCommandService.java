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

import javax.annotation.Nonnull;

/**
 * Service for sending and receiving remote commands.
 * 
 * The commands are simply strings which are sent to certain client groups for which the clients may register.
 * 
 * @author jpenning
 * @since 16.01.2012
 */
public interface IRemoteCommandService {
    
    // Design decision: Currently there is no need to formalize the command itself, 
    // because the client groups are sufficient for the identification of the clients,
    // so strings will do for the commands.
    
    // Command used after an update to the ldap-based alarm configuration.
    // Typical listeners are css clients as well as the headless dal2jms application.
    public static String ReloadFromLdapCommand = "ReloadFromLdap";
    public static String Dal2JmsReloadedCommand = "Dal2JmsReloaded";
    public static String Dal2JmsStartedCommand = "Dal2JmsStarted";
    public static String Dal2JmsWillStopCommand = "Dal2JmsWillStop";
    
    interface IListener {
        // this is called, when the listener receives a command for the proper group.
        // the given command is the same as supplied by the sender.
        // the client then should carry out the appropriate action.
        void receiveCommand(@Nonnull final String command);
    }
    
    // register the given listener for the given client group.
    // if it is registered more than once, it will be called more than once.
    void register(@Nonnull final ClientGroup group, @Nonnull final IListener listener) throws RemoteCommandException;
    
    // deregister the given listener from the given client group.
    // if the listener has not been registered, nothing happens.
    void deregister(@Nonnull final IListener listener);
    
    // all listeners registered in the given client group will receive a call
    // the listeners are told the given command string
    void sendCommand(@Nonnull final ClientGroup group, @Nonnull final String command) throws RemoteCommandException;
    
}
