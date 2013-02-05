package org.csstudio.opibuilder.pvmanager;

import java.util.Arrays;
import java.util.List;

import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.opibuilder.datadefinition.FormatEnum;
import org.csstudio.opibuilder.datadefinition.NotImplementedException;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.ValueUtil;

/**A {@link IValue} that represents PVManager object value. The value is immutable.
 * @author Xihui Chen
 *
 */
public class PMObjectValue implements IValue {
	

	private static final String SPACE = " "; //$NON-NLS-1$

	private static final long serialVersionUID = -2371110066966439376L;
	
	private String cachedString = null;
	
	/**
	 * Value from PVManager PV
	 */
	private Object latestValue;
	private List<Object> allValues;
	
	/**
	 * @param pmValue value from PVManager, cannot be null.
	 * @param valueBuffered if all value is buffered. 
	 * If this is true, pmValue must not be empty.
	 */
	@SuppressWarnings("unchecked")
	public PMObjectValue(Object pmValue, boolean valueBuffered) {
		if(valueBuffered){
			this.allValues = (List<Object>) pmValue;		
			this.latestValue = allValues.get(allValues.size()-1);
			
		}else			
			this.latestValue = pmValue;	
	}

	public Object getLatestValue(){
		return latestValue;
	}
	
	public List<Object> getAllValues(){
		if(allValues == null)
			return Arrays.asList(latestValue);
		return allValues;
	}
	
	@Override
	public ITimestamp getTime() {
		Time time = ValueUtil.timeOf(latestValue);
		if(time != null)
			return TimestampFactory.createTimestamp(
					time.getTimestamp().getSec(), time.getTimestamp().getNanoSec());
		return null;
	}

	@Override
	public ISeverity getSeverity() {
		Alarm alarm = ValueUtil.alarmOf(latestValue);
		if(alarm != null){
			AlarmSeverity pmSeverity = alarm.getAlarmSeverity();
			switch (pmSeverity) {
			case INVALID:
			case UNDEFINED:
			default:
				return ValueFactory.createInvalidSeverity();
			case MAJOR:
				return ValueFactory.createMajorSeverity();
			case MINOR:
				return ValueFactory.createMinorSeverity();
			case NONE:		
				return ValueFactory.createOKSeverity();
			}		
		}		
		return null;
	}

	@Override
	public String getStatus() {
		Alarm alarm = ValueUtil.alarmOf(latestValue);
		if(alarm != null){
			return alarm.getAlarmName();		
		}
		return null;
	}

	@Override
	public Quality getQuality() {
		return Quality.Original;
	}

	@Override
	public IMetaData getMetaData() {
		if(latestValue instanceof VEnum){
			return ValueFactory.createEnumeratedMetaData(
					((VEnum)latestValue).getLabels().toArray(new String[0]));
		}
		Display display = ValueUtil.displayOf(latestValue);
		if (display != null) {
			return ValueFactory.createNumericMetaData(
					display.getLowerDisplayLimit(),
					display.getUpperDisplayLimit(),
					display.getLowerWarningLimit(),
					display.getUpperWarningLimit(),
					display.getLowerAlarmLimit(), 
					display.getUpperAlarmLimit(),
					display.getFormat().getMaximumFractionDigits(),
					display.getUnits());
		}
		return null;
	}

	@Override
	public String format(Format how, int precision) {
		throw new NotImplementedException();
	}

	@Override
	public String format() {
		return PVManagerHelper.getInstance().
				formatValue(FormatEnum.DEFAULT, latestValue, -1);
	}
	
	@Override
	public String toString() {
		if(cachedString == null){
			StringBuilder sb = new StringBuilder();
			ITimestamp time = getTime();
			if (time != null) {
				sb.append(time.toString());
				sb.append(SPACE); //$NON-NLS-1$
			}
			sb.append(format());
			sb.append(SPACE);
			ISeverity severity = getSeverity();
			if (severity != null) {
				sb.append(severity);
				sb.append(SPACE);
			}
			String status = getStatus();
			if (status != null)
				sb.append(status);
			cachedString = sb.toString();
		}
		return cachedString;
	}
	
	@Override
	public int hashCode() {
		return latestValue.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PMObjectValue)
			if(((PMObjectValue)obj).getLatestValue().equals(latestValue))
				return true;
		return false;
	}

}
