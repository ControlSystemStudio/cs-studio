package org.csstudio.opibuilder.pvmanager;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.loc.LocalDataSource;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VNumber;
import static org.epics.pvmanager.vtype.ExpressionLanguage.*;
public class MultipleListeners {
        private static Text textA;
        private static Text textB;
        private static PVReader<VNumber> pv;

        /**
         * @param args
         * @throws InterruptedException
         */
        public static void main(String[] args) throws InterruptedException {
                final Display display = new Display();
                Shell shell = new Shell(display);
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                
                // Note that as long as your application has not overridden
                // the global accelerators for copy, paste, and cut
                //(CTRL+C or CTRL+INSERT, CTRL+V or SHIFT+INSERT, and CTRL+X or SHIFT+DELETE)
                // these behaviours are already available by default.
                // If your application overrides these accelerators,
                // you will need to call Text.copy(), Text.paste() and Text.cut()
                // from the Selection callback for the accelerator when the
                // text widget has focus.

                shell.setSize(200, 200);
                shell.setLayout(null);

                textA = new Text(shell, SWT.BORDER);
                textA.setBounds(10, 10, 162, 26);

                textB = new Text(shell, SWT.BORDER);
                textB.setBounds(10, 42, 162, 26);
                shell.open();

                PVManager.setDefaultDataSource(new LocalDataSource());
                
               // Thread thread = Thread.currentThread();
                
                Executor SWTThread = new Executor() {

                @Override
                public void execute(Runnable task) {
//                	task.run();
                    try {
                        if (!display.isDisposed()) {
                            display.asyncExec(task);
                        }
                    } catch (Exception e) {
                                        e.printStackTrace();
                        }
                }
            };
               
                
//                executor.execute(new Runnable() {
//					
//					@Override
//					public void run() {
						pv= PVManager.read(vNumber("loc://test")).notifyOn(SWTThread).
                		maxRate(TimeDuration.ofMillis(5));
//					}
//				});
                
                
//                for(long i=0; i<1000000000; i++){
//                	//do my stuff
//                }
                Thread.sleep(1000);

//                executor.execute(new Runnable() {
//					
//					@Override
//					public void run() {
						 pv.addPVReaderListener(new PVReaderListener() {		                  
		                        
		                        @Override
		                        public void pvChanged(PVReaderEvent event) {
		                                         if (pv.getValue() != null)
//		                                        textA.setText(pv.getValue().getValue().toString());
		                                	System.out.println("First: " + pv.getValue().getValue().toString() + " " + Thread.currentThread().getName());
		             
		                        }
		                });
						
//					}
//				});
                
               
                Thread.sleep(1000);
//                executor.execute(new Runnable() {
//					
//					@Override
//					public void run() {
						pv.addPVReaderListener(new PVReaderListener() {
	             
	                        @Override
	                        public void pvChanged(PVReaderEvent event) {
	                        	               if (pv.getValue() != null)
//	                                        textB.setText(pv.getValue().getValue().toString());
	                                	System.out.println("Second: " + pv.getValue().getValue().toString() + " " + Thread.currentThread().getName());

	                        }
	                });						
//					}
//				});
                

                while (!shell.isDisposed()) {
                        if (!display.readAndDispatch())
                                display.sleep();
                }
                display.dispose();
                executor.shutdown();
        }
}