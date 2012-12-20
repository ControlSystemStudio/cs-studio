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
package org.csstudio.config.ioconfig.model.statistic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rickens Helge
 * @author $Author: $
 * @since 14.01.2011

 */
public class ProfibusStatisticGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ProfibusStatisticGenerator.class);
    private static final String LINE_END = "\r\n";
    private final StringBuilder _statistic;
    private final Map<GSDFileDBO, Integer> _gsdMasterFileMap;
    private final Map<GSDFileDBO, Integer> _gsdSlaveFileMap;
    private final Map<GSDModuleDBO, ModuleStatistcCounter> _gsdModuleMap;

    /**
     * Constructor.
     */
    public ProfibusStatisticGenerator() {
        _statistic = new StringBuilder();
        _gsdMasterFileMap = new HashMap<GSDFileDBO, Integer>();
        _gsdSlaveFileMap = new HashMap<GSDFileDBO, Integer>();
        _gsdModuleMap = new HashMap<GSDModuleDBO, ModuleStatistcCounter>();
    }

    /**
     * @param gsdFile
     */
    private void countGsdFile(@Nullable final GSDFileDBO gsdFile) {
        Integer gsdFileCounter = _gsdSlaveFileMap.get(gsdFile);
        if(gsdFileCounter == null) {
            gsdFileCounter = 0;
        }
        gsdFileCounter++;
        _gsdSlaveFileMap.put(gsdFile, gsdFileCounter);
    }

    /**
     * @param module
     */
    private void countGsdModule(@Nonnull final ModuleDBO module) {
        final GSDModuleDBO gsdModule = module.getGSDModule();
        ModuleStatistcCounter msc = _gsdModuleMap.get(gsdModule);
        if(msc == null) {
            msc = new ModuleStatistcCounter();
        }
        msc.addModule(module);
        _gsdModuleMap.put(gsdModule, msc);
    }

    /**
     * @param txtFile
     */
    public void getStatisticFile(@Nonnull final File path) throws IOException {
        final FileWriter writer = new FileWriter(path);
        writer.append(_statistic.toString());
        LOG.info("Write File: {}", path.getAbsolutePath());
        writer.close();
    }

    /**
     * @param ioc
     */
    private void iocStatisticCreator(@Nonnull final IocDBO ioc) {
        final Set<ProfibusSubnetDBO> subnets = ioc.getChildren();
        for (final ProfibusSubnetDBO subnet : subnets) {
            subnetStatisticCreator(subnet);
        }
    }

    /**
     * @param master
     */
    private void masterStatisticCreator(@Nonnull final MasterDBO master) {
        final Set<SlaveDBO> slaves = master.getChildren();
        final GSDFileDBO gsdFile = master.getGSDFile();
        if(gsdFile != null) {
            Integer masterCounter = _gsdMasterFileMap.get(gsdFile);
            if(masterCounter == null) {
                masterCounter = 0;
            }
            masterCounter++;
            _gsdMasterFileMap.put(gsdFile, masterCounter);
        }
        for (final SlaveDBO slave : slaves) {
            slaveStatisticCreator(slave);
        }
    }

    /**
     * @param facility
     */
    public void setFacility(@Nonnull final FacilityDBO facility) {
        _statistic.append("Statistik für ").append(facility.getName()).append(" vom ")
        .append(new Date()).append(LINE_END);
        final Map<Short, IocDBO> childrenAsMap = facility.getChildrenAsMap();
        int iocCounter = 0;
        for (final IocDBO node : childrenAsMap.values()) {
            final IocDBO ioc = node;
            iocCounter++;
            iocStatisticCreator(ioc);
        }
        _statistic.append("Anzahl IOC = ").append(iocCounter).append(LINE_END);
        int gsdFileCount = 0;
        int gsdModuleCount = 0;

        for (final GSDFileDBO gsdFileDBO : _gsdSlaveFileMap.keySet()) {
            final Integer count = _gsdSlaveFileMap.get(gsdFileDBO);
            _statistic.append("GSD File: \"").append(String.format("%20s", gsdFileDBO.getName()))
            .append("\" wird ").append(String.format("%3d", count))
            .append(" mal verwendet").append(LINE_END);
            gsdFileCount += count;
        }
        _statistic.append("GSD Files gesamt: ").append(gsdFileCount).append(LINE_END);
        for (final GSDModuleDBO gsdModuleDBO : _gsdModuleMap.keySet()) {
            final ModuleStatistcCounter msc = _gsdModuleMap.get(gsdModuleDBO);
            _statistic
            .append(String.format("GSD Module: \"%40s\" wird %3d mal verwendet.\tAngeschlossen: %4d von %4d Kanälen.",
                                  gsdModuleDBO.getName(),
                                  msc.getModuleCount(),
                                  msc.getUsedChannelsCount(),
                                  msc.getTotalChannelsCount())).append(LINE_END);
            gsdModuleCount += msc.getModuleCount();
        }
        _statistic.append("GSD Modules gesamt: ").append(gsdModuleCount).append(LINE_END);
    }

    /**
     * @param slave
     */
    private void slaveStatisticCreator(@Nonnull final SlaveDBO slave) {
        final Set<ModuleDBO> modules = slave.getChildren();
        countGsdFile(slave.getGSDFile());
        for (final ModuleDBO module : modules) {
            countGsdModule(module);
        }
    }

    /**
     * @param subnet
     */
    private void subnetStatisticCreator(@Nonnull final ProfibusSubnetDBO subnet) {
        final Set<MasterDBO> masters = subnet.getProfibusDPMaster();
        for (final MasterDBO master : masters) {
            masterStatisticCreator(master);
        }
    }

}
