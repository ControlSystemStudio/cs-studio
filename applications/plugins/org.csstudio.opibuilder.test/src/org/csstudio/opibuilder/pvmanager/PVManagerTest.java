package org.csstudio.opibuilder.pvmanager;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.newValuesOf;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.vtype.ValueUtil;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PVManagerTest {

	private static int updates = 0;
	
	String pvName = //"css:sine";
			//"css:count";
//			"loc://test";
			//"css:setpoint";
			//"Ring_Diag:VFM:image"; 
			//"Ring_IDmp:Foil_Plunge:Psn";
			//"css:sensor";
			"sim://ramp(0,100,1,0.1)";
			//"CG1D:Cam:Cam1:AcquireTime";
//			"readback";
	@BeforeClass
	public static void setUp() throws Exception {

		updates = 0;
	}

	@After
	public void tearDown() throws Exception {
	}

	class PV{
		private PVReader<Object> reader;

		public PV(PVReader<Object> reader) {
			this.reader = reader;
		}
		
		//This synchronization will cause deadlock!!!
		public synchronized Object getValue(){
			return reader.getValue();
		}	
		
	}
	
	@Test
	public void testPVReader() throws InterruptedException {

			final PVReader<Object> reader = PVManager.read(channel(pvName)).maxRate(
				ofMillis(100));
		
			final PV pv = new PV(reader);
		

		reader.addPVReaderListener(new PVReaderListener<Object>() {
            public void pvChanged(PVReaderEvent event) {
				// Do something with each value
//				Object newValue = reader.getValue();
//				System.out.println(newValue);
//				Exception ex = reader.lastException();
//				if(ex != null)
//					System.out.println(ex);
//				updates ++;
            	System.out.println(Thread.currentThread().getName() + ": "+ pv.getValue());
				
			}
		});
		
		Thread thread2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("thread2: "+pv.getValue());
				}
			}
		}, "thread2");
		thread2.start();
		
		while(updates < 10){
			Thread.sleep(100);
		}
		reader.close();
	}
@Ignore
	@Test
	public void testPVReadAndWrite() throws InterruptedException{
		final Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());
		Button ok = new Button (shell, SWT.PUSH);
		ok.setText ("OK");
		shell.open ();
		 Executor SWTThread = new Executor() {

             @Override
             public void execute(Runnable task) {
                 try {
                     if (!display.isDisposed()) {
                         display.asyncExec(task);
                     }
                             } catch (Exception e) {
                                     e.printStackTrace();
                             }
             }
         };
		final PVReader<Object> pv = PVManager.read(channel(pvName)).notifyOn(SWTThread)
				.maxRate(ofMillis(10));
		
		Thread.sleep(1000);
		//for(long i=0; i<10000000L; i++){}
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
            public void pvChanged(PVReaderEvent event) {
				System.out.println("First Listener: " + ValueUtil.numericValueOf(pv.getValue()));
				System.out.println(Thread.currentThread().getName());

//				Object newValue =pv.getValue();
//				if(newValue ==null){
//					System.out.println("null"+ " Connected: " + pv.isConnected());
//					return;
//				}
//				
//				Class<?> type = ValueUtil.typeOf(newValue);
//				System.out.println(type + " " + ValueUtil.timeOf(newValue).getTimestamp() + " " + 
//						ValueUtil.numericValueOf(newValue) + " "+
//						ValueUtil.alarmOf(newValue).getAlarmSeverity() + " " + 
//						ValueUtil.alarmOf(newValue).getAlarmStatus() +" " +
////						ValueUtil.displayOf(newValue).getFormat().format(ValueUtil.numericValueOf(newValue)) + " "+
//						" Connected: " + pv.isConnected());
//				if(newValue instanceof VEnum){
//					System.out.println(((VEnum)newValue).getValue() + " " + 
//				((VEnum)newValue).getIndex() +" " + ((VEnum)newValue).getLabels());
//				}
//				Exception ex = pv.lastException();
//				if(ex != null)
//					System.out.println(ex);				
//				updates++;
			}
		});

		
		//Thread.sleep(1000);
		pv.addPVReaderListener(new PVReaderListener() {
			
			@Override
            public void pvChanged(PVReaderEvent event) {
					System.out.println("Second Listener: " + ValueUtil.numericValueOf(pv.getValue()));
			}
		});
		//while(updates < 10){
		//	Thread.sleep(20000);
		//}
			
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ()) display.sleep ();
			}
			display.dispose ();
		pv.close();
	}
	
	@Ignore
	@Test
	public void testReadAllValues() throws InterruptedException{
		 final PVReader<List<Object>> pvReader = PVManager.read(newValuesOf(
				 channel(pvName))).maxRate(ofMillis(1000));
		 pvReader.addPVReaderListener(new PVReaderListener() {
             public void pvChanged(PVReaderEvent event) {
		         // Do something with each value
		         for (Object newValue : pvReader.getValue()) {
		        	 System.out.println(ValueUtil.timeOf(newValue).getTimestamp() + " " + 
								ValueUtil.numericValueOf(newValue) + " "+
								ValueUtil.alarmOf(newValue).getAlarmSeverity() + " " + 
								ValueUtil.alarmOf(newValue).getAlarmName() +" "
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
