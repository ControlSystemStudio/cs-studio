package org.csstudio.opibuilder.properties.support;

import java.util.List;

import org.eclipse.core.runtime.IPath;


/**The description data for a script.
 * @author Xihui Chen
 *
 */
public class ScriptData {
	
	public static String SCRIPT_EXTENSION = "js"; //$NON-NLS-1$
	
	/**
	 * The path of the script.
	 */
	private IPath path;
	
	/**
	 * The input PVs of the script. Which can be accessed in the script and trigger the script execution.
	 */
	private List<String> pvList;
	
	/**Set the script path.
	 * @param path the file path of the script.
	 * @return true if successful. false if the input is not a javascript file.
	 */
	public boolean setPath(IPath path){
		if(path.getFileExtension().equals(SCRIPT_EXTENSION)){
			this.path = path; 
			return true;
		}
		return false;		
	}
	
	/**Get the path of the script.
	 * @return the file path.
	 */
	public IPath getPath() {
		return path;
	}
	
	/**Get the input PVs of the script 
	 * @return
	 */
	public List<String> getPVList() {
		return pvList;
	}
	
	public void addPV(String pv){
		if(!pvList.contains(pv)){
			pvList.add(pv);
		}			
	}
	
	public void removePV(String pv){
		pvList.remove(pv);
	}	
	
	public ScriptData getCopy(){
		ScriptData copy = new ScriptData();
		copy.setPath(path);
		for(String pv : pvList){
			copy.addPV(pv);
		}
		return copy;
	}
}
