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
package org.csstudio.platform.simpledal;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.platform.internal.simpledal.ConnectorFactory;
import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * Factory of
 * {@link IProcessVariableConnectionService ProcessVariableConnectionService}s,
 *
 * @author C1 WPS / KM, MZ, Xihui Chen
 */
public class ProcessVariableConnectionServiceFactory {

    private static ProcessVariableConnectionServiceFactory sharedInstance;

    private IProcessVariableConnectionService _mainProcessVariableConnectionService;

    private SimpleDALBroker broker;

    /**
     * Returns the singleton instance of this factory.
     *
     * @return the singleton instance of this factory
     */
    public static synchronized ProcessVariableConnectionServiceFactory getDefault() {

        if (ProcessVariableConnectionServiceFactory.sharedInstance == null) {
            ProcessVariableConnectionServiceFactory.sharedInstance = new ProcessVariableConnectionServiceFactory();
        }
        return ProcessVariableConnectionServiceFactory.sharedInstance;
    }

    /**
     * Returns the main {@link IProcessVariableConnectionService} for the
     * control system studio.
     *
     * In general, this service should be used by all CSS applications to save
     * resources and ensure maximum channel reuse.
     *
     * @return the main {@link IProcessVariableConnectionService} for the
     *         control system studio
     */
    public IProcessVariableConnectionService getProcessVariableConnectionService() {
        return getMainProcessVariableConnectionService();
    }

    /**
     * Returns a named {@link IProcessVariableConnectionService}. If no service
     * exists for the specified name, a new service is created.
     *
     * @return a {@link IProcessVariableConnectionService}
     */
    public IProcessVariableConnectionService createProcessVariableConnectionService() {
        // TODO: Implement storage for named services and enhance the Connector
        // Overview View to display channels for different connection services
        return getMainProcessVariableConnectionService();
    }


    /**
     * Returns the {@link SimpleDALBroker} which is a narrow interface to the DAL layer.
     * @return the {@link SimpleDALBroker}
     */
    // FIXME: 15.03.2010: swende: Eventuell n Broker-Instanzen betreiben - pro Display eine!
    public SimpleDALBroker getSimpleDALBroker() {
        if (broker == null) {
            broker = SimpleDALBroker.newInstance(new CssApplicationContext("CSS"));
        }
        return broker;
    }

    private synchronized IProcessVariableConnectionService getMainProcessVariableConnectionService() {
            if (_mainProcessVariableConnectionService == null) {
                _mainProcessVariableConnectionService = new ProcessVariableConnectionService(new ConnectorFactory());
            }
        return _mainProcessVariableConnectionService;
    }


}
