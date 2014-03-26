package org.csstudio.archive.reader.appliance;

import java.io.IOException;
import java.util.Iterator;

import org.csstudio.archive.vtype.TimestampHelper;
import org.epics.archiverappliance.retrieval.client.DataRetrieval;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.ValueFactory;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;

/**
 * 
 * <code>ApplianceMeanValueIterator</code> retrieves the mean value of the archived data bins.
 * The size of the bin is specified by the selected time range and the requested number of points.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceMeanValueIterator extends ApplianceValueIterator {
	
    protected final int requestedPoints;
    
	/**
	 * Constructor that fetches data from appliance archive reader.
	 * 
	 * @param reader instance of appliance archive reader
	 * @param name name of the PV
	 * @param start start of the time period
	 * @param end end of the time period
	 * @param points the number of requested points
	 * 
	 * @throws IOException if there was an error during the data fetch process
	 * @throws ArchiverApplianceException if it is not possible to load optimized data for the selected PV
	 */
	public ApplianceMeanValueIterator(ApplianceArchiveReader reader,
			String name, Timestamp start, Timestamp end, int points) throws ArchiverApplianceException, IOException {
		super(reader,name,start,end);
		this.requestedPoints = points;
		this.display = determineDisplay(reader, name, end);
		fetchData();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.archive.reader.appliance.ApplianceValueIterator#fetchDataInternal(java.lang.String)
	 */
	@Override
	protected void fetchDataInternal(String pvName) throws ArchiverApplianceException {
		int interval = Math.max(1,(int)((end.getSec() - start.getSec()) / requestedPoints));
		String mean = new StringBuilder().append("mean_").append(interval).append('(').append(name).append(')').toString();
		super.fetchDataInternal(mean);
	}
		
	/**
	 * Determine and return display values.
	 * 
	 * @param reader instance of appliance archive reader
	 * @param name name of the PV
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
	 * Check if the type of data is OK to be loaded in mean mode.
	 * Mean mode is possible only with numeric scalars. 
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