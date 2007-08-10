package de.desy.css.dal.tine;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.spi.AbstractPropertyFactory;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 * 
 */
public class PropertyFactoryImpl extends AbstractPropertyFactory {
	/**
	 * Creates a new PropertyFactoryImpl object.
	 */
	public PropertyFactoryImpl() {
		super();
	}

	@Override
	protected Class<? extends AbstractPlug> getPlugClass() {
		return TINEPlug.class;
	}

	// AW: HOTFIX!
	@Override
	public DynamicValueProperty getProperty(RemoteInfo ri, Class type,
			LinkListener l) throws InstantiationException, RemoteException {
		return super.getProperty(ri);
	}

}
