
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.archive.sdds.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.csstudio.archive.sdds.server.io.Server;
import org.csstudio.archive.sdds.server.io.ServerException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class SddsServerApplication implements IApplication, RemotelyStoppable, SddsServerApplicationMBean, IGenericServiceListener<ISessionService> {
    
    /** The instance of the server */
    private Server server;
    
    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(SddsServerApplication.class);
    
    /** Help object for synchronization purposes */
    private final Object lock;
    
    /** Flag that indicates if the server is running */
    private boolean running;
    
    /** Flag that indicates if the server has to be restarted */
    private boolean restart;

    /**
     * The standard constructor
     */
    public SddsServerApplication() {
        lock = new Object();
        running = true;
        restart = false;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
	public Object start(IApplicationContext context) throws Exception {
        
        Integer exitType = null;
        int serverPort;
        boolean useJmx = false;
        
        LOG.info("Starting {}", SddsServerActivator.PLUGIN_ID);

        IPreferencesService pref = Platform.getPreferencesService();
        serverPort = pref.getInt(SddsServerActivator.PLUGIN_ID, ServerPreferenceKey.P_SERVER_PORT, 4056, null);
        LOG.info("The server uses port {}", serverPort);
        
        useJmx = pref.getBoolean(SddsServerActivator.PLUGIN_ID, ServerPreferenceKey.P_USE_JMX,
        		false, null);
        
        try {
            server = new Server(serverPort);
            server.start();
            
            if (useJmx == false) {
                SddsServerActivator.getDefault().addSessionServiceListener(this);
            } else {
            	connectMBeanServer();
            }
        } catch(ServerException se) {
            LOG.error("Cannot create an instance of the Server class. ", se);
            LOG.error("Stopping application!");
            running = false;
            restart = false;
        }
        
        context.applicationRunning();
        
        while(running) {
            synchronized(lock) {
                try {
                    lock.wait();
                } catch(InterruptedException ie) {
                    LOG.debug("Interrupted");
                }
            }
        }

        if(server != null) {
            server.stopServer();
        }
        
        if(restart) {
            LOG.info("Restarting {}", SddsServerActivator.PLUGIN_ID);
            exitType = IApplication.EXIT_RESTART;
        } else {
            LOG.info("Stopping {}", SddsServerActivator.PLUGIN_ID);
            exitType = IApplication.EXIT_OK;
        }
        
        return exitType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
	public void stop() {
    	// Nothing to do here
    }


    /**
     * 
     */
    public void connectMBeanServer() {
        
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName myname = null;
        
        String jmxPort = System.getProperty("com.sun.management.jmxremote.port");
        
        LOG.info("The server uses JMX for remote access. Port: " + jmxPort);
        
        try {
            myname = new ObjectName("org.csstudio.archive.sdds.server:name=SddsServer");
            mbeanServer.registerMBean(this, myname);
        } catch (MalformedObjectNameException mone) {
            LOG.error("[*** MalformedObjectNameException ***]: ", mone);
        } catch (NullPointerException npe) {
            LOG.error("[*** NullPointerException ***]: ", npe);
        } catch (InstanceAlreadyExistsException iaee) {
            LOG.error("[*** InstanceAlreadyExistsException ***]: ", iaee);
        } catch (MBeanRegistrationException mbre) {
            LOG.error("[*** MBeanRegistrationException ***]: ", mbre);
        } catch (NotCompliantMBeanException ncmbe) {
            LOG.error("[*** NotCompliantMBeanException ***]: ", ncmbe);
        }
    }

    /**
     * 
     * @param setRestart
     */
    @Override
	public void stopApplication(boolean setRestart) {
        
        this.running = false;
        this.restart = setRestart;
        
        synchronized(lock) {
            lock.notify();
        }
    }

    /**
     * Stops the application. Used by JMX. 
     */
    @Override
	public void stopApplication() {
    	stopApplication(false);
    }

	@Override
	public String readVersion() {
		
		String workspaceDirectory = Platform.getInstallLocation()
									.getURL().getPath() + ".eclipseproduct";
		
		BufferedReader reader = null;
		String version = null;
		
		try {
			reader = new BufferedReader(new FileReader(workspaceDirectory));
			while (reader.ready()) {
				version = reader.readLine().trim();
				if (version.startsWith("version=")) {
					int indexOfEqual = version.indexOf("=") + 1;
					version = version.substring(indexOfEqual);
					break;
				}
			}
		} catch (FileNotFoundException fnfe) {
			LOG.warn("Workspace directory cannot be found: {}", workspaceDirectory);
			version = null;
		} catch (IOException ioe) {
			LOG.warn("Cannot read version file.");
			version = null;
		} finally {
			if (reader!=null) {
				try{reader.close();}catch(Exception e){/* Can be ignored */}
				reader=null;
			}
		}
		
		if (version == null) {
			version = "N/A";
		}
		
		return version;
	}

    @Override
    public void bindService(ISessionService sessionService) {
        IPreferencesService pref = Platform.getPreferencesService();
        String xmppServer = pref.getString(SddsServerActivator.PLUGIN_ID, ServerPreferenceKey.P_XMPP_SERVER,
                                           "krynfs.desy.de", null);
        String xmppUser = pref.getString(SddsServerActivator.PLUGIN_ID, ServerPreferenceKey.P_XMPP_USER,
                                         "sdds-server", null);
        String xmppPassword = pref.getString(SddsServerActivator.PLUGIN_ID, ServerPreferenceKey.P_XMPP_PASSWORD,
                                             "sdds-server", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
		} catch (Exception e) {
			LOG.warn("XMPP connection is not available, ", e);
		}
    }
    
    @Override
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
    
    public void nirvana()
    {
        //sddsReader.readDataPortionSimple("HQCO7L~B", null, -1, startTime, endTime, (short)1, -1, null);
//      running = true;
//      while(running)
//      {
//          synchronized(this)
//          {
//              EpicsRecordData[] data = sddsReader.readData("CMTBVA3V112_ai", 1249120800L, 1249120860L);
//              
//              if(data != null)
//              {
//                  logger.info("Anzahl: " + data.length);
//                  
//                  for(EpicsRecordData p : data)
//                  {
//                      System.out.println(p);
//                  }
//              }
//              
//              this.wait(1000);
//          }
//          
//          running = false;
//      }
      
//      URL url = new URL(null, "sdds://krynfs.desy.de:4000", new SddsStreamHandler());
//      System.out.println(url.getProtocol());
      
      
//      startTime = TimeConverter.convertToLong("2009-01-10 12:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//      endTime = TimeConverter.convertToLong("2009-01-10 12:10:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//              
//      EpicsRecordData[] data = sddsReader.readData("krykWeather_Temp_ai", startTime, endTime);
//      System.out.println("Anzahl der Datenwerte: " + data.length);
//      
//      EpicsRecordData erd = data[0];
//      
//      System.out.println(erd.getTime());
//      
//      TimeInterval ti = new TimeInterval(startTime, endTime);
//      System.out.println("Start month: " + ti.getStartMonthAsString());
//      System.out.println("End month:   " + ti.getEndMonthAsString());
//      
//      int[] years = ti.getYears();
//      for(int i : years)
//      {
//          System.out.println(i);
//      }
    }
}
