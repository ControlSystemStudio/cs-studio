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

package org.csstudio.dal.impl;

import org.apache.log4j.Logger;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.PatternProperty;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.simple.impl.DataUtil;


/**
 * Convenience utilities for plug implementators.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class PropertyUtilities
{
	private PropertyUtilities()
	{
		super();
	}

	/**
	 * Returns class, which implements requested
	 *
	 * @param interfaceClass    the interface who's implementation is requested
	 *
	 * @return class implementing requested interface
	 *
	 * @throws NullPointerException is thrown if interfaceClass is <code>null</code>
	 * @throws IllegalArgumentException is thrown if interfaceClass is not an interface
	 */
	public static Class<? extends SimpleProperty<?>> getImplementationClass(
	    Class<?extends SimpleProperty<?>> interfaceClass)
	{
		if (interfaceClass == null) {
			throw new NullPointerException("interfaceClass");
		}

		if (!interfaceClass.isInterface()) {
			throw new IllegalArgumentException("Class '"
			    + interfaceClass.getName() + "' does not represent interface.");
		}

		// TODO: lines below should be unnecesary
		if (interfaceClass == PatternProperty.class) {
			return PatternPropertyImpl.class;
		}

		// default implementation
		if (interfaceClass == DynamicValueProperty.class
		    || interfaceClass == SimpleProperty.class) {
			return DoublePropertyImpl.class;
		}

		// first we make wild guess from class name only
		if (DynamicValueProperty.class.isAssignableFrom(interfaceClass)) {
			String iname = interfaceClass.getName();
			int i = iname.lastIndexOf('.');
			String cname = iname.substring(0, i) + ".impl" + iname.substring(i)
				+ "Impl";

			try {
				Class c =  Class.forName(cname);

				return c;
			} catch (Throwable e) {
				Logger.getLogger(PropertyUtilities.class).debug("Heuristic lookup failed.", e);
			}
		}

		String iname = interfaceClass.getName();
		int i = iname.lastIndexOf('.');
		int j = iname.lastIndexOf("SimpleProperty");

		if (j > 0) {
			String cname = iname.substring(0, i) + ".impl"
				+ iname.substring(i, j) + "PropertyImpl";

			try {
				Class c = Class.forName(cname);

				return c;
			} catch (Throwable e) {
				Logger.getLogger(PropertyUtilities.class).debug("Heuristic lookup failed.", e);
			}
		}

		if (DoubleProperty.class.isAssignableFrom(interfaceClass)) {
			return DoublePropertyImpl.class;
		}

		throw new IllegalArgumentException("Class '" + interfaceClass.getName()
		    + "' does not have declared implementation class.");
	}
	
	/**
	 * As conveinece method, meant to fix common characteristic aliases. 
	 * @param proxy
	 * @param characteristicName
	 * @param value
	 * @return
	 * @throws DataExchangeException 
	 */
	public static final Object verifyCharacteristic(DirectoryProxy proxy, String characteristicName, Object value) throws DataExchangeException {
		
		if (value==null && characteristicName!=null) {
			if (characteristicName.equals(CharacteristicInfo.C_DISPLAY_NAME.getName())) {
				return proxy.getUniqueName();
			}
			if (characteristicName.equals(CharacteristicInfo.C_ALARM_MAX.getName())) {
				return proxy.getCharacteristic(CharacteristicInfo.C_MAXIMUM.getName());
			}
			if (characteristicName.equals(CharacteristicInfo.C_ALARM_MIN.getName())) {
				return proxy.getCharacteristic(CharacteristicInfo.C_MINIMUM.getName());
			}
			if (characteristicName.equals(CharacteristicInfo.C_WARNING_MAX.getName())) {
				return proxy.getCharacteristic(CharacteristicInfo.C_MAXIMUM.getName());
			}
			if (characteristicName.equals(CharacteristicInfo.C_WARNING_MIN.getName())) {
				return proxy.getCharacteristic(CharacteristicInfo.C_MINIMUM.getName());
			}
			if (characteristicName.equals(CharacteristicInfo.C_META_DATA.getName())) {
				return DataUtil.createMetaData(proxy);
			}
		}
		
		return value;
		
	}
}

/* __oOo__ */
