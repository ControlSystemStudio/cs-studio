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

package org.epics.css.dal.impl;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.EnumPropertyCharacteristics;
import org.epics.css.dal.SequencePropertyCharacteristics;
import org.epics.css.dal.context.PropertyContext;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;


/**
 * Default implementation of EnumProperty
 *
 * @author $Author$
 * @version $Revision$
  */
public class EnumPropertyImpl extends NumericPropertyImpl<Long>
	implements EnumProperty
{
	private Object[] enumValues;
	private String[] enumDescriptions;
	private boolean chInitialized = false;
	
	public EnumPropertyImpl(String name, PropertyContext propertyContext)
	{
		super(Long.class, name, propertyContext);
	}

	private void readCharacteristics() throws DataExchangeException
	{
		if (chInitialized)
			return;
	
		enumDescriptions = (String[])getCharacteristic(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS);
		enumValues = (Object[])getCharacteristic(EnumPropertyCharacteristics.C_ENUM_VALUE);
		if (enumDescriptions == null || enumValues == null) {
			throw new IllegalArgumentException(
			    "Values and descriptions may not be null");
		}
		chInitialized = true;

	}
	

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.EnumSimpleProperty#getEnumValues()
	 */
	public Object[] getEnumValues() throws DataExchangeException
	{
		if (!chInitialized)
			readCharacteristics();
		return enumValues;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.EnumSimpleProperty#getEnumDescriptions()
	 */
	public String[] getEnumDescriptions() throws DataExchangeException
	{
		if (!chInitialized)
			readCharacteristics();
		return enumDescriptions;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.EnumSimpleProperty#indexOf(java.lang.Object)
	 */
	public long indexOf(Object enumerated)
	{
		for (int i = 0; i < enumValues.length; i++) {
			if (enumValues[i].equals(enumerated)) {
				return i;
			}
		}

		return -1;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.EnumSimpleProperty#valueOf(long)
	 */
	public Object valueOf(long index)
	{
		if (index >= 0 && index < enumValues.length) {
			return enumValues[(int)index];
		}

		return null;
	}
}

/* __oOo__ */
