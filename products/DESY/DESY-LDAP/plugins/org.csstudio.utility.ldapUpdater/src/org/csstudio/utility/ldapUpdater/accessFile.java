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

package org.csstudio.utility.ldapUpdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.DataModel;

// import sun.security.krb5.internal.crypto.e;
// junit.samples.VectorTest.java

/**
 *
 * @author valett
 * @author $Author$
 * @version $Revision$
 * @since 04.2008
 */

public class accessFile {

	private DataModel _model;

/**
 * 
 * @param pathFile pfad/dateiname
 * @param hashIt   abbildungssteuerung
 */    
    public final void readData(final String pathFile, final boolean hashIt){
        int i=0;
        ArrayList<String> list = readDataFile(pathFile);
        if (hashIt) {
            for ( String file : list) {
            }
        }
        else {
            for (String file : list) {	//	macht diese loop einen sinn ???           
                if(checkFileAge(file)){
                	
                }
            }
        }
    }
    
    /**
     * 
     * @param toller text
     *
     */
    public final ArrayList<String> readDataFile(final String pathFile) {
        ArrayList<String> firstParameter = new ArrayList<String>();
        File file = new File(pathFile);
        try {
            Map<String,String> gefundeneZeiten = new HashMap<String, String>();
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String line = fr.readLine();
            Entry entries = new Entry();
            while (line != null) {
                if (!line.startsWith("#") && !line.startsWith("*")) {
                    Pattern p = Pattern.compile("(\\S*)(\\s*)(\\S*)(\\s*)(\\S*)");
                    Matcher m = p.matcher(line);
                    if(!m.matches()) {
                        throw new RuntimeException("Fehler in Datei, Zeile ist: "+line);
                    }
                    String[] splittedEntry = new String[3];
                    splittedEntry[0] = m.group(1);
                    splittedEntry[1] = m.group(3);
                    splittedEntry[2] = m.group(5);
                    gefundeneZeiten.put(splittedEntry[0], splittedEntry[2]);                 
                    entries.addEntry(splittedEntry);
                }
                line = fr.readLine();
            }
            for (String key : gefundeneZeiten.keySet()) {
                System.out.println(key + ": " + gefundeneZeiten.get(key));
//				CentralLogger.getInstance().info(this, key + ": " + gefundeneZeiten.get(key));
            }
        } catch (FileNotFoundException e) {
//          e.printStackTrace();
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
			CentralLogger.getInstance().error(this, "File not Found : " + e.getMessage() );
//			_model.setSerror(_model.getSerror()+1);		
        } catch (IOException e) {
//            e.printStackTrace();
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
//			System.err.println("Error: " + e.toString());
//			System.out.println("Error: " + e.toString());
//			_model.setSerror(_model.getSerror()+2);			
			CentralLogger.getInstance().error(this, "IOExeption: " + e.getMessage() + e.toString() ); 
        }
        return firstParameter;
    }

    /**
     * 
     * @param fileName
     *
     */
    private boolean checkFileAge(final String fileName) {
       // TODO Auto-generated method stub
       
    	File file = new File("Y:/directoryServer/"+fileName);
    	Date date = new Date(file.lastModified());
//      date ist jetzt im millisekunden-format. Bsp.: 1208279315000 ;
    	SimpleDateFormat f = new SimpleDateFormat();
    	System.out.println(file+" "+f.format(date));
//	    CentralLogger.getInstance().info(this, file+" "+f.format(date));
    	return  file.exists();
    }
    
}
