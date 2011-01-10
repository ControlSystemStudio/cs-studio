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

public class RawAapiValueIterator extends AapiValueIterator {


	public RawAapiValueIterator(AapiClient aapiClient, int key, String name,
			ITimestamp start, ITimestamp end) {
		super(aapiClient, key, name, start, end);
		setConversionParam(AAPI.DEADBAND_PARAM);
		setConversionMethod(AapiReductionMethod.TAIL_RAW_METHOD);
	}

	@Override
	void dataConversion(AnswerData answerData, List<IValue> result) {
		INumericMetaData meta = ValueFactory.createNumericMetaData(answerData.getDisplayLow(),
				answerData.getDisplayHigh(), answerData.getLowAlarm(),
				answerData.getHighWarning(), answerData.getLowAlarm(),
				answerData.getHighAlarm(), answerData.getPrecision(), answerData.getEgu());
		for (int i = 0; i < answerData.getData().length; i++) {
			
			ITimestamp time = TimestampFactory.createTimestamp(
					answerData.getTime()[i],
					answerData.getUTime()[i]);
			double[] value = new double[1];
			value[0] = answerData.getData()[i];
			ISeverity sevr = new SeverityImpl("NO_ALARM", true, true);
			String stat = "ok";
			result.add(ValueFactory.createDoubleValue(time, sevr, stat,
					(INumericMetaData) meta, IValue.Quality.Interpolated,
					value));
		}	
	}

}
