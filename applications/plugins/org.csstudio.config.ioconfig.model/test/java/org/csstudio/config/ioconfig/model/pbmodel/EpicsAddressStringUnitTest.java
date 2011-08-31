/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;


import java.io.IOException;
import java.util.Collection;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.TestStructureBuilder;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSDTestFiles;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 29.08.2011
 */
public class EpicsAddressStringUnitTest {
    
    private static ProfibusSubnetDBO _BUILD_SUBNET;
    private static GSDFileDBO _b756P33;
    private static GSDFileDBO _siem80d1;
    private static SlaveDBO _BUILD_SLAVE1;
    private static SlaveDBO _BUILD_SLAVE2;
    private static MasterDBO _BUILD_MASTER;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Repository.injectIRepository(new DummyRepository());
        
        _b756P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
        _siem80d1 = GSDTestFiles.siem80d1.getFileAsGSDFileDBO();
        
        buildPrototypes4b756P33();
        buildPrototypes4siem80d1();
        
        final FacilityDBO buildFacility = TestStructureBuilder.buildFacility("FullChannelNumber Test", 1);
        final IocDBO buildIoc = TestStructureBuilder.buildIoc(buildFacility, "FullChannelNumber Test");
        _BUILD_SUBNET = TestStructureBuilder.buildSubnet(buildIoc, "FCNTest");
        _BUILD_MASTER = TestStructureBuilder.buildMaster(_BUILD_SUBNET);
        _BUILD_SLAVE1 = TestStructureBuilder.buildSlave(_BUILD_MASTER, 2, _b756P33);
        _BUILD_SLAVE2 = TestStructureBuilder.buildSlave(_BUILD_MASTER, 56, _siem80d1);
    }

    /**
     * 
     */
    private static void buildPrototypes4siem80d1() {
        GSDModuleDBO gsdModuleDBO;

        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 0");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(0);
        _siem80d1.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 1");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(1);
        _siem80d1.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 2");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(2);
        _siem80d1.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 15");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(15);
        
        ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Test 1.0 DI0.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(1);
        moduleChannelPrototype.setName("Test 1.0 DI1.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(2);
        moduleChannelPrototype.setName("Test 1.0 DI2.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(3);
        moduleChannelPrototype.setName("Test 1.0 DI3.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(4);
        moduleChannelPrototype.setName("Test 1.0 DI4.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(5);
        moduleChannelPrototype.setName("Test 1.0 DI5.");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        _siem80d1.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 15");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(22);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Test 1.0 AI");
        moduleChannelPrototype.setType(DataType.FLOAT);
        moduleChannelPrototype.setStructure(false);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        _siem80d1.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 15");
        gsdModuleDBO.setGSDFile(_siem80d1);
        gsdModuleDBO.setModuleId(51);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Test 1.0 AI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(false);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(1);
        moduleChannelPrototype.setName("Test 1.0 AI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(false);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        _siem80d1.addGSDModule(gsdModuleDBO);
        
    }

    /**
     * @throws IOException
     */
    private static void buildPrototypes4b756P33() throws IOException {
        GSDModuleDBO gsdModuleDBO;
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 8330");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(8330);
        _b756P33.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 1");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(4000);
        
        ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Test 1.0 DI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        _b756P33.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("FullChannelNumber Test 2");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(4001);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Test 2.0 DI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);

        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(1);
        moduleChannelPrototype.setName("Test 2.1 DI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        _b756P33.addGSDModule(gsdModuleDBO);
        
        gsdModuleDBO = new GSDModuleDBO("_b756P33 4360");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(4360);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("DI");
        moduleChannelPrototype.setType(DataType.UINT16);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        _b756P33.addGSDModule(gsdModuleDBO);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        _BUILD_SLAVE1.removeAllChild();
        _BUILD_SLAVE2.removeAllChild();
    }
    
    @Test
    public void subnetAddressTest() throws Exception {
        final String epicsAddressString = _BUILD_SUBNET.getEpicsAddressString();
        Assert.assertEquals("@FCNTest", epicsAddressString);
    }
    @Test
    public void masterAddressTest() throws Exception {
        final String epicsAddressString = _BUILD_MASTER.getEpicsAdressString();
        Assert.assertEquals("@FCNTest", epicsAddressString);
    }
    @Test
    public void slaveAddressTest() throws Exception {
        final String epicsAddressString = _BUILD_SLAVE1.getEpicsAdressString();
        Assert.assertEquals("@FCNTest:2", epicsAddressString);
    }
    @Test
    public void moduleAddressTest() throws Exception {
        final Collection<ModuleDBO> values = _BUILD_SLAVE1.getChildrenAsMap().values();
        int offset = 0;
        for (ModuleDBO moduleDBO : values) {
            final String epicsAddressString = moduleDBO.getEpicsAddressString();
            Assert.assertEquals("@FCNTest:2", epicsAddressString);
            final int inputOffset = moduleDBO.getInputOffset();
            final int inputOffsetNH = moduleDBO.getInputOffsetNH();
            final int outputOffset = moduleDBO.getOutputOffset();
            final int outputOffsetNH = moduleDBO.getOutputOffsetNH();
            Assert.assertEquals(0, inputOffset);
            Assert.assertEquals(offset, inputOffsetNH);
            Assert.assertEquals(0, outputOffset);
            Assert.assertEquals(0, outputOffsetNH);
            offset++;
        }
    }
    
    @Test
    public void channelAddressTestWith2xModuleA1xUNIT8() throws Exception {
        System.out.println("channelAddressTestWith2xModuleA1xUNIT8");
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 8330, 0);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4000, 1);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4000, 2);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE1.getChildrenAsMap().values();
        int channelCounter = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit), epicsAddressString);
                    bit = (bit+1)%8;
                }
                offset++;
            }
        }
        Assert.assertEquals(16, channelCounter);
    }

    @Test
    public void channelAddressTestWith1xModuleA2xUNIT8() throws Exception {
        System.out.println("channelAddressTestWith1xModuleA2xUNIT8");
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 8330, 0);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4001, 1);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE1.getChildrenAsMap().values();
        int channelCounter = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit), epicsAddressString);
                    bit = (bit+1)%8;
                }
                offset++;
            }
        }
        Assert.assertEquals(16, channelCounter);
    }
    
    @Test
    public void channelAddressTestWith8xModuleA1xUNIT16() throws Exception {
        System.out.println("channelAddressTestWith8xModuleA1xUNIT16");
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 8330, 0);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 1);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 2);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 3);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 4);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 5);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 6);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 7);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4360, 8);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE1.getChildrenAsMap().values();
        int channelCounter = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:2/%s 'T=UNSIGN16,B=%s'", offset, bit), epicsAddressString);
                    bit = (bit+1)%16;
                }
                offset+=2;
            }
        }
        Assert.assertEquals( 8*16, channelCounter);
    }
    
    @Test
    public void testOnWagoGsd1() throws Exception {
        System.out.println("testname");
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4000, 3);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4000, 15);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE1.getChildrenAsMap().values();
        int channelCounter = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println("testname :"+epicsAddressString);
                    Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit), epicsAddressString);
                    bit = (bit+1)%8;
                }
                offset+=1;
            }
        }
        Assert.assertEquals(16, channelCounter);
    }
    
    @Test
    public void testOnWagoGsd2() throws Exception {
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4001, 3);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE1, 4001, 15);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE1.getChildrenAsMap().values();
        int channelCounter = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit), epicsAddressString);
                    bit = (bit+1)%8;
                }
                offset+=1;
            }
        }
        Assert.assertEquals(32, channelCounter);
    }
    
    @Test
    public void testOnSiemensGsd1() throws Exception {
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 0, 2);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 1, 3);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 2, 4);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 15, 5);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 6);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 7);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 8);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 51, 9);
        final Collection<ModuleDBO> modules = _BUILD_SLAVE2.getChildrenAsMap().values();
        int channelCounter = 0;
        int outputOffset = 0;
        int inputOffset = 0;
        int offset = 0;
        int bit = 0;
        for (ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap().values();
            for (ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
                boolean input = false;
                for (ChannelDBO channel: channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    input = channel.isInput();
                    if(input) {
                        offset = inputOffset;
                    } else {
                        offset = outputOffset;
                    }
                    if(channelCounter<6*8||channelCounter>50) {
                        Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:56/%s 'T=UNSIGN8,B=%s'", offset, bit), epicsAddressString);
                        bit = (bit+1)%8;
                    } else {
                        Assert.assertEquals("At channel "+channelCounter++, String.format("@FCNTest:56/%s 'T=FLOAT'", offset), epicsAddressString);
                        if(input) {
                            inputOffset+=3;
                        } else {
                            outputOffset+=3;
                        }
                            
                    }
                }
                if(input) {
                    inputOffset+=1;
                } else {
                    outputOffset+=1;
                }
            }
        }
        Assert.assertEquals(67, channelCounter);
    }
    
}
