/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

/**
 *
 */
package org.csstudio.dal.context;

import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.csstudio.dal.EventSystemContext;
import org.csstudio.dal.simple.RemoteInfo;


/**
 * Plug is object, which handles connection management of remote communication layer.
 * Depending of implementation there could be one or several inside single JVM.
 * Plug can be obtained from device or property factory.
 *
 * @author ikriznar
 *
 */
public interface PlugContext extends Identifiable, EventSystemContext<PlugEvent<?>>
{
    /**
     * Returns plug type string, which is distinguishing for plug which
     * creates  proxies for particular communication layer.<p>For
     * example plug that connects to EPICS device my return string "EPICS".</p>
     *
     * @return plug destingushing type name
     */
    public String getPlugType();

    /**
     * Return active configuration of this plug. Configuration should not be changed.
     * @return Returns the configuration.
     */
    public Properties getConfiguration();

    /**
     * Returns a default Directory Context. This is convenience method, directory is obtained from PlugContext.
     *
     * @return default directory from PlugContext
     */
    public DirContext getDefaultDirectory();

    /**
     * Creates new <code>RemoteInfo</code> for provided unique name with additional plug specific information.
     * @param uniqueName unique name
     * @return remote info
     *
     * @throws NamingException if unique name can nto be transformed to remote info
     */
    public RemoteInfo createRemoteInfo(String uniqueName)
        throws NamingException;
}

/* __oOo__ */
