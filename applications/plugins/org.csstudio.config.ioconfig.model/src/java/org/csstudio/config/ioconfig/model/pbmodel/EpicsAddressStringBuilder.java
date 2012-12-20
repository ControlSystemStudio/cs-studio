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
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.PersistenceException;

/**
 *
 * Calculate the EPICS Address String for a Channel.
 * The ChannelType and the StatusAddressOffset was set when it changed.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 03.08.2011
 */
public final class EpicsAddressStringBuilder {


    private final ChannelDBO _channelDBO;

    /**
     * Constructor.
     */
    public EpicsAddressStringBuilder(@Nonnull final ChannelDBO channelDBO) {
        _channelDBO = channelDBO;
    }

    /**
     * Calculate the EPICS Address String for a Channel.
     * The ChannelType and the StatusAddressOffset of the channel was <b>set</b> when it changed!
     */
    @Nonnull
    public String getEpicsAddressString() throws PersistenceException {
        final StringBuilder sb = new StringBuilder(_channelDBO.getModule().getEpicsAddressString());
        sb.append("/");
        sb.append(_channelDBO.getFullChannelNumber());
        if (_channelDBO.getStatusAddressOffset() >= 0) {
            sb.append("/");
            sb.append(_channelDBO.getStatusAddress());
        }
        assembleEpicsAddressType(sb);
        sb.append("'");
        return sb.toString();
    }

    private void assembleEpicsAddressType(@Nonnull final StringBuilder sb) throws PersistenceException {
        sb.append(" 'T=");
        final GSDModuleDBO gsdModule = _channelDBO.getModule().getGSDModule();
        if (gsdModule != null) {
            final Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes = gsdModule
                                                                                   .getModuleChannelPrototypeNH();
            for (final ModuleChannelPrototypeDBO moduleChannelPrototype : moduleChannelPrototypes) {
                if (moduleChannelPrototype != null
                    && moduleChannelPrototype.isInput() == _channelDBO.isInput()
                    && _channelDBO.getChannelNumber() == moduleChannelPrototype.getOffset()) {
                    setChannelType(moduleChannelPrototype);
                    appendDataType(sb, moduleChannelPrototype);
                    _channelDBO.setStatusAddressOffset(moduleChannelPrototype.getShift());
                    appendMinimum(sb, moduleChannelPrototype);
                    appendMaximum(sb, moduleChannelPrototype);
                    appendByteOdering(sb, moduleChannelPrototype);
                }
            }
        }
    }

    private void setChannelType(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) throws PersistenceException {
        if (_channelDBO.getParent().isSimple()) {
            _channelDBO.setChannelTypeNonHibernate(moduleChannelPrototype.getType());
        } else {
            _channelDBO.setChannelTypeNonHibernate(moduleChannelPrototype.getType().getStructure()[0]);
        }
    }

    private void appendByteOdering(@Nonnull final StringBuilder sb,
                                   @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        final Integer byteOrdering = moduleChannelPrototype.getByteOrdering();
        if (byteOrdering !=null && byteOrdering > 0) {
            sb.append(",O=" + byteOrdering);
        }
    }

    private void appendDataType(@Nonnull final StringBuilder sb,
                                @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if (_channelDBO.getChannelType() == DataType.BIT && !_channelDBO.getParent().isSimple()) {
            sb.append(_channelDBO.getParent().getStructureType().getType());
            sb.append(_channelDBO.getBitPostion());
        } else {
            sb.append(moduleChannelPrototype.getType().getType());
        }
    }

    private void appendMaximum(@Nonnull final StringBuilder sb,
                               @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if (moduleChannelPrototype.getMaximum() != null) {
            sb.append(",H=" + moduleChannelPrototype.getMaximum());
        }
    }

    private void appendMinimum(@Nonnull final StringBuilder sb,
                               @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if (moduleChannelPrototype.getMinimum() != null) {
            sb.append(",L=" + moduleChannelPrototype.getMinimum());
        }
    }


}
