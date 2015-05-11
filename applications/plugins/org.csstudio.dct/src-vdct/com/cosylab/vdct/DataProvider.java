package com.cosylab.vdct;

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

import java.util.*;
import java.util.regex.Pattern;

import com.cosylab.vdct.dbd.*;
import com.cosylab.vdct.graphics.objects.*;
import com.cosylab.vdct.inspector.*;

/**
 * Insert the type's description here.
 * Creation date: (8.1.2001 18:21:54)
 * @author Matej Sekoranja
 */

public class DataProvider {
    private static DataProvider instance = null;

    // DBD
    private DBDData dbdDB = null;

    private Vector inspectableListeners = null;

    // list of all loaded DBDs
    private Vector loadedDBDs = null;

    // list of all loaded DBDs
    private Vector currentDBDs = null;

    // edit masks
    private Hashtable linkTypeConfigTable = null;

    // list of all loaded DBs
    //private Vector loadedDBs = null;

/**
 * DataProvider constructor comment.
 */
protected DataProvider() {
    inspectableListeners = new Vector();
    loadedDBDs = new Vector();
    currentDBDs = new Vector();

    linkTypeConfigTable = new Hashtable();
    loadDefaultLinkTypeConfig();
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 * @return java.util.regex.Pattern
 */
public Pattern getEditPatternLinkType(String linkType)
{
    Object[] data = (Object[])linkTypeConfigTable.get(linkType);
    if (data!=null)
        return (Pattern)data[0];
    else
        return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 * @return java.lang.String
 */
public String getEditInitialValueLinkType(String linkType)
{
    Object[] data = (Object[])linkTypeConfigTable.get(linkType);
    if (data!=null)
        return (String)data[1];
    else
        return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 * @return java.lang.String
 */
public String getEditDescriptionLinkType(String linkType)
{
    Object[] data = (Object[])linkTypeConfigTable.get(linkType);
    if (data!=null)
        return (String)data[2];
    else
        return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 */
private void loadDefaultLinkTypeConfig()
{
    // pattern, default value, description
    linkTypeConfigTable.put("CONSTANT", new Object[] { Pattern.compile(".*"), "", "CONSTANT" });
    linkTypeConfigTable.put("PV_LINK", new Object[] { Pattern.compile(".*"), "", "PV_LINK" } );
    linkTypeConfigTable.put("VME_IO", new Object[] { Pattern.compile("#C\\d+ S\\d+( )*( @.*)?"), "#C0 S0 @", "VME_IO - #Ccard Ssignal @parm" });
    linkTypeConfigTable.put("CAMAC_IO", new Object[] { Pattern.compile("#B\\d+ C\\d+ N\\d+ A\\d+ F\\d+( )*( @.*)?"), "CAMAC_IO - #B0 C0 N0 A0 F0 @", "#Bbranch Ccrate Nstation Asubaddress Ffunction @parm" });
    linkTypeConfigTable.put("AB_IO", new Object[] { Pattern.compile("#L\\d+ A\\d+ C\\d+ S\\d+( )*( @.*)?"), "#L0 A0 C0 S0 @", "AB_IO - #Llink Aadapter Ccard Ssignal @parm" });
    linkTypeConfigTable.put("GPIB_IO", new Object[] { Pattern.compile("#L\\d+ A\\d+( )*( @.*)?"), "#L0 A0 @", "GPIB_IO - #Llink Aaddr @parm" });
    linkTypeConfigTable.put("BITBUS_IO", new Object[] { Pattern.compile("#L\\d+ N\\d+ P\\d+ S\\d+( )*( @.*)?"), "BITBUS_IO - @L0 N0 P0 S0 @", "#Llink Nnode Pport Ssignal @parm" });
    linkTypeConfigTable.put("INST_IO", new Object[] { Pattern.compile("@.*"), "@", "INST_IO - @" });
    linkTypeConfigTable.put("BBGPIB_IO", new Object[] { Pattern.compile("#L\\d+ B\\d+ G\\d+( )*( @.*)?"), "#L0 B0 G0 @", "BBGPIB_IO - #Llink Bbbaddr Ggpibaddr @parm" });
    linkTypeConfigTable.put("RF_IO", new Object[] { Pattern.compile("#R\\d+ M\\d+ D\\d+ E\\d+( )*( @.*)?"), "#R0 M0 D0 E0 @", "RF_IO - #Rcryo Mmicro Ddataset Eelement" });
    linkTypeConfigTable.put("VXI_IO", new Object[] { Pattern.compile("#V\\d+ (C\\d+)?+  S\\d+( )*( @.*)?"), "#V0 C0 S0 @", "VXI_IO - #Vframe Cslot Ssignal @parm" });
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 */
public void addLinkTypeConfig(Hashtable table)
{
    linkTypeConfigTable.putAll(table);
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:23:00)
 * @param listener com.cosylab.vdct.inspector.InspectableObjectsListener
 */
public void addInspectableListener(InspectableObjectsListener listener) {
    if (!inspectableListeners.contains(listener))
        inspectableListeners.addElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:25:49)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void fireInspectableObjectAdded(Inspectable object) {
    Enumeration e = inspectableListeners.elements();
    while (e.hasMoreElements())
        ((InspectableObjectsListener)e.nextElement()).inspectableObjectAdded(object);
}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:26:16)
 * @param object com.cosylab.vdct.inspector.Inspectable
 */
public void fireInspectableObjectRemoved(Inspectable object) {
    Enumeration e = inspectableListeners.elements();
    while (e.hasMoreElements())
        ((InspectableObjectsListener)e.nextElement()).inspectableObjectRemoved(object);
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:25:39)
 * @return com.cosylab.vdct.dbd.DBDData
 */
public com.cosylab.vdct.dbd.DBDData getDbdDB() {
    return dbdDB; //!!!
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 22:03:39)
 * @return java.util.Vector
 */
public Vector getInspectable() {
    Vector objs = new Vector();
    getInspectable(Group.getRoot(), objs, true);
    return objs;
}
/**
 * Insert the method's description here.
 * Creation date: (2.2.2001 21:59:25)
 * @param group com.cosylab.vdct.graphics.objects.Group
 * @param deep boolean
 */
private void getInspectable(Group group, Vector objs, boolean deep) {
    Enumeration e = group.getSubObjectsV().elements();
    Object obj;
    while (e.hasMoreElements()) {
        obj = e.nextElement();
        if (obj instanceof com.cosylab.vdct.inspector.Inspectable)
            objs.addElement(obj);
        else if (deep && (obj instanceof Group))
            getInspectable((Group)obj, objs, deep);
    }
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:22:58)
 * @return com.cosylab.vdct.DataProvider
 */
public static DataProvider getInstance() {
    if (instance==null) instance = new DataProvider();
    return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 19:56:16)
 * @return java.lang.String[]
 */
public Object[] getRecordTypes() {
    Object[] records;
    records = new com.cosylab.vdct.util.StringQuickSort().sortEnumeration(
        dbdDB.getRecordNames());
    return records;

}
/**
 * Insert the method's description here.
 * Creation date: (17.4.2001 17:23:30)
 * @param listener com.cosylab.vdct.inspector.InspectableObjectsListener
 */
public void removeInspectableListener(InspectableObjectsListener listener) {
    if (inspectableListeners.contains(listener))
        inspectableListeners.removeElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (8.1.2001 18:25:39)
 * @param newDbdDB com.cosylab.vdct.dbd.DBDData
 */
public void setDbdDB(com.cosylab.vdct.dbd.DBDData newDbdDB) {
    dbdDB = newDbdDB;
}
/**
 * Returns the currentDBDs.
 * @return Vector
 */
public Vector getCurrentDBDs()
{
    return currentDBDs;
}

/**
 * Returns the loadedDBs.
 * @return Vector
 */
public Vector getLoadedDBDs()
{
    return loadedDBDs;
}

}
