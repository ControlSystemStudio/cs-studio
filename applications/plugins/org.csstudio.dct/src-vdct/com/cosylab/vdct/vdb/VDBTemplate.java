package com.cosylab.vdct.vdb;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Component;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.GetGUIInterface;
import com.cosylab.vdct.graphics.objects.Descriptable;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.VisibleObject;
import com.cosylab.vdct.inspector.Inspectable;
import com.cosylab.vdct.inspector.InspectableProperty;
import com.cosylab.vdct.inspector.InspectorManager;
import com.cosylab.vdct.undo.ComposedAction;
import com.cosylab.vdct.undo.CreateTemplateMacroAction;
import com.cosylab.vdct.undo.CreateTemplatePortAction;
import com.cosylab.vdct.undo.DeleteTemplateMacroAction;
import com.cosylab.vdct.undo.DeleteTemplatePortAction;
import com.cosylab.vdct.undo.DescriptionChangeAction;
import com.cosylab.vdct.undo.RenameTemplateMacroAction;
import com.cosylab.vdct.undo.RenameTemplatePortAction;
import com.cosylab.vdct.undo.UndoManager;

/**
 * Data object representing EPICS DB template.
 * All data is obtained from DB file.
 * <code>Group</code> contains template structure.
 * @author Matej
 */

public class VDBTemplate implements Inspectable, Commentable, Descriptable, MonitoredPropertyListener
{
    protected String id = null;
    protected String fileName = null;
    protected String description = null;

    protected Hashtable ports = null;
    protected Vector portsV = null;

    protected Hashtable macros = null;
    protected Vector macrosV = null;

    protected Group group = null;

    private String comment = null;
    private CommentProperty commentProperty = null;

    private static ImageIcon icon = null;

    private static GUISeparator templateSeparator = null;
    private static GUISeparator portsSeparator = null;
    private static GUISeparator macrosSeparator = null;

    private static final String nullString = "";

    private String tempDescription = null;

    private long portsGeneratedID = 0;
    private long macrosGeneratedID = 0;
    protected static Random random = null;

    static
    {
        if (random==null)
            random = new Random();
    }

    class DescriptionProperty implements InspectableProperty
    {
        private static final String defaultDescription = "";
        private static final String name = "Description";
        private static final String helpString = "Template description";

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#allowsOtherValues()
         */
        public boolean allowsOtherValues()
        {
            return false;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getEditPattern()
         */
        public Pattern getEditPattern()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getHelp()
         */
        public String getHelp()
        {
            return helpString;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getInitValue()
         */
        public String getInitValue()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getName()
         */
        public String getName()
        {
            return name;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getSelectableValues()
         */
        public String[] getSelectableValues()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getToolTipText()
         */
        public String getToolTipText()
        {
            return null;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getValue()
         */
        public String getValue()
        {
            String val = getRealDescription();
            if (val==null)
                return defaultDescription;
            else
                return val;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#getVisibility()
         */
        public int getVisibility()
        {
            return InspectableProperty.UNDEFINED_VISIBILITY;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isEditable()
         */
        public boolean isEditable()
        {
            return true;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isSepatator()
         */
        public boolean isSepatator()
        {
            return false;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#isValid()
         */
        public boolean isValid()
        {
            return true;
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#popupEvent(Component, int, int)
         */
        public void popupEvent(Component component, int x, int y)
        {
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#setValue(String)
         */
        public void setValue(String value)
        {
            setDescription(value);
        }

        /**
         * @see com.cosylab.vdct.inspector.InspectableProperty#toString(String)
         */
        public String toString()
        {
            return name;
        }
    }

    /**
     * Constructor for VDBTemplate.
     */
    public VDBTemplate(String id, String fileName)
    {
        this.id = id;
        this.fileName = fileName;
        regeneratePortsID();
        updateDescription();
    }

    /**
     * Returns the description.
     * @return String
     */
    public String getDescription()
    {
        if (tempDescription!=null)
        {
            return tempDescription;
        }
        else
            return description;
    }

    /**
     * Returns the description.
     * @return String
     */
    public String getRealDescription()
    {
        return description;
    }

    /**
     * Returns the group.
     * @return Group
     */
    public Group getGroup()
    {
        return group;
    }

    /**
     * Sets the description.
     * @param description The description to set
     */
    public void setDescription(String description)
    {
        boolean update = false;
        if (this.description==null || description==null ||        // one null
            !this.description.equals(description))                    // not equals equals
        {
            // both null
            if (this.description!=description)
            {
                update=true;

                if (this.description==null || !this.description.equals(description))
                    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
                            new DescriptionChangeAction(this, this.description, description));

                this.description = description;
            }
        }

        updateDescription();

        if (update)
        {
            InspectorManager.getInstance().updateObject(this);
            /// !!!
            if (com.cosylab.vdct.VisualDCT.getInstance() != null)
                com.cosylab.vdct.VisualDCT.getInstance().updateLoadLabel();
            GetGUIInterface cmd = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
            if (cmd!=null)
                cmd.getGUIMenuInterface().updateGroupLabel();
        }
    }


    /**
     * Sets the description.
     * @param description The description to set
     */
    private void updateDescription()
    {
        if (this.description==null || this.description.length()==0)
        {
            // remove extension
            int pos = id.lastIndexOf('.');
            if (pos>0)
                tempDescription = id.substring(0, pos);
            else
                tempDescription = id;
        }
        else
            tempDescription = null;
    }

    /**
     * Sets the group.
     * @param group The group to set
     */
    public void setGroup(Group group)
    {
        this.group = group;
    }

    /**
     * Returns the fileName.
     * @return String
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Returns the id.
     * @return String
     */
    public String getId()
    {
        return id;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getCommentProperty()
     */
    public InspectableProperty getCommentProperty()
    {
        if (commentProperty==null)
            commentProperty = new CommentProperty(this);
        return commentProperty;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getIcon()
     */
    public Icon getIcon()
    {
        if (icon==null)
            icon = new javax.swing.ImageIcon(getClass().getResource("/images/template.gif"));
        return icon;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getModeNames()
     */
    public ArrayList getModeNames()
    {
        return null;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getName()
     */
    public String getName()
    {
        return id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getTemplateSeparator() {
        if (templateSeparator==null) templateSeparator = new GUISeparator("Template");
        return templateSeparator;
    }

    /**
     * @see com.cosylab.vdct.inspector.Inspectable#getProperties(int)
     */
    public InspectableProperty[] getProperties(int mode)
    {
        Vector items = new Vector();
        items.addElement(GUIHeader.getDefaultHeader());

        items.addElement(getTemplateSeparator());
        items.addElement(new NameValueInfoProperty("Class", id));
        items.addElement(new NameValueInfoProperty("FileName", fileName));
        items.addElement(new DescriptionProperty());

        items.addElement(VDBTemplate.getPortsSeparator());

        Iterator i = getPortsV().iterator();
        while (i.hasNext())
        {
            VDBPort port = (VDBPort)i.next();
            items.addElement(new GUISeparator(port.getName()));
            items.addElement(port);
            items.addElement(new PortDescriptionProperty(port));
        }

        final String addString = "Add port...";
        items.addElement(new MonitoredActionProperty(addString, this));

        items.addElement(VDBTemplate.getMacrosSeparator());

        i = getMacrosV().iterator();
        while (i.hasNext())
        {
            VDBMacro macro = (VDBMacro)i.next();
            items.addElement(new GUISeparator(macro.getName()));
            items.addElement(macro);
            items.addElement(new MacroDescriptionProperty(macro));
        }

        InspectableProperty[] properties = new InspectableProperty[items.size()];
        items.copyInto(properties);
        return properties;
    }

    /**
     * Insert the method's description here.
     * Creation date: (10.1.2001 14:49:50)
     */
    public String toString() {
        return "Template: " + getDescription() + " [" + id + "]";
    }

    /**
     * Returns the comment.
     * @return String
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets the comment.
     * @param comment The comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * Returns the ports.
     * @return Hashtable
     */
    public Hashtable getPorts()
    {
        return ports;
    }

    /**
     * Returns the portsV.
     * @return Vector
     */
    public Vector getPortsV()
    {
        return portsV;
    }

    /**
     * Returns the macros.
     * @return Hashtable
     */
    public Hashtable getMacros()
    {
        return macros;
    }

    /**
     * Returns the macrosV.
     * @return Vector
     */
    public Vector getMacrosV()
    {
        return macrosV;
    }

    /**
     * Sets the ports.
     * @param ports The ports to set
     */
    public void setPorts(Hashtable ports)
    {
        regeneratePortsID();
        this.ports = ports;
    }

    /**
     * Sets the portsV.
     * @param portsV The portsV to set
     */
    public void setPortsV(Vector portsV)
    {
        this.portsV = portsV;
    }

    /**
     * Sets the macros.
     * @param macros The macros to set
     */
    public void setMacros(Hashtable macros)
    {
        regenerateMacrosID();
        this.macros = macros;
    }

    /**
     * Sets the macrosV.
     * @param macrosV The macrosV to set
     */
    public void setMacrosV(Vector macrosV)
    {
        this.macrosV = macrosV;
    }

    /**
     */
    private void regeneratePortsID()
    {
        portsGeneratedID = random.nextLong();
    }

    /**
     */
    private void regenerateMacrosID()
    {
        macrosGeneratedID = random.nextLong();
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getPortsSeparator() {
        if (portsSeparator==null) portsSeparator = new GUISeparator("Ports");
        return portsSeparator;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3.2.2001 13:07:04)
     * @return com.cosylab.vdct.vdb.GUISeparator
     */
    public static com.cosylab.vdct.vdb.GUISeparator getMacrosSeparator() {
        if (macrosSeparator==null) macrosSeparator = new GUISeparator("Macros");
        return macrosSeparator;
    }

/**
 */
public VDBPort addPort(String name)
{
    VDBPort vdbPort = new VDBPort(this, name, nullString, null);
    ports.put(name, vdbPort);
    portsV.addElement(vdbPort);

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
            new CreateTemplatePortAction(this, vdbPort));

    regeneratePortsID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
    return vdbPort;
}

/**
 */
public VDBMacro addMacro(String name)
{
    VDBMacro vdbMacro = new VDBMacro(this, name, null);
    macros.put(name, vdbMacro);
    macrosV.addElement(vdbMacro);

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
            new CreateTemplateMacroAction(this, vdbMacro));

    regenerateMacrosID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
    return vdbMacro;
}

/**
 */
public void addPort(VDBPort vdbPort)
{
    ports.put(vdbPort.getName(), vdbPort);
    portsV.addElement(vdbPort);

    regeneratePortsID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void addMacro(VDBMacro vdbMacro)
{
    macros.put(vdbMacro.getName(), vdbMacro);
    macrosV.addElement(vdbMacro);

    regenerateMacrosID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void removePort(String name)
{
    VDBPort port = (VDBPort)ports.remove(name);
    if (port==null)
        return;

    portsV.removeElement(port);

    ComposedAction ca = new ComposedAction();

    // remove visible rep.
    VisibleObject portVisibleObj = port.getVisibleObject();
    if (portVisibleObj!=null)
    {
        portVisibleObj.destroy();
        ca.addAction(new com.cosylab.vdct.undo.DeleteAction(portVisibleObj));
        com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    }

    ca.addAction(new DeleteTemplatePortAction(this, port));

    UndoManager.getInstance().addAction(ca);

    regeneratePortsID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void removeMacro(String name)
{
    VDBMacro macro = (VDBMacro)macros.remove(name);
    if (macro==null)
        return;

    macrosV.removeElement(macro);

    ComposedAction ca = new ComposedAction();

    // remove visible rep.
    VisibleObject macroVisibleObj = macro.getVisibleObject();
    if (macroVisibleObj!=null)
    {
        macroVisibleObj.destroy();
        ca.addAction(new com.cosylab.vdct.undo.DeleteAction(macroVisibleObj));
        com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    }

    ca.addAction(new DeleteTemplateMacroAction(this, macro));

    UndoManager.getInstance().addAction(ca);

    regenerateMacrosID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void removePort(VDBPort port)
{
    ports.remove(port);
    portsV.removeElement(port);

    // no undo here

    regeneratePortsID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void removeMacro(VDBMacro macro)
{
    macros.remove(macro);
    macrosV.removeElement(macro);

    // no undo here

    regenerateMacrosID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void renamePort(VDBPort port, String newName)
{
    String oldName = port.getName();

    ports.remove(oldName);
    port.setName(newName);
    ports.put(port.getName(), port);

    // also fix visible rep.
    if (port.getVisibleObject()!=null)
        port.getVisibleObject().rename(oldName, newName);

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
            new RenameTemplatePortAction(this, port, oldName, newName));

    regeneratePortsID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 */
public void renameMacro(VDBMacro macro, String newName)
{
    String oldName = macro.getName();

    macros.remove(oldName);
    macro.setName(newName);
    macros.put(macro.getName(), macro);

    // also fix visible rep.
    if (macro.getVisibleObject()!=null)
        macro.getVisibleObject().rename(oldName, newName);

    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(
            new RenameTemplateMacroAction(this, macro, oldName, newName));

    regenerateMacrosID();
    InspectorManager.getInstance().updateObject(this);
/*
    updateTemplateFields();
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#addProperty()
 */
public void addProperty()
{
    addPort();
}

/**
 */
public VDBPort addPort()
{
    String message = "Enter port name:";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Add port...",
                                       type );
        if (reply!=null)
        {
            if (!ports.containsKey(reply))
            {
                // check name
                if (reply.trim().length()==0)
                {
                    message = "Empty name! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else if (reply.indexOf(' ')!=-1)
                {
                    message = "No spaces allowed! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else
                    return addPort(reply);
            }
            else
            {
                message = "Port '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }

        }

        break;
    }

    return null;
}

/**
 */
public VDBMacro addMacro()
{
    String message = "Enter macro name:";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Add macro...",
                                       type );
        if (reply!=null)
        {
            if (!macros.containsKey(reply))
            {
                // check name
                if (reply.trim().length()==0)
                {
                    message = "Empty name! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else if (reply.indexOf(' ')!=-1)
                {
                    message = "No spaces allowed! Enter valid name:";
                    type = JOptionPane.WARNING_MESSAGE;
                    continue;
                }
                else
                    return addMacro(reply);
            }
            else
            {
                message = "Macro '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }

        }

        break;
    }

    return null;
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#propertyChanged(InspectableProperty)
 */
public void propertyChanged(InspectableProperty property)
{
    InspectorManager.getInstance().updateProperty(this, property);

/*
    updateTemplateFields();
    InspectorManager.getInstance().updateProperty(this, null);
    unconditionalValidation();
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
*/
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#removeProperty(InspectableProperty)
 */
public void removeProperty(InspectableProperty property)
{
    if (property instanceof VDBPort)
        removePort(property.getName());
    else if (property instanceof VDBMacro)
        removeMacro(property.getName());
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#renameProperty(InspectableProperty)
 */
public void renameProperty(InspectableProperty property)
{
    if (property instanceof VDBPort)
        renamePortProperty(property);
    else if (property instanceof VDBMacro)
        renameMacroProperty(property);
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#renameProperty(InspectableProperty)
 */
public void renamePortProperty(InspectableProperty property)
{
    String message = "Enter new port name of '"+property.getName()+"':";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Rename port...",
                                        type);
        if (reply!=null)
        {

            // check name
            if (reply.trim().length()==0)
            {
                message = "Empty name! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (reply.indexOf(' ')!=-1)
            {
                message = "No spaces allowed! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (!ports.containsKey(reply))
            {
                renamePort((VDBPort)property, reply);
            }
            else
            {
                message = "Port '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }

        }

        break;
    }
}

/**
 * @see com.cosylab.vdct.vdb.MonitoredPropertyListener#renameProperty(InspectableProperty)
 */
public void renameMacroProperty(InspectableProperty property)
{
    String message = "Enter new macro name of '"+property.getName()+"':";
    int type = JOptionPane.QUESTION_MESSAGE;
    while (true)
    {
        String reply = JOptionPane.showInputDialog( null,
                                       message,
                                       "Rename macro...",
                                        type);
        if (reply!=null)
        {

            // check name
            if (reply.trim().length()==0)
            {
                message = "Empty name! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (reply.indexOf(' ')!=-1)
            {
                message = "No spaces allowed! Enter valid name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }
            else if (!macros.containsKey(reply))
            {
                renameMacro((VDBMacro)property, reply);
            }
            else
            {
                message = "Macro '"+reply+"' already exists. Enter other name:";
                type = JOptionPane.WARNING_MESSAGE;
                continue;
            }

        }

        break;
    }
}

    /**
     * Sets the fileName.
     * @param fileName The fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Sets the id.
     * @param id The id to set
     */
    public void setId(String id)
    {
        this.id = id;
        updateDescription();
        if (VDBData.getTemplates().containsKey(this.getId()))
        {
            Console.getInstance().print("Template with ID '"+this.getId()+"' is already loaded. Failed to store template '"+getFileName()+"' to the template repository.");
        }
        else
            VDBData.addTemplate(this);
    }

    /**
     * Returns the portsGeneratedID.
     * @return long
     */
    public long getPortsGeneratedID()
    {
        return portsGeneratedID;
    }

    /**
     * Returns the macrosGeneratedID.
     * @return long
     */
    public long getMacrosGeneratedID()
    {
        return macrosGeneratedID;
    }
}
