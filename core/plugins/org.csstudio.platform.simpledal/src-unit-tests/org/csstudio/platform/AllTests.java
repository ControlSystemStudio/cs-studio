package org.csstudio.platform;
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



import org.csstudio.platform.internal.dal.DataAccessLayerTest1;
import org.csstudio.platform.internal.dal.DataAccessLayerTest2;
import org.csstudio.platform.internal.dal.DataAccessLayerTest3;
import org.csstudio.platform.internal.model.pvs.AbstractProcessVariableNameParserTest;
import org.csstudio.platform.internal.model.pvs.ControlSystemEnumTest;
import org.csstudio.platform.internal.model.pvs.DALPropertyFactoriesProviderTest;
import org.csstudio.platform.internal.model.pvs.DalNameParserTest;
import org.csstudio.platform.internal.model.pvs.ProcessVariableAdressTest;
import org.csstudio.platform.internal.model.pvs.SimpleNameParserTest;
import org.csstudio.platform.internal.simpledal.AnyDataTest;
import org.csstudio.platform.internal.simpledal.ConnectorIdentificationTest;
import org.csstudio.platform.internal.simpledal.DALPrecisionTest;
import org.csstudio.platform.internal.simpledal.DalCacheBugTest;
import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionServiceTest;
import org.csstudio.platform.internal.simpledal.SimpleDALTest;
import org.csstudio.platform.internal.simpledal.SimpleDAL_EPICSTest;
import org.csstudio.platform.internal.simpledal.converters.ConverterTest;
import org.csstudio.platform.internal.simpledal.dal.DalConnectorTest;
import org.csstudio.platform.internal.simpledal.local.DataGeneratorInfosTest;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactoryTest;
import org.csstudio.platform.simpledal.ConnectionStateTest;
import org.csstudio.platform.simpledal.RecordCombinationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AbstractProcessVariableNameParserTest.class,
        ControlSystemEnumTest.class,
        DalNameParserTest.class,
        DALPropertyFactoriesProviderTest.class,
        ProcessVariableAdressTest.class,
        SimpleNameParserTest.class,

        DataAccessLayerTest1.class,
        DataAccessLayerTest2.class,
        DataAccessLayerTest3.class,

        ConverterTest.class,

        DalConnectorTest.class,

        DataGeneratorInfosTest.class,

        AnyDataTest.class,
        ConnectorIdentificationTest.class,
        DalCacheBugTest.class,
        DALPrecisionTest.class,
        ProcessVariableConnectionServiceTest.class,
        SimpleDAL_EPICSTest.class,
        SimpleDALTest.class,

        ProcessVariableAdressFactoryTest.class,

        ConnectionStateTest.class,
        RecordCombinationTest.class,
})
public class AllTests {
    // EMPTY
}

