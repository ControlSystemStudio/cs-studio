package org.epics.css.dal.epics.desy.test;

import java.util.Arrays;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.SequencePropertyCharacteristics;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class EPICSChannelsTest {

	public static final String[] NAME_BASE= {
		"PV_AI_",
		"PV_AO_",
		"PV_BO_",
		"PV_BI_",
		"PV_CALC_",
		"PV_DFANOUT_",
		"PV_COMPRESS_",
		"PV_CALACOUT_",
		"PV_LONGOUT_",
		"PV_LONGIN_",
		"PV_EVENT_",
		"PV_MBBO_",
		"PV_MBBI_",
		"PV_MBBIDIRECT_",
		"PV_SEL_",
		"PV_MBBODIRECT_",
		"PV_PRERMISIVE_",
		"PV_STRINGIN_",
		"PV_SEQ_",
		"PV_STATE_",
		"PV_SUBARRAY_",
		"PV_STRINGout_",
		"PV_WFD_",
		"PV_WFS_",
		"PV_WFL_",
		"PV_ARR_TEST_",
		"PV_COMPRESS_STEP_",
		"PV_AO_STEP_",
		"PV_AI_STEP_",
		"PV_BO_STEP_",
		"PV_BI_STEP_",
		"PV_CALC_STEP_",
		"PV_DFANOUT_STEP_",
		"PV_CALCOUT_STEP_",
		"PV_LONGIN_STEP_",
		"PV_LONGOUT_STEP_",
		"PV_EVENT_STEP_",
		"PV_MBBO_STEP_",
		"PV_MBBI_STEP_",
		"PV_MBBIDIRECT_STEP_",
		"PV_SEL_STEP_",
		"PV_MBBODIRECT_STEP_",
		"PV_PERMISSIVE_STEP_",
		"PV_STRINGIN_STEP_",
		"PV_SEQ_STEP_",
		"PV_STATE_STEP_",
		"PV_SUBARRAY_STEP_",
		"PV_STRINGOUT_STEP_",
		"PV_WFD_STEP_",
		"PV_WFS_STEP_",
		"PV_WFL_STEP_",
	};
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EPICSApplicationContext ctx = new EPICSApplicationContext("Test");
		PropertyFactory fac = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);

		
		try {
			for (int i = 0; i < NAME_BASE.length; i++) {
				System.out.println("*** "+NAME_BASE[i]+"01 ***");
				DynamicValueProperty<?> p= fac.getProperty(NAME_BASE[i]+"01");

				System.out.println("class: "+p.getClass().getName());
				System.out.println("RTYP: "+Arrays.toString((String[])p.getCharacteristic("RTYP")));
				System.out.println("fieldType: "+p.getCharacteristic("fieldType"));
				System.out.println("size: "+p.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH));
				Object o= p.getValue();
				System.out.println("value: "+o);

				fac.getPropertyFamily().destroy(p);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ctx.destroy();
		
		//System.exit(0);
	}

}
