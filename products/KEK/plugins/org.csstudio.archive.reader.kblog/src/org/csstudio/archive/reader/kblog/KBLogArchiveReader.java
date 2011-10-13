package org.csstudio.archive.reader.kblog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.text.RegExHelper;
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
	
	private static DateFormat kblogrdTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final static String kblogrdOutFormat = "free";
	
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
		// TODO make a new thread and do the following operations in it so that
		//      name searching can be canceld on the way.
		
		String reg_exp = RegExHelper.fullRegexFromGlob(glob_pattern);
		System.err.println(reg_exp);
		
		return getNamesByRegExp(key, reg_exp);
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		// TODO make a new thread and do the following operations in it so that
		//      name searching can be canceled on the way.

		ArchiveInfo info = archiveInfos[key-1];
		return KBLogUtil.getProcessVariableNames(kblogRoot, info.getName(), reg_exp);
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		System.err.println("getRawValues from kblogrd.");
		
		String subArchiveName = archiveInfos[key-1].getName();
		String strStart = kblogrdTimeFormat.format(new Date(start.seconds() * 1000));
		String strEnd = kblogrdTimeFormat.format(new Date(end.seconds() * 1000));
		
		// TODO Make a new preference to set the path to kblogrd.
		String strCommand = "kblogrd -r " + name + " -t " + strStart + "-" + strEnd + " -f " + kblogrdOutFormat + " " + subArchiveName;
		System.err.println("Command: " + strCommand);
		
		Process proc = Runtime.getRuntime().exec(strCommand);
		ValueIterator iter = new KBLogValueIterator(proc.getInputStream(), name);
		
		// TODO handle standard error
		// TODO handle return value

		return iter;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		System.err.println("getOptimizedValues from kblogrd.");
		
		// TODO handle standard error
		// TODO handle return value
		
		// TODO return optimized values, not the raw ones.

		return getRawValues(key, name, start, end);
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
