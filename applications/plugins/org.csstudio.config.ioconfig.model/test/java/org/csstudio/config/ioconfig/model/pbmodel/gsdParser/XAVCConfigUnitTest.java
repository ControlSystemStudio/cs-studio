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
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.TestStructureBuilder;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.domain.common.resource.CssResourceLocator;
import org.csstudio.domain.common.resource.CssResourceLocator.RepoDomain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.03.2011
 */
public class XAVCConfigUnitTest {
    
    private static BufferedReader _EXPECTED_CONFIG;
    private static BufferedReader _EXPECTED_INPUT_ADDRESS;
    private static BufferedReader _EXPECTED_OUTPUT_ADDRESS;
    private static int _lineNo;
    private static String _eLine;
    private static String _outLine;
    private static BufferedReader _out;
    private static GSDFileDBO _b756P33;
    private static GSDFileDBO _bIMF5861;
    private static GSDFileDBO _siPart;
    private static boolean _debugPrint = true;
    private static ProfibusSubnetDBO _xavcSubnet;
    private static ArrayList<ChannelDBO> _INPUT_CHANNELS;
    private static ArrayList<ChannelDBO> _OUTPUT_CHANNELS;
    private static int _MODULE_COUNT;
    
    private static void addNewModule(@Nonnull final SlaveDBO pk2, final int moduleNumber, final int sortIndex) throws PersistenceException, IOException {
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setSortIndex(sortIndex);
        mo.setModuleNumber(moduleNumber);
        GsdModuleModel2 gsdModuleModel2 = mo.getGsdModuleModel2();
        Assert.assertNotNull(gsdModuleModel2);
        mo.setConfigurationData(gsdModuleModel2.getExtUserPrmDataConst());
        mo.setNewModel(moduleNumber, "TestUser");
        mo.setName("Module("+_MODULE_COUNT+++")["+moduleNumber+"]: "+mo.getInputOffsetNH()+" / "+mo.getOutputOffsetNH());
        Collection<ChannelStructureDBO> channelStructures = mo.getChildrenAsMap().values();
        for (ChannelStructureDBO channelStructureDBO : channelStructures) {
            Collection<ChannelDBO> channels = channelStructureDBO.getChildrenAsMap().values();
            for (ChannelDBO channelDBO : channels) {
                if(channelDBO.isInput()) {
                    _INPUT_CHANNELS.add(channelDBO);
                } else {
                    _OUTPUT_CHANNELS.add(channelDBO);
                }
            }
        }
    }
    
    /**
     * @param xavcFacility
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public static IocDBO buildIoc(@Nonnull final FacilityDBO xavcFacility) throws PersistenceException {
        final IocDBO xavcIoc = new IocDBO(xavcFacility);
        xavcIoc.setName("XAVC_PB");
        xavcIoc.setSortIndex(0);
        Assert.assertNotNull(xavcIoc);
        return xavcIoc;
    }
    
    /**
     * @param xavcSubnet
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public static MasterDBO buildMaster(@Nonnull final ProfibusSubnetDBO xavcSubnet) throws PersistenceException {
        final MasterDBO xavcMaster = new MasterDBO(xavcSubnet);
        xavcMaster.setName("Master");
        xavcMaster.setSortIndex(1);
        xavcMaster.setRedundant(-1);
        xavcMaster.setMinSlaveInt(6);
        xavcMaster.setPollTime(1000);
        xavcMaster.setDataControlTime(100);
        xavcMaster.setAutoclear(false);
        xavcMaster.setMaxNrSlave(32);
        xavcMaster.setMaxSlaveOutputLen(200);
        xavcMaster.setMaxSlaveInputLen(200);
        xavcMaster.setMaxSlaveDiagEntries(126);
        xavcMaster.setMaxSlaveDiagLen(32);
        xavcMaster.setMaxBusParaLen(128);
        xavcMaster.setMaxSlaveParaLen(244);
        xavcMaster
        .setMasterUserData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        Assert.assertNotNull(xavcMaster);
        return xavcMaster;
    }
    
    private static void buildSlave05(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 5);
        
        pk2.setGSDFile(_b756P33);
        
        addNewModule(pk2, 8330, 0);
        for (int i = 0; i < 7; i++) {
            addNewModule(pk2, 4360, i+1);
        }
        // (hrickens) [30.03.2011]: Die sind später hinzu gekommen!
        //        for (int i = 0; i < 2; i++) {
        //        addNewModule(pk2, 5132);
        //        }
    }
    
    private static void buildSlave10(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 10);
        setBIMF5861Settings(pk2);
        addNewModule(pk2, 1,0);
    }
    
    private static void buildSlave11(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 11);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private static void buildSlave12(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 12);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private static void buildSlave13(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 13);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private static void buildSlave20(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 20);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave21(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 21);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave22(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 22);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave23(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 23);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave24(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 24);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave25(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 25);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private static void buildSlave26(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(xavcMaster, 26);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_siPart);
        
        addNewModule(pk2, 3,0);
    }
    
    /**
     * @param xavcMaster
     * @throws PersistenceException
     * @throws IOException
     */
    public static void buildSlaves(@Nonnull final MasterDBO xavcMaster) throws PersistenceException, IOException {
        buildSlave05(xavcMaster);
        buildSlave10(xavcMaster);
        buildSlave11(xavcMaster);
        buildSlave12(xavcMaster);
        buildSlave13(xavcMaster);
        buildSlave20(xavcMaster);
        buildSlave21(xavcMaster);
        buildSlave22(xavcMaster);
        buildSlave23(xavcMaster);
        buildSlave24(xavcMaster);
        buildSlave25(xavcMaster);
        buildSlave26(xavcMaster);
    }
    
    /**
     * @param xavcIoc
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public static ProfibusSubnetDBO buildSubnet(@Nonnull final IocDBO xavcIoc) throws PersistenceException {
        final ProfibusSubnetDBO xavcSubnet = new ProfibusSubnetDBO(xavcIoc);
        xavcSubnet.setName("XAVC");
        xavcSubnet.setSortIndex(1);
        
        xavcSubnet.setHsa(32);
        xavcSubnet.setBaudRate("6");
        xavcSubnet.setSlotTime(300);
        xavcSubnet.setMaxTsdr(150);
        xavcSubnet.setMinTsdr(11);
        xavcSubnet.setTset(1);
        xavcSubnet.setTqui(0);
        xavcSubnet.setGap(10);
        xavcSubnet.setRepeaterNumber(1);
        xavcSubnet.setTtr(750000);
        xavcSubnet.setWatchdog(1000);
        Assert.assertNotNull(xavcSubnet);
        return xavcSubnet;
    }
    
    public static void debugComparePrint() {
        if(_debugPrint) {
            System.out.println("E: " + _lineNo + _eLine);
            System.out.println("C: " + _lineNo + _outLine);
        }
    }
    
    @Nonnull
    private static SlaveDBO getNewSlave(@Nonnull final MasterDBO xavcMaster, final int address) throws PersistenceException {
        final SlaveDBO slave = new SlaveDBO(xavcMaster);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        slave.setStationStatus(136);
        slave.setSlaveFlag(192);
        Assert.assertNotNull(slave);
        return slave;
    }
    
    /**
     * @param pk2
     * @throws IOException
     */
    private static void setBIMF5861Settings(@Nonnull final SlaveDBO pk2) throws IOException {
        pk2.setGSDFile(_bIMF5861);
        pk2.setPrmUserDataByte(8, 16);
        pk2.setPrmUserDataByte(10, 17);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Repository.injectIRepository(new DummyRepository());
        
        final String resFilePath =
            CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                             IOConfigActivator.PLUGIN_ID,
                                                             "res-test/ConfigFiles/XAVC.xml");
        final String inputAdrFilePath =
            CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                             IOConfigActivator.PLUGIN_ID,
                                                             "res-test/EPICSAddressFiles/XAVCInputAdr.txt");
        final String outputAdrFilePath =
            CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                             IOConfigActivator.PLUGIN_ID,
                                                             "res-test/EPICSAddressFiles/XAVCOutputAdr.txt");
        _EXPECTED_CONFIG = new BufferedReader(new FileReader(resFilePath));
        _EXPECTED_INPUT_ADDRESS = new BufferedReader(new FileReader(inputAdrFilePath));
        _EXPECTED_OUTPUT_ADDRESS = new BufferedReader(new FileReader(outputAdrFilePath));
        _b756P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
        _bIMF5861 = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO();
        _siPart = GSDTestFiles.SiPart.getFileAsGSDFileDBO();
        _INPUT_CHANNELS = new ArrayList<ChannelDBO>();
        _OUTPUT_CHANNELS = new ArrayList<ChannelDBO>();
        
        buildPrototypes();
        
        final FacilityDBO xavcFacility = TestStructureBuilder.buildFacility("AMTF_XAVC", 10);
        final IocDBO xavcIoc = buildIoc(xavcFacility);
        _xavcSubnet = buildSubnet(xavcIoc);
        final MasterDBO xavcMaster = buildMaster(_xavcSubnet);
        buildSlaves(xavcMaster);
    }
    
    /**
     * 
     */
    private static void buildPrototypes() {
        // Prototypes for _b756P33
        
        GSDModuleDBO gsdModuleDBO = new GSDModuleDBO("_b756P33 4360");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(4360);
        
        ModuleChannelPrototypeDBO moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("DI");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        _b756P33.addGSDModule(gsdModuleDBO);

        gsdModuleDBO = new GSDModuleDBO("_b756P33 8330");
        gsdModuleDBO.setGSDFile(_b756P33);
        gsdModuleDBO.setModuleId(8330);
        _b756P33.addGSDModule(gsdModuleDBO);

        // Prototypes for _bIMF5861
        gsdModuleDBO = new GSDModuleDBO("_bIMF5861 3");
        gsdModuleDBO.setGSDFile(_bIMF5861);
        gsdModuleDBO.setModuleId(1);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("flow");
        moduleChannelPrototype.setType(DataType.FLOAT);
        moduleChannelPrototype.setStructure(false);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setOffset(4);
        moduleChannelPrototype.setName("temperature");
        moduleChannelPrototype.setType(DataType.FLOAT);
        moduleChannelPrototype.setStructure(false);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setOffset(8);
        moduleChannelPrototype.setName("totalizer");
        moduleChannelPrototype.setType(DataType.FLOAT);
        moduleChannelPrototype.setStructure(false);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setInput(true);
        moduleChannelPrototype.setOffset(12);
        moduleChannelPrototype.setName("CMDin");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setInput(false);
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("CMDout");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        _bIMF5861.addGSDModule(gsdModuleDBO);
        
        // Prototypes for _siPart
        gsdModuleDBO = new GSDModuleDBO("_siPart 3");
        gsdModuleDBO.setGSDFile(_siPart);
        gsdModuleDBO.setModuleId(3);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(0);
        moduleChannelPrototype.setName("Readback");
        moduleChannelPrototype.setType(DataType.DS33);
        moduleChannelPrototype.setStructure(false);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);

        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(5);
        moduleChannelPrototype.setName("Diskrete Position");
        moduleChannelPrototype.setType(DataType.DS33_1);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(7);
        moduleChannelPrototype.setName("Checkback 0");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(8);
        moduleChannelPrototype.setName("Checkback 1");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        moduleChannelPrototype.setInput(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(9);
        moduleChannelPrototype.setName("Checkback 2");
        moduleChannelPrototype.setType(DataType.UINT8);
        moduleChannelPrototype.setStructure(true);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
        moduleChannelPrototype.setInput(true);
        _siPart.addGSDModule(gsdModuleDBO);
    }

    public static final void sysoutDebug(@Nonnull final String msg) {
        if(_debugPrint) {
            System.out.println(msg);
        }
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        sysoutDebug("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null && _outLine != null) {
            _eLine = _EXPECTED_CONFIG.readLine();
            _outLine = _out.readLine();
            _lineNo++;
            debugComparePrint();
        }
        sysoutDebug("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null) {
            _eLine = _EXPECTED_CONFIG.readLine();
            _lineNo++;
            sysoutDebug(_lineNo + _eLine);
        }
        _out.close();
        _EXPECTED_CONFIG.close();
        _EXPECTED_INPUT_ADDRESS.close();
        _EXPECTED_OUTPUT_ADDRESS.close();
    }
    
    @Test
    public void testXAVCConfig() throws Exception {
        
        _out = GetProfibusXmlAsBufferReader.getProfibusXmlAsBufferReader(_xavcSubnet);
        _lineNo = 1;
        _eLine = _EXPECTED_CONFIG.readLine();
        _outLine = _out.readLine();
        while (_eLine != null && _outLine != null) {
            debugComparePrint();
            _eLine = _eLine.replaceAll(", 0", ",0");
            
            Assert.assertEquals("@Line "+_lineNo, _eLine.toLowerCase(), _outLine.toLowerCase());
            _eLine = _EXPECTED_CONFIG.readLine();
            _outLine = _out.readLine();
            _lineNo++;
        }
        
        if (_eLine != null || _outLine != null) {
            Assert.fail("Config files have not the same size! Stop at line "+_lineNo);
        }
    }
    
    @Test
    public void testEPICSInputAddress() throws Exception {
        int channelCounter = 0;
        ArrayList<ChannelDBO> _INPUT_CHANNELS2 = _INPUT_CHANNELS;
        for (ChannelDBO channelDBO : _INPUT_CHANNELS2) {
            final String epicsAddressString = channelDBO.getEpicsAddressString().trim();
            String readLine = _EXPECTED_INPUT_ADDRESS.readLine();
            if(readLine!=null) {
                readLine = readLine.trim();
            }
            Assert.assertEquals("miss on channel number "+channelCounter+++" -> "+channelDBO+" --> "+channelDBO.getModule(),readLine, epicsAddressString);
        }
    }
    
    @Test
    public void testEPICSOutputAddress() throws Exception {
        int channelCounter = 0;
        for (ChannelDBO channelDBO : _OUTPUT_CHANNELS) {
            final String epicsAddressString = channelDBO.getEpicsAddressString().trim();
            String readLine = _EXPECTED_OUTPUT_ADDRESS.readLine();
            if(readLine!=null) {
                readLine = readLine.trim();
            }
            Assert.assertEquals("miss on channel number "+channelCounter+++" -> "+channelDBO+" --> "+channelDBO.getModule()+channelCounter++,readLine, epicsAddressString);
        }
    }
}
