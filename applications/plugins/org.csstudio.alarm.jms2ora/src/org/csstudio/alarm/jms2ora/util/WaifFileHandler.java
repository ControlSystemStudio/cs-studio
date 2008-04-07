
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.alarm.jms2ora.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * 
 * @author  Markus Moeller
 * @version 1.0
 */

public class WaifFileHandler implements FilenameFilter
{
    private String[]    fileList    = null;
    
    public WaifFileHandler()
    {
        countWaifFiles();
    }
        
    public boolean accept(File dir, String name)
    {
        return name.toLowerCase().matches( "waif_\\d{8}_\\d{9}.ser" );
    }
    
    private void countWaifFiles()
    {
        File waifDir = new File("nirvana");
        
        fileList = waifDir.list(this);
    }
    
    public int getNumberOfWaifFiles()
    {
        if(fileList != null)
        {
            return fileList.length;
        }
        else
        {
            return 0;
        }
    }
    
    public String[] getWaifFileNames()
    {
        return fileList;
    }
    
    public MapMessage getWaifFileContent(int number)
    {
        FileInputStream     fis         = null;
        ObjectInputStream   ois         = null;
        MapMessage          result      = null;
        Message             buffer      = null;
        String              fileName    = null;
        
        if(fileList == null)
        {
            return null;
        }
        
        if((number >= 0) && (number < fileList.length))
        {
            fileName = fileList[number];
       
            try
            {
                fis = new FileInputStream(".\\nirvana\\" + fileName);
                ois = new ObjectInputStream(fis);
                
                buffer = (Message)ois.readObject();
                
                if(buffer instanceof MapMessage)
                {
                    result = (MapMessage)buffer;
                }
                else
                {
                    result = null;
                }
            }
            catch(Exception e)
            {
                result = null;
            }
            finally
            {
                if(ois != null)
                {
                    try
                    {
                        ois.close();
                    }
                    catch(IOException ioe) { }
                }
                
                if(fis != null)
                {
                    try
                    {
                        fis.close();
                    }
                    catch(IOException ioe) { }
                }
                
                ois = null;
                fis = null;
            }
        }
        else
        {
            result = null;
        }
        
        return result;
    }
    
    public int deleteAllFiles()
    {
        File del = null;
        int result = 0;
        
        if(fileList == null)
        {
            return result;
        }
        
        for(int i = 0;i < fileList.length;i++)
        {
            del = new File(".\\nirvana\\" + fileList[i]);
            
            if(!del.delete())
            {
                result++;
            }
        }
        
        return result;
    }
}
