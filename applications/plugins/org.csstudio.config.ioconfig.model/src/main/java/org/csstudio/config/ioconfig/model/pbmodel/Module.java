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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel;

/**
 * @author gerke
 * @author $Author$
 * @version $Revision$
 * @since 22.03.2007
 */

@Entity
@Table(name = "ddb_Profibus_Module")
public class Module extends Node {

    /**
     * The number of module at the GSD File.
     */
    private int _moduleNumber;

    private String _configurationData;

    private int _inputSize;

    private int _outputSize;

    private int _inputOffset;

    private int _outputOffset;

    @Transient
    private String _extModulePrmDataLen;

    /**
     * This Constructor is only used by Hibernate. To create an new {@link Module}
     * {@link #Module(Slave)}
     */
    public Module() {
    }

    /**
     * The default Constructor.
     * @param slave the parent Slave.
     */
    public Module(Slave slave) {
        this(slave,null);
    }

    /**
     * This Constructor set the parent and the name of this node.
     * @param slave The parent Salve
     * @param name the name of this Module.
     */
    public Module(Slave slave, String name) {
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
        return _configurationData;
    }

    public void setConfigurationData(String configurationData) {
        _configurationData = configurationData;
    }

    /**
     * 
     * @return the input offset
     */
    @Transient
    public int getInputOffsetNH() {
        if (getSlave() != null) {
            Module module = null;
            int sub = 1;
            while(module==null&&(getSortIndex()-sub)>=0) {
                module = (Module) getSlave().getChildrenAsMap().get((short) (getSortIndex() - sub));
                sub++;
            }
            if (module != null) { 
                int inputOffset = module.getInputOffsetNH() + module.getInputSize();
                setInputOffset(inputOffset);
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
    public void setInputOffset(int inputOffset) {
        this._inputOffset = inputOffset;
    }


    @Transient
    public int getOutputOffsetNH() {
        if (getSlave() != null) {
            Module module = null;
            int sub = 1;
            while(module==null&&(getSortIndex()-sub)>=0) {
                module = (Module) getSlave().getChildrenAsMap().get((short) (getSortIndex() - sub));
                sub++;
            }
            if (module != null) { 
                int outputOffset = module.getOutputOffsetNH() + module.getOutputSize();
                setOutputOffset(outputOffset);
                return outputOffset;
            }
        }    
        return 0;
    }
    
    public int getOutputOffset() {
        return _outputOffset;
    }

    public void setOutputOffset(int outputOffset) {
        this._outputOffset = outputOffset;
    }

    public int getInputSize() {
        return _inputSize;
    }
    
    public void setInputSize(int inputSize) {
        this._inputSize = inputSize;
    }

    public int getOutputSize() {
        return _outputSize;
    }

    public void setOutputSize(int outputSize) {
        this._outputSize = outputSize;
    }

    public int getModuleNumber() {
        return _moduleNumber;
    }

    public void setModuleNumber(int moduleNumber) {
        if (_moduleNumber == moduleNumber) {
            return;
        }
        _moduleNumber = moduleNumber;
    }

    @Transient
    public GSDModule getGSDModule() {
        return getGSDFile().getGSDModule(getModuleNumber());
    }
    
    @Transient
    @SuppressWarnings("unchecked")
    public Set<ChannelStructure> getChannelStructs() {
        return (Set<ChannelStructure>) getChildren();
    }
    
    @Transient
    @SuppressWarnings("unchecked")
    public Map<Short, ChannelStructure> getChannelStructsAsMap() {
        return (Map<Short, ChannelStructure>) getChildrenAsMap();
    }


    @ManyToOne
    public Slave getSlave() {
        return (Slave) getParent();
    }

    public void setSlave(Slave slave) {
        this.setParent(slave);
    }

    /**
     * 
     * @return the Slave GSD File.
     */
    @Transient
    public GSDFile getGSDFile() {
        return getSlave().getGSDFile();
    }

    @Transient
    public String getExtModulePrmDataLen() {
        return _extModulePrmDataLen;
    }

    /**
     * @param trim
     */
    public void setExtModulePrmDataLen(String extModulePrmDataLen) {
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
    public GsdModuleModel getGsdModuleModel() {
        try {
            if (getSlave().getGSDSlaveData().getGsdModuleList().containsKey(getModuleNumber())) {
                return getSlave().getGSDSlaveData().getGsdModuleList().get(getModuleNumber());
            }
            return getSlave().getGSDSlaveData().getGsdModuleList().values().iterator().next();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof Slave) {
            Slave slave = (Slave) parentNode;
            Module copy = new Module(slave);
            copy.setModuleNumber(getModuleNumber());
//            copy.setDocuments(getDocuments());
            copy.setConfigurationData(getConfigurationData());
            copy.setExtModulePrmDataLen(getExtModulePrmDataLen());

            for (Node n: getChildren()) {
                n.copyThisTo(copy);
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

        Set<ChannelStructure> channelStructs = getChannelStructs();
        for (ChannelStructure channelStructure : channelStructs) {
            Set<Channel> channels = channelStructure.getChannels();
            for (Channel channel : channels) {
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
    public void update() {
            super.update();
    }

    @Transient
    public Set<Channel> getPureChannels() {
        Set<Channel> result = new HashSet<Channel>();
        for (ChannelStructure s : getChannelStructs()) {
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

}