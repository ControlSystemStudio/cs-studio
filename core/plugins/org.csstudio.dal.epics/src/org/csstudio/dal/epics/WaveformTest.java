package org.csstudio.dal.epics;

import java.util.Arrays;

import org.epics.css.dal.DoubleSeqAccess;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.SequencePropertyCharacteristics;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactory;

public class WaveformTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf"};
			//String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf","T:8000c_wf"};
			EPICSApplicationContext ctx= new EPICSApplicationContext("Test");
			//AbstractApplicationContext ctx = new DefaultApplicationContext("Test");
//			/ctx.getConfiguration().put(Plugs.PLUG_PROPERTY_FACTORY_CLASS, "org.epics.css.dal.epics.PropertyFactoryImpl");
			PropertyFactory fac = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

			for (int i = 0; i < names.length; i++) {
				System.out.println("*** "+names[i]+" ***");
				DynamicValueProperty<?> p= fac.getProperty(names[i]);

				System.out.println("class: "+p.getClass().getName());
				System.out.println("RTYP: "+Arrays.toString((String[])p.getCharacteristic("RTYP")));
				System.out.println("fieldType: "+p.getCharacteristic("fieldType"));
				System.out.println("size: "+p.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH));
				System.out.println("value: "+p.getValue());

				DoubleSeqAccess dsa= p.getDataAccess(DoubleSeqAccess.class);
				double[] dd= dsa.getValue();
				System.out.println("value size: "+dd.length);
				//System.out.println(Arrays.toString(dd));
				
				fac.getPropertyFamily().destroy(p);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);

	}

}
