package com.cosylab.vdct.graphics.popup;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

import com.cosylab.vdct.VisualDCT;
import com.cosylab.vdct.events.CommandManager;
import com.cosylab.vdct.events.commands.GetGUIInterface;
import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.EPICSVarOutLink;
import com.cosylab.vdct.graphics.objects.Flexible;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Morphable;
import com.cosylab.vdct.graphics.objects.MultiInLink;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.Rotatable;
import com.cosylab.vdct.graphics.objects.VisibleObject;
import com.cosylab.vdct.plugin.popup.PluginPopupManager;

/**
 * Insert the type's description here.
 * Creation date: (27.1.2001 17:30:55)
 * @author Matej Sekoranja
 */
public class PopUpMenu extends JPopupMenu {
    private static PopUpMenu instance = null;
    private static final String moreString = "More";
    private static final int ITEMS_PER_MENU = 10;
    private static JSeparator separator = null;

    private static final String rotateLinkString = "Rotate Link";

    private static final String cutString = "Cut";
    private static final String copyString = "Copy";
    private static final String moveRenameString = "Move/Rename";
    private static final String morphString = "Morph";
    private static final String deleteString = "Delete";

    private static final String groupString = "Group";
    private static final String ungroupString = "Ungroup";

/**
 * LinkingPopupMenu constructor comment.
 */
public PopUpMenu() {

}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 10:37:54)
 * @return javax.swing.JMenu
 * @param menuItem javax.swing.JMenuItem
 * @param menu javax.swing.JMenu
 */

public static JMenu addItem(JMenuItem item, JMenu menu, int count) {
  if ((count>0) && ((count%ITEMS_PER_MENU)==0)) menu=addMoreMenu(menu);
  menu.add(item);
  return menu;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 10:39:45)
 * @return javax.swing.JMenu
 * @param menu javax.swing.JMenu
 */
public static JMenu addMoreMenu(JMenu menu) {
    JMenu more = new JMenu(moreString);
    menu.addSeparator();
    menu.add(more);
    return more;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:18:55)
 * @return com.cosylab.vdct.graphics.popup.PopUpMenu
 */
public static PopUpMenu getInstance() {
    if (instance==null) instance = new PopUpMenu();
    return instance;
}

/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:18:55)
 * @return javax.swing.JSeparator
 */
public static JSeparator getSeparator() {
    if (separator==null) separator = new JSeparator();
    return separator;
}

class FlexiblePopupMenuHandler implements ActionListener {
    Flexible f;

    public FlexiblePopupMenuHandler(Flexible flexible) {
        f = flexible;
    }

    public void actionPerformed(ActionEvent e) {
        GetGUIInterface cmd = (GetGUIInterface)CommandManager.getInstance().getCommand("GetGUIMenuInterface");
        String action = e.getActionCommand();

        ViewState view = ViewState.getInstance();

        if (action.equals(ungroupString)) {    // we only ungroup one group
           view.deselectAll();
           view.setAsSelected((VisibleObject)f);
           cmd.getGUIMenuInterface().ungroup();
           return;
       }

        if (!view.isSelected(f)) {
            view.deselectAll();
            view.setAsSelected((VisibleObject)f);
        }


        if (action.equals(cutString)) {
            cmd.getGUIMenuInterface().cut();
        } else if (action.equals(copyString)) {
            cmd.getGUIMenuInterface().copy();
        } else if (action.equals(moveRenameString)) {
            cmd.getGUIMenuInterface().rename();
        } else if (action.equals(morphString)) {
            cmd.getGUIMenuInterface().morph();
        }  else if (action.equals(deleteString)) {
            cmd.getGUIMenuInterface().delete();
        }  else if (action.equals(groupString)) {
            VisualDCT.getInstance().showGroupDialog();
        }
    }


}



/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 20:17:37)
 * @param object com.cosylab.vdct.graphics.popup.Popupable
 * @param component javax.swing.JComponent
 * @param x int
 * @param y int
 */
public void show(Object object, JComponent component, int x, int y) {
    // add object items
    setLabel("");
    Vector items = new Vector();

    if (object instanceof Popupable) {
        items = ((Popupable)object).getItems();
        setLabel(((Popupable)object).getLabel());
        if (items==null) items = new Vector();
    }

    if (object instanceof Rotatable && !(object instanceof EPICSVarOutLink))
        addRotatableItems((Rotatable)object, items);

    if (object instanceof Flexible)
        populateWithFlexible((Flexible)object, items);

    populate(items, component, x, y);

    if (object instanceof Popupable)
        addPluginItems(this, (Popupable)object);

    //    show popup menu
     show(component, x, y);
}

private void addRotatableItems(final Rotatable rot, Vector items) {

    // rotate only if more than one inlink is connected
    // if not, automatic mode is used
    if (rot instanceof MultiInLink &&
            ((MultiInLink)rot).getLinkCount() <    2)
        return;

    // dirty...
    if (rot instanceof Record) items.add(new JSeparator());

    JMenuItem menuitem = new JMenuItem(rotateLinkString);
    menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            rot.rotate();
            DrawingSurface.getInstance().repaint();
        }
    });
    items.add(menuitem);


}

private void populateWithFlexible(Flexible object, Vector items) {
    // add Flexible items
    if (object instanceof VisibleObject) {

        if (items.size() != 0) items.add(new JSeparator());

        ActionListener l = new FlexiblePopupMenuHandler((Flexible)object);

        JMenuItem menuitem = new JMenuItem(cutString);
        menuitem.addActionListener(l);
        items.add(menuitem);

        menuitem = new JMenuItem(copyString);
        menuitem.addActionListener(l);
        items.add(menuitem);

        menuitem = new JMenuItem(moveRenameString);
        menuitem.addActionListener(l);
        items.add(menuitem);

        if (object instanceof Morphable) {
            menuitem = new JMenuItem(morphString);
            menuitem.addActionListener(l);
            items.add(menuitem);
        }

        menuitem = new JMenuItem(deleteString);
        menuitem.addActionListener(l);
        items.add(menuitem);

        items.add(new JSeparator());

        menuitem = new JMenuItem(groupString);
        menuitem.addActionListener(l);
        items.add(menuitem);

        if (object instanceof Group) {
            menuitem = new JMenuItem(ungroupString);
            menuitem.addActionListener(l);
            items.add(menuitem);
        }
    }

}

private void populate(Vector items, JComponent component, int x, int y) {
    if ((items==null) || (items.size()==0)) return;

    if (getComponentCount()>0) removeAll();

    Object obj;
    Enumeration e = items.elements();
    while (e.hasMoreElements())
    {
        obj = e.nextElement();
        if (obj instanceof JMenuItem)
            add((JMenuItem)obj);
        else if (obj instanceof JSeparator)
            add((JSeparator)obj);
    }
}

/**
 * Helper method which adds plugin items
 */
public static void addPluginItems(JPopupMenu menu, Popupable object)
{
    // get list of all selected object if this object is also selected
    // otherwise only give this object
    // if selected items vector is null, we have popup over empty workspace
    Vector selectedItems = null;
    if (object!=null)
    {
        selectedItems = new Vector();
        ViewState view = ViewState.getInstance();
        if (view.isSelected(object))
            // make a copy of vector to allow plugins to play with it
            selectedItems.addAll(view.getSelectedObjects());
        else
            selectedItems.addElement(object);
    }

    Vector items = PluginPopupManager.getInstance().getAllPluginItems(selectedItems);

    if (items.size() > 0)
        menu.add(getSeparator());

    Enumeration e = items.elements();
    while (e.hasMoreElements())
    {
        Object obj = e.nextElement();
        if (obj instanceof JMenuItem)
            menu.add((JMenuItem) obj);
        else if (obj instanceof JSeparator)
            menu.add((JSeparator) obj);
    }
}
}
