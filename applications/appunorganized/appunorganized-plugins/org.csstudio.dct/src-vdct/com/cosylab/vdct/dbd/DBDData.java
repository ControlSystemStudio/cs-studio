package com.cosylab.vdct.dbd;

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
import com.cosylab.vdct.Console;
import com.cosylab.vdct.ConsoleInterface;

/**
 * This type was created in VisualAge.
 */

public class DBDData {
    protected Hashtable records = null;
    protected Hashtable menus = null;
    protected Hashtable devices = null;
/**
 * DBDData constructor comment.
 */
public DBDData() {
    records = new Hashtable();
    menus = new Hashtable();
    devices = new Hashtable();
}
/**
 * This method was created in VisualAge.
 * @param dd com.cosylab.vdct.dbd.DBDDeviceData
 */
public void addDevice(DBDDeviceData dd) {
    // key def. : <recordtype>+"/"+<device choice string>

    if (!devices.containsKey((dd.record_type+"/"+dd.choice_string)))
        devices.put((dd.record_type+"/"+dd.choice_string), dd);
    else
        Console.getInstance().println("Device "+dd.record_type+"/"+dd.choice_string+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @param md com.cosylab.vdct.MenuData
 */
public void addMenu(DBDMenuData md) {
    if (!menus.containsKey(md.getName()))
        menus.put(md.getName(), md);
    else
        Console.getInstance().println("Menu "+md.getName()+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @param rd com.cosylab.vdct.RecordData
 */
public void addRecord(DBDRecordData rd) {
    if (!records.containsKey(rd.name))
        records.put(rd.name, rd);
    else
        Console.getInstance().println("Record "+rd.getName()+" already exists in DBD - ignoring this definition.");
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param db com.cosylab.vdct.db.DBData
 */
public boolean consistencyCheck(com.cosylab.vdct.db.DBData db) {
    String illegalString;

    ConsoleInterface console = Console.getInstance();

    console.println();
    console.print("o) Checking consistency with DBD file...");
    if (db==null) {
        console.println();
        console.println("\t No DB data defined.");
        console.println();
        return false;
    }

    boolean isOK = true;
    com.cosylab.vdct.db.DBRecordData dbRecord;
    DBDRecordData dbdRecord;
    Enumeration e2;
    com.cosylab.vdct.db.DBFieldData dbField;
    DBDFieldData dbdField;
    DBDMenuData menu;
    String dev;

    // records check
    Enumeration e = db.getRecordsV().elements();
    while (e.hasMoreElements()) {
        dbRecord = (com.cosylab.vdct.db.DBRecordData)(e.nextElement());
        dbdRecord = this.getDBDRecordData(dbRecord.getRecord_type());
        if (dbdRecord!=null) {

            // fields check (fields, menus, devices)
            e2 = dbRecord.getFieldsV().elements();
            while (e2.hasMoreElements()) {
                dbField = (com.cosylab.vdct.db.DBFieldData)(e2.nextElement());
                dbdField = dbdRecord.getDBDFieldData(dbField.getName());
                if (dbdField!=null) {

                    // case when visual data is applied on non-existing (defined) field
                    // aloww this
                    if (dbField.getValue() == null)
                        continue;

                    // device check
                    if (dbdField.getField_type() == DBDConstants.DBF_DEVICE) {
                        dev = dbRecord.getRecord_type()+"/"+dbField.getValue();
                        if (dbField.getValue().indexOf("$")!=-1) {
                                console.println();
                                console.print("\tWarning: Record '"+dbRecord.getName()+"', field '"+dbField.getName()+"':");
                                console.print(" Value '"+dbField.getValue()+"' is not valid device -> template definition?...");
                                dbField.setTemplate_def(true);
                        }
                        else if (this.getDBDDeviceData(dev) == null) {
                            console.println();
                            console.print("\tWarning: Record type '"+dbRecord.getRecord_type()+"', field '"+dbdField.getName()+"':");
                            console.print(" Device '"+dev+"' is not defined DBD file.");

                            /* RT 12438 - if dbd not present, DTYP information is gone
                            illegalString="# field("+dbField.getName()+",\""+dbField.getValue()+"\")";
                            if (dbField.getComment()!=null)    illegalString=dbField.getComment()+"\n"+illegalString;
                            dbField.setComment(illegalString);

                            dbField.setValue("");
                            */
                        }
                    }
                    // menu check
                    else if (dbdField.getField_type() == DBDConstants.DBF_MENU) {
                        menu = this.getDBDMenuData(dbdField.getMenu_name());
                        if (menu==null) {
                            isOK = false;
                            console.println();
                            console.print("\tRecord type '"+dbRecord.getRecord_type()+"', field '"+dbdField.getName()+"':");
                            console.print(" Menu '"+dbdField.getMenu_name()+"' is not defined DBD file (DBD file error)...");
                        }
                        else {
                            if (!menu.containsValue(dbField.getValue())) {
                                console.println();
                            /*    console.print("\t Warning: Value '"+dbField.getValue()+"' is not valid for menu '"+dbdField.getMenu_name()+". Using defaults...");

                                illegalString="# field("+dbField.getName()+",\""+dbField.getValue()+"\")";
                                if (dbField.getComment()!=null)    illegalString=dbField.getComment()+"\n"+illegalString;
                                dbField.setComment(illegalString);

                                dbField.setValue("");*/

                                console.print("\tWarning: Record '"+dbRecord.getName()+"', field '"+dbField.getName()+"':");
                                console.print(" Value '"+dbField.getValue()+"' is not valid for menu '"+dbdField.getMenu_name()+" -> template definition?...");
                                dbField.setTemplate_def(true);

                            }
                        }
                    }


                }
                else {
                    isOK=false;
                    console.println();
                    console.print("\tRecord '"+dbRecord.getName()+"':");
                    console.print(" Field '"+dbField.getName()+"' in record type '"+dbRecord.getRecord_type()+"' is not defined in DBD file. Field will be commented out when saved!");

                    illegalString="# illegal line - undefined field: field("+dbField.getName()+",\""+dbField.getValue()+"\")";
                    if (dbRecord.getComment()!=null) illegalString=dbRecord.getComment()+"\n"+illegalString;
                    dbRecord.setComment(illegalString);
                }
            }
        }
        else {
            isOK=false;
            console.println();
            console.print("\tRecord '"+dbRecord.getName()+"':");
            console.print(" Record type '"+dbRecord.getRecord_type()+"' is not defined in DBD file.");
        }
    }

    if (isOK) {
        console.println();
        console.print("\tOK");
    } else {
        console.println();
        console.print("o) DB file is not consistent with DBD file!");
    }
    console.println(); console.println();
    return isOK;
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.dbd.DBDDeviceData
 * @param deviceName java.lang.String
 */
public DBDDeviceData getDBDDeviceData(String deviceName) {
    return (DBDDeviceData)(devices.get(deviceName));
}
/**
 * This method was created in VisualAge.
 * @return com.cosylab.vdct.dbd.DBDMenuData
 * @param menuName java.lang.String
 */
public DBDMenuData getDBDMenuData(String menuName) {
    return (DBDMenuData)(menus.get(menuName));
}
/**
 * This method was created in VisualAge.
 * @returncom.cosylab.vdct.dbd.DBDRecordData
 * @param recordName java.lang.String
 */
public DBDRecordData getDBDRecordData(String recordName) {
    return (DBDRecordData)(records.get(recordName));
}
/**
 * Insert the method's description here.
 * Creation date: (10.1.2001 17:26:33)
 * @return java.util.Hashtable
 */
public java.util.Hashtable getDevices() {
    return devices;
}
/**
 * Insert the method's description here.
 * Creation date: (3.2.2001 19:58:09)
 * @return java.util.Enumeration
 */
public Enumeration getRecordNames() {
    return records.keys();
}
}
