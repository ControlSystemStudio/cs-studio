/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

/**
 * @author swende
 * 
 */
public class AbstractProcessVariableNameParserTest {
	private Mockery _mockery;
	
	private AbstractProcessVariableNameParser _parser;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create a mock for the connector factory
		_mockery = new Mockery() {
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};

		_parser = _mockery.mock(AbstractProcessVariableNameParser.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.pvs.AbstractProcessVariableNameParser#parseRawName(java.lang.String)}.
	 */
	@Test
	public void testParseRawName() {
		_mockery.checking(new Expectations() {
			{
				// 
				one(_parser).doParse("abc1","abc1");
				returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.UNKNOWN, "", "abc1", ""));
				one(_parser).doParse("abc2","dal-epics://abc2");
				returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.DAL_EPICS, "", "abc2", ""));
				one(_parser).doParse("abc3","epics://abc3");
				returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.EPICS, "", "abc3", ""));
				one(_parser).doParse("abc4","tine://abc4");
				returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.TINE, "", "abc4", ""));				
			}
		});

		// do something
		_parser.parseRawName("abc1");
		_parser.parseRawName("dal-epics://abc2");
		_parser.parseRawName("epics://abc3");
		_parser.parseRawName("tine://abc4");
		
		// check mocks
		_mockery.assertIsSatisfied();
	}

}
