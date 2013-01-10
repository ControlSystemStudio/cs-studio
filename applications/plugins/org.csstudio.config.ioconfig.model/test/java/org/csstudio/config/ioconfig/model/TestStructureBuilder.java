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

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.junit.Assert;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 29.08.2011
 */
public final class TestStructureBuilder {

    /**
     * Constructor.
     */
    private TestStructureBuilder() {
        // Hidden Constructor.
    }

    @Nonnull
    public static FacilityDBO buildFacility(@Nonnull final String name, final int index) {
        final FacilityDBO facility = new FacilityDBO();
        facility.setName(name);
        facility.setSortIndex(index);
        Assert.assertNotNull(facility);
        return facility;
    }

    @Nonnull
    public static IocDBO buildIoc(@Nonnull final FacilityDBO facility, @Nonnull final String name) throws PersistenceException {
        final IocDBO ioc = new IocDBO(facility);
        ioc.setName(name);
        ioc.setSortIndex(0);
        Assert.assertNotNull(ioc);
        return ioc;
    }

    @Nonnull
    public static ProfibusSubnetDBO buildSubnet(@Nonnull final IocDBO ioc, @Nonnull final String name) throws PersistenceException {
        final ProfibusSubnetDBO subnet = new ProfibusSubnetDBO(ioc);
        subnet.setName(name);
        subnet.setSortIndex(1);
        subnet.setHsa(32);
        subnet.setBaudRate("6");
        subnet.setSlotTime(300);
        subnet.setMaxTsdr(150);
        subnet.setMinTsdr(11);
        subnet.setTset(1);
        subnet.setTqui(0);
        subnet.setGap(10);
        subnet.setRepeaterNumber(1);
        subnet.setTtr(750000);
        subnet.setWatchdog(1000);
        Assert.assertNotNull(subnet);
        return subnet;
    }


    /**
     * @param xavcSubnet
     * @return
     * @throws PersistenceException
     */
    @Nonnull
    public static MasterDBO buildMaster(@Nonnull final ProfibusSubnetDBO xavcSubnet) throws PersistenceException {
        final MasterDBO master = new MasterDBO(xavcSubnet);
        master.setName("Master");
        master.setSortIndex(1);
        master.setRedundant(-1);
        master.setMinSlaveInt(6);
        master.setPollTime(1000);
        master.setDataControlTime(100);
        master.setAutoclear(false);
        master.setMaxNrSlave(32);
        master.setMaxSlaveOutputLen(200);
        master.setMaxSlaveInputLen(200);
        master.setMaxSlaveDiagEntries(126);
        master.setMaxSlaveDiagLen(32);
        master.setMaxBusParaLen(128);
        master.setMaxSlaveParaLen(244);
        master
        .setMasterUserData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
        Assert.assertNotNull(master);
        return master;
    }

    @Nonnull
    public static SlaveDBO buildSlave(@Nonnull final MasterDBO master, final int address, @Nonnull final GSDFileDBO gsdFile) throws PersistenceException {
        final SlaveDBO slave = new SlaveDBO(master);
        slave.setFdlAddress(address);
        slave.setSortIndexNonHibernate(address);
        slave.setMinTsdr(11);
        slave.setWdFact1(100);
        slave.setWdFact2(10);
        slave.setStationStatus(136);
        slave.setSlaveFlag(128);
        slave.setGSDFile(gsdFile);
        Assert.assertNotNull(slave);
        return slave;
    }

    @Nonnull
    public static ModuleDBO addNewModule(@Nonnull final SlaveDBO pk2, final int moduleNumber, final int sortIndex) throws PersistenceException {
        final ModuleDBO mo = new ModuleDBO(pk2);
        mo.setSortIndex(sortIndex);
        mo.setModuleNumber(moduleNumber);
        final GsdModuleModel2 gsdModuleModel2 = mo.getGsdModuleModel2();
        Assert.assertNotNull(gsdModuleModel2);
        mo.setConfigurationData(gsdModuleModel2.getExtUserPrmDataConst());
        mo.setNewModel(moduleNumber, "TestUser");
        return mo;
    }

    public static void buildModuleChannelPrototype(final int offset, @Nonnull final String name, @Nonnull final DataType dataType, final boolean structure, final boolean input, @Nonnull final GSDModuleDBO gsdModuleDBO) {
        ModuleChannelPrototypeDBO moduleChannelPrototype;
        moduleChannelPrototype = new ModuleChannelPrototypeDBO();
        moduleChannelPrototype.setOffset(offset);
        moduleChannelPrototype.setName(name);
        moduleChannelPrototype.setType(dataType);
        moduleChannelPrototype.setStructure(structure);
        moduleChannelPrototype.setInput(input);
        gsdModuleDBO.addModuleChannelPrototype(moduleChannelPrototype);
    }
}
