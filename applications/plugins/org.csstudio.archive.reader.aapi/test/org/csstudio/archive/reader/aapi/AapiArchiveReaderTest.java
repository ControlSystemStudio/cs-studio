package org.csstudio.archive.reader.aapi;

import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.aapi.AapiArchiveReader;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AapiArchiveReaderTest {

	private AapiArchiveReader _aapiArchiveReader;

	@Before
	public void setUp() throws Exception {
		_aapiArchiveReader = new AapiArchiveReader(
				"aapi://kryksdds.desy.de:4056");
	}

	@Test
	public void testGetRawData() throws UnknownChannelException, Exception {
		ITimestamp start = TimestampFactory.createTimestamp(1267350000, 0);
		ITimestamp end = TimestampFactory.createTimestamp(1267355000, 0);
		ValueIterator rawValues = _aapiArchiveReader.getRawValues(
				0, "krykWeather:vWindBoe_ai", start, end);
		assertNotNull(rawValues);
		printoutRaw(rawValues);
	}

	private void printoutRaw(ValueIterator rawValues) {
		// TODO Auto-generated method stub
		
	}

	@Test
	public void testGetMinMaxData() throws UnknownChannelException, Exception {
		ITimestamp start = TimestampFactory.createTimestamp(1267350000, 0);
		ITimestamp end = TimestampFactory.createTimestamp(1267355000, 0);
		ValueIterator optimizedValues = _aapiArchiveReader.getOptimizedValues(
				0, "krykWeather:vWindBoe_ai", start, end, 10);
		assertNotNull(optimizedValues);
		printoutMinMax(optimizedValues);
	}

	private void printoutMinMax(ValueIterator optimizedValues) throws Exception {
		while (optimizedValues.hasNext()) {
			IMinMaxDoubleValue value = (IMinMaxDoubleValue) optimizedValues
					.next();
			System.out.print("time: " + value.getTime().toString());
			System.out.print("\tmin: " + value.getMinimum());
			System.out.print("\tmax: " + value.getMaximum());
			System.out.println("\tavr: " + value.getValue());
		}
	}

	@After
	public void tearDown() throws Exception {
		_aapiArchiveReader.close();
		_aapiArchiveReader = null;
	}

}
