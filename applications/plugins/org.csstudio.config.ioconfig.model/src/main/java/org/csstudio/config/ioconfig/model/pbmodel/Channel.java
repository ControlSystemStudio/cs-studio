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
 * $Id$
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.PersistenceException;

/**
 * 
 * @author gerke
 * @author $Author$
 * @version $Revision$
 * @since 21.03.2007
 */
@Entity
@Table(name = "ddb_Profibus_Channel")
public class Channel extends Node {

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
     * This Constructor is only used by Hibernate. To create an new {@link Channel}
     * {@link #Channel(ChannelStructure, boolean, boolean)} or
     * {@link #Channel(ChannelStructure, String, boolean, boolean, short)} or
     */
    public Channel() {
    }

    /**
     * Generate a new Pure Channel on the parent Channel Structure. The Channel get the first free
     * Station Address. The max Station Address is {@link Channel}
     * {@value #DEFAULT_MAX_STATION_ADDRESS}
     * 
     * @param channelStructure
     *            the parent Channel Structure.
     * 
     * @param input
     *            only if true then is the channel a Input otherwise a Output channel.
     * @param digital
     *            only if true then is the channel a Digital otherwise a Analog channel.
     */
    public Channel(ChannelStructure channelStructure, boolean input, boolean digital) {
        this(channelStructure, null, input, digital, (short) -1);
    }

    /**
     * Generate a new Pure Channel on the parent Channel Structure. The Channel get the first free
     * Station Address. The max Station Address is {@link Channel}
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
     */
    public Channel(ChannelStructure channelStructure, String name, boolean input, boolean digital,
            short sortIndex) {
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
     */
    @Transient
    public int getFullChannelNumber() {
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
    public void setChannelNumber(int channelNumber) {
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
    public void setInput(boolean input) {
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
    public void setOutput(boolean output) {
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
    public void setIoName(String ioName) {
        this._ioName = ioName;
    }

    /**
     * 
     * @return is only true if the {@link Channel} digital.
     */
    public boolean isDigital() {
        return _digital;
    }

    /**
     * 
     * @param digital
     *            set only true if this {@link Channel} digital.
     */
    public void setDigital(boolean digital) {
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

    public DataType getChannelType() {
        if (_channelType < DataType.values().length) {
            return DataType.values()[_channelType];
        }
        return DataType.BIT;
    }

    public void setChannelType(DataType type) {
        _channelType = type.ordinal();
    }

    public void setChannelTypeNonHibernate(DataType type) {
        setChannelType(type);
        if (getModule() != null) {
            // Don't work with only one update!
            getModule().update();
            getModule().update();
        } else {
            localUpdate();
        }
    }

    public String getCurrenUserParamDataIndex() {
        return _currenUserParamDataIndex;
    }

    public void setCurrenUserParamDataIndex(String currenUserParamDataIndex) {
        _currenUserParamDataIndex = currenUserParamDataIndex;
    }

    public String getCurrentValue() {
        return _currentValue;
    }

    public void setCurrentValue(String currentValue) {
        _currentValue = currentValue;
    }

    @Column(name = "CHSIZE")
    public int getStatusAddressOffset() {
        return _statusAddressOffset;
    }

    @Transient
    public int getStatusAddress() {
        return getModule().getInputOffsetNH() + _statusAddressOffset;
    }

    public void setStatusAddressOffset(Integer statusAddress) {
        if (statusAddress == null) {
            statusAddress = -1;
        }
        _statusAddressOffset = statusAddress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     * 
     * @param epicsAdress
     *            the Epics Address String.
     */
    public void setEpicsAddressString(String epicsAdress) {
        _epicsAdress = epicsAdress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     * 
     * @return the Epics Address String
     */
    @Transient
    public String getEpicsAddressStringNH() {
        assembleEpicsAddressString();
        return _epicsAdress;
    }

    /**
     * contribution to ioName (PV-link to EPICSORA)
     * 
     * @return the Epics Address String
     */
    public String getEpicsAddressString() {
        return _epicsAdress;
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
     * @return the parent {@link ChannelStructure}.
     */
    @ManyToOne
    public ChannelStructure getChannelStructure() {
        return (ChannelStructure) getParent();
    }

    /**
     * 
     * @param channelStructure
     *            the parent {@link ChannelStructure} of this Channel.
     */
    public void setChannelStructure(ChannelStructure channelStructure) {
        this.setParent(channelStructure);
    }

    /**
     * 
     * @return the Slave GSD File
     */
    @Transient
    public GSDFile getGSDFile() {
        return getChannelStructure().getModule().getGSDFile();
    }

    /**
     * {@inheritDoc}
     */
    protected void localUpdate() {
        int channelNumber = 0;
        short channelSortIndex = getSortIndex();
        short structSortIndex = getParent().getSortIndex();
        short moduleSortIndex = getModule().getSortIndex();

        if (!(channelSortIndex <= 0 && structSortIndex <= 0 && moduleSortIndex <= 0)) {
            if (getChannelStructure().isSimple()) {
                if (structSortIndex > 0) {
                    ChannelStructure channelStructure = null;
                    short counter = structSortIndex;
                    while (channelStructure == null && counter > 0) {
                        channelStructure = getModule().getChannelStructsAsMap().get(--counter);
                        if (channelStructure != null && channelStructure.getLastChannel() != null 
                                && channelStructure.getLastChannel().isInput() == isInput()) {
                            if (channelStructure.isSimple()) {
                                Channel next = channelStructure.getLastChannel();
                                channelNumber = next.getChannelNumber();
                                channelNumber += next.getChannelType().getByteSize();
                            } else {
                                Channel next = channelStructure.getLastChannel();
                                channelNumber = next.getChannelNumber();
                                channelNumber += channelStructure.getStructureType().getByteSize();
                            }
                            break;
                        }
                        channelStructure = null;
                    }
                }
            } else {
                boolean isSet = false;

                if (channelSortIndex > 0) {
                    Channel channel = null;
                    short counter = channelSortIndex;
                    while (channel == null && counter > 0) {
                        channel = getChannelStructure().getChannelsAsMap().get(--counter);
                        if (channel != null) {
                            channelNumber = channel.getChannelNumber();
                            channelNumber += channel.getChannelType().getByteSize();
                            isSet = true;
                            break;
                        }
                    }
                }

                if (structSortIndex > 0 && !isSet) {
                    ChannelStructure channelStructure = null;
                    short counter = structSortIndex;
                    while (channelStructure == null && counter > 0) {
                        channelStructure = getModule().getChannelStructsAsMap().get(--counter);
                        if (channelStructure != null
                                && channelStructure.getFirstChannel().isInput() == isInput()) {
                            if (channelStructure.isSimple()) {
                                channelNumber = channelStructure.getFirstChannel()
                                        .getChannelNumber();
                                channelNumber += channelStructure.getFirstChannel()
                                        .getChannelType().getByteSize();
                                break;
                            } else if (!channelStructure.isSimple()) {
                                channelNumber = channelStructure.getStructureType().getByteSize();
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
     */
    @Transient
    @Override
    public void assembleEpicsAddressString() {
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
            if (getChannelType() == DataType.BIT && !getChannelStructure().isSimple()) {
                sb.append(getChannelStructure().getStructureType().getType());
                sb.append(getBitPostion());
            } else {
                sb.append(getChannelType().getType());
            }
            Set<ModuleChannelPrototype> moduleChannelPrototypes = getModule().getGSDModule()
                    .getModuleChannelPrototypeNH();
            for (ModuleChannelPrototype moduleChannelPrototype : moduleChannelPrototypes) {
                if (moduleChannelPrototype.isInput() == isInput()
                        && getChannelNumber() == moduleChannelPrototype.getOffset()) {
                    setStatusAddressOffset(moduleChannelPrototype.getShift());
                    if (moduleChannelPrototype.getMinimum() != null) {
                        sb.append(",L=" + moduleChannelPrototype.getMinimum());
                    }
                    if (moduleChannelPrototype.getMaximum() != null) {
                        sb.append(",H=" + moduleChannelPrototype.getMaximum());
                    }
                    if (moduleChannelPrototype.getMaximum() != null
                            && moduleChannelPrototype.getByteOrdering() > 0) {
                        sb.append(",O=" + moduleChannelPrototype.getByteOrdering());
                    }
                }
            }
//            sb.append(getChannelType().getDefaultHigh());
            sb.append("'");
            setEpicsAddressString(sb.toString());
        } catch (NullPointerException e) {
            setEpicsAddressString(null);
        }
        setDirty((oldAdr == null || !oldAdr.equals(getEpicsAddressString())));
    }

    @Transient
    private Object getBitPostion() {
        StringBuilder sb = new StringBuilder();
        if (getChannelType() == DataType.BIT) {
            sb.append(",B=");
            sb.append(getSortIndex());
        }
        return sb.toString();
    }

    /**
     * 
     * @return the parent {@link Module}.
     */
    @Transient
    public Module getModule() {
        if (getChannelStructure() != null) {
            return getChannelStructure().getModule();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
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
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getFullChannelNumber());
        sb.append(": ");
        sb.append(getName());
        if (getIoName() != null && getIoName().length() > 0) {
            sb.append(" [" + getIoName() + "]");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyThisTo(Node parentNode) {
        Node copy = super.copyThisTo(parentNode);
        copy.setName(getName());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof ChannelStructure) {
            ChannelStructure channelStructure = (ChannelStructure) parentNode;
            Channel copy = new Channel(channelStructure, getName(), isInput(), isDigital(),
                    getSortIndex());
            // copy.setDocuments(getDocuments());
            // copy.setChannelNumber(getChannelNumber());
            copy.setChannelType(getChannelType());
            copy.setCurrentValue(getCurrentValue());
            copy.setCurrenUserParamDataIndex(getCurrenUserParamDataIndex());
            copy.setIoName(getIoName());
            return copy;
        }
        return null;
    }

}
