package org.csstudio.archivereader.aapi;

import java.util.List;

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;

public class MinMaxAapiValueIterator extends AapiValueIterator {

	private INumericMetaData _meta;
	private AnswerData _data;
	//TODO (jhatje) paramter hochreichen.
	public MinMaxAapiValueIterator(AapiClient aapiClient, int key, String name,
			ITimestamp start, ITimestamp end, int count) {
		super(aapiClient, key, name, start, end);
		setCount(count);
		_requestData.setConversParam(AAPI.DEADBAND_PARAM);
		_requestData
				.setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);

	}

	@Override
	void dataConversion(AnswerData answerData, List<IMinMaxDoubleValue> result) {
		_meta = ValueFactory.createNumericMetaData(_data.getDisplayLow(),
				_data.getDisplayHigh(), _data.getLowAlarm(),
				_data.getHighWarning(), _data.getLowAlarm(),
				_data.getHighAlarm(), _data.getPrecision(), _data.getEgu());
		for (int i = 0; i+2 < _data.getData().length; i = i+3) {
			
			ITimestamp time = TimestampFactory.createTimestamp(
					_data.getTime()[i],
					_data.getUTime()[i]);
			double[] value = new double[1];
			value[0] = _data.getData()[i+2];
			ISeverity sevr = new SeverityImpl("NO_ALARM", true, true);
			String stat = "ok";
			Double min = _data.getData()[i];
			Double max = _data.getData()[i+1];
			result.add(ValueFactory.createMinMaxDoubleValue(time, sevr, stat,
					(INumericMetaData) _meta, IValue.Quality.Interpolated,
					value, min, max));
		// TODO Auto-generated method stub
		}	
	}
	
}
