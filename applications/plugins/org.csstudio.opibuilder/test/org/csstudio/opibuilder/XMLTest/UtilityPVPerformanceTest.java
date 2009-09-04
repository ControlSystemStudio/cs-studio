package org.csstudio.opibuilder.XMLTest;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.jdom.Element;
import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestResult;

public class UtilityPVPerformanceTest extends TestCase {

	
	
	
	@Test
	public void testPVStartStop() throws Exception{
			final PV pv = PVFactory.createPV("sim://ramp");	
			int i = 100000;
			while(i >0){
				i--;
				PVListener pvListener = new PVListener(){
					public void pvDisconnected(PV pv) {
						// TODO Auto-generated method stub
						
					}
					public void pvValueUpdate(PV pv) {
						System.out.println(pv.getValue());
					}
				};
				pv.addListener(pvListener);
				pv.start();		
				
				Thread.sleep(10);
				pv.removeListener(pvListener);
				pv.stop();
				
			}
			
	}

	
}
