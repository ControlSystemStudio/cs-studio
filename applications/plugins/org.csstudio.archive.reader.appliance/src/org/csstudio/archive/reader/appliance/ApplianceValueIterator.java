package org.csstudio.archive.reader.appliance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.archiverappliance.retrieval.client.DataRetrieval;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.FieldValue;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;

/**
 * Raw appliance value iterator is an iterator which iterates through specific PV
 * values. It uses the pbrawclient to retrieve the data.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceValueIterator implements ValueIterator {
	
	private GenMsgIterator strm;
    private final Display display;
    private Iterator<EpicsMessage> iterator;
    
	/**
	 * Constructor that fetches data from appliance archive reader.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV
	 * @param start, start of the time period
	 * @param end, end of the time period
	 * @throws IOException if there was an error during the data fetch process
	 */
	public ApplianceValueIterator(ApplianceArchiveReader reader,
			String name, Timestamp start, Timestamp end, boolean optimized) throws IOException {
		if (optimized) {
			//in case of mean retrieval the limits are not returned. 
			//Loading them separately
			String pvName = name.substring(name.indexOf('(') + 1, name.indexOf(')'));
			display = determineDisplay(reader, pvName, end);
		} else {
			display = null;
		}
		
		fetchData(reader, name, start, end);
	}
	
	/**
	 * Fetches data from appliance archiver reader.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV
	 * @param start, start of the time period
	 * @param end, end of the time period
	 * @throws IOException if there was an error reading data
	 */
	private void fetchData(ApplianceArchiveReader reader, String name, Timestamp start, Timestamp end) throws IOException{				
		java.sql.Timestamp sqlStartTimestamp = TimestampHelper.toSQLTimestamp(start);
		java.sql.Timestamp sqlEndTimestamp = TimestampHelper.toSQLTimestamp(end);
		
		DataRetrieval dataRetrieval = reader.createDataRetriveal(reader.getDataRetrievalURL());
		strm = dataRetrieval.getDataForPV(name, sqlStartTimestamp, sqlEndTimestamp);
		if (strm != null) { 
			iterator = strm.iterator();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#next()
	 */
	@Override
	public VType next() throws Exception {
        EpicsMessage result = iterator.next();
		return new ArchiveVNumber(
				TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
				getSeverity(result.getSeverity()), 
				String.valueOf(result.getStatus()), 
				display == null ? getDisplay(strm.getPayLoadInfo()) : display, 
				result.getNumberValue().intValue());
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#close()
	 */
	@Override
	public void close() {
		try {
			if(strm != null) {
				strm.close();
				strm = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Determine and return display values.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV
	 * @return the display
	 * @throws IOException if there was an error reading data
	 */
	private Display determineDisplay(ApplianceArchiveReader reader, String name, Timestamp time) throws IOException {
		//to retrieve the display, request future data
		java.sql.Timestamp timestamp = TimestampHelper.toSQLTimestamp(time);
		
		Map<String, String> headers = new HashMap<String, String>();
		DataRetrieval dataRetrieval = reader.createDataRetriveal(reader.getDataRetrievalURL());
		GenMsgIterator genMsgIterator = dataRetrieval.getDataForPV(name, timestamp, timestamp);
		if (genMsgIterator != null) {
			try {
				PayloadInfo payloadInfo = null;
				Iterator<EpicsMessage> it = genMsgIterator.iterator();
				while(it.hasNext()) {
					it.next();
					payloadInfo = genMsgIterator.getPayLoadInfo();
					for (FieldValue  fieldValue : payloadInfo.getHeadersList()) {
						if (!headers.containsKey(fieldValue.getName())) {
							headers.put(fieldValue.getName(), fieldValue.getVal());
						}
					}
				}
			} finally {
				genMsgIterator.close();
			}
		}
		
		String lopr = headers.get(ApplianceArchiveReaderConstants.LOPR);
		String low = headers.get(ApplianceArchiveReaderConstants.LOW);
		String lolo = headers.get(ApplianceArchiveReaderConstants.LOLO);
		String egu = headers.get(ApplianceArchiveReaderConstants.EGU);
		String prec = headers.get(ApplianceArchiveReaderConstants.PREC);
		String high = headers.get(ApplianceArchiveReaderConstants.HIGH);
		String hihi = headers.get(ApplianceArchiveReaderConstants.HIHI);
		String hopr = headers.get(ApplianceArchiveReaderConstants.HOPR);
		return ValueFactory.newDisplay(
				(lopr != null) ? Double.parseDouble(lopr) : Double.NaN, 
				(low != null) ? Double.parseDouble(low) : Double.NaN, 
				(lolo != null) ? Double.parseDouble(lolo) : Double.NaN, 
				(egu != null) ? egu : "", 
				(prec != null) ? NumberFormats.format(Integer.parseInt(prec)) : 
					NumberFormats.toStringFormat(), 
				(high != null) ? Double.parseDouble(high) : Double.NaN, 
				(hihi != null) ? Double.parseDouble(hihi) : Double.NaN, 
				(hopr != null) ? Double.parseDouble(hopr) : Double.NaN, 
				(lopr != null) ? Double.parseDouble(lopr) : Double.NaN, 
				(hopr != null) ? Double.parseDouble(hopr) : Double.NaN
		);
	}
	
	/**
	 * Extract the limits from the given payloadinfo. 
	 * @param info the info to extract the limits from
	 * @return the display
	 */
	private Display getDisplay(PayloadInfo info) {
		Map<String, String> headers = new HashMap<String, String>();
		for (FieldValue fieldValue : info.getHeadersList()) {
			if (!headers.containsKey(fieldValue.getName())) {
				headers.put(fieldValue.getName(), fieldValue.getVal());
			}
		}
		
		String lopr = headers.get(ApplianceArchiveReaderConstants.LOPR);
		String low = headers.get(ApplianceArchiveReaderConstants.LOW);
		String lolo = headers.get(ApplianceArchiveReaderConstants.LOLO);
		String egu = headers.get(ApplianceArchiveReaderConstants.EGU);
		String prec = headers.get(ApplianceArchiveReaderConstants.PREC);
		String high = headers.get(ApplianceArchiveReaderConstants.HIGH);
		String hihi = headers.get(ApplianceArchiveReaderConstants.HIHI);
		String hopr = headers.get(ApplianceArchiveReaderConstants.HOPR);
		return ValueFactory.newDisplay(
				(lopr != null) ? Double.parseDouble(lopr) : Double.NaN, 
				(low != null) ? Double.parseDouble(low) : Double.NaN, 
				(lolo != null) ? Double.parseDouble(lolo) : Double.NaN, 
				(egu != null) ? egu : "", 
				(prec != null) ? NumberFormats.format(Integer.parseInt(prec)) : 
					NumberFormats.toStringFormat(), 
				(high != null) ? Double.parseDouble(high) : Double.NaN, 
				(hihi != null) ? Double.parseDouble(hihi) : Double.NaN, 
				(hopr != null) ? Double.parseDouble(hopr) : Double.NaN, 
				(lopr != null) ? Double.parseDouble(lopr) : Double.NaN, 
				(hopr != null) ? Double.parseDouble(hopr) : Double.NaN
		);
	}
			
	
	/**
	 * Determines alarm severity from given numerical representation.
	 * 
	 * @param severity, numerical representation of alarm severity
	 * @return Alarm severity.
	 */
	private AlarmSeverity getSeverity(int severity) {
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