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
import java.util.SortedMap;

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
    private int _statusAddressOffset;
    private int _channelType;
    
    private boolean _input;
    private boolean _digital;
    private boolean _isUpdated;
    
    private String _ioName;
    private String _currenUserParamDataIndex;
    private String _currentValue;
    private String _epicsAdress;
    
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
        this(channelStructure, " ", input, digital, (short) -1);
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
     * @return the Channel Number exclusive offset.
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
     * @return is only true when this Channel is an Input.
     */
    public boolean isInput() {
        return _input;
    }
    
    /**
     * @param input this channel as Input.
     */
    public void setInput(final boolean input) {
        this._input = input;
    }
    
    /**
     * @return is only true when this Channel is an Output.
     */
    @Transient
    public boolean isOutput() {
        return !isInput();
    }
    
    /**
     * @param output set this channel as Output.
     */
    public void setOutput(final boolean output) {
        setInput(!output);
    }
    
    /**
     * @return the IO Name of this Channel.
     */
    @CheckForNull
    public String getIoName() {
        return _ioName;
    }
    
    /**
     * @param ioName the IO Name of this Channel.
     */
    public void setIoName(@Nonnull final String ioName) {
        this._ioName = ioName;
    }
    
    /**
     * @return is only true if the {@link ChannelDBO} digital.
     */
    public boolean isDigital() {
        return _digital;
    }
    
    /**
     * @param digital set only true if this {@link ChannelDBO} digital.
     */
    public void setDigital(final boolean digital) {
        _digital = digital;
    }
    
    /**
     * @return the bit size of the Channel.
     */
    @Transient
    public int getChSize() {
        return getChannelType().getBitSize();
    }
    
    /**
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
        } catch (final IllegalArgumentException e) {
            channelType = null;
        }
        if (channelType == null || channelType != type) {
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
    
    public void setStatusAddressOffset(@CheckForNull final Integer statusAddress) {
        _statusAddressOffset = statusAddress == null ? -1 : statusAddress;
    }
    
    /**
     * contribution to ioName (PV-link to EPICSORA)
     *
     * @param epicsAdress the Epics Address String.
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
        if (isDigital()) {
            index = getSortIndex();
        }
        return index;
    }
    
    /**
     * @return the parent {@link ChannelStructureDBO}.
     */
    @ManyToOne
    @Nonnull
    public ChannelStructureDBO getChannelStructure() {
        return (ChannelStructureDBO) getParent();
    }
    
    /**
     * @param channelStructure the parent {@link ChannelStructureDBO} of this Channel.
     */
    public void setChannelStructure(@Nonnull final ChannelStructureDBO channelStructure) {
        this.setParent(channelStructure);
    }
    
    @Transient
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return getChannelStructure().getModule().getGSDFile();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void localUpdate() throws PersistenceException {
        NodeMap.countlocalUpdate();
        int channelNumber = 0;
        final short channelSortIndex = getSortIndex();
        final short structSortIndex = getParent().getSortIndex();
        final short moduleSortIndex = getModule().getSortIndex();
        
        if (!((channelSortIndex <= 0) && (structSortIndex <= 0) && (moduleSortIndex <= 0))) {
            if (getChannelStructure().isSimple()) {
                channelNumber = updateSimpleChannel(channelNumber, structSortIndex);
            } else {
                channelNumber =
                                updateStructureChannel(channelNumber,
                                                       channelSortIndex,
                                                       structSortIndex);
            }
        }
        
        setChannelNumber(channelNumber);
        assembleEpicsAddressString();
    }
    
    private int updateStructureChannel(final int channelNumber,
                                       final short channelSortIndex,
                                       final short structSortIndex) throws PersistenceException {
        Integer cNumber = getNextFreeChannelNumberFromParent(channelSortIndex);
        
        if (cNumber == null) {
            cNumber = getNextFreeChannelNumberFromPreviousModul(structSortIndex);
        }
        
        return cNumber == null ? channelNumber : cNumber;
    }
    
    @CheckForNull
    private Integer getNextFreeChannelNumberFromPreviousModul(final short structSortIndex) throws PersistenceException {
        Integer cNumber = null;
        if(structSortIndex > 0) {
            ChannelStructureDBO channelStructure = null;
            short counter = structSortIndex;
            while ( (channelStructure == null) && (counter > 0)) {
                channelStructure = getModule().getChildrenAsMap().get(--counter);
                if(channelStructure != null) {
                    final ChannelDBO lastChannel = channelStructure.getLastChannel();
                    cNumber = getChannelNumberFromChannel(lastChannel, channelStructure);
                    channelStructure = null;
                }
            }
        }
        return cNumber;
    }
    
    @CheckForNull
    private Integer getChannelNumberFromChannel(@CheckForNull final ChannelDBO lastChannel, @Nonnull final ChannelStructureDBO channelStructure) {
        Integer cNumber = null;
        if(lastChannel != null && lastChannel.isInput() == isInput()) {
            if (channelStructure.isSimple()) {
                cNumber = lastChannel.getChannelNumber();
                cNumber += lastChannel.getChannelType().getByteSize();
            } else {
                cNumber = lastChannel.getChannelNumber();
                cNumber += channelStructure.getStructureType().getByteSize();
            }
        }
        return cNumber;
    }

    @CheckForNull
    private Integer getNextFreeChannelNumberFromParent(final short channelSortIndex) throws PersistenceException {
        Integer cNumber = null;
        if (channelSortIndex > 0) {
            ChannelDBO channel = null;
            short counter = channelSortIndex;
            while ((channel == null) && (counter > 0)) {
                channel = getChannelStructure().getChildrenAsMap().get(--counter);
                if (channel != null) {
                    cNumber = channel.getChannelNumber();
                    cNumber += channel.getChannelType().getByteSize();
                    return cNumber;
                }
            }
        }
        return cNumber;
    }
    
    private int updateSimpleChannel(final int channelNumber, final short structSortIndex) throws PersistenceException {
        int cNr = channelNumber;
        if (structSortIndex > 0) {
            ChannelStructureDBO channelStructure = null;
            final SortedMap<Short, ChannelStructureDBO> childrenAsMap = getModule().getChildrenAsMap();
            final SortedMap<Short, ChannelStructureDBO> subMap = childrenAsMap.headMap(structSortIndex);
                while (!subMap.isEmpty() && (channelStructure = subMap.remove(subMap.lastKey())) != null) {
                    final ChannelDBO lastChannel = channelStructure.getLastChannel();
                    if (isRightSimpleChannel(channelStructure, lastChannel)) {
                        // Previous Channel is:
                        cNr = lastChannel.getChannelNumber();
                        if (channelStructure.isSimple()) {
                            cNr += lastChannel.getChannelType().getByteSize();
                        } else {
                            cNr += channelStructure.getStructureType().getByteSize();
                        }
                        break;
                    }
                }
        }
        return cNr;
    }
    
    private boolean isRightSimpleChannel(@CheckForNull final ChannelStructureDBO channelStructure,
                                         @CheckForNull final ChannelDBO lastChannel) {
        return (channelStructure != null) && (lastChannel != null)
               && (lastChannel.isInput() == isInput());
    }
    
    @Transient
    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        NodeMap.countAssembleEpicsAddressString();
        final String oldAdr = getEpicsAddressString();
        try {
            final StringBuilder sb = new StringBuilder(getModule().getEpicsAddressString());
            sb.append("/");
            sb.append(getFullChannelNumber());
            if (getStatusAddressOffset() >= 0) {
                sb.append("/");
                sb.append(getStatusAddress());
            }
            assembleEpicsAddressType(sb);
            sb.append("'");
            setEpicsAddressString(sb.toString());
        } catch (final NullPointerException e) {
            LOG.warn("", e);
            setEpicsAddressString("");
        }
        setDirty((isDirty() || (oldAdr == null) || !oldAdr.equals(getEpicsAddressString())));
    }
    
    private void assembleEpicsAddressType(@Nonnull final StringBuilder sb) throws PersistenceException {
        sb.append(" 'T=");
        final GSDModuleDBO gsdModule = getModule().getGSDModule();
        if (gsdModule != null) {
            final Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes =
                                                                     gsdModule
                                                                             .getModuleChannelPrototypeNH();
            for (ModuleChannelPrototypeDBO moduleChannelPrototype : moduleChannelPrototypes) {
                if ((moduleChannelPrototype != null)
                    && (moduleChannelPrototype.isInput() == isInput())
                    && (getChannelNumber() == moduleChannelPrototype.getOffset())) {
                    setChannelType(moduleChannelPrototype);
                    appendDataType(sb, moduleChannelPrototype);
                    setStatusAddressOffset(moduleChannelPrototype.getShift());
                    appendMinimum(sb, moduleChannelPrototype);
                    appendMaximum(sb, moduleChannelPrototype);
                    appendByteOdering(sb, moduleChannelPrototype);
                }
            }
        }
    }
    
    private void appendDataType(@Nonnull final StringBuilder sb,
                                @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if ((getChannelType() == DataType.BIT) && !getChannelStructure().isSimple()) {
            sb.append(getChannelStructure().getStructureType().getType());
            sb.append(getBitPostion());
        } else {
            sb.append(moduleChannelPrototype.getType().getType());
        }
    }
    
    private void appendByteOdering(@Nonnull final StringBuilder sb,
                                   @Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        final Integer byteOrdering = moduleChannelPrototype.getByteOrdering();
        if ((byteOrdering !=null) && (byteOrdering > 0)) {
            sb.append(",O=" + byteOrdering);
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
    
    private void setChannelType(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) throws PersistenceException {
        if (getChannelStructure().isSimple()) {
            setChannelTypeNonHibernate(moduleChannelPrototype.getType());
        } else {
            setChannelTypeNonHibernate(moduleChannelPrototype.getType().getStructure()[0]);
        }
    }
    
    @Transient
    @Nonnull
    public String getBitPostion() {
        final StringBuilder sb = new StringBuilder();
        if (getChannelType() == DataType.BIT) {
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
        final StringBuffer sb = new StringBuffer();
        try {
            sb.append(getFullChannelNumber());
            sb.append(": ");
            sb.append(getName());
            final String ioName = getIoName();
            if ((ioName != null) && (ioName.length() > 0)) {
                sb.append(" [" + ioName + "]");
            }
        } catch (final PersistenceException e) {
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
        final ChannelDBO copy = (ChannelDBO) super.copyThisTo(parentNode);
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
        final ChannelStructureDBO channelStructure = parentNode;
        String name = getName();
        if (name == null) {
            name = " ";
        }
        final ChannelDBO copy =
                          new ChannelDBO(channelStructure,
                                         name,
                                         isInput(),
                                         isDigital(),
                                         getSortIndex());
        copy.setChannelType(getChannelType());
        String currentValue = getCurrentValue();
        if (currentValue == null) {
            currentValue = "";
        }
        copy.setCurrentValue(currentValue);
        String currenUserParamDataIndex = getCurrenUserParamDataIndex();
        if (currenUserParamDataIndex == null) {
            currenUserParamDataIndex = " ";
        }
        copy.setCurrenUserParamDataIndex(currenUserParamDataIndex);
        String ioName = getIoName();
        if (ioName == null) {
            ioName = " ";
        }
        copy.setIoName(ioName);
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
    public VirtualLeaf addChild(@Nullable final VirtualLeaf child) throws PersistenceException {
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
    
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
