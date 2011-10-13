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

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.IValue.Quality;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/**
 * ValueIterator that read data from kblogrd via the standard output.
 * 
 * @author Takashi Nakamoto
 */
public class KBLogValueIterator implements ValueIterator {
	private static final String charset = "US-ASCII";
	private static DateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS", Locale.US);
	
	private IValue nextValue = null;
	private String pvName;
	
	private BufferedReader stdoutReader;
	
	/**
	 * 
	 * @param kblogrdStdOut
	 */
	KBLogValueIterator(InputStream kblogrdStdOut, String name) {
		try {
			stdoutReader = new BufferedReader(new InputStreamReader(kblogrdStdOut, charset));
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING,
					"Character set " + charset + " is not supported in this platform. System default charset will be used as a fallback.");
			
			stdoutReader = new BufferedReader(new InputStreamReader(kblogrdStdOut));
		}
		
		pvName = name;
		nextValue = decodeNextValue();
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
			Date date = timeFormat.parse(strTime);
			long msFromEpoch = date.getTime();
			long secFromEpoch = (long) Math.floor((double)msFromEpoch / 1000.0);
			long msInSecond = msFromEpoch - secFromEpoch * 1000;
			return TimestampFactory.createTimestamp(secFromEpoch, msInSecond*1000*1000);
		} catch (ParseException ex) {
			return null;
		}
	}

	private synchronized IValue decodeNextValue() {
		// TODO throw exception when time out
		
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
							"Invalid line in kblogrd output: " + line);
					continue;
				}

				String strTime = line.substring(0, firstTab);
				String strName = line.substring(firstTab+1, secondTab);
				String strValue = line.substring(secondTab+1);
				if (strTime.isEmpty() || strName.isEmpty() || strValue.isEmpty()) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Invalid line in kblogrd output: " + line);
					continue;
				}
				
				// Parse time stamp.
				ITimestamp time = parseTimestamp(strTime);
				if (time == null) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Invalid format of timestamp: " + strTime);
					continue;
				}
				
				// Check the PV name just in case.
				if (!pvName.equals(strName)) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Unexpected values of '" + strName + "' were obtained while reading values of '" + pvName + "'.");
					continue;
				}

				// Parse double value.
				try {
					double value = Double.parseDouble(strValue);
					
					// TODO need to be reconsidered the arguments of this value (e.g. Severity, Status, Metadata).
					return ValueFactory.createDoubleValue(time,
							ValueFactory.createOKSeverity(),
							"OK",
							null,
							Quality.Original,
							new double[]{value});
				} catch (NumberFormatException ex) {
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"Failed to parse double value: " + strValue, ex);
					continue;
				}
				
				// TODO Support String, Long and Enum.
			}
			
			// No more value.
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING,
					"Failed to read the output from kblogrd.", ex);
			return null;
		}
	}

	@Override
	public synchronized boolean hasNext() {
		return nextValue != null;
	}

	@Override
	public synchronized IValue next() throws Exception {
		IValue ret = nextValue;
		nextValue = decodeNextValue();

		return ret;
	}

	@Override
	public synchronized void close() {
		try {
			stdoutReader.close();
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, 
					"An error occured while closing the pipe to stdout of kblogrd.", ex);
		}
	}
}
