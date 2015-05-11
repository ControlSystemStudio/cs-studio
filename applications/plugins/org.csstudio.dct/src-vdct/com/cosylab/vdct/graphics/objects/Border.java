/*
 * Created on 26.7.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cosylab.vdct.graphics.objects;

import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

import com.cosylab.vdct.Console;
import com.cosylab.vdct.Constants;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.util.StringUtils;

/**
 * @author msekoranja
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Border extends ContainerObject implements Selectable, SaveObject,
        Flexible, Clipboardable, Movable {

    private static final String hashIdPrefix = "Border";

    private String getAvailableHashId()
    {
        int number = 0;
        String testHashId = hashIdPrefix + String.valueOf(number);

        while(getParent().containsObject(testHashId))
        {
            number++;
            testHashId = hashIdPrefix + String.valueOf(number);
        }

        return testHashId;
    }

    private String hashId;
    private String name;
    private static final String nullString = "";

    /**
     * @param parent
     */
    public Border(String name, Group parent) {
        this(name, parent, false);
    }

    /**
     * @param parent
     * @param useHashtable
     */
    public Border(String parName, Group parentGroup, boolean useHashtable) {
        super(parentGroup, useHashtable);

        if(parName == null)
        {
            hashId = getAvailableHashId();

            if(parentGroup.getAbsoluteName().length() > 0)
                name = parentGroup.getAbsoluteName() + Constants.GROUP_SEPARATOR + hashId;
            else
                name = hashId;
        }
        else
            name = parName;
    }

    public Object removeObject(String id) {
        Object obj = super.removeObject(id);
        if (obj != null) {
            return obj;
        }

        Vector e = getSubObjectsV();
        VisibleObject object;
        for (int i = 0; i < e.size(); i++) {
            object = (VisibleObject) e.get(i);
            if (object instanceof BorderObject) {
                if (id.equals(((BorderObject)object).getName())) {
                    e.remove(i);
                }
            }
        }
        return null;
    }

    /*
     *  (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#destroy()
     */
    public void destroy() {
        super.destroy();

        if (getParent() != null) {
            getParent().removeObject(name);
        }
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#draw(java.awt.Graphics, boolean)
     */
    protected void draw(Graphics g, boolean hilited) {
        Enumeration e = getSubObjectsV().elements();
        while (e.hasMoreElements())
          ((VisibleObject)e.nextElement()).draw(g, hilited);
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#getHashID()
     */
    public String getHashID() {
        return hashId;
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#revalidatePosition()
     */
    public void revalidatePosition() {
        Enumeration e = getSubObjectsV().elements();
        while (e.hasMoreElements())
          ((VisibleObject)e.nextElement()).revalidatePosition();
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#validate()
     */
    protected void validate() {
        Enumeration e = getSubObjectsV().elements();
        while (e.hasMoreElements())
          ((VisibleObject)e.nextElement()).validate();
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.Flexible#getFlexibleName()
     */
    public String getFlexibleName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.Movable#checkMove(int, int)
     */
    public boolean checkMove(int dx, int dy) {
        boolean ok = true;

        Enumeration selected = getSubObjectsV().elements();
        while (selected.hasMoreElements() && ok)
            ok = ((Movable)selected.nextElement()).checkMove(dx, dy);

        return ok;
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.Movable#move(int, int)
     */
    public boolean move(int dx, int dy) {
        boolean repaint = false;

        Enumeration selected = getSubObjectsV().elements();
        while (selected.hasMoreElements())
            repaint = ((Movable)selected.nextElement()).move(dx, dy);

        return repaint;
    }

    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.Visitable#accept(com.cosylab.vdct.graphics.objects.Visitor)
     */
    public void accept(Visitor visitor) {
        visitor.visitGroup();
    }

    /**
     * Returned value inicates change
     * Creation date: (21.12.2000 22:21:12)
     * @return com.cosylab.visible.objects.VisibleObject
     * @param x int
     * @param y int
     */
    public VisibleObject hiliteComponentsCheck(int x, int y) {

        ViewState view = ViewState.getInstance();
        VisibleObject spotted = null;

        Enumeration e = subObjectsV.elements();
        VisibleObject vo;
        while (e.hasMoreElements()) {
            vo = (VisibleObject)(e.nextElement());
            vo = vo.intersects(x, y);
            if (vo!=null) {
                spotted=vo;
                if (view.getHilitedObject()!=vo) return vo;
            }
        }

        return spotted;
    }

    /**
     * @return com.cosylab.visible.objects.VisibleObject
     * @param px int
     * @param py int
     */
    public VisibleObject intersects(int px, int py) {
        // first check on small sub-objects like connectors
        VisibleObject spotted = hiliteComponentsCheck(px, py);
        if (spotted instanceof TextBox)
            return spotted;
        else if (spotted != null)
            return this;
        else
            return null;
    }


    /* (non-Javadoc)
     * @see com.cosylab.vdct.graphics.objects.VisibleObject#intersects(int, int, int, int)
     */
    public VisibleObject intersects(int p1x, int p1y, int p2x, int p2y) {
        boolean ok = true;

        Enumeration selected = getSubObjectsV().elements();
        while (selected.hasMoreElements() && ok)
            ok = ((VisibleObject)selected.nextElement()).intersects(p1x, p1y, p2x, p2y) != null;

        if (ok)
            return this;
        else
            return null;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    public Flexible copyToGroup(String group)
    {
        Console.getInstance().println("Borders cannot be copied (use import).");
        return null;
    }

    public boolean moveToGroup(String group)
    {
        String currentParent = Group.substractParentName(getName());
        if(group.equals(currentParent))
            return false;

        //String oldName = getName();
        String newName;
        if (group.equals(nullString))
            newName = Group.substractObjectName(getName());
        else
            newName = group + Constants.GROUP_SEPARATOR + Group.substractObjectName(getName());

        // object with new name already exists, add suffix // !!!
        Object obj;
        boolean renameNeeded = false;
        while ((obj=Group.getRoot().findObject(newName, true))!=null)
        {
            if (obj==this)    // it's me :) already moved, fix data
            {
                name = newName;
                return true;
            }
            else
            {
                renameNeeded = true;
                newName = StringUtils.incrementName(newName, Constants.MOVE_SUFFIX);
            }
        }

        if (renameNeeded)
            return rename(newName);

        getParent().removeObject(Group.substractObjectName(getName()));
        setParent(null);
        Group.getRoot().addSubObject(newName, this, true);

        name = newName;
        unconditionalValidation();

        return true;
    }

    public boolean rename(String newName)
    {
        String newObjName = Group.substractObjectName(newName);
        String oldObjName = Group.substractObjectName(getName());

        if(!oldObjName.equals(newObjName))
        {
            getParent().removeObject(oldObjName);
            String fullName = StringUtils.replaceEnding(getName(), oldObjName, newObjName);
            name = fullName;
            getParent().addSubObject(newObjName, this);
        }

    // move if needed
        moveToGroup(Group.substractParentName(newName));

        return true;
    }
}
