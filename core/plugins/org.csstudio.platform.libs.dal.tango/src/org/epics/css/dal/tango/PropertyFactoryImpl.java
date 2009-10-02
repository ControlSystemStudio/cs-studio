package org.epics.css.dal.tango;

import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.spi.AbstractPropertyFactory;

/**
 * 
 * <code>PropertyFactoryImpl</code> is the property factory for Tango 
 * properties.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PropertyFactoryImpl extends AbstractPropertyFactory {
	
	/**
	 * Creates a new PropertyFactoryImpl object.
	 */
	public PropertyFactoryImpl() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.spi.AbstractFactorySupport#getPlugClass()
	 */
	@Override
	protected Class<? extends AbstractPlug> getPlugClass() {
		return TangoPropertyPlug.class;
	}
}
