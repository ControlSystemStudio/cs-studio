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
public class KS2ConfigUnitTest {
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testKS2Config() throws Exception {
        FacilityDBO ks2Facility = new FacilityDBO();
        ks2Facility.setName("ColdBox 42");
        ks2Facility.setSortIndex(2);
        
        IocDBO ks2Ioc = new IocDBO(ks2Facility);
        ks2Ioc.setName("kryoKS2");
        ks2Ioc.setSortIndex(0);
        
        ProfibusSubnetDBO ks2Subnet = new ProfibusSubnetDBO(ks2Ioc);
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
        
        MasterDBO ks2Master = new MasterDBO(ks2Subnet);
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
        
        buildSlave2(ks2Master);
        buildSlave3(ks2Master);
        buildSlave4(ks2Master);
        buildSlave5(ks2Master);
        buildSlave6(ks2Master);
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
        
        StringWriter sw = new StringWriter();
        ProfibusConfigXMLGenerator generator = new ProfibusConfigXMLGenerator();
        generator.setSubnet(ks2Subnet);
        
        generator.getXmlFile(sw);
        
        System.out.println(sw.toString());
        
        BufferedReader expected = new BufferedReader(new FileReader("./res-test/ConfigFilesInUse/KS2.xml"));
        BufferedReader out = new BufferedReader(new StringReader(sw.toString()));
        
        int lineNo = 1;
        String eLine = expected.readLine();
        String outLine = out.readLine();
        while (eLine != null && outLine != null) {
            System.out.println(lineNo+eLine);
            eLine = eLine.replaceAll(", 0", ", 0");
            Assert.assertEquals(eLine, outLine);
            eLine = expected.readLine();
            outLine = out.readLine();
            lineNo++;
        }
        
        if (eLine != null || outLine != null) {
            Assert.fail("Config files have not the same size");
        }
    }

    private void buildSlave2(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 2);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.DESY_MSyS_V10.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
        
    }

    private void buildSlave3(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 3);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }

    private void buildSlave4(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 4);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }

    private void buildSlave5(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 5);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave6(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 6);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave7(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 7);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave8(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 8);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }

    private void buildSlave9(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 9);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave10(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 10);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave11(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 11);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave12(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 12);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave13(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 13);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave14(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 14);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave15(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 15);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave16(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 16);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave17(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 17);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave18(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 18);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave19(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 19);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave20(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 20);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave21(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 21);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave22(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 22);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave23(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 23);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave25(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 25);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave29(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 29);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.PF009A8.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave30(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 30);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave31(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 31);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave32(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 32);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave33(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 33);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave34(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 34);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave35(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 35);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave36(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 36);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.B756_P33.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave37(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 37);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave38(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 38);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    
    private void buildSlave39(MasterDBO ks2Master) throws PersistenceException, IOException {
        SlaveDBO pk2 = getNewSlave(ks2Master, 39);
        //        pk2.setFdlAddress(fdlAddress) // TODO (hrickens) [29.03.2011]:
        pk2.setGSDFile(GSDTestFiles.YP0206CA.getFileAsGSDFileDBO());
        
        ModuleDBO mo = new ModuleDBO(pk2);
        mo.setModuleNumber(6);
    }
    

    
    private SlaveDBO getNewSlave(MasterDBO ks2Master, int address) throws PersistenceException {
        SlaveDBO slave = new SlaveDBO(ks2Master);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        return slave;
    }
    
}
