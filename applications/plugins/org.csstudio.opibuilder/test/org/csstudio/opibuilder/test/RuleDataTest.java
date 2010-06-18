package org.csstudio.opibuilder.test;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.script.Expression;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.visualparts.RuleDataEditDialog;
import org.junit.Test;

public class RuleDataTest {

	DisplayModel model = new DisplayModel();
	
	//@Test
	public void testGenerateScript() {
		
		//model.setForegroundColor(new RGB(0,255,0));
		RuleData ruleData = new RuleData(model);
		ruleData.setName("test");
		ruleData.setPropId("foreground_color");
		ruleData.addPV(new PVTuple("loc://test", true));
		ruleData.setOutputExpValue(true);
		ruleData.addExpression(new Expression("pv0 > 5", "sdf"));
		System.out.println(ruleData.generateScript());
	}
	
	@Test
	public void testDialog(){
		RuleDataEditDialog dialog = new RuleDataEditDialog(null, new RuleData(model));
		dialog.open();
	}
	

}
