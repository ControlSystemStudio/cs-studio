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

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;

/**
 * TODO (Rickens Helge) : 
 * 
 * @author Rickens Helge
 * @author $Author: $
 * @since 18.01.2011

 */
public class ModuleStatistcCounter {
    
    private Integer _moduleCount = 0;
    private Integer _totalChannelsCount = 0;
    private Integer _usedChannelsCount = 0;
    
    
    /**
     * Constructor.
     * @param gsdModule
     */
    public ModuleStatistcCounter() {
        // constructor 
    }
    
    @Nonnull
    public Integer getModuleCount() {
        return _moduleCount;
    }
    
    @Nonnull
    public Integer getTotalChannelsCount() {
        return _totalChannelsCount;
    }
    
    @Nonnull
    public Integer getUsedChannelsCount() {
        return _usedChannelsCount;
    }

    @Nonnull
    public Integer getUnusedChannelsCount() {
        return _totalChannelsCount-_usedChannelsCount;
    }
    
    /**
     * @param module
     * @throws PersistenceException 
     */
    public void addModule(@Nonnull ModuleDBO module) throws PersistenceException {
        _moduleCount++;
        Collection<ChannelStructureDBO> channelStructs = module.getChannelStructsAsMap().values();
        for (ChannelStructureDBO channelStructure : channelStructs) {
            Collection<ChannelDBO> channels = channelStructure.getChannelsAsMap().values();
            for (ChannelDBO channel : channels) {
                _totalChannelsCount++;
                String ioName = channel.getIoName();
                if(ioName != null &&  !ioName.isEmpty()) {
                    _usedChannelsCount++;                    
                }
            }
        }
    }
    
    
    
}
