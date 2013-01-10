package org.csstudio.archive.reader.aapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;
import de.desy.aapi.RequestData;

public class AapiConnectionTest {

	private static final String HOST = "kryksdds.desy.de";
	private static final int PORT = 4056;
	private AapiClient _aapiClient;
	private RequestData _requestData;

	@Before
	public void setUp() throws Exception {
		_aapiClient = new AapiClient(HOST, PORT);
		_requestData = new RequestData();
		_requestData.setUFromTime(0);
		_requestData.setUToTime(0);
		_requestData.setPvList(new String[] {"krykWeather:vWindBoe_ai"});
	}

	@Test
	public void testGetRawData() {
		_requestData.setFromTime(1267350000);
		_requestData.setToTime(1267351000);
		_requestData.setConversParam(AAPI.DEADBAND_PARAM);
		_requestData.setConversionMethod(AapiReductionMethod.TAIL_RAW_METHOD);
		AnswerData data = _aapiClient.getData(_requestData);
		assertNotNull(data);
		printoutRaw(data);
	}

	
	private void printoutRaw(AnswerData data) {
		// TODO Auto-generated method stub
		
	}

	@Test
	public void testGetMinMaxData() {
		_requestData.setFromTime(1267350000);
		_requestData.setToTime(1267355000);
		_requestData.setNumberOfSamples(10);
		_requestData.setConversParam(AAPI.DEADBAND_PARAM);
		_requestData.setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);
		AnswerData data = _aapiClient.getData(_requestData);
		assertNotNull(data);
		//assert fourth sample 
		//time
		assertEquals(1267351500, data.getTime()[9]);
		//min
		assertEquals(3.5462508, data.getData()[9], 0.0000001);
		//max
		assertEquals(5.204016, data.getData()[10], 0.0000001);
		//avr
		assertEquals(4.279712, data.getData()[11], 0.0000001);
		printoutMinMax(data);
	}
	
	private void printoutMinMax(AnswerData data) {
		int j = 0;
		for (int i = 0; i+2 < data.getData().length; i = i+3) {
			j++;
			System.out.print(j + " time: " + data.getTime()[i]);
			System.out.print("\tmin: " + data.getData()[i]);
			System.out.print("\tmax: " + data.getData()[i+1]);
			System.out.println("\tavr: " + data.getData()[i+2]);
		}
	}

	@After
	public void tearDown() throws Exception {
		_aapiClient = null;
		_requestData = null;
	}
}
