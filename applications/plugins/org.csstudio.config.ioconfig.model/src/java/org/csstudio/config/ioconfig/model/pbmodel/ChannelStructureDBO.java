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
 * $Id: ChannelStructure.java,v 1.8 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.hibernate.annotations.BatchSize;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.8 $
 * @since 18.12.2008
 */
@Entity
@BatchSize(size=32)
@Table(name = "ddb_Profibus_Channel_Structure")
public class ChannelStructureDBO extends AbstractNodeDBO<ModuleDBO, ChannelDBO> {

    private static final long serialVersionUID = 1L;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private String _structureType;
    private boolean _simple;

    /**
     * Default Constructor used only by Hibernate. To crate a {@link ChannelStructureDBO} use the
     * Factory methods.
     */
    public ChannelStructureDBO() {
        // Constructor for Hibernate
    }

    private ChannelStructureDBO(@Nonnull final ModuleDBO module, final boolean simple, final boolean isInput, @Nonnull final DataType type,
                                @Nonnull final String name) throws PersistenceException {
        this(module, simple, isInput, type, name, DEFAULT_MAX_STATION_ADDRESS);
    }

    private ChannelStructureDBO(@Nonnull final ModuleDBO module, final boolean simple, final boolean isInput, @Nonnull final DataType type,
                                @Nonnull final String name, final int defaultMaxStationAddress) throws PersistenceException {
        super(module);
        setSimple(simple);
        setName("Struct of " + name);
        setStructureType(type);

        buildChildren(type, isInput, name);

    }

    @Nonnull 
    public static ChannelStructureDBO makeSimpleChannel(@Nonnull final ModuleDBO module, final boolean isInput) throws PersistenceException {
        return makeSimpleChannel(module, "", isInput, false);
    }

    @SuppressWarnings("unused")
    @Nonnull 
    public static ChannelStructureDBO makeSimpleChannel(@Nonnull final ModuleDBO module, 
                                                        @Nonnull final String name, 
                                                        final boolean isInput,
                                                        final boolean isDigit) throws PersistenceException {
        ChannelStructureDBO channelStructure = new ChannelStructureDBO(module, true, isInput,
                DataType.SIMPLE, name);
        new ChannelDBO(channelStructure, name, isInput, isDigit, channelStructure.getSortIndex());
        return channelStructure;
    }

    @Nonnull 
    public static ChannelStructureDBO makeChannelStructure(@Nonnull final ModuleDBO module, final boolean isInput,
                                                           @Nonnull final DataType type, @Nonnull final String name) throws PersistenceException {
        ChannelStructureDBO channelStructureDBO = new ChannelStructureDBO(module, false, isInput, type, name, DEFAULT_MAX_STATION_ADDRESS);
        channelStructureDBO.buildChildren(type, isInput, name);
        return channelStructureDBO;
    }

    private void buildChildren(@Nonnull final DataType type, 
                               final boolean isInput, 
                               @Nonnull final String name) throws PersistenceException {
        if (isSimple()) {
            return;
        }

        DataType[] structer = type.getStructure();
//        Channel channel = new Channel(this, name, isInput, structer[0].getByteSize() < 8, (short)-1);
        for (short sortIndex = 0; sortIndex < structer.length; sortIndex++) {
            ChannelDBO channel = new ChannelDBO(this, 
                                                name + sortIndex, 
                                                isInput, 
                                                structer[sortIndex].getByteSize() < 8, 
                                                sortIndex);

            channel.setName(name + sortIndex);
            // Use setChannelType to reduce the local Updates.
            // Make a local Update after add all Channels.
            channel.setChannelType(structer[sortIndex]);
        }
        getModule().update();
        getModule().update();
    }

    /**
     *
     * @return the parent Module.
     */
    @ManyToOne
    @Nonnull 
    public ModuleDBO getModule() {
        return (ModuleDBO) getParent();
    }

    /**
     *
     * @param module
     *            the parent Module.
     */
    public void setModule(@Nonnull final ModuleDBO module) {
        this.setParent(module);
    }

    @Transient
    @CheckForNull 
    public ChannelDBO getFirstChannel() throws PersistenceException {
        TreeMap<Short, ChannelDBO> treeMap = (TreeMap<Short, ChannelDBO>) getChildrenAsMap();
        return treeMap.get(treeMap.firstKey());
    }

    @Transient
    @CheckForNull 
    public ChannelDBO getLastChannel() throws PersistenceException {
        TreeMap<Short, ChannelDBO> treeMap = (TreeMap<Short, ChannelDBO>) getChildrenAsMap();
        if (treeMap.size() > 0) {
            Short lastKey = treeMap.lastKey();
            return treeMap.get(lastKey);
        }
        return null;
    }

    public boolean isSimple() {
        return _simple;
    }

    public void setSimple(final boolean simple) {
        _simple = simple;

    }

    @Nonnull 
    public DataType getStructureType() {
        return DataType.valueOf(_structureType);
    }

    public void setStructureType(@CheckForNull final DataType type) {
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
    @Nonnull 
    public String toString() {
        ChannelDBO channel;
        StringBuilder sb = new StringBuilder();
        try {
            channel = getChildrenAsMap().get((short) 0);
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
            
        } catch (PersistenceException e) {
            sb.append("Device Database ERROR: ");
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    @Override
    public void setCreatedBy(@Nonnull final String createdBy) {
        super.setCreatedBy(createdBy);
        for (NamedDBClass node : getChildren()) {
            node.setCreatedBy(createdBy);
        }
    }

    @Override
    public void setUpdatedBy(@Nonnull final String updatedBy) {
        super.setUpdatedBy(updatedBy);
        for (NamedDBClass node : getChildren()) {
            node.setUpdatedBy(updatedBy);
        }
    }

    @Override
    public void setCreatedOn(@Nonnull final Date createdOn) {
        super.setCreatedOn(createdOn);
        for (ChannelDBO node : getChildren()) {
            node.setCreatedOn(createdOn);
        }
    }

    @Override
    public void setUpdatedOn(@Nonnull final Date updatedOn) {
        super.setUpdatedOn(updatedOn);
        for (ChannelDBO node : getChildren()) {
            node.setUpdatedOn(updatedOn);
        }
    }

    @Override
    public int getfirstFreeStationAddress(final int maxStationAddress) throws PersistenceException {
        if (isSimple()) {
            return getSortIndex();
        }
        return super.getfirstFreeStationAddress(maxStationAddress);
    }

    @Override
    @Nonnull 
    public ChannelStructureDBO copyThisTo(@Nonnull final ModuleDBO parentNode) throws PersistenceException {
        ChannelStructureDBO copy = (ChannelStructureDBO) super.copyThisTo(parentNode);
        copy.setName(getName());
        return copy;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull 
    public ChannelStructureDBO copyParameter(@Nonnull final ModuleDBO parentNode) throws PersistenceException {
        ModuleDBO module = parentNode;
        String name = getName();
        if(name==null){
            name="";
        }
        
        ChannelStructureDBO copy = new ChannelStructureDBO(module,
                                                           isSimple(),
                                                           true,
                                                           getStructureType(),
                                                           name);
        copy.setSortIndex((int) getSortIndex());
        copy.removeAllChild();
        for (ChannelDBO node : getChildrenAsMap().values()) {
            ChannelDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    @Override
    protected void localUpdate() throws PersistenceException {
        Collection<ChannelDBO> values = getChildrenAsMap().values();
        for (ChannelDBO channel : values) {
            channel.localUpdate();
        }
    }

    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        for (ChannelDBO node : getChildrenAsMap().values()) {
            if (node != null) {
                node.localUpdate();
                if (node.isDirty()) {
                    node.save();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.CHANNEL_STRUCTURE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ChannelDBO createChild() throws PersistenceException {
        throw new UnsupportedOperationException("No simple child can be created for node type " + getClass().getName());
    }
    
    // CHECKSTYLE OFF: StrictDuplicateCode
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(@CheckForNull Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    // CHECKSTYLE ON: StrictDuplicateCode 
}
