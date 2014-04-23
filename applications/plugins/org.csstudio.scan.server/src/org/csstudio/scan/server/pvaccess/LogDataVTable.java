package org.csstudio.scan.server.pvaccess;

import java.util.List;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.DataLogListener;
import org.epics.pvaccess.PVFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;

public class LogDataVTable implements DataLogListener{

	private  PVTopStructure pvTop;
	private static final PVDataCreate pvDataCreate = PVFactory.getPVDataCreate();
	
	public LogDataVTable( PVTopStructure pvTop) {
		super();
		this.pvTop = pvTop;
	}


	@Override
	public void logDataChanged(final DataLog datalog) {
		PVStructure pvField = pvTop.getPVStructure();

        	ScanData scanData;
			try {
				scanData = datalog.getScanData();
				
				PVStringArray labelsField =
						(PVStringArray)pvField.getScalarArrayField("labels", ScalarType.pvString);
					String[] labels;
					if (labelsField != null)
					{
						StringArrayData data = new StringArrayData();
						labelsField.get(0, labelsField.getLength(), data);
						data.data = scanData.getDevices();
						labels = data.data;
					}
					else
						labels = null;

				PVStructure valueField = pvField.getStructureField("value");
				if (valueField != null)
				{
					//if(scanData.getDevices().length != valueField.getPVFields().length)
						// check if there are new devices (columns)?
					for (PVField pvColumn : valueField.getPVFields())
			        {
						PVScalarArray scalarArray = (PVScalarArray)pvColumn;

			        	List<ScanSample> samples = scanData.getSamples(pvColumn.getFieldName());
			        	double[] colDouble = new double[samples.size()];
		        		int i = 0;
		        		for (ScanSample sample : samples){
		        			//different data types?
		        			colDouble[i]=ScanSampleFormatter.asDouble(sample);
		        			i++;
		        		}
		        		((PVDoubleArray)scalarArray).put(0, samples.size(), colDouble, 0);
						
			        }
			    }
					
	        	BitSet bitSet = new BitSet(pvField.getNumberFields());
	        	// refreshing whole structure, I need to learn to send just new data
	        	bitSet.set(0);
	        	pvTop.notifyListeners(bitSet);
        			
			} catch (Exception e) {

				e.printStackTrace();
			}

        	
	}

}
