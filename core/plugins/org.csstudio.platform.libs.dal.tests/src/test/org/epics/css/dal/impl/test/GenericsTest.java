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

package org.epics.css.dal.impl.test;

import junit.framework.TestCase;

import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.SimpleProperty;


public class GenericsTest extends TestCase
{
	class DA<T> implements DataAccess<T>
	{
		public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
		{
			// TODO Auto-generated method stub
		}

		public Class<T> getDataType()
		{
			return null;
		}

		public DynamicValueListener<T, ? extends SimpleProperty<T>>[] getDynamicValueListeners()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public T getLatestReceivedValue()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public T getValue() throws DataExchangeException
		{
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isSettable()
		{
			// TODO Auto-generated method stub
			return false;
		}

		public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T,P> l)
		{
			// TODO Auto-generated method stub
		}

		public void setValue(T value) throws DataExchangeException
		{
			// TODO Auto-generated method stub
		}
		public boolean hasDynamicValueListeners() {
			return false;
		}
	}

	//this test does not do anything - things are alway assignable among the 
	//same classes, regardless of generics
	public void testTypeCheck()
	{
		try {
			DataAccess da1 = new DA();
			DataAccess da2 = new DA<Object>();
			DataAccess da3 = new DA<Double>();
			DataAccess da4 = new DA<String>();
			
			assertTrue(da1.getClass().equals(da2.getClass()));
			assertTrue(da1.getClass().isAssignableFrom(da2.getClass()));
			assertTrue(da1.getClass().isAssignableFrom(da3.getClass()));
			assertTrue(da1.getClass().isAssignableFrom(da4.getClass()));
			assertTrue(da2.getClass().isAssignableFrom(da3.getClass()));
			assertTrue(da2.getClass().isAssignableFrom(da4.getClass()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}

/* __oOo__ */
