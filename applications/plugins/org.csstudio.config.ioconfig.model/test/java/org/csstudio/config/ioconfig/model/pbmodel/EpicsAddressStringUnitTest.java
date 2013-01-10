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

import static org.csstudio.config.ioconfig.model.TestStructureBuilder.buildModuleChannelPrototype;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.TestStructureBuilder;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSDTestFiles;
import org.junit.After;
import org.junit.Assert;
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
    private static GSDFileDBO _B756P33;
    private static GSDFileDBO _SIEM80D1;
    private static SlaveDBO _BUILD_SLAVE1;
    private static SlaveDBO _BUILD_SLAVE2;
    private static MasterDBO _BUILD_MASTER;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Repository.injectIRepository(new DummyRepository());

        _B756P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
        _SIEM80D1 = GSDTestFiles.siem80d1.getFileAsGSDFileDBO();

        buildPrototypes4b756P33();
        buildPrototypes4siem80d1();

        final FacilityDBO buildFacility = TestStructureBuilder
                .buildFacility("FullChannelNumber Test", 1);
        final IocDBO buildIoc = TestStructureBuilder.buildIoc(buildFacility,
                                                              "FullChannelNumber Test");
        _BUILD_SUBNET = TestStructureBuilder.buildSubnet(buildIoc, "FCNTest");
        _BUILD_MASTER = TestStructureBuilder.buildMaster(_BUILD_SUBNET);
        _BUILD_SLAVE1 = TestStructureBuilder.buildSlave(_BUILD_MASTER, 2, _B756P33);
        _BUILD_SLAVE2 = TestStructureBuilder.buildSlave(_BUILD_MASTER, 56, _SIEM80D1);
    }

    private static void buildPrototypes4siem80d1() {
        GSDModuleDBO gsdModuleDBO;

        gsdModuleDBO = buildModule("FullChannelNumber Test 0", _SIEM80D1, 0);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 1", _SIEM80D1, 1);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 2", _SIEM80D1, 2);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 15", _SIEM80D1, 15);
        buildModuleChannelPrototype(0, "Test 1.0 DI0.", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(1, "Test 1.0 DI1.", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(2, "Test 1.0 DI2.", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(3, "Test 1.0 DI3.", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(4, "Test 1.0 DI4.", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(5, "Test 1.0 DI5.", DataType.UINT8, true, true, gsdModuleDBO);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 15", _SIEM80D1, 22);
        buildModuleChannelPrototype(0, "Test 1.0 AI.", DataType.FLOAT, false, true, gsdModuleDBO);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 15", _SIEM80D1, 51);
        buildModuleChannelPrototype(0, "Test 1.0 AI.", DataType.UINT8, true, false, gsdModuleDBO);
        buildModuleChannelPrototype(1, "Test 1.0 AI.", DataType.UINT8, true, false, gsdModuleDBO);
        _SIEM80D1.addGSDModule(gsdModuleDBO);

    }

    private static void buildPrototypes4b756P33() {
        GSDModuleDBO gsdModuleDBO;
        gsdModuleDBO = buildModule("FullChannelNumber Test 8330", _B756P33, 8330);
        _B756P33.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 1", _B756P33, 4000);

        buildModuleChannelPrototype(0, "Test 1.0 DI", DataType.UINT8, true, true, gsdModuleDBO);
        _B756P33.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("FullChannelNumber Test 2", _B756P33, 4001);

        buildModuleChannelPrototype(0, "Test 2.0 DI", DataType.UINT8, true, true, gsdModuleDBO);
        buildModuleChannelPrototype(1, "Test 2.1 DI", DataType.UINT8, true, true, gsdModuleDBO);
        _B756P33.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = buildModule("_b756P33 4360", _B756P33, 4360);

        buildModuleChannelPrototype(0, "DI", DataType.UINT16, true, true, gsdModuleDBO);
        _B756P33.addGSDModule(gsdModuleDBO);
    }

    @Nonnull
    private static GSDModuleDBO buildModule(@Nonnull final String moduleName,
                                            @Nonnull final GSDFileDBO gsdFile,
                                            final int moduleId) {
        GSDModuleDBO gsdModuleDBO;
        gsdModuleDBO = new GSDModuleDBO(moduleName);
        gsdModuleDBO.setGSDFile(gsdFile);
        gsdModuleDBO.setModuleId(moduleId);
        return gsdModuleDBO;
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
        for (final ModuleDBO moduleDBO : values) {
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
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel " + channelCounter++,
                                        String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit),
                                        epicsAddressString);
                    bit = (bit + 1) % 8;
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
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel " + channelCounter++,
                                        String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit),
                                        epicsAddressString);
                    bit = (bit + 1) % 8;
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
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println(epicsAddressString);
                    Assert.assertEquals("At channel " + channelCounter++,
                                        String.format("@FCNTest:2/%s 'T=UNSIGN16,B=%s'",
                                                      offset,
                                                      bit),
                                        epicsAddressString);
                    bit = (bit + 1) % 16;
                }
                offset += 2;
            }
        }
        Assert.assertEquals(8 * 16, channelCounter);
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
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    System.out.println("testname :" + epicsAddressString);
                    Assert.assertEquals("At channel " + channelCounter++,
                                        String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit),
                                        epicsAddressString);
                    bit = (bit + 1) % 8;
                }
                offset += 1;
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
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    Assert.assertEquals("At channel " + channelCounter++,
                                        String.format("@FCNTest:2/%s 'T=UNSIGN8,B=%s'", offset, bit),
                                        epicsAddressString);
                    bit = (bit + 1) % 8;
                }
                offset += 1;
            }
        }
        Assert.assertEquals(32, channelCounter);
    }

    @Test
    public void testOnSiemensGsd1() throws Exception {
        addNewModules();
        final Collection<ModuleDBO> modules = _BUILD_SLAVE2.getChildrenAsMap().values();
        int channelCounter = 0;
        int outputOffset = 0;
        int inputOffset = 0;
        int offset = 0;
        int bit = 0;
        for (final ModuleDBO moduleDBO : modules) {
            final Collection<ChannelStructureDBO> channelStrutures = moduleDBO.getChildrenAsMap()
                    .values();
            for (final ChannelStructureDBO channelStructure : channelStrutures) {
                final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap()
                        .values();
                boolean input = false;
                for (final ChannelDBO channel : channels) {
                    final String epicsAddressString = channel.getEpicsAddressString();
                    input = channel.isInput();
                    offset = input ? inputOffset : outputOffset;
                    if (channelCounter < 6 * 8 || channelCounter > 50) {
                        Assert.assertEquals("At channel " + channelCounter++,
                                            String.format("@FCNTest:56/%s 'T=UNSIGN8,B=%s'",
                                                          offset,
                                                          bit),
                                            epicsAddressString);
                        bit = (bit + 1) % 8;
                    } else {
                        Assert.assertEquals("At channel " + channelCounter++,
                                            String.format("@FCNTest:56/%s 'T=FLOAT'", offset),
                                            epicsAddressString);
                        if (input) {
                            inputOffset += 3;
                        } else {
                            outputOffset += 3;
                        }
                    }
                }
                if (input) {
                    inputOffset += 1;
                } else {
                    outputOffset += 1;
                }
            }
        }
        Assert.assertEquals(67, channelCounter);
    }

    /**
     * @throws PersistenceException
     */
    private void addNewModules() throws PersistenceException {
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 0, 2);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 1, 3);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 2, 4);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 15, 5);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 6);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 7);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 22, 8);
        TestStructureBuilder.addNewModule(_BUILD_SLAVE2, 51, 9);
    }

}
