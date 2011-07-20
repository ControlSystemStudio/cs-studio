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
import java.io.StringReader;
import java.io.StringWriter;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.03.2011
 */
//CHECKSTYLE:OFF
@Ignore
public class KS2ConfigUnitTest {

    private BufferedReader _expected;
    private int _lineNo;
    private String _eLine;
    private String _outLine;
    private BufferedReader _out;



    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _expected = new BufferedReader(new FileReader("./../../../applications/plugins/org.csstudio.config.ioconfig.model/res-test/ConfigFilesInUse/KS2.xml"));
    }

    @Test
    public void testKS2Config() throws Exception {
        final FacilityDBO ks2Facility = new FacilityDBO();
        ks2Facility.setName("ColdBox 42");
        ks2Facility.setSortIndex(2);

        final IocDBO ks2Ioc = new IocDBO(ks2Facility);
        ks2Ioc.setName("kryoKS2");
        ks2Ioc.setSortIndex(0);

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

        final StringWriter sw = new StringWriter();
        final ProfibusConfigXMLGenerator generator = new ProfibusConfigXMLGenerator();
        generator.setSubnet(ks2Subnet);

        generator.getXmlFile(sw);

        _out = new BufferedReader(new StringReader(sw.toString()));

        _lineNo = 1;
        _eLine = _expected.readLine();
        _outLine = _out.readLine();
        while (_eLine != null && _outLine != null) {
            System.out.println("E: " + _lineNo + _eLine);
            System.out.println("C: " + _lineNo + _outLine);
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

    @After
    public void setDown() throws Exception {
        System.out.println("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null && _outLine != null) {
            _eLine = _expected.readLine();
            _outLine = _out.readLine();
            _lineNo++;
            System.out.println("E: "+_lineNo+_eLine);
            System.out.println("C: "+_lineNo+_outLine);
        }
        System.out.println("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null) {
            _eLine = _expected.readLine();
            _lineNo++;
            System.out.println(_lineNo+_eLine);
        }
        _out.close();
        _expected.close();
    }

    private void buildSlave02(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 2);
        pk2.setGSDFile(GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO());

        int sortIndex = 0;

        addNewModule(pk2, 6, sortIndex++);
        addNewModule(pk2, 6, sortIndex++);
        addNewModule(pk2, 6, sortIndex++);
        addNewModule(pk2, 6, sortIndex++);
        addNewModule(pk2, 6, sortIndex++);
        final ModuleDBO module = addNewModule(pk2, 6, sortIndex++);
        String configurationData = module.getConfigurationData();
        final int lastIndexOf = configurationData.lastIndexOf(',', configurationData.lastIndexOf(',')-1);
        configurationData = configurationData.substring(0, lastIndexOf).concat(",0x00,0x00");
        module.setConfigurationData(configurationData);
    }

    private void buildSlave3(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk3 = getNewSlave(ks2Master, 3);
        pk3.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        int sortIndex = 0;

        addNewModule(pk3, 8330, sortIndex++);
        addNewModule(pk3, 5300, sortIndex++);
        addNewModule(pk3, 4360, sortIndex++);
        addNewModule(pk3, 4360, sortIndex++);

    }

    private void buildSlave4(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk4 = getNewSlave(ks2Master, 4);

        pk4.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        int sortIndex = 0;

        addNewModule(pk4, 8330, sortIndex++);
        addNewModule(pk4, 4780, sortIndex++);

        for (int i = 0; i < 15; i++) {
            addNewModule(pk4, 4550, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk4, 4360, sortIndex++);
        }

    }

    private void buildSlave5(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk5 = getNewSlave(ks2Master, 5);

        pk5.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        int sortIndex = 0;

        addNewModule(pk5, 8330, sortIndex++);

        for (int i = 0; i < 3; i++) {
            addNewModule(pk5, 4780, sortIndex++);
        }
        for (int i = 0; i < 2; i++) {
            addNewModule(pk5, 4550, sortIndex++);
        }
        addNewModule(pk5, 4360, sortIndex++);
        addNewModule(pk5, 4550, sortIndex++);
    }

    private void buildSlave06(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 6);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5300, sortIndex++);
            addNewModule(pk2, 4360, sortIndex++);
            addNewModule(pk2, 4360, sortIndex++);
        }
        addNewModule(pk2, 5300, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
    }

    private void buildSlave7(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 7);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 4310, sortIndex++);
        }
        addNewModule(pk2, 5010, sortIndex++);
        addNewModule(pk2, 5010, sortIndex++);
        for (int i = 0; i < 7; i++) {
            addNewModule(pk2, 4540, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 2000, sortIndex++);
        }
    }

    private void buildSlave8(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 8);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
    }

    private void buildSlave9(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 9);

        pk2.setGSDFile(GSDTestFiles.DESY_MSyS_V11.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex++);
        for (int i = 0; i < 6; i++) {
            addNewModule(pk2, 9, sortIndex++);
        }
    }

    private void buildSlave10(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 10);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);

        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 4550, sortIndex++);
        }
        addNewModule(pk2, 5300, sortIndex++);
        addNewModule(pk2, 5300, sortIndex++);
        for (int i = 0; i < 5; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
    }

    private void buildSlave11(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 11);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 5300, sortIndex++);
        }
        for (int i = 0; i < 5; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        addNewModule(pk2, 4550, sortIndex++);
    }

    private void buildSlave12(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 12);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave13(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 13);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
    }

    private void buildSlave14(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 14);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5301, sortIndex++);
        }
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 1000, sortIndex++);
        addNewModule(pk2, 1000, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 2240, sortIndex++);
        }
        addNewModule(pk2, 2000, sortIndex++);
        addNewModule(pk2, 2000, sortIndex++);
    }

    private void buildSlave15(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 15);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 4550, sortIndex++);
        }
        addNewModule(pk2, 4360, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 4610, sortIndex++);
        }

    }

    private void buildSlave16(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 16);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 5300, sortIndex++);
    }

    private void buildSlave17(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 17);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave18(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 18);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave19(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 19);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave20(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 20);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4530, sortIndex++);
        addNewModule(pk2, 4530, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        addNewModule(pk2, 4530, sortIndex++);
        addNewModule(pk2, 4530, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
    }

    private void buildSlave21(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 21);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave22(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 22);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave23(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 23);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave25(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 25);

        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());

        final int sortIndex = 0;
        addNewModule(pk2, 6, sortIndex);
    }

    private void buildSlave29(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 29);

        pk2.setGSDFile(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());

        addNewModule(pk2, 1, 0);
    }

    private void buildSlave30(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 30);

        pk2.setGSDFile(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());

        addNewModule(pk2, 1, 0);
    }

    private void buildSlave31(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 31);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 5300, sortIndex++);
        }
        for (int i = 0; i < 5; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave32(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 32);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        for (int i = 0; i < 6; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5300, sortIndex++);
        }
    }

    private void buildSlave33(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 33);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5630, sortIndex++);
        }
        for (int i = 0; i < 5; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        for (int i = 0; i < 5; i++) {
            addNewModule(pk2, 5300, sortIndex++);
        }

    }

    private void buildSlave34(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 34);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave35(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 35);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave36(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 36);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5630, sortIndex++);
        }
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 5310, sortIndex++);
            addNewModule(pk2, 5311, sortIndex++);
        }
        for (int i = 0; i < 8; i++) {
            addNewModule(pk2, 4360, sortIndex++);
        }
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave37(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 37);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
        addNewModule(pk2, 4610, sortIndex++);
    }

    private void buildSlave38(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 38);

        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());

        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        for (int i = 0; i < 3; i++) {
            addNewModule(pk2, 4550, sortIndex++);
        }
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        for (int i = 0; i < 4; i++) {
            addNewModule(pk2, 4610, sortIndex++);
        }
    }

    private void buildSlave39(final MasterDBO ks2Master) throws PersistenceException, IOException {
        final SlaveDBO pk2 = getNewSlave(ks2Master, 39);
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        int sortIndex = 0;
        addNewModule(pk2, 8330, sortIndex++);
        addNewModule(pk2, 4780, sortIndex++);
        addNewModule(pk2, 4550, sortIndex++);
        addNewModule(pk2, 5630, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 5311, sortIndex++);
        addNewModule(pk2, 5310, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
        addNewModule(pk2, 4360, sortIndex++);
    }



    private SlaveDBO getNewSlave(final MasterDBO ks2Master, final int address) throws PersistenceException {
        final SlaveDBO slave = new SlaveDBO(ks2Master);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        slave.setStationStatus(136);
        slave.setSlaveFlag(192);

        return slave;
    }

    private ModuleDBO addNewModule(final SlaveDBO pk, final int moduleNumber, final int sortIndex) throws PersistenceException, IOException {
        final ModuleDBO mo = new ModuleDBO(pk);
        mo.setSortIndex(sortIndex);
        mo.setModuleNumber(moduleNumber);
        try {
            mo.setConfigurationData(mo.getGsdModuleModel2().getExtUserPrmDataConst());
        } catch (final Exception e) {
            System.out.println("hier!");
        }
        return mo;
    }
}
//CHECKSTYLE:ON
