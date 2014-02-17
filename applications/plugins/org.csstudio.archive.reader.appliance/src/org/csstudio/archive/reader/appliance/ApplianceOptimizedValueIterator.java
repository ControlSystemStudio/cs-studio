package org.csstudio.archive.reader.appliance;

import java.io.IOException;
import java.util.Iterator;

import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.archiverappliance.retrieval.client.DataRetrieval;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;

/**
 * 
 * <code>ApplianceOptimizedValueIterator</code> is the value iterator for the
 * optimized data retrieval. This iterator returns the mean value or the statistics type
 * if requested so.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceOptimizedValueIterator extends ApplianceValueIterator {
	
    private final int requestedPoints;
    private GenMsgIterator stdStream;
    private Iterator<EpicsMessage> stdIterator;
    private final boolean useStatistics;
    
	/**
	 * Constructor that fetches data from appliance archive reader.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV
	 * @param start, start of the time period
	 * @param end, end of the time period
	 * @param count the number of requested points
	 * @param useStatistics if true the iterator will return the statistics type, if false it will
	 * 			return the mean type as the numeric value
	 * 
	 * @throws IOException if there was an error during the data fetch process
	 * @throws ArchiverApplianceException if it is not possible to load optimized data for the selected PV
	 */
	public ApplianceOptimizedValueIterator(ApplianceArchiveReader reader,
			String name, Timestamp start, Timestamp end, int count, boolean useStatistics) throws ArchiverApplianceException, IOException {
		this.requestedPoints = count;
		this.useStatistics = useStatistics;
		this.display = determineDisplay(reader, name, end);
		fetchData(reader, name, start, end);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.archive.reader.appliance.ApplianceValueIterator#fetchData(org.csstudio.archive.reader.appliance.ApplianceArchiveReader, java.lang.String, org.epics.util.time.Timestamp, org.epics.util.time.Timestamp)
	 */
	@Override
	protected void fetchData(ApplianceArchiveReader reader, String name, Timestamp start, Timestamp end) throws ArchiverApplianceException {
		int interval = Math.max(1,(int)((end.getSec() - start.getSec()) / requestedPoints));
		String mean = new StringBuilder().append("mean_").append(interval).append('(').append(name).append(')').toString();
		super.fetchData(reader, mean, start, end);
		
		if (useStatistics) {
			String std = new StringBuilder().append("std_").append(interval).append('(').append(name).append(')').toString();
			
			java.sql.Timestamp sqlStartTimestamp = TimestampHelper.toSQLTimestamp(start);
			java.sql.Timestamp sqlEndTimestamp = TimestampHelper.toSQLTimestamp(end);
			DataRetrieval dataRetrieval = reader.createDataRetriveal(reader.getDataRetrievalURL());
			stdStream = dataRetrieval.getDataForPV(std, sqlStartTimestamp, sqlEndTimestamp);
			if (stdStream != null) { 
				stdIterator = stdStream.iterator();
			} else {
				throw new ArchiverApplianceException("Could not fetch data.");
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#next()
	 */
	@Override
	public VType next() throws Exception {
		if (useStatistics) {
	        EpicsMessage meanResult = mainIterator.next();
	        EpicsMessage stdResult = stdIterator.next();
	        PayloadType type = mainStream.getPayLoadInfo().getType();
	        if (type == PayloadType.SCALAR_BYTE || 
	        		type == PayloadType.SCALAR_DOUBLE ||
	        		type == PayloadType.SCALAR_FLOAT ||
	        		type == PayloadType.SCALAR_INT ||
	        		type == PayloadType.SCALAR_SHORT) {
	        	double mean = meanResult.getNumberValue().doubleValue();
	        	double std = stdResult.getNumberValue().doubleValue();
				return new ArchiveVStatistics(
						TimestampHelper.fromSQLTimestamp(meanResult.getTimestamp()),
						getSeverity(meanResult.getSeverity()), 
						String.valueOf(meanResult.getStatus()), 
						display, 
						mean-std*1.5,
						mean+std*1.5,
						Double.NaN,
						std,
						100);
	        } 
	        throw new UnsupportedOperationException("PV type " + type + " is not supported.");
		} else {
			return super.next();
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ValueIterator#close()
	 */
	@Override
	public void close() {
		super.close();
		try {
			if(stdStream != null) {
				stdStream.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Determine and return display values.
	 * 
	 * @param reader, instance of appliance archive reader
	 * @param name, name of the PV
	 * 
	 * @return the display
	 * @throws IOException if there was an error reading data
	 * @throws ArchiverApplianceException if the data cannot be loaded with the optimized method
	 */
	private Display determineDisplay(ApplianceArchiveReader reader, String name, Timestamp time) throws ArchiverApplianceException,IOException {
		//to retrieve the display, request the raw data for the end timestamp
		java.sql.Timestamp timestamp = TimestampHelper.toSQLTimestamp(time);
		DataRetrieval dataRetrieval = reader.createDataRetriveal(reader.getDataRetrievalURL());
		GenMsgIterator genMsgIterator = dataRetrieval.getDataForPV(name, timestamp, timestamp);
		if (genMsgIterator != null) {
			try {
				PayloadInfo payloadInfo = null;
				Iterator<EpicsMessage> it = genMsgIterator.iterator();
				if(it.hasNext()) {
					it.next();
					payloadInfo = genMsgIterator.getPayLoadInfo();
					if (!isDataTypeOKForOptimized(payloadInfo.getType())) {
						throw new ArchiverApplianceException("Cannot use optimized data on type " + payloadInfo.getType());
					}
					return getDisplay(payloadInfo);
				}
			} finally {
				genMsgIterator.close();
			}
		}
		
		return ValueFactory.newDisplay(Double.NaN, Double.NaN, Double.NaN, "", 
				NumberFormats.toStringFormat(),	Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
	}
	
	/**
	 * Check if the type of data is OK to be loaded in optimized mode.
	 * Optimized mode is possible only with numeric scalars. 
	 * 
	 * @param type the type to check
	 * @return true if OK or false otherwise
	 */
	private boolean isDataTypeOKForOptimized(PayloadType type) {
		return type == PayloadType.SCALAR_BYTE ||
				type == PayloadType.SCALAR_DOUBLE ||
				type == PayloadType.SCALAR_FLOAT ||
				type == PayloadType.SCALAR_INT ||
				type == PayloadType.SCALAR_SHORT;
	}
}