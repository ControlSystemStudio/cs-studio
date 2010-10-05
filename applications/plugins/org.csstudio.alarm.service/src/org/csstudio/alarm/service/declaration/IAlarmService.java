/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id$
 */
package org.csstudio.alarm.service.declaration;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This service abstracts access to alarm and log messages.
 *
 * Currently two implementations exist with different abilities (DAL and JMS).<br>
 * Both implementations allow for<br>
 * - retrieving the initial state of a pv<br>
 * - monitoring the connection state<br>
 * - listening to alarm and log messages<br>
 *
 * The JMS implementation also allows for<br>
 * - selection of topics, which are actually topics from the JMS server<br>
 * - sending acknowledges<br>
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public interface IAlarmService {

    /**
     * Create a new connection to the underlying alarm system.
     *
     * @return the currently available implementation of the alarm connection
     */
    @Nonnull
    IAlarmConnection newAlarmConnection();

    /**
     * Synchronously retrieve the initial state of the given PVs.
     *
     * On entry, the init items list has to contain the names of all PVs which should be initialized.
     * The implementations of the alarm init item may contain other data as well.
     *
     * When the state could be retrieved, the init method of the init item is called from the service.
     * Handling of connections is done internally in the service.
     */
    void retrieveInitialState(@Nonnull List<IAlarmInitItem> initItems);

    /**
     * Create an alarm resource.
     * You give as much of the parameters as you know in your context, e.g.:
     *
     * If you use the jms implementation you may want to specify the list of jms topics.
     * You can set this to null if you want to connect to the default as defined in the preferences of the alarm service.
     *
     * If you are not using an ldap server, you may want to specify a filepath to an xml configuration file for the set
     * of pvs to watch for. Again, you can set this to null if you want to use the default as defined in the preferences.
     *
     * @param topics
     * @param filepath
     *
     * @return the new alarm resource
     */
    @Nonnull
    IAlarmResource createAlarmResource(@CheckForNull List<String> topics,
                                       @CheckForNull String filepath);
}
