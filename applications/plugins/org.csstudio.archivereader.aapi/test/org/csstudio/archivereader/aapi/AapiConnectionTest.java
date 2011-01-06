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
		requestData.setToTime(1267350060);
		requestData.setUFromTime(0);
		requestData.setUToTime(0);
		requestData.setNumberOfSamples(20);
		requestData.setPvList(new String[] {"krykWeather:Temp_ai"});
		requestData.setConversParam(AAPI.DEADBAND_PARAM);
//		requestData.setConversionMethod(AapiReductionMethod.NO_FILTERING_METHOD);
		requestData.setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);
		AnswerData data = _aapiClient.getData(requestData);
		assertNotNull(data);
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
	}

}
