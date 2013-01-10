package org.csstudio.archive.reader.archiverecord;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;

public class ArchiveRecordReader implements ArchiveReader {

	private final String _url;

	public ArchiveRecordReader(String url) {
		_url = url;
	}

	@Override
	public String getServerName() {
		return "Archive Record";
	}

	@Override
	public String getURL() {
		return _url;
	}

	@Override
	public String getDescription() {
		return "no description available";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
        return new ArchiveInfo[] {
                new ArchiveInfo("archiveRecord", "Archive Record", 1)};	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern)
			throws Exception {
		//Archive Record does not support a name service, use channelarchiver instead.
		return null;
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		//Archive Record does not support a name service, use channelarchiver instead.
		return null;
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		ArchiveRecordsValueIterator valueIterator = new ArchiveRecordsValueIterator(name, start, end);
		valueIterator.getData();
		return valueIterator;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		return getRawValues(key, name, start, end);
	}

	@Override
	public void cancel() {

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
