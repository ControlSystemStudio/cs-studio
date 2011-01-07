package org.csstudio.archivereader.aapi;

import static org.junit.Assert.*;

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

	@Before
	public void setUp() throws Exception {
		_aapiClient = new AapiClient(HOST, PORT);
	}

	@Test
	public void testGetData() {
		RequestData requestData = new RequestData();
		requestData.setFromTime(1267350000);
		requestData.setToTime(1267355000);
		requestData.setUFromTime(0);
		requestData.setUToTime(0);
		requestData.setNumberOfSamples(10);
		requestData.setPvList(new String[] {"krykWeather:vWindBoe_ai"});
		requestData.setConversParam(AAPI.DEADBAND_PARAM);
		requestData.setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);
		AnswerData data = _aapiClient.getData(requestData);
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
		printout(data);
	}
	
	private void printout(AnswerData data) {
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
	}

}
