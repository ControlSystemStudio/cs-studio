/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.device;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Helper for handling scan_config.xml files
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanConfig
{
    final private static String XML_SCAN_CONFIG = "scan_config";
    final private static String XML_BEAMLINE = "beamline";
    final private static String XML_PV = "pv";
    final private static String XML_NAME = "name";
    final private static String XML_NAME_PATTERN = "name_pattern";
    final private static String XML_ALIAS = "alias";
    final private static String XML_SLEW_RATE = "slew_rate";

    /** Predefined devices, maybe with alias */
    final private DeviceInfo[] devices;
    
    /** Map of PV names to slew rate */
    final private Map<String, Double> pv_slew_rates = new HashMap<String, Double>();

    /** Pattern for PV name and associated slew rate */
    static private class PatternedSlew
    {
        final Pattern pattern;
        final double slew_rate;

        public PatternedSlew(final String pattern, final double slew_rate)
        {
            this.pattern = Pattern.compile(pattern);
            this.slew_rate = slew_rate;
        }

        public boolean matches(final String device_name)
        {
            return pattern.matcher(device_name).matches();
        }
    };

    /** Slew rates for PV name patterns */
    final private List<PatternedSlew> patterned_slew_rates = new ArrayList<PatternedSlew>();

    /** Default slew rate for PVs that were not specified */
    final public static double DEFAULT_SLEW_RATE = 0.05;
    
    /** Read scan configuration from XML file
     *  @param config_file_name Name of XML file
     *  @throws Exception on error
     */
    public ScanConfig(final String config_file_name) throws Exception
    {
        this(new FileInputStream(config_file_name));
    }

    /** Read scan configuration from XML stream
     *  @param stream Stream for XML content
     *  @throws Exception on error
     */
    public ScanConfig(final InputStream stream) throws Exception
    {
        devices = read(stream);
    }

    /** @return {@link DeviceInfo}s read from file */
    public DeviceInfo[] getDevices()
    {
        return devices;
    }

    /** Get slew rate for device, otherwise returning default */
    public double getSlewRate(final String device_name)
    {
        Double slew = pv_slew_rates.get(device_name);
        if (slew != null)
            return slew;

        // Check pattern
        for (PatternedSlew ps : patterned_slew_rates)
            if (ps.matches(device_name))
                return ps.slew_rate;
        return DEFAULT_SLEW_RATE;
    }
    
    /** Read device configuration from XML stream
     *  @param stream Stream for XML content
     *  @return {@link DeviceInfo}s read from stream
     *  @throws Exception on error
     */
    private DeviceInfo[] read(final InputStream stream) throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);

        // Check root element for <scan_config>, but still allow <beamline>
        final Element root_node = doc.getDocumentElement();
        root_node.normalize();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals(XML_SCAN_CONFIG))
        {
            if (root_name.equals(XML_BEAMLINE))
                Logger.getLogger(ScanConfig.class.getName())
                    .log(Level.WARNING, "<" + XML_BEAMLINE + "> is deprecated, use <" + XML_SCAN_CONFIG + ">");
            else
                throw new Exception("Got " + root_name + " instead of " + XML_SCAN_CONFIG);
        }
        
        // Loop over <pv>s, being very lenient where they are in the document.
        // This allows both <scan_config> and legacy <beamline>.
        // Users who want strict checking can refer to the schema in their XML files,
        // use xmllint etc.
        final List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
        final NodeList pvs = root_node.getElementsByTagName(XML_PV);
        for (int i=0; i<pvs.getLength(); ++i)
        {
            final Element pv = (Element) pvs.item(i);
            final double slew_rate = XMLUtil.getSubelementDouble(pv, XML_SLEW_RATE, Double.NaN);
            final String name = XMLUtil.getSubelementString(pv, XML_NAME, "");
            if (name.isEmpty())
            {   // Check if it's a pattern, which then requires a slew rate
                final String pattern = XMLUtil.getSubelementString(pv, XML_NAME_PATTERN, "");
                if (pattern.isEmpty())
                    throw new Exception("Missing <pv> <name> or <name_pattern>");
                if (Double.isNaN(slew_rate))
                    throw new Exception("Missing <slew_rate> for <pv> <name_pattern>");
                patterned_slew_rates.add(new PatternedSlew(pattern, slew_rate));
            }
            else
            {   // Got name, maybe with alias and slew rate
                final String alias = XMLUtil.getSubelementString(pv, XML_ALIAS, name);
                devices.add(new DeviceInfo(name, alias));
                if (! Double.isNaN(slew_rate))
                    pv_slew_rates.put(name, slew_rate);
            }
        }
        return devices.toArray(new DeviceInfo[devices.size()]);
    }
}
