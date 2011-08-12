package org.csstudio.ams.performancetesttool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

public class MapMessagePropertiesReader {
    private Hashtable<String, String> table = null;
    private Properties properties = null;
    private Random random = new Random(System.currentTimeMillis());
    
    public MapMessagePropertiesReader(String fileName) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));
            table = replaceVariables();
        } catch (FileNotFoundException fnfe) {
            properties = null;
        } catch (IOException ioe) {
            properties = null;
        }
        
        if (table != null) {
            if (table.size() <= 0) {
                table = null;
            }
        }
    }
    
    public boolean existProperties() {
        return (table != null);
    }
    
    public void printContent() {
        String key = null;
        
        if (table != null) {
            Enumeration<?> keyList = null;
            keyList = table.keys();
            while (keyList.hasMoreElements()) {
                key = (String) keyList.nextElement();
                System.out.println(" " + key + " = " + table.get(key));
            }
            keyList = null;
        }
    }
    
    private Hashtable<String, String> replaceVariables() {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        StringTokenizer token = null;
        Enumeration<?> keyList = null;
        String key = null;
        String value = null;
        String variable = null;
        String param = null;
        int indexOfColon = 0;
        
        keyList = properties.keys();
        
        while (keyList.hasMoreElements()) {
            key = (String) keyList.nextElement();
            
            value = properties.getProperty(key).trim();
            
            if (value.startsWith("{") && value.endsWith("}")) {
                // The value is an option ( {time:format string} | {option:option name} )
                indexOfColon = value.indexOf(':');
                
                if (indexOfColon != -1) {
                    variable = value.substring(1, indexOfColon).trim();
                    param = value.substring(indexOfColon + 1, value.length() - 1).trim();
                    
                    if (variable.compareToIgnoreCase("date") == 0) {
                        ht.put(key, getDateString(param));
                    } else if (variable.compareToIgnoreCase("random") == 0) {
                        token = new StringTokenizer(param, ",");
                        
                        int count = token.countTokens();
                        if (count > 0) {
                            String[] val = new String[count];
                            
                            int i = 0;
                            while (token.hasMoreElements()) {
                                val[i++] = token.nextToken();
                            }
                            int rand = random.nextInt(count);
                            ht.put(key, val[rand]);
                        } else {
                            ht.put(key, "*** No list for random choice available ***");
                        }
                    } else {
                        ht.put(key, "*** unknown variable ***");
                    }
                } else // invalid option
                {
                    ht.put(key, "*** invalid variable format ***");
                }
            } else {
                ht.put(key, value);
            }
        }
        
        return ht;
    }
    
    public String getDateString(String f) {
        SimpleDateFormat format = new SimpleDateFormat(f);
        
        return format.format(Calendar.getInstance().getTime());
    }
    
    public Hashtable<String, String> getMessageProperties() {
        return table;
    }
}
