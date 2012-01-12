package org.csstudio.dal.epics.demo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkAdapter;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.impl.DynamicValuePropertyImpl;
import org.epics.css.dal.spi.AbstractPropertyFactory;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class Demo3 {

	public static void main(String[] args) throws Exception {
		
		final String propertyName = "manyChannel_002";
		AbstractApplicationContext ctx = new EPICSApplicationContext("DEMO");
		final PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.ASYNC_LINK_POLICY,"EPICS");

		new Thread(new Runnable(){
			
			public void run() {
				try {
				EnumProperty pr1 = pf.getProperty(propertyName, EnumProperty.class, null);
				pr1.addDynamicValueListener(new DynamicValueAdapter<Long, EnumProperty>(){
					public void valueChanged(DynamicValueEvent<Long, EnumProperty> event) {
						System.out.println("enum "+event.getValue());
					}
				});
				
				pr1.addLinkListener(new LinkAdapter<EnumProperty>(){
					public void connected(ConnectionEvent<EnumProperty> e) {
						System.out.println(e.getState());
					}
				});
				pr1.addPropertyChangeListener(new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent arg0) {
						System.out.println(arg0.getPropertyName());
					}
				});
				System.out.println("Enum property done.");
				} catch (Exception e){}
			}
		}).start();
		
		
		new Thread(new Runnable(){
			public void run() {
				try {
				StringProperty pr2 = pf.getProperty(propertyName, StringProperty.class, null);
				
				pr2.addDynamicValueListener(new DynamicValueAdapter<String, StringProperty>(){
					public void valueChanged(DynamicValueEvent<String, StringProperty> event) {
						System.out.println("string " + event.getValue());
					}
				});
				
				pr2.addLinkListener(new LinkAdapter<StringProperty>(){
					public void connected(ConnectionEvent<StringProperty> e) {
						System.out.println(e.getState());
					}
				});
				pr2.addPropertyChangeListener(new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent arg0) {
						System.out.println(arg0.getPropertyName());
					}
				});
				
				System.out.println("String property done.");
				} catch (Exception e){}
			
		}}).start();
		
		new Thread(new Runnable(){
			public void run() {
				try {
				DoubleProperty pr3 = pf.getProperty(propertyName, DoubleProperty.class, null);
				
				pr3.addDynamicValueListener(new DynamicValueAdapter<Double, DoubleProperty>(){
					public void valueChanged(DynamicValueEvent<Double, DoubleProperty> event) {
						System.out.println("double "+ event.getValue());
					}
				});
				
				pr3.addLinkListener(new LinkAdapter<DoubleProperty>(){
					public void connected(ConnectionEvent<DoubleProperty> e) {
						System.out.println(e.getState());
					}
				});
				pr3.addPropertyChangeListener(new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent arg0) {
						System.out.println(arg0.getPropertyName());
					}
				});
				
				System.out.println("Double property done.");
				} catch (Exception e){}
			}
		}).start();
		
		
		new Thread(new Runnable(){
			public void run() {
				try {
				LongProperty pr4 = pf.getProperty(propertyName, LongProperty.class, null);
				
				pr4.addDynamicValueListener(new DynamicValueAdapter<Long, LongProperty>(){
					public void valueChanged(DynamicValueEvent<Long, LongProperty> event) {
						System.out.println("long " + event.getValue());
					}
				});
				
				pr4.addLinkListener(new LinkAdapter<LongProperty>(){
					public void connected(ConnectionEvent<LongProperty> e) {
						System.out.println(e.getState());
					}
				});
				pr4.addPropertyChangeListener(new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent arg0) {
						System.out.println(arg0.getPropertyName());
					}
				});
				
				System.out.println("Long property done.");
				} catch (Exception e){}
				
			}
		}).start();
		
		
//		for (int i = 0; i < 6; i++) {
//			pr1.getCharacteristicAsynchronously("SEVR", new ResponseListener(){
//				public void responseError(ResponseEvent event) {
//					System.out.println(event.getResponse().getError());
//				}
//				public void responseReceived(ResponseEvent event) {
//					System.out.println(event.getResponse().getValue());
//					
//				}
//			});
//		}
	}
}
