package org.csstudio.archivereader.aapi;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;

import de.desy.aapi.AapiClient;
import de.desy.aapi.AnswerData;
import de.desy.aapi.RequestData;

public abstract class AapiValueIterator implements ValueIterator {

	private final AapiClient _aapiClient;
	RequestData _requestData;
	
	private List<IMinMaxDoubleValue> _result = new ArrayList<IMinMaxDoubleValue>();

	public AapiValueIterator(AapiClient aapiClient, int key, String name,
			ITimestamp start, ITimestamp end) {
		_aapiClient = aapiClient;
		_requestData = new RequestData();
		_requestData.setFromTime((int) start.seconds());
		_requestData.setToTime((int) end.seconds());
		_requestData.setPvList(new String[]{name});
	}
	
	public void setCount(int count) {
		_requestData.setNumberOfSamples(count);
	}
	
	
	@Override
	public boolean hasNext() {
		System.out.println(">>>>> AapiValueIterator.hasNext");
		if (_result.size() > 0) {
			return true;
		}
		System.out.println(">>>>> AapiValueIterator.hasNext no next value");
		return false;
	}

	@Override
	public IValue next() throws Exception {
		System.out.println(">>>>> AapiValueIterator.next");
		if (_result.size() > 0) {
			IValue val = _result.remove(0);
			IMinMaxDoubleValue mmval = (IMinMaxDoubleValue) val;
			System.out.println(">>>>> " + mmval.getTime() + " " + mmval.getValue());
			return val;
		}
		System.out.println(">>>>> AapiValueIterator.next return null");
		return null;
	}
	
	public void getData() {
		dataConversion(_aapiClient.getData(_requestData), _result);
	}

	@Override
	public void close() {
		_result.clear();
		_result = null;
	}

	abstract void dataConversion(AnswerData answerData, List<IMinMaxDoubleValue> result);
}
