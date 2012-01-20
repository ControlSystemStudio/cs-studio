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

import java.util.ArrayList;

import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.LifecycleState;


/**
 * Default PropertyFactoryService implementation
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DefaultPropertyFactoryService implements PropertyFactoryService
{
	private static DefaultPropertyFactoryService service;

	/**
	 * Returns the PropertyFactoryService object
	 *
	 * @return PropertyFactoryService object
	 */
	public static final synchronized PropertyFactoryService getPropertyFactoryService()
	{
		if (service == null) {
			service = new DefaultPropertyFactoryService();
		}

		return service;
	}

	private ArrayList<AbstractApplicationContext> ctxList = new ArrayList<AbstractApplicationContext>();

	protected DefaultPropertyFactoryService()
	{
		super();
		Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run()
				{
					shutdown();
				}
				;
			});
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.spi.PropertyFactoryService#getPropertyFactory(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.context.PropertyFamily, org.csstudio.dal.spi.PropertyFactory.LinkPolicy)
	 */
	public PropertyFactory getPropertyFactory(AbstractApplicationContext ctx,
	    LinkPolicy linkPolicy)
	{
		return getPropertyFactory(ctx, linkPolicy, null);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.spi.PropertyFactoryService#getPropertyFactory(org.csstudio.dal.context.AbstractApplicationContext, org.csstudio.dal.spi.LinkPolicy, java.lang.String)
	 */
	public PropertyFactory getPropertyFactory(AbstractApplicationContext ctx,
	    LinkPolicy linkPolicy, String plugName)
	{
		if (linkPolicy == null)
			linkPolicy = LinkPolicy.SYNC_LINK_POLICY;
		
		if (plugName == null) {
			Class cl = null;

			try {
				cl = Plugs.getDefaultPropertyFactory(ctx.getConfiguration());
			} catch (Throwable t) {
				throw new IllegalArgumentException(
				    "Could not load factory implementation: " + t);
			}

			if (cl != null) {
				try {
					PropertyFactory df = (PropertyFactory)cl.newInstance();
					df.initialize(ctx, linkPolicy);

					return df;
				} catch (Throwable t) {
					throw new IllegalArgumentException(
					    "Could not instantiate '" + cl.getName()
					    + "' factory implementation: " + t);
				}
			} else {
				throw new IllegalArgumentException(
			    "Could not find factory implementation information in configuration.");
			}

			//PropertyFactoryImpl simulator = new PropertyFactoryImpl();
			//simulator.initialize(ctx, linkPolicy);
			//return simulator;
		}

		Class cl;

		try {
			cl = Plugs.getPropertyFactoryClassForPlug(plugName,
				    ctx.getConfiguration());
		} catch (Throwable t) {
			throw new IllegalArgumentException(
			    "Failed to load factory implementation: " + t);
		}

		try {
			PropertyFactory df = (PropertyFactory)cl.newInstance();
			df.initialize(ctx, linkPolicy);

			return df;
		} catch (Throwable t) {
			throw new IllegalArgumentException("Could not instantiate '"
			    + cl.getName() + "' factory implementation: " + t);
		}
	}

	/*
	 * This method is called when JVM is shuting down and service is celaning all it's
	 * resources.
	 */
	private void shutdown()
	{
		AbstractApplicationContext[] l = (AbstractApplicationContext[])ctxList
			.toArray(new AbstractApplicationContext[0]);

		for (int i = 0; i < l.length; i++) {
			if (l[i].getLifecycleState() != LifecycleState.DESTROYING
			    && l[i].getLifecycleState() != LifecycleState.DESTROYED) {
				l[i].destroy();
			}
		}
	}
}

/* __oOo__ */
