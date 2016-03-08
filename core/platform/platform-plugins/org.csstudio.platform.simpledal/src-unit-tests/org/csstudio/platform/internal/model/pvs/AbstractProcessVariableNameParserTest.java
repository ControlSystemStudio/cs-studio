/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 /**
 *
 */
package org.csstudio.platform.internal.model.pvs;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
//                one(_parser).doParse("abc3","epics://abc3");
//                returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.EPICS, "", "abc3", ""));
//                one(_parser).doParse("abc4","tine://abc4");
//                returnValue(new ProcessVariableAdress("abc", ControlSystemEnum.TINE, "", "abc4", ""));
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
