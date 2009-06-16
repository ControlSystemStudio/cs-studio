/**
 * 
 */
package org.csstudio.dct.nameresolution.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.util.AliasResolutionException;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link FieldFunctionService}.
 * 
 * @author Sven Wende
 * 
 */
public final class FieldFunctionServiceTest implements IFieldFunction {
	private FieldFunctionService service;

	/**
	 * Setup.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = new FieldFunctionService();
		service.registerFunction("a", this);
	}

	/**
	 * Test method for
	 * {@link FieldFunctionService#findRequiredVariables(String)}
	 * .
	 */
	@Test
	public void testFindRequiredVariables() {
		doTestFindRequiredVariables(null);
		doTestFindRequiredVariables("");
		doTestFindRequiredVariables("abc");
		doTestFindRequiredVariables("$(a)bc", "a");
		doTestFindRequiredVariables("$(a)b$(c)", "a", "c");
		doTestFindRequiredVariables("$(a)$(b)$(c)", "a", "b", "c");
		doTestFindRequiredVariables("x$(a)x$(b)x$(c)x", "a", "b", "c");
		doTestFindRequiredVariables("$(a)$(a)$(a)", "a");
	}

	private void doTestFindRequiredVariables(String source, String... variables) {
		Set<String> vars = service.findRequiredVariables(source);

		HashSet<String> expectedvars = new HashSet<String>(Arrays.asList(variables));
		assertEquals(expectedvars.size(), vars.size());
		assertTrue(vars.containsAll(expectedvars));
	}
	
	/**
	 * Test method for
	 * {@link FieldFunctionService#resolve(String, org.csstudio.dct.model.IElement)}
	 * .
	 */
	@Test
	public void testResolve() {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("a", "1_$(b)_$(e)_");
		vars.put("b", "$(c)>$(d)");
		vars.put("c", "2");
		vars.put("d", "3");
		vars.put("e", "4");
		vars.put("f", "5$(g)");
		vars.put("g", "6$(f)");
		vars.put("m", "$(n)");
		vars.put("n", "$(o)");
		vars.put("o", "$(m)");
		vars.put("p", "$(q)");
		vars.put("q", "7");
		
		
		
		
		
		// .. no vars
		doTestResolve(null, null, vars, false);
		doTestResolve("", "", vars, false);
		doTestResolve("a", "a", vars, false);
		doTestResolve("1", "1", vars, false);
		doTestResolve("$(a", "$(a", vars, false);
		doTestResolve("$a", "$a", vars, false);
		doTestResolve("(a)", "(a)", vars, false);
		doTestResolve("$a)", "$a)", vars, false);
		
		// .. simple single vars
		doTestResolve("$(d)", "3", vars, false);
		doTestResolve("_$(d)", "_3", vars, false);
		doTestResolve("$(d)_", "3_", vars, false);
		doTestResolve("_$(d)_", "_3_", vars, false);
		doTestResolve("$(c)$(d)", "23", vars, false);
		
		// .. simple multiple vars
		doTestResolve("$(c)$(d)", "23", vars, false);
		doTestResolve("$(c)$(d)$(c)$(d)", "2323", vars, false);
		doTestResolve("$(d)$(d)$(d)$(d)", "3333", vars, false);
		doTestResolve("_$(c)_$(d)_$(c)_$(d)_", "_2_3_2_3_", vars, false);
		doTestResolve("$(q)$(q)$(p)$(q)", "7777", vars, false);
		
		// .. nested vars
		doTestResolve("$(a)", "1_2>3_4_", vars, false);
		doTestResolve("$(b)", "2>3", vars, false);
		
		// .. erroneous
		doTestResolve("$(x)", null, vars, true);
		doTestResolve("$(f)", null, vars, true);
		doTestResolve("$(g)", null, vars, true);
		doTestResolve("$(m)", null, vars, true);
		doTestResolve("$(n)", null, vars, true);
		doTestResolve("$(o)", null, vars, true);
		
		
	}

	private void doTestResolve(String source, String expected, Map<String, String> vars, boolean errorExpected) {
		try {
			String result = service.resolve(source, vars);

			if (errorExpected) {
				fail("Expected exception was not thrown.");
			} else {
				assertEquals(expected, result);
			}
		} catch (AliasResolutionException e) {
			if (!errorExpected) {
				e.printStackTrace();
				fail("Unexpected exception has been thrown.");
			}
		}

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.nameresolution.internal.FieldFunctionService#evaluate(java.lang.String, org.csstudio.dct.model.IRecord, java.lang.String)}
	 * .
	 */
	@Test
	public void testEvaluate() {
		doTestEvaluate(null, null, false);
		doTestEvaluate("", "", false);
		doTestEvaluate("a", "a", false);
		doTestEvaluate(">a", ">a", false);
		doTestEvaluate(">a(", ">a(", false);
		doTestEvaluate(">a(b,c)", "a:b:c", false);
		doTestEvaluate(">a(b,cd)", "a:b:cd", false);
		doTestEvaluate(">a(b,c,d)", null, true);
		doTestEvaluate(">a(a)", null, true);
		doTestEvaluate(">unknown(a)", null, true);
	}

	private void doTestEvaluate(String source, String expected, boolean errorExpected) {
		String result;

		try {
			result = service.evaluate(source, new Record("test", "ai", UUID.randomUUID()), "val");

			if (errorExpected) {
				fail("Expected exception was not thrown.");
			} else {
				assertEquals(expected, result);
			}
		} catch (Exception e) {
			if (!errorExpected) {
				fail("Unexpected exception has been thrown.");
			}
		}

	}

	/**
	 *{@inheritDoc}
	 */
	public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
		if (parameters == null || parameters.length != 2) {
			throw new Exception("This function expects 2 parameters.");
		}

		StringBuffer sb = new StringBuffer();

		sb.append(name);

		for (String p : parameters) {
			sb.append(":");
			sb.append(p);
		}

		return sb.toString();
	}

	public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knownParameters, IRecord record) {
		return Collections.EMPTY_LIST;
	}

}
