/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.epics.css.dal.epics;

import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.spi.AbstractPropertyFactory;

/**
 * EPICS PropertyFactory implementation
 *
 * @author $Author$
 * @version $Revision$
  */
public class PropertyFactoryImpl extends AbstractPropertyFactory
{
	/**
	 * @see org.epics.css.dal.spi.AbstractFactorySupport#getPlugClass()
	 */
	@Override
	protected Class<? extends AbstractPlug> getPlugClass() {
		return EPICSPlug.class;
	}

	/**
	 * Creates a new PropertyFactoryImpl object.
	 */
	public PropertyFactoryImpl() {
		super();
	}
	
}
