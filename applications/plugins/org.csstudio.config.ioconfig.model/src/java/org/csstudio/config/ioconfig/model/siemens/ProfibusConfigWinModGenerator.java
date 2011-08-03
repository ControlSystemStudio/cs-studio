/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.model.siemens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.08.2010
 */
public class ProfibusConfigWinModGenerator {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProfibusConfigWinModGenerator.class);
    
    private static final String LINE_END = "\r\n";
    
    private final StringBuilder _winModConfig;
    private final StringBuilder _winModSlaveAdr;
    private int _slot;
    private int _id;
    private int _module;
    private int _lineNr;
    
    
    
    /**
     * Constructor.
     */
    public ProfibusConfigWinModGenerator(@Nonnull final String fileName) {
        _winModConfig = new StringBuilder(200);
        _winModSlaveAdr = new StringBuilder(200);
        _slot = 1;
        _lineNr = 0;
    }
    
    /**
     * @param winModSlaveAdr
     * @param id
     * @param module
     * @param io
     * @param lineNr
     * @param digital
     * @param bit
     */
    private void addAdr(@Nonnull final StringBuilder winModSlaveAdr, final int id, final int module,
                        @Nonnull final WinModChannel winModChannel, final int lineNr, final boolean digital) {
        final short bit = winModChannel.getBit();
        final int fullBytes = bit/8;
        final int bitsModifier = fullBytes*8;
        final int byt = winModChannel.getByteNo()+lineNr+fullBytes;
        winModSlaveAdr.append("'ID").append(id).append(".M").append(module).append(".").append(winModChannel.getIO()).append(" ").append(byt);
        if(digital) {
            winModSlaveAdr.append(".").append(bit-bitsModifier);
        }
        winModSlaveAdr.append("'");
    }
    
    public void addSymbol(final int lineNr,
                          @Nonnull final ChannelDBO channelDBO,
                          @Nonnull final WinModChannel winModChannel,
                          @Nonnull final String channelType) {
        _winModSlaveAdr.append(",");
        if(channelDBO.getIoName()==null) {
            addAdr(_winModSlaveAdr, _id,_module, winModChannel, lineNr, channelDBO.isDigital());
        }else {
            _winModSlaveAdr.append("'").append(channelDBO.getIoName());
            if(channelType.startsWith("D")&&winModChannel.getIO().endsWith("B")) {
                if(lineNr==4) {
                    _winModSlaveAdr.append("_Stat");
                }else {
                    _winModSlaveAdr.append("_Byte").append(lineNr);
                }
            }
            _winModSlaveAdr.append("'");
        }
    }
    
    private void appendAddLine(final int lineNr, @Nonnull final ChannelDBO channelDBO, @Nonnull final WinModChannel winModChannel, @Nonnull final String channelType) {
        appendLine(lineNr, channelDBO, winModChannel, channelType, true);
    }
    
    /**
     * @param fileInput
     * @param length
     */
    private void appendAs2HexString(@Nonnull final StringBuilder fileInput, final int length) {
        final String hexSize = String.format("%04X", length);
        // lower Bytes of size
        fileInput.append(hexSize.substring(2)).append(" ")
        // higher Bytes of size
        .append(hexSize.substring(0, 2));
    }
    
    /**
     * @param normslaveParamDataSize
     * @param configurationData
     * @return
     */
    public int appendConfigurationData(final int normslaveParamDataSize, @CheckForNull final String configurationData) {
        int size = normslaveParamDataSize;
        if (configurationData != null) {
            size += configurationData.split(",").length;
            final String cleanConfigData = cleanString(configurationData);
            _winModConfig.append(cleanConfigData).append(" ");
        }
        return size;
    }
    
    private void appendLine(final int lineNr, @Nonnull final ChannelDBO channelDBO, @Nonnull final WinModChannel winModChannel, @Nonnull final String channelType) {
        appendLine(lineNr, channelDBO, winModChannel, channelType, false);
    }
    
    private void appendLine(final int lineNr, @Nonnull final ChannelDBO channelDBO, @Nonnull final WinModChannel winModChannel, @Nonnull final String channelType, final boolean add) {
        _winModSlaveAdr.append(_lineNr++).append(",");
        // Treibersignal
        addAdr(_winModSlaveAdr, _id,_module, winModChannel, lineNr, channelDBO.isDigital());
        // Adresse
        _winModSlaveAdr.append(",");
        if(add) {
            addAdr(_winModSlaveAdr, _id,_module, winModChannel, lineNr, channelDBO.isDigital());
        }
        // Symbol
        addSymbol(lineNr, channelDBO, winModChannel, channelType);
        _winModSlaveAdr.append(",'").append(channelType).append("','").append(winModChannel.getDef()).append("',");
        if(channelDBO.getDescription()!=null && !channelDBO.getDescription().isEmpty()) {
            _winModSlaveAdr.append("'").append(winModChannel.getDesc()).append("'");
        }
        _winModSlaveAdr.append(LINE_END);
        
    }
    
    /**
     * @param slave
     * @param fdlAddress
     * @param modelName
     * @param gsdFileName
     */
    public void buildSlaveHeader(@Nonnull final SlaveDBO slave, final int fdlAddress, @Nonnull final String modelName, @Nonnull final String gsdFileName) {
        _winModConfig.append("DPSUBSYSTEM 1, ").append("DPADDRESS ")
        .append(fdlAddress).append(", \"")
        .append(gsdFileName)
        .append("\", \"").append(modelName)
        .append("\"").append(LINE_END)
        .append("BEGIN").append(LINE_END)
        .append("  PNO_IDENT_NO ").append("\"")
        .append(ProfibusConfigXMLGenerator.getInt(slave.getIDNo()))
        .append("\"").append(LINE_END);
    }
    
    /**
     * @param string2Clean
     * @return
     */
    @Nonnull
    public String cleanString(@Nonnull final String string2Clean) {
        final String cleanString = string2Clean
        .replaceAll("(0x)", "")
        .replaceAll(",", " ").replaceAll("  ", " ").trim();
        return cleanString;
    }
    
    /**
     * @param channelStructureDBO
     * @throws PersistenceException
     */
    private void createChannel(@Nonnull final ChannelStructureDBO channelStructureDBO) throws PersistenceException {
        final Map<Short, ChannelDBO> channelsAsMap = channelStructureDBO.getChildrenAsMap();
        final Set<Entry<Short, ChannelDBO>> entrySet = channelsAsMap.entrySet();
        for (final Entry<Short, ChannelDBO> entry : entrySet) {
            final ChannelDBO channelDBO = entry.getValue();
            final WinModChannel winModChannel = new WinModChannel(channelDBO);
            
            if(!winModChannel.single()) {
                appendLine(0, channelDBO, winModChannel, winModChannel.getConvertedChannelType());
                winModChannel.setDef("0");
                winModChannel.setIO2("B");
                for (int i = 0; i < winModChannel.getLineSize(); i++) {
                    appendAddLine(i, channelDBO, winModChannel, winModChannel.getMbbChannelType());
                }
            } else {
                appendLine(0, channelDBO, winModChannel, winModChannel.getConvertedChannelType());
            }
        }
    }
    
    /**
     * @param module
     * @throws PersistenceException
     */
    private void createModule(@Nonnull final ModuleDBO module, final int fdlAddress) throws PersistenceException {
        _module = module.getSortIndex()+1;
        List<Integer> slaveCfgData;
        final GsdModuleModel2 gsdModuleModel2 = module.getGsdModuleModel2();
        if (gsdModuleModel2 != null) {
            slaveCfgData = gsdModuleModel2.getValue();
            final int length = slaveCfgData.size();
            final GSDModuleDBO gsdModule = module.getGSDModule();
            if (gsdModule != null) {
                _winModConfig.append("DPSUBSYSTEM 1, ").append("DPADDRESS ")
                .append(fdlAddress + ", ").append("SLOT ").append(_slot++).append(", ")
                .append("\"" + gsdModule.getName() + "\"").append(LINE_END).append("BEGIN")
                .append(LINE_END).append("  SLAVE_CFG_DATA ").append("\"");
                appendAs2HexString(_winModConfig, length);
                _winModConfig
                .append(" ")
                .append(Arrays.toString(slaveCfgData.toArray()).replaceAll(",", " ")
                        .replaceAll("[\\[\\]]", "")).append("\"").append(LINE_END)
                        .append("  OBJECT_REMOVEABLE ").append("\"1\"").append(LINE_END)
                        .append("END").append(LINE_END).append(LINE_END);
            }
        }
        final Map<Short, ChannelStructureDBO> channelStructsAsMap = module.getChildrenAsMap();
        final Set<Entry<Short, ChannelStructureDBO>> entrySet = channelStructsAsMap.entrySet();
        for (final Entry<Short, ChannelStructureDBO> entry : entrySet) {
            createChannel(entry.getValue());
        }
    }
    
    /**
     * @throws PersistenceException
     *
     */
    private void createSlave(@Nonnull final SlaveDBO slave) throws PersistenceException {
        _id = slave.getSortIndex();
        _slot = 1;
        int normslaveParamDataSize = 0;
        int posNormslaveParamDataSize = 0;
        final int fdlAddress = slave.getFdlAddress();
        final Map<Short, ModuleDBO> childrenAsMap = slave.getChildrenAsMap();
        final Set<Entry<Short, ModuleDBO>> moduleEntrySet = childrenAsMap.entrySet();
        String modelName = slave.getModelName();
        if (modelName.length() > 24) {
            modelName = modelName.substring(0, 24);
        }
        final String gsdFileName = getGsdFileName(slave);
        buildSlaveHeader(slave, fdlAddress, modelName, gsdFileName);
        final String prmUserData = slave.getPrmUserData();
        if (!"Property not found".equals(prmUserData)) {
            final String cleanPrmUserData = cleanString(prmUserData);
            _winModConfig.append("  NORMSLAVE_PARAM_DATA ").append("\"");
            posNormslaveParamDataSize = _winModConfig.length();
            normslaveParamDataSize+=slave.getPrmUserDataList().size();
            _winModConfig.append(" ").append(cleanPrmUserData).append(" ");
            for (final Entry<Short, ModuleDBO> entry : moduleEntrySet) {
                final ModuleDBO module = entry.getValue();
                final String configurationData = module.getConfigurationData();
                normslaveParamDataSize =
                    appendConfigurationData(normslaveParamDataSize,
                                            configurationData);
            }
            _winModConfig.deleteCharAt(_winModConfig.length() - 1);
            _winModConfig.append("\"").append(LINE_END);
        }
        _winModConfig.append("END").append(LINE_END).append(LINE_END);
        final StringBuilder sb = new StringBuilder(5);
        appendAs2HexString(sb, normslaveParamDataSize);
        _winModConfig.insert(posNormslaveParamDataSize, sb.toString());
        for (final Entry<Short, ModuleDBO> entry : moduleEntrySet) {
            createModule(entry.getValue(), fdlAddress);
        }
    }
    /**
     * @param slave
     * @return
     */
    @Nonnull
    private String getGsdFileName(@Nonnull final SlaveDBO slave) {
        final GSDFileDBO gsdFile = slave.getGSDFile();
        return gsdFile==null?"no GSD File available":gsdFile.getName();
    }
    
    /**
     * 
     * @param path
     *            The target File Path.
     * @throws IOException
     */
    public final void getTxtFile(@Nonnull final File path) throws IOException {
        final FileWriter writer = new FileWriter(path);
        writer.append(_winModSlaveAdr.toString());
        LOG.info("Write File: {}", path.getAbsolutePath());
        writer.close();
    }
    
    /**
     * 
     * @param path
     *            The target File Path.
     * @throws IOException
     */
    public final void getXmlFile(@Nonnull final File path) throws IOException {
        final FileWriter writer = new FileWriter(path);
        writer.append(_winModConfig.toString());
        LOG.info("Write File: {}", path.getAbsolutePath());
        writer.close();
    }
    
    /**
     *
     * @param subnet
     *            The Profibus Subnet.
     * @throws PersistenceException
     */
    public final void setSubnet(@Nonnull final ProfibusSubnetDBO subnet) throws PersistenceException {
        _winModSlaveAdr.append(",'Treibersignal','Adresse','Symbol','Typ','Default Wert','Kommentar'").append(LINE_END);
        _lineNr++;
        final Set<MasterDBO> masterTree = subnet.getProfibusDPMaster();
        if ( masterTree == null || masterTree.size() < 1) {
            return;
        }
        
        for (final MasterDBO master : masterTree) {
            final Map<Short, SlaveDBO> slaves = master.getChildrenAsMap();
            for (final short key : slaves.keySet()) {
                final SlaveDBO slave = slaves.get(key);
                createSlave(slave);
            }
            
        }
    }
}
