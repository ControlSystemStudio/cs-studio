package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import junit.framework.TestCase;

public class OpiColorDefTest extends TestCase {

	public void testOpiColorDef() throws EdmException {
		
		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");
		EdmModel.getInstance();
		
		OpiColorDef.writeDefFile(EdmModel.getColorsList(), "src/test/resources/color.def");
	}
}
