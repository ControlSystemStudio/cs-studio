package org.csstudio.autocomplete.pvmanager.formula;

import org.csstudio.autocomplete.AutoCompleteType;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class FormulaTests {

	@Test
	public void testParser() {
		FormulaContentParser fcp = new FormulaContentParser();
		ContentDescriptor formula = new ContentDescriptor();
		formula.setAutoCompleteType(AutoCompleteType.Formula);
		ContentDescriptor desc = null;

		formula.setValue("A+func((B*C),2,\"");
		desc = fcp.parse(formula);
		System.out.println(desc);
		Assert.assertTrue(desc.getContentType() == ContentType.Empty);

		formula.setValue("A+func((B*C),2.2,'");
		desc = fcp.parse(formula);
		System.out.println(desc);
		Assert.assertTrue(desc.getContentType() == ContentType.Empty);

		formula.setValue("A+func((B*C),2.2,'CWS-");
		desc = fcp.parse(formula);
		System.out.println(desc);
		Assert.assertTrue(desc.getContentType() == ContentType.PV);

		formula.setValue("A + func() * bo");
		desc = fcp.parse(formula);
		System.out.println(desc);
		Assert.assertTrue(desc.getContentType() == ContentType.FormulaFunction);
	}
}
