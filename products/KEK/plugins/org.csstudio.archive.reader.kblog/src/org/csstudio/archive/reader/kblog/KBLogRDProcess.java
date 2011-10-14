package org.csstudio.archive.reader.kblog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;

public class KBLogRDProcess {
	// DO NOT READ OR WRITE THIS VALUE OUTSIDE getUniqueCommandID().
	private static int uniqueCommandId = 0;	

	private final static DateFormat kblogrdTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final static String kblogrdOutFormat = "free";
	
	private String strCommand;
	private String name;
	private KBLogValueIterator iter;
	private KBLogErrorHandleThread errHandler;
	private boolean started;
	private Process proc;
	private boolean canceled;
	
	/**
	 * Initialize the "kblogrd" command execution with given arguments.
	 * 
	 * @param subArchiveName Sub archive name (e.g. BM/DCCT)
	 * @param name PV name (e.g. BM_DCCT:HCUR)
	 * @param start Start time of data sequence
	 * @param end End time of data sequence
	 * @param stepSecond Interval of each data. Set this to 0 if you want to obtain all raw data in the specified range.
	 */
	public KBLogRDProcess(String subArchiveName, String name, ITimestamp start, ITimestamp end, int stepSecond) {
		String kblogrdCommand = KBLogPreferences.getPathToKBLogRD();
		String strStart = kblogrdTimeFormat.format(new Date(start.seconds() * 1000));
		String strEnd = kblogrdTimeFormat.format(new Date(end.seconds() * 1000));
		this.name = name;
	
		if (stepSecond > 0)
			this.strCommand = kblogrdCommand + " -r " + name + " -t " + strStart + "-" + strEnd + "d" + stepSecond + " -f " + kblogrdOutFormat + " " + subArchiveName;
		else
			this.strCommand = kblogrdCommand + " -r " + name + " -t " + strStart + "-" + strEnd + " -f " + kblogrdOutFormat + " " + subArchiveName;

		this.iter = null;
		this.errHandler = null;
		this.started = false;
		this.proc = null;
		this.canceled = false;
	}
	
	/**
	 * This method executes "kblogrd" command, and returns ValueIterator of the obtained archived values.
	 * With this method, error messages are also handled in a separate thread. 
	 * 
	 * This method can be called only once. From the second time, this method does nothing.  
	 * 
	 * @return ValueIterator of the obtained archived values.
	 */
	public synchronized ValueIterator start() {
		if (started || canceled)
			return null;
		
		int commandId = getUniqueCommandID();
		Logger.getLogger(Activator.ID).log(Level.INFO, "Command " + commandId + ": " + strCommand);
		
		try {
			proc = Runtime.getRuntime().exec(strCommand);
			iter = new KBLogValueIterator(proc.getInputStream(), name, commandId);

			// Handle the error messages in a separate thread.
			errHandler = new KBLogErrorHandleThread(proc.getErrorStream(), commandId);
			errHandler.start();
		} catch (IOException e) {
			String kblogrdCommand = KBLogPreferences.getPathToKBLogRD();
			Logger.getLogger(Activator.ID).log(Level.SEVERE, "Failed to run " + kblogrdCommand + " (" + commandId + ").");
		}
		
		// TODO try to see what happens when the process is forcibly shut down here.
		//      If it works well, store "proc" in an instance field and kill it when cancel() method is called.
		
		if (iter != null)
			started = true;
		
		return (ValueIterator)iter;
	}
	
	public synchronized boolean isFinished() {
		if (iter == null || errHandler == null)
			return false;
		
		if (canceled)
			return true;
		
		return iter.isClosed() && errHandler.isClosed();
	}
	
	/**
	 * Cancel execution of kblogrd.
	 * Note that this method does not immediately cancel the execution due to blocked I/O.
	 * So, it may take some time to complete this method.
	 */
	public synchronized void cancel() {
		if (isFinished())
			return;
		
		if (iter != null && !iter.isClosed())
			iter.close();
		
		if (errHandler != null && errHandler.isClosed())
			errHandler.close();
		
		if (proc != null)
			proc.destroy();
		
		canceled = true;
	}

	/**
	 * This must be called only once right before "kblogrd" command is called.
	 * 
	 * @return Command ID unique in this class.
	 */
	private synchronized int getUniqueCommandID() {
		uniqueCommandId += 1;
		return uniqueCommandId;
	}
}
