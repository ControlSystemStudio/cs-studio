package org.csstudio.archive.reader.rdb.jparc;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;

public class RDBValueIterator implements ValueIterator
{
	private long step;
	final private ITimestamp end;
	private IDoubleValue value;

	public RDBValueIterator(RDBUtil rdb, String name, ITimestamp start,
            ITimestamp end)
    {
		// TODO Perform actual RDB query
		
		// Demo will return 10 dummy samples
		step = (end.seconds() - start.seconds()) / 10;
		if (step <= 0)
			step = 1;
		value = ValueFactory.createDoubleValue(start, ValueFactory.createOKSeverity(),
				"Demo",
				ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 2, "Volt"),
				IValue.Quality.Original, new double[] { 3.14 });
		this.end = end;
    }

	@Override
	public boolean hasNext()
	{
		return value != null;
	}

	@Override
	public IValue next() throws Exception
	{
		final IValue result = value;
		
		// TODO Get next result from RDB query
		
		// Creating next fake sample
		final ITimestamp time = TimestampFactory.createTimestamp(
				value.getTime().seconds() + step,
				value.getTime().nanoseconds());
		if (time.isGreaterThan(end))
			value = null;
		else
			value = ValueFactory.createDoubleValue(time,
					value.getSeverity(),
					value.getStatus(),
					(INumericMetaData) value.getMetaData(),
					value.getQuality(),
					value.getValues());
		return result;
	}

	@Override
	public void close()
	{
		// TODO Close RDB PreparedStatement or ResultSet
	}
}
