/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ITimestamp.Format;
import org.eclipse.osgi.util.NLS;

/** Abstract base class for all items in the alarm configuration tree.
 *  <p>
 *  Basic hierarchy:
 *  <ul>
 *  <li>One AlarmTreeRoot
 *  <li>Many AlarmTreeComponent entries to build the hierarchy
 *  <li>Finally AlarmTreePV entries as leaves
 *  </ul> 
 *  @see AlarmTreeRoot
 *  @see AlarmTreeComponent
 *  @see AlarmTreePV
 *  
 *  @author Kay Kasemir, Xihui Chen
 */
public abstract class AlarmTree
{
    /** RDB ID */
    final private int id;
    
    /** Visible name of the item */
    final private String name;

    /** Parent node */
    final protected AlarmTree parent;

    // According to JProfiler, equals()/getPathName()/hashCode()
    // are called A LOT whenever the tree view is updated or
    // whenever an alarm item is added/removed from various lists,
    // so they have to be fast.
    
    /** Full path name of this item.
     *  Like parent and hash_code it's final so that it can be computed once,
     *  because it is used very often.
     */
    final private String path_name;
    
    /** Hash code of this item */
    final private int hash_code;

    /** Sub-tree elements of this item */
    final private List<AlarmTree> children = new ArrayList<AlarmTree>();
    
    /** Sub-tree elements of this item which are currently in alarm */
    final private List<AlarmTree> alarm_children = new ArrayList<AlarmTree>();
    
    /** Guidance messages */
    private List<GDCDataStructure> guidance = new ArrayList<GDCDataStructure>();

    /** Related displays */
    private List<GDCDataStructure> displays = new ArrayList<GDCDataStructure>();
    
    /** Commands */
    private List<GDCDataStructure> commands = new ArrayList<GDCDataStructure>();
    
    /** Current severity of this item/subtree */
    protected SeverityLevel current_severity = SeverityLevel.OK;
    
    /** Highest/latched alarm severity of this item/subtree */
    protected SeverityLevel severity = SeverityLevel.OK;
    
    /**  Highest/latched alarm message of this item/subtree */
    protected String message = SeverityLevel.OK.getDisplayName();

    /** Time of last configuration change */
    private ITimestamp config_time;

    /** Initialize alarm tree item
     *  @param id RDB ID
     *  @param name Name of the item
     *  @param parent Parent item or <code>null</code>
     *  @throws Error on tree structure error
     */
    public AlarmTree(final int id, final String name, final AlarmTree parent)
    {
        this.id = id;
        this.name = name;
        this.parent = parent;
        if (parent == null)
        {
            path_name = name;
        }
        else
        {
            parent.addChild(this);
            path_name = AlarmTreePath.makePath(parent.getPathName(), name);
        }
        hash_code = getPathName().hashCode();
    }

    /** @return RDB ID */
    public int getID()
    {
        return id;
    }
    
    /** @return Name */
    public String getName()
    {
        return name;
    }

    /** @return Full path name to this item, including the item name itself */
    final public String getPathName()
    {
        return path_name;
    }
    
    /** Compare by path name
     *  @param obj Other object
     *  @return <code>true</code> if path names match
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
            return true;
        if (! (obj instanceof AlarmTree))
            return false;
        final AlarmTree other = (AlarmTree) obj;
        return getPathName().equals(other.getPathName());
    }

    /** @return Hash code of path name */
    @Override
    public int hashCode()
    {
        return hash_code;
    }

    /** @return Description of this item's position in the alarm tree */
    abstract public AlarmTreePosition getPosition();
    
    /** @return Text (multi-line) that can be used as a tool-tip to
     *          describe this item and its current state
     */
    public String getToolTipText()
    {
        return NLS.bind(Messages.Alarm_TT,
            new Object[]
            {
                getPathName(),
                getCurrentSeverity().getDisplayName(),
                getSeverity().getDisplayName(),
                getMessage()
            });
    }

    /** @return Guidance messages */
    public List<GDCDataStructure> getGuidance()
    {
        return guidance;
    }

    /** Add guidance message.
     *  @param title
     *  @param details
     */
    public void addGuidance(final String title, final String details)
    {
        guidance.add(new GDCDataStructure(title, details));
    }

    /** @param guidance Guidance messages */
    void setGuidance(final List<GDCDataStructure> guidance)
    {
        this.guidance = guidance == null ? new ArrayList<GDCDataStructure>() : guidance;
    }

    /** @return Related displays */
    public List<GDCDataStructure> getDisplays()
    {
        return displays;
    }

    /** Add related display
     *  @param title
     *  @param display
     */
    public void addDisplay(final String title, final String display)
    {
        displays.add(new GDCDataStructure(title, display));
    }

    /** @param displays Related displays */
    void setDisplays(final List<GDCDataStructure> displays)
    {
        this.displays = displays == null ? new ArrayList<GDCDataStructure>() : displays;
    }

    /** @return Commands */
    public List<GDCDataStructure> getCommands()
    {
        return commands;
    }
    
    /** @param commands Commands */
    void setCommands(final List<GDCDataStructure> commands)
    {
        this.commands = commands == null ? new ArrayList<GDCDataStructure>() : commands;
    }

    /** @return Time of last configuration change */
    public String getConfigTime()
    {
        if (config_time == null)
            return Messages.Unknown;
        return config_time.format(Format.DateTimeSeconds);
    }
    
    /** @param config_time Time of last configuration change */
    void setConfigTime(final ITimestamp config_time)
    {
        this.config_time = config_time;
    }
    
    /** Return the parent item or <code>null</code> for the root element */
    public AlarmTree getParent()
    {
        return parent;
    }

    /** @return Alarm tree root element */
    protected AlarmTreeRoot getRoot()
    {
        AlarmTree root = this;
        while (root != null)
        {
            if (root instanceof AlarmTreeRoot)
                return (AlarmTreeRoot) root;
            root = root.getParent();
        }
        throw new Error("Alarm tree has no root"); //$NON-NLS-1$
    }
    
    /** @param child Child element to add 
     *  @throws Error on tree structure error
     */
    protected void addChild(final AlarmTree child)
    {
        children.add(child);
    }

    /** @param child Child element to remove 
     *  @throws Error on tree structure error
     */
    protected void removeChild(final AlarmTree child)
    {
        children.remove(child);
    }

    /** @return Number of sub-elements in configuration hierarchy */
    public int getChildCount()
    {
        return children.size();
    }
    
    /** Get one child element.
     *  @param index Child element index 0 .. (getChildCount()-1)
     *  @return Sub-item in configuration hierarchy
     */
    public AlarmTree getChild(final int index)
    {
        return children.get(index);
    }

    /** @return Number of sub-elements in configuration hierarchy
     *          which are currently in alarm
     */
    public int getAlarmChildCount()
    {
        return alarm_children.size();
    }
    
    /** Get one of the child elements which are currently in alarm.
     *  @param index Child element index 0 .. (getAlarmChildCount()-1)
     *  @return Sub-item in alarm hierarchy
     */
    public AlarmTree getAlarmChild(final int index)
    {
        return alarm_children.get(index);
    }

    
    /** Locate child element by name.
     *  @param child_name Name of child to locate.
     *  @return Child with given name or <code>null</code> if not found.
     */
    public AlarmTree getChild(final String child_name)
    {
        for (AlarmTree child : children)
            if (child.getName().equals(child_name))
                return child;
        return null;
    }

    /** @return Current severity */
    public SeverityLevel getCurrentSeverity()
    {
        return current_severity;
    }

    /** @return Highest or latched severity */
    public SeverityLevel getSeverity()
    {
        return severity;
    }
    
    /** @return Highest or latched alarm message */
    public String getMessage()
    {
        return message;
    }
    
    /** Acknowledge or un-acknowledge current alarms.
     *  <p>
     *  For PV entries, it actually acknowledges the alarm.
     *  For other hierarchy entries, it acknowledges all child alarms.
     *  
     *  @param acknowledge Acknowledge, or un-acknowledge?
     */
    public void acknowledge(final boolean acknowledge)
    {
        for (AlarmTree child : children)
            child.acknowledge(acknowledge);
    }

    /** Set severity/status of this item by maximizing over its child
     *  severities.
     *  Recursively updates its parent items,
     *  the root item will then notify the model,
     *  which will notify listeners.
     *  @param pv PV that triggered this update
     */
    public void maximizeSeverity(final AlarmTreePV pv)
    {
        // Get maximum child severity and its status
        current_severity = SeverityLevel.OK;
        severity = SeverityLevel.OK;
        message = severity.getDisplayName();
        alarm_children.clear();
        for (AlarmTree child : children)
        {
            // Maximize 'current' severity
            if (child.getCurrentSeverity().ordinal() > current_severity.ordinal())
                current_severity = child.getCurrentSeverity();
            // Maximize latched severity/status
            final SeverityLevel child_sevr = child.getSeverity();
            final int level = child_sevr.ordinal();
            if (level > 0)
                alarm_children.add(child);
            if (level > severity.ordinal())
            {
                severity = child_sevr;
                message = child.getMessage();
            }
        }
        // Percolate changes towards root
        if (parent != null)
            parent.maximizeSeverity(pv);
    }

    /** Dump this item and sub-items */
    public void dump()
    {
        dump(0);
    }

    /** Dump this item and sub-items
     *  @param level Indentation level
     */
    @SuppressWarnings("nls")
    private void dump(int level)
    {
        String indent = "";
        for (int i=0; i<level; ++i)
            indent = "   " + indent;
        final String indent1 = indent + "   ";
        System.out.println(indent + "* " + toString());
        if(guidance != null && !guidance.isEmpty()){
	        for (GDCDataStructure guide : guidance) {
	        	System.out.println(indent +
	                    "  - Guidance: ");
	        	System.out.println(indent1 +
	                    "  - Title: " + guide.getTitle());
	        	System.out.println(indent1 +
	                    "  - Details: " + guide.getDetails());        	
	        }
        }
        if(displays != null && !displays.isEmpty()) {    
	        for (GDCDataStructure display : displays) {
	        	System.out.println(indent +
	                    "  - Displays: ");
	        	System.out.println(indent1 +
	                    "  - Title: " + display.getTitle());
	        	System.out.println(indent1 +
	                    "  - Details: " + display.getDetails());        	
	        }
        }
        if(commands != null && !commands.isEmpty()) {
	        for (GDCDataStructure command : commands) {
	        	System.out.println(indent +
	                    "  - Command: ");
	        	System.out.println(indent1 +
	                    "  - Title: " + command.getTitle());
	        	System.out.println(indent1 +
	                    "  - Details: " + command.getDetails());        	
	        }
        }
        for (AlarmTree child : children)
            child.dump(level + 1);
    }

    /** @return XML tag for this tree item */
    protected String getXMLTag()
    {
        return XMLTags.COMPONENT;
    }
    
    /** Write XML representation of this element, including children
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    final protected void writeItemXML(final PrintWriter out, final int level) throws Exception
    {
        final String tag = getXMLTag();
        XMLWriter.start(out, level, tag + " " + XMLTags.NAME + "=\"" + name + "\"");
        out.println();
        writeConfigXML(out, level+1);
        for (AlarmTree child : children)
            child.writeItemXML(out, level+1);
        XMLWriter.end(out, level, tag);
        out.println();
    }
    
    /** Write configuration detail in XML format to output.
     *  Default writes guidance, displays, commands.
     *  Derived classes can add more detail
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     */
    protected void writeConfigXML(final PrintWriter out, final int level)
    {
        writeGCD_XML(out, level, XMLTags.GUIDANCE, guidance);
        writeGCD_XML(out, level, XMLTags.DISPLAY, displays);
        writeGCD_XML(out, level, XMLTags.COMMAND, commands);
    }

    /** Write GDCDataStructure as XML
     *  @param out PrintWriter to which to send XML output
     *  @param level Indentation level
     *  @param tag XML Tag
     *  @param gcd The data
     */
    private void writeGCD_XML(final PrintWriter out, final int level,
            final String tag, final List<GDCDataStructure> gcd)
    {
        if (gcd == null  ||  gcd.isEmpty())
            return;
        for (GDCDataStructure guid : gcd)
        {
            XMLWriter.start(out, level, tag);
            out.println();
            XMLWriter.XML(out, level+1, XMLTags.TITLE, guid.getTitle());
            XMLWriter.XML(out, level+1, XMLTags.DETAILS, guid.getDetails());
            XMLWriter.end(out, level, tag);
            out.println();
        }
    }

    /** @return Short string representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(getPathName());
        buf.append(" (").append(current_severity.getDisplayName()).append("/");
        buf.append(severity.getDisplayName()).append("/");
        buf.append(message).append(")");
        if (children.size() > 0)
        {
            buf.append(" - ");
            for (int i=0; i<children.size(); ++i)
            {
                if (i > 0)
                    buf.append(", ");
                buf.append(children.get(i).getName());
            }
        }
        return buf.toString();
    }
}
