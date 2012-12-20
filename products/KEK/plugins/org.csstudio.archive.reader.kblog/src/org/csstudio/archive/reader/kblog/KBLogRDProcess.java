package org.csstudio.archive.reader.kblog;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;

/**
 * An instance of this class represents one "kblogrd" process
 * 
 * @author Takashi Nakamoto
 */
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
	private int commandId;
	private boolean useAverage;
	private int stepSecond;
	private ITimestamp startTime;
	private ITimestamp endTime;
	private String kblogrdPath;
	
	/**
	 * Initialize the "kblogrd" command execution with given arguments.
	 * 
	 * @param kblogrdPath Path to "kblogrd" command.
	 * @param subArchiveName Sub archive name (e.g. BM/DCCT)
	 * @param name PV name (e.g. BM_DCCT:HCUR)
	 * @param start Start time of data sequence
	 * @param end End time of data sequence
	 * @param stepSecond Interval of each data. Set this to 0 or less if you want to obtain all raw data in the specified range.
	 * @param useAverage With this option, iterator for averaged values (and min/max values) will be returned when start() is called. (Note that this option will be ignored when stepSecond is 0 or less.)
	 */
	public KBLogRDProcess(String kblogrdPath, String subArchiveName, String name, ITimestamp start, ITimestamp end, int stepSecond, boolean useAverage) {
		this.kblogrdPath = kblogrdPath;
		String strStart = kblogrdTimeFormat.format(new Date(start.seconds() * 1000));
		String strEnd = kblogrdTimeFormat.format(new Date(end.seconds() * 1000));
		this.name = name;

		// 1. If stepSecond is positive, and useAverage is false,
		//     => Call "kblogrd" command with "d" option to suppress the amount of data that the command outputs.
		//        (Sampled raw values, fastest)
		//
		// 2. If stepSecond is 0 or negative,
		//     => Call "kblogrd" command without "d" option to obtain all data in the specified time range.
		//        (Raw values, second slowest)
		//
		// 3. If stepSecond is positive, and useAverage is true,
		//     => Call "kblogrd" command without "d" option to obtain all data in the specified time range,
		//        and calculate average/min/max values later when next() method of iterator is called.
		//        (Optimized for chart displaying, slowest)
		if (stepSecond > 0 && !useAverage)
			this.strCommand = kblogrdPath + " -r " + name + " -t " + strStart + "-" + strEnd + "d" + stepSecond + " -f " + kblogrdOutFormat + " " + subArchiveName;
		else
			this.strCommand = kblogrdPath + " -r " + name + " -t " + strStart + "-" + strEnd + " -f " + kblogrdOutFormat + " " + subArchiveName;

		this.iter = null;
		this.errHandler = null;
		this.started = false;
		this.proc = null;
		this.canceled = false;
		this.commandId = 0;
		this.useAverage = useAverage;
		this.stepSecond = stepSecond;
		this.startTime = start;
		this.endTime = end;
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
		
		commandId = getUniqueCommandID();
		Logger.getLogger(Activator.ID).log(Level.INFO, "Command " + commandId + ": " + strCommand);
		
		try {
			proc = Runtime.getRuntime().exec(strCommand);
			if (stepSecond > 0 && useAverage) {
				KBLogRawValueIterator baseIter = new KBLogRawValueIterator(proc.getInputStream(), name, kblogrdPath, commandId);
				iter = new KBLogAveragedValueIterator(baseIter, startTime, endTime, stepSecond);
			} else
				iter = new KBLogRawValueIterator(proc.getInputStream(), name, kblogrdPath, commandId);

			// Handle the error messages in a separate thread.
			errHandler = new KBLogErrorHandleThread(proc.getErrorStream(), kblogrdPath, commandId);
			errHandler.start();
		} catch (IOException e) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE, "Failed to run " + kblogrdPath + " (" + commandId + ").");
		}
		
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
		Logger.getLogger(Activator.ID).log(Level.FINE, "KBLogRDProcess.cancel() is requested for " + kblogrdPath + " (" + commandId + ").");
		
		if (isFinished())
			return;
		
		// Close the standard output.
		if (iter != null && !iter.isClosed())
			iter.close();

		// Close the standard error.
		if (errHandler != null && errHandler.isClosed())
			errHandler.close();
		
		if (proc != null) {
			// Close the standard input.
			try{
				OutputStream outStream = proc.getOutputStream();
				if (outStream != null)
					outStream.close();
			} catch (IOException ex) {
				Logger.getLogger(Activator.ID).log(Level.SEVERE,
						"Failed to close the standard input of " + kblogrdPath + " (" + commandId + ").");
			}
			
			// Destroy the running process.
			proc.destroy();
		}
		
		canceled = true;
	}

	/**
	 * This must be called every time "kblogrd" command is called.
	 * 
	 * @return Command ID unique in this class.
	 */
	private synchronized int getUniqueCommandID() {
		uniqueCommandId += 1;
		return uniqueCommandId;
	}
}
