package org.csstudio.archive.reader.monica;

import java.util.Iterator;
import java.util.Properties;

import atnf.atoms.mon.comms.MoniCAClientIce;

public class MonicaArchiveClient extends MoniCAClientIce {

	String adaptorName= "";
	
	public MonicaArchiveClient(String adaptorName, Properties iceProperties) throws Exception {
        super(getIceProperties(adaptorName, iceProperties));
        
        if (adaptorName!=null)
        	this.adaptorName = adaptorName;
	}

	static private Ice.Properties getIceProperties(String adaptorName, Properties iceProperties) {
        Ice.Properties props = Ice.Util.createProperties();
        
        for (Iterator<String> keys = iceProperties.stringPropertyNames().iterator(); keys.hasNext();) {
        	String key = keys.next();
        	if (key.startsWith("Ice.")) {
        		props.setProperty(key, iceProperties.getProperty(key));
        	}
        }
        
        if (adaptorName!=null && !adaptorName.trim().isEmpty())
        	props.setProperty("AdapterName", adaptorName);
        
        return props;
	}

	public String getAdaptorName() {
		return adaptorName;
	}

	public void close() {
		super.disconnect();
	}
}
