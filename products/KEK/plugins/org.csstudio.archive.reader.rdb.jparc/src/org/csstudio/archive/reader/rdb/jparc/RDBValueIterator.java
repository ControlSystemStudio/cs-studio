/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb.jparc;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** Iterator over archive samples for a query
 *  @author Kay Kasemir
 */
public class RDBValueIterator implements ValueIterator
{
	private long step;
	final private ITimestamp end;
	private IDoubleValue value;
	final private int basevalue;

	public RDBValueIterator(final RDBUtil rdb, final String name,
			final ITimestamp start, final ITimestamp end)
    {
		// TODO Perform actual RDB query
		
		// Demo will return 10 dummy samples
		step = (end.seconds() - start.seconds()) / 10;
		if (step <= 0)
			step = 1;
		basevalue = name.hashCode() % 100;
		value = ValueFactory.createDoubleValue(start, ValueFactory.createOKSeverity(),
				"Demo",
				ValueFactory.createNumericMetaData(0, 10, 0, 0, 0, 0, 2, "Volt"),
				IValue.Quality.Original, getValue(start));
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
					getValue(time));
		return result;
	}

	private double[] getValue(final ITimestamp time)
	{
		return new double[] { basevalue + time.seconds()/60 % 1000 };
	}
	
	@Override
	public void close()
	{
		// TODO Close RDB PreparedStatement or ResultSet
	}
}
