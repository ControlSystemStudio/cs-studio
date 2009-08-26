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

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.12.2008
 */
@Entity
@Table(name = "ddb_Profibus_Channel_Structure")
public class ChannelStructure extends Node implements IStructured {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String _structureType;
    private boolean _simple;

    /**
     * Default Constructor used only by Hibernate. To crate a {@link ChannelStructure} use the
     * Factory methods.
     */
    public ChannelStructure() {
    }

    // private ChannelStructure(Module module, boolean simple) {
    // this(module, simple, true, null, null, DEFAULT_MAX_STATION_ADDRESS);
    // }

    private ChannelStructure(Module module, boolean simple, boolean isInput, DataType type,
            String name) {
        this(module, simple, isInput, type, name, DEFAULT_MAX_STATION_ADDRESS);
    }

    private ChannelStructure(Module module, boolean simple, boolean isInput, DataType type,
            String name, int defaultMaxStationAddress) {
        setSimple(simple);
        setParent(module);
        setName("Struct of " + name);
        setStructureType(type);
        module.addChild(this);

        buildChildren(type, isInput, name);

    }

    public static ChannelStructure makeSimpleChannel(Module module, boolean isInput) {
        return makeSimpleChannel(module, null, isInput, false);
    }

    public static ChannelStructure makeSimpleChannel(Module module, String name, boolean isInput,
            boolean isDigit) {
        ChannelStructure channelStructure = new ChannelStructure(module, true, isInput,
                DataType.SIMPLE, name);
        new Channel(channelStructure, name, isInput, isDigit, channelStructure.getSortIndex());
        return channelStructure;
    }

    /**
     * 
     * @param module
     * @param isInput
     * @return
     */
    public static ChannelStructure makeSimpleChannelStructure(Module module, boolean isInput) {
        return new ChannelStructure(module, true, isInput, DataType.SIMPLE, null, 1);
    }

    public static ChannelStructure makeTestChannelStructure(Module module) {
        return new ChannelStructure(module, false, true, DataType.SIMPLE, null, 1);
    }

    public static ChannelStructure makeChannelStructure(Module module, boolean isInput,
            DataType type, String name) {
        return new ChannelStructure(module, false, isInput, type, name, DEFAULT_MAX_STATION_ADDRESS);
    }

    private void buildChildren(DataType type, boolean isInput, String name) {
        if (isSimple()) {
            return;
        }

        DataType[] structer = type.getStructure();
        for (short sortIndex = 0; sortIndex < structer.length; sortIndex++) {
            Channel channel = new Channel(this, name + sortIndex, isInput, structer[sortIndex]
                    .getByteSize() < 8, sortIndex);

            channel.setName(name + sortIndex);
            // use setChannelType to reduce the local Updates. Make a local Update after add all
            // Channles.
            // channel.setChannelTypeNonHibernate(structerSize);
            channel.setChannelType(structer[sortIndex]);
            // channel.setChannelNumber(channelPrototype.getOffset()*8+sortIndex);
            // channel.setCreatedOn(now);
            // channel.setUpdatedOn(now);
            // channel.setCreatedBy(createdBy);
            // channel.setUpdatedBy(createdBy);
        }
        // this.localUpdate();
        // update();
        // getModule().localUpdate();
        getModule().update();
        getModule().update();
    }

    /**
     * 
     * @return the parent Module.
     */
    @ManyToOne
    public Module getModule() {
        return (Module) getParent();
    }

    /**
     * 
     * @param module
     *            the parent Module.
     */
    public void setModule(Module module) {
        this.setParent(module);
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Set<Channel> getChannels() {
        return (Set<Channel>) getChildren();
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Map<Short, Channel> getChannelsAsMap() {
        return (Map<Short, Channel>) getChildrenAsMap();
    }

    @Transient
    public Channel getFirstChannel() {
        TreeMap<Short, Channel> treeMap = (TreeMap<Short, Channel>) getChannelsAsMap();
        return treeMap.get(treeMap.firstKey());
    }

    @Transient
    public Channel getLastChannel() {
        TreeMap<Short, Channel> treeMap = (TreeMap<Short, Channel>) getChannelsAsMap();
        if (treeMap.size() > 0) {
            Short lastKey = treeMap.lastKey();
            return treeMap.get(lastKey);
        }
        return null;
    }

    public boolean isSimple() {
        return _simple;
    }

    public void setSimple(boolean simple) {
        _simple = simple;

    }

    public DataType getStructureType() {
        return DataType.valueOf(_structureType);
    }

    public void setStructureType(DataType type) {
        if (type == null) {
            _structureType = DataType.BIT.name();
        } else {
            _structureType = type.name();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.config.ioconfig.model.Node#toString()
     */
    @Override
    public String toString() {
        Channel channel = getChannelsAsMap().get((short) 0);
        StringBuilder sb = new StringBuilder();
        if (channel != null) {
            sb.append(channel.getFullChannelNumber());
            sb.append(":");
        }
        sb.append(getName() + " (" + getStructureType().name() + " structure)");

        for (NamedDBClass node : getChildrenAsMap().values()) {
            sb.append(LINE_SEPARATOR);
            sb.append("\t- ");
            sb.append(node.toString());
        }

        return sb.toString();
    }

    @Override
    public void setCreatedBy(String createdBy) {
        super.setCreatedBy(createdBy);
        for (NamedDBClass node : getChildren()) {
            node.setCreatedBy(createdBy);
            // PersistentAndUpdateHelper.addUnsavedNode(node);
        }
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        super.setUpdatedBy(updatedBy);
        for (NamedDBClass node : getChildren()) {
            node.setUpdatedBy(updatedBy);
            // PersistentAndUpdateHelper.addUnsavedNode(node);
        }
    }

    @Override
    public void setCreatedOn(Date createdOn) {
        super.setCreatedOn(createdOn);
        for (Node node : getChildren()) {
            node.setCreatedOn(createdOn);
        }
    }

    @Override
    public void setUpdatedOn(Date updatedOn) {
        super.setUpdatedOn(updatedOn);
        for (Node node : getChildren()) {
            node.setUpdatedOn(updatedOn);
        }
    }

    @Override
    public short getfirstFreeStationAddress(int maxStationAddress) {
        if (isSimple()) {
            return getSortIndex();
        }
        return super.getfirstFreeStationAddress(maxStationAddress);
    }

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
    public Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof Module) {
            Module module = (Module) parentNode;
            ChannelStructure copy = new ChannelStructure(module, isSimple(), true,
                    getStructureType(), getName());
            copy.removeAllChild();
            for (Node node : getChildren()) {
                node.copyThisTo(copy);
            }
            return copy;
        }
        return null;
    }

}
