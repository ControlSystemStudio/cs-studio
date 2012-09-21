package org.csstudio.opibuilder.pvmanager;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.util.time.TimeDuration.ofMillis;
import static org.junit.Assert.fail;

import java.util.List;

import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.pvmanager.jca.JCADataSource;
import org.epics.pvmanager.sim.SimulationDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PVManagerTest {

	private static int updates = 0;
	
	String pvName = //"css:sine";
			"css:count";
			//"css:setpoint";
			//"Ring_Diag:VFM:image"; 
			//"Ring_IDmp:Foil_Plunge:Psn";
			//"css:sensor";
			//"sim://noise";
			//"CG1D:Cam:Cam1:AcquireTime";
	@BeforeClass
	public static void setUp() throws Exception {

		updates = 0;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testPVReader() throws InterruptedException {

		
		final PVReader<List<Object>> reader = PVManager.read(newValuesOf(channel(pvName))).maxRate(
				ofMillis(1000));

		reader.addPVReaderListener(new PVReaderListener() {
			public void pvChanged() {
				// Do something with each value
				Object newValue = reader.getValue();
				System.out.println(newValue);
				Exception ex = reader.lastException();
				if(ex != null)
					System.out.println(ex);
				updates ++;
			}
		});
		while(updates < 10){
			Thread.sleep(100);
		}
		reader.close();
	}
	@Ignore
	@Test
	public void testPVReadAndWrite() throws InterruptedException{
		final PV<Object, Object> pv = PVManager.readAndWrite(channel(pvName)).
				asynchWriteAndMaxReadRate(ofMillis(1));
		Thread.sleep(1000);
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
				Object newValue =pv.getValue();
				if(newValue ==null){
					System.out.println("null"+ " Connected: " + pv.isConnected());
					return;
				}
				
				Class<?> type = ValueUtil.typeOf(newValue);
				System.out.println(type + " " + ValueUtil.timeOf(newValue).getTimestamp() + " " + 
						ValueUtil.numericValueOf(newValue) + " "+
						ValueUtil.alarmOf(newValue).getAlarmSeverity() + " " + 
						ValueUtil.alarmOf(newValue).getAlarmStatus() +" " +
//						ValueUtil.displayOf(newValue).getFormat().format(ValueUtil.numericValueOf(newValue)) + " "+
						" Connected: " + pv.isConnected());
				if(newValue instanceof VEnum){
					System.out.println(((VEnum)newValue).getValue() + " " + 
				((VEnum)newValue).getIndex() +" " + ((VEnum)newValue).getLabels());
				}
				Exception ex = pv.lastException();
				if(ex != null)
					System.out.println(ex);
				ex = pv.lastWriteException();
				if(ex != null)
					System.out.println(ex);
				updates++;
			}
		});

		
//		while(updates < 10){
			Thread.sleep(1000);
//			pv.write(updates);
//		}
		System.out.println(pv.isConnected());
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
			public void pvChanged() {
					System.out.println("Second Listener: " + pv.getValue());
			}
		});
		Thread.sleep(1000);
		pv.close();
	}
	
	@Test
	public void testReadAllValues() throws InterruptedException{
		 final PVReader<List<Object>> pvReader = PVManager.read(newValuesOf(
				 channel(pvName))).maxRate(ofMillis(1000));
		 pvReader.addPVReaderListener(new PVReaderListener() {
		     public void pvChanged() {
		         // Do something with each value
		         for (Object newValue : pvReader.getValue()) {
		        	 System.out.println(ValueUtil.timeOf(newValue).getTimestamp() + " " + 
								ValueUtil.numericValueOf(newValue) + " "+
								ValueUtil.alarmOf(newValue).getAlarmSeverity() + " " + 
								ValueUtil.alarmOf(newValue).getAlarmStatus() +" "
//								ValueUtil.displayOf(newValue).getFormat().format(ValueUtil.numericValueOf(newValue)) + " "+
							);
		         }
		     }
		 });
		 Thread.sleep(5000);
		 // Remember to close
		 pvReader.close();
	}
	
	
}
