/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.ams.filter;

import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableTObject;
import org.csstudio.ams.filter.FilterConditionProcessVariable.Operator;
import org.csstudio.ams.filter.FilterConditionProcessVariable.SuggestedProcessVariableType;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.dal.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilterConditionProcessVariableIntegrationWithDALTest {

	private static final String TEST_CHANNEL_NAME = "epics://krykWeather:Temp_ai.VAL";
	private static final int FILTER_CONDITION_ID = 42;
	private static final int FILTER_ID = 23;
	private FilterConditionProcessVariable _filterConditionPV;
	volatile boolean _connected; 

	@Before
	public void setUp() throws Exception {
		_filterConditionPV = new FilterConditionProcessVariable();

		FilterConditionProcessVariableTObject configuration = new FilterConditionProcessVariableTObject(
				FILTER_CONDITION_ID, TEST_CHANNEL_NAME, Operator.EQUALS,
				SuggestedProcessVariableType.DOUBLE, new Double(1.0));
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault()
				.getProcessVariableConnectionService();
		IProcessVariableAddress address = ProcessVariableAdressFactory
				.getInstance().createProcessVariableAdress(TEST_CHANNEL_NAME);
		_connected = false;
		service.register(new IProcessVariableValueListener<Double>() {
			public void connectionStateChanged(ConnectionState connectionState) {
				if( ConnectionState.CONNECTED.equals(connectionState) )
				{
					_connected = true;
				}
			}

			public void valueChanged(Double value, Timestamp timestamp) {
			}

			public void errorOccured(String error) {
				
			}

            public void valueChanged(Double value)
            {
                // TODO Auto-generated method stub
                
            }
		}, address,ValueType.DOUBLE);

		_filterConditionPV.doInit(configuration, FILTER_CONDITION_ID,
				FILTER_ID, service, address);
	}

	@After
	public void tearDown() throws Exception {
		_connected = false;
		_filterConditionPV = null;
		System.gc(); // hopefully service clean up will run...
		Thread.yield();
		System.gc();
	}

	@Test(timeout=5000)
	public void testMatchWithDALValue() {
		waitToBeConnected();
		
		_filterConditionPV.match(new MockMapMessage() {});
	}

	private void waitToBeConnected() {
		while(! _connected) {
			Thread.yield();
		}
	}

}
