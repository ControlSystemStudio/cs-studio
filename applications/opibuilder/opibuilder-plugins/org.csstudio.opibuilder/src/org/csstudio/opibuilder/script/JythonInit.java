package org.csstudio.opibuilder.script;

import java.util.Properties;

import org.python.core.PySystemState;

public class JythonInit {
    
    private static PythonInterpreter pythonInterpreter;

    protected JythonInit(){
	
    }
    
    public static PythonInterpreter getInstance() {
	if(pythonInterpreter == null) {
	    synchronized(JythonInit.class){
		Properties props = System.getProperties();
		Properties jythonProps = new Properties();
//		
//		if(props.getProperty("python.home") == null) {
//		    jythonProps.setProperty("python.home", value)
//		}
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		PySystemState.initialize(props, jythonProps, new String[0], classloader);
		
		pythonInterpreter = new PythonInterpreter();
		
		pythonInterpreter.exec("import sys");
	    }
	}
	return pythonInterpreter;
    }
}
