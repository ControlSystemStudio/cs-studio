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
 * $Id: ProfibusConfigXMLGenerator.java,v 1.4 2010/08/20 13:33:10 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.platform.logging.CentralLogger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 14.05.2008
 */
public class ProfibusConfigXMLGenerator {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(ProfibusConfigXMLGenerator.class);
    
    /**
     * The Profibus Config XML {@link Document}.
     */
    private final Document _document;

    /**
     * The Element for the BUSPARAMETER.
     */
    private final Element _busparameter;

    /**
     * The Element for the SLAVE_CONFIGURATION.
     */
    private final Element _slaveConfig;

    /**
     * The XML Root Element (PROFIBUS-DP_PARAMETERSET).
     */
    private final Element _root;

    /**
     * The XML Master Element.
     */
    private final Element _master;

    /**
     * List of all Slave Field Address.
     */
    private final ArrayList<Integer> _slaveFldAdrs;

    private final ArrayList<XmlSlave> _slaveList;

    private final Element _fmbSet;

    public ProfibusConfigXMLGenerator() {
        _slaveFldAdrs = new ArrayList<Integer>();
        _slaveList = new ArrayList<XmlSlave>();
        _root = new Element("PROFIBUS-DP_PARAMETERSET");
        _document = new Document(_root);
        _busparameter = new Element("BUSPARAMETER");
        _master = new Element("MASTER");
        _fmbSet = new Element("FMB_SET");
        _slaveConfig = new Element("SLAVE_CONFIGURATION");

    }

    /**
     *
     * @param subnet
     *            The Profibus Subnet.
     * @throws PersistenceException 
     */
    public final void setSubnet(final ProfibusSubnetDBO subnet) throws PersistenceException {
        Set<MasterDBO> masterTree = subnet.getProfibusDPMaster();
        if ((masterTree == null) || (masterTree.size() < 1)) {
            return;
        }

        MasterDBO master = masterTree.iterator().next();

        makeMaster(subnet, master);
        makeFMB(master);

        _busparameter.addContent(_master);
        _busparameter.addContent(_fmbSet);

        Map<Short, ? extends AbstractNodeDBO> childrenAsMap = master.getChildrenAsMap();
        Iterator<Short> iterator = childrenAsMap.keySet().iterator();
        while (iterator.hasNext()) {
            Short key = iterator.next();
            addSlave((SlaveDBO) childrenAsMap.get(key));
        }
    }

    private void makeFMB(final MasterDBO master) {
        String[] fmbKeys = new String[] {
                "max_number_slaves",
                "max_slave_output_len",
                "max_slave_input_len",
                "max_slave_diag_entries",
                "max_slave_diag_len",
                "max_bus_para_len",
                "max_slave_para_len"
        };
        String[] fmbValues = new String[] {
                Integer.toString(master.getMaxNrSlave()),
                Integer.toString(master.getMaxSlaveOutputLen()),
                Integer.toString(master.getMaxSlaveInputLen()),
                Integer.toString(master.getMaxSlaveDiagEntries()),
                Integer.toString(master.getMaxSlaveDiagLen()),
                Integer.toString(master.getMaxBusParaLen()),
                Integer.toString(master.getMaxSlaveParaLen()),
        };
        assert fmbKeys.length == fmbValues.length;
        for (int i = 0; i < fmbKeys.length; i++) {
            _fmbSet.setAttribute(fmbKeys[i], fmbValues[i]);
        }
    }

    private void makeMaster(final ProfibusSubnetDBO subnet, final MasterDBO master) {
        String[] masterKeys = new String[] { "bus_para_len", "fdl_add", "baud_rate", "tslot",
                "min_tsdr", "max_tsdr", "tqui", "tset", "ttr", "gap", "hsa", "max_retry_limit",
                "bp_flag", "min_slave_interval", "poll_timeout", "data_control_time", "reserved",
                "master_user_data_length", "master_user_data" };
        String autoClear = "0";
        if (master.isAutoclear()) {
            autoClear = "1";
        }
        short fdl;
		// XXX: Es wird immer der Index des Masters benötigt.
            fdl = master.getSortIndex();
		// if(master.getRedundant()<0) {
		// fdl = master.getSortIndex();
		// }else {
		// fdl = master.getRedundant();
		// }
        String[] masterValues = new String[] {
                /* bus_para_len */"66"/*
                                       * TODO:busParaLen is Default=66? und muss das geändert werden
                                       * können?
                                       */,
                /* fdl_add */Short.toString(fdl),
                /*
                 * We have some problems with work of the XML configuration.
                 * 1st. The parameter baud_rate in the <MASTER> section must be write in decimal notation.
                 * Otherwise the bus will start with a baud_rate = 9600 kbit/s.
                 */
                /* baud_rate */subnet.getBaudRate(),
                /* tslot */Integer.toString(subnet.getSlotTime()),
                /* min_tsdr */Integer.toString(subnet.getMinTsdr()),
                /* max_tsdr */Integer.toString(subnet.getMaxTsdr()),
                /* tqui */Short.toString(subnet.getTqui()),
                /* tset */Short.toString(subnet.getTset()),
                /* ttr */Long.toString(subnet.getTtr()),
                /* gap */Short.toString(subnet.getGap()),
                /* hsa */Short.toString(subnet.getHsa()),
                /* max_retry_limit */Short.toString(subnet.getRepeaterNumber()),
                /* bp_flag */autoClear,/* TODO:is bpFlag Autoclear? */
                /* min_slave_interval */Integer.toString(master.getMinSlaveInt()),
                /* poll_timeout */Integer.toString(master.getPollTime()),
                /* data_control_time */Integer.toString(master.getDataControlTime()),
                /* reserved */"0,0,0,0,0,0"/*
                                            * TODO:reserved is Default=0? und muss das geändert
                                            * werden können?
                                            */,
                /* master_user_data_length */Integer
                        .toString(master.getMasterUserData().split(",").length),
                /* master_user_data */master.getMasterUserData() };
        assert masterKeys.length == masterValues.length;
        for (int i = 0; i < masterKeys.length; i++) {
            _master.setAttribute(masterKeys[i], masterValues[i]);
        }
    }

    /**
     *
     * @param slave
     *            The Profibus Slave.
     * @throws PersistenceException 
     */
	private void addSlave(final SlaveDBO slave) throws PersistenceException {
		/*
		 * Has the Slave no GSD File is the Slave a bus Passive node. Don't need
		 * a configuration on the IOC.
		 */
		if (slave.getGSDFile() != null) {
			XmlSlave xmlSlave = new XmlSlave(slave);
			_slaveList.add(xmlSlave);
			_slaveFldAdrs.add(slave.getFdlAddress());
		}
    }

    /**
     *
     * @param path
     *            The target File Path.
     * @throws IOException
     */
    public final void getXmlFile(@Nonnull final File path) throws IOException {
        Writer writer = new FileWriter(path);
        LOG.info("Write File:" + path.getAbsolutePath());
        getXmlFile(writer);
    }
    
    public final void getXmlFile(@Nonnull final Writer writer) throws IOException {
        _root.addContent(_busparameter);
        _slaveConfig.addContent(slaveTable());
        for (XmlSlave xmlSlave : _slaveList) {
            _slaveConfig.addContent(xmlSlave.getSlave());
        }
        _root.addContent(_slaveConfig);
        Format format = Format.getPrettyFormat();
        format.setEncoding("ISO-8859-1");
        XMLOutputter out = new XMLOutputter(format);
        out.output(_document, writer);
        writer.close();
    }

    /**
     *
     * @return The Slave Table XML element.
     */
    private Element slaveTable() {
        Element slaveTable = new Element("SLAVE_TABLE");
        for (int i = 0; i < _slaveFldAdrs.size(); i++) {
            slaveTable.setAttribute("Station_" + (i + 1), Integer.toString(_slaveFldAdrs.get(i)));
        }
        return slaveTable;
    }

    /**
     * Return the integer value of a String.
     * The String can dec or hex.
     * @param value The integer value as Sting.
     * @return The value as int.
     */
    public static int getInt(final String value) {
        String tmp = value.toUpperCase().trim();
        int radix = 10;
        try {
            if(tmp.startsWith("0X")) {
                tmp = tmp.substring(2);
                radix=16;
            }
            return Integer.parseInt(tmp,radix);
        }catch (Exception e) {
            return -1;
        }
    }
}
