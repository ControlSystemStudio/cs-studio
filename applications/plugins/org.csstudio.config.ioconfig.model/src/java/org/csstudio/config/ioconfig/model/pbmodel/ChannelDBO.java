/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
/*
 * $Id: Channel.java,v 1.10 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.InvalidLeave;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.annotations.BatchSize;

/**
 *
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.10 $
 * @since 21.03.2007
 */
@Entity
@BatchSize(size=32)
@Table(name = "ddb_Profibus_Channel")
public class ChannelDBO extends AbstractNodeDBO<ChannelStructureDBO, InvalidLeave> {

    private static final long serialVersionUID = 1L;

    private int _channelNumber;

    private boolean _input;

    private boolean _digital;

    private String _ioName;

    private String _currenUserParamDataIndex;

    private String _currentValue;

    private int _statusAddressOffset;

    private int _channelType;

    private String _epicsAdress;

    private boolean _isUpdated;

    /**
     * This Constructor is only used by Hibernate. To create an new {@link ChannelDBO}
     * {@link #Channel(ChannelStructureDBO, boolean, boolean)} or
     * {@link #Channel(ChannelStructureDBO, String, boolean, boolean, short)} or
     */
    public ChannelDBO() {
        // Constructor for Hibernate
    }

    /**
     * Generate a new Pure Channel on the parent Channel Structure. The Channel get the first free
     * Station Address. The max Station Address is {@link ChannelDBO}
     * {@value #DEFAULT_MAX_STATION_ADDRESS}
     *
     * @param channelStructure
     *            the parent Channel Structure.
     *
     * @param input
     *            only if true then is the channel a Input otherwise a Output channel.
     * @param digital
     *            only if true then is the channel a Digital otherwise a Analog channel.
     * @throws PersistenceException 
     */
    public ChannelDBO(@Nonnull final ChannelStructureDBO channelStructure, final boolean input, final boolean digital) throws PersistenceException {
        this(channelStructure, null, input, digital, (short) -1);
    }

    /**
     * Generate a new Pure Channel on the parent Channel Structure. The Channel get the first free
     * Station Address. The max Station Address is {@link ChannelDBO}
     * {@value #DEFAULT_MAX_STATION_ADDRESS}
     *
     * @param channelStructure
     *            the parent Channel Structure.
     *
     * @param name
     *            the name of this Channel.
     *
     * @param input
     *            only if true then is the channel a Input otherwise a Output channel.
     * @param digital
     *            only if true then is the channel a Digital otherwise a Analog channel.
     * @param sortIndex
     *            the sort posiotion for this Channel.
     * @throws PersistenceException 
     */
    public ChannelDBO(@Nonnull final ChannelStructureDBO channelStructure, @Nonnull final String name, final boolean input, final boolean digital,
            final int sortIndex) throws PersistenceException {
        setName(name);
        setInput(input);
        setDigital(digital);
        setParent(channelStructure);
        setSortIndex(sortIndex);
        channelStructure.addChild(this);
        localUpdate();
    }

    /**
     *
     * @return The Channel number inclusive offset.
     * @throws PersistenceException 
     */
    @Transient
    public int getFullChannelNumber() throws PersistenceException {
        int value = 0;
        if (isInput()) {
            value = getModule().getInputOffsetNH();
        } else {
            value = getModule().getOutputOffsetNH();
        }
        return value + _channelNumber;
    }

    /**
     *
     * @return the Channel Number.
     */
    public int getChannelNumber() {
        return _channelNumber;
    }

    /**
     * Only used from Hibernate or Internal! The Channel number are automatic set when the sortIndex
     * are set.
     *
     * @param channelNumber
     *            the channel start Address.
     */
    public void setChannelNumber(final int channelNumber) {
        this._channelNumber = channelNumber;
    }

    /**
     *
     * @return is only true when this Channel is an Input.
     */
    public boolean isInput() {
        return _input;
    }

    /**
     *
     * @param input
     *            this channel as Input.
     */
    public void setInput(final boolean input) {
        this._input = input;
    }

    /**
     *
     * @return is only true when this Channel is an Output.
     */
    @Transient
    public boolean isOutput() {
        return !isInput();
    }

    /**
     *
     * @param output
     *            set this channel as Output.
     */
    public void setOutput(final boolean output) {
        setInput(!output);
    }

    /**
     *
     * @return the IO Name of this Channel.
     */
    public String getIoName() {
        return _ioName;
    }

    /**
     *
     * @param ioName
     *            the IO Name of this Channel.
     */
    public void setIoName(@Nonnull final String ioName) {
        this._ioName = ioName;
    }

    /**
     *
     * @return is only true if the {@link ChannelDBO} digital.
     */
    public boolean isDigital() {
        return _digital;
    }

    /**
     *
     * @param digital
     *            set only true if this {@link ChannelDBO} digital.
     */
    public void setDigital(final boolean digital) {
        _digital = digital;
    }

    /**
     *
     * @return the bit size of the Channel.
     */
    @Transient
    public int getChSize() {
        return getChannelType().getBitSize();
    }

    /**
     *
     * @return the Type of this {@link ChannelDBO}
     */
    @Nonnull
    public DataType getChannelType() {
        if (_channelType < DataType.values().length) {
            return DataType.values()[_channelType];
        }
        return DataType.BIT;
    }

    /**
     *
     * @param type set the Type of this {@link ChannelDBO}
     */
    public void setChannelType(@Nonnull final DataType type) {
        _channelType = type.ordinal();
    }

    public void setChannelTypeNonHibernate(@Nonnull final DataType type) throws PersistenceException {
        if (getChannelType() != type) {
            setChannelType(type);
            setDirty(true);
            if (getModule() != null) {
                // Don't work with only one update!
                getModule().update();
                getModule().update();
            } else {
                localUpdate();
            }
        }
    }

    public String getCurrenUserParamDataIndex() {
        return _currenUserParamDataIndex;
    }

    public void setCurrenUserParamDataIndex(@Nonnull final String currenUserParamDataIndex) {
        _currenUserParamDataIndex = currenUserParamDataIndex;
    }

    public String getCurrentValue() {
        return _currentValue;
    }

    public void setCurrentValue(@Nonnull final String currentValue) {
        _currentValue = currentValue;
    }

    @Column(name = "CHSIZE")
    public int getStatusAddressOffset() {
        return _statusAddressOffset;
    }

    @Transient
    public int getStatusAddress() throws PersistenceException {
        return getModule().getInputOffsetNH() + _statusAddressOffset;
    }

    public void setStatusAddressOffset(@CheckForNull Integer statusAddress) {
        _statusAddressOffset = statusAddress == null?-1:statusAddress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     *
     * @param epicsAdress
     *            the Epics Address String.
     */
    public void setEpicsAddressString(@Nonnull final String epicsAdress) {
        _epicsAdress = epicsAdress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     *
     * @return the Epics Address String
     */
    @Transient
    @Nonnull
    public String getEpicsAddressStringNH() {
        return _epicsAdress==null?"":_epicsAdress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     *
     * @return the Epics Address String
     */
    @Nonnull
    public String getEpicsAddressString() {
        return _epicsAdress==null?"":_epicsAdress;
    }

    @Transient
    public int getStruct() {
        int index = 0;
        if (isDigital()) {
            index = getSortIndex();
        }
        return index;
    }

    /**
     *
     * @return the parent {@link ChannelStructureDBO}.
     */
    @ManyToOne
    @Nonnull
    public ChannelStructureDBO getChannelStructure() {
        return (ChannelStructureDBO) getParent();
    }

    /**
     *
     * @param channelStructure
     *            the parent {@link ChannelStructureDBO} of this Channel.
     */
    public void setChannelStructure(@Nonnull final ChannelStructureDBO channelStructure) {
        this.setParent(channelStructure);
    }

    /**
     *
     * @return the Slave GSD File
     */
    @Transient
    public GSDFileDBO getGSDFile() {
        return getChannelStructure().getModule().getGSDFile();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    protected void localUpdate() throws PersistenceException {
        NodeMap.countlocalUpdate();
        int channelNumber = 0;
        short channelSortIndex = getSortIndex();
        short structSortIndex = getParent().getSortIndex();
        short moduleSortIndex = getModule().getSortIndex();

        // refreshChannelType();

        if (!((channelSortIndex <= 0) && (structSortIndex <= 0) && (moduleSortIndex <= 0))) {
            // if it a simple Channel (AI/AO)
            if (getChannelStructure().isSimple()) {
                if (structSortIndex > 0) {
                    ChannelStructureDBO channelStructure = null;
                    short counter = structSortIndex;
                    while ((channelStructure == null) && (counter > 0)) {
                        channelStructure = getModule().getChildrenAsMap().get(--counter);
                        if ((channelStructure != null) && (channelStructure.getLastChannel() != null)
                                && (channelStructure.getLastChannel().isInput() == isInput())) {
                            // Previous Channel is:
                            if (channelStructure.isSimple()) {
                                ChannelDBO next = channelStructure.getLastChannel();
                                channelNumber = next.getChannelNumber();
                                channelNumber += next.getChannelType().getByteSize();
                            } else {
                                ChannelDBO next = channelStructure.getLastChannel();
                                channelNumber = next.getChannelNumber();
                                channelNumber += channelStructure.getStructureType().getByteSize();
                            }
                            break;
                        }
                        channelStructure = null;
                    }
                }
            } else {
                // Structe Channel (8 bit (DI/DO)))
                boolean isSet = false;

                if (channelSortIndex > 0) {
                    ChannelDBO channel = null;
                    short counter = channelSortIndex;
                    while ((channel == null) && (counter > 0)) {
                        channel = getChannelStructure().getChildrenAsMap().get(--counter);
                        if (channel != null) {
                            channelNumber = channel.getChannelNumber();
                            channelNumber += channel.getChannelType().getByteSize();
                            isSet = true;
                            break;
                        }
                    }
                }

                if ((structSortIndex > 0) && !isSet) {
                    ChannelStructureDBO channelStructure = null;
                    short counter = structSortIndex;
                    while ((channelStructure == null) && (counter > 0)) {
                        channelStructure = getModule().getChildrenAsMap().get(--counter);
                        if ((channelStructure != null)
                                && (channelStructure.getFirstChannel().isInput() == isInput())) {
                            if (channelStructure.isSimple()) {
                                channelNumber = channelStructure.getFirstChannel()
                                        .getChannelNumber();
                                channelNumber += channelStructure.getFirstChannel()
                                        .getChannelType().getByteSize();
                                break;
                            } else if (!channelStructure.isSimple()) {
                                channelNumber = channelStructure.getLastChannel()
                                        .getChannelNumber()
                                        + channelStructure.getStructureType().getByteSize();
                                break;
                            }

                        }
                        channelStructure = null;
                    }
                }
            }
        }

        setChannelNumber(channelNumber);
        assembleEpicsAddressString();
    }

    /**
     * Assemble the Epics Address String.
     * @throws PersistenceException 
     */
    @Transient
    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        NodeMap.countAssembleEpicsAddressString();
        String oldAdr = getEpicsAddressString();
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(getModule().getEpicsAddressString());
            sb.append("/");
            sb.append(getFullChannelNumber());
            if (getStatusAddressOffset() >= 0) {
                sb.append("/");
                sb.append(getStatusAddress());
            }
            sb.append(" 'T=");
            Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes = getModule().getGSDModule()
                    .getModuleChannelPrototypeNH();
            for (ModuleChannelPrototypeDBO moduleChannelPrototype : moduleChannelPrototypes) {
                if ((moduleChannelPrototype.isInput() == isInput())
                        && (getChannelNumber() == moduleChannelPrototype.getOffset())) {
                        if(getChannelStructure().isSimple()) {
                            setChannelTypeNonHibernate(moduleChannelPrototype.getType());
                        }else {
                            setChannelTypeNonHibernate(moduleChannelPrototype.getType().getStructure()[0]);
                        }
                    if ((getChannelType() == DataType.BIT)
                            && !getChannelStructure().isSimple()) {
                        sb.append(getChannelStructure().getStructureType().getType());
                        sb.append(getBitPostion());
                    } else {
                        sb.append(moduleChannelPrototype.getType().getType());
                    }
                    setStatusAddressOffset(moduleChannelPrototype.getShift());
                    if (moduleChannelPrototype.getMinimum() != null) {
                        sb.append(",L=" + moduleChannelPrototype.getMinimum());
                    }
                    if (moduleChannelPrototype.getMaximum() != null) {
                        sb.append(",H=" + moduleChannelPrototype.getMaximum());
                    }
                    if ((moduleChannelPrototype.getMaximum() != null)
                            && (moduleChannelPrototype.getByteOrdering() > 0)) {
                        sb.append(",O=" + moduleChannelPrototype.getByteOrdering());
                    }
                } else {
//                    if (getChannelType() == DataType.BIT && !getChannelStructure().isSimple()) {
//                        sb.append(getChannelStructure().getStructureType().getType());
//                        sb.append(getBitPostion());
//                    } else {
////                        sb.append(getChannelType().getType());
//                    }

                }
            }
            sb.append("'");
            setEpicsAddressString(sb.toString());
        } catch (NullPointerException e) {
            setEpicsAddressString(null);
        }
        setDirty((isDirty() || (oldAdr == null) || !oldAdr.equals(getEpicsAddressString())));
    }

    @Transient
    public String getBitPostion() {
        StringBuilder sb = new StringBuilder();
        if (getChannelType() == DataType.BIT ) {
//        if (getChannelType() == DataType.BIT && getSortIndex()>=0) {
            sb.append(",B=");
            sb.append(getSortIndex());
        }
        return sb.toString();
    }

    /**
     *
     * @return the parent {@link ModuleDBO}.
     */
    @Transient
    public ModuleDBO getModule() {
        if (getChannelStructure() != null) {
            return getChannelStructure().getModule();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public void update() throws PersistenceException {
        localUpdate();
        if (_isUpdated) {
            _isUpdated = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws PersistenceException {
        super.save();
        _isUpdated = false;
    }

    /**
     * @return The Name of this Node.
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(getFullChannelNumber());
            sb.append(": ");
            sb.append(getName());
            if ((getIoName() != null) && (getIoName().length() > 0)) {
                sb.append(" [" + getIoName() + "]");
            }
        } catch (PersistenceException e) {
            sb.append("Device Database ERROR: ").append(e.getMessage());
            CentralLogger.getInstance().error(this, e);
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public ChannelDBO copyThisTo(@Nonnull final ChannelStructureDBO parentNode) throws PersistenceException {
        ChannelDBO copy = (ChannelDBO) super.copyThisTo(parentNode);
        copy.setName(getName());
        return copy;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    protected ChannelDBO copyParameter(@Nonnull final ChannelStructureDBO parentNode) throws PersistenceException {
        ChannelStructureDBO channelStructure = parentNode;
        ChannelDBO copy = new ChannelDBO(channelStructure,
                                         getName(),
                                         isInput(),
                                         isDigital(),
                                         getSortIndex());
        // copy.setDocuments(getDocuments());
        // copy.setChannelNumber(getChannelNumber());
        copy.setChannelType(getChannelType());
        copy.setCurrentValue(getCurrentValue());
        copy.setCurrenUserParamDataIndex(getCurrenUserParamDataIndex());
        copy.setIoName(getIoName());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.CHANNEL;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public InvalidLeave addChild(@Nullable InvalidLeave child) throws PersistenceException {
        // do nothing. Channel is the leave node.
        return null;
    }

}
