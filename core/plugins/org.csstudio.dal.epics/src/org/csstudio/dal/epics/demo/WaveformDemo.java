package org.csstudio.dal.epics.demo;

import org.csstudio.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class WaveformDemo {

	public static void main(String[] args) throws Exception {
		char c = 0;
		
		System.out.println(">>>"+c);
		EPICSApplicationContext ctx = new EPICSApplicationContext("Test");
		PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);
		
		StringProperty sp = pf.getProperty("wave", StringProperty.class, null);
		System.out.println("Waveform value: " + sp.getValue());
//		String val = "2345678900;
//		System.out.println("Set value: " + val);
//		sp.setValue(val);
//		Thread.sleep(2000);
//		System.out.println("New Value: " + sp.getValue());
	}
}
