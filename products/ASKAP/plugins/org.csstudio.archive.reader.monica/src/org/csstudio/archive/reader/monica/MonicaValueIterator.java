package org.csstudio.archive.reader.monica;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import atnf.atoms.mon.PointData;

public class MonicaValueIterator implements ValueIterator {
	
    private static final Display DisplayNone = ValueFactory.newDisplay(Double.NaN, Double.NaN, 
            Double.NaN, "", NumberFormats.toStringFormat(), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);


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
	public VType next() throws Exception {
		PointData pointData = monicaValues.get(currentIndex);
		currentIndex++;
		return pointDatatToIValue(pointData);
	}

	@Override
	public void close() {
		currentIndex = 0;
	}
	
	private VType pointDatatToIValue(PointData pointData) {
		
		Date date = pointData.getTimestamp().getAsDate();
				
		Time timeStamp =  ValueFactory.newTime(Timestamp.of(date));
		
		Object value = pointData.getData();
		
		Alarm alarm = null;
		
		if (pointData.isValid()) {
			if (pointData.getAlarm())
				alarm = ValueFactory.newAlarm(AlarmSeverity.MAJOR, "ALARM");
			else
				alarm = ValueFactory.newAlarm(AlarmSeverity.NONE, "OK");
			
		} else {
			alarm = ValueFactory.newAlarm(AlarmSeverity.INVALID, "INVALID");
		}
		
		VType ivalue = null;

		
		if (pointData.getData()==null) {
			ivalue = ValueFactory.newVInt(0, alarm, timeStamp, DisplayNone);
			return ivalue;
		}
		
		if (value instanceof Float || value instanceof Double) {
			double v = ((Number) value).doubleValue();
			ivalue = ValueFactory.newVDouble(v, alarm, timeStamp, DisplayNone);
			
		} else if (value instanceof Integer || value instanceof Long) {
			long v = ((Number) value).longValue();
			ivalue = ValueFactory.newVDouble((double) v, alarm, timeStamp, DisplayNone);
			
		} else if (value instanceof Boolean) {
			List<String> labels = new ArrayList<>();
			labels.add("true");
			labels.add("false");
			
			int v = ((Boolean) value).booleanValue() ? 0:1;
			ivalue = ValueFactory.newVEnum(v, labels, alarm, timeStamp);
			
		} else if (value instanceof String) {			
			ivalue = ValueFactory.newVString(value.toString(), alarm, timeStamp);
			
		} else {
			//TODO: AbsTime RelTime Angle are not supported at the moment.
			System.out.println("Unknow type- " + value.toString());			
			return null;
		}
		
		return ivalue;
	}

}
