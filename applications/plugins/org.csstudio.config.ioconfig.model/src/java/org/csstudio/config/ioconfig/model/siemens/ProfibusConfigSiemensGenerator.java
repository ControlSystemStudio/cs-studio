/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.model.siemens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
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

    private final String _fileName;
    private final StringBuilder _fileInput;
    private int _slot;
    private static final String LINE_END = "\r\n";

    /**
     * Constructor.
     */
    public ProfibusConfigSiemensGenerator(final String fileName) {
        _fileName = fileName;
        _fileInput = new StringBuilder(200);
        _slot = 0;
    }

    /**
    *
    * @param subnet
    *            The Profibus Subnet.
    */
    public final void setSubnet(final ProfibusSubnetDBO subnet) {
        Set<MasterDBO> masterTree = subnet.getProfibusDPMaster();
        if ( (masterTree == null) || (masterTree.size() < 1)) {
            return;
        }

        for (MasterDBO master : masterTree) {
            Set<SlaveDBO> slaves = master.getSlaves();
            for (SlaveDBO slave : slaves) {
                createSlave(slave);
            }

        }
    }

    /**
     *
     */
    private void createSlave(final SlaveDBO slave) {
        int fdlAddress = slave.getFdlAddress();
        _slot=0;
        _fileInput.append("DPSUBSYSTEM 1, ")
                  .append("DPADDRESS ")
                  .append(fdlAddress + ", ")
                  .append("\""+slave.getGSDFile().getName()+"\", ")
                  .append("\"TODO: ??? Optional ???\"")
                  .append(LINE_END)
                  .append("BEGIN")
                  .append(LINE_END)
                  .append("  PNO_IDENT_NO ")
                  .append("\""+slave.getIDNo()+"\"")
                  .append(LINE_END);
        if (!slave.getPrmUserData().equals("Property not found")) {
            _fileInput.append("  NORMSLAVE_PARAM_DATA ").append("\"" + slave.getPrmUserData()
                    + "\"")
                  .append(LINE_END);
    }
        _fileInput.append("END")
                  .append(LINE_END)
                  .append(LINE_END);
        Set<ModuleDBO> modules = slave.getModules();
        for (ModuleDBO module : modules) {
            createModule(module, fdlAddress);
        }
    }

    /**
     * @param module
     */
    private void createModule(final ModuleDBO module, final int fdlAddress) {
        _fileInput.append("DPSUBSYSTEM 1, ")
                  .append("DPADDRESS ")
                  .append(fdlAddress + ", ")
                  .append("SLOT ")
                  .append(_slot++)
                  .append(", ")
                  .append("\""+module.getGSDModule().getName()+" ")
                  .append("("+module.getConfigurationData()+")\"")
                  .append(LINE_END)
                  .append("BEGIN")
                  .append(LINE_END)
                  .append("  SLAVE_CFG_DATA ")
                  .append(module.getConfigurationData())
                  .append(LINE_END)
                  .append("  OBJECT_REMOVEABLE ")
                  .append("\"TODO\"")
                  .append(LINE_END)
                  .append("END")
                  .append(LINE_END)
                  .append(LINE_END);



    }

    /**
    *
    * @param path
    *            The target File Path.
    * @throws IOException
    */
    public final void getXmlFile(final File path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.append(_fileInput.toString());
        CentralLogger.getInstance().info(this, "Write File:" + path.getAbsolutePath());
        writer.close();
    }
}
