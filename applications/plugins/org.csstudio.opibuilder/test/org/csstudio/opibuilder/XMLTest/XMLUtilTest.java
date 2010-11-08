package org.csstudio.opibuilder.XMLTest;

import junit.framework.TestCase;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.junit.Test;

public class XMLUtilTest extends TestCase {

	AbstractWidgetModel testModel = new DisplayModel();
	
	
	@Override
	protected void setUp() throws Exception {
		testModel = new DisplayModel();
	}
	
	@Test
	public void testWidgetToXMLElement(){
	//	Element element = XMLUtil.WidgetToXMLElement(testModel);
		System.out.println(XMLUtil.widgetToXMLString(testModel, true));
	}

	
}
