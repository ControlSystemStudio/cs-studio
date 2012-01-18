package org.csstudio.dal.epics.demo;

import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

public class Demo {


	public static void main(final String[] args) throws Exception {

		final PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(new EPICSApplicationContext("test"), LinkPolicy.ASYNC_LINK_POLICY);
		for (int i = 0; i < 10; i++) {
			final DoubleProperty dp = pf.getProperty("manyChannel_002", DoubleProperty.class, null);
			final int c = i;
			dp.addDynamicValueListener(new DynamicValueListener<Double, DoubleProperty>(){
				@Override
                public void valueChanged(final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println(c + " " + event.getValue());
				}
				@Override
                public void valueUpdated(final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println(c + " " + event.getValue());
				}
				@Override
                public void conditionChange(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A condition change");

				}
				@Override
                public void errorResponse(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A error");

				}
				@Override
                public void timelagStarts(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timelag");

				}
				@Override
                public void timelagStops(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timelag stop");

				}
				@Override
                public void timeoutStarts(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timeout start");
				}
				@Override
                public void timeoutStops(
						final DynamicValueEvent<Double, DoubleProperty> event) {
					System.out.println("A timeout stop");
				}


			});
		}
		Thread.sleep(3000);
	}
}
