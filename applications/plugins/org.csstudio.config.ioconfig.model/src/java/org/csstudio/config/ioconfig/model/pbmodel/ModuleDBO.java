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
 * $Id: Module.java,v 1.5 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.hibernate.annotations.BatchSize;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 22.03.2007
 */

@Entity
@BatchSize(size=32)
@Table(name = "ddb_Profibus_Module")
public class ModuleDBO extends AbstractNodeDBO {

    /**
     * The number of module at the GSD File.
     */
    private int _moduleNumber = -1;

    private List<Integer> _configurationData = new ArrayList<Integer>();

    private int _inputSize;

    private int _outputSize;

    private int _inputOffset;

    private int _outputOffset;

    @Transient
    private String _extModulePrmDataLen;

    /**
     * This Constructor is only used by Hibernate. To create an new {@link ModuleDBO}
     * {@link #Module(SlaveDBO)}
     */
    public ModuleDBO() {
    }

    /**
     * The default Constructor.
     * @param slave the parent Slave.
     * @throws PersistenceException 
     */
    public ModuleDBO(final SlaveDBO slave) throws PersistenceException {
        this(slave,null);
    }

    /**
     * This Constructor set the parent and the name of this node.
     * @param slave The parent Salve
     * @param name the name of this Module.
     * @throws PersistenceException 
     */
    public ModuleDBO(final SlaveDBO slave, final String name) throws PersistenceException {
        setParent(slave);
        setName(name);
        slave.addChild(this);
    }

    /*
     * Die length ergibt sich daraus das die ConfigurationData maxmal 20 byte enthalten darf. Da
     * jedes Byte als Hex String (z.B. 0x01) gespeichert wird muss dieser Wert mal 4 genommen
     * werden. Weiter sind die Werte per Komma getrennt was bis zu weitern 19 Stellen erfordern
     * kann. length = 204+19
     */
    @Column(name = "cfg_data", length = 99)
    public String getConfigurationData() {
        StringBuilder sb = new StringBuilder();
        for (Integer value : _configurationData) {
            sb.append(String.format("0x%02X,", value));
        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    /**
     * @return
     */
    @Transient
    public List<Integer> getConfigurationDataList() {
        // TODO Auto-generated method stub
        return _configurationData;
    }

    
    public void setConfigurationData(@CheckForNull final String configurationData) {
        if (configurationData != null && !configurationData.trim().isEmpty()) {
            String[] split = configurationData.split(",");
            _configurationData = new ArrayList<Integer>();
            for (String value : split) {
                _configurationData.add(GsdFileParser.gsdValue2Int(value));
            }
        }
    }
    
    @Transient
    public void setConfigurationDataByte(Integer index, Integer value) {
        _configurationData.set(index, value);
    }

    
    @Transient
    public void setConfigurationData(@Nonnull final List<Integer> configurationDataList) {
        _configurationData = configurationDataList;
    }

    /**
     *
     * @return the input offset
     * @throws PersistenceException 
     */
    @Transient
    public int getInputOffsetNH() throws PersistenceException {
        if (getSlave() != null) {
            ModuleDBO module = null;
            int sub = 1;
            while((module==null)&&((getSortIndex()-sub)>=0)) {
                module = (ModuleDBO) getSlave().getChildrenAsMap().get((short) (getSortIndex() - sub));
                sub++;
            }
            if (module != null) {
                int inputOffset = module.getInputOffsetNH() + module.getInputSize();
//                setInputOffset(inputOffset);
                return inputOffset;
            }
        }
        return 0;
    }

    /**
     *
     * @return the input offset
     */
    public int getInputOffset() {
        return _inputOffset;
    }

    /**
     *
     * @param inputOffset set the input offset.
     */
    public void setInputOffset(final int inputOffset) {
        this._inputOffset = inputOffset;
    }


    @Transient
    public int getOutputOffsetNH() throws PersistenceException {
        if (getSlave() != null) {
            ModuleDBO module = null;
            int sub = 1;
            while((module==null)&&((getSortIndex()-sub)>=0)) {
                module = (ModuleDBO) getSlave().getChildrenAsMap().get((short) (getSortIndex() - sub));
                sub++;
            }
            if (module != null) {
                int outputOffset = module.getOutputOffsetNH() + module.getOutputSize();
//                setOutputOffset(outputOffset);
                return outputOffset;
            }
        }
        return 0;
    }

    public int getOutputOffset() {
        return _outputOffset;
    }

    public void setOutputOffset(final int outputOffset) {
        this._outputOffset = outputOffset;
    }

    public int getInputSize() {
        return _inputSize;
    }

    public void setInputSize(final int inputSize) {
        this._inputSize = inputSize;
    }

    public int getOutputSize() {
        return _outputSize;
    }

    public void setOutputSize(final int outputSize) {
        this._outputSize = outputSize;
    }

    public int getModuleNumber() {
        return _moduleNumber;
    }

    public void setModuleNumber(final int moduleNumber) {
        _moduleNumber = moduleNumber;
    }

    @Transient
    public GSDModuleDBO getGSDModule() {
        return getGSDFile().getGSDModule(getModuleNumber());
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Set<ChannelStructureDBO> getChannelStructs() {
        return (Set<ChannelStructureDBO>) getChildren();
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Map<Short, ChannelStructureDBO> getChannelStructsAsMap() throws PersistenceException {
        return (Map<Short, ChannelStructureDBO>) getChildrenAsMap();
    }


    @ManyToOne
    public SlaveDBO getSlave() {
        return (SlaveDBO) getParent();
    }

    public void setSlave(final SlaveDBO slave) {
        this.setParent(slave);
    }

    /**
     *
     * @return the Slave GSD File.
     */
    @Transient
    public GSDFileDBO getGSDFile() {
        return getSlave().getGSDFile();
    }

    @Transient
    public String getExtModulePrmDataLen() {
        return _extModulePrmDataLen;
    }

    /**
     * @param trim
     */
    public void setExtModulePrmDataLen(final String extModulePrmDataLen) {
        _extModulePrmDataLen = extModulePrmDataLen;
    }

    @Transient
    public String getEpicsAddressString() {
        /** contribution to ioName (PV-link to EPICSORA) */
        try {
            return getSlave().getEpicsAdressString();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Transient
    @CheckForNull
    public GsdModuleModel2 getGsdModuleModel2() throws IOException {
        try {
            GsdModuleModel2 module = getSlave().getGSDFile().getParsedGsdFileModel().getModule(getModuleNumber());
//            if (module == null) {
//                module = getSlave().getGSDFile().getParsedGsdFileModel().getModule(getModuleNumber());
//            }
            return module;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Transient
    public GsdModuleModel getGsdModuleModel() {
        try {
            HashMap<Integer, GsdModuleModel> gsdModuleList = getSlave().getGSDSlaveData().getGsdModuleList();
            if (gsdModuleList.containsKey(getModuleNumber())) {
                return gsdModuleList.get(getModuleNumber());
            }
            return gsdModuleList.values().iterator().next();
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    @Transient
    public short getMaxOffset() {
        if (getGsdModuleModel() != null) {
            short offset = getSortIndex();
            SlaveCfgData slaveCfgData = new SlaveCfgData(getGsdModuleModel().getValue());
            int byteMulti = 1;
            if (slaveCfgData.isWordSize()) {
                byteMulti = 2;
            }
            offset += (slaveCfgData.getNumber() * byteMulti);
            return offset;
        }
        return -1;
    }

    @Override
    public AbstractNodeDBO copyThisTo(final AbstractNodeDBO parentNode) throws PersistenceException {
        AbstractNodeDBO copy = super.copyThisTo(parentNode);
        copy.setName(getName());
        return copy;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public AbstractNodeDBO copyParameter(final NamedDBClass parentNode) throws PersistenceException {
        if (parentNode instanceof SlaveDBO) {
            SlaveDBO slave = (SlaveDBO) parentNode;
            ModuleDBO copy = new ModuleDBO(slave);
            copy.setModuleNumber(getModuleNumber());
            if(slave.getChildrenAsMap().get(getSortIndex())==null) {
                copy.setSortIndex((int)getSortIndex());
            }
//            copy.setDocuments(getDocuments());
            copy.setConfigurationData(getConfigurationData());
            copy.setExtModulePrmDataLen(getExtModulePrmDataLen());

            for (AbstractNodeDBO node: getChildrenAsMap().values()) {
                AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
                childrenCopy.setSortIndexNonHibernate(node.getSortIndex());

            }

            return copy;
        }
        return null;
    }

    @Override
    public void localUpdate() {
        // make Offset
        int input;
        int output;

        // make Size
        input = 0;
        output = 0;

        Set<ChannelStructureDBO> channelStructs = getChannelStructs();
        for (ChannelStructureDBO channelStructure : channelStructs) {
            Set<ChannelDBO> channels = channelStructure.getChannels();
            for (ChannelDBO channel : channels) {
                if (channel.isInput()) {
                    input  += channel.getChannelType().getBitSize();
                } else {
                    output += channel.getChannelType().getBitSize();
                }
            }
        }
        if (input/8 != getInputSize()) {
            setInputSize(input / 8);
        }
        if (output/8 != getOutputSize()) {
            setOutputSize(output / 8);
        }
    }

    @Override
    public void update() throws PersistenceException {
            super.update();
    }

    @Transient
    public Set<ChannelDBO> getPureChannels() {
        Set<ChannelDBO> result = new HashSet<ChannelDBO>();
        for (ChannelStructureDBO s : getChannelStructs()) {
            if (s.isSimple()) {
                result.addAll(s.getChannels());
            }
        }
        return result;
    }

    @Transient
    public String getExtUserPrmDataConst() {
        if(getConfigurationData()==null) {
            return getGsdModuleModel().getModiExtUserPrmDataConst().trim();
        }
        return getConfigurationData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public NodeType getNodeType() {
        return NodeType.MODULE;
    }

    /**
     * @return The Name of this Node.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (getSortIndex() != null) {
            sb.append(getSortIndex());
        }
        sb.append('[');
        sb.append(getModuleNumber());
        sb.append(']');
        if (getName() != null) {
            sb.append(':');
            sb.append(getName());
        }
        return sb.toString();
    }

}
