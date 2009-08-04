package org.epics.css.dal.impl;

import java.util.Collections;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * 
 * <code>SynchronizedPropertyFamilyImpl</code> is a PropertyFamily, which
 * stores all the properties within a synchronized collection. This means
 * that all calls that change the structure of this family (add, removed etc.)
 * are synchronized within this class.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SynchronizedPropertyFamilyImpl extends PropertyFamilyImpl 
				implements PropertyFamily {

	/**
	 * Constructs a new PropertyFamily, which uses a synchronized collection
	 * to store the devices.
	 * 
	 * @param pf the owner of this family
	 */
	public SynchronizedPropertyFamilyImpl(PropertyFactory pf) {
		super(pf);
		properties = Collections.synchronizedMap(properties);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollectionMap#remove(org.epics.css.dal.DynamicValueProperty)
	 */
	@Override
	protected synchronized void remove(DynamicValueProperty<?> property) {
		super.remove(property);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.impl.PropertyFamilyImpl#add(org.epics.css.dal.DynamicValueProperty)
	 */
	@Override
	public synchronized void add(DynamicValueProperty<?> property) {
		super.add(property);
	}

}
