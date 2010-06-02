package org.epics.css.dal.tango.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.Response;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.epics.css.dal.tango.TangoApplicationContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * <code>PropertyTest</code> is a JUnit test for tango property plug properties.
 * It tests get/set actions (synchronous and asynchronous) as well as tests
 * monitoring of values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PropertyTest {

	private class DoubleResponseListener extends DynamicValueAdapter<Double, DoubleProperty> implements ResponseListener<Double> {
		Response<Double> lastResponse;
		List<DynamicValueEvent<Double,DoubleProperty>> changeEvents = Collections.synchronizedList(new ArrayList<DynamicValueEvent<Double,DoubleProperty>>());
		List<DynamicValueEvent<Double,DoubleProperty>> updateEvents = Collections.synchronizedList(new ArrayList<DynamicValueEvent<Double,DoubleProperty>>());
		List<DynamicValueEvent<Double,DoubleProperty>> errorEvents = Collections.synchronizedList(new ArrayList<DynamicValueEvent<Double,DoubleProperty>>());
		int responseCount = 0;
		int changeCount = 0;
		int updateCount = 0;
		int errorCount = 0;
		boolean onlyChangesNotify = false;
		boolean notify = true;
		boolean deny = true;
		boolean denyUpdates = false;
		public void responseError(final ResponseEvent<Double> event) {
			if (denyUpdates && deny) {
                return;
            }
			this.lastResponse = event.getResponse();
			errorCount++;
			if (notify) {
				denyUpdates = true;
    			synchronized(this) {
    				notifyAll();
    			}
			}
		}
		public void responseReceived(final ResponseEvent<Double> event) {
			if (denyUpdates && deny) {
                return;
            }
			this.lastResponse = event.getResponse();
			responseCount++;
			if (notify) {
				denyUpdates = true;
    			synchronized(this) {
    				notifyAll();
    			}
			}
		}

		@Override
		public void valueChanged(final DynamicValueEvent<Double,DoubleProperty> event) {
			if (denyUpdates && deny) {
                return;
            }
			changeCount++;
			changeEvents.add(event);
			if (notify) {
				denyUpdates = true;
    			synchronized(this) {
    				notifyAll();
    			}
			}
		}

		@Override
		public void valueUpdated(final DynamicValueEvent<Double,DoubleProperty> event) {
			if (denyUpdates && deny) {
                return;
            }
			updateCount++;
   			updateEvents.add(event);
   			if (notify && !onlyChangesNotify) {
   				denyUpdates = true;
    			synchronized(this) {
    				notifyAll();
    			}
			}
   		}

		@Override
		public void errorResponse(final DynamicValueEvent<Double,DoubleProperty> event) {
			if (denyUpdates && deny) {
                return;
            }
			errorCount++;
			errorEvents.add(event);
			if (notify && !onlyChangesNotify) {
				denyUpdates = true;
    			synchronized(this) {
    				notifyAll();
    			}
			}
		}

		public void reset(final boolean onlyChangesNotify, final boolean notify, final boolean deny) {
			errorCount = 0;
			updateCount = 0;
			responseCount = 0;
			changeCount = 0;
			changeEvents.clear();
			updateEvents.clear();
			errorEvents.clear();
			lastResponse = null;
			denyUpdates = false;
			this.deny = deny;
			this.onlyChangesNotify = onlyChangesNotify;
			this.notify = notify;
		}

		@SuppressWarnings("unchecked")
		public DynamicValueEvent<Double,DoubleProperty>[] getChangeEvents() {
			return changeEvents.toArray(new DynamicValueEvent[changeEvents.size()]);
		}

		@SuppressWarnings("unchecked")
		public DynamicValueEvent<Double,DoubleProperty>[] getUpdateEvents() {
			return updateEvents.toArray(new DynamicValueEvent[updateEvents.size()]);
		}

		@SuppressWarnings("unchecked")
		public DynamicValueEvent<Double,DoubleProperty>[] getErrorEvents() {
			return errorEvents.toArray(new DynamicValueEvent[errorEvents.size()]);
		}
	}

	private static final String DEVICE = "tango/tangotest/1";
	private static final String DOUBLE_PROPERTY = "ampli";

	private final PropertyFactory propertyFactory;
	private DoubleProperty doubleProperty;

	private DoubleResponseListener doubleResponseListener;

	public PropertyTest() {
		super();
		System.setProperty("TANGO_HOST","localhost:20000");
		final TangoApplicationContext ctx = new TangoApplicationContext("TangoPropertyTest");
		propertyFactory = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);
	}

	@Before
	public void beforeTest() throws RemoteException, InstantiationException {
		doubleProperty = propertyFactory.getProperty(DEVICE + "/" + DOUBLE_PROPERTY,DoubleProperty.class,null);
		doubleResponseListener = new DoubleResponseListener();
		doubleProperty.setValue(0.);
	}

	@After
	public void afterTest() {
		doubleProperty.removeDynamicValueListener(doubleResponseListener);
		propertyFactory.getPropertyFamily().destroy(doubleProperty);
	}

	@Test
	public void testDoubleSyncGet() throws DataExchangeException {
		final Double value = doubleProperty.getValue();
		Assert.assertNotNull("Value not null:", value);
		Assert.assertNotSame("Value not NaN:", Double.NaN,value.doubleValue());
	}

	@Test
	public void testDoubleSyncSet() throws DataExchangeException, InterruptedException {
		final double value = 3.2;

		doubleProperty.setValue(value);
		//this was a synchronous call, but for some reason tango doesn't update it immediately
		//it is half synchronous
		Thread.sleep(3000);

		final double valueRet = doubleProperty.getValue();

		Assert.assertEquals("Value read is value set:", value, valueRet);
	}

	@Test
	public void testDoubleAsyncGet() throws DataExchangeException, InterruptedException {

		final Request<Double> request = doubleProperty.getAsynchronous(doubleResponseListener);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(5000);
		}
		final Response<Double> response = doubleResponseListener.lastResponse;
		Assert.assertNotNull("Response not null:",response);
		Assert.assertTrue("Response successful:",response.success());
		Assert.assertTrue("Response is last:", response.isLast());
		Assert.assertNotNull("Value not null:", response.getValue());
		Assert.assertNotSame("Value not NaN:", Double.NaN,response.getValue().doubleValue());
		Assert.assertEquals("Response same as request response:", request.getLastResponse(),response);
	}

	@Test
	public void testDoubleAsyncSet() throws DataExchangeException, InterruptedException {

		final Double value = 3.2;
		doubleResponseListener.reset(true,true,true);
		final Request<Double> request = doubleProperty.setAsynchronous(value,doubleResponseListener);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(5000);
		}
		//wait for tango to register
		Thread.sleep(3000);
		final Double newValue = doubleProperty.getValue();
		Assert.assertEquals("Value get is value set:", value,newValue);

		final Response<Double> response = doubleResponseListener.lastResponse;
		Assert.assertNotNull("Response not null:",response);
		Assert.assertTrue("Response successful:",response.success());
		Assert.assertTrue("Response is last:", response.isLast());
		Assert.assertNotNull("Value not null:", response.getValue());
		Assert.assertNotSame("Value get is value set:", value,response.getValue().doubleValue());
		Assert.assertEquals("Response same as request response:", request.getLastResponse(),response);
	}

	@Test
	public void testDoubleChangeMonitor() throws DataExchangeException, UnsupportedOperationException, InterruptedException {
		doubleProperty.getDefaultMonitor().setHeartbeat(false);
		doubleProperty.addDynamicValueListener(doubleResponseListener);
		doubleResponseListener.reset(true,true,false);
		Thread.sleep(3000);
		final Double value3 = 3.;
		doubleProperty.setValue(value3);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(2000);
		}
		final DynamicValueEvent<Double,DoubleProperty>[] events = doubleResponseListener.getChangeEvents();
		Assert.assertTrue("Number of events must be at least 1.", events.length >= 1);
		Assert.assertEquals("First change:", value3, events[events.length-1].getValue());
	}

	@Test
	public void testDoublePeriodicMonitor() throws DataExchangeException, UnsupportedOperationException, InterruptedException {
		doubleProperty.getDefaultMonitor().setHeartbeat(true);
		doubleProperty.getDefaultMonitor().setTimerTrigger(1000);
		doubleResponseListener.reset(false,false,true);
		doubleProperty.addDynamicValueListener(doubleResponseListener);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(5000);
		}
		Assert.assertTrue("Number of updates has to be at least 4. It was " + doubleResponseListener.updateCount +".", doubleResponseListener.updateCount >= 4);
		Assert.assertTrue("Number of changes can be at most 2. It was " + doubleResponseListener.changeCount +".", doubleResponseListener.changeCount <= 2);
		DynamicValueEvent<Double,DoubleProperty>[] events = doubleResponseListener.getErrorEvents();
		Assert.assertEquals("Number of errors:",0,events.length);

		doubleProperty.getDefaultMonitor().setTimerTrigger(3000);
		doubleResponseListener.reset(false,false,true);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(5000);
		}
		Assert.assertTrue("Number of updates has to be 2 or 1. It was " + doubleResponseListener.updateCount +".", (doubleResponseListener.updateCount == 2) || (doubleResponseListener.updateCount == 1));
		Assert.assertEquals("Number of changes has to be 0. It was " + doubleResponseListener.changeCount +".", 0, doubleResponseListener.changeCount);
		events = doubleResponseListener.getErrorEvents();
		Assert.assertEquals("Number of errors:",0,events.length);

		doubleProperty.getDefaultMonitor().setTimerTrigger(1000);
		//wait for tango to register
		Thread.sleep(3000);
		doubleResponseListener.reset(true,true,false);
		final Double value1 = 1.;
		doubleProperty.setValue(value1);
		//monitor is periodic and not on change. wait for change to happen
		Thread.sleep(2000);
		final Double value2 = 2.;
		doubleProperty.setValue(value2);
		synchronized (doubleResponseListener) {
			doubleResponseListener.wait(3000);
		}

		Assert.assertEquals("Number of changes has to be 2. It was " + doubleResponseListener.changeCount +".", 2, doubleResponseListener.changeCount);
		events = doubleResponseListener.getChangeEvents();
		Assert.assertEquals("Number of events", 2, events.length);
		Assert.assertEquals("First change", value1, events[0].getValue());
		Assert.assertEquals("Second change", value2, events[1].getValue());
	}
}
