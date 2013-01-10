package org.csstudio.archive.reader.kblog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This thread transfers error messages from kblogrd to the standard logger.
 * 
 * @author Takashi Nakamoto
 */
public class KBLogErrorHandleThread extends Thread {
	private static final String charset = "US-ASCII";
	
	private BufferedReader stderrReader;
	private String kblogrdPath;
	private int commandId;
	
	private boolean closed;
	
	/**
	 * Constructor of KBLogErrorHandleThread.
	 * 
	 * @param kblogrdStdErr InputStream of standard error for kblogrd.
	 * @param kblogrdPath Path to "kblogrd" command.
	 * @param commandId Command ID of kblogrd.
	 */
	KBLogErrorHandleThread(InputStream kblogrdStdErr, String kblogrdPath, int commandId) {
		this.kblogrdPath = kblogrdPath;
		
		Logger.getLogger(Activator.ID).log(Level.FINEST,
				"Start to read the standard error of " + kblogrdPath + " (" + commandId + ").");

		try {
			stderrReader = new BufferedReader(new InputStreamReader(kblogrdStdErr, charset));
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING,
					"Character set " + charset + " is not supported in this platform. System default charset will be used as a fallback.");
			
			stderrReader = new BufferedReader(new InputStreamReader(kblogrdStdErr));
		}
		
		this.commandId = commandId;
		this.closed = false;
	}
	
	public void run() {
		try {
			String line;
			
			// Transfer the messages in the standard error to the CSS logging system.
			while ((line = stderrReader.readLine()) != null) {
				Logger.getLogger(Activator.ID).log(Level.FINE,
						"Error message from " + kblogrdPath + " (" + commandId + "): " + line);
			}
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING,
					"IOException while reading standard error of " + kblogrdPath + " (" + commandId + ")", ex);
		}
		
		// Close the standard error.
		close();
	}
	
	public synchronized void close() {
		if (closed)
			return;
		
		try{
			stderrReader.close();
			closed = true;
			
			Logger.getLogger(Activator.ID).log(Level.FINEST,
					"End of reading the standard error of " + kblogrdPath + " (" + commandId + ").");
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE,
					"Failed to close the standard error off " + kblogrdPath + " (" + commandId + ")", ex);
		}
	}
	
	public synchronized boolean isClosed() {
		return closed;
	}
}
