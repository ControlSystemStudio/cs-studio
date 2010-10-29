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
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.platform.logging.CentralLogger;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.08.2010
 */
public class ProfibusConfigSiemensGenerator {

    private final StringBuilder _fileInput;
    private int _slot;
    private static final String LINE_END = "\r\n";

    /**
     * Constructor.
     */
    public ProfibusConfigSiemensGenerator(@Nonnull final String fileName) {
        _fileInput = new StringBuilder(200);
		_slot = 1;
    }

    /**
    *
    * @param subnet
    *            The Profibus Subnet.
    */
    public final void setSubnet(@Nonnull final ProfibusSubnetDBO subnet) {
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
     *
     */
    private void createSlave(@Nonnull final SlaveDBO slave) {
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
        _fileInput.append("DPSUBSYSTEM 1, ").append("DPADDRESS ")
        		  .append(fdlAddress).append(", \"")
        		  .append(slave.getGSDFile().getName())
        		  .append("\", \"").append(modelName)
        		  .append("\"").append(LINE_END)
        		  .append("BEGIN").append(LINE_END)
                  .append("  PNO_IDENT_NO ").append("\"")
                  .append(ProfibusConfigXMLGenerator.getInt(slave.getIDNo()))
                  .append("\"").append(LINE_END);
        if (!slave.getPrmUserData().equals("Property not found")) {
			String cleanPrmUserData = slave.getPrmUserData()
					.replaceAll("(0x)", "")
					.replaceAll(",", " ").trim();
			_fileInput.append("  NORMSLAVE_PARAM_DATA ").append("\"");
			posNormslaveParamDataSize = _fileInput.length();
			normslaveParamDataSize+=slave.getPrmUserDataList().size();
			_fileInput.append(" ").append(cleanPrmUserData).append(" ");
			for (short key : keySet) {
				ModuleDBO module = (ModuleDBO) childrenAsMap.get(key);
				String configurationData = module.getConfigurationData();
				if (configurationData != null) {
					normslaveParamDataSize += configurationData.split(",").length;
					String cleanConfigData = configurationData
							.replaceAll("(0x)", "")
							.replaceAll(",", "").trim();
					_fileInput.append(cleanConfigData).append(" ");
				}
			}
			_fileInput.deleteCharAt(_fileInput.length() - 1);
            _fileInput.append("\"").append(LINE_END);
        }
        _fileInput.append("END").append(LINE_END).append(LINE_END);
        StringBuilder sb = new StringBuilder(5);
        appendAs2HexString(sb, normslaveParamDataSize);
        _fileInput.insert(posNormslaveParamDataSize, sb.toString());
        for (short key : keySet) {
            createModule((ModuleDBO) childrenAsMap.get(key), fdlAddress);
        }
    }

    /**
     * @param module
     */
    private void createModule(@Nonnull final ModuleDBO module, final int fdlAddress) {
		String slaveCfgData = module.getGsdModuleModel().getValue()
				.replaceAll("0x", "");
		int length = slaveCfgData.split(",").length;
		_fileInput.append("DPSUBSYSTEM 1, ")
				.append("DPADDRESS ").append(fdlAddress + ", ")
				.append("SLOT ").append(_slot++).append(", ")
				.append("\"" + module.getGSDModule().getName() + "\"")
				.append(LINE_END)
				.append("BEGIN").append(LINE_END)
				.append("  SLAVE_CFG_DATA ").append("\"");
		appendAs2HexString(_fileInput, length);
		_fileInput.append(" ").append(slaveCfgData.replaceAll(",", " "))
				.append("\"").append(LINE_END)
				.append("  OBJECT_REMOVEABLE ").append("\"1\"")
				.append(LINE_END).append("END").append(LINE_END).append(LINE_END);
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
        writer.append(_fileInput.toString());
        CentralLogger.getInstance().info(this, "Write File:" + path.getAbsolutePath());
        writer.close();
    }
}
