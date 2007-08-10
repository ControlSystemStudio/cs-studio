package de.desy.css.dal.tine.demo;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.PropertyFactory;

import de.desy.css.dal.tine.TINEApplicationContext;

public class SimpleDemo {
		
	public static void main(String[] args) throws RemoteException, InstantiationException {
		try {
			
			// Choose TINe channel name. Name must be of form:
			// <protocol, always TINE>/<context name>/<group name>/<device name>/<property name>
//			String name = "TINE/DEFAULT/JWKSINE/Device 3/Amplitude";
//			String name = "TINE/TEST/WinSineServer/SineGen0/Amplitude";
			String name = "TINE/DEFAULT/TIMESRV/device_0/SYSTIME";
			
			// Create application context
			DefaultApplicationContext ctx= new TINEApplicationContext("SimpleDemo");
			
			// print out configuration, which can be used on arbitrary application context
			ctx.getConfiguration().store(System.out,"TINE application context configuration");
			
			// creates factory, which will provide TINE channels
			PropertyFactory propertyFactory = DefaultPropertyFactoryService.
				getPropertyFactoryService().getPropertyFactory(ctx, null);
			
			// We request double channel with specified name
			RemoteInfo remoteInfo = new RemoteInfo(name);
			final DynamicValueProperty property = propertyFactory.getProperty(remoteInfo);
//			final DoubleProperty property = propertyFactory.getProperty(name, DoubleProperty.class, null);
			
			// We register listener, which will receive value updates
			
			property.addDynamicValueListener(new DynamicValueAdapter() {
			
				@Override
				public void valueUpdated(DynamicValueEvent arg0) {
					System.out.println("UP: "+arg0.getValue());
				}
			
				@Override
				public void valueChanged(DynamicValueEvent arg0) {
					System.out.println("CH: "+arg0.getValue());
				}
			
			});
			
//			System.out.println(property.getConnectionState());
//			for (DynamicValueState s : property.getCondition().getStates()) System.out.println(s); 
//			System.out.println("GETTING VALUE " + property.getValue());
//			System.out.println(property.getConnectionState());
//			for (DynamicValueState s : property.getCondition().getStates()) System.out.println(s); 
			
//			final double max = property.getMaximum();
//			
//			new Thread(new Runnable() {
//				public void run() {
//					int i=0;
//					while(i++<10) {
//						try {
//							double d= Math.random()*max;
//							System.out.println("SET: "+d);
//							property.setValue(d);
//							System.out.println("GET: "+property.getValue());
//						} catch (DataExchangeException e1) {
//							e1.printStackTrace();
//						}
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//					
//					System.out.println("Done.");
//					System.exit(0);
//					
//				}
//			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
