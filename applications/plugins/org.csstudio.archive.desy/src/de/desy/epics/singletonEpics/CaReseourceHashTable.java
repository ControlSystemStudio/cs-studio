/*
 * @author Matthias Clausen
 */
package de.desy.epics.singletonEpics;

import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;

import java.io.Serializable;
import java.util.*;



public class CaReseourceHashTable
    implements Serializable
{
    //~ Instance fields --------------------------------------------------------

    private String attributeX = "";
    private String attributeY = "";
    Hashtable monitoredPVs; // current device name / PV object
    Hashtable getSetPVs;

    //~ Constructors -----------------------------------------------------------

    public CaReseourceHashTable( String x,
                               String y )
    {
        attributeX = x;
        attributeY = y;
        monitoredPVs = new Hashtable();
        getSetPVs = new Hashtable();
    }

    //~ Methods ----------------------------------------------------------------

    public String getAttributeX(  )
    {
        return attributeX;
    }

    public String getAttributeY(  )
    {
        return attributeY;
    }
    
    public class storedObject {
        
        public void storedObject ( Channel newChannel)
        {
            setChannel (newChannel);
            Date currentDate = new Date();
            setDateCreated ( currentDate);
            setLastDateAccess ( currentDate);
            // life time will get the default value
        }
        
        public void storedObject ( Channel newChannel, int newLifeTime)
        {
            setChannel( newChannel);
            Date currentDate = new Date();
            setDateCreated ( currentDate);
            setLastDateAccess ( currentDate);
            setLifeTime ( newLifeTime);
        }
        
        Channel	channel;               // the channel access ID
        Date    dateCreated;        // the date the ID was created  ( criteria for the grabage collector)
        Date    lastDateAccess;     // last time this record was accessed
        int     lifeTime = 24*60*60*1000;// life time in mS
        
        private void setChannel ( Channel newChannel)
        {
            channel = newChannel;
        }
        
        private Channel getChannel ()
        {
            return channel;
        }
        
        private void setDateCreated( Date newDate)
        {
            dateCreated = newDate;
        }
        private Date getDateCreated ( )
        {
            return dateCreated;
        }
        
        private void setLastDateAccess( Date newDate)
        {
            lastDateAccess = newDate;
        }
        private Date getLastDateAccess ( )
        {
            return lastDateAccess;
        }
        private void updateLastDateAccess()
        {
            Date currentDate = new Date();
            lastDateAccess = currentDate;
        }
                
        private void setLifeTime( int newLifeTime)
        {
            lifeTime = newLifeTime;
        }
        private int getlifeTime ( )
        {
            return lifeTime;
        }
    }
        
    
    public class monitoredObject {
        
        public void monitoredObject()
        {
        }
      
        Channel channel;
        Monitor monitor;
        boolean valid = false;  // if a value was invalid we'll have to reset the invalid state 
                                // and clear any error states
        
        private void setChannel ( Channel newChannel)
        {
            channel = newChannel;
        }
        private Channel getChannel ()
        {
            return channel;
        }
        
        private void setMonitor ( Monitor newMonitor)
        {
            monitor = newMonitor;
        }
        private Monitor getMonitor ()
        {
            return monitor;
        }
        
        private void setValid  ()
        {
            valid = true;
        }
        
        private void setInValid ()
        {
            valid = false;
        }
        
        private boolean isValid()
        {
            return valid;
        }
    }
    
    public class monitoredArgs {
        
        public void monitoredArgs()
        {
        }
        
        String deviceName;
        int elementCount;
        
        private void setDeviceName ( String newDeviceName)
        {
            deviceName = newDeviceName;
        }
        private String getDeviceName ()
        {
            return deviceName;
        }
        
        private void setElementCount ( int newElementCount)
        {
            elementCount = newElementCount;
        }
        private int getElementCount ()
        {
            return elementCount;
        }
    }
    
    
}
