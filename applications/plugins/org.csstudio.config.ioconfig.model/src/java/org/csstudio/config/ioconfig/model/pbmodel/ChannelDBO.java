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
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.VirtualLeaf;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.hibernate.annotations.BatchSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.10 $
 * @since 21.03.2007
 */
@Entity
@BatchSize(size = 32)
@Table(name = "ddb_Profibus_Channel")
public class ChannelDBO extends AbstractNodeDBO<ChannelStructureDBO, VirtualLeaf> {
    
    private static final Logger LOG = LoggerFactory.getLogger(ChannelDBO.class);
    
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
    public ChannelDBO(@Nonnull final ChannelStructureDBO channelStructure,
                      final boolean input,
                      final boolean digital) throws PersistenceException {
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
    public ChannelDBO(@Nonnull final ChannelStructureDBO channelStructure,
                      @Nonnull final String name,
                      final boolean input,
                      final boolean digital,
                      final int sortIndex) throws PersistenceException {
        super(channelStructure);
        setName(name);
        setInput(input);
        setDigital(digital);
        setSortIndex(sortIndex);
        setChannelType(DataType.DS33);//
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
        if(isInput()) {
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
    @CheckForNull
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
        return DataType.forId(_channelType);
    }
    
    /**
     * @param type set the Type of this {@link ChannelDBO}
     */
    public void setChannelType(@Nonnull final DataType type) {
        _channelType = type.getId();
    }
    
    public void setChannelTypeNonHibernate(@Nonnull final DataType type) throws PersistenceException {
        DataType channelType;
        try {
            channelType = getChannelType();
        } catch (IllegalArgumentException e) {
            channelType = null;
        }
        if(channelType==null||channelType != type) {
            setChannelType(type);
            setDirty(true);
            if(getModule() != null) {
                // Don't work with only one update!
                getModule().update();
                getModule().update();
            } else {
                localUpdate();
            }
        }
    }
    
    @CheckForNull
    public String getCurrenUserParamDataIndex() {
        return _currenUserParamDataIndex;
    }
    
    public void setCurrenUserParamDataIndex(@Nonnull final String currenUserParamDataIndex) {
        _currenUserParamDataIndex = currenUserParamDataIndex;
    }
    
    @CheckForNull
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
        _statusAddressOffset = statusAddress == null ? -1 : statusAddress;
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
        return _epicsAdress == null ? "" : _epicsAdress;
    }
    
    /**
     * contribution to ioName (PV-link to EPICSORA)
     *
     * @return the Epics Address String
     */
    @Nonnull
    public String getEpicsAddressString() {
        return _epicsAdress == null ? "" : _epicsAdress;
    }
    
    @Transient
    public int getStruct() {
        int index = 0;
        if(isDigital()) {
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
    @CheckForNull
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
        
        if(! ( (channelSortIndex <= 0) && (structSortIndex <= 0) && (moduleSortIndex <= 0))) {
            // if it a simple Channel (AI/AO)
            if(getChannelStructure().isSimple()) {
                channelNumber = updateSimpleChannel(channelNumber, structSortIndex);
            } else {
                channelNumber = updateStructureChannel(channelNumber,
                                                       channelSortIndex,
                                                       structSortIndex);
            }
        }

        setChannelNumber(channelNumber);
        assembleEpicsAddressString();
    }
    
    private int updateStructureChannel(int channelNumber,
                                       short channelSortIndex,
                                       short structSortIndex) throws PersistenceException {
        int cNumber = channelNumber;
        // Structe Channel (8 bit (DI/DO)))
        boolean isSet = false;
        
        if(channelSortIndex > 0) {
            ChannelDBO channel = null;
            short counter = channelSortIndex;
            while ( (channel == null) && (counter > 0)) {
                channel = getChannelStructure().getChildrenAsMap().get(--counter);
                if(channel != null) {
                    cNumber = channel.getChannelNumber();
                    cNumber += channel.getChannelType().getByteSize();
                    isSet = true;
                    break;
                }
            }
        }
        
        if( (structSortIndex > 0) && !isSet) {
            ChannelStructureDBO channelStructure = null;
            short counter = structSortIndex;
            while ( (channelStructure == null) && (counter > 0)) {
                channelStructure = getModule().getChildrenAsMap().get(--counter);
                if(channelStructure != null) {
                    ChannelDBO firstChannel = channelStructure.getFirstChannel();
                    if(firstChannel != null && firstChannel.isInput() == isInput()) {
                        if(channelStructure.isSimple()) {
                            cNumber = firstChannel.getChannelNumber();
                            cNumber += firstChannel.getChannelType().getByteSize();
                            break;
                        } else {
                            ChannelDBO lastChannel = channelStructure.getLastChannel();
                            cNumber = lastChannel.getChannelNumber()
                                    + channelStructure.getStructureType().getByteSize();
                            break;
                        }
                    }
                }
                channelStructure = null;
            }
        }
        return cNumber;
    }
    
    private int updateSimpleChannel(int channelNumber, short structSortIndex) throws PersistenceException {
        int cNr = channelNumber;
        if(structSortIndex > 0) {
            ChannelStructureDBO channelStructure = null;
            short counter = structSortIndex;
            while ( (channelStructure == null) && (counter > 0)) {
                channelStructure = getModule().getChildrenAsMap().get(--counter);
                ChannelDBO lastChannel = channelStructure.getLastChannel();
                if(isRightSimpleChannel(channelStructure, lastChannel)) {
                    // Previous Channel is:
                    cNr = lastChannel.getChannelNumber();
                    if(channelStructure.isSimple()) {
                        cNr += lastChannel.getChannelType().getByteSize();
                    } else {
                        cNr += channelStructure.getStructureType().getByteSize();
                    }
                    break;
                }
                channelStructure = null;
            }
        }
        return cNr;
    }
    
    /**
     * @param channelStructure
     * @param lastChannel
     * @return
     */
    private boolean isRightSimpleChannel(@CheckForNull ChannelStructureDBO channelStructure,
                                         @CheckForNull ChannelDBO lastChannel) {
        return (channelStructure != null) && (lastChannel != null)
                && (lastChannel.isInput() == isInput());
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
            StringBuilder sb = new StringBuilder(getModule().getEpicsAddressString());
            sb.append("/");
            sb.append(getFullChannelNumber());
            if(getStatusAddressOffset() >= 0) {
                sb.append("/");
                sb.append(getStatusAddress());
            }
            sb.append(" 'T=");
            Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes = getModule().getGSDModule()
                    .getModuleChannelPrototypeNH();
            for (ModuleChannelPrototypeDBO moduleChannelPrototype : moduleChannelPrototypes) {
                if( (moduleChannelPrototype.isInput() == isInput())
                        && (getChannelNumber() == moduleChannelPrototype.getOffset())) {
                    setChannelType(moduleChannelPrototype);
                    appendDataType(sb, moduleChannelPrototype);
                    setStatusAddressOffset(moduleChannelPrototype.getShift());
                    appendMinimum(sb, moduleChannelPrototype);
                    appendMaximum(sb, moduleChannelPrototype);
                    appendByteOdering(sb, moduleChannelPrototype);
                }
            }
            sb.append("'");
            setEpicsAddressString(sb.toString());
        } catch (NullPointerException e) {
            setEpicsAddressString("");
        }
        setDirty( (isDirty() || (oldAdr == null) || !oldAdr.equals(getEpicsAddressString())));
    }

    /**
     * @param sb
     * @param moduleChannelPrototype
     */
    private void appendDataType(StringBuilder sb, ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if( (getChannelType() == DataType.BIT) && !getChannelStructure().isSimple()) {
            sb.append(getChannelStructure().getStructureType().getType());
            sb.append(getBitPostion());
        } else {
            sb.append(moduleChannelPrototype.getType().getType());
        }
    }

    /**
     * @param sb
     * @param moduleChannelPrototype
     */
    private void appendByteOdering(StringBuilder sb,
                                   ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if( (moduleChannelPrototype.getMaximum() != null)
                && (moduleChannelPrototype.getByteOrdering() > 0)) {
            sb.append(",O=" + moduleChannelPrototype.getByteOrdering());
        }
    }

    /**
     * @param sb
     * @param moduleChannelPrototype
     */
    private void appendMaximum(StringBuilder sb, ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if(moduleChannelPrototype.getMaximum() != null) {
            sb.append(",H=" + moduleChannelPrototype.getMaximum());
        }
    }

    /**
     * @param sb
     * @param moduleChannelPrototype
     */
    private void appendMinimum(StringBuilder sb, ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if(moduleChannelPrototype.getMinimum() != null) {
            sb.append(",L=" + moduleChannelPrototype.getMinimum());
        }
    }

    /**
     * @param moduleChannelPrototype
     * @throws PersistenceException
     */
    private void setChannelType(ModuleChannelPrototypeDBO moduleChannelPrototype) throws PersistenceException {
        if(getChannelStructure().isSimple()) {
            setChannelTypeNonHibernate(moduleChannelPrototype.getType());
        } else {
            setChannelTypeNonHibernate(moduleChannelPrototype.getType().getStructure()[0]);
        }
    }
    
    @Transient
    @Nonnull
    public String getBitPostion() {
        StringBuilder sb = new StringBuilder();
        if(getChannelType() == DataType.BIT) {
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
    @Nonnull
    public ModuleDBO getModule() {
        return getChannelStructure().getModule();
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public void update() throws PersistenceException {
        localUpdate();
        if(_isUpdated) {
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
            if( (getIoName() != null) && (getIoName().length() > 0)) {
                sb.append(" [" + getIoName() + "]");
            }
        } catch (PersistenceException e) {
            sb.append("Device Database ERROR: ").append(e.getMessage());
            LOG.error("Device Database ERROR: {}", e);
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
    @Nonnull
    public VirtualLeaf addChild(@Nullable VirtualLeaf child) throws PersistenceException {
        return VirtualLeaf.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public VirtualLeaf createChild() throws PersistenceException {
        return VirtualLeaf.INSTANCE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
    
    
    
}
