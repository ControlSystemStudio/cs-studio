package org.csstudio.scan.log.derby;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.MemoryDataLog;

public class DerbyDataLog extends MemoryDataLog implements DataLog
{
	public DerbyDataLog()
	{
		// TODO Need to locate the correct log based on the scan name/ID
	}

	@Override
	public void log(ScanSample sample)
	{
		super.log(sample);
	}

	@Override
	public long getLastScanDataSerial()
	{
		return super.getLastScanDataSerial();
	}

	@Override
	public ScanData getScanData()
	{
		return super.getScanData();
	}
}
