package org.csstudio.archive.reader.kblog;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	private final static String scheme = "kblog://";
	private String kblogRoot; 
	private ArchiveInfo[] archiveInfos;
	
	/**
	 * Constructor of KBLogArchiveReader.
	 * 
	 * @param url URL must start with "kblog://".
	 */
	public KBLogArchiveReader(final String url)
	{
		// TODO do whatever need to be done during initialization.
		
		// Parse URL
		if (!url.startsWith(scheme)) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, "Wrong URL for KBLogArchiveReader: " + url);
		}
		kblogRoot = url.substring(scheme.length());
		
		// Obtain sub archive names
		String[] subArchives = KBLogUtil.getSubArchives(kblogRoot);
		if (subArchives.length == 0) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, "Failed to find archives in " + kblogRoot);
		}
		
		archiveInfos = new ArchiveInfo[subArchives.length];
		for (int i=0; i<subArchives.length; i++) { 
			int key = i + 1;
			archiveInfos[i] = new ArchiveInfo(subArchives[i], "dummy", key);
		}
	}
	
	@Override
	public String getServerName() {
		return "kblog";
	}

	@Override
	public String getURL() {
		return scheme + kblogRoot;
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
		return archiveInfos;
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
