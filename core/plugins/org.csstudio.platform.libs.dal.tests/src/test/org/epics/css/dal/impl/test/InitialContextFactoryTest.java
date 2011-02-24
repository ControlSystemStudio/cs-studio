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

import java.io.IOException;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import junit.framework.TestCase;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.directory.DirectoryUtilities;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simulation.DoublePropertyProxyImpl;
import org.epics.css.dal.simulation.PropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;

import com.cosylab.naming.URIName;


public class InitialContextFactoryTest extends TestCase
{
	DirContext initialContext;
	DirContext simContext;

	protected void setUp() throws NamingException, IOException
	{
		if (initialContext != null) {
			return;
		}

		initialContext = DirectoryUtilities.getInitialContext();

		assertNotNull(initialContext);
		assertEquals(initialContext.getClass().getName(),
		    "org.epics.css.dal.directory.InitialDirContextImpl");

		simContext = SimulatorPlug.getInstance().getDefaultDirectory();

		assertNotNull(simContext);

		Object o = initialContext.lookup(new URIName(RemoteInfo.DAL_TYPE_PREFIX
			        + SimulatorPlug.SCHEME_SUFFIX, null, null, null));
		assertEquals(o, simContext);
	}

	public void testSimulatorContextBuilder() throws NamingException
	{
		//test ProertyProxyCharacteristics
		URIName uri = new URIName(RemoteInfo.DAL_TYPE_PREFIX
			    + SimulatorPlug.SCHEME_SUFFIX, SimulatorPlug.DEFAULT_AUTHORITY,
			    new PropertyProxyImpl("",SimulatorPlug.getInstance(),Object.class).getClass().getSimpleName(), null);
		Attributes attr = initialContext.getAttributes(uri);
		assertNotNull(attr);
		assertEquals(attr.get(NumericPropertyCharacteristics.C_DESCRIPTION).get(),
		    "Simulated Property");
		assertEquals(attr.get(NumericPropertyCharacteristics.C_PROPERTY_TYPE)
		    .get(), "property");
		assertEquals(attr.get(NumericPropertyCharacteristics.C_RESOLUTION).get(),
		    0xFFFF);
		assertEquals(attr.get(NumericPropertyCharacteristics.C_SCALE_TYPE).get(),
		    "linear");
		assertEquals(attr.get(NumericPropertyCharacteristics.C_UNITS).get(),
		    "amper");

		//test DoublePropertyProxyCharacteristics
		uri = new URIName(RemoteInfo.DAL_TYPE_PREFIX
			    + SimulatorPlug.SCHEME_SUFFIX, SimulatorPlug.DEFAULT_AUTHORITY,
			    new DoublePropertyProxyImpl("",null).getClass().getSimpleName(), null);
		attr = initialContext.getAttributes(uri);
		assertNotNull(attr);
		assertEquals(attr.get(NumericPropertyCharacteristics.C_FORMAT).get(),
		    "%.4f");
	}

	public void testProxyImplCharacteristicsWithURI()
		throws DataExchangeException, NamingException
	{
		//test default values
		PropertyProxyImpl ppi = new PropertyProxyImpl("test",null,Object.class);
		assertEquals(ppi.getCharacteristic(
		        NumericPropertyCharacteristics.C_DESCRIPTION),
		    "Simulated Property");
		assertEquals(ppi.getCharacteristic(
		        NumericPropertyCharacteristics.C_UNITS), "amper");

		//dummy test characteristic names
		String[] names = ppi.getCharacteristicNames();
		assertNotNull(names);
		assertTrue(names.length > 0);

		//bind proxy and check if it has its own characteristics
		String name = "testCase";
		PropertyProxyImpl ppi2 = new PropertyProxyImpl(name,null,Object.class);
		URIName uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY, name,
			    null);

		Attributes characteristics = new BasicAttributes();
		characteristics.put(NumericPropertyCharacteristics.C_DESCRIPTION,
		    "Simulated Property characteristics");
		characteristics.put(NumericPropertyCharacteristics.C_UNITS, "volt");
		simContext.bind(uri, ppi2, characteristics);
		assertEquals(ppi2.getCharacteristic(
		        NumericPropertyCharacteristics.C_DESCRIPTION),
		    "Simulated Property characteristics");
		assertEquals(ppi2.getCharacteristic(
		        NumericPropertyCharacteristics.C_UNITS), "volt");
	}

	public void testProxyImplCharacteristicsWithString()
		throws DataExchangeException, NamingException
	{
		//bind proxy and check if it has its own characteristics
		String name = "testCase";
		PropertyProxyImpl ppi2 = new PropertyProxyImpl(name,null,Object.class);
		Attributes characteristics = new BasicAttributes();
		characteristics.put(NumericPropertyCharacteristics.C_DESCRIPTION,
		    "Simulated Property 1");
		characteristics.put(NumericPropertyCharacteristics.C_UNITS, "volt1");
		simContext.bind("/" + name, ppi2, characteristics);

		// TODO: fix this
		//assertEquals(ppi2.getCharacteristic(NumericPropertyCharacteristics.C_DESCRIPTION), "Simulated Property 1");
		//assertEquals(ppi2.getCharacteristic(NumericPropertyCharacteristics.C_UNITS), "volt1");
	}

	public void testURINameLookup()
		throws DataExchangeException, NamingException
	{
		// uri string
		String name = "PS1:current";
		String uri = "DAL-Simulator:///" + name;
		URIName uname = new URIName("DAL-Simulator", null, name, null);

		DirContext ctx = SimulatorPlug.getInstance().getDefaultDirectory();

		Object o = initialContext.lookup(uri);
		assertNull(o);
	}
}

/* __oOo__ */
