package org.csstudio.opibuilder.XMLTest;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.jdom.Element;
import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestResult;

public class XMLUtilTest extends TestCase {

	AbstractWidgetModel testModel;
	
	
	@Override
	protected void setUp() throws Exception {
		testModel = new DisplayModel();
	}
	
	@Test
	public void testWidgetToXMLElement(){
		Element element = XMLUtil.WidgetToXMLElement(testModel);
		System.out.println(XMLUtil.ElementToString(element));
	}

	
}
