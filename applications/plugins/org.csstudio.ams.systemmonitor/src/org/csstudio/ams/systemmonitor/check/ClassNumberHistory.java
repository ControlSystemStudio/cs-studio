
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.systemmonitor.check;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 26.03.2012
 */
public class ClassNumberHistory {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClassNumberHistory.class);
    
    private TreeMap<Long, String> content;

    /** Location of the workspace directory. Used to store the date object. */
    private String workspaceLocation;

    public ClassNumberHistory(String wsLocation) {
        workspaceLocation = wsLocation;
        content = loadMap();
        if (content == null) {
            content = new TreeMap<Long, String>(new TimestampComparator());
        }
        cleanHistory();
    }
    
    public void put(Long key, String value) {
        content.put(key, value);
    }
    
    public boolean containsClassNumber(String number) {
        if (number == null) {
            return false;
        }
        return content.containsValue(number);
    }
    
    public void remove(Long key) {
        if (content.containsKey(key)) {
            content.remove(key);
        }
    }
    
    public void removeAll() {
        content.clear();
    }
    
    public void removeClassNumber(String number) {
        if (number == null) {
            return;
        }
        if (content.containsValue(number)) {
            Iterator<Long> iter = content.keySet().iterator();
            while (iter.hasNext()) {
                Long key = iter.next();
                if (content.get(key).compareTo(number) == 0) {
                    content.remove(key);
                    LOG.info("Removed class number: {}", number);
                    break;
                }
            }
        }
    }
    
    public boolean storeContent() {
        return saveMap();
    }
    
    private void cleanHistory() {
        long deleteTime = System.currentTimeMillis() - 3600000L;
        Iterator<Long> iter = content.keySet().iterator();
        while (iter.hasNext()) {
            long key = iter.next();
            if (key <= deleteTime) {
                iter.remove();
                LOG.info("Cleaning class number: {} - {}", content.get(key), key);
            }
        }
    }
    
    private boolean saveMap() {
        
        FileOutputStream  fos = null;
        ObjectOutputStream oos = null;
        boolean result = false;
        
        try {
            fos = new FileOutputStream(workspaceLocation + "classNumberHistory.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(content);
            result = true;
        } catch(FileNotFoundException fnfe) {
            LOG.error("saveMap(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
            result = false;
        } catch(IOException ioe) {
            LOG.error("saveMap(): [*** IOException ***]: " + ioe.getMessage());
            result = false;
        } finally {
            if(oos!=null){try{oos.close();}catch(Exception e){/*Can be ignored*/}oos=null;}
            if(fos!=null){try{fos.close();}catch(Exception e){/*Can be ignored*/}fos=null;}
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private TreeMap<Long, String> loadMap() {
        
        FileInputStream  fis = null;
        ObjectInputStream ois = null;
        Object object = null;
        TreeMap<Long, String> m = null;
        
        try {
            fis = new FileInputStream(workspaceLocation + "classNumberHistory.ser");
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            if(object instanceof TreeMap<?, ?>) {
                m = (TreeMap<Long, String>) object;
            }
        } catch(FileNotFoundException fnfe) {
            LOG.error("loadMap(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
        } catch(IOException ioe) {
            LOG.error("loadMap(): [*** IOException ***]: " + ioe.getMessage());
        } catch(ClassNotFoundException cnfe) {
            LOG.error("loadMap(): [*** ClassNotFoundException ***]: " + cnfe.getMessage());
        } finally {
            if(ois!=null){try{ois.close();}catch(Exception e){/*Can be ignored*/}ois=null;}
            if(fis!=null){try{fis.close();}catch(Exception e){/*Can be ignored*/}fis=null;}
        }
        
        return m;
    }
}
