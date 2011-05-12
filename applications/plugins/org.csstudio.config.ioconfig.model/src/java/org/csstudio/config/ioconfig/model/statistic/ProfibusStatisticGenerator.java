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

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author Rickens Helge
 * @author $Author: $
 * @since 14.01.2011

 */
public class ProfibusStatisticGenerator {
    
    private final StringBuilder _statistic;
    private final Map<GSDFileDBO, Integer> _gsdMasterFileMap;
    private final Map<GSDFileDBO, Integer> _gsdSlaveFileMap;
    private final Map<GSDModuleDBO, ModuleStatistcCounter> _gsdModuleMap;
    private static final String LINE_END = "\r\n";
    
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
     * @param facility
     * @throws PersistenceException 
     */
    public void setFacility(@Nonnull FacilityDBO facility) throws PersistenceException {
        _statistic.append("Statistik für ").append(facility.getName()).append(" vom ").append(new Date()).append(LINE_END);
        Map<Short, ? extends AbstractNodeDBO> childrenAsMap = facility.getChildrenAsMap();
        int iocCounter = 0;
        for (AbstractNodeDBO node : childrenAsMap.values()) {
            if (node instanceof IocDBO) {
                IocDBO ioc = (IocDBO) node;
                iocCounter++;
                iocStatisticCreator(ioc);
                
            }
            
        }
        _statistic.append("Anzahl IOC = ").append(iocCounter).append(LINE_END);
        int gsdFileCount = 0;
        int gsdModuleCount = 0;
        
        for (GSDFileDBO gsdFileDBO : _gsdSlaveFileMap.keySet()) {
            Integer count = _gsdSlaveFileMap.get(gsdFileDBO);
            _statistic.append("GSD File: \"").append(String.format("%20s", gsdFileDBO.getName())).append("\" wird ").append(String.format("%3d",count)).append(" mal verwendet").append(LINE_END);
            gsdFileCount += count;
        }
        _statistic.append("GSD Files gesamt: ").append(gsdFileCount).append(LINE_END);
        for (GSDModuleDBO gsdModuleDBO : _gsdModuleMap.keySet()) {
            ModuleStatistcCounter msc = _gsdModuleMap.get(gsdModuleDBO);
            _statistic.append(String.format("GSD Module: \"%40s\" wird %3d mal verwendet.\tAngeschlossen: %4d von %4d Kanälen.",gsdModuleDBO.getName(),msc.getModuleCount(),msc.getUsedChannelsCount(),msc.getTotalChannelsCount())).append(LINE_END);
            gsdModuleCount += msc.getModuleCount();
        }
        _statistic.append("GSD Modules gesamt: ").append(gsdModuleCount).append(LINE_END);
    }
    
    /**
     * @param ioc
     * @throws PersistenceException 
     */
    private void iocStatisticCreator(@Nonnull IocDBO ioc) throws PersistenceException {
        Set<ProfibusSubnetDBO> subnets = ioc.getChildren();
        for (ProfibusSubnetDBO subnet: subnets) {
            subnetStatisticCreator(subnet);
        }
    }

    /**
     * @param subnet
     * @throws PersistenceException 
     */
    private void subnetStatisticCreator(@Nonnull ProfibusSubnetDBO subnet) throws PersistenceException {
        Set<MasterDBO> masters = subnet.getProfibusDPMaster();
        for (MasterDBO master : masters) {
            masterStatisticCreator(master);
        }
    }

    /**
     * @param master
     * @throws PersistenceException 
     */
    private void masterStatisticCreator(@Nonnull MasterDBO master) throws PersistenceException {
        Set<SlaveDBO> slaves = master.getChildren();
        GSDFileDBO gsdFile = master.getGSDFile();
        if (gsdFile != null) {
            Integer masterCounter = _gsdMasterFileMap.get(gsdFile);
            if (masterCounter == null) {
                masterCounter = 0;
            }
            masterCounter++;
            _gsdMasterFileMap.put(gsdFile, masterCounter);
        }
        for (SlaveDBO slave : slaves) {
            slaveStatisticCreator(slave);
        }
    }

    /**
     * @param slave
     * @throws PersistenceException 
     */
    private void slaveStatisticCreator(@Nonnull SlaveDBO slave) throws PersistenceException {
        Set<ModuleDBO> modules = slave.getChildren();
        countGsdFile(slave.getGSDFile());
        for (ModuleDBO module : modules) {
            countGsdModule(module);
        }
    }

    /**
     * @param gsdFile
     */
    private void countGsdFile(@Nullable GSDFileDBO gsdFile) {
        Integer gsdFileCounter = _gsdSlaveFileMap.get(gsdFile);
        if(gsdFileCounter==null) {
            gsdFileCounter = 0;
        }
        gsdFileCounter++;
        _gsdSlaveFileMap.put(gsdFile, gsdFileCounter);
    }

    /**
     * @param module
     * @throws PersistenceException 
     */
    private void countGsdModule(@Nonnull ModuleDBO module) throws PersistenceException {
        GSDModuleDBO gsdModule = module.getGSDModule();
        ModuleStatistcCounter msc = _gsdModuleMap.get(gsdModule);
        if(msc==null) {
            msc = new ModuleStatistcCounter();
        }
        msc.addModule(module);
        _gsdModuleMap.put(gsdModule, msc);
    }

    
    /**
     * @param txtFile
     * @throws IOException 
     */
    public void getStatisticFile(@Nonnull File path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.append(_statistic.toString());
        CentralLogger.getInstance().info(this, "Write File:" + path.getAbsolutePath());
        writer.close();
    }

    
}
