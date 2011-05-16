/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.platform;


import org.csstudio.platform.epicsdbfile.FieldTest;
import org.csstudio.platform.epicsdbfile.RecordInstanceDatabaseLexerTest;
import org.csstudio.platform.epicsdbfile.RecordInstanceDatabaseParserTest;
import org.csstudio.platform.epicsdbfile.RecordInstanceTest;
import org.csstudio.platform.epicsdbfile.TokenTest;
import org.csstudio.platform.internal.model.ArchiveDataSourceFactoryTest;
import org.csstudio.platform.internal.model.ArchiveDataSourceTest;
import org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistryTest;
import org.csstudio.platform.internal.model.ProcessVariableFactoryTest;
import org.csstudio.platform.internal.model.ProcessVariableTest;
import org.csstudio.platform.logging.CentralLoggerTest;
import org.csstudio.platform.logging.JMSLogThreadUnitTest;
import org.csstudio.platform.management.CommandDescriptionTest;
import org.csstudio.platform.management.CommandParameterDefinitionTest;
import org.csstudio.platform.management.CommandParameterEnumValueTest;
import org.csstudio.platform.management.CommandParametersTest;
import org.csstudio.platform.management.CommandResultTest;
import org.csstudio.platform.util.StringUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FieldTest.class,
        RecordInstanceDatabaseLexerTest.class,
        RecordInstanceDatabaseParserTest.class,
        RecordInstanceTest.class,
        TokenTest.class,

        ArchiveDataSourceFactoryTest.class,
        ArchiveDataSourceTest.class,
        ControlSystemItemFactoriesRegistryTest.class,
        ProcessVariableFactoryTest.class,
        ProcessVariableTest.class,

        CentralLoggerTest.class,
        JMSLogThreadUnitTest.class,

        CommandDescriptionTest.class,
        CommandParameterDefinitionTest.class,
        CommandParameterEnumValueTest.class,
        CommandParametersTest.class,
        CommandResultTest.class,

        StringUtilTest.class

})
public class AllTests {
    // EMPTY
}

