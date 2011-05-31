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

package org.epics.css.dal.epics.desy.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * Playground for DAL characteristic tests. This the following bugs:
 * 
 * 1. Properties created via a PropertyFactory does not equal the ones that are delivered with a ConnectionEvent, when the connection is established
 * 2. Characteristics are initialized too late
 * 3. PropertyChangeListeners for characteristics does not fire events (maybe this correlates to 2.)
 * 
 * Other things I found out:
 * 
 * 1. Property cannot be destroyed: PropertyFamilyImpl.destroy(DynamicValueProperty prop) is not implemented and marked as TODO
 * ... maybe more later
 * 
 * Look for the TODO-Tags. There the bugs will be explained more detailed.
 * 
 * @author Sven Wende
 */
public class DalCharacteristicsTest implements DynamicValueListener,
		LinkListener {
	private PropertyFactory _factory = null;

	private DynamicValueProperty _property = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new DalCharacteristicsTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DalCharacteristicsTest() throws Exception {
		// set system properties
		System.setProperty("dal.plugs", "EPICS");
		System.setProperty("dal.plugs.default", "EPICS");
		System.setProperty("dal.propertyfactory.EPICS",
				"org.epics.css.dal.epics.PropertyFactoryImpl");

		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
				"YES");
		System.setProperty(
				"com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
				"15.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
				"5065");
		System.setProperty("com.cosylab.epics.caj.CAJContext.server_port",
				"5064");
		System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
				"16384");

		// get the property _factory
		_factory = DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(new DefaultApplicationContext("Test"),
						LinkPolicy.NO_LINK_POLICY, "EPICS");

		// create a property
		RemoteInfo channel = new RemoteInfo("EPICS", "Chiller:Pressure:1", null, null);

		// TODO: The API needs to be changed here. Igor: We already talked about this.
		_property = (DynamicValueProperty) _factory.getProperty(channel);

		// link that property to the control system
		_factory.asyncLinkProperty(channel, null, this);

		Thread.sleep(5000);
		_factory.getPropertyFamily().destroy(_property);

	}

	public void conditionChange(DynamicValueEvent event) {

	}

	public void errorResponse(DynamicValueEvent event) {

	}

	public void timelagStarts(DynamicValueEvent event) {

	}

	public void timelagStops(DynamicValueEvent event) {
	}

	public void timeoutStarts(DynamicValueEvent event) {

	}

	public void timeoutStops(DynamicValueEvent event) {

	}

	public void valueChanged(DynamicValueEvent event) {

	}

	public void valueUpdated(DynamicValueEvent event) {

	}

	public void operational(ConnectionEvent e) {
		// the property is connected now
		DynamicValueProperty property = (DynamicValueProperty) e
				.getConnectable();

		// TODO: Bug: The property that was connected does not equal the one
		// that was created initially. This probably breaks caching.
		System.out.println("Bug: Properties are not the same: "
				+ (property.equals(_property)) + ":" + (property == _property));

		// try to access the characteristics
		try {
			// Get all available properties and print them on the console

			// TODO: Bug: Not all characteristics are available (e.g. none from
			// NumericPropertyCharacteristics.class). Only default values are
			// delivered, but not real ones.
			String[] characteristics = property.getCharacteristicNames();

			for (String key : characteristics) {
				System.out.println(key + " : "
						+ property.getCharacteristic(key));
			}

			// Get certain characteristics asynchroniously and print them on the
			// console
			property.addResponseListener(new ResponseListener() {
				public void responseError(ResponseEvent event) {
					System.out
							.println("ERROR during async GET of characteristics");
				}

				public void responseReceived(ResponseEvent event) {
					// TODO: BUG: event.getResponse().getValue() usually
					// delivers null here. Digging into DAL code, we found out,
					// that ProxyPropertyImpl.getCompleted(GetEvent ev) is
					// called to late. Because of that, the characteristics are
					// initialized too late. When they are finally initialized,
					// we get no further events.
					System.out.println("-->" + event.getResponse().getIdTag()
							+ ":" + event.getResponse().getValue());
				}

			});

			System.out
					.println(property
							.getCharacteristicAsynchronously(NumericPropertyCharacteristics.C_MAXIMUM));
			System.out
					.println(property
							.getCharacteristicAsynchronously(NumericPropertyCharacteristics.C_GRAPH_MAX));

			// Add a property change listener to get information about changes
			// in characteristics
			// TODO: Bug: doesn�t seem to work - we get no events
			property.addPropertyChangeListener(new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println("Property Change Event: "
							+ evt.getPropertyName() + ": " + evt.getNewValue());
				}

			});
			

		} catch (DataExchangeException e1) {
			e1.printStackTrace();
		}
	}

	public void connected(ConnectionEvent e) {
	}
	
	public void connectionFailed(ConnectionEvent e) {
	}

	public void connectionLost(ConnectionEvent e) {
	}

	public void destroyed(ConnectionEvent e) {
	}

	public void disconnected(ConnectionEvent e) {
	}

	public void resumed(ConnectionEvent e) {

	}

	public void suspended(ConnectionEvent e) {
	}
}
