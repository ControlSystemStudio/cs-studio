package org.csstudio.archive.reader.kblog;

import java.util.ArrayList;
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
	private ArrayList<KBLogRDProcess> kblogrdProcesses;
	
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
		
		// Container of kblogrd processes.
		kblogrdProcesses = new ArrayList<KBLogRDProcess>();
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
		//      name searching can be canceled on the way.
		
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
		String subArchiveName = archiveInfos[key-1].getName();
		KBLogRDProcess kblogrdProcess = new KBLogRDProcess(subArchiveName, name, start, end, 0);
		
		return kblogrdProcess.start();		
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
		
		String subArchiveName = archiveInfos[key-1].getName();
		KBLogRDProcess kblogrdProcess = new KBLogRDProcess(subArchiveName, name, start, end, stepSecond);
		synchronized (kblogrdProcesses) {
			kblogrdProcesses.add(kblogrdProcess);
		}
		
		return kblogrdProcess.start();
	}

	@Override
	public void cancel() {
		synchronized (kblogrdProcesses) {
			KBLogRDProcess[] procs = kblogrdProcesses.toArray(new KBLogRDProcess[0]);
			for (KBLogRDProcess proc : procs) {
				if (!proc.isFinished()) {
					proc.cancel();
				}
				
				kblogrdProcesses.remove(proc);
			}
		}
		
		System.err.println("Cancel is requested.");
	}

	@Override
	public void close() {
		System.err.println("KBLogArchiveReader.close() is requested.");
		
		cancel();
		
		// TODO do whatever need to be done during finalization.
	}
}
