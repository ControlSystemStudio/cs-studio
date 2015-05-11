package com.cosylab.vdct.graphics.objects;

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

import java.awt.*;
import java.util.*;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.*;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.dbd.DBDConstants;

import com.cosylab.vdct.inspector.*;

import com.cosylab.vdct.graphics.popup.*;
import javax.swing.*;
import java.awt.event.*;

import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (21.12.2000 20:46:35)
 * @author Matej Sekoranja
 */
public abstract class LinkManagerObject extends ContainerObject implements Hub, Inspectable, Popupable {

    class PopupMenuHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LinkCommand cmd = (LinkCommand)CommandManager.getInstance().getCommand("LinkCommand");
            cmd.setData(LinkManagerObject.this, LinkManagerObject.this.getField(e.getActionCommand()));
             cmd.execute();
        }
    }

    public final static String nullString = "";

    // GUI linking support
    private LinkSource targetLink = null;
    public final static String inlinkString = "INLINK";
    public final static String outlinkString = "OUTLINK";
    public final static String fwdlinkString = "FWDLINK";
    public final static String varlinkString = "VARIABLE";
    public final static String varlinkPortString = "VAR. to PORT/MACRO";

/**
 * LinkManagerObject constructor comment.
 * @param parent com.cosylab.vdct.graphics.objects.ContainerObject
 */
public LinkManagerObject(ContainerObject parent)
{
    super(parent);
}

/**
 */
public void addInvalidLink(EPICSLink field)
{
}

/**
 */
public void removeInvalidLink(EPICSLink field)
{
}

/**
 */
public abstract VDBFieldData getField(String name);

/**
 * Insert the method's description here.
 * Creation date: (27.1.2001 16:12:03)
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public abstract void fieldChanged(VDBFieldData field);

/**
 * Insert the method's description here.
 * Creation date: (5.2.2001 9:42:29)
 * @param e java.util.Enumeration list of VDBFieldData fields
 * @param prevGroup java.lang.String
 * @param group java.lang.String
 */
public void fixEPICSOutLinks(Enumeration e, String prevGroup, String group) {
    if (prevGroup.equals(group)) return;

    String prefix;
    if (group.equals(nullString)) prefix=nullString;
    else prefix=group+Constants.GROUP_SEPARATOR;

    String old;
    int type; VDBFieldData field;
    while (e.hasMoreElements()) {
        field = (VDBFieldData)e.nextElement();
        type = LinkProperties.getType(field);
        if (type != LinkProperties.VARIABLE_FIELD) {
            old = field.getValue();
            if (!old.equals(nullString) && !old.startsWith(Constants.HARDWARE_LINK) &&
                old.startsWith(prevGroup)) {
                if (prevGroup.equals(nullString))
                    field.setValue(prefix+old);
                else
                    field.setValue(prefix+old.substring(prevGroup.length()+1));
            }
        }
    }

}
/**
 * Goes through link fields (in, out, var, fwd) and cheks
 * if ther are OK, if not it fixes it
 * When record is moved, renames, etc. value of in, out, fwd
 * should be changed, but visual link is still preserved :)
 * (linked list). It compares start point end end point and ...
 * Creation date: (2.5.2001 19:37:46)
 */
public void fixLinks() {

    Object unknownField;

    Enumeration e = getSubObjectsV().elements();
    while (e.hasMoreElements())
    {
            unknownField = e.nextElement();

            // go and find source
            if (unknownField instanceof EPICSVarLink)
            {
                fixLink((EPICSVarLink)unknownField);
            }
            else if (unknownField instanceof EPICSLinkOutIn)
            {
                fixLink((EPICSLinkOutIn)unknownField);
            }

    }

}


public void fixLinks_() {

    Object unknownField;
    EPICSVarLink varlink;

    Enumeration e = getSubObjectsV().elements();
    while (e.hasMoreElements())
    {
            unknownField = e.nextElement();

            // go and find source
            if (unknownField instanceof EPICSVarLink)
            {
                varlink = (EPICSVarLink)unknownField;
                fixLink(varlink);
            }

/*
            else if (unknownField instanceof EPICSLinkOut)
            {
                source = (EPICSLinkOut)unknownField;
                InLink inlink = EPICSLinkOut.getEndPoint(source);
                if (inlink!=null && inlink instanceof EPICSVarLink)
                {
                    varlink = (EPICSVarLink)inlink;
                    targetName = varlink.getFieldData().getFullName();
                    // now I got source and target, compare values
                    String oldTarget = LinkProperties.getTarget(source.getFieldData());
                    if (!oldTarget.equals(targetName))
                    {
                        // not the same, fix it gently as a doctor :)
                        String value = source.getFieldData().getValue();
                        value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
                        source.getFieldData().setValueSilently(value);
                        source.fixLinkProperties();
                    }
                }
            }

*/

    }

}

public static void fixLink(EPICSVarLink varlink)
{
    LinkSource data = null;
    String targetName = varlink.getFieldData().getFullName();

    Enumeration e2 = varlink.getStartPoints().elements();
    while (e2.hasMoreElements())
    {
        Object unknownField = e2.nextElement();
        if (unknownField instanceof OutLink)
        {
            if (unknownField instanceof EPICSLink)
                data = ((EPICSLink)unknownField).getFieldData();
            else if (unknownField instanceof Port)
                data = ((Port)unknownField).getData();
        }
        else
            continue;    // nothing to fix

        // now I got source and target, compare values
        String oldTarget = LinkProperties.getTarget(data);
        if (!oldTarget.equals(targetName))
        {
            // not the same, fix it gently as a doctor :)
            String value = data.getValue();
            // value = targetName + link properties
            value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
            data.setValueSilently(value);

            // !!!!
            if (unknownField instanceof EPICSLink)
                ((EPICSLink)unknownField).fixLinkProperties();
            else if (unknownField instanceof Port)
                ((Port)unknownField).fixLinkProperties();
        }
    }
}

/**
 * !!!! duplication
 * @param macro
 */
public static void fixMacroLink(Macro macro)
{
    LinkSource data = null;
    String targetName = macro.getData().getFullName();

    Enumeration e2 = macro.getStartPoints().elements();
    while (e2.hasMoreElements())
    {
        Object unknownField = e2.nextElement();
        if (unknownField instanceof OutLink)
        {
            if (unknownField instanceof EPICSLink)
                data = ((EPICSLink)unknownField).getFieldData();
            else if (unknownField instanceof Port)
                data = ((Port)unknownField).getData();
        }
        else
            continue;    // nothing to fix

        // now I got source and target, compare values
        String oldTarget = LinkProperties.getTarget(data);
        if (!oldTarget.equals(targetName))
        {
            // not the same, fix it gently as a doctor :)
            String value = data.getValue();
            // value = targetName + link properties
            value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
            data.setValueSilently(value);

            // !!!!
            if (unknownField instanceof EPICSLink)
                ((EPICSLink)unknownField).fixLinkProperties();
            else if (unknownField instanceof Port)
                ((Port)unknownField).fixLinkProperties();
        }
    }
}

public static void fixLink(EPICSLinkOutIn linkoutin)
{
    LinkSource data = null;
    String targetName = linkoutin.getFieldData().getFullName();

    Enumeration e2 = linkoutin.getStartPoints().elements();
    while (e2.hasMoreElements())
    {
        Object unknownField = e2.nextElement();
        if (unknownField instanceof OutLink)
        {
            if (unknownField instanceof EPICSLink)
                data = ((EPICSLink)unknownField).getFieldData();
            else if (unknownField instanceof Port)
                data = ((Port)unknownField).getData();
        }
        else
            continue;    // nothing to fix

        // now I got source and target, compare values
        String oldTarget = LinkProperties.getTarget(data);
        if (!oldTarget.equals(targetName))
        {
            // not the same, fix it gently as a doctor :)
            String value = data.getValue();
            // value = targetName + link properties
            value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
            data.setValueSilently(value);

            // !!!!
            if (unknownField instanceof EPICSLink)
                ((EPICSLink)unknownField).fixLinkProperties();
            else if (unknownField instanceof Port)
                ((Port)unknownField).fixLinkProperties();
        }
    }
}

public static void fixLink_(EPICSVarLink varlink)
{
    EPICSLinkOut source;
    String targetName = varlink.getFieldData().getFullName();

    Enumeration e2 = varlink.getStartPoints().elements();
    while (e2.hasMoreElements())
    {
        Object unknownField = e2.nextElement();
        if (unknownField instanceof EPICSLinkOut)
            source = (EPICSLinkOut)unknownField;
        else
            continue;    // nothing to fix

        // now I got source and target, compare values
        String oldTarget = LinkProperties.getTarget(source.getFieldData());
        if (!oldTarget.equals(targetName))
        {
            // not the same, fix it gently as a doctor :)
            String value = source.getFieldData().getValue();
            // value = targetName + link properties
            value = targetName + com.cosylab.vdct.util.StringUtils.removeBegining(value, oldTarget);
            source.getFieldData().setValueSilently(value);
            source.fixLinkProperties();
        }
    }
}

/**
 * Returns true if link is software link
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public static boolean isSoftwareLink(VDBFieldData field)
{
    if (field.getValue().startsWith(Constants.HARDWARE_LINK) ||
        field.getValue().startsWith("@") ||    // !!!?? INST_IO
        field.getValue().equals(nullString) /*||
        Character.isDigit(field.getValue().charAt(0))
        to be checked outside (there can be a record starting with a digit) */)
        return false;
    else
        return true;
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public boolean manageLink(VDBFieldData field) {

    int type = LinkProperties.getType(field);
    if (type == LinkProperties.VARIABLE_FIELD)
    {
        // invoke validateLink at the end

//        EPICSVarOutLink link = null;
        OutLink link = null;

        if (this.containsObject(field.getName()))
//            link = (EPICSVarOutLink)getSubObject(field.getName());
            link = (OutLink)getSubObject(field.getName());

        if (link!=null && link.getInput()!=null)
        {
            link.validateLink();
            return true;
        }


        // check new VAR->PORT/macro link
        LinkProperties properties = new LinkProperties(field);
        InLink portLink = EPICSLinkOut.getTarget(properties, false, true);

        if (portLink==null || (!(portLink instanceof TemplateEPICSPort) && !(portLink instanceof Macro)))
        {
            if (link!=null)
            {
                link.validateLink();
                return true;
            }
            else
                return false;
        }

        // create a new one
        if (link==null)
        {
            link = new EPICSVarOutLink(this, field);
            addLink(link);
        }

        portLink.setOutput(link, null);
        link.setInput(portLink);

        if (link!=null)
            link.validateLink();

        return true;

    }
    else
    {

        if (this.containsObject(field.getName()))
        {
            // existing link
            EPICSLinkOut link = (EPICSLinkOut)getSubObject(field.getName());
            link.valueChanged();
            link.setDestroyed(false);
            return true;

        }
        else if (type!=LinkProperties.TEMPLATE_MACRO)
        {
            if (!LinkManagerObject.isSoftwareLink(field))
                return false;

            // new link
            LinkProperties properties = new LinkProperties(field);
            InLink varlink = EPICSLinkOut.getTarget(properties);
            // can point to null? OK, cross will be showed

            // assume constant (this is not perfect but handles almost all the cases)
            char firstChar = field.getValue().charAt(0);     // length > 0 already checked...
            if (varlink == null &&
                    (Character.isDigit(firstChar) || firstChar == '.' ||
                     firstChar == '-' || firstChar == '+')
                )
                return false;

            EPICSLinkOut outlink = null;

            if (type==LinkProperties.INLINK_FIELD)
                outlink = new EPICSInLink(this, field);
            else if (type==LinkProperties.OUTLINK_FIELD)
                outlink = new EPICSOutLink(this, field);
            else /*if (type==LinkProperties.FWDLINK_FIELD)*/
                outlink = new EPICSFwdLink(this, field);

            addLink(outlink);

            if (varlink!=null) varlink.setOutput(outlink, null);
            outlink.setInput(varlink);

            return true;
        }
        else
            return false;
    }
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 9:36:15)
 * @return boolean
 * @param field com.cosylab.vdct.vdb.VDBFieldData
 */
public boolean manageLink_(VDBFieldData field) {

    int type = LinkProperties.getType(field);
    if (type == LinkProperties.VARIABLE_FIELD)
    {
        if (this.containsObject(field.getName()))
        {
            EPICSVarLink link = (EPICSVarLink)getSubObject(field.getName());
            link.validateLink();
            return true;
        }
        return false;
    }
    else
    {

        if (this.containsObject(field.getName()))
        {
            // existing link
            EPICSLinkOut link = (EPICSLinkOut)getSubObject(field.getName());
            link.valueChanged();
            link.setDestroyed(false);
            return true;

        }
        else
        {
            if (!LinkManagerObject.isSoftwareLink(field))
                return false;

            // new link
            LinkProperties properties = new LinkProperties(field);
            InLink varlink = EPICSLinkOut.getTarget(properties);
            // can point to null? OK, cross will be showed

            EPICSLinkOut outlink = null;

            if (type==LinkProperties.INLINK_FIELD)
                outlink = new EPICSInLink(this, field);
            else if (type==LinkProperties.OUTLINK_FIELD)
                outlink = new EPICSOutLink(this, field);
            else /*if (type==LinkProperties.FWDLINK_FIELD)*/
                outlink = new EPICSFwdLink(this, field);

            addLink(outlink);
            /*if (!properties.isIsInterGroupLink())
            {
                String id = EPICSLinkOut.generateConnectorID(outlink);
                Connector connector = new Connector(id, this, outlink, varlink);
                if (varlink!=null)
                {
                    connector.setX((outlink.getOutX()+varlink.getInX())/2);
                    connector.setY((outlink.getOutY()+varlink.getInY())/2);
                }
                addSubObject(id, connector);
            }
            else*/
            {
                if (varlink!=null) varlink.setOutput(outlink, null);
                outlink.setInput(varlink);
            }

            return true;
        }
    }
}

/**
 * Insert the method's description here.
 * Creation date: (1.2.2001 17:38:36)
 * @param dx int
 * @param dy int
 */
public void moveConnectors(int dx, int dy) {

  ViewState view = ViewState.getInstance();
  Enumeration e = subObjectsV.elements();
  Connector con; Object obj;
  while (e.hasMoreElements()) {
    obj = e.nextElement();
    if (obj instanceof Connector) {
        con = (Connector)obj;
        con.revalidatePosition();

        if (view.isSelected(con))
            continue;    // will move by itself
        InLink endpoint = EPICSLinkOut.getEndPoint(con);
        /*OutLink startpoint = EPICSLinkOut.getStartPoint(con);
        EPICSLinkOut lo = null;
        if (!(startpoint instanceof EPICSLinkOut))
            lo = (EPICSLinkOut)startpoint;*/
        if (((endpoint instanceof EPICSLink) &&
            (view.isSelected(((EPICSLink)endpoint).getParent())) /*||
            ((lo!=null) && lo.getLinkProperties().isIsInterGroupLink())*/)
            ||
            ((endpoint instanceof LinkManagerObject) && view.isSelected(endpoint)))
            con.move(dx, dy);
    }
  }
}

/**
 * Insert the method's description here.
 * Creation date: (21.12.2000 21:58:56)
 * @param g java.awt.Graphics
 * @param hilited boolean
 */
public void postDraw(Graphics g, boolean hilited) {
    Enumeration e = subObjectsV.elements();
    VisibleObject vo;
    while (e.hasMoreElements()) {
        vo = (VisibleObject)(e.nextElement());
        if (vo instanceof Connector)
            vo.paint(g, hilited);
    }

}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:31:29)
 * @return java.util.Vector
 */
public Vector getLinkMenus(Enumeration vdbFields) {
    Vector items = new Vector();
    ActionListener l = createPopupmenuHandler();
    VDBFieldData field;
    JMenuItem menuitem;

    boolean portOrTemplateMacro2All = (getTargetLink() instanceof VDBPort) ||
                                      (getTargetLink() instanceof VDBTemplateMacro);

    if (getTargetLink()==null || portOrTemplateMacro2All) {

        JMenu inlinks = new JMenu(inlinkString);
        JMenu outlinks = new JMenu(outlinkString);
        JMenu fwdlinks = new JMenu(fwdlinkString);
        JMenu varlinks = null;
        if (portOrTemplateMacro2All)
            varlinks = new JMenu(varlinkString);
        else
            varlinks = new JMenu(varlinkPortString);

        JMenu inMenu = inlinks;
        JMenu outMenu = outlinks;
        JMenu fwdMenu = fwdlinks;
        JMenu varMenu = varlinks;

        int inpItems, outItems, fwdItems;
        int varItems;
        inpItems=outItems=fwdItems=0;
        varItems=0;

        while (vdbFields.hasMoreElements()) {
            field = (VDBFieldData)(vdbFields.nextElement());
            // do not show non-empty fields since their value will be overriden
            if (field.getValue().equals(nullString) || field.getValue().equals(Constants.NONE)) {
                switch (field.getType()) {
                    case DBDConstants.DBF_INLINK:
                         menuitem = new JMenuItem(field.getName());
                         menuitem.addActionListener(l);
                         inlinks = PopUpMenu.addItem(menuitem, inlinks, inpItems);
                         inpItems++;
                         break;
                    case DBDConstants.DBF_OUTLINK:
                         menuitem = new JMenuItem(field.getName());
                         menuitem.addActionListener(l);
                         outlinks = PopUpMenu.addItem(menuitem, outlinks, outItems);
                         outItems++;
                         break;
                    case DBDConstants.DBF_FWDLINK:
                         menuitem = new JMenuItem(field.getName());
                         menuitem.addActionListener(l);
                         fwdlinks = PopUpMenu.addItem(menuitem, fwdlinks, fwdItems);
                         fwdItems++;
                         break;
                    default:

                         // no not add fields with undefined GUI type
                         // TODO DBD VAL promptgroup workaround
                         if (!portOrTemplateMacro2All &&
                             (field.getGUI_type() == DBDConstants.GUI_UNDEFINED && !field.getName().equals("VAL")))
                             break;

                         menuitem = new JMenuItem(field.getName());
                         menuitem.addActionListener(l);
                         varlinks = PopUpMenu.addItem(menuitem, varlinks, varItems);
                         varItems++;
                         break;

                }
            }
        }

        if (inMenu.getItemCount() > 0)
            items.addElement(inMenu);
        if (outMenu.getItemCount() > 0)
            items.addElement(outMenu);
        if (fwdMenu.getItemCount() > 0)
            items.addElement(fwdMenu);
        if (varMenu.getItemCount() > 0)
            items.addElement(varMenu);

    }
    else if ((getTargetLink().getType() == DBDConstants.DBF_INLINK) ||
                (getTargetLink().getType() == DBDConstants.DBF_OUTLINK) ||
              (getTargetLink().getType() == DBDConstants.DBF_FWDLINK)) { // no targets (only ports) for VAR->PORTS
        int count = 0;
        JMenu varlinkItem = new JMenu(varlinkString);
        JMenu menu = varlinkItem;

        while (vdbFields.hasMoreElements()) {
            field = (VDBFieldData)(vdbFields.nextElement());
/*            switch (field.getType()) {
                case DBDConstants.DBF_CHAR:
                case DBDConstants.DBF_UCHAR:
                case DBDConstants.DBF_SHORT:
                case DBDConstants.DBF_USHORT:
                case DBDConstants.DBF_LONG:
                case DBDConstants.DBF_ULONG:
                case DBDConstants.DBF_FLOAT:
                case DBDConstants.DBF_DOUBLE:
                case DBDConstants.DBF_STRING:
                case DBDConstants.DBF_NOACCESS:
                case DBDConstants.DBF_ENUM:
                case DBDConstants.DBF_MENU:
                case DBDConstants.DBF_DEVICE:  // ?
                  menuitem = new JMenuItem(field.getName());
                  menuitem.addActionListener(l);
                  menu = PopUpMenu.addItem(menuitem, menu, count);
                  count++;
            }
*/
            if (field.getType()!=DBDConstants.DBF_INLINK &&
                field.getType()!=DBDConstants.DBF_OUTLINK &&
                field.getType()!=DBDConstants.DBF_FWDLINK)
            {
                  menuitem = new JMenuItem(field.getName());
                  menuitem.addActionListener(l);
                  menu = PopUpMenu.addItem(menuitem, menu, count);
                  count++;
            }

        }
        if (count > 0) items.addElement(varlinkItem);

    }

    return items;
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 23:00:51)
 * @return com.cosylab.vdct.graphics.objects.LinkManagerObject.PopupMenuHandler
 */
private com.cosylab.vdct.graphics.objects.LinkManagerObject.PopupMenuHandler createPopupmenuHandler() {
    return new PopupMenuHandler();
}

/**
 * Insert the method's description here.
 * Creation date: (30.1.2001 11:59:54)
 */
protected void destroyFields() {

    Object[] objs = new Object[subObjectsV.size()];
    subObjectsV.copyInto(objs);
    for (int i=0; i < objs.length; i++)
        ((VisibleObject)objs[i]).destroy();

}


    /**
     * Returns the targetLink.
     * @return LinkSource
     */
    public LinkSource getTargetLink()
    {
        return targetLink;
    }

    /**
     * Sets the targetLink.
     * @param targetLink The targetLink to set
     */
    public void setTargetLink(LinkSource targetLink)
    {
        this.targetLink = targetLink;
    }

/**
 * @param linkableMacros
 * @param macros
 * @param deep
 */
public static void checkIfMacroCandidate(VDBFieldData field, HashMap macros) {

    String value = field.getValue();
    final String macroStart = "$(";

    int startMacroPos = 0;
    while ((startMacroPos = value.indexOf(macroStart, startMacroPos)) != -1)
    {
        // search string is 2 chars long and this will not cause out of bounds error
        // if match is at very end of the string
        startMacroPos++;

        // find end of macro reference
        int endMacroPos = value.indexOf(')', startMacroPos);

        // invalid reference
        if (endMacroPos == -1 || endMacroPos == startMacroPos+1)
            continue;

        // check if not port
        int dotPos = value.indexOf('.', startMacroPos);
        if (dotPos != -1 && dotPos < endMacroPos)
            continue;

        // "composed macros" are not supported !!!
        dotPos = value.indexOf('$', startMacroPos);
        if (dotPos != -1 && dotPos < endMacroPos)
            continue;

        // check if not already defined
        String macroName = value.substring(startMacroPos-1, endMacroPos+1);
        if (Group.getRoot().getLookupTable().get(macroName)!=null)
            continue;

        // we have got undefined macro, add it to the list
        ArrayList list = (ArrayList)macros.get(macroName);
        if (list == null)
        {
            list = new ArrayList();
            macros.put(macroName, list);
        }
        if (!list.contains(field))
            list.add(field);

    }

}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:54:43)
 * @return boolean
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isFirstField(Field field) {
    // find first field and compare

    Enumeration e = subObjectsV.elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof Field)
            if (obj==field)
                return true;
            else
                return false;
    }

    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:53:47)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public boolean isLastField(Field field) {
    for (int i= subObjectsV.size()-1; i>=0; i--)
        if (subObjectsV.elementAt(i) instanceof Field)
            if (subObjectsV.elementAt(i)==field)
                return true;
            else
                return false;
    return false;

}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldDown(Field field) {
    // move visual field
    Vector fields = getSubObjectsV();
    int pos = fields.indexOf(field);

    pos++;
    while (pos<fields.size() && !(fields.elementAt(pos) instanceof Field))
        pos++;

    if (pos<fields.size()) {
        fields.removeElement(field);
        fields.insertElementAt(field, pos);
        revalidateFieldsPosition();
    }
    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldDownAction(field));
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public void moveFieldUp(Field field) {
    // move visual field
    Vector fields = getSubObjectsV();
    int pos = fields.indexOf(field);
    pos--;
    while (pos>=0 && !(fields.elementAt(pos) instanceof Field))
        pos--;

    if (pos>=0) {
        fields.removeElement(field);
        fields.insertElementAt(field, pos);
        revalidateFieldsPosition();
    }

    com.cosylab.vdct.events.CommandManager.getInstance().execute("RepaintWorkspace");
    com.cosylab.vdct.undo.UndoManager.getInstance().addAction(new com.cosylab.vdct.undo.MoveFieldUpAction(field));
}

/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 22:36:11)
 * @param field com.cosylab.vdct.graphics.objects.Field
 */
public abstract void revalidateFieldsPosition();

}
