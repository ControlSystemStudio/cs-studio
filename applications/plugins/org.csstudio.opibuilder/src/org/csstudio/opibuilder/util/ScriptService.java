package org.csstudio.opibuilder.util;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

public class ScriptService {

	private static ScriptService instance;
	
	private Context scriptContext;
	
	public ScriptService() {
		scriptContext = Context.enter();		
	}
	
	public static ScriptService getInstance() {
		if(instance == null)
			instance = new ScriptService();
		return instance;
	}
	
	
	public Context getScriptContext() {
		return scriptContext;
	}
	
	
	public void exit(){
		Context.exit();
		instance =  null;
	}
	
}
