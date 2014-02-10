package org.csstudio.archive.reader.appliance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
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

import com.google.protobuf.Descriptors.FieldDescriptor;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.FieldValue;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;

/**
 * 
 * <code>ApplianceValueIterator</code> is the base class for different value iterators.
 * It provides the facilities to extract the common values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class ApplianceValueIterator implements ValueIterator {
	
    protected Display display;
    protected GenMsgIterator mainStream;
    protected Iterator<EpicsMessage> mainIterator;
    private FieldDescriptor valDescriptor;
    	
    
	/**
	 * Fetches data from appliance archiver reader.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV as used in the request made to the server
	 * @param start, start of the time period
	 * @param end, end of the time period
	 * 
	 * @throws ArchiverApplianceException if the data for the pv could not be loaded
	 */
	protected void fetchData(ApplianceArchiveReader reader, String name, Timestamp start, Timestamp end) throws ArchiverApplianceException {				
		java.sql.Timestamp sqlStartTimestamp = TimestampHelper.toSQLTimestamp(start);
		java.sql.Timestamp sqlEndTimestamp = TimestampHelper.toSQLTimestamp(end);
		
		DataRetrieval dataRetrieval = reader.createDataRetriveal(reader.getDataRetrievalURL());
		mainStream = dataRetrieval.getDataForPV(name, sqlStartTimestamp, sqlEndTimestamp);
		if (mainStream != null) { 
			mainIterator = mainStream.iterator();
		} else {
			throw new ArchiverApplianceException("Could not fetch data.");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return mainIterator != null && mainIterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#next()
	 */
	@Override
	public VType next() throws Exception {
        EpicsMessage result = mainIterator.next();
        PayloadType type = mainStream.getPayLoadInfo().getType();
        if (type == PayloadType.SCALAR_BYTE || 
        		type == PayloadType.SCALAR_DOUBLE ||
        		type == PayloadType.SCALAR_FLOAT ||
        		type == PayloadType.SCALAR_INT ||
        		type == PayloadType.SCALAR_SHORT) {
			return new ArchiveVNumber(
					TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
					getSeverity(result.getSeverity()), 
					String.valueOf(result.getStatus()), 
					display == null ? getDisplay(mainStream.getPayLoadInfo()) : display, 
					result.getNumberValue());
        } else if (type == PayloadType.SCALAR_ENUM) {
        	return new ArchiveVEnum(
					TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
					getSeverity(result.getSeverity()), 
					String.valueOf(result.getStatus()), 
					 null, //TODO get the labels from somewhere
					result.getNumberValue().intValue());
        } else if (type == PayloadType.SCALAR_STRING) {
        	if (valDescriptor == null) {
        		valDescriptor = getValDescriptor(result);
        	}        	
        	return new ArchiveVString(
					TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
					getSeverity(result.getSeverity()), 
					String.valueOf(result.getStatus()), 
					String.valueOf(result.getMessage().getField(valDescriptor)));
        } else if (type == PayloadType.WAVEFORM_DOUBLE
        		|| type == PayloadType.WAVEFORM_FLOAT){
        	if (valDescriptor == null) {
        		valDescriptor = getValDescriptor(result);
        	}
        	List<?> o = (List<?>)result.getMessage().getField(valDescriptor);
        	double[] val = new double[o.size()];
        	if (type == PayloadType.WAVEFORM_DOUBLE) {
	        	for (int i = 0; i < val.length; i++) {
	        		val[i] = (Double)o.get(i);
	        	}
        	} else {
        		for (int i = 0; i < val.length; i++) {
            		val[i] = (Float)o.get(i);
            	}
        	}
        	return new ArchiveVNumberArray(
        			TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
					getSeverity(result.getSeverity()), 
					String.valueOf(result.getStatus()), 
					display == null ? getDisplay(mainStream.getPayLoadInfo()) : display, 
					val);
        } else if (type == PayloadType.WAVEFORM_BYTE 
        		|| type == PayloadType.WAVEFORM_SHORT 
        		|| type == PayloadType.WAVEFORM_INT) {
        	if (valDescriptor == null) {
        		valDescriptor = getValDescriptor(result);
        	}
        	List<?> o = (List<?>)result.getMessage().getField(valDescriptor);
        	int[] val = new int[o.size()];
        	if (type == PayloadType.WAVEFORM_INT) {
	        	for (int i = 0; i < val.length; i++) {
	        		val[i] = (Integer)o.get(i);
	        	}
        	} else if (type == PayloadType.WAVEFORM_SHORT) {
        		for (int i = 0; i < val.length; i++) {
            		val[i] = (Short)o.get(i);
            	}
        	} else {
        		for (int i = 0; i < val.length; i++) {
            		val[i] = (Byte)o.get(i);
            	}
        	}
        	return new ArchiveVNumberArray(
        			TimestampHelper.fromSQLTimestamp(result.getTimestamp()),
					getSeverity(result.getSeverity()), 
					String.valueOf(result.getStatus()), 
					display == null ? getDisplay(mainStream.getPayLoadInfo()) : display, 
					val);
        }
        throw new UnsupportedOperationException("PV type " + type + " is not supported.");
	}
	
	/**
	 * Extracts the descriptor for the value field so it can be reused on each iteration.
	 * 
	 * @param message the epics message to extract the descriptor from
	 * @return the descriptor if it was found or null if not found
	 */
	private FieldDescriptor getValDescriptor(EpicsMessage message) {
		Iterator<FieldDescriptor> it = message.getMessage().getAllFields().keySet().iterator();
		FieldDescriptor fd;
		while(it.hasNext()) {
			fd = it.next();
			if (fd.getName().equalsIgnoreCase("val")) {
				return fd;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#close()
	 */
	@Override
	public void close() {
		try {
			if(mainStream != null) {
				mainStream.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
			
	/**
	 * Extract the display properties (min, max, alarm limits) from the given payloadinfo.
	 *  
	 * @param info the info to extract the limits from
	 * @return the display
	 */
	protected Display getDisplay(PayloadInfo info) {
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