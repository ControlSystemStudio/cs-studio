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

import javax.annotation.Nonnull;

/**
 * The operation of the alarm service depends on its implementation (JMS or DAL) and
 * certain parameters (topics, facilities for ldap, filename).
 *
 * Currently the parameters are defined in the clients (i.e. alarm table and alarm tree). Therefore the parameters
 * must be given to the alarm service methods. It is desirable to store the parameters as preferences internally to
 * the alarm service, because all clients depend on the same set. This will be achieved in the future. The implementation
 * of this interface collects the parameters, so they are already together in one place.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 16.06.2010
 */
public interface IAlarmResource {

    // alarm resource objects are created using factory methods in the alarm service

    /**
     * @return list of topics usable for registering at jms
     */
    @Nonnull
    List<String> getTopics();

    /**
     * @return list of facilities usable for ldap based retrieving of a content model
     */
    @Nonnull
    List<String> getFacilities();

    /**
     * @return path to xml file containing a content model
     */
    @Nonnull
    String getFilepath();

}
