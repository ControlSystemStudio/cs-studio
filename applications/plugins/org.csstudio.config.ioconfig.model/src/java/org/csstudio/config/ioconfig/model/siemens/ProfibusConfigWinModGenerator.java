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
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.08.2010
 */
public class ProfibusConfigWinModGenerator {

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
    *
    * @param subnet
    *            The Profibus Subnet.
     * @throws PersistenceException 
    */
    public final void setSubnet(@Nonnull final ProfibusSubnetDBO subnet) throws PersistenceException {
        _winModSlaveAdr.append(",'Treibersignal','Adresse','Symbol','Typ','Default Wert','Kommentar'").append(LINE_END);
        _lineNr++;
        Set<MasterDBO> masterTree = subnet.getProfibusDPMaster();
        if ( (masterTree == null) || (masterTree.size() < 1)) {
            return;
        }

        for (MasterDBO master : masterTree) {
            Map<Short, ? extends AbstractNodeDBO> slaves = master.getChildrenAsMap();
            for (short key : slaves.keySet()) {
                SlaveDBO slave = (SlaveDBO) slaves.get(key);
                createSlave(slave);
            }

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
        int fdlAddress = slave.getFdlAddress();
        Map<Short, ? extends AbstractNodeDBO> childrenAsMap = slave.getChildrenAsMap();
        Set<Short> keySet = childrenAsMap.keySet();
        String modelName = slave.getModelName();
        if (modelName.length() > 24) {
            modelName = modelName.substring(0, 24);
        }

        _winModConfig.append("DPSUBSYSTEM 1, ").append("DPADDRESS ")
        		  .append(fdlAddress).append(", \"")
        		  .append(slave.getGSDFile().getName())
        		  .append("\", \"").append(modelName)
        		  .append("\"").append(LINE_END)
        		  .append("BEGIN").append(LINE_END)
                  .append("  PNO_IDENT_NO ").append("\"")
                  .append(ProfibusConfigXMLGenerator.getInt(slave.getIDNo()))
                  .append("\"").append(LINE_END);
        String prmUserData = slave.getPrmUserData();
        if (!prmUserData.equals("Property not found")) {
			String cleanPrmUserData = prmUserData
					.replaceAll("(0x)", "")
					.replaceAll(",", " ").replaceAll("  ", " ").trim();
			_winModConfig.append("  NORMSLAVE_PARAM_DATA ").append("\"");
			posNormslaveParamDataSize = _winModConfig.length();
			normslaveParamDataSize+=slave.getPrmUserDataList().size();
			_winModConfig.append(" ").append(cleanPrmUserData).append(" ");
			for (short key : keySet) {
				ModuleDBO module = (ModuleDBO) childrenAsMap.get(key);
				String configurationData = module.getConfigurationData();
				if (configurationData != null) {
					normslaveParamDataSize += configurationData.split(",").length;
					String cleanConfigData = configurationData
							.replaceAll("(0x)", "")
							.replaceAll(",", " ").replaceAll("  ", " ").trim();
					_winModConfig.append(cleanConfigData).append(" ");
				}
			}
			_winModConfig.deleteCharAt(_winModConfig.length() - 1);
            _winModConfig.append("\"").append(LINE_END);
        }
        _winModConfig.append("END").append(LINE_END).append(LINE_END);
        StringBuilder sb = new StringBuilder(5);
        appendAs2HexString(sb, normslaveParamDataSize);
        _winModConfig.insert(posNormslaveParamDataSize, sb.toString());
        for (short key : keySet) {
            createModule((ModuleDBO) childrenAsMap.get(key), fdlAddress);
        }
    }

    /**
     * @param module
     * @throws PersistenceException 
     */
    private void createModule(@Nonnull final ModuleDBO module, final int fdlAddress) throws PersistenceException {
    	_module = module.getSortIndex()+1;
		List<Integer> slaveCfgData;
        try {
            slaveCfgData = module.getGsdModuleModel2().getValue();
            int length = slaveCfgData.size();
    		if (module.getGSDModule() != null) {
    			_winModConfig.append("DPSUBSYSTEM 1, ").append("DPADDRESS ")
    					.append(fdlAddress + ", ").append("SLOT ").append(_slot++)
    					.append(", ")
    					.append("\"" + module.getGSDModule().getName() + "\"")
    					.append(LINE_END).append("BEGIN").append(LINE_END)
    					.append("  SLAVE_CFG_DATA ").append("\"");
    			appendAs2HexString(_winModConfig, length);
    			_winModConfig.append(" ").append(Arrays.toString(slaveCfgData.toArray()).replaceAll(",", " ").replaceAll("[\\[\\]]", ""))
    			.append("\"").append(LINE_END)
    			.append("  OBJECT_REMOVEABLE ").append("\"1\"")
    			.append(LINE_END).append("END").append(LINE_END)
    			.append(LINE_END);
    		}
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
		Map<Short, ChannelStructureDBO> channelStructsAsMap = module.getChildrenAsMap();
		Set<Short> keySet = channelStructsAsMap.keySet();
		for (Short key : keySet) {
			createChannel(channelStructsAsMap.get(key));
		}
    }

	/**
	 * @param channelStructureDBO
	 * @throws PersistenceException 
	 */
	private void createChannel(ChannelStructureDBO channelStructureDBO) throws PersistenceException {
		Map<Short, ChannelDBO> channelsAsMap = channelStructureDBO.getChildrenAsMap();
		Set<Short> keySet = channelsAsMap.keySet();
		for (Short key : keySet) {
			ChannelDBO channelDBO = channelsAsMap.get(key);
			char io1;
			String io2="";
			String def;
			String convertedChannelType;
			String mbbChannelType;
			String desc = "";
			int lines = 1;
			DataType channelType = channelDBO.getChannelType();
			switch (channelType) {
			case BIT:
				convertedChannelType = "B"; // Binary
				break;
			case INT8:
			case UINT8:
			    convertedChannelType = "A"; // Analog
			    break;
			case INT16:
			case UINT16:
				convertedChannelType = "A"; // Analog
				lines = 2;
				break;
			case INT32:
			case UINT32:
				lines = 4;
				convertedChannelType = "A"; // Analog
				break;
			case DS33:
				lines = channelType.getByteSize();
				desc = "> "+channelType;
				convertedChannelType = "A"; // Analog
				break;
			default:
				convertedChannelType = "A"; // Analog
				break;
			}
			if(channelDBO.isInput()) {
					io1 = 'E';              // Eingang
					convertedChannelType += "I"; // Input
					mbbChannelType = "DI";
			} else {
					io1 = 'A';              // Ausgang
					convertedChannelType += "O"; // Output
					mbbChannelType = "DO";
			}

			if(channelDBO.isDigital()) {
				def = "0";
			}else {
				def = "0,00";
				int byteSize = channelType.getByteSize();
				switch (byteSize) {
				case 1:
					io2 = "B"; // Byte
					break;
				case 2:
					io2 = "W"; // Word
					break;
				case 4:
					io2 = "D"; // Double Word
					break;
				case 5:
					io2 = "D"; // TODO: hier kommt sicherlich was anderes hin!
					break;
				default:
					break;
				}
			}
			
			String description = channelDBO.getDescription();
			if(description!=null) {
				desc += description.replaceAll("[\r\n]", " ");
			}
//			int bytee = channelDBO.getStatusAddress()+1; //XXX ist Falsch!!! 
			
			int bytee;
			Short bit = 0;
			if(channelDBO.isDigital()) {
				bit = channelDBO.getSortIndex();
//				bytee = bit / 8;
//				if(bytee>0) {
//					bit = (short) (bit - (8*bytee));
//				}
				
//				bytee = channelDBO.getFullChannelNumber();
				
				bytee = channelDBO.getChannelNumber();
			} else {
//				bytee = channelDBO.getStruct();
				bytee = channelDBO.getFullChannelNumber();
				bytee = channelDBO.getChannelNumber();
			}
			
			String io = io1+""+io2;
			if(lines>1) {
			    appendLine(0, io, bytee, channelDBO, bit, convertedChannelType, def, desc);
			    def = "0";
			    io = io1+"B";
    			for (int i = 0; i < lines; i++) {
    				appendAddLine(i, io, bytee, channelDBO, bit, mbbChannelType, def, desc);
    			}
			} else { 
			    appendAddLine(0, io, bytee, channelDBO, bit, convertedChannelType, def, desc);
			}
		}
	}

	/**
	 * @param lineNr
	 * @param bit 
	 * @param channelDBO 
	 * @param bytee 
	 * @param io 
	 * @param def 
	 * @param channelType 
	 * @param desc 
	 */
	private void appendLine(int lineNr, String io, int bytee, ChannelDBO channelDBO, Short bit, String channelType, String def, String desc) {
	    _winModSlaveAdr.append(_lineNr++).append(",");
	    // Treibersignal
	    addAdr(_winModSlaveAdr, _id,_module, io, bytee+lineNr, channelDBO.isDigital(), bit);
	       // Adresse
        _winModSlaveAdr.append(",");
        // Symbol
        _winModSlaveAdr.append(",");
        if(channelDBO.getIoName()==null) {
            addAdr(_winModSlaveAdr, _id,_module, io, bytee+lineNr, channelDBO.isDigital(), bit);
        }else {
            _winModSlaveAdr.append("'").append(channelDBO.getIoName());
//          if(lineNr>0) {
            if(channelType.startsWith("D")&&io.endsWith("B")) {
                if(lineNr==4) {
                    _winModSlaveAdr.append("_Stat");
                }else {
                    _winModSlaveAdr.append("_Byte").append(lineNr);
                }
            }
            _winModSlaveAdr.append("'");
        }
        _winModSlaveAdr.append(",'").append(channelType).append("','").append(def).append("',");
        if(channelDBO.getDescription()!=null && !channelDBO.getDescription().isEmpty()) {
            _winModSlaveAdr.append("'").append(desc).append("'");
        }
        _winModSlaveAdr.append(LINE_END);
	}
	
	private void appendAddLine(int lineNr, String io, int bytee, ChannelDBO channelDBO, Short bit, String channelType, String def, String desc) {
		_winModSlaveAdr.append(_lineNr++).append(",");
		// Treibersignal
		addAdr(_winModSlaveAdr, _id,_module, io, bytee+lineNr, channelDBO.isDigital(), bit);

		// Adresse
		_winModSlaveAdr.append(",");
		addAdr(_winModSlaveAdr, _id,_module, io, bytee+lineNr, channelDBO.isDigital(), bit);
		
		// Symbol
		_winModSlaveAdr.append(",");
		if(channelDBO.getIoName()==null) {
			addAdr(_winModSlaveAdr, _id,_module, io, bytee+lineNr, channelDBO.isDigital(), bit);
		}else {
			_winModSlaveAdr.append("'").append(channelDBO.getIoName());
//			if(lineNr>0) {
			if(channelType.startsWith("D")&&io.endsWith("B")) {
			    if(lineNr==4) {
			        _winModSlaveAdr.append("_Stat");
			    }else {
			        _winModSlaveAdr.append("_Byte").append(lineNr);
			    }
			}
			_winModSlaveAdr.append("'");
		}
		_winModSlaveAdr.append(",'").append(channelType).append("','").append(def).append("',");
		if(channelDBO.getDescription()!=null && !channelDBO.getDescription().isEmpty()) {
			_winModSlaveAdr.append("'").append(desc).append("'");
		}
		_winModSlaveAdr.append(LINE_END);
	}

	/**
	 * @param winModSlaveAdr
	 * @param id
	 * @param module
	 * @param io
	 * @param bytee
	 * @param digital
	 * @param bit
	 */
	private void addAdr(@Nonnull StringBuilder winModSlaveAdr, int id, int module,
			@Nonnull String io, int bytee, boolean digital, short bit) {
	    int fullBytes = bit/8;
	    int bitsModifier = fullBytes*8; 
        int byt = bytee+fullBytes;
		winModSlaveAdr.append("'ID").append(id).append(".M").append(module).append(".").append(io).append(" ").append(byt);
		if(digital) {
			winModSlaveAdr.append(".").append(bit-bitsModifier);
		}
		winModSlaveAdr.append("'");
	}

	/**
	 * @param fileInput
	 * @param length
	 */
	private void appendAs2HexString(@Nonnull StringBuilder fileInput, int length) {
		String hexSize = String.format("%04X", length);
				// lower Bytes of size
		fileInput.append(hexSize.substring(2)).append(" ")
				// higher Bytes of size
				.append(hexSize.substring(0, 2));
	}

	/**
	 * 
	 * @param path
	 *            The target File Path.
	 * @throws IOException
	 */
    public final void getXmlFile(@Nonnull final File path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.append(_winModConfig.toString());
        CentralLogger.getInstance().info(this, "Write File:" + path.getAbsolutePath());
        writer.close();
    }

    /**
     * 
     * @param path
     *            The target File Path.
     * @throws IOException
     */
    public final void getTxtFile(@Nonnull final File path) throws IOException {
    	FileWriter writer = new FileWriter(path);
    	writer.append(_winModSlaveAdr.toString());
    	CentralLogger.getInstance().info(this, "Write File:" + path.getAbsolutePath());
    	writer.close();
    }
}
