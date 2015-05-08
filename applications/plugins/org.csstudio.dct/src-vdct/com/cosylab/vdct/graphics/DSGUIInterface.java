package com.cosylab.vdct.graphics;

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

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.cosylab.vdct.*;
import com.cosylab.vdct.Console;
import com.cosylab.vdct.vdb.*;
import com.cosylab.vdct.undo.*;
import com.cosylab.vdct.graphics.objects.*;
import com.cosylab.vdct.events.*;
import com.cosylab.vdct.events.commands.*;

/**
 * Insert the type's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @author Matej Sekoranja
 */
public class DSGUIInterface implements GUIMenuInterface, VDBInterface {

    private static DSGUIInterface instance = null;

    private DrawingSurface drawingSurface;

    // to remember on cut from which group object has beed cut
    private ArrayList pasteNames = null;
    // to remember copied objects for multiple pasting
    private Vector copiedObjects = null;
    private int pasteCount = 0;

    private double pasteX;
    private double pasteY;
    private boolean doOffsetAtPaste = false;

    //private static final String nullString = "";

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:49)
 * @param drawingSurface com.cosylab.vdct.graphics.DrawingSurface
 */
public DSGUIInterface(DrawingSurface drawingSurface) {
    this.drawingSurface=drawingSurface;
    DSGUIInterface.instance = this;
    pasteNames = new ArrayList();
    copiedObjects = new Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:12:21)
 */
public void moveOrigin(int direction)
{
    int dx = 0;
    int dy = 0;
    ViewState view = ViewState.getInstance();
    int d = (int)(100*view.getScale());

    switch (direction)
    {
        case SwingConstants.WEST:
            dx =- d;
            break;
        case SwingConstants.EAST:
            dx =+ d;
            break;
        case SwingConstants.NORTH:
            dy =+ d;
            break;
        case SwingConstants.SOUTH:
            dy =- d;
            break;
    }

    if (view.moveOrigin(dx, dy))
    {
        drawingSurface.setBlockNavigatorRedrawOnce(true);
        drawingSurface.recalculateNavigatorPosition();
        drawingSurface.repaint();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void baseView() {
    drawingSurface.baseView();
}
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
public java.lang.String checkGroupName(String name, boolean relative) {
    return checkRecordName(name, relative);
}
/**
 * Returns error message or null if OK
 * Creation date: (3.2.2001 22:11:01)
 * @return java.lang.String
 * @param name java.lang.String
 */
public java.lang.String checkRecordName(String name, boolean relative) {

    if (name.trim().length()==0) {
        return "Empty name!";
    }
    else if (name.indexOf(' ')!=-1) return "No spaces allowed!";

    else if (!relative && (Group.getRoot().findObject(name, true)!=null))
        return "Name already exists!";
    else if (relative && (drawingSurface.getViewGroup().findObject(name, true)!=null))
        return "Name already exists!";
    else if (name.length()>Settings.getInstance().getRecordLength()) {
        return "WARNING: Name length is "+name.length()+" characters!";
    }
    else
        return null;

}
public void copyToSystemClipboard(Vector objs)
{
    try
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        NamingContext nc = new NamingContext(null, Group.getEditingTemplateData(), null, null, false);

        if (Group.getEditingTemplateData() != null)
        {
            boolean hasPortOrMacro = false;
            Enumeration en = objs.elements();
            while (en.hasMoreElements())
            {
                Object o = en.nextElement();
                if (o instanceof Port || o instanceof Macro)
                {
                    hasPortOrMacro = true;
                    break;
                }
            }
            if (hasPortOrMacro)
                Group.writeTemplateData(dos, nc, objs);
        }

        Group.writeObjects(objs, dos, nc, false);
        Group.writeVDCTData(objs, dos, nc, false);

        StringSelection ss = new StringSelection(baos.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
    }
    catch (Throwable th)
    {
        th.printStackTrace();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void copy() {
    ViewState view = ViewState.getInstance();
    copy(view.getSelectedObjects(), true);
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void systemCopy() {
    ViewState view = ViewState.getInstance();
    copyToSystemClipboard(view.getSelectedObjects());
    view.deselectAll();
    drawingSurface.repaint();
}


private void copy(Vector objects, boolean firstCopy) {

    if (objects.size()==0) return;
    Group.getClipboard().destroy();

    ViewState view = ViewState.getInstance();
    pasteNames.clear();
    if (firstCopy) {
        copiedObjects.clear();
        pasteCount = 0;
    } else {
        pasteCount++;
    }

    Object obj;
    Enumeration selected = objects.elements();

    int minx=Integer.MAX_VALUE, miny=Integer.MAX_VALUE;
    while (selected.hasMoreElements()) {
        obj = selected.nextElement();
        if (obj instanceof VisibleObject) {
            minx = Math.min(minx, ((VisibleObject)obj).getX());
            miny = Math.min(miny, ((VisibleObject)obj).getY());
        }
        if (firstCopy)
            copiedObjects.add(obj);
    }

    // remember position for paste
    pasteX = (minx - view.getRx()/view.getScale());
    pasteY = (miny - view.getRy()/view.getScale());
    doOffsetAtPaste = true;

    selected = objects.elements();

    while (selected.hasMoreElements()) {
        obj = selected.nextElement();
        if (obj instanceof Flexible) {
            Flexible copy = ((Flexible)obj).copyToGroup(Constants.CLIPBOARD_NAME);
            if (copy instanceof Movable)
                ((Movable)copy).move(-minx, -miny);

        }
    }
    // fix links (due to order of copying links might not be validated...)
    Group.getClipboard().manageLinks(true);
    view.deselectAll();
    drawingSurface.repaint();

}

public Box createBox()
{
    Group parentGroup = drawingSurface.getViewGroup();

    ViewState view = ViewState.getInstance();
    double scale = view.getScale();

    int posX = (int)((drawingSurface.getPressedX() + view.getRx()) / scale);
    int posY = (int)((drawingSurface.getPressedY() + view.getRy()) / scale);

    //String parentName = parentGroup.getAbsoluteName();

    Box grBox = new Box(null, parentGroup, posX, posY, posX, posY);
    if (Settings.getInstance().getSnapToGrid())
        grBox.snapToGrid();

    Group.getRoot().addSubObject(grBox.getName(), grBox, true);

    drawingSurface.repaint();

    return grBox;
}

public Line createLine()
{
    Group parentGroup = drawingSurface.getViewGroup();

    ViewState view = ViewState.getInstance();
    double scale = view.getScale();

    int posX = (int)((drawingSurface.getPressedX() + view.getRx()) / scale);
    int posY = (int)((drawingSurface.getPressedY() + view.getRy()) / scale);

    //String parentName = parentGroup.getAbsoluteName();

    Line grLine = new Line(null, parentGroup, posX, posY, posX, posY);
    if (Settings.getInstance().getSnapToGrid())
        grLine.snapToGrid();

    Group.getRoot().addSubObject(grLine.getName(), grLine, true);

    drawingSurface.repaint();

    return grLine;
}

public TextBox createTextBox()
{
    Group parentGroup = drawingSurface.getViewGroup();

    ViewState view = ViewState.getInstance();
    double scale = view.getScale();

    int posX = (int)((drawingSurface.getPressedX() + view.getRx()) / scale);
    int posY = (int)((drawingSurface.getPressedY() + view.getRy()) / scale);

    //String parentName = parentGroup.getAbsoluteName();

    TextBox grTextBox = new TextBox(null, parentGroup, posX, posY, posX, posY);
    if (Settings.getInstance().getSnapToGrid())
        grTextBox.snapToGrid();

    Group.getRoot().addSubObject(grTextBox.getName(), grTextBox, true);

    grTextBox.setBorder(true);

    drawingSurface.repaint();

    return grTextBox;
}

/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 23:27:30)
 * @param name java.lang.String
 * @param type java.lang.String
 * @param relative boolean
 */
public void createRecord(String name, String type, boolean relative) {
    if (relative)
    {
        String parentName = drawingSurface.getViewGroup().getAbsoluteName();
        if (parentName.length()>0)
            name = parentName + Constants.GROUP_SEPARATOR + name;
    }

    VDBRecordData recordData = VDBData.getNewVDBRecordData(
            DataProvider.getInstance().getDbdDB(), type, name);
    if (recordData==null) {
        Console.getInstance().println("o) Interal error: failed to create record "+name+" ("+type+")!");
        return;
    }

    ViewState view = ViewState.getInstance();
    double scale = view.getScale();

    Record record = new Record(null,
                               recordData,
                               (int)((drawingSurface.getPressedX() + view.getRx()) / scale),
                               (int)((drawingSurface.getPressedY() + view.getRy()) / scale));
    if (Settings.getInstance().getSnapToGrid())
        record.snapToGrid();

    Group.getRoot().addSubObject(name, record, true);

    UndoManager.getInstance().addAction(new CreateAction(record));

    //drawingSurface.setModified(true);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void cut() {
    ViewState view = ViewState.getInstance();
    //copyToSystemClipboard(view.getSelectedObjects());

    if (view.getSelectedObjects().size()==0) return;
    Group.getClipboard().destroy();

    pasteNames.clear();
    copiedObjects.clear();

    Object obj;
    Enumeration selected = view.getSelectedObjects().elements();

    int minx=Integer.MAX_VALUE, miny=Integer.MAX_VALUE;
    while (selected.hasMoreElements()) {
        obj = selected.nextElement();
        if (obj instanceof VisibleObject) {
            minx = Math.min(minx, ((VisibleObject)obj).getX());
            miny = Math.min(miny, ((VisibleObject)obj).getY());
        }
        copiedObjects.add(obj);
    }

    // remember position for paste
    pasteX = (minx - view.getRx()/view.getScale());
    pasteY = (miny - view.getRy()/view.getScale());
    doOffsetAtPaste = false;

    selected = view.getSelectedObjects().elements();
    while (selected.hasMoreElements()) {
        obj = selected.nextElement();
        if (obj instanceof Flexible) {
            Flexible flex = (Flexible)obj;
            String oldGroup = Group.substractParentName(flex.getFlexibleName());
            if (flex.moveToGroup(Constants.CLIPBOARD_NAME))
            {
                pasteNames.add(oldGroup);
                if (obj instanceof Movable)
                    //((Movable)obj).move(-view.getRx(), -view.getRy());
                    ((Movable)obj).move(-minx, -miny);
            }
        }
    }

    view.deselectAll();
    view.setAsHilited(null);
    drawingSurface.setModified(true);
    drawingSurface.repaint();


}


/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void delete() {
    ViewState view = ViewState.getInstance();
    if (view.getSelectedObjects().size()==0) return;

    try    {

        UndoManager.getInstance().startMacroAction();

        VisibleObject obj;
        Enumeration selected = view.getSelectedObjects().elements();
        while (selected.hasMoreElements())
        {
            obj = (VisibleObject)selected.nextElement();

            if (obj instanceof Connector)
            {
                ((Connector)obj).bypass();
            }
            else
            {
                obj.destroy();
                UndoManager.getInstance().addAction(new DeleteAction(obj));
            }

        }

    }
    catch (Exception e)
    {
    }
    finally
    {
        UndoManager.getInstance().stopMacroAction();
    }

    view.deselectAll();
    view.deblinkAll();
    view.setAsHilited(null);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 18:08:42)
 * @return com.cosylab.vdct.graphics.DSGUIInterface
 */
public static DSGUIInterface getInstance() {
    return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void group(String groupName) {
    ViewState view = ViewState.getInstance();
    if (view.getSelectedObjects().size()==0) return;

    ComposedAction composedAction = new ComposedAction();

    Group g = (Group)Group.getRoot().findObject(groupName, true);
    if (g==null)
    {
        g = Group.createGroup(groupName);
        if (Settings.getInstance().getSnapToGrid())
            g.snapToGrid();
        composedAction.addAction(new CreateAction(g));
    }

    int n = 0; int avgX = 0; int avgY = 0;
    Object obj; Flexible flex; String oldGroup;
    Enumeration selected = view.getSelectedObjects().elements();
    while (selected.hasMoreElements()) {
        obj = selected.nextElement();
        if (obj instanceof Flexible)
        {
            flex = (Flexible)obj; oldGroup = Group.substractParentName(flex.getFlexibleName());
            flex.moveToGroup(groupName);

            composedAction.addAction(new MoveToGroupAction(flex, oldGroup, groupName));        // if true ?!!!

            if (obj instanceof VisibleObject)
            {
                VisibleObject vo = (VisibleObject)obj;
                avgX += vo.getX();    avgY += vo.getY(); n++;
            }
        }
    }

    UndoManager.getInstance().addAction(composedAction);

    //g = (Group)Group.getRoot().findObject(groupName, true);
    if ((g!=null) && (n!=0)) {
        // center of all
        g.setX(avgX/n); g.setY(avgY/n);
    }
    view.deselectAll();
    if (g.getParent()==drawingSurface.getViewGroup())
        view.setAsSelected(g);
    drawingSurface.getViewGroup().manageLinks(true);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importDB(java.io.File file) throws IOException {
    drawingSurface.importDB(file);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importTemplateDB(java.io.File file) throws IOException {
    drawingSurface.open(file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importFields(java.io.File file) throws IOException {
    int result = JOptionPane.showConfirmDialog(VisualDCT.getInstance(),
                    "Do you want to ignore database link fields?",
                    "Import fields",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

    // window closed
    if (result == JOptionPane.CLOSED_OPTION)
        return;

    boolean ignoreLinkFields = (result == JOptionPane.OK_OPTION);
    drawingSurface.importFields(file, ignoreLinkFields);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importBorder(java.io.File file) throws IOException {
    drawingSurface.importBorder(file);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void importDBD(java.io.File file) throws IOException {
    drawingSurface.openDBD(file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (29.4.2001 11:37:22)
 * @return boolean
 */
public boolean isModified() {
    return drawingSurface.isModified();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void levelUp() {
    drawingSurface.moveLevelUp();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void newCmd() {
    drawingSurface.initializeWorkspace();

    SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
    cmd.setFile(null);
    cmd.execute();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void openDB(java.io.File file) throws IOException {
    if (drawingSurface.open(file))
    {
         //!!!
         VisualDCT.getInstance().setOpenedFile(file);
        //SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
        //cmd.setFile(file.getCanonicalPath());
        //cmd.execute();
    }
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void openDBD(java.io.File file) throws IOException {
    drawingSurface.openDBD(file);
}

public void systemPaste() {
    try
    {
        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
         boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
         if (hasTransferableText)
         {
             String str = (String)contents.getTransferData(DataFlavor.stringFlavor);
             if (str == null || str.length() == 0)
                 return;
            ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
            drawingSurface.open(bais, null, true, true);
         }
    }
    catch (Throwable th)
    {
        th.printStackTrace();
    }
}

public void paste() {
    // do some offset (a little trick to have snapping also done) for copy only
    final int OFFSET = Constants.GRID_SIZE;
    double scale = ViewState.getInstance().getScale();
    double px = pasteX <= 0 ? 0 : pasteX;
    double py = pasteY <= 0 ? 0 : pasteY;
    if (doOffsetAtPaste)
        pasteAtPosition((int)((pasteX+OFFSET)*scale), (int)((pasteY+OFFSET)*scale));
    else
        pasteAtPosition((int)(pasteX*scale), (int)(pasteY*scale));
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void pasteAtPosition(int pX, int pY) {
    ViewState view = ViewState.getInstance();
    String currentGroupName = drawingSurface.getViewGroup().getAbsoluteName();

    Group group = Group.getClipboard();

    int size = group.getSubObjectsV().size();
    if (size==0) return;

    double scale = view.getScale();
    int posX = (int)((pX + view.getRx()) / scale) + pasteCount*Constants.MULTIPLE_PASTE_GAP;
    int posY = (int)((pY + view.getRy()) / scale) + pasteCount*Constants.MULTIPLE_PASTE_GAP;

    posX = posX <= 0 ? 0 : posX;
    posX = posX >= view.getWidth() - group.getAbsoulteWidth() ? view.getWidth() - group.getAbsoulteWidth() : posX;
    posY = posY <= 0 ? 0 : posY;
    posY = posY >= view.getHeight() - group.getAbsoulteHeight() ? view.getHeight() - group.getAbsoulteHeight() : posY;

    Object objs[] = new Object[size];
    group.getSubObjectsV().copyInto(objs);

    for(int i=0; i<size; i++) {
        if (objs[i] instanceof Flexible)
                view.setAsSelected((VisibleObject)objs[i]);
    }

    boolean isCopy = pasteNames.size()!=size;
    ComposedAction composedAction = new ComposedAction();

    Flexible flex;
    for(int i=0; i<size; i++) {
        if (objs[i] instanceof Flexible) {
            flex = (Flexible)objs[i];

            if (flex.moveToGroup(currentGroupName))
            {
                if (isCopy)
                    composedAction.addAction(new CreateAction((VisibleObject)objs[i]));        // if true ?!!!
                else {
                    //System.out.println("Cut/paste:"+pasteNames.get(i).toString()+"->"+currentGroupName);
                    composedAction.addAction(new MoveToGroupAction(flex, pasteNames.get(i).toString(), currentGroupName));
                }


                if (objs[i] instanceof Movable) {
                    //((Movable)objs[i]).move(view.getRx(), view.getRy());
                    ((Movable)objs[i]).move(posX, posY);
                }
            }
            else
                view.deselectObject((VisibleObject)objs[i]);
        }
    }

    UndoManager.getInstance().addAction(composedAction);

    drawingSurface.getViewGroup().manageLinks(true);
    //recopy objects for multiple paste
    copy(copiedObjects, false);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void print() {}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void redo() {
    ViewState.getInstance().deselectAll();
    UndoManager.getInstance().redo();
    updateMenuItems();
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void rename() {

    ViewState view = ViewState.getInstance();
    int size = view.getSelectedObjects().size();
    if (size==0) return;

    Object objs[] = new Object[size];
    view.getSelectedObjects().copyInto(objs);

    for(int i=0; i<size; i++)
        if (objs[i] instanceof Flexible)
        {
            // call gui
            ShowRenameDialog cmd = (ShowRenameDialog)CommandManager.getInstance().getCommand("ShowRenameDialog");
            cmd.setOldName(((Flexible)objs[i]).getFlexibleName());
            cmd.execute();
        }
    view.deselectAll();
    drawingSurface.getViewGroup().manageLinks(true);

    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:05:02)
 */
public void rename(java.lang.String oldName, java.lang.String newName) {
    ViewState view = ViewState.getInstance();
    Object obj = Group.getRoot().findObject(oldName, true);
    if (obj instanceof Flexible)
    {
        Flexible flex = (Flexible)obj;
        if (flex.rename(newName))
        {
            UndoManager.getInstance().addAction(new RenameAction(flex, oldName, newName));

            view.deselectObject((VisibleObject)obj);
            drawingSurface.getViewGroup().manageLinks(true);
            drawingSurface.repaint();
        }
    }
}

public void morph() {
    ViewState view = ViewState.getInstance();

    int size = view.getSelectedObjects().size();
    if (size==0) return;

    Object objs[] = new Object[size];
    view.getSelectedObjects().copyInto(objs);

    for(int i=0; i<size; i++)
        if (objs[i] instanceof Morphable)
        {
            // call gui
            ShowMorphingDialog cmd = (ShowMorphingDialog)CommandManager.getInstance().getCommand("ShowMorphingDialog");
            cmd.setName(((Morphable)objs[i]).getName());
            cmd.setOldType(((Morphable)objs[i]).getType());
            cmd.setTargets(((Morphable)objs[i]).getTargets());
            cmd.execute();
        }
    view.deselectAll();

    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3.5.2001 10:05:02)
 */
public void morph(java.lang.String name, String newType) {
    ViewState view = ViewState.getInstance();
    Object oldObject = Group.getRoot().findObject(name, true);
    if (oldObject instanceof Record)
    {
        try {
            UndoManager.getInstance().startMacroAction();

            Record record = (Record)oldObject;

            VDBRecordData oldRecordData = record.getRecordData();

            if (record.morph(newType))
            {
                UndoManager.getInstance().addAction(new MorphAction(record, oldRecordData, record.getRecordData()));

                view.deselectObject((VisibleObject)oldObject);
                drawingSurface.repaint();
            }
        } finally {
            UndoManager.getInstance().stopMacroAction();
        }
    }
    else if (oldObject instanceof Template)
    {
        try {
            UndoManager.getInstance().startMacroAction();

            Template template = (Template)oldObject;

            VDBTemplateInstance oldTemplateData = template.getTemplateData();

            if (template.morph(newType))
            {
                UndoManager.getInstance().addAction(new MorphTemplateAction(template, oldTemplateData, template.getTemplateData()));

                view.deselectObject((VisibleObject)oldObject);
                drawingSurface.repaint();
            }
        } finally {
            UndoManager.getInstance().stopMacroAction();
        }
    }
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:27)
 * @param file java.io.File
 */
public void save(java.io.File file) throws IOException {
/*
 if (drawingSurface.isTemplateMode())
 {
     saveAsTemplate(file);
     return;
 }
*/
 Group.save(Group.getRoot(), file, false);

 VDBTemplate data = Group.getEditingTemplateData();
 if (data==null)
 {
     // create a new
     // id = basename
    data = new VDBTemplate(file.getName(), file.getAbsolutePath());

    data.setPorts(new Hashtable());
    data.setPortsV(new Vector());

    data.setMacros(new Hashtable());
    data.setMacrosV(new Vector());

    data.setGroup(Group.getRoot());

    Group.setEditingTemplateData(data);
    drawingSurface.getTemplateStack().push(data);

    VDBData.addTemplate(data);
 }
 // save as check
 //    fileName & id has been changed, fix them
 else if (!file.getAbsolutePath().equals(data.getFileName()))
 {
     // reload previous ...
     drawingSurface.reloadTemplate(data);

     // ... and fix current id (basename) and path
    data.setFileName(file.getAbsolutePath());
    data.setId(file.getName());

 }

 // if ok
 drawingSurface.setModified(false);
 UndoManager.getInstance().prepareAfterSaving();

 // !!!
 VisualDCT.getInstance().updateLoadLabel();
 VisualDCT.getInstance().setOpenedFile(file);
 //SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
 //cmd.setFile(file.getCanonicalPath());
 //cmd.execute();

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void saveAsGroup(java.io.File file) throws IOException {
 Group.save(drawingSurface.getViewGroup(), file, false);

}
/**
 * @see com.cosylab.vdct.graphics.GUIMenuInterface#saveAsTemplate(File)
 */
public void saveAsTemplate(File file) throws IOException
{
/*
 VDBTemplate data = null;
 Stack stack = drawingSurface.getTemplateStack();
 if (stack.isEmpty())
 {

    String id = file.getName();
    // remove spaces and extension
    id = id.replace(' ', '_');
    int pos = id.lastIndexOf('.');
    if (pos>0)
        id = id.substring(0, pos);

    // generate first free
    while (VDBData.getTemplates().containsKey(id))
        id = StringUtils.incrementName(id, null);

     // create a new
    data = new VDBTemplate(id, file.getAbsolutePath());
    data.setDescription(data.getId());
    data.setInputs(new Hashtable());
    data.setInputComments(new Hashtable());
    data.setOutputs(new Hashtable());
    data.setOutputComments(new Hashtable());
    data.setGroup(Group.getRoot());
     stack.push(data);

     Group.setEditingTemplateData(data);
 }

 Group.saveAsTemplate(Group.getRoot(), file);

 drawingSurface.setModified(false);

 // show user template mode
 drawingSurface.updateWorkspaceGroup();

 // new
 if (data!=null)
 {
     // add to list of loaded templates
     VDBData.getTemplates().put(data.getId(), data);

     VisualDCT.getInstance().updateLoadLabel();
 }

 SetWorkspaceFile cmd = (SetWorkspaceFile)CommandManager.getInstance().getCommand("SetFile");
 cmd.setFile(file.getCanonicalPath());
 cmd.execute();
*/
}

/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:48:27)
 * @param file java.io.File
 */
public void export(java.io.File file) throws IOException {
 Group.save(Group.getRoot(), file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param file java.io.File
 */
public void exportAsGroup(java.io.File file) throws IOException {
 Group.save(drawingSurface.getViewGroup(), file, true);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void selectAll() {
    if (drawingSurface.getViewGroup().selectAllComponents())
        drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void setFlatView(boolean state) {
    drawingSurface.getView().setFlat(state);
    drawingSurface.getViewGroup().unconditionalValidateSubObjects(state);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:43:50)
 * @param scale double
 */
public void setScale(double scale) {
    drawingSurface.setScale(scale);
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void showGrid(boolean state) {
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (27.4.2001 19:54:27)
 * @param state boolean
 */
public void showNavigator(boolean state) {
    drawingSurface.repaint();
}
/**
 * Zoom selection
 * Creation date: (4.2.2001 15:57:56)
 */
public void smartZoom() {
    ViewState view = ViewState.getInstance();
    if (view.getSelectedObjects().size()==0) return;

    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;

    VisibleObject vo;
    Enumeration e = view.getSelectedObjects().elements();
    while (e.hasMoreElements())
    {
        vo = (VisibleObject)e.nextElement();
        if (vo instanceof Border)
        {
            Border b = (Border)vo;
            Enumeration e2 = b.getSubObjectsV().elements();
            while (e2.hasMoreElements())
            {
                vo = (VisibleObject)e2.nextElement();
                minX = Math.min(minX, vo.getRx());
                minY = Math.min(minY, vo.getRy());
                maxX = Math.max(maxX, vo.getRx()+vo.getRwidth());
                maxY = Math.max(maxY, vo.getRy()+vo.getRheight());
            }
        }
        else
        {
            minX = Math.min(minX, vo.getRx());
            minY = Math.min(minY, vo.getRy());
            maxX = Math.max(maxX, vo.getRx()+vo.getRwidth());
            maxY = Math.max(maxY, vo.getRy()+vo.getRheight());
        }
    }

    int space = (minX+minY+maxX+maxY)/75;
    drawingSurface.zoomArea(minX-space-view.getRx(), minY-space-view.getRy(),
                            maxX+space-view.getRx(), maxY+space-view.getRy());

}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 * @param state boolean
 */
public void snapToGrid(boolean state) {
    drawingSurface.getViewGroup().unconditionalValidateSubObjects(drawingSurface.isFlat());
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void undo() {
    ViewState.getInstance().deselectAll();
    UndoManager.getInstance().undo();
    updateMenuItems();
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (4.2.2001 15:32:01)
 */
public void ungroup() {
    ViewState view = ViewState.getInstance();
    int size = view.getSelectedObjects().size();
    if (size==0) return;

    ComposedAction composedAction = new ComposedAction();

    String currentGroupName = drawingSurface.getViewGroup().getAbsoluteName();

    Object objs2[]; int size2;
    Group group;

    Object objs[] = new Object[size];
    view.getSelectedObjects().copyInto(objs);
    for (int i=0; i<size; i++) {
        if (objs[i] instanceof Group) {
            group = (Group)objs[i];
            view.deselectObject(group);
            size2 = group.getSubObjectsV().size();
            objs2 = new Object[size2];
            group.getSubObjectsV().copyInto(objs2);
            for (int j=0; j<size2; j++)
            {
            /*!!!can be outside    if (objs2[i] instanceof Movable)
                    ((Movable)objs2[i]).move(view.getRx()-group.getInternalRx(),
                                             view.getRy()-group.getInternalRy());

            */
                if (objs2[i] instanceof Flexible) {
                    Flexible flex = (Flexible)objs2[j];
                    flex.moveToGroup(currentGroupName);

                    composedAction.addAction(new MoveToGroupAction(flex, group.getAbsoluteName(), currentGroupName));        // if true ?!!!


                    view.setAsSelected((VisibleObject)objs2[j]);
                }
            }

            if (group.getSubObjectsV().size()==0) {
                group.destroy();
                composedAction.addAction(new DeleteAction(group));
            }
        }
    }

    UndoManager.getInstance().addAction(composedAction);

    drawingSurface.getViewGroup().manageLinks(true);
    drawingSurface.repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 18:12:34)
 */
public void updateMenuItems() {
    SetRedoMenuItemState cmd = (SetRedoMenuItemState)CommandManager.getInstance().getCommand("SetRedoMenuItemState");
    if (cmd != null)
    {
        cmd.setState(UndoManager.getInstance().actions2redo()>0);
        cmd.execute();
    }

    SetUndoMenuItemState cmd2 = (SetUndoMenuItemState)CommandManager.getInstance().getCommand("SetUndoMenuItemState");
    if (cmd2 != null)
    {
        cmd2.setState(UndoManager.getInstance().actions2undo()>0);
        cmd2.execute();
    }
}

/**
 * Insert the method's description here.
 * Creation date: (22.4.2001 18:12:34)
 */
public void updateGroupLabel() {
    drawingSurface.updateWorkspaceGroup();
}

}
