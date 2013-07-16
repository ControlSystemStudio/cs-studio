package org.csstudio.archive.reader.monica;

import java.util.Vector;

import org.csstudio.archive.reader.Severity;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

import atnf.atoms.mon.PointData;

public class MonicaValueIterator implements ValueIterator {

	private int currentIndex = 0;
	private Vector<PointData> monicaValues;
		
	public MonicaValueIterator(Vector<PointData> monicaValues) {
		if (monicaValues==null)
			this.monicaValues = new Vector<PointData>();
		else
			this.monicaValues = monicaValues;
		
	}

	@Override
	public boolean hasNext() {
		if (currentIndex<monicaValues.size())
			return true;
		
		return false;
	}

	@Override
	public IValue next() throws Exception {
		PointData pointData = monicaValues.get(currentIndex);
		currentIndex++;
		return pointDatatToIValue(pointData);
	}

	@Override
	public void close() {
		currentIndex = 0;
	}
	
	private IValue pointDatatToIValue(PointData pointData) {
		
		double sec = pointData.getTimestamp().getAsDate().getTime()/1000;
				
		ITimestamp timeStamp =  TimestampFactory.fromDouble(sec);
		
		Object value = pointData.getData();
		ISeverity severity = null;
		
		if (pointData.isValid()) {
			if (pointData.getAlarm())
				severity = new Severity("MAJOR");
			else
				severity = new Severity("OK");
			
		} else {
			severity = new Severity("INVALID");
		}
		
		IValue ivalue = null;

		
		if (pointData.getData()==null) {
			ivalue = ValueFactory.createLongValue(timeStamp, severity, 
					"Connected",
					null,
					Quality.Original,
					new long[]{0});
			
			return ivalue;
		}
		
		if (value instanceof Float || value instanceof Double) {
			double v = ((Number) value).doubleValue();
			ivalue = ValueFactory.createDoubleValue(timeStamp, severity, 
					"Connected",
					null,
					Quality.Original,
					new double[]{v});
		} else if (value instanceof Integer || value instanceof Long) {
			long v = ((Number) value).longValue();
			ivalue = ValueFactory.createLongValue(timeStamp, severity, 
					"Connected",
					null,
					Quality.Original,
					new long[]{v});
		} else if (value instanceof Boolean) {
			IEnumeratedMetaData booleanMetaData = ValueFactory.createEnumeratedMetaData(new String[]{"true", "false"});
			int v = ((Boolean) value).booleanValue() ? 0:1;
			ivalue = ValueFactory.createEnumeratedValue(timeStamp, severity, 
					"Connected",
					booleanMetaData,
					Quality.Original,
					new int[]{v});
		} else if (value instanceof String) {
			ivalue = ValueFactory.createStringValue(timeStamp, severity, 
					"Connected",
					Quality.Original,
					new String[]{value.toString()});
		} else {
			//TODO: AbsTime RelTime Angle are not supported at the moment.
			System.out.println("Unknow type- " + value.toString());			
			return null;
		}
		
		return ivalue;
	}

}
