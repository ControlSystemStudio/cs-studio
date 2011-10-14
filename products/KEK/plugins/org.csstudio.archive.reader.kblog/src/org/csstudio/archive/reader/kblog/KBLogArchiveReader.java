package org.csstudio.archive.reader.kblog;

import java.io.IOException;
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
	
	// DO NOT READ OR WRITE THIS VALUE OUTSIDE getUniqueCommandID().
	private static int uniqueCommandId = 0;
	
	/**
	 * This must be called only once right before "kblogrd" command is called.
	 * 
	 * @return Command ID unique in this class.
	 */
	private synchronized int getUniqueCommandID() {
		uniqueCommandId += 1;
		return uniqueCommandId;
	}
	
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
			archiveInfos[i] = new ArchiveInfo(subArchives[i], "", key);
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
		return getValuesFromKBLogRD(key, name, start, end, 0);
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {

		if (count <= 0)
            throw new Exception("Count must be positive");

		double diff = end.toDouble() - start.toDouble();
		if (diff <= 0)
			throw new Exception("Difference of start time and end time must be greater than 0 second.");

		// At least, two points are required to draw a chart.
		if (count == 1)
			count = 2;
		
		int stepSecond = (int)Math.floor(diff / (count-1));
		if (stepSecond <= 0) {
			// No need to thin out values.
			return getRawValues(key, name, start, end);
		}
		
		return getValuesFromKBLogRD(key, name, start, end, stepSecond);
	}
	
	private ValueIterator getValuesFromKBLogRD(int key, String name, ITimestamp start, ITimestamp end, int stepSecond) {
		String kblogrdCommand = KBLogPreferences.getPathToKBLogRD();
		String subArchiveName = archiveInfos[key-1].getName();
		String strStart = kblogrdTimeFormat.format(new Date(start.seconds() * 1000));
		String strEnd = kblogrdTimeFormat.format(new Date(end.seconds() * 1000));
	
		// TODO Make a new preference to set the path to kblogrd.
		String strCommand;
		if (stepSecond > 0)
			strCommand = kblogrdCommand + " -r " + name + " -t " + strStart + "-" + strEnd + "d" + stepSecond + " -f " + kblogrdOutFormat + " " + subArchiveName;
		else
			strCommand = kblogrdCommand + " -r " + name + " -t " + strStart + "-" + strEnd + " -f " + kblogrdOutFormat + " " + subArchiveName;
			
		int commandId = getUniqueCommandID();
		Logger.getLogger(Activator.ID).log(Level.INFO, "Command " + commandId + ": " + strCommand);
		
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(strCommand);
			ValueIterator iter = new KBLogValueIterator(proc.getInputStream(), name, commandId);

			// Handle the error messages in a separate thread.
			Thread errHandler = new KBLogErrorHandleThread(proc.getErrorStream(), commandId);
			errHandler.start();
			
			return iter;
		} catch (IOException e) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE, "Failed to run " + kblogrdCommand + " (" + commandId + ").");
		}
		
		// TODO try to see what happens when the process is forcibly shut down here.
		//      If it works well, store "proc" in an instance field and kill it when cancel() method is called.
		
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
