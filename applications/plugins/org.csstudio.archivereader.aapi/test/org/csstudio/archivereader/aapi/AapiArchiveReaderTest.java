package org.csstudio.archivereader.aapi;

import static org.junit.Assert.*;

import org.csstudio.archivereader.UnknownChannelException;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;
import de.desy.aapi.RequestData;

public class AapiArchiveReaderTest {

	private AapiArchiveReader _aapiArchiveReader;

	@Before
	public void setUp() throws Exception {
		_aapiArchiveReader = new AapiArchiveReader("aapi://kryksdds.desy.de:4056");
	}

	@Test
	public void testGetData() throws UnknownChannelException, Exception {
		ITimestamp start = TimestampFactory.createTimestamp(1267350000, 0);
		ITimestamp end = TimestampFactory.createTimestamp(1267355000, 0);
		ValueIterator optimizedValues = _aapiArchiveReader.getOptimizedValues(0, "krykWeather:Temp_ai", start, end, 200);
		assertNotNull(optimizedValues);
		printout(optimizedValues);
	}
	
	private void printout(ValueIterator optimizedValues) throws Exception {
		while (optimizedValues.hasNext()) {
			IMinMaxDoubleValue value = (IMinMaxDoubleValue) optimizedValues.next();
			System.out.print("time: " + value.getTime().toString());
			System.out.print("\tmin: " + value.getValue());
		}
//			System.out.print("\tmax: " + data.getData()[i+1]);
//			System.out.println("\tavr: " + data.getData()[i+2]);
	}

	@After
	public void tearDown() throws Exception {
	}

}
