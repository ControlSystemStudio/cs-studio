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
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSDTestFiles;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 01.07.2011
 */
public class HibernateSaveUnitTest {
    
    private static HibernateRepository _REPOSITORY;

    private FacilityDBO _facilityDBO;
    
    private GSDFileDBO _b756p33;
    
    private boolean _gsdFileExist;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        _REPOSITORY = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(_REPOSITORY);
        
    }
    @AfterClass
    public static void tearDownAfterClass() {
        Repository.close();
    }
    
    /**
     * @throws PersistenceException
     */
    public void createTestFacility() throws PersistenceException {
        _facilityDBO = new FacilityDBO();
        _facilityDBO.setName("unit Test facility");
        _facilityDBO.setSortIndex(0);
        _facilityDBO.save();
    }
    
    /**
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public IocDBO createTestIoc() throws PersistenceException {
        final IocDBO ioc = _facilityDBO.createChild();
        ioc.setName("unit test ioc");
        ioc.save();
        return ioc;
    }
    
    /**
     * @param slaveLoad
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public ModuleDBO createTestModule(@Nonnull final SlaveDBO slaveLoad) throws PersistenceException {
        final ModuleDBO module = slaveLoad.createChild();
        module.setName("unit test module");
        module.save();
        module.setNewModel(4550, "a Tester");
        return module;
    }
    
    /**
     * @param masterLoad
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public SlaveDBO createTestSlave(@Nonnull final MasterDBO masterLoad) throws PersistenceException {
        final SlaveDBO slave = masterLoad.createChild();
        slave.setName("unit test slave");
        slave.setGSDFile(_b756p33);
        slave.save();
        return slave;
    }
    
    /**
     * @param iocLoad
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public ProfibusSubnetDBO createTestSubnet(@Nonnull final IocDBO iocLoad) throws PersistenceException {
        final ProfibusSubnetDBO profibusSubnet = iocLoad.createChild();
        profibusSubnet.setName("unit test subnet");
        profibusSubnet.save();
        return profibusSubnet;
    }
    
    /**
     * @param profibusSubnetLoad
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public MasterDBO createTextMaster(@Nonnull final ProfibusSubnetDBO profibusSubnetLoad) throws PersistenceException {
        final MasterDBO master = profibusSubnetLoad.createChild();
        master.setName("unit test master");
        master.save();
        return master;
    }
    
    @Ignore
    @Test
    public void saveTest() throws Exception {
        
        // Facility
        createTestFacility();
        
        final FacilityDBO facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        assertEquals(_facilityDBO, facilityLoad);
        
        // IOC
        final IocDBO ioc = createTestIoc();
        
        final IocDBO iocLoad = testIoc(ioc);
        
        // Subnet
        final ProfibusSubnetDBO profibusSubnet = createTestSubnet(iocLoad);
        final ProfibusSubnetDBO profibusSubnetLoad = testSubnet(profibusSubnet);
        
        // Master
        final MasterDBO master = createTextMaster(profibusSubnetLoad);
        final MasterDBO masterLoad = testMaster(master);
        
        // Slave
        final SlaveDBO slave = createTestSlave(masterLoad);
        final SlaveDBO slaveLoad = testSlave(slave);
        
        // Module
        final ModuleDBO module = createTestModule(slaveLoad);
        
        testModule(module);
        
    }
    
    @Before
    public void setUp() throws Exception {
        final List<GSDFileDBO> load = _REPOSITORY.load(GSDFileDBO.class);
        if (load != null) {
            for (final GSDFileDBO gsdFileDBO : load) {
                if ("B756_P33.GSD".equals(gsdFileDBO.getName())) {
                    _b756p33 = gsdFileDBO;
                    _gsdFileExist = true;
                    break;
                }
            }
        }
        if (_b756p33 == null) {
            _b756p33 = GSDTestFiles.B756_P33.getFileAsGSDFileDBO();
            _REPOSITORY.save(_b756p33);
        }
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        if (_facilityDBO != null) {
            _REPOSITORY.removeNode(_facilityDBO);
        }
        if (!_gsdFileExist && _b756p33 != null) {
            _REPOSITORY.removeGSDFiles(_b756p33);
        }
    }
    
    /**
     * @param ioc
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public IocDBO testIoc(@Nonnull final IocDBO ioc) throws PersistenceException {
        FacilityDBO facilityLoad;
        facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        Assert.assertNotNull(facilityLoad);
        final Set<IocDBO> iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        final IocDBO iocLoad = iocSet.iterator().next();
        assertEquals(ioc, iocLoad);
        return iocLoad;
    }
    
    /**
     * @param master
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public MasterDBO testMaster(@Nonnull final MasterDBO master) throws PersistenceException {
        FacilityDBO facilityLoad;
        Set<IocDBO> iocSet;
        IocDBO iocLoad;
        Set<ProfibusSubnetDBO> profibusSubnetSet;
        ProfibusSubnetDBO profibusSubnetLoad;
        facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        Assert.assertNotNull(facilityLoad);
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        profibusSubnetLoad = profibusSubnetSet.iterator().next();
        final Set<MasterDBO> masterSet = profibusSubnetLoad.getChildren();
        assertFalse(masterSet.isEmpty());
        final MasterDBO masterLoad = masterSet.iterator().next();
        
        assertEquals(master, masterLoad);
        return masterLoad;
    }
    
    /**
     * @param module
     * @throws PersistenceException
     */
    public void testModule(@Nonnull final ModuleDBO module) throws PersistenceException {
        FacilityDBO facilityLoad;
        Set<IocDBO> iocSet;
        IocDBO iocLoad;
        Set<ProfibusSubnetDBO> profibusSubnetSet;
        ProfibusSubnetDBO profibusSubnetLoad;
        Set<MasterDBO> masterSet;
        MasterDBO masterLoad;
        SlaveDBO slaveLoad;
        Set<SlaveDBO> slaveSet;
        facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        final ModuleDBO moduleLoad = null;
        if (facilityLoad != null) {
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
            final Set<ModuleDBO> moduleSet = slaveLoad.getChildren();
            assertFalse(moduleSet.isEmpty());
            moduleSet.iterator().next();
        }
        assertEquals(module, moduleLoad);
    }
    
    /**
     * @param slave
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public SlaveDBO testSlave(@Nonnull final SlaveDBO slave) throws PersistenceException {
        FacilityDBO facilityLoad;
        Set<IocDBO> iocSet;
        IocDBO iocLoad;
        Set<ProfibusSubnetDBO> profibusSubnetSet;
        ProfibusSubnetDBO profibusSubnetLoad;
        Set<MasterDBO> masterSet;
        MasterDBO masterLoad;
        facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        Assert.assertNotNull(facilityLoad);
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        profibusSubnetLoad = profibusSubnetSet.iterator().next();
        masterSet = profibusSubnetLoad.getChildren();
        assertFalse(masterSet.isEmpty());
        masterLoad = masterSet.iterator().next();
        final Set<SlaveDBO> slaveSet = masterLoad.getChildren();
        assertFalse(slaveSet.isEmpty());
        final SlaveDBO slaveLoad = slaveSet.iterator().next();
        
        assertEquals(slave, slaveLoad);
        return slaveLoad;
    }
    
    /**
     * @param profibusSubnet
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public ProfibusSubnetDBO testSubnet(@Nonnull final ProfibusSubnetDBO profibusSubnet) throws PersistenceException {
        FacilityDBO facilityLoad;
        Set<IocDBO> iocSet;
        IocDBO iocLoad;
        facilityLoad = _REPOSITORY.load(FacilityDBO.class, _facilityDBO.getId());
        Assert.assertNotNull(facilityLoad);
        iocSet = facilityLoad.getChildren();
        assertFalse(iocSet.isEmpty());
        iocLoad = iocSet.iterator().next();
        final Set<ProfibusSubnetDBO> profibusSubnetSet = iocLoad.getChildren();
        assertFalse(profibusSubnetSet.isEmpty());
        final ProfibusSubnetDBO profibusSubnetLoad = profibusSubnetSet.iterator().next();
        
        assertEquals(profibusSubnet, profibusSubnetLoad);
        return profibusSubnetLoad;
    }
    
    
}
