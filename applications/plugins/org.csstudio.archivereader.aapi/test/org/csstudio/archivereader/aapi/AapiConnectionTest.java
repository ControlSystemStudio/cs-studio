package org.csstudio.archivereader.aapi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		requestData.setFromTime(1277098200);
		requestData.setToTime(1277357400);
		requestData.setNum(500);
		requestData.setPV(new String[] {"krykWeather:Temp_ai"});
		requestData.setPvSize(10);
		requestData.setConversParam(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD.getMethodNumber());
		AnswerData data = _aapiClient.getData(requestData);
		assertNotNull(data);
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
