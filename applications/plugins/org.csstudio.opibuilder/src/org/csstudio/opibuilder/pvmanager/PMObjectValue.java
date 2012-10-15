package org.csstudio.opibuilder.pvmanager;

import java.util.List;

import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.opibuilder.datadefinition.FormatEnum;
import org.csstudio.opibuilder.datadefinition.NotImplementedException;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Time;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.ValueUtil;

/**A {@link IValue} that represents PVManager object value.
 * @author Xihui Chen
 *
 */
public class PMObjectValue implements IValue {
	

	private static final long serialVersionUID = -2371110066966439376L;
	
	/**
	 * Value from PVManager PV
	 */
	private Object latestValue;
	private List<Object> allValues;
	
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
//		Display display = ValueUtil.displayOf(latestValue);
//		if (display != null) {
//			return display.getFormat().format(ValueUtil.numericValueOf(latestValue));
//		}
//		if(latestValue instanceof Scalar)
//			return ((Scalar)latestValue).getValue().toString();		
	}
	
	@Override
	public String toString() {
		return format();
	}

}
