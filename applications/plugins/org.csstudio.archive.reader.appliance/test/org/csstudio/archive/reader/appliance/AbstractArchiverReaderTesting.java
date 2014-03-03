package org.csstudio.archive.reader.appliance;

import java.util.ArrayList;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;

/**
 * 
 * <code>AbstractArchiverReaderTesting</code> is the base class for the
 * archiver appliance reader test classes. It provides common methods
 * to retrieve different kinds of data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class AbstractArchiverReaderTesting {

	
	public static void main(String[] args) throws UnknownChannelException, Exception {
		ApplianceArchiveReader reader = new ApplianceArchiveReader("pbraw://192.168.12.131:17665/retrieval", false);
		ValueIterator it = reader.getRawValues(1, "waveform", Timestamp.now().minus(TimeDuration.ofMinutes(5)), Timestamp.now());
		it.next();
		System.out.println("21");
		
	}
	/**
	 * @return the reader used for loading the data
	 */
	protected abstract ArchiveReader getReader() throws Exception;
	
	
	/**
	 * Loads the data as statistics if applicable for the given pv name. This method will always 
	 * retrieve optimized data.
	 * 
	 * @param pvname the name of the provided PV
	 * @param count the number of requested points
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of statistical data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVType[] getValuesStatistics(String pvname, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, true, 100,start,end);
		ArrayList<ArchiveVType> vals = new ArrayList<ArchiveVType>();
		while(iterator.hasNext()) {
			vals.add((ArchiveVType)iterator.next());
		}
		iterator.close();
		return vals.toArray(new ArchiveVType[vals.size()]);
	}
	
	/**
	 * Loads the numerical array data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVNumber[] getValuesNumber(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		ArrayList<ArchiveVNumber> vals = new ArrayList<ArchiveVNumber>();
		while(iterator.hasNext()) {
			vals.add((ArchiveVNumber)iterator.next());
		}
		iterator.close();
		return vals.toArray(new ArchiveVNumber[vals.size()]);
	}
	
	/**
	 * Loads the numerical data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVNumberArray[] getValuesNumberArray(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		ArrayList<ArchiveVNumberArray> vals = new ArrayList<ArchiveVNumberArray>();
		while(iterator.hasNext()) {
			vals.add((ArchiveVNumberArray)iterator.next());
		}
		iterator.close();
		return vals.toArray(new ArchiveVNumberArray[vals.size()]);
	}
	
	/**
	 * Loads the string array data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVType[] getValuesStringArray(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		//exception should occur in the next line
		iterator.next();
		return null;
	}
	
	/**
	 * Loads string-type data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVString[] getValuesString(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		ArrayList<ArchiveVString> vals = new ArrayList<ArchiveVString>();
		while(iterator.hasNext()) {
			vals.add((ArchiveVString)iterator.next());
		}
		iterator.close();
		return vals.toArray(new ArchiveVString[vals.size()]);
		
	}
	
	/**
	 * Loads the enum array data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVType[] getValuesEnumArray(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		//exception should occur in the next line
		iterator.next();
		return null;
	}
	
	/**
	 * Loads enum-type data for the provided pv.
	 * 
	 * @param pvname the name of the PV to load data from
	 * @param optimized true if optimized data should be loaded or false otherwise
	 * @param count number of points to load in case optimized retrieval is selected
	 * @param start the start timestamp of the data
	 * @param end the end timestamp of the data
	 * @return the array of data
	 * @throws Exception in case of an error
	 */
	protected ArchiveVEnum[] getValuesEnum(String pvname, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception {
		ValueIterator iterator = getIterator(pvname, optimized, count,start,end);
		ArrayList<ArchiveVEnum> vals = new ArrayList<ArchiveVEnum>();
		while(iterator.hasNext()) {
			vals.add((ArchiveVEnum)iterator.next());
		}
		iterator.close();
		return vals.toArray(new ArchiveVEnum[vals.size()]);
	}
	
	private ValueIterator getIterator(String name, boolean optimized, int count, Timestamp start, Timestamp end) throws Exception{
		return optimized ? 
				getReader().getOptimizedValues(1, name, start, end,count) :
				getReader().getRawValues(1, name, start, end);
	}
	
	/**
	 * Determines alarm severity from the given numerical representation.
	 * 
	 * @param severity, numerical representation of alarm severity
	 * @return Alarm severity.
	 */
	protected AlarmSeverity getSeverity(int severity) {
	   if (severity == 0) {
		   return AlarmSeverity.NONE;
	   } else if (severity == 1) {
		   return AlarmSeverity.MINOR;
	   } else if (severity == 2) {
		   return AlarmSeverity.MAJOR;
	   } else if (severity == 3) {
		   return AlarmSeverity.INVALID;
	   } else {
		   return AlarmSeverity.UNDEFINED;
	   }
	}
}
