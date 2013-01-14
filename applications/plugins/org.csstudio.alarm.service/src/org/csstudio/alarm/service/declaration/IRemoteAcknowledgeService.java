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
package org.csstudio.alarm.service.declaration;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Remote service for persisting and managing the acknowledge state of all pvs
 * The service is found via rmi registry using its full pathname
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public interface IRemoteAcknowledgeService extends Remote {
    
    /**
     * Tell the service that the given pv has been acknowledged.
     * This will set the acknowledge state.
     * 
     * @param pvName
     * @throws RemoteException
     */
    void announceAcknowledge(@Nonnull final String pvName) throws RemoteException;
    
    /**
     * Tell the service that the given pv has had another alarm update.
     * This will clear the acknowledge state.
     * 
     * @param pvName
     * @throws RemoteException
     */
    void announceAlarm(@Nonnull final String pvName) throws RemoteException;
    
    /**
     * @param pvName
     * @return the time stamp of the acknowledge announcement
     * @throws RemoteException
     */
    @CheckForNull
    String getAcknowledgeTime(@Nonnull final String pvName) throws RemoteException;
    
    /**
     * @return collection of pvs which have been acknowledged
     * @throws RemoteException
     */
    @Nonnull
    Collection<String> getAcknowledgedPvs() throws RemoteException;
    
}
