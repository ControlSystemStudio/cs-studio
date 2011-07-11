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
package org.csstudio.config.ioconfig.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSDTestFiles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) : 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 01.07.2011
 */
public class HibernateSaveUnitTest {
    
    private HibernateRepository _repository;
    private FacilityDBO _facilityDBO;
    private GSDFileDBO _b756p33;
    private boolean _gsdFileExist = false;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(_repository);
        List<GSDFileDBO> load = _repository.load(GSDFileDBO.class);
        for (GSDFileDBO gsdFileDBO : load) {
            if(gsdFileDBO.getName().equals("B756_P33.GSD")) {
                _b756p33 = gsdFileDBO;
                _gsdFileExist = true;
                break;
            }
        }
        if(_b756p33==null) {
            _b756p33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
            _repository.save(_b756p33);
        }
        
    }
    
    @Test
    public void saveTest() throws Exception {
        
        // Facility
        _facilityDBO = new FacilityDBO();
        _facilityDBO.setName("unit Test facility");
        _facilityDBO.setSortIndex(0);
        _facilityDBO.save();
        
        FacilityDBO facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        
        assertEquals(_facilityDBO, facilityLoad);
        
        // IOC
        IocDBO ioc = _facilityDBO.createChild();
        ioc.setName("unit test ioc");
        ioc.save();
        
        facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        Set<IocDBO> iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        IocDBO iocLoad = iocSet.iterator().next();
        assertEquals(ioc, iocLoad);
        
        // Subnet
        ProfibusSubnetDBO profibusSubnet = iocLoad.createChild();
        profibusSubnet.setName("unit test subnet");
        profibusSubnet.save();

        facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        Set<ProfibusSubnetDBO> profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        ProfibusSubnetDBO profibusSubnetLoad = profibusSubnetSet.iterator().next();
        
        assertEquals(profibusSubnet, profibusSubnetLoad);
        
        // Master
        MasterDBO master = profibusSubnetLoad.createChild();
        master.setName("unit test master");
        master.save();
        
        facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        profibusSubnetLoad = profibusSubnetSet.iterator().next();
        Set<MasterDBO> masterSet = profibusSubnetLoad.getChildren();
        assertFalse(masterSet.isEmpty());
        MasterDBO masterLoad = masterSet.iterator().next();
        
        assertEquals(master, masterLoad);
        
        // Slave
        SlaveDBO slave = masterLoad.createChild();
        slave.setName("unit test slave");
        slave.setGSDFile(_b756p33);
        slave.save();
        
        facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        profibusSubnetLoad = profibusSubnetSet.iterator().next();
        masterSet = profibusSubnetLoad.getChildren();
        assertFalse(masterSet.isEmpty());
        masterLoad = masterSet.iterator().next();
        Set<SlaveDBO> slaveSet = masterLoad.getChildren();
        assertFalse(slaveSet.isEmpty());
        SlaveDBO slaveLoad = slaveSet.iterator().next();
        
        assertEquals(slave, slaveLoad);
        
        // Module
        ModuleDBO module = slaveLoad.createChild();
        module.setName("unit test module");
        
        module.save();
        Map<Integer, GSDModuleDBO> gsdModules = _b756p33.getGSDModules();
        GSDModuleDBO gsdModule = _b756p33.getGSDModule(4550);
        module.setNewModel(4550, "a Tester");
        
        facilityLoad = _repository.load(FacilityDBO.class, _facilityDBO.getId());
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        profibusSubnetLoad = profibusSubnetSet.iterator().next();
        masterSet = profibusSubnetLoad.getChildren();
        assertFalse(masterSet.isEmpty());
        masterLoad = masterSet.iterator().next();
        slaveSet = masterLoad.getChildren();
        assertFalse(slaveSet.isEmpty());
        slaveLoad = slaveSet.iterator().next();
        Set<ModuleDBO> moduleSet = slaveLoad.getChildren();
        assertFalse(moduleSet.isEmpty());
        ModuleDBO moduleLoad = moduleSet.iterator().next();
        
        assertEquals(module, moduleLoad);
        
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        if(_facilityDBO!=null) {
            _repository.removeNode(_facilityDBO);
        }
        if(!_gsdFileExist&& _b756p33!=null) {
            _repository.removeGSDFiles(_b756p33);
        }
    }
    
}
