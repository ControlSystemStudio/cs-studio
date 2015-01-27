/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.alarm.beast.XMLTags;
import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Load Alarm Configuration from XML and then insert them to RDB.
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class AlarmConfigurationLoader
{
    /** Maximum title length when loading older config files */
    private static final int MAX_TITLE = 100;

    final private AlarmConfiguration config;

    /** Initialize
     *  @param config AlarmConfiguration to update with loaded information
     */
    public AlarmConfigurationLoader(final AlarmConfiguration config)
    {
        this.config = config;
    }

    /** Load information from XML document
     *  @param filename XML config file name
     *  @throws Exception on error
     */
    public void load(final String filename) throws Exception
    {
        final InputStream stream = new FileInputStream(filename);
        final DocumentBuilder builder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        load(builder.parse(stream));
    }

    /** Load information from XML document
     *  @param doc XML DOM
     *  @throws Exception on error
     */
    private void load(final Document doc) throws Exception
    {
        // Check root element type
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals("config"))
            throw new Exception("Expected <config>, found <" + root_name
                    + ">");

        // Simple configuration name consistency check
        final String name = loadName(root_node);
        final String config_name = config.getAlarmTree().getName();
        if (!name.equals(config_name))
            throw new Exception("Cannot apply configuration file for '" + name +
                    "' to existing configuration named '" + config_name + "'");

        // Load components under root
        loadChildComponents(root_node, config.getAlarmTree());
    }

    /** Recursively load all COMPONENT nodes under a start node
     *  @param node DOM Node where to start
     *  @param parent AlarmTree parent to which to add loaded tree elements
     *  @throws Exception on error
     */
    private void loadChildComponents(final Element node,
                                     final AlarmTreeItem parent) throws Exception
    {
        Element component =
            DOMHelper.findFirstElementNode(node.getFirstChild(), XMLTags.COMPONENT);
        while (component != null)
        {
            loadComponent(component, parent);
            component = DOMHelper.findNextElementNode(component, XMLTags.COMPONENT);
        }
    }

    /** Load AlarmTreeComponent and sub-elements
     *  @param component Component DOM
     *  @param parent Parent element in alarm tree
     *  @throws Exception
     */
    private void loadComponent(final Element component, final AlarmTreeItem parent) throws Exception
    {
        // New component, or modify existing component?
        final String name = loadName(component);
        AlarmTreeItem tree_component = parent.getChild(name);
        if (tree_component == null)
            tree_component = config.addComponent(parent, name);
        else if (tree_component instanceof AlarmTreePV)
            throw new Exception("Cannot turn PV " + tree_component.getPathName() + " into component");

        // Read component config elements
        loadCommonConfig(component, tree_component);
		config.configureItem(tree_component, tree_component.getGuidance(),
				tree_component.getDisplays(), tree_component.getCommands(),
				tree_component.getAutomatedActions());

        System.out.println("Loading " + tree_component.getPathName());

        // Get sub-components
        loadChildComponents(component, tree_component);

        // Get PVs
        Element pv =
            DOMHelper.findFirstElementNode(component.getFirstChild(), XMLTags.PV);
        while (pv != null)
        {
            loadPV(pv, tree_component);
            pv = DOMHelper.findNextElementNode(pv, XMLTags.PV);
        }
    }

    /** Load 'name' from attribute
     *  @param component DOM node
     *  @return Name
     *  @throws Exception on error
     */
    private String loadName(final Element component) throws Exception
    {
        final String name = component.getAttribute(XMLTags.NAME);
        if (name == null)
            throw new Exception ("Missing 'name'");
        return name;
    }

    /** Load config items common to all alarm tree elements (guidance, ...)
     *  @param node DOM node
     *  @param item Alarm tree item to configure
     *  @throws Exception on error
     */
    private void loadCommonConfig(final Element node,
            final AlarmTreeItem item) throws Exception
    {
    	item.setGuidance(loadGDC(node, XMLTags.GUIDANCE));
    	item.setDisplays(loadGDC(node, XMLTags.DISPLAY));
    	item.setCommands(loadGDC(node, XMLTags.COMMAND));
    	item.setAutomatedActions(loadAA(node, XMLTags.AUTOMATED_ACTION));
    }

    /** Load AlarmTreePV
     *  @param node Alarm tree PV DOM
     *  @param parent Alarm tree parent
     *  @throws Exception on error
     */
    private void loadPV(final Element node, final AlarmTreeItem parent) throws Exception
    {
        final String name = loadName(node);
        // Existing or new PV?
        final AlarmTreePV pv;
        final AlarmTreeItem existing = parent.getChild(name);
        if (existing == null)
            pv = config.addPV(parent, name);
        else if (existing instanceof AlarmTreePV)
            pv = (AlarmTreePV) existing;
        else
            throw new Exception("Cannot turn existing " + existing.getPathName()
                                + " into PV");

        System.out.println("Loading " + pv.getPathName());

        loadCommonConfig(node, pv);

        pv.setEnabled(DOMHelper.getSubelementBoolean(node, XMLTags.ENABLED, true));
        pv.setDescription(DOMHelper.getSubelementString(node, XMLTags.DESCRIPTION));
        pv.setLatching(DOMHelper.getSubelementBoolean(node, XMLTags.LATCHING));
        pv.setAnnunciating(DOMHelper.getSubelementBoolean(node, XMLTags.ANNUNCIATING));
        pv.setFilter(DOMHelper.getSubelementString(node, XMLTags.FILTER));
        pv.setDelay(DOMHelper.getSubelementDouble(node, XMLTags.DELAY, 0.0));
        pv.setCount(DOMHelper.getSubelementInt(node, XMLTags.COUNT, 0));
        config.configurePV(pv, pv.getDescription(), pv.isEnabled(),
                pv.isAnnunciating(), pv.isLatching(),
                pv.getDelay(), pv.getCount(), pv.getFilter(),
                pv.getGuidance(), pv.getDisplays(), pv.getCommands(), pv.getAutomatedActions());
    }

    /** Load Guidance/Displays/Commands/Automated Actions
     *  @param node DOM node, could be a component or a PV
     *  @param name name of the item to load, it must be one of XMLTags.GUIDANCE/DISPLAY/COMMAND/AUTOMATED_ACTION
     *  @return Guidance/Displays/Commands/Automated Actions array, never null.
     */
    private GDCDataStructure[] loadGDC(final Element node,
                                       final String name) throws Exception
    {
        final List<GDCDataStructure> gdcList = new ArrayList<GDCDataStructure>();

        Element gdcNode = DOMHelper.findFirstElementNode(node.getFirstChild(), name);
        while (gdcNode != null)
        {
            String title = DOMHelper.getSubelementString(gdcNode, XMLTags.TITLE);
            String details = DOMHelper.getSubelementString(gdcNode, XMLTags.DETAILS);
            // New files use
            //    <name><title>....</title><details>...</details></name>
            // If we find an old file with simply
            //    <name>...</name>
            // we will use its node value value as title and details
            if (title.length() <= 0)
            {
                details = gdcNode.getFirstChild().getNodeValue();
                if (details.length() < MAX_TITLE)
                    title = details;
                else
                    title = details.substring(0, MAX_TITLE);
            }
            if (title.length() > 0)
                gdcList.add(new GDCDataStructure(title, details));
            gdcNode = DOMHelper.findNextElementNode(gdcNode, name);
        }
        return gdcList.toArray(new GDCDataStructure[gdcList.size()]);
    }
    
    /** Load Automated Actions
     *  @param node DOM node, could be a component or a PV
     *  @param name name of the item to load, it must be XMLTags.AUTOMATED_ACTION
     *  @return Automated Actions array, never null.
     */
	private AADataStructure[] loadAA(final Element node, final String name)
			throws Exception {
		final List<AADataStructure> aaList = new ArrayList<AADataStructure>();

		Element aaNode = DOMHelper.findFirstElementNode(node.getFirstChild(), name);
		while (aaNode != null) {
			String title = DOMHelper.getSubelementString(aaNode, XMLTags.TITLE);
			String details = DOMHelper.getSubelementString(aaNode, XMLTags.DETAILS);
			Integer delay = DOMHelper.getSubelementInt(aaNode, XMLTags.DELAY);
			aaList.add(new AADataStructure(title, details, delay));
			aaNode = DOMHelper.findNextElementNode(aaNode, name);
		}
		return aaList.toArray(new AADataStructure[aaList.size()]);
	}
}
