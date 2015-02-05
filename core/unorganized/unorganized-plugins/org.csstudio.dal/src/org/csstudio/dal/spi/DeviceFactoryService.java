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

package org.csstudio.dal.spi;

import org.csstudio.dal.context.AbstractApplicationContext;


/**
 * A service, which produces instances of <code>DeviceFactory</code> of
 * different flavors. This service is rather direct class than interface
 * implementation since there was need for only one implementation ath the time
 * of cretaion of this type of service.
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 */
public interface DeviceFactoryService
{
	/**
	 * Constant for defining system property.
	 */
	public static final String DEFAULT_FACTORY_IMPL = "DeviceFactoryService.default_factory_impl";

	/**
	 * Constant for defining System property.
	 */
	public static final String DEFAULT_APPLICATION_CONTEXT = "DeviceFactoryService.default_application_context";

	/**
	 * Returns device factory implementation.
	 * Service may decide to reuse factory if factory with same
	 * parameters has already been created.
	 *
	 * @param ctx
	 *          ApplicationContext, which will provide configuration basis,
	 *          non-null
	 * @param linkPolicy
	 *          a link policy code, valid values are <code>NO_LINK_POLICY</code>,
	 *          <code>SYNC_LINK_POLICY</code> and <code>ASYNC_LINK_POLICY</code>.
	 * @return instance of <code>DeviceFactory</code>
	 */
	public DeviceFactory getDeviceFactory(AbstractApplicationContext ctx,
	    LinkPolicy linkPolicy);

	/**
	 * Returns device factory implementation for particular plug name.
	 * Service may decide to reuse factory if factory with same
	 * parameters has already been created.
	 *
	 * @param ctx
	 *          ApplicationContext, which will provide configuration basis,
	 *          non-null
	 * @param linkPolicy
	 *          a link policy code, valid values are <code>NO_LINK_POLICY</code>,
	 *          <code>SYNC_LINK_POLICY</code> and <code>ASYNC_LINK_POLICY</code>.
	 * @param plugName name of plug used by factory, if <code>null</code> then default is used
	 *
	 * @return instance of <code>DeviceFactory</code>
	 */
	public DeviceFactory getDeviceFactory(AbstractApplicationContext ctx,
	    LinkPolicy linkPolicy, String plugName);

	/**
	 * Returns default device factory implementation. Implementation class is
	 * obtained from the default application context. Default application
	 * context is an implementation of the interface <code>AbstractApplicationContext</code>
	 * defined in the system properties using
	 * <code>DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT</code> identifier.
	 * If no context is defined method should use a known implementation
	 * of the <code>AbstractApplicationContext</code>.
	 * Service my decide to reuse factory if factory with same
	 * parameters has already been created.
	 *
	 * @return instance of <code>DeviceFactory</code>
	 */
	public DeviceFactory getDefaultDeviceFactory();
}

/* __oOo__ */
