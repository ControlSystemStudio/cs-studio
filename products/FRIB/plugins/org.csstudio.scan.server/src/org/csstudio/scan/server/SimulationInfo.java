/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.server.internal.PathStreamTool;
import org.csstudio.scan.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Simulation info provider: Slew rates for devices
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulationInfo
{
	final private static String XML_BEAMLINE = "beamline";
	final private static String XML_PV = "pv";
	final private static String XML_NAME = "name";
	final private static String XML_ALIAS = "alias";
	final private static String XML_NAME_PATTERN = "name_pattern";
    final private static String XML_SLEW_RATE = "slew_rate";

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

    /** @return Default {@link SimulationInfo}, initialized from preferences */
    public static SimulationInfo getDefault() throws Exception
    {
    	final String path = ScanSystemPreferences.getSimulationConfigPath();
        final InputStream config_stream = PathStreamTool.openStream(path);
        return new SimulationInfo(config_stream);
    }

    /** Initialize
	 *  @param config_file_name Name of XML file
	 *  @throws Exception on error
	 */
	public SimulationInfo(final String config_file_name) throws Exception
	{
		this(new FileInputStream(config_file_name));
	}

	/** Initialize
	 *  @param stream Stream for XML content
	 *  @throws Exception on error
	 */
	public SimulationInfo(final InputStream stream) throws Exception
	{
        readSimulationConfig(stream);
	}

	/** Read simulation config file
	 *  @param stream Stream for XML content
	 *  @throws Exception on error
	 */
	private void readSimulationConfig(final InputStream stream) throws Exception
    {
	    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);

        // Check root element
        final Element root_node = doc.getDocumentElement();
        root_node.normalize();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals(XML_BEAMLINE))
            throw new Exception("Got " + root_name + " instead of " + XML_BEAMLINE);

        // Loop over <pv>s
        final NodeList pvs = root_node.getElementsByTagName(XML_PV);
        for (int i=0; i<pvs.getLength(); ++i)
        {
            final Element pv = (Element) pvs.item(i);

            // Any simulation info?
            final double slew_rate = XMLUtil.getSubelementDouble(pv, XML_SLEW_RATE);
            if (Double.isNaN(slew_rate))
            	continue;

            // Name or alias?
        	String name = XMLUtil.getSubelementString(pv, XML_NAME, "");
        	name = XMLUtil.getSubelementString(pv, XML_ALIAS, name);
        	if (! name.isEmpty())
        		pv_slew_rates.put(name, slew_rate);

        	// Pattern?
        	final String pattern = XMLUtil.getSubelementString(pv, XML_NAME_PATTERN, "");
        	if (! pattern.isEmpty())
        		patterned_slew_rates.add(new PatternedSlew(pattern, slew_rate));
        }
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
}
