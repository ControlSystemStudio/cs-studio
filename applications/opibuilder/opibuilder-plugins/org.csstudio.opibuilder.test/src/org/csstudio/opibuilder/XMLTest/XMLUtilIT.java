/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.XMLTest;

import junit.framework.TestCase;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.junit.Test;

public class XMLUtilIT extends TestCase {

    AbstractWidgetModel testModel = new DisplayModel();


    @Override
    protected void setUp() throws Exception {
        testModel = new DisplayModel();
    }

    @Test
    public void testWidgetToXMLElement(){
    //    Element element = XMLUtil.WidgetToXMLElement(testModel);
        System.out.println(XMLUtil.widgetToXMLString(testModel, true));
    }


}
