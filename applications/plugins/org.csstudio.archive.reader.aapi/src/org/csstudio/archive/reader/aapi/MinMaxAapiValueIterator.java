package org.csstudio.archive.reader.aapi;

import java.util.List;

import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

import de.desy.aapi.AAPI;
import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;

public class MinMaxAapiValueIterator extends AapiValueIterator {

	public MinMaxAapiValueIterator(AapiClient aapiClient, int key, String name,
			ITimestamp start, ITimestamp end, int count) {
		super(aapiClient, key, name, start, end);
		setCount(count);
		setConversionParam(AAPI.DEADBAND_PARAM);
		setConversionMethod(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD);
	}

	@Override
	void dataConversion(AnswerData answerData, List<IValue> result) {
		INumericMetaData meta = ValueFactory.createNumericMetaData(answerData.getDisplayLow(),
				answerData.getDisplayHigh(), answerData.getLowAlarm(),
				answerData.getHighWarning(), answerData.getLowAlarm(),
				answerData.getHighAlarm(), answerData.getPrecision(), answerData.getEgu());
		for (int i = 0; i+2 < answerData.getData().length; i = i+3) {
			
			ITimestamp time = TimestampFactory.createTimestamp(
					answerData.getTime()[i],
					answerData.getUTime()[i]);
			double[] value = new double[1];
			value[0] = answerData.getData()[i+2];
			ISeverity sevr = new SeverityImpl("NO_ALARM", true, true);
			String stat = "ok";
			Double min = answerData.getData()[i];
			Double max = answerData.getData()[i+1];
			result.add(ValueFactory.createMinMaxDoubleValue(time, sevr, stat,
					(INumericMetaData) meta, IValue.Quality.Interpolated,
					value, min, max));
		}	
	}
	
}
