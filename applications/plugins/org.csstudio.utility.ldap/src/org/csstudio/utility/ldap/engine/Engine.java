/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.engine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;
import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.connection.LDAPConnector;
import org.csstudio.utility.ldap.preference.PreferenceConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class Engine extends Job {
    private LdapReferences ldapReferences = null;
    private Collector   ldapWriteTimeCollector = null;
    private Collector   ldapReadTimeCollector = null;
    private Collector   ldapWriteRequests = null;

    public Engine(String name) {
        super(name);
        // TODO Auto-generated constructor stub
        this.ldapReferences = new LdapReferences();
        /*
         * initialize statistic
         */
        ldapWriteTimeCollector = new Collector();
        ldapWriteTimeCollector.setApplication(name);
        ldapWriteTimeCollector.setDescriptor("Time to write to LDAP server");
        ldapWriteTimeCollector.setContinuousPrint(true);
        ldapWriteTimeCollector.setContinuousPrintCount(1000.0);
        ldapWriteTimeCollector.getAlarmHandler().setDeadband(5.0);
        ldapWriteTimeCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);   // 500ms
        ldapWriteTimeCollector.getAlarmHandler().setHighRelativeLimit(400.0);   // 200%
        
        ldapReadTimeCollector = new Collector();
        ldapReadTimeCollector.setApplication(name);
        ldapReadTimeCollector.setDescriptor("Time to find LDAP entries");
        ldapReadTimeCollector.setContinuousPrint(true);
        ldapReadTimeCollector.setContinuousPrintCount(1000.0);
        ldapReadTimeCollector.getAlarmHandler().setDeadband(5.0);
        ldapReadTimeCollector.getAlarmHandler().setHighAbsoluteLimit(500.0);    // 500ms
        ldapReadTimeCollector.getAlarmHandler().setHighRelativeLimit(500.0);    // 200%
        
        ldapWriteRequests = new Collector();
        ldapWriteRequests.setApplication(name);
        ldapWriteRequests.setDescriptor("LDAP Write Request Buffer Size");
        ldapWriteRequests.setContinuousPrint(true);
        ldapWriteRequests.setContinuousPrintCount(1000.0);
        ldapWriteRequests.getAlarmHandler().setDeadband(5.0);
        ldapWriteRequests.getAlarmHandler().setHighAbsoluteLimit(50.0);    // 500ms
        ldapWriteRequests.getAlarmHandler().setHighRelativeLimit(200.0);    // 200%
    }
    private static      Engine thisEngine = null;
    private boolean     doWrite = false;
    private DirContext  ctx;
    private Vector<WriteRequest>    writeVector = new Vector<WriteRequest>();
    boolean addVectorOK = true;
    
    /**
     * @param args
     */

    protected IStatus run(IProgressMonitor monitor) {
        Integer intSleepTimer = null;

        CentralLogger.getInstance().info(this, "Ldap Engine started"); 
        
        // TODO: 
        /*
         *  create message ONCE
         *  retry forever if ctx == null
         *  BUT do NOT block caller (calling sigleton)
         *  submit ctx = new LDAPConnector().getDirContext(); to 'background process'
         *  
         */
        CentralLogger.getInstance().debug(this, "Engine.run - start");
        ctx = new LDAPConnector().getDirContext();
        CentralLogger.getInstance().debug(this, "Engine.run - ctx: " + ctx.toString());
        if ( ctx  != null) {
            CentralLogger.getInstance().info( this, "Engine.run - successfully connected to LDAP server");
        } else {
            CentralLogger.getInstance().fatal( this, "Engine.run - connection to LDAP server failed");
        }

        while (true) {
            //
            // do the work actually prepared
            //
            if (doWrite) {
                performLdapWrite();
            }
            /*
             * sleep before we check for work again
             */
//          System.out.println("Engine.run - waiting...");
            try {
                if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL).trim().length()>0) {
                    intSleepTimer = new Integer(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL));
                } else {
                    intSleepTimer = 10; //default
                }
                Thread.sleep( (long)intSleepTimer );
            }
            catch (InterruptedException  e) {
                return null;
            }
        }
    }


    public static Engine getInstance() {
        //
        // get an instance of our sigleton
        //
        if ( thisEngine == null) {
            synchronized (Engine.class) {
                if (thisEngine == null) {
                    thisEngine = new Engine("LdapEngine");
                    thisEngine.schedule();
                }
            }
        }
        return thisEngine;
    }
    
    synchronized public void addLdapWriteRequest(String attribute, String channel, String value) {
        // boolean addVectorOK = true;
        WriteRequest writeRequest = new WriteRequest( attribute, channel, value);
        int maxBuffersize = 1000;
        //
        // add request to vector
        //
        int bufferSize = writeVector.size();
        /*
         * statistic information
         */
        ldapWriteRequests.setValue(bufferSize);
        
        /// System.out.println("Engine.addLdapWriteRequest actual buffer size: " + bufferSize);
        if ( bufferSize > maxBuffersize) {
            if (addVectorOK) {
                System.out.println("Engine.addLdapWriteRequest writeVector > " + maxBuffersize + " - cannot store more!");
                CentralLogger.getInstance().warn(this, "writeVector > " + maxBuffersize + " - cannot store more!"); 
                addVectorOK = false;
            }
        } else {
            if ( ! addVectorOK) {
                System.out.println("Engine.addLdapWriteRequest writeVector - continue writing");
                CentralLogger.getInstance().warn(this, "writeVector < " + maxBuffersize + " - resume writing"); 
                addVectorOK = true;
            }
            writeVector.add(writeRequest);
        }
        //
        // aleays trigger writing
        //
        doWrite = true;
    }
    
    private void performLdapWrite() {
//      int size= writeVector.size();
        int maxNumberOfWritesProcessed = 200;
        //ModificationItem[] modItem = new ModificationItem[writeVector.size()];
        ModificationItem[] modItem = new ModificationItem[maxNumberOfWritesProcessed];
        int i = 0;
        String channel;
        channel = null;
        i = 0;

        while(writeVector.size()>0) {
            
            //WriteRequest writeReq = writeVector.firstElement();
            //
            // return first element and remove it from list
            //
            WriteRequest writeReq = writeVector.remove(0);
            //
            // prepare LDAP request for all entries matching the same channel
            //
            if ( channel == null) {
                // first time setting
                channel = writeReq.getChannel();
            } 
            if ( !channel.equals(writeReq.getChannel())){
                //System.out.print("write: ");
                // TODO this hard coded string must be removed to the preferences
                changeValue("eren", channel, modItem);
                //System.out.println(" finisch!!!");
                modItem = new ModificationItem[maxNumberOfWritesProcessed];
                i = 0;
                //
                // define next channel name
                //
                channel = writeReq.getChannel();
            }
            //
            // combine all items that are related to the same channel
            //
            BasicAttribute ba = new BasicAttribute( writeReq.getAttribute(), writeReq.getValue());
            modItem[i++] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE,ba);
            //writeVector.remove(0);
            if( (writeVector.size()>100) &&(writeVector.size()%100)==0)
                System.out.println("Engine.performLdapWrite buffer size: "+writeVector.size());
        }
        //
        // still something left to do?
        //
        if (i != 0 ) {
            //
            try {
                //System.out.println("Vector leer jetzt den Rest zum LDAP Server schreiben");
                changeValue("eren", channel, modItem);
            }
             catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    //
                    // too bad it did not work
                    doWrite = true; // retry!
                    return;
                }
        } else {
            //System.out.println("Vector leer - nothing left to do");
        }
        
        doWrite = false;
    }

    /**
     * @param string
     * @param channel
     * @param modItemTemp
     */
    private void changeValue(String string, String channel, ModificationItem[] modItem) {
        int j=0;
        int n;
        Vector <String>namesInNamespace = null;
        GregorianCalendar startTime = null;

        // Delete null values and make right size
        for(;j<modItem.length;j++){
            if(modItem[j]==null)
                break;
        }
//      System.out.println("Enter Engine.changeValue with: " + channel);
        ModificationItem modItemTemp[] = new ModificationItem[j];
        for( n = 0; n<j; n++){
            modItemTemp[n] = modItem[n];
        }
        //
        // set start time
        //
        startTime = new GregorianCalendar();
        
        //
        // is channel name already in ldearReference hash table?
        //
        if ( ldapReferences.hasEntry(channel)) {
        // if ( false) { // test case with no hash table
            //
            // take what's already stored
            //
            CentralLogger.getInstance().debug(this, "Engine.changeValue : found entry for channel: " + channel);
            //System.out.println ("Engine.changeValue : found entry for channel: " + channel);
            namesInNamespace = this.ldapReferences.getEntry( channel).getNamesInNamespace();
            for( int index = 0; index < namesInNamespace.size(); index++) {

                String ldapChannelName = (String) namesInNamespace.elementAt(index);
                //
                // TODO put 'endsWith' into preference page
                //
                if(ldapChannelName.endsWith(",o=DESY,c=DE")){
                    ldapChannelName=ldapChannelName.substring(0,ldapChannelName.length()-12);
                }
                try {
                    ctx.modifyAttributes(ldapChannelName, modItemTemp);
                    //System.out.println ("Engine.changeValue : Time to write to LDAP: (known channel: " + ldapChannelName + ") [" + n + "] " + gregorianTimeDifference ( startTime, new GregorianCalendar()));
                    ldapWriteTimeCollector.setInfo(channel);
                    ldapWriteTimeCollector.setValue( gregorianTimeDifference ( startTime, new GregorianCalendar())/n);
                } catch (NamingException e) {
                	CentralLogger.getInstance().warn( this, "Engine.changeValue: Naming Exception! Channel: " +  ldapChannelName);
                    System.out.println("Engine.changeValue: Naming Exception! Channel: " +  ldapChannelName);
                    String errorCode = e.getExplanation();
                    if ( errorCode.contains("10")) {
                    	System.out.println( "Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
                    	CentralLogger.getInstance().warn( this, "Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
                    }
                    e.printStackTrace();
                    //
                    // too bad it did not work
                    doWrite = false;    // wait for next time
                    CentralLogger.getInstance().debug(this, "Engine.changeValue : cannot write to LDAP server (naming exception)"); 
                    return;
                }catch (Exception e) {
                    e.printStackTrace();
                    //
                    // too bad it did not work
                    doWrite = false;    // wait for next time
                    CentralLogger.getInstance().debug(this, "Engine.changeValue : cannot write to LDAP server"); 
                    return;
                }
            }
    
        } else {
            //
            // search for channel in ldap server
            //
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            try {
                NamingEnumeration<SearchResult> results = ctx.search("",string+"=" + channel, ctrl);
                //System.out.println ("Engine.changeValue : Time to search channel: " + gregorianTimeDifference ( startTime, new GregorianCalendar()));
                ldapReadTimeCollector.setInfo(channel);
                ldapReadTimeCollector.setValue(gregorianTimeDifference ( startTime, new GregorianCalendar()));
    //          System.out.println("Enter Engine.changeValue results for channnel: " + channel );
                namesInNamespace = new Vector<String>();
                while(results.hasMore()) {
    //              System.out.println("Engine.changeValue in while channnel: " + channel );
                    String ldapChannelName = results.next().getNameInNamespace();
                    namesInNamespace.add( ldapChannelName);
                    //
                    // TODO put 'endsWith' into preference page
                    //
                    if(ldapChannelName.endsWith(",o=DESY,c=DE")){
                        ldapChannelName=ldapChannelName.substring(0,ldapChannelName.length()-12);
                    }
                    try {
                        ctx.modifyAttributes(ldapChannelName, modItemTemp);
                        ldapWriteTimeCollector.setInfo(channel);
                        ldapWriteTimeCollector.setValue( gregorianTimeDifference ( startTime, new GregorianCalendar())/n);
                        //System.out.println ("Engine.changeValue : Time to write to LDAP: (" +  channel + ")" + gregorianTimeDifference ( startTime, new GregorianCalendar()));
                    } catch (NamingException e) {
                    	CentralLogger.getInstance().warn( this, "Engine.changeValue: Naming Exception! Channel: " +  ldapChannelName);
                        System.out.println("Engine.changeValue: Naming Exception! Channel: " +  ldapChannelName);
                        String errorCode = e.getExplanation();
                        if ( errorCode.contains("10")) {
                        	System.out.println( "Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
                        	CentralLogger.getInstance().warn( this, "Error code 10: Please check LDAP replica! - replica may be out of synch - use: [start accepting updates] in SUN-LDAP Console");
                        }
                    	e.printStackTrace();
                        //
                        // too bad it did not work
                        doWrite = false;    // wait for next time
                        return;
                    }catch (Exception e) {
                        e.printStackTrace();
                        //
                        // too bad it did not work
                        doWrite = false;    // wait for next time
                        return;
                    }
                    
    //              //
    //              // reset for to get ready for values of next channel
    //              //
    //              modItemTemp = new ModificationItem[writeVector.size()];
                }
    
            } catch (NamingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //
            // save ldapEntries
            //
            if ( namesInNamespace.size()>0) {
                //
                // Write if really something found
                //
                ldapReferences.newLdapEntry( channel, namesInNamespace);
                //System.out.println ("Engine.changeValue : add entry for channel: " + channel);
            }
        }
        //
        // calcualte time difference
        //
        //System.out.println ("Engine.changeValue : Time to write to LDAP-total: " + gregorianTimeDifference ( startTime, new GregorianCalendar()));
    }
    
    public int gregorianTimeDifference ( GregorianCalendar fromTime, GregorianCalendar toTime) {
        //
        // calculate time difference
        //
        Date fromDate = fromTime.getTime();
        Date toDate = toTime.getTime();
        long fromLong = fromDate.getTime();
        long toLong = toDate.getTime();
        long timeDifference = toLong - fromLong;
        int intDiff = (int)timeDifference;
        return intDiff;
    }

    public void setLdapValueOld ( String channel, String severity, String status, String timeStamp) {
        ModificationItem epicsStatus, epicsSeverity, epicsTimeStamp, epicsAcknowledgeTimeStamp ;
        ModificationItem[] modItem = null;
        int i = 0;

        String channelName = "eren=" + channel;

        //
        // change severity if value is entered
        //
        if ((severity != null)&& (severity.length() > 0)) {
            epicsSeverity = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute( "epicsAlarmSeverity", severity));
            modItem[i++] = epicsSeverity;
        }

        //
        // change status if value is entered
        //
        if ((status != null) && (status.length() > 0)) {
            epicsStatus = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmStatus", status));
        }

        //
        // change alarm time stamp
        //
        if ((timeStamp != null) && (timeStamp.length() > 0)) {
            epicsTimeStamp = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmTimeStamp", timeStamp));
        }

        //
        // change time stamp acknowledged time
        //
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
        java.util.Date currentDate = new java.util.Date();
        String eventTime = sdf.format(currentDate);

        epicsAcknowledgeTimeStamp = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmAcknTimeStamp", eventTime));

        try {
            ctx.modifyAttributes(channelName, modItem);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    private class WriteRequest {
        private String  attribute = null;
        private String  channel = null;
        private String  value = null;
        
        public WriteRequest ( String attribute, String channel, String value) {
            
            this.attribute = attribute;
            this.channel = channel;
            this.value = value;
        }
        
        public String getAttribute () {
            return this.attribute;
        }
        
        public String getChannel () {
            return this.channel;
        }
        
        public String getValue () {
            return this.value;
        }

    }
    

}
