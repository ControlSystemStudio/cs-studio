package org.csstudio.dal.impl;

import java.util.Collections;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.context.PropertyFamily;
import org.csstudio.dal.spi.PropertyFactory;

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
	 * @see org.csstudio.dal.group.PropertyCollectionMap#remove(org.csstudio.dal.DynamicValueProperty)
	 */
	@Override
	protected synchronized void remove(DynamicValueProperty<?> property) {
		super.remove(property);
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.impl.PropertyFamilyImpl#add(org.csstudio.dal.DynamicValueProperty)
	 */
	@Override
	public synchronized void add(DynamicValueProperty<?> property) {
		super.add(property);
	}
	
}
