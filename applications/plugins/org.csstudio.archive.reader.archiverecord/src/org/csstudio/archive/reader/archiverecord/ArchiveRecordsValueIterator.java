package org.csstudio.archive.reader.archiverecord;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

public class ArchiveRecordsValueIterator implements ValueIterator {

	private final String _name;
	private final ITimestamp _start;
	private final ITimestamp _end;

	private List<IValue> _result = new ArrayList<IValue>();

	public ArchiveRecordsValueIterator(String name, ITimestamp start,
			ITimestamp end) {
				_name = name;
				_start = start;
				_end = end;
	}

	@Override
	public boolean hasNext() {
		if (_result.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IValue next() throws Exception {
		if (_result.size() > 0) {
			return _result.remove(0);
		}
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public void getData() {
		int error = 0;
        	final ArchiveRecord ar = new ArchiveRecord(_name);
    		int dim = 0;
                try {
					dim = ar.getDimension();
                if (dim <= 0) {
                    error = -1;
                    dim = 0;
                }
                if (dim > 0) {
                    ar.getAllFromCA();
                }
                } catch (Exception e) {
                	// TODO Auto-generated catch block
                	e.printStackTrace();
                }
			final int count = 1; // do not use WF answerClass.getCount();
			final int num_samples = dim;
//			final IValue samples[]= new IValue[num_samples];
			final INumericMetaData meta = ValueFactory.createNumericMetaData(
					100,0, //DisplayHigh(),DisplayLow(),
					100,0, //High,LowAlarm(),
					100,0, //High,LowWarn(),
					2, " "//Precision(),Egu()
					);
			for (int si=0; si<num_samples; si++) {
				final long secs = ar.getTime()[si];
				final long nano = ar.getNsec()[si];
				final ITimestamp time = TimestampFactory.createTimestamp(secs, nano);

				final ISeverity sevClass= new SeverityImpl("",true,true);
				final double values[] = new double[count]; // count=1
			    for (int vi=0; vi<count; ++vi) {
                    values[vi] = ar.getVal()[si];
                }
//				samples[si] = ValueFactory.createDoubleValue(time, sevClass,"", meta,IValue.Quality.Original, values);
				_result.add(ValueFactory.createDoubleValue(time, sevClass,"", meta,IValue.Quality.Original, values));
			}


	}

}
