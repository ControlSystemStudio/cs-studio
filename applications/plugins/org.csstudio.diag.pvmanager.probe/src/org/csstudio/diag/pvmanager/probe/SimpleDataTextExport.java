package org.csstudio.diag.pvmanager.probe;

import java.io.IOException;
import java.io.Writer;

import org.epics.util.array.ListNumber;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueUtil;

public class SimpleDataTextExport implements DataExportFormat {
	
	// TODO: we should take these from a default place
	private static TimestampFormat timeFormat = new TimestampFormat(
			"yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

	@Override
	public void export(Object value, Writer writer) {
		if (!canExport(value)) {
			throw new IllegalArgumentException("Type " + value.getClass().getSimpleName() + " is not supported by this data export.");
		}
		
		try {
			Time time = ValueUtil.timeOf(value);
			if (time != null && time.getTimestamp() != null) {
				writer.append("\"")
			      .append(timeFormat.format(time.getTimestamp()))
			      .append("\" ");
			}
			
			Alarm alarm = ValueUtil.alarmOf(value);
			if (alarm != null) {
			    writer.append(alarm.getAlarmSeverity().name())
			      .append(" ")
			      .append(alarm.getAlarmName());
			}

			if (value instanceof VNumber) {
				
				
			    writer.append(" ")
			      .append(Double.toString(((VNumber) value).getValue().doubleValue()));
			}
			if (value instanceof VNumberArray) {
				ListNumber data = ((VNumberArray) value).getData();
				for (int i = 0; i < data.size(); i++) {
					writer.append(" ")
					      .append(Double.toString(data.getDouble(i)));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Write failed", e);
		}
	}

	@Override
	public boolean canExport(Object data) {
		if (data instanceof VNumber) {
			return true;
		}
		
		if (data instanceof VNumberArray) {
			return true;
		}
		
		return false;
	}

}
