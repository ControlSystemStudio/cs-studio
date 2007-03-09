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

package org.epics.css.dal.spi;

import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LifecycleEvent;
import org.epics.css.dal.context.LifecycleListener;
import org.epics.css.dal.context.PlugContext;
import org.epics.css.dal.proxy.AbstractPlug;

import java.util.Properties;

import javax.naming.directory.DirContext;


/**
 * This is convenience implementation of common factory code.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class AbstractFactorySupport
{
	protected AbstractApplicationContext ctx;
	protected LinkPolicy linkPolicy;
	private AbstractPlug plug;
	protected LifecycleListener lifecycleListener = new LifecycleListenerImpl();

	private class LifecycleListenerImpl implements LifecycleListener
	{
		public void destroying(LifecycleEvent event)
		{
			destroyAll();
			
			try
			{
				if (plug != null)
					plug.releaseInstance();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public void destroyed(LifecycleEvent event)
		{
		}

		public void initialized(LifecycleEvent event)
		{
		}

		public void initializing(LifecycleEvent event)
		{
		}
	}

	/**
	 * Default contructor.
	 *
	 */
	protected AbstractFactorySupport()
	{
	}

	/**
	 * Must destroy all created objects
	 *
	 */
	protected abstract void destroyAll();

	/**
	 * Must return plug implemntation class, which extends <code>AbstractPlug</code>.
	 * @return plug implemntation class
	 */
	protected abstract Class<?extends AbstractPlug> getPlugClass();

	/**
	 * Returns instance of plug, which must be used by this factory. Plug is created if necessary.
	 * @return instance of plug dedicated to this factory.
	 */
	protected AbstractPlug getPlugInstance()
	{
		if (plug == null) {
			try {
				plug = (AbstractPlug)getPlugClass()
					.getMethod("getInstance", new Class[]{ Properties.class })
					.invoke(null, new Object[]{ ctx.getConfiguration() });
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Plug '" + getPlugClass()
				    + "' is not correctly implemented. ", e);
			}
		}

		return plug;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.spi.DeviceFactory#getLinkPolicy()
	 */
	public LinkPolicy getLinkPolicy()
	{
		return linkPolicy;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.spi.DeviceFactory#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.spi.DeviceFactory#initialize(org.epics.css.dal.context.AbstractApplicationContext, org.epics.css.dal.spi.LinkPolicy)
	 */
	public void initialize(AbstractApplicationContext ctx, LinkPolicy policy)
	{
		this.ctx = ctx;
		this.linkPolicy = policy;
		ctx.addLifecycleListener(lifecycleListener);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.spi.DeviceFactory#getPlugType()
	 */
	public String getPlugType()
	{
		return getPlugInstance().getPlugType();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.spi.DeviceFactory#getDefaultDirectory()
	 */
	public DirContext getDefaultDirectory()
	{
		return getPlugInstance().getDefaultDirectory();
	}

	/**
	 * Return plug which is used for connection. If this factory serves as facade for multiple plugs,
	 * than default plug must be returned.
	 *
	 * @return plug which is used for connection
	 */
	public PlugContext getPlug()
	{
		return getPlugInstance();
	}
}

/* __oOo__ */
