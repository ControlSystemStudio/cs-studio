package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.csstudio.archive.reader.influxdb.ChunkReader;
import org.csstudio.archive.influxdb.MetaTypes;
import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder.Factory;
import org.influxdb.dto.QueryResult;

/**
 * Class for reading chunked data from aggregate queries
 * (i.e. "SELECT MEAN(),MAX(),MIN()")
 * for both sample and metadata simultaneously.
 * @author Amanda Carpenter
 *
 */
public class AggregatedChunkReader extends ChunkReader
{
	AggregatedChunkReader(BlockingQueue<QueryResult> sample_queue, Instant last_sample_time,
			BlockingQueue<QueryResult> metadata_queue, Instant last_metadata_time, int timeout_secs,
			Factory decoder_factory)
	{
		super(sample_queue, last_sample_time, metadata_queue, last_metadata_time, timeout_secs, decoder_factory);
	}
	
    @Override
    public Object getValue(final String colname) throws Exception
    {
    	if ("status".equals(colname))
    		return "";
    	else if ("severity".equals(colname))
    		return "NONE";
    	else
    		return super.getValue(colname);
    }

    @Override
    public boolean hasValue(final String colname)
    {
        return "status".equals(colname) || "severity".equals(colname) || super.hasValue(colname);
    }
}
