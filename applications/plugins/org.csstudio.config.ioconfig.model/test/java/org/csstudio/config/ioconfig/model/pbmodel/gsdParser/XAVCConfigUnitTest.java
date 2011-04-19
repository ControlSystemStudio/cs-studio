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
    private GSDFileDBO _SiPart;
    
    private void addNewModule(SlaveDBO pk2, int moduleNumber, int sortIndex) throws PersistenceException, IOException {
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setSortIndex(sortIndex);
        mo.setModuleNumber(moduleNumber);
        try {
            mo.setConfigurationData(mo.getGsdModuleModel2().getExtUserPrmDataConst());
        } catch (Exception e) {
            System.out.println("hier!");
        }
    }
    
    private void buildSlave05(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 5);
        
        pk2.setGSDFile(_B756_P33);
        
        addNewModule(pk2, 8330, 0);
        
        for (int i = 0; i < 7; i++) {
            addNewModule(pk2, 4360, i+1);
        }
        // TODO (hrickens) [30.03.2011]: Die sind später hinzu gekommen!
        //        for (int i = 0; i < 2; i++) {
        //        addNewModule(pk2, 5132);
        //        }
    }
    
    private void buildSlave10(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 10);
        setBIMF5861Settings(pk2);
        addNewModule(pk2, 1,0);
    }

    /**
     * @param pk2
     * @throws IOException
     */
    private void setBIMF5861Settings(SlaveDBO pk2) throws IOException {
        pk2.setGSDFile(_BIMF5861);
        pk2.setPrmUserDataByte(8, 16);
        pk2.setPrmUserDataByte(10, 17);
    }
    
    private void buildSlave11(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 11);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private void buildSlave12(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 12);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private void buildSlave13(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 13);
        
        setBIMF5861Settings(pk2);
        
        addNewModule(pk2, 1,0);
    }
    
    private void buildSlave20(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 20);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave21(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 21);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave22(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 22);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave23(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 23);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave24(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 24);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave25(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 25);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private void buildSlave26(MasterDBO xavcMaster) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(xavcMaster, 26);
        pk2.setMinTsdr(200);
        
        pk2.setGSDFile(_SiPart);
        
        addNewModule(pk2, 3,0);
    }
    
    private SlaveDBO getNewSlave(MasterDBO xavcMaster, int address) throws PersistenceException {
        SlaveDBO slave = new SlaveDBO(xavcMaster);
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
        _SiPart = GSDTestFiles.SiPart.getFileAsGSDFileDBO();
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
        FacilityDBO xavcFacility = new FacilityDBO();
        xavcFacility.setName("AMTF_XAVC");
        xavcFacility.setSortIndex(10);
        
        IocDBO xavcIoc = new IocDBO(xavcFacility);
        xavcIoc.setName("XAVC_PB");
        xavcIoc.setSortIndex(0);
        
        ProfibusSubnetDBO xavcSubnet = new ProfibusSubnetDBO(xavcIoc);
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
        
        MasterDBO xavcMaster = new MasterDBO(xavcSubnet);
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
        
        StringWriter sw = new StringWriter();
        ProfibusConfigXMLGenerator generator = new ProfibusConfigXMLGenerator();
        generator.setSubnet(xavcSubnet);
        
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
    
}
