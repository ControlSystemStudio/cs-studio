package org.csstudio.archive.reader.kblog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/**
 * ValueIterator that reads data from kblogrd via the standard output.
 * 
 * @author Takashi Nakamoto
 */
public class KBLogRawValueIterator implements KBLogValueIterator {
	private static final String charset = "US-ASCII";
	
	private IValue nextValue;
	private Object nextValueMutex; 
	
	private String pvName;
	private int commandId;
	private String kblogrdPath;
	
	private DateFormat timeFormat;
	
	private BufferedReader stdoutReader;
	private boolean closed;
	private Object closedMutex;
	private boolean initialized;
	private Object initializedMutex;
	
	/**
	 * Constructor of KBLogRawValueIterator.
	 * 
	 * @param kblogrdStdOut InputStream obtained from the standard output of "kblogrd".
	 * @param name PVName
	 * @param kblogrdPath Path to "kblogrd" command.
	 * @param commandId Unique ID of executed "kblogrd" command.
	 */
	KBLogRawValueIterator(InputStream kblogrdStdOut, String name, String kblogrdPath, int commandId) {
		this.pvName = name;
		this.kblogrdPath = kblogrdPath;
		this.commandId = commandId;
		this.closed = false;
		this.closedMutex = new Object();
		this.initialized = false;
		this.initializedMutex = new Object();
		this.nextValue = null;
		this.nextValueMutex = new Object();
		this.timeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.US);
		
		Logger.getLogger(Activator.ID).log(Level.FINE,
				"Start to read the standard output of " + kblogrdPath + " (" + commandId + ").");
		
		try {
			stdoutReader = new BufferedReader(new InputStreamReader(kblogrdStdOut, charset));
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING,
					"Character set " + charset + " is not supported in this platform. System default charset will be used as a fallback.");
			
			stdoutReader = new BufferedReader(new InputStreamReader(kblogrdStdOut));
		}
	}
	
	/**
	 * Parse time stamp of the output from kblogrd in 'free' format.
	 * 
	 * @param str String to parse.
	 * @return Obtained time stamp.
	 */
	private ITimestamp parseTimestamp(String str) {
		// Append "0" at the end so that the last part of string represents millisecond. 
		String strTime = str + "0";
		
		try {
			Date date;
			synchronized (timeFormat) {
				date = timeFormat.parse(strTime);
			}
			long msFromEpoch = date.getTime();
			long secFromEpoch = (long) Math.floor((double)msFromEpoch / 1000.0);
			long msInSecond = msFromEpoch - secFromEpoch * 1000;
			return TimestampFactory.createTimestamp(secFromEpoch, msInSecond*1000*1000);
		} catch (ParseException ex) {
			return null;
		}
	}

	private IValue decodeNextValue() {
		synchronized (closedMutex) {
			if (closed)
				return null;
		}
		
		try{
			String line;
			
			// Try to read lines until a valid value is obtained.
			while ((line = stdoutReader.readLine()) != null) {
				if (line.isEmpty())
					continue;
			
				int firstTab = line.indexOf("\t");
				int secondTab = line.indexOf("\t", firstTab+1);
				if (firstTab == -1 || secondTab == -1) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Invalid line in " + kblogrdPath + " (" + commandId + ") output: " + line);
					continue;
				}

				String strTime = line.substring(0, firstTab);
				String strName = line.substring(firstTab+1, secondTab);
				String strValue = line.substring(secondTab+1);
				if (strTime.isEmpty() || strName.isEmpty() || strValue.isEmpty()) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Invalid line in " + kblogrdPath + " (" + commandId + ") output: " + line);
					continue;
				}
				
				// Parse time stamp.
				ITimestamp time = parseTimestamp(strTime);
				if (time == null) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Invalid timestamp in " + kblogrdPath + " (" + commandId + ") output: " + strTime);
					continue;
				}
				
				// Check the PV name just in case.
				if (!pvName.equals(strName)) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Unexpected values of '" + strName + "' were obtained while reading values of '" + pvName + "' via " + kblogrdPath + " (" + commandId +").");
					continue;
				}
				
				// Parse double value.
				try {
					if (strValue.indexOf("\t") == -1) {
						// scalar value
						
						boolean integer = false;
						double doubleValue = 0;
						long longValue = 0;
						ISeverity severity = ValueFactory.createOKSeverity();
						String status = "";
						
						if (strValue.equals("Connected")) {
							doubleValue = 0;
							status = KBLogMessages.StatusConnected;
							severity = KBLogSeverityInstances.connected;
						} else if (strValue.equals("Disconnected")) {
							doubleValue = 0;
							status = KBLogMessages.StatusDisconnected;
							severity = KBLogSeverityInstances.disconnected;
						} else if (strValue.equals("INF")) {
							// TODO this part is not tested
							doubleValue = Double.POSITIVE_INFINITY;
							status = KBLogMessages.StatusNormal;
							severity = KBLogSeverityInstances.normal;
						} else if (strValue.equals("-INF")) {
							// TODO this part is not tested
							doubleValue = Double.NEGATIVE_INFINITY;
							status = KBLogMessages.StatusNormal;
							severity = KBLogSeverityInstances.normal;
						} else if (strValue.equals("NaN")) {
							// TODO this part is not tested.
							doubleValue = Double.NaN;
							status = KBLogMessages.StatusNaN;
							severity = KBLogSeverityInstances.nan;
						} else if (strValue.indexOf('.') >= 0) {
							doubleValue = Double.parseDouble(strValue);
							status = KBLogMessages.StatusNormal;
							severity = KBLogSeverityInstances.normal;
						} else {
							integer = true;
							longValue = Long.parseLong(strValue);
							status = KBLogMessages.StatusNormal;
							severity = KBLogSeverityInstances.normal;
						}
	
						if (integer) {
							return ValueFactory.createLongValue(time,
									severity,
									status,
									null,
									Quality.Original,
									new long[]{longValue});
						} else {
							return ValueFactory.createDoubleValue(time,
									severity,
									status,
									null,
									Quality.Original,
									new double[]{doubleValue});
						}
					} else {
						// array
						boolean integer = true;
						String[] strElements = strValue.split("\t");
						
						// if there is one double value in the array, the array will
						// be a double array. Otherwise, it will be a long array.
						for (String strElement : strElements) {
							if (strElement.indexOf('.') >= 0) {
								integer = false;
								break;
							}
						}
						
						if (integer) {
							long[] longArray = new long[strElements.length];
							for (int i=0; i<strElements.length; i++) { 
								longArray[i] = Long.parseLong(strElements[i]);
							}
							return ValueFactory.createLongValue(time,
									KBLogSeverityInstances.normal,
									KBLogMessages.StatusNormal,
									null,
									Quality.Original,
									longArray);
						} else {
							double[] doubleArray = new double[strElements.length];
							for (int i=0; i<strElements.length; i++) { 
								doubleArray[i] = Double.parseDouble(strElements[i]);
							}
							return ValueFactory.createDoubleValue(time,
									KBLogSeverityInstances.normal,
									KBLogMessages.StatusNormal,
									null,
									Quality.Original,
									doubleArray);
						}
					}
				} catch (NumberFormatException ex) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Failed to parse numeric value obtained from " + kblogrdPath + " (" + commandId + "): " + strValue, ex);
					continue;
				}
			}
			
			// No more value.
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE,
					"Failed to read the output from " + kblogrdPath + " (" + commandId + ").", ex);
			return null;
		}
	}

	@Override
	public boolean hasNext() {
		synchronized (initializedMutex) {
			if (!initialized) {
				nextValue = decodeNextValue();
				initialized = true;
			}
		}
		
		synchronized (nextValueMutex) {
			return nextValue != null;
		}
	}

	@Override
	public IValue next() throws Exception {
		synchronized (initializedMutex) {
			if (!initialized) {
				nextValue = decodeNextValue();
				initialized = true;
			}
		}
		
		synchronized (nextValueMutex) {
			IValue ret = nextValue;
			nextValue = decodeNextValue();
			
			return ret;
		}
	}

	@Override
	public void close() {
		try {
			synchronized (closedMutex) {
				if (closed)
					return;
				
				// The standard output will be forcibly closed so that next call of the next() method
				// will return null and quits the data acquisition.
				stdoutReader.close();
				closed = true;
			}
			
			Logger.getLogger(Activator.ID).log(Level.FINEST,
					"End of reading the standard output of " + kblogrdPath + " (" + commandId + ").");
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE, 
					"An error occured while closing the pipe to stdout of " + kblogrdPath + " (" + commandId + ").", ex);
		}
	}
	
	@Override
	public boolean isClosed() {
		synchronized (closedMutex) {
			return closed;
		}
	}
	
	public int getCommandID() {
		return commandId;
	}
	
	public String getPathToKBLogRD() {
		return kblogrdPath;
	}
}
