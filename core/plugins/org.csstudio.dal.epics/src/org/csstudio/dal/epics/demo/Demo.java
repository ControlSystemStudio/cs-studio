package org.csstudio.dal.epics.demo;

import java.util.Map;

import org.csstudio.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.epics.css.dal.spi.PropertyFactoryService;

public class Demo {

	
	public static void main(String[] args) throws Exception {
		
		PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(new EPICSApplicationContext("test"), LinkPolicy.ASYNC_LINK_POLICY);
		for (int i = 0; i < 10; i++) {
			DoubleProperty dp = pf.getProperty("manyChannel_002", DoubleProperty.class, null);
			final int c = i; 
			dp.addDynamicValueListener(new DynamicValueListener<Double, DoubleProperty>(){
				public void valueChanged(DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println(c + " " + event.getValue());
				}
				public void valueUpdated(DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println(c + " " + event.getValue());
				}
				public void conditionChange(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A condition change");
					
				}
				public void errorResponse(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A error");
					
				}
				public void timelagStarts(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timelag");
					
				}
				public void timelagStops(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timelag stop");
					
				}
				public void timeoutStarts(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timeout start");
				}
				public void timeoutStops(
						DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timeout stop");
				}
				
				
			});
		}
		Thread.sleep(3000);
	}
}
