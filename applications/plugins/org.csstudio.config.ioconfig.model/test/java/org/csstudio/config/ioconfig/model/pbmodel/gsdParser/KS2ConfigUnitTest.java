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
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.03.2011
 */
public class KS2ConfigUnitTest {

    private BufferedReader _expected;
    private int _lineNo;
    private String _eLine;
    private String _outLine;
    private BufferedReader _out;
    private boolean _debugPrint;

    @Nonnull
    private List<ModuleDBO> addNewModules(final int sortIndex, @Nonnull final SlaveDBO pk2, final int... modules) throws PersistenceException {
        int index = sortIndex;
        final List<ModuleDBO> modulesList = new ArrayList<ModuleDBO>();
        for (final int moduleNumber : modules) {
            modulesList.add(addNewModule(pk2, moduleNumber, index++));
        }
        return modulesList;
    }

    @Nonnull
    private ModuleDBO addNewModule(@Nonnull final SlaveDBO pk, final int moduleNumber, final int sortIndex) throws PersistenceException {
        final ModuleDBO mo = new ModuleDBO(pk);
        mo.setSortIndex(sortIndex);
        mo.setModuleNumber(moduleNumber);
        try {
            mo.setConfigurationData(mo.getGsdModuleModel2().getExtUserPrmDataConst());
        } catch (final Exception e) {
            sysoutDebug("hier!");
        }
        return mo;
    }

    private void buildSlave02(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 2);
        pk2.setGSDFile(GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO());

        final List<ModuleDBO> addNewModules = addNewModules(0,pk2, 6,6,6,6,6,6);
        final ModuleDBO module = addNewModules.get(addNewModules.size()-1);
        String configurationData = module.getConfigurationData();
        final int lastIndexOf = configurationData.lastIndexOf(',', configurationData.lastIndexOf(',')-1);
        configurationData = configurationData.substring(0, lastIndexOf).concat(",0x00,0x00");
        module.setConfigurationData(configurationData);
    }

    private void buildSlave06(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 6);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0,pk2, 8330, 4780, 5300, 4360, 4360, 5300, 4360, 4360, 5300, 4360, 4360, 5300, 4360);
    }

    private void buildSlave10(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 10);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0,pk2, 8330, 4780, 4550, 4550, 4550, 5300, 5300, 4360, 4360, 4360, 4360, 4360);
    }

    private void buildSlave11(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 11);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0,pk2, 8330, 4780, 5300, 5300, 5300, 5300, 4360, 4360, 4360, 4360, 4360, 4550);
    }

    private void buildSlave12(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 12);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4550, 4550, 5630, 5630, 4610, 4610);
    }

    private void buildSlave13(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 13);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4550, 5630, 5630, 5310, 4360, 4360);
    }

    private void buildSlave14(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 14);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4360, 4360, 5301, 5301, 5301, 4550, 1000, 1000, 2240, 2240, 2240, 2240, 2000, 2000);
    }

    private void buildSlave15(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 15);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 4550, 4360, 4610, 4610, 4610, 4610);
    }

    private void buildSlave16(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 16);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 5300);
    }

    private void buildSlave17(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 17);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave18(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 18);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave19(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 19);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave20(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 20);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4360, 4530, 4530, 4360, 4360, 4360, 4530, 4530, 4360, 4360, 4360);
    }

    private void buildSlave21(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 21);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave22(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 22);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave23(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 23);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave25(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 25);
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6);
    }

    private void buildSlave29(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 29);
        pk2.setGSDFile(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());
        addNewModule(pk2, 1, 0);
    }

    private void buildSlave3(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk3 = getNewSlave(ks2Master, 3);
        pk3.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk3, 8330, 5300, 4360, 4360);

    }

    private void buildSlave30(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 30);
        pk2.setGSDFile(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());
        addNewModule(pk2, 1, 0);
    }

    private void buildSlave31(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 31);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 5630, 5300, 5300, 5300, 5300, 4360, 4360, 4360, 4360, 4360, 4610, 4610);
    }

    private void buildSlave32(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 32);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 5630, 5630, 4360, 4360, 4360, 4360, 4360, 4360, 5300, 5300, 5300);
    }

    private void buildSlave33(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 33);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 5630, 5630, 5630, 4360, 4360, 4360, 4360, 4360, 5300, 5300, 5300, 5300, 5300);
    }

    private void buildSlave34(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 34);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 4360, 5310, 4610, 4610);
    }

    private void buildSlave35(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 35);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 4360, 4610, 4610, 4610, 4610);
    }

    private void buildSlave36(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 36);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330,  4780, 4550, 4550, 5630, 5630, 5630, 5310, 5311, 5310, 5311, 5310, 5311,
                       4360, 4360, 4360, 4360, 4360, 4360, 4360, 4360, 4610, 4610);
        }

    private void buildSlave37(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 37);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4550, 4550, 5630, 5630, 4610, 4610);
    }

    private void buildSlave38(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 38);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 4550, 4550, 5630, 5310, 4360, 4610, 4610, 4610, 4610);
    }

    private void buildSlave39(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 39);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4780, 4550, 5630, 5310, 5311, 5310, 4360, 4360);
    }

    private void buildSlave4(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk4 = getNewSlave(ks2Master, 4);
        pk4.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk4, 8330, 4780);
        int sortIndex = 2;
        for (int i = 0; i < 15; i++) {
            addNewModule(pk4, 4550, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk4, 4360, sortIndex++);
        }
    }

    private void buildSlave5(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk5 = getNewSlave(ks2Master, 5);
        pk5.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk5, 8330, 4780, 4780, 4780, 4550, 4550, 4360, 4550);
    }

    private void buildSlave7(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 7);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330,  4310, 4310, 4310, 4310, 5010, 5010);
        int sortIndex = 7;
        for (int i = 0; i < 7; i++) {
            addNewModule(pk2, 4540, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 2000, sortIndex++);
        }
    }

    private void buildSlave8(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 8);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 8330, 4360, 4360, 5310, 5310);
    }

    private void buildSlave9(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 9);
        pk2.setGSDFile(GSDTestFiles.DESY_MSyS_V11.getFileAsGSDFileDBO());
        addNewModules(0, pk2, 6, 9, 9, 9, 9, 9, 9);
    }

    public void debugComparePrint() {
        if(_debugPrint) {
            System.out.println("E: " + _lineNo + _eLine);
            System.out.println("C: " + _lineNo + _outLine);
        }
    }

    @Nonnull
    private SlaveDBO getNewSlave(@Nonnull final MasterDBO ks2Master, final int address) throws PersistenceException {
        final SlaveDBO slave = new SlaveDBO(ks2Master);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        slave.setStationStatus(136);
        slave.setSlaveFlag(128);

        return slave;
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _debugPrint = false;
        _expected = new BufferedReader(new FileReader("./../../../applications/plugins/org.csstudio.config.ioconfig.model/res-test/ConfigFilesInUse/KS2.xml"));
    }

    public final void sysoutDebug(@Nonnull final String msg) {
        if(_debugPrint) {
            System.out.println(msg);
        }
    }



    @After
    public void tearDown() throws Exception {
        sysoutDebug("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null && _outLine != null) {
            _eLine = _expected.readLine();
            _outLine = _out.readLine();
            _lineNo++;
            debugComparePrint();
        }
        sysoutDebug("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null) {
            _eLine = _expected.readLine();
            _lineNo++;
            sysoutDebug(_lineNo+_eLine);
        }
        _out.close();
        _expected.close();
    }

    @Test
    public void testKS2Config() throws Exception {
        final FacilityDBO ks2Facility = buildFacility();
        final IocDBO ks2Ioc = buildIoc(ks2Facility);
        final ProfibusSubnetDBO ks2Subnet = buildSubnet(ks2Ioc);
        final MasterDBO ks2Master = buildMaster(ks2Subnet);
        buildSlaves(ks2Master);
        _out = GetProfibusXmlAsBufferReader.getProfibusXmlAsBufferReader(ks2Subnet);
        _lineNo = 1;
        _eLine = _expected.readLine();
        _outLine = _out.readLine();
        while (_eLine != null && _outLine != null) {
            debugComparePrint();
            _eLine = _eLine.replaceAll(", 0", ",0");
            Assert.assertEquals("@Line "+_lineNo, _eLine.toLowerCase(), _outLine.toLowerCase());
            _eLine = _expected.readLine();
            _outLine = _out.readLine();
            _lineNo++;
        }

        if (_eLine != null || _outLine != null) {
            Assert.fail("Config files have not the same size");
        }
    }

    // CHECKSTYLE OFF: ExecutableStatement
    private void buildSlaves(@Nonnull final MasterDBO ks2Master) throws PersistenceException, IOException {
        buildSlave02(ks2Master);
        buildSlave3(ks2Master);
        buildSlave4(ks2Master);
        buildSlave5(ks2Master);
        buildSlave06(ks2Master);
        buildSlave7(ks2Master);
        buildSlave8(ks2Master);
        buildSlave9(ks2Master);
        buildSlave10(ks2Master);
        buildSlave11(ks2Master);
        buildSlave12(ks2Master);
        buildSlave13(ks2Master);
        buildSlave14(ks2Master);
        buildSlave15(ks2Master);
        buildSlave16(ks2Master);
        buildSlave17(ks2Master);
        buildSlave18(ks2Master);
        buildSlave19(ks2Master);
        buildSlave20(ks2Master);
        buildSlave21(ks2Master);
        buildSlave22(ks2Master);
        buildSlave23(ks2Master);
        buildSlave25(ks2Master);
        buildSlave29(ks2Master);
        buildSlave30(ks2Master);
        buildSlave31(ks2Master);
        buildSlave32(ks2Master);
        buildSlave33(ks2Master);
        buildSlave34(ks2Master);
        buildSlave35(ks2Master);
        buildSlave36(ks2Master);
        buildSlave37(ks2Master);
        buildSlave38(ks2Master);
        buildSlave39(ks2Master);
    }
    // CHECKSTYLE ON: ExecutableStatement

    @Nonnull
    private MasterDBO buildMaster(@Nonnull final ProfibusSubnetDBO ks2Subnet) throws PersistenceException {
        final MasterDBO ks2Master = new MasterDBO(ks2Subnet);
        ks2Master.setName("Master");
        ks2Master.setSortIndex(40);
        ks2Master.setRedundant(41);
        ks2Master.setMinSlaveInt(6);
        ks2Master.setPollTime(1000);
        ks2Master.setDataControlTime(100);
        ks2Master.setAutoclear(false);
        ks2Master.setMaxNrSlave(40);
        ks2Master.setMaxSlaveOutputLen(160);
        ks2Master.setMaxSlaveInputLen(160);
        ks2Master.setMaxSlaveDiagEntries(126);
        ks2Master.setMaxSlaveDiagLen(32);
        ks2Master.setMaxBusParaLen(128);
        ks2Master.setMaxSlaveParaLen(244);
        ks2Master
        .setMasterUserData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        return ks2Master;
    }

    @Nonnull
    private ProfibusSubnetDBO buildSubnet(@Nonnull final IocDBO ks2Ioc) throws PersistenceException {
        final ProfibusSubnetDBO ks2Subnet = new ProfibusSubnetDBO(ks2Ioc);
        ks2Subnet.setName("KS2");
        ks2Subnet.setSortIndex(1);
        ks2Subnet.setHsa(125);
        ks2Subnet.setBaudRate("6");
        ks2Subnet.setSlotTime(300);
        ks2Subnet.setMaxTsdr(150);
        ks2Subnet.setMinTsdr(11);
        ks2Subnet.setTset(1);
        ks2Subnet.setTqui(0);
        ks2Subnet.setGap(10);
        ks2Subnet.setRepeaterNumber(1);
        ks2Subnet.setTtr(300000);
        ks2Subnet.setWatchdog(1000);
        return ks2Subnet;
    }

    @Nonnull
    private IocDBO buildIoc(@Nonnull final FacilityDBO ks2Facility) throws PersistenceException {
        final IocDBO ks2Ioc = new IocDBO(ks2Facility);
        ks2Ioc.setName("kryoKS2");
        ks2Ioc.setSortIndex(0);
        return ks2Ioc;
    }

    @Nonnull
    private FacilityDBO buildFacility() {
        final FacilityDBO ks2Facility = new FacilityDBO();
        ks2Facility.setName("ColdBox 42");
        ks2Facility.setSortIndex(2);
        return ks2Facility;
    }
}
