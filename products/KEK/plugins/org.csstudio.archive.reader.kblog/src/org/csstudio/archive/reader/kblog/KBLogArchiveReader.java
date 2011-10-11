package org.csstudio.archive.reader.kblog;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;

/**
 * ArchiveReader for kblog
 * 
 * @author Takashi Nakamoto
 */
public class KBLogArchiveReader implements ArchiveReader {

	public KBLogArchiveReader(final String url) throws Exception
	{
		// TODO do whatever need to be done during initialization.
	}
	
	@Override
	public String getServerName() {
		return "kblog";
	}

	@Override
	public String getURL() {
		return "kblog://localhost";
	}

	@Override
	public String getDescription() {
		return "KBLog archive reader";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
		return new ArchiveInfo[]
        {
			new ArchiveInfo("kglog", "localhost", 1)
        };
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern)
			throws Exception {
		// TODO invoke kblog commands to search PV names
		return new String[]{"PV1", "PV2", "PV3"};
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		// TODO invoke kblog commands or grep to serach PV names
		return new String[]{"PV4", "PV5", "PV6"};
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		// TODO make a new thread and invoke kblog commands to obtain raw values
		// TODO wait for the thread
		return null;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		// TODO make a new thread and invoke kblog commands to obtain raw values
		// TODO wait for the thread
		return null;
	}

	@Override
	public void cancel() {
		// TODO kill running value-obtaining threads.
	}

	@Override
	public void close() {
		cancel();
		
		// TODO do whatever need to be done during finalization.
	}
}
