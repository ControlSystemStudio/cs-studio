/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.test;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.script.Expression;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.visualparts.RuleDataEditDialog;
import org.junit.Test;

public class RuleDataDemo {

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
