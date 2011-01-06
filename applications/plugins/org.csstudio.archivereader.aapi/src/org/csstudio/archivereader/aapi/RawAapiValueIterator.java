package org.csstudio.archivereader.aapi;

import java.util.List;

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.ITimestamp;

import de.desy.aapi.AapiClient;
import de.desy.aapi.AnswerData;

public class RawAapiValueIterator extends AapiValueIterator {


	public RawAapiValueIterator(AapiClient aapiClient, int key, String name,
			ITimestamp start, ITimestamp end) {
		super(aapiClient, key, name, start, end);
		// TODO Auto-generated constructor stub
	}

	@Override
	void dataConversion(AnswerData answerData, List<IMinMaxDoubleValue> result) {
		// TODO Auto-generated method stub
		
	}

}
