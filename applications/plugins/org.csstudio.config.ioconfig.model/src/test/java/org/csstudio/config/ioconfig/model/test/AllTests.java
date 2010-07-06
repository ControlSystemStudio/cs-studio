package org.csstudio.config.ioconfig.model.test;

import org.csstudio.config.ioconfig.model.DocumentTest;
import org.csstudio.config.ioconfig.model.Facility_Test;
import org.csstudio.config.ioconfig.model.NodeTest;
import org.csstudio.config.ioconfig.model.SearchRootTest;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure_Test;
import org.csstudio.config.ioconfig.model.pbmodel.Channel_Test;
import org.csstudio.config.ioconfig.model.pbmodel.CopyNode_Test;
import org.csstudio.config.ioconfig.model.pbmodel.Module_Test;
import org.csstudio.config.ioconfig.model.pbmodel.Slave_Test;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmDataConst_Test;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ExtUserPrmData_Test.class,
    ExtUserPrmDataConst_Test.class ,

    Channel_Test.class,
    ChannelStructure_Test.class,
    CopyNode_Test.class,
    Module_Test.class,
    Slave_Test.class,

    DocumentTest.class,
    Facility_Test.class,
    Facility_Test.class,
    NodeTest.class,
    SearchRootTest.class
})
public class AllTests {
    // Suite does not implement anything.
}
