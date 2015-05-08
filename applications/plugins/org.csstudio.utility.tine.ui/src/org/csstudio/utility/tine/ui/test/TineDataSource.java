/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.utility.tine.ui.test;
import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JTextField;

import de.desy.tine.client.TLink;
import de.desy.tine.client.TLinkCallback;
import de.desy.tine.definitions.TMode;
import de.desy.tine.*;
import de.desy.tine.queryUtils.TQuery;

//
// for TINE we need the file: /tine/database/cshosts.csv
// in the directory: java.home which in this case is defined during startup:
// -Dtine.home=d:\tine
//

public class TineDataSource //extends AbstractDataSource implements TLinkCallback
{
    //
    // things we need for TINE
    //
    String[] value = new String[1];
    String[] buffer = new String[300];
    Hashtable monitoredDeviceNames; // current device name / TLink object

    TineDataSource ( String name, int wakeupTime, int priority,
        int nameSpaceType, int accessType, int minScanTime, int defaultScanTime)
    {
/*        initDataSource ( name, wakeupTime, priority, nameSpaceType, accessType, minScanTime, defaultScanTime);

        System.out.println( "java.home is : " + System.getProperty( "java.home"));

        monitoredDeviceNames = new Hashtable();
        //registerObjectAndMethod ( this);
*/    }


    public void getValue ( String deviceName)
    {
//        int cc;
//        incrementDiagCounter ( "GetValue");
//        //
//        // do the things to get the value
//        //
//        /*
//        float[] value = new float[500];
//        TDataType dout = new TDataType( value);
//        TDataType din = new TDataType();
//        */
//
////        TLink linkToData = new TLink( "/HERA/HEPBPM/WL197 MX/POSITIONS.X", dout, din, TAccess.CA_READ);
//        TLink linkToData = new TLink( deviceName);
//
//        //
//        // set the array delimiter
//        // the default is <CR>
//        // there might be a problem with strings which contain spaces
//        // but we are looking here for 'real' data ...
//        //
//        linkToData.setArrayDelimiter(" ");
//
//        if (( cc = linkToData.execute(3000)) == 0) {
////        if (( cc = linkToData.execute()) == 0) {
//            //
//            // update value in registry
//            //
//            /*
//             * number of values
//             *
//             linkToData.getLastDataSize()  .. oder so ..
//             */
////            registry.updateValueInRegistry ( "TINE|"+deviceName, "" + value[0]);
//            registry.updateValueInRegistry ( "TINE|"+deviceName, linkToData.getOutputDataObject().toString());
//            linkToData.cancel();
//            //
//            // if the data source supports alarming -> invoce registry.updateAlarmInRegistry (TODO)
//            //
//        }
//        else {
//            MainFrame.errlog ("TINE : error getting value for " + deviceName);
//            String message = linkToData.getLastError();
//            MainFrame.errlog ("TINE : " + message);
//        }
//
//        return ;
    }
    public int registerMonitor ( String requestName)
    {
        int errorCode = -1;
//        String deviceName;
//
//        deviceName = deviceNameWithoutDataSourceName(requestName);
//
//        incrementDiagCounter ( "RegisterMonitor");
//        TLink linkToData = new TLink( requestName);
//        //
//        // set the array delimiter
//        // the default is <CR>
//        // there might be a problem with strings which contain spaces
//        // but we are looking here for 'real' data ...
//        //
//        linkToData.setArrayDelimiter(" ");
//        int linkid = linkToData.attach( TMode.CM_REFRESH, this, 1000 );
//        monitoredDeviceNames.put( requestName, linkToData);
//
////        JCALib.addChannelName( deviceName);
//        //
        return errorCode;
    }

    public int deregisterMonitor ( String requestName)
    {
        int errorCode = -1;
//        String deviceName;
//        TLink linkToData;
//
//        deviceName = deviceNameWithoutDataSourceName( requestName);
//        incrementDiagCounter ( "DeregisterMonitor");
//        if ( monitoredDeviceNames.containsKey(requestName)) {
//            linkToData = (TLink)monitoredDeviceNames.get( requestName);
//            linkToData.cancel();
//            monitoredDeviceNames.remove( requestName);
//        } else {
//            MainFrame.errlog ("TINE : error stopping monitor for " + deviceName);
//        }
//
        return errorCode;
    }

    public void callback (TLink link)
    {
//        registry.updateValueInRegistry ( "TINE|"+link.getFullDeviceName()+ "/" + link.getProperty(),
//        link.getOutputDataObject().toString());
    }

    public void displayNameSpaceBrowser (Object calledFrom) {
//        if ( newBrowser == null) {
//            newBrowser = new TineNameSpaceBrowser((Component)calledFrom, this);
//        }
//        newBrowser.setVisible( true);
//        ((JTextField)calledFrom).setText(newBrowser.getSelectedName());
//        // return the result as a string
//        return ;
    }
    public String[] getContexts() {
        return TQuery.getContexts();
    }
    public String[] getDeviceServers(String context) {
        if(!context.equals(NameSpaceBrowser.NA))
            return TQuery.getDeviceServers(context);
        else return null;
    }
    public String[] getDevices(String context, String server) {
        if(!context.equals(NameSpaceBrowser.NA) && !server.equals(NameSpaceBrowser.NA))
            return TQuery.getDeviceNames(context, server);
        else return null;
    }
    public String[] getDeviceProperties(String context, String server, String device) {
        if(!context.equals(NameSpaceBrowser.NA) &&
           !server.equals(NameSpaceBrowser.NA) &&
           !device.equals(NameSpaceBrowser.NA))
            return TQuery.getDeviceProperties(context, server, device);
        else return null;
    }
}