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
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.03.2011
 */
public class XAVCConfigUnitTest {
    
    private BufferedReader _expected;
    private int _lineNo;
    private String _eLine;
    private String _outLine;
    private BufferedReader _out;
    private GSDFileDBO _B756_P33;
    private GSDFileDBO _BIMF5861;
    
    private void addNewModule(SlaveDBO pk2, int moduleNumber) throws PersistenceException {
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(moduleNumber);
        mo.setConfigurationData(mo.getGsdModuleModel().getExtUserPrmDataConst());
    }
    
    private void buildSlave05(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 5);
        
        pk2.setGSDFile(_B756_P33);
        
        addNewModule(pk2, 8330);
        
        for (int i = 0; i < 7; i++) {
            addNewModule(pk2, 4361);
        }
        // TODO (hrickens) [30.03.2011]: Die sind später hinzu gekommen!
        //        for (int i = 0; i < 2; i++) {
        //        addNewModule(pk2, 5132);
        //        }
    }
    
    private void buildSlave10(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 10);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave11(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 11);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave12(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 12);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave13(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 13);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave20(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 20);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave21(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 21);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave22(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 22);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave23(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 23);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave24(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 24);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave25(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 25);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private void buildSlave26(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 26);
        
        pk2.setGSDFile(_BIMF5861);
        
        addNewModule(pk2, 0);
    }
    
    private SlaveDBO getNewSlave(MasterDBO ks2Master, int address) throws PersistenceException {
        SlaveDBO slave = new SlaveDBO(ks2Master);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        slave.setStationStatus(136);
        slave.setSlaveFlag(192);
        
        return slave;
    }
    
    @After
    public void setDown() throws Exception {
        System.out
                .println("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null && _outLine != null) {
            _eLine = _expected.readLine();
            _outLine = _out.readLine();
            _lineNo++;
            System.out.println("E: " + _lineNo + _eLine);
            System.out.println("C: " + _lineNo + _outLine);
        }
        System.out
                .println("      ---------- --------- -------- ------ ----- ---- --- -- -  -   -    -     -      -       -        -");
        while (_eLine != null) {
            _eLine = _expected.readLine();
            _lineNo++;
            System.out.println(_lineNo + _eLine);
        }
        _out.close();
        _expected.close();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _expected = new BufferedReader(new FileReader("./res-test/ConfigFiles/XAVC.xml"));
        _B756_P33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
        setProtoTypesAtB756_P33(_B756_P33);
        _BIMF5861 = GSDTestFiles.BIMF5861.getFileAsGSDFileDBO();
        setProtoTypesAtBIMF5861(_BIMF5861);
    }
    
    /**
     * @param bIMF5861
     */
    private void setProtoTypesAtBIMF5861(GSDFileDBO bIMF5861) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param b756_P33
     */
    private void setProtoTypesAtB756_P33(GSDFileDBO b756_P33) {
        // TODO Auto-generated method stub
        
    }

    @Test
    public void testXAVCConfig() throws Exception {
        FacilityDBO ks2Facility = new FacilityDBO();
        ks2Facility.setName("AMTF_XAVC");
        ks2Facility.setSortIndex(10);
        
        IocDBO ks2Ioc = new IocDBO(ks2Facility);
        ks2Ioc.setName("XAVC_PB");
        ks2Ioc.setSortIndex(0);
        
        ProfibusSubnetDBO ks2Subnet = new ProfibusSubnetDBO(ks2Ioc);
        ks2Subnet.setName("XAVC");
        ks2Subnet.setSortIndex(1);
        
        ks2Subnet.setHsa(32);
        ks2Subnet.setBaudRate("6");
        ks2Subnet.setSlotTime(300);
        ks2Subnet.setMaxTsdr(150);
        ks2Subnet.setMinTsdr(11);
        ks2Subnet.setTset(1);
        ks2Subnet.setTqui(0);
        ks2Subnet.setGap(10);
        ks2Subnet.setRepeaterNumber(1);
        ks2Subnet.setTtr(750000);
        ks2Subnet.setWatchdog(1000);
        
        MasterDBO ks2Master = new MasterDBO(ks2Subnet);
        ks2Master.setName("Master");
        ks2Master.setSortIndex(1);
        ks2Master.setRedundant(-1);
        ks2Master.setMinSlaveInt(6);
        ks2Master.setPollTime(1000);
        ks2Master.setDataControlTime(100);
        ks2Master.setAutoclear(false);
        ks2Master.setMaxNrSlave(32);
        ks2Master.setMaxSlaveOutputLen(200);
        ks2Master.setMaxSlaveInputLen(200);
        ks2Master.setMaxSlaveDiagEntries(126);
        ks2Master.setMaxSlaveDiagLen(32);
        ks2Master.setMaxBusParaLen(128);
        ks2Master.setMaxSlaveParaLen(244);
        ks2Master
                .setMasterUserData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        
        buildSlave05(ks2Master);
        buildSlave10(ks2Master);
        buildSlave11(ks2Master);
        buildSlave12(ks2Master);
        buildSlave13(ks2Master);
        buildSlave20(ks2Master);
        buildSlave21(ks2Master);
        buildSlave22(ks2Master);
        buildSlave23(ks2Master);
        buildSlave24(ks2Master);
        buildSlave25(ks2Master);
        buildSlave26(ks2Master);
        
        StringWriter sw = new StringWriter();
        ProfibusConfigXMLGenerator generator = new ProfibusConfigXMLGenerator();
        generator.setSubnet(ks2Subnet);
        
        generator.getXmlFile(sw);
        
        _out = new BufferedReader(new StringReader(sw.toString()));
        
        _lineNo = 1;
        _eLine = _expected.readLine();
        _outLine = _out.readLine();
        while (_eLine != null && _outLine != null) {
            System.out.println("E: " + _lineNo + _eLine);
            System.out.println("C: " + _lineNo + _outLine);
            _eLine = _eLine.replaceAll(", 0", ", 0");
            Assert.assertEquals(_eLine, _outLine);
            _eLine = _expected.readLine();
            _outLine = _out.readLine();
            _lineNo++;
        }
        
        if (_eLine != null || _outLine != null) {
            Assert.fail("Config files have not the same size");
        }
    }
    
}
