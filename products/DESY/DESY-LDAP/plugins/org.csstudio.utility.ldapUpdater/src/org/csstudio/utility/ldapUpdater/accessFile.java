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
            // TODO Auto-generated catch block
//          e.printStackTrace();
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
			CentralLogger.getInstance().error(this, "File not Found : " + e.getMessage() );
			_model.setSerror(_model.getSerror()+1);		
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
//			System.err.println("Error: " + e.getMessage());
//			System.out.println("Error: " + e.getMessage());
//			System.err.println("Error: " + e.toString());
//			System.out.println("Error: " + e.toString());
			_model.setSerror(_model.getSerror()+2);			
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
