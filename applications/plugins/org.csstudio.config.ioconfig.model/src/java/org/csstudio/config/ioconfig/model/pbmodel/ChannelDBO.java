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

import java.util.SortedMap;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
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
public class ChannelDBO extends AbstractNodeSharedImpl<ChannelStructureDBO, VirtualLeaf> {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelDBO.class);
    private static final long serialVersionUID = 1L;

    private int _channelNumber;
    private int _statusAddressOffset;
    private int _channelType;

    private boolean _input;
    private boolean _digital;

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
     * @param channelStructure the parent Channel Structure.
     * @param name the name of this Channel.
     * @param input only if true then is the channel a Input otherwise a Output channel.
     * @param digital only if true then is the channel a Digital otherwise a Analog channel.
     * @param sortIndex the sort position for this Channel.
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
        localUpdate();
    }

    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public VirtualLeaf addChild(@Nullable final VirtualLeaf child) throws PersistenceException {
        return VirtualLeaf.INSTANCE;
    }

    @Transient
    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        NodeMap.countAssembleEpicsAddressString();
        final String oldAdr = getEpicsAddressString();
        try {
            final EpicsAddressStringBuilder eAdrBuilder = new EpicsAddressStringBuilder(this);
            final String newAdr = eAdrBuilder.getEpicsAddressString();
            setEpicsAddressString(newAdr);
        } catch (final NullPointerException e) {
            LOG.warn("", e);
            setEpicsAddressString("");
        }
        setDirty((isDirty() || oldAdr == null || !oldAdr.equals(getEpicsAddressString())));
    }

    /**
    * {@inheritDoc}
    * @throws PersistenceException
    */
    @Override
    @Nonnull
    protected ChannelDBO copyParameter(@Nonnull final ChannelStructureDBO parentNode) throws PersistenceException {
        final ChannelStructureDBO channelStructure = parentNode;
        final String name = getName() == null ? " " : getName();
        final ChannelDBO copy;
        copy = new ChannelDBO(channelStructure, name, isInput(), isDigital(), getSortIndex());
        copy.setChannelType(getChannelType());
        final String currentValue = getCurrentValue()==null?"":getCurrentValue();
        copy.setCurrentValue(currentValue);
        final String currenUserParamDataIndex = getCurrenUserParamDataIndex() == null ? " "
                : getCurrenUserParamDataIndex();
        copy.setCurrenUserParamDataIndex(currenUserParamDataIndex);
        final String ioName = getIoName()==null?" ":getIoName();
        copy.setIoName(ioName);
        copy.setDescription(getDescription());
        final GSDModuleDBO module = getModule().getGSDModule();
        if (module != null) {
            final TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes = module
                    .getModuleChannelPrototypeNH();
            final ModuleChannelPrototypeDBO[] array = moduleChannelPrototypes
                    .toArray(new ModuleChannelPrototypeDBO[0]);
            final Short sortIndex = getChannelStructure().getSortIndex();
            final ModuleChannelPrototypeDBO moduleChannelPrototype = array[sortIndex];
            setStatusAddressOffset(moduleChannelPrototype.getShift());
            setChannelNumber(moduleChannelPrototype.getOffset());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @Nonnull
    public ChannelDBO copyThisTo(@Nonnull final ChannelStructureDBO parentNode,
                                 @CheckForNull final String namePrefix) throws PersistenceException {
        final ChannelDBO copy = (ChannelDBO) super.copyThisTo(parentNode, namePrefix);
        copy.assembleEpicsAddressString();
        return copy;
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
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }

    @Transient
    @Nonnull
    public String getBitPostion() {
        if (getChannelType() == DataType.BIT) {
            return ",B="+getSortIndex();
        }
        return "";
    }

    /**
     * @return the Channel Number exclusive offset.
     */
    public int getChannelNumber() {
        return _channelNumber;
    }

    /**
     * @return the parent {@link ChannelStructureDBO}.
     */
    @ManyToOne
    @Nonnull
    public ChannelStructureDBO getChannelStructure() {
        return getParent();
    }

    /**
     * @return the Type of this {@link ChannelDBO}
     */
    @Nonnull
    public DataType getChannelType() {
        return DataType.forId(_channelType);
    }

    /**
     * @return the bit size of the Channel.
     */
    @Transient
    public int getChSize() {
        return getChannelType().getBitSize();
    }

    @CheckForNull
    public String getCurrentValue() {
        return _currentValue;
    }

    @CheckForNull
    public String getCurrenUserParamDataIndex() {
        return _currenUserParamDataIndex;
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

    /**
     * @return The Channel number inclusive offset.
     * @throws PersistenceException
     */
    @Transient
    public int getFullChannelNumber() throws PersistenceException {
        final int value = isInput()?getModule().getInputOffsetNH():getModule().getOutputOffsetNH();
        return value + _channelNumber;
    }

    @Transient
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return getChannelStructure().getModule().getGSDFile();
    }

    /**
     * @return the IO Name of this Channel.
     */
    @CheckForNull
    public String getIoName() {
        return _ioName;
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

    public int getNextFreeChannelNumberFromParent(final int channelNumber) {
        int cNumber = channelNumber;
        if (channelNumber > 0) {
            final SortedMap<Short, ChannelDBO> headMap = getChannelStructure().getChildrenAsMap()
                    .headMap(getSortIndex());
            if (!headMap.isEmpty()) {
                final ChannelDBO channel = headMap.get(headMap.lastKey());
                if (channel != null) {
                    cNumber = channel.getChannelNumber();
                    cNumber += channel.getChannelType().getByteSize();
                }
            }
        }
        return cNumber;
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

    @Transient
    public int getStatusAddress() throws PersistenceException {
        return getModule().getInputOffsetNH() + _statusAddressOffset;
    }

    @Column(name = "CHSIZE")
    public int getStatusAddressOffset() {
        return _statusAddressOffset;
    }

    @Transient
    public int getStruct() {
        return isDigital() ? getSortIndex() : 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @return is only true if the {@link ChannelDBO} digital.
     */
    public boolean isDigital() {
        return _digital;
    }

    /**
     * @return is only true when this Channel is an Input.
     */
    public boolean isInput() {
        return _input;
    }

    /**
     * @return is only true when this Channel is an Output.
     */
    @Transient
    public boolean isOutput() {
        return !isInput();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void localUpdate() throws PersistenceException {
        NodeMap.countlocalUpdate();
        assembleEpicsAddressString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() throws PersistenceException {
        super.save();
    }

    /**
     * Only used from Hibernate or Internal! The Channel number are automatic set when the sortIndex
     * are set.
     *
     * @param channelNumber the channel start Address.
     */
    public void setChannelNumber(final int channelNumber) {
        this._channelNumber = channelNumber;
    }

    /**
     * @param channelStructure the parent {@link ChannelStructureDBO} of this Channel.
     */
    public void setChannelStructure(@Nonnull final ChannelStructureDBO channelStructure) {
        this.setParent(channelStructure);
    }

    /**
     * @param type set the Type of this {@link ChannelDBO}
     */
    public void setChannelType(@Nonnull final DataType type) {
        _channelType = type.getId();
    }

    @Transient
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
            // Don't work with only one update!
            getModule().update();
            getModule().update();
        }
    }

    public void setCurrentValue(@Nonnull final String currentValue) {
        _currentValue = currentValue;
    }

    public void setCurrenUserParamDataIndex(@Nonnull final String currenUserParamDataIndex) {
        _currenUserParamDataIndex = currenUserParamDataIndex;
    }

    /**
     * @param digital set only true if this {@link ChannelDBO} digital.
     */
    public void setDigital(final boolean digital) {
        _digital = digital;
    }

    /**
     * @param epicsAdress the Epics Address String.
     */
    public void setEpicsAddressString(@Nonnull final String epicsAdress) {
        _epicsAdress = epicsAdress;
    }

    /**
     * @param input this channel as Input.
     */
    public void setInput(final boolean input) {
        this._input = input;
    }

    /**
     * @param ioName the IO Name of this Channel.
     */
    public void setIoName(@Nonnull final String ioName) {
        this._ioName = ioName;
    }

    /**
     * @param output set this channel as Output.
     */
    public void setOutput(final boolean output) {
        setInput(!output);
    }

    public void setStatusAddressOffset(@CheckForNull final Integer statusAddress) {
        _statusAddressOffset = statusAddress == null ? -1 : statusAddress;
    }

    /**
     * @return The Name of this Node.
     */
    @Override
    @Nonnull
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        try {
            sb.append(getFullChannelNumber()).append(": ").append(getName());
            final String ioName = getIoName();
            if (ioName != null && !ioName.trim().isEmpty()) {
                sb.append(" [" + ioName.trim() + "]");
            }
        } catch (final PersistenceException e) {
            sb.append("Device Database ERROR: ").append(e.getMessage());
            LOG.error("Device Database ERROR: {}", e);
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() throws PersistenceException {
        localUpdate();
    }

}
